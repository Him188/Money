package money;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;

import java.io.File;
import java.util.EnumMap;

/**
 * 该类仅用于旧数据转换为新数据. 新数据使用到 {@code MoeDB} 的 {@link net.mamoe.moedb.AbstractDatabase}
 */
class OldDatabase {
    enum Keys {
        STRINGS,
        MAPS,
        LISTS
    }

    private EnumMap<Keys, ConfigSection> data;

    OldDatabase() {
    }

    void loadFile(File file) {
        Config config = new Config(file, Config.YAML);
        data = new EnumMap<>(Keys.class);
        data.put(Keys.STRINGS, config.getSection("strings"));
        data.put(Keys.MAPS, config.getSection("maps"));
        data.put(Keys.LISTS, config.getSection("lists"));
    }

    EnumMap<Keys, ConfigSection> getData() {
        return data;
    }
}
