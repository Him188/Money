package money.command;

import cn.nukkit.Player;
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
	public GiveOnline1Command(String name, Money owner, String[] aliases, Map<String, CommandParameter[]> commandParameters) {
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

		if (args.length < 1) {
			sender.sendMessage(this.getPlugin().translateMessage("give-online-format-error", this.getName()));
			return true;
		}

		double to;

		try {
			to = Double.parseDouble(args[0]);
		} catch (NumberFormatException e) {
			sender.sendMessage(this.getPlugin().translateMessage("number-format-error"));
			return true;
		}

		Server.getInstance().getOnlinePlayers().forEach((uuid, player) -> {
			getPlugin().addMoney(player, to, CurrencyType.FIRST);
			player.sendMessage(getPlugin().translateMessage("give-done", sender.getName(), to, getPlugin().getMoneyUnit1()));
		});

		getPlugin().setAllMoney(to, CurrencyType.FIRST);

		sender.sendMessage(getPlugin().translateMessage("super-set-success", getPlugin().getMoneyUnit1(), to));
		return true;
	}
}
