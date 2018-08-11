package money.command;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import money.CurrencyType;
import money.Money;

import java.util.HashMap;

/**
 * @author Him188 @ Money Project
 */
public class WalletInfo1Command extends MoneyCommand {
	public WalletInfo1Command(String name, Money owner, String[] aliases) {
		super(name, owner, aliases);
		this.setPermission("money.command.walletinfo1");
		this.setCommandParameters(new HashMap<String, CommandParameter[]>() {
			{
				put("wallet-info-1", new CommandParameter[]{

				});
			}
		});
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
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
