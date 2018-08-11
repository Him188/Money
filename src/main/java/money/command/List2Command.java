package money.command;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.TextFormat;
import money.Money;
import money.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Him188 @ Money Project
 */
public class List2Command extends MoneyCommand {
    public List2Command(String name, Money owner, String[] aliases) {
        super(name, owner, aliases);
        this.setPermission("money.command.list2");
        this.setCommandParameters(new HashMap<String, CommandParameter[]>() {
            {
                put("list-1", new CommandParameter[]{
                        new CommandParameter("page", CommandParamType.INT, true)
                });
            }
        });
    }

    @Override
    public boolean execute(final CommandSender sender, String label, final String[] args) {
        if (!this.testPermissionSilent(sender)) {
            sender.sendMessage(this.getPlugin().translateMessage("has-no-permission"));
            return true;
        }

        Server.getInstance().getScheduler().scheduleAsyncTask(getPlugin(), new AsyncTask() {
            @SuppressWarnings("Duplicates") // TODO: 2017/5/3  analyze
            @Override
            public void onRun() {
                Map<String, String> linkedHashMap = Utils.sortMap(getPlugin().getDataMap(), "money2");

                int pages = linkedHashMap.size() / 6;


                int page = 1;
                if (args.length > 0) {
                    try {
                        if (Integer.parseInt(args[0]) < 0) {
                            page = 1;
                        } else if (Integer.parseInt(args[0]) - 1 > pages) {
                            page = pages - 1;
                        } else {
                            page = Integer.parseInt(args[0]);
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }

                int i;
                StringBuilder msg = new StringBuilder(
                        getPlugin().translateMessage("list",
                                "type", getPlugin().getCurrency2(),
                                "page", page,
                                "all", (pages + 1)
                        ) + "\n");
                for (i = 6 * (page - 1); i < 6 * page; i++) {
                    String value = Utils.getKeyByNumber(i, linkedHashMap);
                    String key = Utils.getValueByNumber(i, linkedHashMap);
                    if (key != null && value != null && !key.equals("") && !value.equals("")) {
                        msg.append(TextFormat.YELLOW).append("No.").append(i + 1).append(" ").append(TextFormat.GOLD)
                                .append(value).append(TextFormat.AQUA).append("  ").append(key).append(" \n");
                    }
                }

                sender.sendMessage(msg.toString());
            }
        });

        sender.sendMessage(this.getPlugin().translateMessage("list-listing"));
        return true;
    }
}
