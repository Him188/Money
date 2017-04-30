package money.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import money.Money;
import money.Utils;

import java.util.Map;
import java.util.Objects;

/**
 * @author Him188 @ Money Project
 * @since Money 1.0.0
 */
public class Set1Command extends MoneyCommand {
	public Set1Command(String name, Money owner, String[] aliases, Map<String, CommandParameter[]> commandParameters) {
		super(name, owner, aliases, commandParameters);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!this.testPermission(sender)) {
			sender.sendMessage(this.getPlugin().translateMessage("has-no-permission"));
			return true;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage(this.getPlugin().translateMessage("use-in-game"));
			return true;
		}

		if (args.length < 2) {
			sender.sendMessage(this.getPlugin().translateMessage("set-format-error", this.getName()));
			return true;
		}

		double to = Double.parseDouble(args[1]);

		Player p = Utils.getPlayer(args[0]);
		String name;
		if (p == null) {
			name = args[0];
		} else {
			p.sendMessage(getPlugin().translateMessage("give-done", sender.getName(), new Long(args[1]), getPlugin().getMoneyUnit1()));
			name = p.getName();
		}

		if (Objects.equals(name, "")) {
			sender.sendMessage(getPlugin().translateMessage("invalid-name", this.getName()));
			return true;
		}
		getPlugin().setMoney(name, to);

		sender.sendMessage(getPlugin().translateMessage("set-success", new Long(args[1]), getPlugin().getMoneyUnit1(), name));
		return true;
	}
}
