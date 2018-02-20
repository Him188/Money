package money;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.ServerCommandEvent;
import cn.nukkit.permission.Permission;
import cn.nukkit.plugin.MethodEventExecutor;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import money.command.*;
import money.event.bank.BankChangeEvent;
import money.event.bank.BankDecreaseEvent;
import money.event.bank.BankIncreaseEvent;
import money.event.money.MoneyChangeEvent;
import money.event.money.MoneyDecreaseEvent;
import money.event.money.MoneyIncreaseEvent;
import money.tasks.BankInterestTask;
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

    private Map<String, String> language = new HashMap<>();

    /**
     * 数据库, 使用自 {@code MoeDB} 前置插件
     */
    AbstractDatabase db = null;
    private Map<String, String> commands = new HashMap<>();
    private MoneyEventListener listener;

    private final static String nowCommandVersion = "5";
    private final static String nowLanguageVersion = "7";


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
    private static PluginCommand<Money> matchCommand(String key, String name, Money owner, String[] args) {
        key = key.replace("-", "");
        key = key.toLowerCase();
        for (Class<?> commandClass : COMMAND_CLASSES) {
            if (commandClass.getSimpleName().toLowerCase().equals(key + "command")) {
                try {
                    Constructor<?> constructor = commandClass.getConstructor(String.class, Money.class, String[].class);
                    return (PluginCommand<Money>) constructor.newInstance(name, owner, args);
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
        reloadConfig();
    }

    private Integer errorTimes = 0;

    //由事件触发
    @SuppressWarnings("WeakerAccess")
    public void chooseLanguage(ServerCommandEvent event) {
        if (getConfig().getAll().isEmpty()) {
            event.setCancelled();

            String language;

            if (errorTimes == 3) {
                language = "chs";
                this.getLogger().info(TextFormat.GREEN + "输入3次无效. 将使用默认设置");
                this.getLogger().notice("已使用 [简体中文] 作为默认语言.");
            } else {
                switch (event.getCommand()) {
                    case "":
                        return;
                    case "chs":
                        this.getLogger().notice("已使用 [简体中文] 作为默认语言.");
                        language = "chs";
                        break;
                    case "eng":
                        this.getLogger().notice("Have chosen [English] as the default language.");
                        language = "eng";
                        break;
                    case "cht":
                        this.getLogger().notice("已使用 [繁體中文] 作為默認語言.");
                        language = "cht";
                        break;
                    default:
                        this.getLogger().notice("欢迎使用本经济插件, 请选择语言: (输入 3 次错误自动选择中文)");
                        this.getLogger().notice(
                                "Hello. Please choose a language: (It will choose Chinese Simplified automatically when inputting error 3 times)");
                        this.getLogger().info(TextFormat.AQUA + "chs: 简体中文");
                        this.getLogger().info(TextFormat.AQUA + "cht: 繁體中文");
                        this.getLogger().info(TextFormat.AQUA + "eng: English\n");
                        errorTimes++;
                        return;
                }
            }

            saveResource("Language_" + language + ".properties", "Language.properties", true);
            saveResource("Config_" + language + ".yml", "Config.yml", true);
            saveResource("Commands_" + language + ".yml", "Commands.yml", true);

            init();
        }
    }

    @Override
    public void onEnable() {
        if (getConfig().getAll().isEmpty()) {
            try {
                getServer().getPluginManager().registerEvent(ServerCommandEvent.class, this, EventPriority.HIGHEST,
                        new MethodEventExecutor(this.getClass().getDeclaredMethod("chooseLanguage", ServerCommandEvent.class)), this);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            this.getLogger().notice("欢迎使用本经济插件, 请选择语言: (输入 3 次错误自动选择中文)");
            this.getLogger().notice(
                    "Hello. Please choose a language: (It will choose Chinese Simplified automatically when inputting error 3 times)");
            this.getLogger().info(TextFormat.AQUA + "chs: 简体中文");
            this.getLogger().info(TextFormat.AQUA + "cht: 繁體中文");
            this.getLogger().info(TextFormat.AQUA + "eng: English\n");
            chooseLanguage(new ServerCommandEvent(null, ""));
        } else {
            init();
        }

    }

    @Override
    public void onDisable() {
        super.onDisable();
        save();
    }

    @SuppressWarnings("SpellCheckingInspection")
    private void init() {
        try {
            initConfig();
            initDatabase();
            initLanguage();
            initCommands();

			/* Register permissions */

            final Map<String, String> permissions = new HashMap<String, String>() {
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
                }
            };

            permissions.forEach((name, permission) -> getServer().getPluginManager().addPermission(new Permission(name, permission, permission)));


			/* Register commands */

            commands.forEach((key, value) -> {
                if (value == null || value.equals("") || value.equals("version") || value.equals("type") || value.equals("language")) {
                    return;
                }

                if (!isCurrency2Enabled() && key.contains("2")) { //currency 2
                    return;
                }

                PluginCommand<Money> command = matchCommand(key, value, this, new String[0]);
                if (command == null) {
                    return;
                }
                Server.getInstance().getCommandMap().register(value, command);
            });


            Server.getInstance().getScheduler().scheduleDelayedTask(this, new BankInterestTask(this,
                    Long.parseLong(getConfig().getAll().getOrDefault("bank-interest-time", 0).toString()) * 1000,
                    Float.parseFloat(getConfig().getAll().getOrDefault("bank-interest-value", 0).toString()),
                    db.getLong("last-time", new Date().getTime()),
                    getConfig().getBoolean("bank-interest-real", true)), 1200);

            if (getConfig().getString("database-type", "1").equals("1") && getConfig().getInt("database-save-tick", 2400) != 0) {
                Server.getInstance().getScheduler()
                        .scheduleRepeatingTask(this, this::save, getConfig().getInt("database-save-tick", 2400));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initLanguage() {
        Map<String, Object> c;

        if (new File(getDataFolder() + "/Language.yml").exists()) {
            c = new Config(getDataFolder() + "/Language.yml", Config.YAML).getAll();
            if (!new File(getDataFolder() + "/Language.yml").renameTo(new File(getDataFolder() + "/Language.yml.bak"))) {
                getLogger().notice("语言文件转换成功, 但重命名旧文件 \"Language.yml\" 为 \"Language.yml.bak\" 失败, 请手动重命名将其作为一个备份或删除");
                getLogger().notice("Language file converting success! But renaming old file \"Language.yml\" to" +
                                   " \"Language.yml.bak\" failed. Please rename it manually to make it as a backup or delete it.");
            }
        } else {
            c = new Config(new File(getDataFolder(), "Language.properties"), Config.PROPERTIES).getAll();
        }

        language = new HashMap<String, String>() {
            {
                c.forEach((key, value) -> put(key, value.toString()));
            }
        };

        if (!language.getOrDefault("version", "0").equals(nowLanguageVersion)) {
            saveResource("Language_" + language.getOrDefault("type", "chs") + ".properties", "Language.properties",
                    true);
            initLanguage(language);
        }
    }

    private void initLanguage(Map<String, String> old) {
        initLanguage();
        old.forEach((key, value) -> language.put(key, value));

        Config con = new Config(new File(getDataFolder(), "Language.properties"), Config.PROPERTIES);
        con.setAll(new LinkedHashMap<>(language));
        con.save();
    }


    private void initCommands() {
        Map<String, Object> c = new Config(new File(getDataFolder(), "Commands.yml"), Config.YAML).getAll();
        commands = new HashMap<String, String>() {
            {
                c.forEach((key, value) -> put(key, String.valueOf(value)));
            }
        };

        if (!Objects.equals(commands.get("version"), nowCommandVersion)) {
            saveResource("Commands_" + commands.getOrDefault("type", "chs") + ".yml", "Commands.yml", true);
            initCommands(commands);
        }
    }

    private void initCommands(Map<String, String> old) {
        initCommands();
        old.forEach((key, value) -> commands.put(key, value));

        Config con = new Config(getDataFolder() + "/Commands.yml", Config.YAML);
        con.setAll(new LinkedHashMap<>(commands));
        con.save();
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
        //noinspection ResultOfMethodCallIgnored
        new File(getDataFolder() + "/Config.yml").renameTo(new File(getDataFolder() + "/config.yml"));
        super.reloadConfig();
    }

    private void initConfig() {
        reloadConfig();
        Config config = new Config(Config.YAML);
        config.load(getResource("Config_chs.yml"));

        Map<String, Object> normal = config.getAll();
        final boolean[] formatted = {false};
        normal.forEach((key, value) -> {
            if (getConfig().getAll().putIfAbsent(key, value) == null) {
                formatted[0] = true;
            }
        });

        if (formatted[0]) {
            saveConfig();
        }
    }


    private void initDatabase() {
        switch (getConfig().getString("database-type", "1")) {
            case "1": {
                db = new HashDatabase(new Config(new File(getDataFolder(), "data.dat"), Config.YAML));

                File oldFile = new File(getDataFolder(), "db.dat");

                if (db.keySet().size() == 0 && oldFile.exists()) {
                    convertDatabase(oldFile);
                    save();
                    if (oldFile.renameTo(new File(getDataFolder(), "db.dat.bak"))) {
                        getLogger().notice("数据转换成功! 旧文件已经被重命名为 \"db.dat.bak\", 你可以删除该文件, 或是将其作为一个备份.");
                        getLogger().notice("Data file converting success! The old data file is renamed as " +
                                           "\"db.dat.bak\", you can delete that file, or make it as a backup.");
                    } else {
                        getLogger().notice("数据转换成功, 但重命名旧文件 \"db.dat\" 为 \"db.dat.bak\" 失败, 请手动重命名将其作为一个备份或删除");
                        getLogger().notice("Data file converting success! But renaming old file \"db.dat\" to \"db.dat.bak\" failed. " +
                                           "Please rename it manually to make it as a backup or delete it.");
                    }

                }
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
                    Server.getInstance().shutdown();
                }
                return;
            }
            default:
                getLogger().critical("目前仅支持 Config 和 Redis 数据库");
                getLogger().critical("Now this plugin only support Config and Redis database");
                Server.getInstance().forceShutdown();
        }
    }


    @SuppressWarnings("unchecked")
    private void convertDatabase(File oldFile) {
        OldDatabase old = new OldDatabase();
        old.loadFile(oldFile);

        HashMap<String, Map<String, Object>> map = new HashMap<>();
        old.getData().get(OldDatabase.Keys.MAPS).forEach((key, value) -> {
            if (value == null) {
                return;
            }
            map.put(key, (Map<String, Object>) value);
        });

        this.db.putAll(map);
    }


    public String translateMessage(String message) {
        if (language.get(message) == null) {
            return TextFormat.colorize(message);
        }

        return TextFormat.colorize(language.get(message));
    }

    public String translateMessage(String message, Map<String, Object> args) {
        if (language.get(message) == null) {
            return message;
        }

        final String[] msg = {translateMessage(message)};
        args.forEach((key, value) -> {
            if (value instanceof Double || value instanceof Float) {
                msg[0] = msg[0].replace("$" + key + "$", String.valueOf(Math.round(Double.parseDouble(value.toString()))));
            } else {
                msg[0] = msg[0].replace("$" + key + "$", value.toString());
            }
        });
        return msg[0];
    }

    public String translateMessage(String message, Object... keys_values) {
        Map<String, Object> map = new HashMap<>();

        String key = null;
        for (Object o : keys_values) {
            if (key == null) {
                key = o.toString();
            } else {
                map.put(key, o);
                key = null;
            }
        }

        return translateMessage(message, map);
    }


    @SuppressWarnings("unchecked")
    public LinkedHashMap<String, Map<String, String>> getDataMap() {
        LinkedHashMap<String, Map<String, String>> result = new LinkedHashMap<>();

        for (String s : db.keySet()) {
            result.put(s, new LinkedHashMap<String, String>() {
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
    public boolean setMoney(String player, float money, CurrencyType type) {
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
    public boolean addMoney(String player, float amount, CurrencyType type) {
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
    public boolean reduceMoney(String player, float amount, CurrencyType type) {
        if (amount < 0) {
            return addMoney(player, -amount, type);
        }

        MoneyDecreaseEvent event = new MoneyDecreaseEvent(player, amount, type);
        getServer().getPluginManager().callEvent(event);
        return !event.isCancelled() && setMoney(player, getMoney(player, type) - amount, type);
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
    public boolean setBank(String player, float bank) {
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
    public int setAllMoney(final float amount) {
        return setAllMoney(amount, CurrencyType.FIRST);
    }

    @Override
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
    public int addAllMoney(final float amount) {
        return addAllMoney(amount, CurrencyType.FIRST);
    }

    @Override
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
    public int reduceAllMoney(final float amount) {
        return reduceAllMoney(amount, CurrencyType.FIRST);
    }

    @Override
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
    public boolean addBank(String player, float amount) {
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
    public boolean reduceBank(final String player, final float amount) {
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
    public int setAllBank(float amount) {
        int i = 0;
        for (String s : getPlayers()) {
            if (setBank(s, amount)) {
                i++;
            }
        }
        return i;
    }


    public Set<String> getPlayers() {
        return new HashSet<>(db.keySet());
    }
}
