package money.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import money.Money;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author Him188 @ Money Project
 */
public class SeeMoney1Command extends MoneyCommand {
    public SeeMoney1Command(String name, Money owner, String[] aliases) {
        super(name, owner, aliases);
        this.setPermission("money.command.see1");
        this.setCommandParameters(new HashMap<String, CommandParameter[]>() {
            {
                put("see-money-1", new CommandParameter[]{
                        new CommandParameter("player", CommandParamType.RAWTEXT, false)
                });
            }
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(this.getPlugin().translateMessage("see-money-format-error", "cmd", this.getName()));
            return true;
        }

        Player p = Server.getInstance().getPlayer(args[0]);
        String name;
        if (p == null) {
            name = args[0];
        } else {
            name = p.getName();
        }

        if (Objects.equals(name, "")) {
            sender.sendMessage(this.getPlugin().translateMessage("see-money-format-error", "cmd", this.getName()));
            return true;
        }

        sender.sendMessage(this.getPlugin().translateMessage("see-money-success",
                "player", name,
                "amount", Money.getInstance().getMoney(name),
                "type", Money.getInstance().getCurrency1()
        ));
        return false;
    }
}
