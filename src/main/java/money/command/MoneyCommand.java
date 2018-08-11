package money.command;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import money.Money;

/**
 * @author Him188 @ Money Project
 */
public abstract class MoneyCommand extends PluginCommand<Money> {
    public MoneyCommand(String name, Money owner, String[] aliases) {
        super(name, owner);
        this.setAliases(aliases);
        this.setPermission("money.command");
    }

    @Override
    public abstract boolean execute(CommandSender sender, String commandLabel, String[] args);
}
