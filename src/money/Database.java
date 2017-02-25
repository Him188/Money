package money;

import cn.nukkit.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @see YAMLDatabase
 */
public abstract class Database {
	protected static Map<String, Map<String, String>> data = new HashMap<>(); //YAML


	public void init() {  //MYSQL

	}

	public void loadPlayer(String player) {  //MYSQL

	}

	public void savePlayer(String player) {  //MYSQL

	}

	public boolean connect(String IP, String user, String password) {  //MYSQL
		return false;
	}


	public abstract void initPlayer(String player);

	public abstract String get(String player, String key);

	public abstract String get(Player player, String key);

	public abstract void set(String player, String key, String value);

	public abstract void set(Player player, String key, String value);


	public void save() {  //YAML

	}

	public boolean loadFile(String file) {  //YAML
		return false;
	}

	public Map<String, Map<String, String>> getData(){return data;}

/*
	@SuppressWarnings("unchecked")
	public static <K, V> V superMapPut(Map<K, V> map, K key, V value) {
		try {
			final K[] found = (K[]) java.lang.reflect.Array.newInstance(key.getClass().getComponentType(), 0);
			map.forEach((k, v) -> {
				if (found[0] != null) {
					return;
				}
				if (k.toString().toLowerCase().trim().equals(key.toString().toLowerCase().trim())) {
					found[0] = k;
				}
			});

			return found[0] == null ? null : map.put(found[0], value);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> V superMapPutIfAbsent(Map<K, V> map, K key, V value) {
		try {
			final K[] found = (K[]) java.lang.reflect.Array.newInstance(key.getClass().getComponentType(), 0);
			map.forEach((k, v) -> {
				if (found[0] != null) {
					return;
				}
				if (k.toString().toLowerCase().trim().equals(key.toString().toLowerCase().trim())) {
					found[0] = k;
				}
			});

			return found[0] == null ? map.put(key, value) : map.putIfAbsent(found[0], value);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> V superMapGet(Map<K, V> map, K key) {
		try {
			final K[] found = (K[]) java.lang.reflect.Array.newInstance(key.getClass().getComponentType(), 0);
			map.forEach((k, v) -> {
				if (found[0] != null) {
					return;
				}
				if (k.toString().toLowerCase().trim().equals(key.toString().toLowerCase().trim())) {
					found[0] = k;
				}
			});

			return found[0] == null ? null : map.get(found[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}*/
}
