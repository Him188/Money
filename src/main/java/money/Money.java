package money;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.event.Listener;
import cn.nukkit.permission.Permission;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import money.command.*;
import money.event.bank.BankChangeEvent;
import money.event.bank.BankDecreaseEvent;
import money.event.bank.BankIncreaseEvent;
import money.event.money.AccountCreateEvent;
import money.event.money.MoneyChangeEvent;
import money.event.money.MoneyDecreaseEvent;
import money.event.money.MoneyIncreaseEvent;
import money.tasks.BankInterestTask;
import money.utils.LanguageType;
import money.utils.Translator;
import net.mamoe.moedb.AbstractDatabase;
import net.mamoe.moedb.defaults.HashDatabase;
import net.mamoe.moedb.defaults.RedisDatabase;
import net.mamoe.moedb.defaults.SafeRedisDatabase;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Money 的主类
 *
 * @author Him188 @ Money Project
 */
public final class Money extends PluginBase implements MoneyAPI, Listener {
    private static Money instance = null;

    private Translator translator;

    /**
     * 数据库, 使用自 {@code MoeDB} 前置插件
     */
    AbstractDatabase db = null;
    private MoneyEventListener listener;

    private static final Class<?>[] COMMAND_CLASSES = {
            BankInfoCommand.class,
            BankSaveCommand.class,
            BankTakeCommand.class,
            Give1Command.class,
            Give2Command.class,
            GiveOnline1Command.class,
            GiveOnline2Command.class,
            List1Command.class,
            List2Command.class,
            Pay1Command.class,
            Pay2Command.class,
            Set1Command.class,
            Set2Command.class,
            SuperSet1Command.class,
            SuperSet2Command.class,
            WalletInfo1Command.class,
            WalletInfo2Command.class,
            SeeMoney1Command.class,
            SeeMoney2Command.class,
            SeeBankCommand.class
    };

