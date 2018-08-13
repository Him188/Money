package money.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import money.CurrencyType;
import money.Money;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author Him188 @ Money Project
 */
public class Set2Command extends MoneyCommand {
    public Set2Command(String name, Money owner, String[] aliases) {
        super(name, owner, aliases);
        this.setPermission("money.command.set2");
        this.setCommandParameters(new HashMap<String, CommandParameter[]>() {
            {
                put("set-2", new CommandParameter[]{
                        new CommandParameter("player", CommandParamType.STRING, false),
                        new CommandParameter("amount", CommandParamType.FLOAT, false)
                });
            }
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(this.getPlugin().translateMessage("set-format-error", "cmd", this.getName()));
            return true;
        }

        float amount = Float.parseFloat(args[1]);

        Player p = Server.getInstance().getPlayer(args[0]);
        String name;
        if (p == null) {
            name = args[0];
        } else {
            name = p.getName();
        }

        if (Objects.equals(name, "")) {
            sender.sendMessage(getPlugin().translateMessage("invalid-name", "cmd", this.getName()));
            return true;
        }
        if (!getPlugin().setMoney(name, amount, CurrencyType.SECOND)) {
            sender.sendMessage(getPlugin().translateMessage("set-failed",
                    "amount", amount,
                    "type", getPlugin().getCurrency2(),
                    "name", name));
            return true;
        }

        if (p != null) {
            p.sendMessage(getPlugin().translateMessage("set-done",
                    "name", sender.getName(),
                    "amount", amount,
                    "type", getPlugin().getCurrency2()));
        }

        sender.sendMessage(getPlugin().translateMessage("set-success",
                "amount", amount,
                "type", getPlugin().getCurrency2(),
                "name", name));
        return true;
    }
}
