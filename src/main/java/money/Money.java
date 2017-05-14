package money;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParameter;
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
import net.mamoe.moedb.db.ConfigDatabase;
import net.mamoe.moedb.db.KeyValueDatabase;
import net.mamoe.moedb.db.RedisDatabase;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Money 的主类
 *
 * @author Him188 @ Money Project
 * @since Money 1.0.0
 */
public final class Money extends PluginBase implements MoneyAPI, Listener {
    private static Money instance = null;

    private Map<String, String> language = new HashMap<>();

    /**
     * 数据库, 使用自 {@code MoeDB} 前置插件
     */
    KeyValueDatabase db = null; // TODO: 2017/5/3  数据库选择
    private Map<String, String> commands = new HashMap<>();

    private final static String nowCommandVersion = "4";
    private final static String nowLanguageVersion = "6";


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
    };

    private static CommandExecutor matchExecutor(String key, String name, Money owner, String[] args, Map<String, CommandParameter[]> commandArgs) {
        key = key.replace("-", "");
        key = key.toLowerCase();
        for (Class<?> commandClass : COMMAND_CLASSES) {
            if (commandClass.getSimpleName().toLowerCase().equals(key + "command")) {
                try {
                    Constructor<?> constructor = commandClass.getConstructor(String.class, Money.class, String[].class, Map.class);
                    return (CommandExecutor) constructor.newInstance(name, owner, args, commandArgs);
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

    {
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
                    put("money.command.superset1", "op");
                }
            };

            permissions.forEach((name, permission) -> getServer().getPluginManager().addPermission(new Permission(name, permission, permission)));


			/* Register Command parameters */

            final Map<String, Map<String, CommandParameter[]>> parameters =
                    new HashMap<String, Map<String, CommandParameter[]>>() {
                        {
                            put("wallet-info-1", new HashMap<String, CommandParameter[]>() {{
                                put("wallet-info-1", new CommandParameter[]{

                                });
                            }});
                            put("wallet-info-2", new HashMap<String, CommandParameter[]>() {{
                                put("wallet-info-2", new CommandParameter[]{

                                });
                            }});
                            put("bank-info", new HashMap<String, CommandParameter[]>() {{
                                put("bank-info", new CommandParameter[]{

                                });
                            }});
                            put("bank-save", new HashMap<String, CommandParameter[]>() {{
                                put("bank-save", new CommandParameter[]{
                                        new CommandParameter("amount", CommandParameter.ARG_TYPE_INT, false)
                                });
                            }});
                            put("bank-take", new HashMap<String, CommandParameter[]>() {{
                                put("bank-take", new CommandParameter[]{
                                        new CommandParameter("amount", CommandParameter.ARG_TYPE_INT, false)
                                });
                            }});
                            put("pay-1", new HashMap<String, CommandParameter[]>() {{
                                put("bank-1", new CommandParameter[]{
                                        new CommandParameter("player", CommandParameter.ARG_TYPE_STRING, false),
                                        new CommandParameter("amount", CommandParameter.ARG_TYPE_INT, false)
                                });
                            }});
                            put("pay-2", new HashMap<String, CommandParameter[]>() {{
                                put("bank-2", new CommandParameter[]{
                                        new CommandParameter("player", CommandParameter.ARG_TYPE_STRING, false),
                                        new CommandParameter("amount", CommandParameter.ARG_TYPE_INT, false)
                                });
                            }});
                            put("give-1", new HashMap<String, CommandParameter[]>() {{
                                put("give-1", new CommandParameter[]{
                                        new CommandParameter("player", CommandParameter.ARG_TYPE_STRING, false),
                                        new CommandParameter("amount", CommandParameter.ARG_TYPE_INT, false)
                                });
                            }});
                            put("give-2", new HashMap<String, CommandParameter[]>() {{
                                put("give-2", new CommandParameter[]{
                                        new CommandParameter("player", CommandParameter.ARG_TYPE_STRING, false),
                                        new CommandParameter("amount", CommandParameter.ARG_TYPE_INT, false)
                                });
                            }});
                            put("set-1", new HashMap<String, CommandParameter[]>() {{
                                put("set-1", new CommandParameter[]{
                                        new CommandParameter("player", CommandParameter.ARG_TYPE_STRING, false),
                                        new CommandParameter("amount", CommandParameter.ARG_TYPE_INT, false)
                                });
                            }});
                            put("set-2", new HashMap<String, CommandParameter[]>() {{
                                put("set-2", new CommandParameter[]{
                                        new CommandParameter("player", CommandParameter.ARG_TYPE_STRING, false),
                                        new CommandParameter("amount", CommandParameter.ARG_TYPE_INT, false)
                                });
                            }});
                            put("super-set-1", new HashMap<String, CommandParameter[]>() {{
                                put("super-set-1", new CommandParameter[]{
                                        new CommandParameter("amount", CommandParameter.ARG_TYPE_INT, false)
                                });
                            }});
                            put("super-set-2", new HashMap<String, CommandParameter[]>() {{
                                put("super-set-2", new CommandParameter[]{
                                        new CommandParameter("amount", CommandParameter.ARG_TYPE_INT, false)
                                });
                            }});
                            put("list-1", new HashMap<String, CommandParameter[]>() {{
                                put("list-1", new CommandParameter[]{
                                        new CommandParameter("page", CommandParameter.ARG_TYPE_INT, true)
                                });
                            }});
                            put("list-2", new HashMap<String, CommandParameter[]>() {{
                                put("list-2", new CommandParameter[]{
                                        new CommandParameter("page", CommandParameter.ARG_TYPE_INT, true)
                                });
                            }});
                            put("give-online-1", new HashMap<String, CommandParameter[]>() {{
                                put("give-online-1", new CommandParameter[]{
                                        new CommandParameter("amount", CommandParameter.ARG_TYPE_INT, false)
                                });
                            }});
                            put("give-online-2", new HashMap<String, CommandParameter[]>() {{
                                put("give-online-2", new CommandParameter[]{
                                        new CommandParameter("amount", CommandParameter.ARG_TYPE_INT, false)
                                });
                            }});
                        }
                    };

			/* Register commands */

            commands.forEach((key, value) -> {
                if (value == null || value.equals("") || value.equals("version") || value.equals("type") || value.equals("language")) {
                    return;
                }

                if (!isCurrency2Enabled() && key.contains("2")) { //currency 2
                    return;
                }

                PluginCommand<Money> cmd;
                cmd = new PluginCommand<>(value, this);
                cmd.setExecutor(matchExecutor(key, value, this, new String[0], parameters.getOrDefault(key, new HashMap<>())));
                Server.getInstance().getCommandMap().register(value, cmd);
            });


            Server.getInstance().getScheduler().scheduleDelayedTask(this, new BankInterestTask(this,
                    Long.parseLong(getConfig().getAll().getOrDefault("bank-interest-time", 0).toString()) * 1000,
                    Float.parseFloat(getConfig().getAll().getOrDefault("bank-interest-value", 0).toString()),
                    Long.parseLong(db.get("last-time", new Date().getTime()).toString()),
                    getConfig().getBoolean("bank-interest-real", true)), 1200);

            if (getConfig().getString("database-type", "1").equals("1") && getConfig().getInt("database-save-tick", 2400) != 0) {
                Server.getInstance().getScheduler()
                        .scheduleDelayedTask(this, ((ConfigDatabase) this.db)::save, getConfig().getInt("database-save-tick", 2400));
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
            c = new Config(getDataFolder() + "/Language.properties", Config.PROPERTIES).getAll();
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

        Config con = new Config(getDataFolder() + "/Language.properties", Config.PROPERTIES);
        con.setAll(new LinkedHashMap<>(language));
        con.save();
    }


    private void initCommands() {
        Map<String, Object> c = new Config(getDataFolder() + "/Commands.yml", Config.YAML).getAll();
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
            case "1":
                db = new ConfigDatabase(new Config(getDataFolder() + "/db.dat", Config.YAML),
                        getConfig().getInt("database-save-tick", 2400) == 0);

                if (db.getKeys().size() == 0 && new File(getDataFolder() + "/Data.yml").exists()) {
                    if (!convertDatabase(getDataFolder() + "/Data.yml")) {
                        getLogger().critical("数据转换失败, 但插件仍将继续加载, 使用空的新数据库. 旧的文件保留, 你可以修复其错误后重新启动服务器");
                        getLogger().critical("Data file converting failed. But the plugin will still enable while using" +
                                " new-empty-database. Old data file will not be deleted, you can fix error(s) which is printed " +
                                "just now and restart the server, the plugin will retry converting.");
                    } else {
                        if (new File(getDataFolder() + "/Data.yml").renameTo(new File(getDataFolder() + "/Data.yml.bak"))) {
                            getLogger().notice("数据转换成功! 旧文件已经被重命名为 \"Data.yml.bak\", 你可以删除该文件, 或是将其作为一个备份.");
                            getLogger().notice("Data file converting success! The old data file is renamed as " +
                                    "\"Data.yml.bak\", you can delete that file, or make it as a backup.");
                        } else {
                            getLogger().notice("数据转换成功, 但重命名旧文件 \"Data.yml\" 为 \"Data.yml.bak\" 失败, 请手动重命名将其作为一个备份或删除");
                            getLogger()
                                    .notice("Data file converting success! But renaming old file \"Data.yml\" to \"Data.yml.bak\" failed. " +
                                            "Please rename it manually to make it as a backup or delete it.");
                        }

                    }
                }
                return;
            case "2":
                ConfigSection section = getConfig().getSection("database-server-settings");
                try {
                    db = new RedisDatabase(
                            section.getString("host", "localhost"),
                            section.getInt("port", 6379),
                            section.getString("user", ""),
                            section.getString("password", "")
                    );

                    ((RedisDatabase) db).select(section.getInt("id", 0));
                    ((RedisDatabase) db).echo("Money plugin is now connected!");
                } catch (JedisConnectionException e) {
                    getLogger().critical("无法连接 Redis 数据库.");
                    getLogger().critical("Cannot connect redis database server");
                    Server.getInstance().shutdown();
                }
                return;
            default:
                getLogger().critical("目前仅支持 Config 和 Redis 数据库");
                getLogger().critical("Now this plugin only support Config and Redis database");
                Server.getInstance().shutdown();
        }
    }


    private boolean convertDatabase(String oldFile) {
        OldDatabase old = new OldDatabase();
        if (!old.loadFile(oldFile)) {
            getLogger().critical("检测到旧数据文件 (Data.yml) 存在, 但无法转换为新数据库(错误原因: 无法读取该文件). 请检查是否有权限且保证未对其进行修改!");
            getLogger().critical(
                    "It seems that old data file (Data.yml) is exists, but the plugin can't convert it to new format(Reason: can't read file). " +
                            "Please check the filesystem permission and be sure you did't modify it!");
            return false;
        }

        old.getData().forEach((player, data) -> db.hashSet(player, new LinkedHashMap<>(data)));
        return true;
    }


    public String translateMessage(String message) {
        if (language.get(message) == null) {
            return message;
        }

        return language.get(message);
    }

    public String translateMessage(String message, Map<String, Object> args) {
        if (language.get(message) == null) {
            return message;
        }

        final String[] msg = {language.get(message)};
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


    public LinkedHashMap<String, LinkedHashMap<String, String>> getDataMap() {
        LinkedHashMap<String, LinkedHashMap<String, String>> result = new LinkedHashMap<>();

        for (String s : db.getKeys()) {
            result.put(s, new LinkedHashMap<String, String>() {
                {
                    db.hashGetAll(s, new HashMap<>()).forEach((key, value) -> put(key, value.toString()));
                }
            });
        }

        return result;
    }

    public void updateBankLastTime(long time) {
        db.set("last-time", time);
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
        if (type == CurrencyType.FIRST) {
            return Float.parseFloat(db.hashGet(player, "money1", "0").toString());
        } else {
            return Float.parseFloat(db.hashGet(player, "money2", "0").toString());
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
        return getMoney(player, CurrencyType.SECOND);
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
            if (type == CurrencyType.FIRST) {
                return db.hashSet(player, "money1", event.getTarget());
            } else {
                return db.hashSet(player, "money2", event.getTarget());
            }
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
        MoneyIncreaseEvent event = new MoneyIncreaseEvent(player, amount, type);
        getServer().getPluginManager().callEvent(event);
        return !event.isCancelled() && setMoney(player, getMoney(player) + amount, type);
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
        MoneyDecreaseEvent event = new MoneyDecreaseEvent(player, amount, type);
        getServer().getPluginManager().callEvent(event);
        return !event.isCancelled() && setMoney(player, getMoney(player) - amount);
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
        return Float.parseFloat(db.hashGet(player, "bank", "0").toString());
    }

    @Override
    public boolean setBank(Player player, float bank) {
        return setBank(player.getName(), bank);
    }

    @Override
    public boolean setBank(String player, float bank) {
        BankChangeEvent event = new BankChangeEvent(player, bank);
        Server.getInstance().getPluginManager().callEvent(event);
        return !event.isCancelled() && db.hashSet(player, "bank", event.getTarget());
    }


    @Override
    public int setAllMoney(final float amount) {
        return setAllMoney(amount, CurrencyType.FIRST);
    }

    @Override
    public int setAllMoney(final float amount, final CurrencyType type) {
        int i = 0;
        for (String s : getPlayersFiltered()) {

            i++;
            setMoney(s, amount, type);
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
        for (String s : getPlayersFiltered()) {

            i++;
            addMoney(s, amount, type);
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
        for (String s : getPlayersFiltered()) {

            i++;
            reduceMoney(s, amount, type);
        }
        return i;
    }


    @Override
    public boolean addBank(String player, float amount) {
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
        BankDecreaseEvent event = new BankDecreaseEvent(player, amount);
        getServer().getPluginManager().callEvent(event);
        return !event.isCancelled() && setBank(player, getBank(player) - event.getAmount());
    }

    @Override
    public boolean reduceBank(final Player player, final float amount) {
        return reduceMoney(player.getName(), amount);
    }


    @Override
    public int setAllBank(float amount) {
        int i = 0;
        for (String s : getPlayersFiltered()) {
            if (setBank(s, amount)) {
                i++;
            }
        }
        return i;
    }


    @Override
    public Set<String> getPlayers() {
        return new LinkedHashSet<>(db.getKeys());
    }

    @Override
    public Set<String> getPlayersFiltered() {
        Set<String> player = getPlayers();
        player.removeIf((key) -> db.hashGetAll(key, new HashMap<>()).isEmpty());
        return player;
    }
}
