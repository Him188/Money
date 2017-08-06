package money.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import money.Money;
import money.Utils;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author Him188 @ Money Project
 */
public class SeeBankCommand extends MoneyCommand {
    public SeeBankCommand(String name, Money owner, String[] aliases) {
        super(name, owner, aliases);
        this.setPermission("money.command.seebank");
        this.setCommandParameters(new HashMap<String, CommandParameter[]>() {
            {
                put("see-bank", new CommandParameter[]{
                        new CommandParameter("player", CommandParameter.ARG_TYPE_RAW_TEXT, false)
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
            sender.sendMessage(this.getPlugin().translateMessage("see-bank-format-error", "cmd", this.getName()));
            return true;
        }

        Player p = Utils.getPlayer(args[0]);
        String name;
        if (p == null) {
            name = args[0];
        } else {
            name = p.getName();
        }

        if (Objects.equals(name, "")) {
            sender.sendMessage(this.getPlugin().translateMessage("see-bank-format-error", "cmd", this.getName()));
            return true;
        }

        sender.sendMessage(this.getPlugin().translateMessage("see-bank-success",
                "player", name,
                "amount", Money.getInstance().getBank(name)
        ));
        return false;
    }
}
