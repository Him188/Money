package money;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.server.ServerCommandEvent;
import cn.nukkit.plugin.MethodEventExecutor;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import money.command.*;
import money.event.BankChangeEvent;
import money.event.MoneyChangeEvent;

import java.io.File;
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
	public YAMLDatabase data = null;
	private Map<String, String> commands = new HashMap<>();

	protected long bank_time = 0L;
	protected long last_time = 0L;
	protected double bank_interest = 0d;

	private final static String nowCommandVersion = "4";
	private final static String nowLanguageVersion = "5";


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

	private static CommandExecutor matchExecutor(String key) {
		key = key.replace("-", "");
		key = key.toLowerCase();

		for (Class<?> commandClass : COMMAND_CLASSES) {
			if (commandClass.getSimpleName().toLowerCase().equals(key)) {
				try {
					return (CommandExecutor) commandClass.newInstance();
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

	@Override
	public void onLoad() {
		getDataFolder().mkdir();
		reloadConfig();
	}

	private Integer errorTimes = 0;

	//由事件触发
	public void chooseLanguage(ServerCommandEvent event) {
		if (getConfig().getAll().isEmpty()) {
			event.setCancelled();

			String language;

			if (errorTimes == 3) {
				language = "chs";
				this.getLogger().info(TextFormat.GREEN + "输入3次错误, 将使用默认设置");
				this.getLogger().info(TextFormat.YELLOW + "已使用 [简体中文] 作为默认语言.");
			} else {
				switch (event.getCommand()) {
					case "":
						return;
					case "chs":
						this.getLogger().info(TextFormat.YELLOW + "已使用 [简体中文] 作为默认语言.");
						language = "chs";
						break;
					case "eng":
						this.getLogger().info(TextFormat.YELLOW + "Have chosen [English] as the default language.");
						language = "eng";
						break;
					case "cht":
						this.getLogger().info(TextFormat.YELLOW + "已使用 [繁體中文] 作為默認語言.");
						language = "cht";
						break;
					default:
						this.getLogger().info(TextFormat.YELLOW + "请输入 'chs' 或 'cht' 或 'eng'");
						this.getLogger().info(TextFormat.YELLOW + "Please enter 'chs', 'cht' or 'eng'");
						errorTimes++;
						return;
				}
			}

			saveResource("Language_" + language + ".yml", "Language.yml", true);
			saveResource("Config_" + language + ".yml", "Config.yml", true);
			saveResource("Commands_" + language + ".yml", "Commands.yml", true);

			init();
			return;
		}
	}

	@Override
	public void onEnable() {
		if (getConfig().getAll().isEmpty()) {
			try {
				getServer().getPluginManager().registerEvent(ServerCommandEvent.class, this, EventPriority.HIGHEST, new MethodEventExecutor(this.getClass().getMethod("chooseLanguage")), this);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}

			this.getLogger().info(TextFormat.YELLOW + "欢迎使用本插件, 请选择语言: (输入 3 次错误自动选择中文)");
			this.getLogger().info(TextFormat.YELLOW + "Hello. Please choose a language: (It will choose chs automatically when inputting error 3 times)");
			this.getLogger().info(TextFormat.AQUA + "chs: 简体中文");
			this.getLogger().info(TextFormat.AQUA + "cht: 繁體中文");
			this.getLogger().info(TextFormat.AQUA + "eng: English\n");
			chooseLanguage(new ServerCommandEvent(null, ""));
		} else {
			init();
		}
	}

	public void init() {

		try {
			initDatabase();
			initConfig();
			initLanguage();
			initCommands();

			instance = this;

			/* Register Commands */

			/* Command Parameters */
			final Map<String, Map<String, CommandParameter[]>> parameters = new HashMap<String, Map<String, CommandParameter[]>>() {
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

			commands.forEach((key, value) -> {
				PluginCommand cmd;
				cmd = new PluginCommand<>(value, this);
				cmd.setExecutor(matchExecutor(key));
				cmd.setCommandParameters(parameters.getOrDefault(key, new HashMap<>()));
				Server.getInstance().getCommandMap().register(key, cmd);
			});

			Server.getInstance().getScheduler().scheduleDelayedTask(new BankInterestTask(this), 1200);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Server.getInstance().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {
		save();
	}


	private void initLanguage() {
		Map<String, Object> c = new Config(getDataFolder() + "/Language.yml", Config.YAML).getAll();
		language = new HashMap<>();
		for (Map.Entry<String, Object> entry : c.entrySet()) {
			language.put(entry.getKey(), entry.getValue().toString());
		}

		if (!Objects.equals(language.get("version"), nowLanguageVersion)) {
			saveResource("Language_" + language.getOrDefault("type", "chs") + ".yml", "Language.yml", true);
			initLanguage(language);
		}
	}

	private void initLanguage(Map<String, String> old) {
		initLanguage();
		old.forEach((key, value) -> language.put(key, value));

		Config con = new Config(getDataFolder() + "/Language.yml", Config.YAML);
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		map.putAll(language);
		con.setAll(map);
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
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		map.putAll(commands);
		con.setAll(map);
		con.save();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent event) {
		data.initPlayer(event.getPlayer().getName());
	}

	protected void save() {
		data.save();
	}

	protected String getLanguage() {
		try {
			return (String) getConfig().get("language");
		} catch (Exception ignore) {

		}

		return null;
	}

	private void initConfig() {
		saveResource("Config_chs.yml", "Config_default.yml", true);

		Map<String, Object> normal = new Config(getDataFolder() + "/Config_default.yml", Config.YAML).getAll();
		final boolean[] formatted = {false};
		normal.forEach((key, value) -> {
			getConfig().getAll().putIfAbsent(key, value);

			formatted[0] = true;
		});

		if (formatted[0]) {
			Config con = new Config(getDataFolder() + "/Config.yml", Config.YAML);
			con.setAll((LinkedHashMap<String, Object>) getConfig().getAll());
			con.save();
		}

		new File(getDataFolder() + "/Config_default.yml").delete();

		data.data.putIfAbsent("__BANK__", new HashMap<>());

		last_time = new Date().getTime();
		data.data.get("__BANK__").put("time", Long.toString(last_time));
		bank_time = Long.parseLong(getConfig().getAll().getOrDefault("bank-interest-time", 0).toString()) * 1000;
		bank_interest = 1 + Double.parseDouble(getConfig().getAll().getOrDefault("bank-interest-value", 0).toString());
	}

	private void initDatabase() {
		data = new YAMLDatabase(this);
		if (!data.loadFile(getDataFolder() + "/Data.yml")) {
			this.getLogger().warning(translateMessage("load-Database-error"));
			this.getLogger().warning(translateMessage("stop-plugin"));
			Server.getInstance().getPluginManager().disablePlugin(this);
			return;
		}
	}


	public String translateMessage(String message) {
		if (language.get(message) == null) {
			return message;
		}

		return language.get(message);
	}

	public String translateMessage(String message, Object... args) {  //%s 字符串 %n换行符
		if (language.get(message) == null) {
			return message;
		}

		return String.format(language.get(message), args);
	}


	/*
	 * ***********************
	 * ********* API *********
	 * ***********************
	 */

	@Override
	public String getMoneyUnit1() {
		return getMonetaryUnit1();
	}

	@Override
	public String getMoneyUnit2() {
		return getMonetaryUnit2();
	}

	@Override
	@Deprecated
	public String getMoneyUnit(boolean unit) {
		return unit ? getMoneyUnit2() : getMoneyUnit1();
	}

	@Override
	public String getMonetaryUnit(CurrencyType unit) {
		return null;
	}

	@Override
	public String getMonetaryUnit1() {
		try {
			return getConfig().get("money-unit-1").toString();
		} catch (Exception ignore) {

		}

		return null;
	}

	@Override
	public String getMonetaryUnit2() {
		try {
			return getConfig().get("money-unit-2").toString();
		} catch (Exception ignore) {

		}

		return null;
	}

	@Override
	public String getMoneyUnit(CurrencyType type) {
		return type.booleanValue() ? getMoneyUnit2() : getMoneyUnit1();
	}

	@Override
	public String getMonetaryUnit(boolean unit) {
		return unit ? getMonetaryUnit2() : getMonetaryUnit1();
	}


	@Override
	public boolean isMoneyUnit2Enabled() {
		try {
			return getConfig().get("enable-unit-2") == null || !Boolean.parseBoolean(getConfig().get("enable-unit-2").toString());
		} catch (Exception ignore) {

		}
		return false;
	}


	@Override
	@Deprecated
	public Double getMoney(String player, boolean type) {
		try {
			if (type) {
				return Double.parseDouble(data.get(player, "money2"));
			} else {
				return Double.parseDouble(data.get(player, "money1"));
			}
		} catch (Exception ignore) {

		}

		return null;
	}

	@Override
	@Deprecated
	public Double getMoney(Player player, boolean type) {
		return getMoney(player.getName(), type);
	}

	@Override
	public Double getMoney(String player, CurrencyType type) {
		return null;
	}

	@Override
	public Double getMoney(Player player, CurrencyType type) {
		return null;
	}

	@Override
	public Double getMoney(Player player) {
		return getMoney(player.getName(), false);
	}

	@Override
	public Double getMoney(String player) {
		return getMoney(player, false);
	}


	@Override
	@Deprecated
	public void setMoney(String player, double money, boolean type) {
		try {
			if (type) {
				MoneyChangeEvent event = new MoneyChangeEvent(player, money, true);
				Server.getInstance().getPluginManager().callEvent(event);
				if (!event.isCancelled()) {
					data.set(player, "money2", Double.toString(event.getTarget()));
				}
			} else {
				MoneyChangeEvent event = new MoneyChangeEvent(player, money, false);
				Server.getInstance().getPluginManager().callEvent(event);
				if (!event.isCancelled()) {
					data.set(player, "money1", Double.toString(event.getTarget()));
				}
			}
		} catch (Exception ignore) {

		}
	}

	@Override
	@Deprecated
	public void setMoney(Player player, double money, boolean type) {
		setMoney(player.getName(), money, type);
	}

	@Override
	public void setMoney(String player, double money, CurrencyType type) {

	}

	@Override
	public void setMoney(Player player, double money, CurrencyType type) {

	}

	@Override
	public void setMoney(Player player, double money) {
		setMoney(player.getName(), money, false);
	}

	@Override
	public void setMoney(String player, double money) {
		setMoney(player, money, false);
	}


	@Override
	@Deprecated
	public void addMoney(Player player, double amount, boolean type) {
		addMoney(player.getName(), amount, type);
	}

	//enumeration type
	@Override
	@SuppressWarnings("ConstantConditions")
	@Deprecated
	public void addMoney(String player, double amount, boolean type) {
		setMoney(player, getMoney(player, type) + amount, type);
	}

	@Override
	public void addMoney(Player player, double amount, CurrencyType type) {
		addMoney(player.getName(), amount, type);
	}

	@Override
	public void addMoney(String player, double amount, CurrencyType type) {
		setMoney(player, getMoney(player, type) + amount, type);
	}

	@Override
	public void addMoney(String player, double amount) {
		addMoney(player, amount, false);
	}

	public void addMoney(Player player, double amount) {
		addMoney(player.getName(), amount, false);
	}

	@Override
	public void reduceMoney(String player, double amount) {
		addMoney(player, -amount);
	}

	@Override
	public void reduceMoney(Player player, double amount) {
		addMoney(player, -amount);
	}

	@Override
	@Deprecated
	public void reduceMoney(Player player, double amount, boolean type) {
		addMoney(player, -amount, type);
	}

	@Override
	@Deprecated
	public void reduceMoney(String player, double amount, boolean type) {
		addMoney(player, -amount, type);
	}

	@Override
	public Double getBank(Player player) {
		return getBank(player.getName());
	}

	@Override
	public Double getBank(String player) {
		try {
			return Double.parseDouble(data.get(player, "bank"));
		} catch (Exception ignore) {

		}

		return null;
	}

	@Override
	public void setBank(Player player, double bank) {
		setBank(player.getName(), bank);
	}

	@Override
	public void setBank(String player, double bank) {
		BankChangeEvent event = new BankChangeEvent(player, bank);
		Server.getInstance().getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			data.set(player, "bank", Double.toString(event.getTarget()));
		}
	}


	@Override
	public void setAllMoney(final double amount) {
		setAllMoney(amount, CurrencyType.FIRST);
	}

	@Override
	public void setAllMoney(final double amount, CurrencyType type) {
		final String k = type.booleanValue() ? "money1" : "money2";
		final String v = String.valueOf(amount);
		data.getData().replaceAll((key, value) -> {
			value.put(k, v);
			return value;
		});
	}
}
