package money.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import money.Money;

import java.util.HashMap;

/**
 * @author Him188 @ Money Project
 */
public class Pay1Command extends MoneyCommand {
    public Pay1Command(String name, Money owner, String[] aliases) {
        super(name, owner, aliases);
        this.setPermission("money.command.pay1");
        this.setCommandParameters(new HashMap<String, CommandParameter[]>() {
            {
                put("pay-1", new CommandParameter[]{
                        new CommandParameter("player", CommandParamType.STRING, false),
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

        if (args.length < 2) {
            sender.sendMessage(this.getPlugin().translateMessage("pay-format-error", "cmd", this.getName()));
            return true;
        }

        float amount = Float.parseFloat(args[1]);
        float money = getPlugin().getMoney((Player) sender);
        if (money < amount) {
            sender.sendMessage(this.getPlugin().translateMessage("pay-value-error"));
            return true;
        }
        if (amount < 0) {
            sender.sendMessage(this.getPlugin().translateMessage("pay-value-error-2"));
            return true;
        }
        if (money - amount < Float.parseFloat(getPlugin().getConfig().get("pay-1-limit").toString())) {
            sender.sendMessage(this.getPlugin().translateMessage("pay-can-not-less-than-initiation",
                    "amount", (Float.parseFloat(getPlugin().getConfig().get("pay-1-limit").toString())),
                    "type", getPlugin().getCurrency1()));
            return true;
        }

        Player toPlayer = Server.getInstance().getPlayer(args[0]);
        String toName;
        if (toPlayer == null) {
            toName = args[0];
        } else {
            toName = toPlayer.getName();
        }

        if (toName.isEmpty()) {
            sender.sendMessage(this.getPlugin().translateMessage("invalid-name", "cmd", this.getName()));
            return true;
        }


        if (!getPlugin().reduceMoney((Player) sender, amount)) {
            sender.sendMessage(this.getPlugin().translateMessage("pay-failed",
                    "amount", amount,
                    "type", getPlugin().getCurrency1(),
                    "name", toName));
            return true;
        }

        if (!getPlugin().addMoney(toName, amount)) {
            sender.sendMessage(this.getPlugin().translateMessage("pay-failed",
                    "amount", amount,
                    "type", getPlugin().getCurrency1(),
                    "name", toName));
            return true;
        }

        if (toPlayer != null) {
            toPlayer.sendMessage(this.getPlugin().translateMessage("pay-for-you",
                    "name", amount,
                    "type", getPlugin().getCurrency1()));
        }

        sender.sendMessage(this.getPlugin().translateMessage("pay-success",
                "amount", amount,
                "type", getPlugin().getCurrency1(),
                "name", toName));
        return true;
    }
}
