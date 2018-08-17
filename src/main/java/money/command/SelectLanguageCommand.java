package money.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import money.Money;
import money.utils.LanguageType;

import java.util.HashMap;

/**
 * @author Him188moe @ Money Project
 * @since 3.3
 */
public class SelectLanguageCommand extends MoneyCommand {
    public SelectLanguageCommand(String name, Money owner, String[] aliases) {
        super(name, owner, aliases);
        this.setPermission("money.command.selectlang");
        this.setUsage("/moneyselectlang <chs|eng|cht>");
        this.setCommandParameters(new HashMap<String, CommandParameter[]>() {
            {
                put("selectlanguage", new CommandParameter[]{
                        new CommandParameter("type", false, LanguageType.stringValues)
                });
            }
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            sender.sendMessage(this.getPermissionMessage());
            return false;
        }

        if (args.length == 0) {
            return false;
        }

        LanguageType type;
        try {
            type = LanguageType.fromString(args[0]);
        } catch (IllegalArgumentException e) {
            return false;
        }

        this.getPlugin().resetLanguage(type, true);
        sender.sendMessage("OK, you should restart server to apply changes");
        return true;
    }
}
