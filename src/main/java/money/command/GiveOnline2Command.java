package money.command;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import money.CurrencyType;
import money.Money;

import java.util.HashMap;

/**
 * @author Him188 @ Money Project
 */
public class GiveOnline2Command extends MoneyCommand {
    public GiveOnline2Command(String name, Money owner, String[] aliases) {
        super(name, owner, aliases);
        this.setPermission("money.command.giveonline2");
        this.setCommandParameters(new HashMap<String, CommandParameter[]>() {
            {
                put("give-online-2", new CommandParameter[]{
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

        if (args.length < 1) {
            sender.sendMessage(this.getPlugin().translateMessage("give-online-format-error", "cmd", this.getName()));
            return true;
        }

        float to;

        try {
            to = Float.parseFloat(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(this.getPlugin().translateMessage("number-format-error"));
            return true;
        }

        final String message = getPlugin().translateMessage("give-online-for-you",
                "name", sender.getName(),
                "amount", to,
                "type", getPlugin().getCurrency2());
        Server.getInstance().getOnlinePlayers().forEach((uuid, player) -> {
            getPlugin().addMoney(player, to, CurrencyType.SECOND);
            player.sendMessage(message);
        });

        int count = getPlugin().addAllMoney(to, CurrencyType.SECOND);

        sender.sendMessage(getPlugin().translateMessage("give-online-done",
                "count", count,
                "type", getPlugin().getCurrency2(),
                "amount", to));
        return true;
    }
}
