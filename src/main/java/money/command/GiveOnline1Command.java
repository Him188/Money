package money.command;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import money.CurrencyType;
import money.Money;

import java.util.Map;

/**
 * @author Him188 @ Money Project
 * @since Money 2.0.0
 */
public class GiveOnline1Command extends MoneyCommand {
	public GiveOnline1Command(String name, Money owner, String[] aliases,
	                          Map<String, CommandParameter[]> commandParameters) {
		super(name, owner, aliases, commandParameters);
		this.setPermission("money.command.giveonline1");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!this.testPermission(sender)) {
			//sender.sendMessage(this.getPlugin().translateMessage("has-no-permission"));
			return true;
		}


		if (args.length < 1) {
			sender.sendMessage(this.getPlugin().translateMessage("give-online-format-error", "cmd", this.getName()));
			return true;
		}

		float to;

		try {
			to = Float.parseFloat(args[0]);
		} catch (NumberFormatException e) {
			sender.sendMessage(this.getPlugin().translateMessage("number-format-error"));
			return true;
		}

		Server.getInstance().getOnlinePlayers().forEach((uuid, player) -> {
			getPlugin().addMoney(player, to, CurrencyType.FIRST);
			player.sendMessage(getPlugin().translateMessage("give-online-for-you",
					"name", sender.getName(),
					"amount", to,
					"type", getPlugin().getCurrency1()));
		});

		int count = getPlugin().addAllMoney(to, CurrencyType.FIRST);
		sender.sendMessage(getPlugin().translateMessage("give-online-done",
				"count", count,
				"type", getPlugin().getCurrency1(),
				"amount", to));
		return true;
	}
}
