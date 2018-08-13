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
public class BankSaveCommand extends MoneyCommand {
    public BankSaveCommand(String name, Money owner, String[] aliases) {
        super(name, owner, aliases);
        this.setPermission("money.command.banksave");
        this.setCommandParameters(new HashMap<String, CommandParameter[]>() {
            {
                put("bank-save", new CommandParameter[]{
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
            sender.sendMessage(getPlugin().translateMessage("bank-save-format-error", "cmd", this.getName()));
            return true;
        }

        float amount = Float.parseFloat(args[0]);
        float money = getPlugin().getMoney((Player) sender);
        if (money < amount) {
            sender.sendMessage(getPlugin().translateMessage("bank-save-value-error"));
            return true;
        }

        if (!getPlugin().reduceMoney((Player) sender, amount)) {
            sender.sendMessage(getPlugin().translateMessage("bank-save-failed",
                    "amount", amount,
                    "type", getPlugin().getCurrency1())
            );
            return true;
        }

        if (!getPlugin().addBank((Player) sender, amount)) {
            sender.sendMessage(getPlugin().translateMessage("bank-save-failed",
                    "amount", amount,
                    "type", getPlugin().getCurrency1())
            );
            return true;
        }
        sender.sendMessage(getPlugin().translateMessage("bank-save-success",
                "amount", amount,
                "type", getPlugin().getCurrency1()));
        return true;
    }
}
