package money.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import money.Money;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author Him188 @ Money Project
 */
public class Set1Command extends MoneyCommand {
    public Set1Command(String name, Money owner, String[] aliases) {
        super(name, owner, aliases);
        this.setPermission("money.command.set1");
        this.setCommandParameters(new HashMap<String, CommandParameter[]>() {
            {
                put("set-1", new CommandParameter[]{
                        new CommandParameter("player", CommandParamType.STRING, false),
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

        if (args.length < 2) {
            sender.sendMessage(this.getPlugin().translateMessage("set-format-error", "cmd", this.getName()));
            return true;
        }

        float to = Float.parseFloat(args[1]);

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
        if (!getPlugin().setMoney(name, to)) {
            sender.sendMessage(getPlugin().translateMessage("set-failed",
                    "amount", Float.parseFloat(args[1]),
                    "type", getPlugin().getCurrency1(),
                    "name", name)
            );
            return true;
        }

        if (p != null) {
            p.sendMessage(getPlugin().translateMessage("set-done",
                    "name", sender.getName(),
                    "amount", Float.parseFloat(args[1]),
                    "type", getPlugin().getCurrency1())
            );
        }

        sender.sendMessage(getPlugin().translateMessage("set-success",
                "amount", Float.parseFloat(args[1]),
                "type", getPlugin().getCurrency1(),
                "name", name));
        return true;
    }
}
