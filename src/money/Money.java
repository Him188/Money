package money;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
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
	YAMLDatabase data = null;
	private Map<String, String> commands = new HashMap<>();

	private boolean canUseMoney2 = false;
	private boolean command_threaded = false;

	protected long bank_time = 0L;
	protected long last_time = 0L;
	protected double bank_interest = 0d;

	private final static String nowCommandVersion = "4";
	private final static String nowLanguageVersion = "5";


	public static Money getInstance() {
		return instance;
	}

	@Override
	public void onLoad() {
		try {
			getDataFolder().mkdir();
			reloadConfig();
			if (getConfig().getAll().isEmpty()) {
				String language = "";
				Integer errorTimes = 0;
				Boolean selected = false;
				while (!selected) {
					this.getLogger().info(TextFormat.YELLOW + "欢迎使用本插件, 请选择语言: (输入 3 次错误自动选择中文)");
					this.getLogger().info(TextFormat.YELLOW + "Hello. Please choose a language: (It will choose chs automatically when inputting error 3 times)");
					this.getLogger().info(TextFormat.AQUA + "chs: 简体中文");
					this.getLogger().info(TextFormat.AQUA + "cht: 繁體中文");
					this.getLogger().info(TextFormat.AQUA + "eng: English\n");
					Scanner in = new Scanner(System.in);
					switch (in.next()) {
						case "chs":
							this.getLogger().info(TextFormat.YELLOW + "已使用 [简体中文] 作为默认语言.");
							selected = true;
							language = "chs";
							break;
						case "eng":
							this.getLogger().info(TextFormat.YELLOW + "Have chosen [English] as the default language.");
							selected = true;
							language = "eng";
							break;
						case "cht":
							this.getLogger().info(TextFormat.YELLOW + "已使用 [繁體中文] 作為默認語言.");
							selected = true;
							language = "cht";
							break;
						default:
							this.getLogger().info(TextFormat.YELLOW + "请输入 'chs' 或 'cht' 或 'eng'");
							this.getLogger().info(TextFormat.YELLOW + "Please enter 'chs', 'cht' or 'eng'");
							errorTimes++;
							break;
					}

					if (errorTimes == 3) {
						language = "chs";
						this.getLogger().info(TextFormat.GREEN + "输入3次错误, 将使用默认设置");
						this.getLogger().info(TextFormat.YELLOW + "已使用 [简体中文] 作为默认语言.");
						break;
					}
				}


				saveResource("Language_" + language + ".yml", "Language.yml", true);
				saveResource("Config_" + language + ".yml", "Config.yml", true);
				saveResource("Commands_" + language + ".yml", "Commands.yml", true);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onEnable() {
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
				cmd.setExecutor(this);// TODO: 2017/4/25
				cmd.setCommandParameters(parameters.getOrDefault(key, new HashMap<>()));
				Server.getInstance().getCommandMap().register(key, cmd);
			});

			Server.getInstance().getScheduler().scheduleDelayedTask(new TaskHandler(this), 1200);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Server.getInstance().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {
		save();
	}

	@Override
	public boolean onCommand(CommandSender poster, Command command, String label, String[] args) {
		if (command_threaded) {
			// TODO: 2017/4/25  async task
			new Thread(() -> {
				String msg = callCommand(poster, command, label, args);
				if (!Objects.equals(msg, "")) {
					poster.sendMessage(msg);
				}
			}).start();
			return true;
		}

		String msg = callCommand(poster, command, label, args);
		if (!Objects.equals(msg, "")) {
			poster.sendMessage(msg);
			return true;
		}

		return false;
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
		command_threaded = (boolean) getConfig().get("command-threaded");
	}

	private void initDatabase() {
		data = new YAMLDatabase(this);
		if (!data.loadFile(getDataFolder() + "/Data.yml")) {
			this.getLogger().warning(translateMessage("load-Database-error"));
			this.getLogger().warning(translateMessage("stop-plugin"));
			Server.getInstance().getPluginManager().disablePlugin(this);
			return;
		}

		canUseMoney2 = (getConfig().get("enable-money-2") == null || !Boolean.parseBoolean(getConfig().get("enable-money-2").toString()));

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


	@SuppressWarnings("ConstantConditions")
	private String callCommand(CommandSender poster, Command command, String label, String[] args) {
		Player sender = null;
		if (poster instanceof Player) {
			sender = (Player) poster;
		}
		if (poster == null) {
			return "";
		}
		Player p;
		Double money, to;
		String name;
		String cmd = command.getName().toLowerCase();
		for (Map.Entry<String, String> entry : commands.entrySet()) {
			if (Objects.equals(cmd, entry.getValue())) {
				switch (entry.getKey()) {
					case "wallet-info-1":
						if (sender == null) {
							return translateMessage("use-in-game");
						}

						return translateMessage("your-money-1", Math.round(getMoney(sender, false)), getMoneyUnit1());

					case "wallet-info-2":
						if (!canUseMoney2) {
							return translateMessage("cannot-use-money-2");
						}

						if (sender == null) {
							return translateMessage("use-in-game");
						}

						return translateMessage("your-money-2", Math.round(getMoney(sender, true)), getMoneyUnit2());

					case "bank-info":
						if (sender == null) {
							return translateMessage("use-in-game");
						}

						return translateMessage("your-bank", Math.round(getBank(sender)), getMoneyUnit1());

					case "bank-save":
						if (sender == null) {
							return translateMessage("use-in-game");
						}

						if (args.length < 1) {
							return translateMessage("bank-save-format-error", commands.get("bank-save"));
						}

						to = Double.parseDouble(args[0]);
						money = getMoney(sender);
						if (money < to) {
							return translateMessage("bank-save-value-error");
						}
						setMoney(sender, money - to);
						setBank(sender, getBank(sender) + to);

						return translateMessage("bank-save-success", Math.round(Double.parseDouble(args[0])), getMoneyUnit1());

					case "bank-take":
						if (sender == null) {
							return translateMessage("use-in-game");
						}

						if (args.length < 1) {
							return translateMessage("bank-take-format-error", commands.get("bank-take"));
						}

						to = Double.parseDouble(args[0]);
						money = getBank(sender);
						if (money < to) {
							return translateMessage("bank-take-value-error");
						}
						setBank(sender, money - to);
						setMoney(sender, getMoney(sender) + to);

						return translateMessage("bank-take-success", Math.round(Double.parseDouble(args[0])), getMoneyUnit1());

					case "pay-1":
						if (sender == null) {
							return translateMessage("use-in-game");
						}

						if (args.length < 2) {
							return translateMessage("pay-format-error", commands.get("pay-1"));
						}

						to = Double.parseDouble(args[1]);
						money = getMoney(sender);
						if (money < to) {
							return translateMessage("pay-value-error");
						}
						if (money < 0) {
							return translateMessage("pay-value-error-1");
						}
						if (money - to < Double.parseDouble(getConfig().get("pay-1-limit").toString())) {
							return translateMessage("pay-can-not-less-than-initiation", Math.round(Double.parseDouble(getConfig().get("pay-1-limit").toString())), getMoneyUnit1());
						}

						p = Server.getInstance().getPlayer(args[0]);
						if (p == null) {
							name = args[0];
						} else {
							p.sendMessage(TextFormat.GOLD + sender.getName() + "支付给了你 " + Math.round(Double.parseDouble(args[1])) + getMonetaryUnit1());
							name = p.getName();
						}

						if (Objects.equals(name, "")) {
							return translateMessage("invalid-name", commands.get("pay-1"));
						}


						setMoney(sender, money - to);
						setMoney(name, getMoney(name) + to);

						return translateMessage("pay-success", Math.round(Double.parseDouble(args[1])), getMoneyUnit1(), name);
					case "pay-2":
						if (!canUseMoney2) {
							return translateMessage("cannot-use-money-2");
						}

						if (sender == null) {
							return translateMessage("use-in-game");
						}

						if (args.length < 2) {
							return translateMessage("pay-format-error", commands.get("pay-2"));
						}

						to = Double.parseDouble(args[1]);
						money = getMoney(sender);
						if (money < to) {
							return translateMessage("pay-value-error");
						}
						if (money < 0) {
							return translateMessage("pay-value-error-2");
						}
						if (money - to < Double.parseDouble(getConfig().get("pay-2-limit").toString())) {
							return translateMessage("pay-can-not-less-than-initiation", Math.round(Double.parseDouble(getConfig().get("pay-2-limit").toString())), getMoneyUnit2());
						}

						p = Server.getInstance().getPlayer(args[0]);
						if (p == null) {
							name = args[0];
						} else {
							p.sendMessage(TextFormat.GOLD + sender.getName() + "支付给了你 " + Math.round(Double.parseDouble(args[1])) + getMonetaryUnit1());
							name = p.getName();
						}

						if (Objects.equals(name, "")) {
							return translateMessage("invalid-name", commands.get("pay-2"));
						}

						setMoney(sender, money - to);
						setMoney(name, getMoney(name) + to);

						return translateMessage("pay-success", Math.round(Double.parseDouble(args[1])), getMoneyUnit2(), name);
					case "give-1":
						if (!poster.isOp()) {
							return translateMessage("has-no-permission");
						}

						if (args.length < 2) {
							return translateMessage("give-format-error", commands.get("give-1"));
						}

						p = Server.getInstance().getPlayer(args[0]);
						if (p == null) {
							name = args[0];
						} else {
							p.sendMessage(translateMessage("give-done", poster.getName(), new Long(args[1]), getMoneyUnit1()));
							name = p.getName();
						}

						if (Objects.equals(name, "")) {
							return translateMessage("invalid-name", commands.get("give-1"));
						}

						setMoney(name, getMoney(name) + Double.parseDouble(args[1]));
						return translateMessage("give-success", new Long(args[1]), getMoneyUnit1(), name);
					case "give-2":
						if (!canUseMoney2) {
							return translateMessage("cannot-use-money-2");
						}

						if (!poster.isOp()) {
							return translateMessage("has-no-permission");
						}

						if (args.length < 2) {
							return translateMessage("give-format-error", commands.get("give-2"));
						}

						p = Server.getInstance().getPlayer(args[0]);
						if (p == null) {
							name = args[0];
						} else {
							p.sendMessage(translateMessage("give-done", poster.getName(), new Long(args[1]), getMoneyUnit2()));
							name = p.getName();
						}

						if (Objects.equals(name, "")) {
							return translateMessage("invalid-name", commands.get("give-2"));
						}

						setMoney(name, getMoney(name, true) + Double.parseDouble(args[1]), true);
						return translateMessage("give-success", new Long(args[1]), getMoneyUnit2(), name);
					case "set-1":
						if (!poster.isOp()) {
							return translateMessage("has-no-permission");
						}

						if (args.length < 2) {
							return translateMessage("set-format-error", commands.get("set-1"));
						}

						p = Server.getInstance().getPlayer(args[0]);
						if (p == null) {
							name = args[0];
						} else {
							name = p.getName();
						}

						if (Objects.equals(name, "")) {
							return translateMessage("invalid-name", commands.get("set-1"));
						}

						setMoney(name, Double.parseDouble(args[1]));
						return translateMessage("set-success", name, getMoneyUnit1(), new Long(args[1]));

					case "set-2":
						if (!canUseMoney2) {
							return translateMessage("cannot-use-money-2");
						}

						if (!poster.isOp()) {
							return translateMessage("has-no-permission");
						}

						if (args.length < 2) {
							return translateMessage("set-format-error", commands.get("set-2"));
						}

						p = Server.getInstance().getPlayer(args[0]);
						if (p == null) {
							name = args[0];
						} else {
							name = p.getName();
						}

						if (Objects.equals(name, "")) {
							return translateMessage("invalid-name", commands.get("set-2"));
						}

						setMoney(name, Double.parseDouble(args[1]), true);
						return translateMessage("set-success", name, getMoneyUnit2(), new Long(args[1]));

					case "super-set-1":
						if (!poster.isOp()) {
							return translateMessage("has-no-permission");
						}

						if (args.length < 1) {
							return translateMessage("super-set-format-error", commands.get("super-set-1"));
						}

						String[] finalArgs = args;
						data.getData().replaceAll((key, value) -> {
							value.put("money1", finalArgs[0]);
							return value;
						});

						return translateMessage("super-set-success", getMoneyUnit1(), new Integer(args[0]));

					case "super-set-2":
						if (!poster.isOp()) {
							return translateMessage("has-no-permission");
						}

						if (args.length < 1) {
							return translateMessage("super-set-format-error", commands.get("super-set-2"));
						}

						String[] finalArgs1 = args;
						data.getData().replaceAll((key, value) -> {
							value.put("money2", finalArgs1[0]);
							return value;
						});

						return translateMessage("super-set-success", getMoneyUnit2(), new Integer(args[0]));

					case "list-1":
					case "list-2":
						/* sort */
						HashMap<String, String> map = new HashMap<>();
						data.getData().forEach((key, value) -> {
							IPlayer p1 = Server.getInstance().getOfflinePlayer(key);
							if (p1 == null) {
								return;
							}

							if (p1.isOp()) {
								return;
							}

							map.put(key, value.get("money" + (entry.getKey().equals("list-1") ? "1" : "2")));
						});

						ArrayList<String> list = new ArrayList<>(map.values());

						list.sort((a, b) -> new Double(b).compareTo(new Double(a)));
						LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
						ArrayList<String> arrayList = new ArrayList<>();
						list.forEach((value) -> {
							final String[] key = {null};

							map.forEach((k, v) -> {
								if (key[0] != null) {
									return;
								}
								if (value == null) {
									return;
								}
								if (v.equals(value) && !arrayList.contains(k)) {
									key[0] = k;
									arrayList.add(k);
								}
							});

							linkedHashMap.put(key[0], value);
						});

						/* **** */
						int pages = linkedHashMap.size() / 6;


						if (args.length > 0) {
							if (new Integer(args[0]) < 0) {
								args[0] = "0";
							} else if (new Integer(args[0]) - 1 > pages) {
								args[0] = String.valueOf(pages - 1);
							}
						} else {
							args = new String[]{"1"};
						}

						int i;
						StringBuilder msg = new StringBuilder(translateMessage("list", (entry.getKey().equals("list-1") ? getMonetaryUnit1() : getMonetaryUnit2()), new Integer(args[0]), (pages + 1)) + "\n");
						for (i = 6 * (new Integer(args[0]) - 1); i < 6 * new Integer(args[0]); i++) {
							String value = getKeyByNumber(i, linkedHashMap);
							String key = getValueByNumber(i, linkedHashMap);
							if (key != null && value != null && !key.equals("") && !value.equals("")) {
								msg.append(TextFormat.YELLOW).append("No.").append(i + 1).append(" ").append(TextFormat.GOLD).append(value).append(TextFormat.AQUA).append("  ").append(key).append(" \n");
							}
						}

						return msg.toString();
					case "give-online-1":
					case "give-online-2":
						if (!poster.isOp()) {
							return "";
						}

						if (args.length < 1) {
							return translateMessage("give-online-format-error", commands.get(entry.getKey()));
						}

						final int amount = new Integer(args[0]);
						Server.getInstance().getOnlinePlayers().forEach((uuid, player) -> {
							addMoney(player, amount, entry.getKey().equals("give-online-2"));
							player.sendMessage(translateMessage("give-done", poster.getName(), amount, entry.getKey().equals("give-online-2") ? getMonetaryUnit2() : getMonetaryUnit1()));
						});

						return translateMessage("give-online-done");
				}
			}
		}

		return "";
	}

	@SuppressWarnings("unchecked")
	private static <T> T getKeyByNumber(int number, Map<T, ?> map) {
		int i = 0;
		for (Map.Entry<T, ?> entry : map.entrySet()) {
			if (i++ == number) {
				return entry.getKey();
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private static <T> T getValueByNumber(int number, Map<?, T> map) {
		int i = 0;
		for (Map.Entry entry : map.entrySet()) {
			if (i++ == number) {
				return (T) entry.getValue();
			}
		}

		return null;
	}

	/**
	 * 判断 needle 是否存在于数组 arr 的值中.
	 *
	 * @param needle Object
	 * @param arr    Object
	 *
	 * @return boolean
	 */
	private static <T> boolean in_array(T needle, T[] arr) {
		for (T value : arr) {
			if (Objects.equals(value, needle)) {
				return true;
			}
		}

		return false;
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
	public String getMonetaryUnit(boolean unit) {
		return unit ? getMonetaryUnit2() : getMonetaryUnit1();
	}


	@Override
	public boolean isMoneyUnit2Enabled() {
		try {
			return Boolean.getBoolean(getConfig().get("enable-unit-2").toString());
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
	public void setAllMoney(final double amount){
		setAllMoney(amount, CurrencyType.FIRST);
	}

	@Override
	public void setAllMoney(final double amount, CurrencyType type){
		final String k = type.booleanValue() ? "money1" : "money2";
		final String v = String.valueOf(amount);
		data.getData().replaceAll((key, value) -> {
			value.put(k, v);
			return value;
		});
	}
}
