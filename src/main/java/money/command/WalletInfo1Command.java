package money.command;

import cn.nukkit.Player;
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
public class WalletInfo1Command extends MoneyCommand {
	public WalletInfo1Command(String name, Money owner, String[] aliases,
	                          Map<String, CommandParameter[]> commandParameters) {
		super(name, owner, aliases, commandParameters);
		this.setPermission("money.command.walletinfo1");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!this.testPermission(sender)) {
			//sender.sendMessage(this.getPlugin().translateMessage("has-no-permission"));
			return true;
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage(this.getPlugin().translateMessage("use-in-game"));
			return true;
		}

		sender.sendMessage(this.getPlugin().translateMessage("your-money-1",
				"amount", getPlugin().getMoney((Player) sender, CurrencyType.FIRST),
				"type", getPlugin().getCurrency1()));
		return true;
	}
}
