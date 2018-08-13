package money.command;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.scheduler.AsyncTask;
import money.CurrencyType;
import money.Money;

import java.util.HashMap;

/**
 * @author Him188 @ Money Project
 */
public class SuperSet2Command extends MoneyCommand {
    public SuperSet2Command(String name, Money owner, String[] aliases) {
        super(name, owner, aliases);
        this.setPermission("money.command.superset2");
        this.setCommandParameters(new HashMap<String, CommandParameter[]>() {
            {
                put("super-set-2", new CommandParameter[]{
                        new CommandParameter("amount", CommandParamType.FLOAT, false)
                });
            }
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(this.getPlugin().translateMessage("set-format-error", "cmd", this.getName()));
            return true;
        }

        float amount;

        try {
            amount = Float.parseFloat(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(this.getPlugin().translateMessage("number-format-error"));
            return true;
        }

        sender.sendMessage(getPlugin().translateMessage("give-online-wait"));

        Server.getInstance().getScheduler().scheduleAsyncTask(this.getPlugin(), new AsyncTask() {
            @Override
            public void onRun() {
                Server.getInstance().getOnlinePlayers().forEach((uuid, player) -> {
                    getPlugin().setMoney(player, amount, CurrencyType.SECOND);
                });

                sender.sendMessage(getPlugin().translateMessage("super-set-success",
                        "type", getPlugin().getCurrency2(),
                        "amount", amount));
            }
        });
        return true;
    }
}
