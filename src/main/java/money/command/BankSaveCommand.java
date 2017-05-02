package money.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import money.Money;

import java.util.Map;

/**
 * @author Him188 @ Money Project
 * @since Money 2.0.0
 */
public class BankSaveCommand extends MoneyCommand{
	public BankSaveCommand(String name, Money owner, String[] aliases, Map<String, CommandParameter[]> commandParameters) {
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
			sender.sendMessage(getPlugin().translateMessage("bank-save-format-error", this.getName()));
			return true;
		}

		double to = Double.parseDouble(args[0]);
		Double money = getPlugin().getMoney((Player) sender);
		if (money < to) {
			sender.sendMessage(getPlugin().translateMessage("bank-save-value-error"));
			return true;

		}
		getPlugin().setMoney((Player) sender, money - to);
		getPlugin().reduceMoney((Player) sender, to);


		sender.sendMessage(getPlugin().translateMessage("bank-save-success", Math.round(Double.parseDouble(args[0])), getPlugin().getMoneyUnit1()));
		return true;
	}
}