    @SuppressWarnings("unchecked")
    private static PluginCommand<Money> matchCommand(String key, String name, Money owner, String[] aliases) {
        key = key.replace("-", "");
        key = key.toLowerCase();
        for (Class<?> commandClass : COMMAND_CLASSES) {
            if (commandClass.getSimpleName().toLowerCase().equals(key + "command")) {
                try {
                    Constructor<?> constructor = commandClass.getConstructor(String.class, Money.class, String[].class);
                    return (PluginCommand<Money>) constructor.newInstance(name, owner, aliases);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }
        }
        return null;
    }

    public static Money getInstance() {
        return instance;
    }

    public Money() {
        super();
        instance = this;
    }

    @Override
    public void onLoad() {
        if (!getDataFolder().mkdir() && !getDataFolder().exists()) {
            this.getLogger().warning("无法创建配置目录 (" + getDataFolder() + ")");
            this.getLogger().warning("Could not create data directory (" + getDataFolder() + ")");
        }
    }

    public void resetLanguage(LanguageType type, boolean replace) {
        this.getLogger().notice("Language " + type.getTranslationName() + " is selected.");
        this.getLogger().notice("Of course, you can use command '/moneyselectlang <chs|eng|cht>' to change");

        if (replace) {
            //backup
            for (String fileName : new String[]{
                    "config.yml",
                    "Language.properties",
                    "Commands.yml"
            }) {
                File file = new File(getDataFolder(), fileName);
                File backup = new File(getDataFolder(), fileName + ".backup");
                if (backup.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    backup.delete();
                }
                if (file.exists()) {
                    if (!file.renameTo(backup)) {
                        this.getLogger().warning("Backing up old " + fileName + " failed.");
                    }
                }
            }
        }

        saveResource("Language_" + type.name().toLowerCase() + ".properties", "Language.properties", replace);
        saveResource("config_" + type.name().toLowerCase() + ".yml", "config.yml", replace);
        saveResource("Commands_" + type.name().toLowerCase() + ".yml", "Commands.yml", replace);
    }

    @Override
    public void onEnable() {
        if (!new File(getDataFolder(), "Language.properties").exists()
            || !new File(getDataFolder(), "config.yml").exists()
            || !new File(getDataFolder(), "Commands.yml").exists()) {

            LanguageType type = LanguageType.getDefaultLanguage();
            this.resetLanguage(type, false);
        }

        reloadConfig();
        initDatabase();

        this.translator = new Translator();
        this.translator.load(new Config(new File(getDataFolder(), "Language.properties"), Config.PROPERTIES).getRootSection());


        /* Register permissions */

        new HashMap<String, String>() {
            {
                put("money.command.bankinfo", "true");
                put("money.command.banksave", "true");
                put("money.command.banktake", "true");
                put("money.command.list1", "true");
                put("money.command.list2", "true");
                put("money.command.pay1", "true");
                put("money.command.pay2", "true");
                put("money.command.walletinfo1", "true");
                put("money.command.walletinfo2", "true");


                put("money.command.give1", "op");
                put("money.command.give2", "op");
                put("money.command.giveonline1", "op");
                put("money.command.giveonline2", "op");
                put("money.command.set1", "op");
                put("money.command.set2", "op");
                put("money.command.superset1", "op");
                put("money.command.superset2", "op");
                put("money.command.see1", "op");
                put("money.command.see2", "op");
                put("money.command.seebank", "op");

                put("money.command.selectlang", "op");
            }
        }.forEach((name, permission) -> getServer().getPluginManager().addPermission(new Permission(name, permission, permission)));

        /* Register commands */

        Server.getInstance().getCommandMap().register("moneyselectlang", new SelectLanguageCommand("moneyselectlang", this, new String[0]));
        new Config(getDataFolder() + "/Commands.yml", Config.YAML).getRootSection().forEach((key, value) -> {
            if (value == null || value.equals("") || value.equals("version") || value.equals("type") || value.equals("language")) {
                return;
            }

            if (!isCurrency2Enabled() && key.contains("2")) { //currency 2
                return;
            }

            PluginCommand<Money> command = matchCommand(key, value.toString(), this, new String[0]);
            if (command == null) {
                return;
            }
            Server.getInstance().getCommandMap().register(value.toString(), command);
        });


        Server.getInstance().getScheduler().scheduleDelayedTask(this, new BankInterestTask(this,
                getConfig().getLong("bank-interest-time", 0) * 1000,
                getConfig().getLong("bank-interest-value", 0),
                db.getLong("last-time", new Date().getTime()),
                getConfig().getBoolean("bank-interest-real", true)), 1200);

        if (getConfig().getString("database-type", "1").equals("1") && getConfig().getInt("database-save-tick", 2400) != 0) {
            Server.getInstance().getScheduler().scheduleRepeatingTask(this, this::save, getConfig().getInt("database-save-tick", 2400));
        }

        if (listener == null) {
            listener = new MoneyEventListener(this);
        }

        Server.getInstance().getPluginManager().registerEvents(listener, this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        save();
    }

    public void save() {
        if (db instanceof HashDatabase) {
            Config config = new Config(new File(getDataFolder(), "data.dat"), Config.YAML);
            config.setAll(db);
            config.save();
        }
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
    }

    private void initDatabase() {
        switch (getConfig().getString("database-type", "1")) {
            case "1": {
                db = new HashDatabase(new Config(new File(getDataFolder(), "data.dat"), Config.YAML));
                return;
            }

            case "2": {
                ConfigSection section = getConfig().getSection("database-server-settings");
                try {
                    db = new SafeRedisDatabase(
                            section.getString("host", "localhost"),
                            section.getInt("port", 6379),
                            section.getString("user", ""),
                            section.getString("password", "")
                    );

                    ((RedisDatabase) db).select(section.getInt("id", 0));
                    ((RedisDatabase) db).getClient().echo("Money plugin is now connected!");
                } catch (JedisConnectionException e) {
                    getLogger().critical("无法连接 Redis 数据库.");
                    getLogger().critical("Cannot connect redis database server");
                    Server.getInstance().forceShutdown();
                }
                return;
            }
            default:
                getLogger().critical("目前仅支持 Config 和 Redis 数据库");
                getLogger().critical("Now this plugin only support Config and Redis database");
                Server.getInstance().forceShutdown();
        }
    }

    public String translateMessage(String message) {
        return this.translator.translate(message);
    }

    public String translateMessage(String message, Object... keys_values) {
        return this.translator.translate(message, keys_values);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Map<String, String>> getDataMap() {
        Map<String, Map<String, String>> result = new HashMap<>();

        for (String s : db.keySet()) {
            result.put(s, new HashMap<String, String>() {
                {
                    ((Map<String, ?>) db.getOrDefault(s, new HashMap<>())).forEach((key, value) -> put(key, value.toString()));
                }
            });
        }

        return result;
    }

    public void updateBankLastTime(long time) {
        db.put("last-time", time);
    }

    /*
     * ***********************
     * ********* API *********
     * ***********************
     */

    @Override
    public String getCurrency(CurrencyType type) {
        return type.booleanValue() ? getCurrency2() : getCurrency1();
    }

    @Override
    public String getCurrency1() {
        return getConfig().getString("money-unit-1");
    }

    @Override
    public String getCurrency2() {
        return getConfig().getString("money-unit-2");
    }


    @Override
    @Deprecated
    public String getMoneyUnit1() {
        return getCurrency1();
    }

    @Override
    @Deprecated
    public String getMonetaryUnit1() {
        return getCurrency1();
    }

    @Override
    @Deprecated
    public String getMoneyUnit2() {
        return getCurrency2();
    }

    @Override
    @Deprecated
    public String getMoneyUnit(boolean unit) {
        return getCurrency(CurrencyType.fromBoolean(unit));
    }

    @Override
    @Deprecated
    public String getMonetaryUnit(CurrencyType unit) {
        return getCurrency(unit);
    }

    @Override
    @Deprecated
    public String getMonetaryUnit2() {
        return getCurrency2();
    }

    @Override
    @Deprecated
    public String getMoneyUnit(CurrencyType type) {
        return getCurrency(type);
    }

    @Override
    @Deprecated
    public String getMonetaryUnit(boolean unit) {
        return getCurrency(CurrencyType.fromBoolean(unit));
    }


    @Override
    @Deprecated
    public boolean isMoneyUnit2Enabled() {
        return isCurrency2Enabled();
    }

    @Override
    public boolean isCurrency2Enabled() {
        try {
            return getConfig().get("enable-unit-2") != null &&
                   Boolean.parseBoolean(getConfig().get("enable-unit-2").toString());
        } catch (Exception ignore) {

        }
        return false;
    }


    @Override
    public float getMoney(String player, CurrencyType type) {
        AbstractDatabase child = db.getChildDatabase(player);
        if (child == null) {
            return 0F;
        }
        if (type == CurrencyType.FIRST) {
            return child.getFloat("money1", 0F);
        } else {
            return child.getFloat("money2", 0F);
        }
    }

    @Override
    public float getMoney(Player player, CurrencyType type) {
        return getMoney(player.getName(), type);
    }

    @Override
    public float getMoney(Player player) {
        return getMoney(player.getName(), CurrencyType.FIRST);
    }

    @Override
    public float getMoney(String player) {
        return getMoney(player, CurrencyType.FIRST);
    }

    @Override
    @Deprecated
    public float getMoney(String player, boolean type) {
        return getMoney(player, CurrencyType.fromBoolean(type));
    }

    @Override
    @Deprecated
    public float getMoney(Player player, boolean type) {
        return getMoney(player.getName(), CurrencyType.fromBoolean(type));
    }


    @Override
    @Deprecated
    public boolean setMoney(String player, float money, boolean type) {
        return setMoney(player, money, CurrencyType.fromBoolean(type));
    }

    @Override
    @Deprecated
    public boolean setMoney(Player player, float money, boolean type) {
        return setMoney(player, money, CurrencyType.fromBoolean(type));
    }

    @Override
    public synchronized boolean setMoney(String player, float money, CurrencyType type) {
        MoneyChangeEvent event = new MoneyChangeEvent(player, money, type);
        Server.getInstance().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            AbstractDatabase data = db.getChildDatabase(player);
            if (data == null) {
                data = new HashDatabase();
            }
            if (type == CurrencyType.FIRST) {
                data.put("money1", event.getTarget());
            } else {
                data.put("money2", event.getTarget());
            }
            db.put(player, data);
            return true;
        }

        return false;
    }

    @Override
    public boolean setMoney(Player player, float money, CurrencyType type) {
        return setMoney(player.getName(), money, type);
    }

    @Override
    public boolean setMoney(Player player, float money) {
        return setMoney(player.getName(), money, CurrencyType.FIRST);
    }

    @Override
    public boolean setMoney(String player, float money) {
        return setMoney(player, money, CurrencyType.FIRST);
    }


    @Override
    @Deprecated
    public boolean addMoney(Player player, float amount, boolean type) {
        return addMoney(player.getName(), amount, CurrencyType.fromBoolean(type));
    }

    @Override
    @Deprecated
    public boolean addMoney(String player, float amount, boolean type) {
        return addMoney(player, amount, CurrencyType.fromBoolean(type));
    }

    @Override
    public boolean addMoney(Player player, float amount, CurrencyType type) {
        return addMoney(player.getName(), amount, type);
    }

    @Override
    public synchronized boolean addMoney(String player, float amount, CurrencyType type) {
        if (amount < 0) {
            return reduceMoney(player, -amount, type);
        }

        MoneyIncreaseEvent event = new MoneyIncreaseEvent(player, amount, type);
        getServer().getPluginManager().callEvent(event);
        return !event.isCancelled() && setMoney(player, getMoney(player, type) + event.getAmount(), type);
    }

    @Override
    public boolean addMoney(String player, float amount) {
        return addMoney(player, amount, CurrencyType.FIRST);
    }

    @Override
    public boolean addMoney(Player player, float amount) {
        return addMoney(player.getName(), amount, CurrencyType.FIRST);
    }


    @Override
    public synchronized boolean reduceMoney(String player, float amount, CurrencyType type) {
        if (amount < 0) {
            return addMoney(player, -amount, type);
        }

        MoneyDecreaseEvent event = new MoneyDecreaseEvent(player, amount, type);
        getServer().getPluginManager().callEvent(event);
        return !event.isCancelled() && setMoney(player, getMoney(player, type) - event.getAmount(), type);
    }

    @Override
    public boolean reduceMoney(Player player, float amount, CurrencyType type) {
        return reduceMoney(player.getName(), amount, type);
    }

    @Override
    public boolean reduceMoney(String player, float amount) {
        return reduceMoney(player, amount, CurrencyType.FIRST);
    }

    @Override
    public boolean reduceMoney(Player player, float amount) {
        return reduceMoney(player, amount, CurrencyType.FIRST);
    }

    @Override
    @Deprecated
    public boolean reduceMoney(Player player, float amount, boolean type) {
        return reduceMoney(player, amount, CurrencyType.fromBoolean(type));
    }

    @Override
    @Deprecated
    public boolean reduceMoney(String player, float amount, boolean type) {
        return reduceMoney(player, amount, CurrencyType.fromBoolean(type));
    }

    @Override
    public float getBank(Player player) {
        return getBank(player.getName());
    }

    @Override
    public float getBank(String player) {
        AbstractDatabase data = db.getChildDatabase(player);
        return data == null ? 0F : data.getFloat("bank", 0F);
    }

    @Override
    public boolean setBank(Player player, float bank) {
        return setBank(player.getName(), bank);
    }

    @Override
    public synchronized boolean setBank(String player, float bank) {
        BankChangeEvent event = new BankChangeEvent(player, bank);
        Server.getInstance().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        AbstractDatabase data = db.getChildDatabase(player);
        if (data == null) {
            data = new HashDatabase();
        }
        data.put("bank", event.getTarget());
        db.put(player, data);
        return true;
    }


    @Override
    @Deprecated
    public int setAllMoney(final float amount) {
        return setAllMoney(amount, CurrencyType.FIRST);
    }

    @Override
    @Deprecated
    public int setAllMoney(final float amount, final CurrencyType type) {
        int i = 0;
        for (String s : getPlayers()) {

            if (setMoney(s, amount, type)) {
                i++;
            }
        }
        return i;
    }

    @Override
    @Deprecated
    public int addAllMoney(final float amount) {
        return addAllMoney(amount, CurrencyType.FIRST);
    }

    @Override
    @Deprecated
    public int addAllMoney(final float amount, final CurrencyType type) {
        int i = 0;
        for (String s : getPlayers()) {

            if (addMoney(s, amount, type)) {
                i++;
            }
        }
        return i;
    }

    @Override
    @Deprecated
    public int reduceAllMoney(final float amount) {
        return reduceAllMoney(amount, CurrencyType.FIRST);
    }

    @Override
    @Deprecated
    public int reduceAllMoney(final float amount, final CurrencyType type) {
        int i = 0;
        for (String s : getPlayers()) {

            if (reduceMoney(s, amount, type)) {
                i++;
            }
        }
        return i;
    }


    @Override
    public synchronized boolean addBank(String player, float amount) {
        if (amount < 0) {
            return reduceBank(player, -amount);
        }

        BankIncreaseEvent event = new BankIncreaseEvent(player, amount);
        getServer().getPluginManager().callEvent(event);
        return !event.isCancelled() && setBank(player, getBank(player) + event.getAmount());

    }

    @Override
    public boolean addBank(Player player, float amount) {
        return addBank(player.getName(), amount);
    }


    @Override
    public synchronized boolean reduceBank(final String player, final float amount) {
        if (amount < 0) {
            return addBank(player, -amount);
        }

        BankDecreaseEvent event = new BankDecreaseEvent(player, amount);
        getServer().getPluginManager().callEvent(event);
        return !event.isCancelled() && setBank(player, getBank(player) - event.getAmount());
    }

    @Override
    public boolean reduceBank(final Player player, final float amount) {
        return reduceBank(player.getName(), amount);
    }


    @Override
    @Deprecated
    public int setAllBank(float amount) {
        int i = 0;
        for (String s : getPlayers()) {
            if (setBank(s, amount)) {
                i++;
            }
        }
        return i;
    }

    @Override
    public synchronized boolean createAccount(String player, float money1, float money2, float bank) {
        AccountCreateEvent event = new AccountCreateEvent(player, money1, money2, bank);
        Server.getInstance().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        AbstractDatabase data = db.getChildDatabase(player);
        if (data == null) {
            data = new HashDatabase();
        }
        data.put("money1", event.getMoney1());
        data.put("money2", event.getMoney2());
        data.put("bank", event.getBank());
        db.put(player, data);
        return true;
    }

    public Set<String> getPlayers() {
        return new HashSet<>(db.keySet());
    }
}
