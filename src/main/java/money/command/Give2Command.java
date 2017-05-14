package money.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import money.CurrencyType;
import money.Money;
import money.Utils;

import java.util.Map;
import java.util.Objects;

/**
 * @author Him188 @ Money Project
 * @since Money 2.0.0
 */
public class Give2Command extends MoneyCommand {
	public Give2Command(String name, Money owner, String[] aliases, Map<String, CommandParameter[]> commandParameters) {
		super(name, owner, aliases, commandParameters);
		this.setPermission("money.command.give2");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!this.testPermission(sender)) {
			//sender.sendMessage(this.getPlugin().translateMessage("has-no-permission"));
			return true;
		}


		if (args.length < 2) {
			sender.sendMessage(this.getPlugin().translateMessage("give-format-error", "cmd", this.getName()));
			return true;
		}

		float to = Float.parseFloat(args[1]);

		Player p = Utils.getPlayer(args[0]);
		String name;
		if (p == null) {
			name = args[0];
		} else {
			name = p.getName();
		}

		if (Objects.equals(name, "")) {
			sender.sendMessage(getPlugin().translateMessage("invalid-name", "cmd", this.getName()));
			return true;
		}

		if (!getPlugin().addMoney(name, to, CurrencyType.SECOND)) {
			sender.sendMessage(getPlugin().translateMessage("give-failed",
					"amount", Float.parseFloat(args[1]),
					"type", getPlugin().getCurrency2(),
					"name", name));
			return true;
		}

		if (p != null) {
			p.sendMessage(getPlugin().translateMessage("give-done",
					"name", sender.getName(),
					"amount", Long.parseLong(args[1]),
					"type", getPlugin().getCurrency2()));
		}
		sender.sendMessage(getPlugin().translateMessage("give-success",
				"amount", Float.parseFloat(args[1]),
				"type", getPlugin().getCurrency2(),
				"name", name));
		return true;
	}
}
