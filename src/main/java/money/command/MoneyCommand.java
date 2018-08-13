package money.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import money.Money;

/**
 * @author Him188 @ Money Project
 */
public abstract class MoneyCommand extends PluginCommand<Money> implements CommandExecutor {
    public MoneyCommand(String name, Money owner, String[] aliases) {
        super(name, owner);
        this.setAliases(aliases);
        this.setPermission("money.command");
        this.setPermissionMessage(this.getPlugin().translateMessage("has-no-permission"));
    }

    @Override
    public abstract boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args);
}
