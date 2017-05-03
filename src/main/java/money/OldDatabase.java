package money;

import cn.nukkit.utils.Config;

import java.util.HashMap;
import java.util.Map;

/**
 * 该类仅用于旧数据转换为新数据. 新数据使用到 {@code MoeDB} 的 {@link net.mamoe.moedb.db.KeyValueDatabase}
 */
class OldDatabase {
	private Map<String, Map<String, String>> data;
	private Money owner;

	public OldDatabase(Money owner2) {
		owner = owner2;
	}

	public boolean loadFile(String file) {
		Map<String, Object> config = (new Config(file, Config.YAML)).getAll();
		data = new HashMap<>();
		config.entrySet().stream().filter(entry -> entry.getValue() != null).forEach(entry -> {
			if (entry.getValue() instanceof HashMap) {
				try {
					//noinspection unchecked
					data.put(entry.getKey(), (HashMap<String, String>) entry.getValue());
				} catch (Exception e) {
					e.printStackTrace();
					owner.getLogger().warning(owner.translateMessage("load-Database-exception", e.getMessage(), e.getCause().getMessage()));
				}
			} else {
				config.remove(entry.getKey());
			}

		});

		return true;
	}

	public Map<String, Map<String, String>> getData() {
		return data;
	}
}
