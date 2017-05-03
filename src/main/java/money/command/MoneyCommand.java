package money.command;

import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParameter;
import money.Money;

import java.util.Map;

/**
 * @author Him188 @ Money Project
 * @since Money 2.0.0
 */
public abstract class MoneyCommand extends PluginCommand<Money> implements CommandExecutor {
	public MoneyCommand(String name, Money owner, String[] aliases, Map<String, CommandParameter[]> commandParameters) {
		super(name, owner);
		this.setExecutor(this);
		this.setAliases(aliases);
		this.setCommandParameters(commandParameters);
		this.setPermission("money.command");
	}
}
