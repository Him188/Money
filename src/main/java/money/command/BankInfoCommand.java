package money.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import money.Money;

import java.util.HashMap;

/**
 * @author Him188 @ Money Project
 */
public class BankInfoCommand extends MoneyCommand {
    public BankInfoCommand(String name, Money owner, String[] aliases) {
        super(name, owner, aliases);
        this.setPermission("money.command.bankinfo");
        this.setCommandParameters(new HashMap<String, CommandParameter[]>() {
            {
                put("bank-info", new CommandParameter[]{
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
        sender.sendMessage(this.getPlugin().translateMessage("your-bank",
                "amount", getPlugin().getBank((Player) sender),
                "type", getPlugin().getCurrency1()
        ));
        return true;
    }
}
