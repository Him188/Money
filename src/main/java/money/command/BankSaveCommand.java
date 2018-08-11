package money.command;

import cn.nukkit.Player;
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
                        new CommandParameter("amount", CommandParamType.INT, false)
                });
            }
        });
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!this.testPermissionSilent(sender)) {
            sender.sendMessage(this.getPlugin().translateMessage("has-no-permission"));
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.getPlugin().translateMessage("use-in-game"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(getPlugin().translateMessage("bank-save-format-error", "cmd", this.getName()));
            return true;
        }

        float to = Float.parseFloat(args[0]);
        float money = getPlugin().getMoney((Player) sender);
        if (money < to) {
            sender.sendMessage(getPlugin().translateMessage("bank-save-value-error"));
            return true;
        }

        if (!getPlugin().reduceMoney((Player) sender, to)) {
            sender.sendMessage(getPlugin().translateMessage("bank-save-failed",
                    "amount", (Float.parseFloat(args[0])),
                    "type", getPlugin().getCurrency1())
            );
            return true;
        }

        if (!getPlugin().addBank((Player) sender, to)) {
            sender.sendMessage(getPlugin().translateMessage("bank-save-failed",
                    "amount", (Float.parseFloat(args[0])),
                    "type", getPlugin().getCurrency1())
            );
            getPlugin().addMoney((Player) sender, to);

            return true;
        }
        sender.sendMessage(getPlugin().translateMessage("bank-save-success",
                "amount", (Float.parseFloat(args[0])),
                "type", getPlugin().getCurrency1()));
        return true;
    }
}
