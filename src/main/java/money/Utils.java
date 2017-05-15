package money;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.Server;

import java.util.*;

/**
 * @author Him188 @ Money Project
 * @since Money 2.0.0
 */
public final class Utils {
    public static Player getPlayer(String name) {
        Player player;

        player = Server.getInstance().getPlayerExact(name);
        if (player != null) {
            return player;
        }

        return Server.getInstance().getPlayer(name);
    }

    public static LinkedHashMap<String, String> sortMap(LinkedHashMap<String, LinkedHashMap<String, String>> data, String key) {
        HashMap<String, String> map = new HashMap<>();
        data.forEach((k, value) -> {
            if (k == null) {
                return;
            }

            IPlayer p1 = Server.getInstance().getOfflinePlayer(k);
            if (p1 == null) {
                return;
            }

            if (p1.isOp()) {
                return;
            }

            String v = value.get(k);
            if (v == null) {
                return;
            }
            map.put(k, v);
        });

        ArrayList<String> list = new ArrayList<>(map.values());

        list.sort((a, b) -> new Float(b).compareTo(Float.parseFloat(a)));
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        HashSet<String> set = new HashSet<>();
        list.forEach((value) -> {
            final String[] key2 = {null};

            map.forEach((k, v) -> {
                if (key2[0] != null) {
                    return;
                }
                if (value == null) {
                    return;
                }
                if (v.equals(value) && !set.contains(k)) {
                    key2[0] = k;
                    set.add(k);
                }
            });

            linkedHashMap.put(key2[0], value);
        });
        return linkedHashMap;
    }


    @SuppressWarnings("unchecked")
    public static <T> T getKeyByNumber(int number, Map<T, ?> map) {
        int i = 0;
        for (Map.Entry<T, ?> entry : map.entrySet()) {
            if (i++ == number) {
                return entry.getKey();
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValueByNumber(int number, Map<?, T> map) {
        int i = 0;
        for (Map.Entry entry : map.entrySet()) {
            if (i++ == number) {
                return (T) entry.getValue();
            }
        }

        return null;
    }
}
