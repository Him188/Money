package money;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


class YAMLDatabase extends Database {
	protected static Map<String, Map<String, String>> data;
	private Money owner;
	private String file;

	private Map<String, String> defaultValue = new HashMap<>();

	public YAMLDatabase(Money owner2) {
		owner = owner2;

		defaultValue.put("money1", owner.config.getOrDefault("initial-money-1", "").toString());
		defaultValue.put("money2", owner.config.getOrDefault("initial-money-2", "").toString());
		defaultValue.put("bank", owner.config.getOrDefault("initial-bank-money", "").toString());
	}

	public void initPlayer(String player) {
		data.putIfAbsent(player, defaultValue);

		Map<String, String> m = data.getOrDefault(player, new HashMap<>());
		m.putIfAbsent("money1", owner.config.getOrDefault("initial-money-1", "").toString());
		m.putIfAbsent("money2", owner.config.getOrDefault("initial-money-2", "").toString());
		m.putIfAbsent("bank", owner.config.getOrDefault("initial-bank-money", "").toString());
		data.putIfAbsent(player, m);
	}

	public String get(String player, String key) {
		return data.getOrDefault(player, new HashMap<>()).getOrDefault(key, "0");
	}

	public String get(Player player, String key) {
		return get(player.getName(), key);
	}

	public void set(String player, String key, String value) {
		data.getOrDefault(player, new HashMap<>()).put(key, value);
	}

	public void set(Player player, String key, String value) {
		set(player.getName(), key, value);
	}

	public boolean loadFile(String file) {
		this.file = file;
		Map<String, Object> config = (new Config(file, Config.YAML)).getAll();
		data = new HashMap<>();
		config.entrySet().stream().filter(entry -> entry.getValue() != null).forEach(entry -> {
			if (entry.getValue() instanceof HashMap) {
				try {
					//noinspection unchecked
					data.put(entry.getKey(), (HashMap<String, String>) entry.getValue());
				} catch (Exception e) {
					owner.getLogger().warning(owner.translateMessage("load-Database-exception", e.getMessage(), e.getCause().getMessage()));
				}
			} else {
				config.remove(entry.getKey());
			}

		});

		return true;
	}

	public void save() {
		LinkedHashMap<String, Object> conf = new LinkedHashMap<>();
		conf.putAll(data);
		Config c = new Config(file, Config.YAML);
		c.setAll(conf);
		c.save();
	}

	public Map<String, Map<String, String>> getData() {
		return data;
	}
}
