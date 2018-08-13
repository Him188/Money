package money.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import money.Money;

import java.util.HashMap;

/**
 * @author Him188 @ Money Project
 */
public class BankTakeCommand extends MoneyCommand {
    public BankTakeCommand(String name, Money owner, String[] aliases) {
        super(name, owner, aliases);
        this.setPermission("money.command.banktake");
        this.setCommandParameters(new HashMap<String, CommandParameter[]>() {
            {
                put("bank-take", new CommandParameter[]{
                        new CommandParameter("amount", CommandParamType.FLOAT, false)
                });
            }
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.getPlugin().translateMessage("use-in-game"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(getPlugin().translateMessage("bank-take-format-error", "cmd", this.getName()));
            return true;
        }

        float amount = Float.parseFloat(args[0]);
        float bank = getPlugin().getBank((Player) sender);
        if (bank < amount) {
            getPlugin().translateMessage("bank-take-value-error");
            return true;
        }

        if (!getPlugin().reduceBank((Player) sender, amount)) {
            sender.sendMessage(getPlugin().translateMessage("bank-take-failed",
                    "amount", amount,
                    "type", getPlugin().getCurrency1())
            );
            return true;
        }

        if (!getPlugin().addMoney((Player) sender, amount)) {
            sender.sendMessage(getPlugin().translateMessage("bank-take-failed",
                    "amount", amount,
                    "type", getPlugin().getCurrency1())
            );
            return true;
        }

        sender.sendMessage(getPlugin().translateMessage("bank-take-success",
                "amount", amount,
                "type", getPlugin().getCurrency1()));
        return true;

    }
}
