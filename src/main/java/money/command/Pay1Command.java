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
public class Pay1Command extends MoneyCommand {
    public Pay1Command(String name, Money owner, String[] aliases) {
        super(name, owner, aliases);
        this.setPermission("money.command.pay1");
        this.setCommandParameters(new HashMap<String, CommandParameter[]>() {
            {
                put("bank-1", new CommandParameter[]{
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
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.getPlugin().translateMessage("use-in-game"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(this.getPlugin().translateMessage("pay-format-error", "cmd", this.getName()));
            return true;
        }

        float to = Float.parseFloat(args[1]);
        float money = getPlugin().getMoney((Player) sender);
        if (money < to) {
            sender.sendMessage(this.getPlugin().translateMessage("pay-value-error"));
            return true;
        }
        if (money < 0) {
            sender.sendMessage(this.getPlugin().translateMessage("pay-value-error-1"));
            return true;
        }
        if (money - to < Float.parseFloat(getPlugin().getConfig().get("pay-1-limit").toString())) {
            sender.sendMessage(this.getPlugin().translateMessage("pay-can-not-less-than-initiation",
                    "amount", (Float.parseFloat(getPlugin().getConfig().get("pay-1-limit").toString())),
                    "type", getPlugin().getCurrency1()));
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
            sender.sendMessage(this.getPlugin().translateMessage("invalid-name", "cmd", this.getName()));
            return true;
        }


        if (!getPlugin().reduceMoney((Player) sender, to)) {
            sender.sendMessage(this.getPlugin().translateMessage("pay-failed",
                    "amount", (Float.parseFloat(args[1])),
                    "type", getPlugin().getCurrency1(),
                    "name", name));
            return true;
        }

        if (!getPlugin().addMoney((Player) sender, to)) {
            sender.sendMessage(this.getPlugin().translateMessage("pay-failed",
                    "amount", (Float.parseFloat(args[1])),
                    "type", getPlugin().getCurrency1(),
                    "name", name));
            getPlugin().addMoney((Player) sender, to);
            return true;
        }

        if (p != null) {
            p.sendMessage(this.getPlugin().translateMessage("pay-for-you",
                    "name", (Float.parseFloat(args[1])),
                    "type", getPlugin().getCurrency1()));
        }

        sender.sendMessage(this.getPlugin().translateMessage("pay-success",
                "amount", (Float.parseFloat(args[1])),
                "type", getPlugin().getCurrency1(),
                "name", name));
        return true;
    }
}
