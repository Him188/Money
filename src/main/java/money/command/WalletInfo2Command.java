package money.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import money.CurrencyType;
import money.Money;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Him188 @ Money Project
 */
public class WalletInfo2Command extends MoneyCommand {
	public WalletInfo2Command(String name, Money owner, String[] aliases) {
		super(name, owner, aliases);
		this.setPermission("money.command.walletinfo2");
		this.setCommandParameters(new HashMap<String, CommandParameter[]>() {
			{
				put("wallet-info-2", new CommandParameter[]{

				});
			}
		});
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

		sender.sendMessage(this.getPlugin().translateMessage("your-money-2",
				"amount", getPlugin().getMoney((Player) sender, CurrencyType.SECOND),
				"type", getPlugin().getCurrency2()));
		return true;
	}
}
