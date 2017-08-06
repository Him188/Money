package money.command;

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
public class SuperSet1Command extends MoneyCommand {
	public SuperSet1Command(String name, Money owner, String[] aliases) {
		super(name, owner, aliases);
		this.setPermission("money.command.superset1");
		this.setCommandParameters(new HashMap<String, CommandParameter[]>() {
			{
				put("super-set-1", new CommandParameter[]{
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

		if (args.length < 1) {
			sender.sendMessage(this.getPlugin().translateMessage("set-format-error", "cmd", this.getName()));
			return true;
		}

		float to;

		try {
			to = Float.parseFloat(args[0]);
		} catch (NumberFormatException e) {
			sender.sendMessage(this.getPlugin().translateMessage("number-format-error"));
			return true;
		}
		int count = getPlugin().setAllMoney(to, CurrencyType.FIRST);

		sender.sendMessage(getPlugin().translateMessage("super-set-success",
				"count", count,
				"type", getPlugin().getCurrency1(),
				"amount", Float.parseFloat(args[0])));
		return true;
	}
}
