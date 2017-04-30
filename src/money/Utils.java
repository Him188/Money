package money;

import cn.nukkit.Player;
import cn.nukkit.Server;

/**
 * @author Him188 @ Money Project
 * @since Money 1.0.0
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
}
