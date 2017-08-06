package money.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import money.Money;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Him188 @ Money Project
 */
public class BankTakeCommand extends MoneyCommand {
	public BankTakeCommand(String name, Money owner, String[] aliases,
	                       Map<String, CommandParameter[]> commandParameters) {
		super(name, owner, aliases, commandParameters);
		this.setPermission("money.command.banktake");
		this.setCommandParameters(new HashMap<String, CommandParameter[]>() {
			{
				put("bank-take", new CommandParameter[]{
						new CommandParameter("amount", CommandParameter.ARG_TYPE_INT, false)
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

		if (args.length < 1) {
			sender.sendMessage(getPlugin().translateMessage("bank-take-format-error", "cmd", this.getName()));
			return true;
		}

		float to = Float.parseFloat(args[0]);
		Float money = getPlugin().getBank((Player) sender);
		if (money < to) {
			getPlugin().translateMessage("bank-take-value-error");
			return true;
		}

		if (!getPlugin().reduceBank((Player) sender, to)) {
			sender.sendMessage(getPlugin().translateMessage("bank-take-failed",
					"amount", (Float.parseFloat(args[0])),
					"type", getPlugin().getCurrency1())
			);
			return true;
		}

		if (!getPlugin().addMoney((Player) sender, to)) {
			sender.sendMessage(getPlugin().translateMessage("bank-take-failed",
					"amount", (Float.parseFloat(args[0])),
					"type", getPlugin().getCurrency1())
			);
			getPlugin().reduceBank((Player) sender, to);
			return true;
		}

		sender.sendMessage(getPlugin().translateMessage("bank-take-success",
				"amount", (Float.parseFloat(args[0])),
				"type", getPlugin().getCurrency1()));
		return true;


	}
}
