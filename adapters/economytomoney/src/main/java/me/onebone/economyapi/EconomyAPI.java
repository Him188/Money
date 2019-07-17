package me.onebone.economyapi;

import cn.nukkit.Player;
import me.onebone.economyapi.event.account.CreateAccountEvent;
import money.CurrencyType;
import money.Money;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * @author Him188moe @ Money Project
 */
public class EconomyAPI {
    private static EconomyAPI instance = new EconomyAPI();

    public static EconomyAPI getInstance() {
        return instance;
    }


    public boolean createAccount(Player player) {
        return this.createAccount(player, -1, false);
    }

    public boolean createAccount(Player player, double defaultMoney) {
        return this.createAccount(player, defaultMoney, false);
    }

    public boolean createAccount(Player player, double defaultMoney, boolean force) {
        return this.createAccount(player.getName(), defaultMoney, force);
    }

    public boolean createAccount(String player, double defaultMoney, boolean force) {
        CreateAccountEvent event = new CreateAccountEvent(player, defaultMoney);
        if (event.isCancelled()) {
            return false;
        }
        return Money.getInstance().createAccount(event.getPlayer(), (float) event.getDefaultMoney(), 0, 0);
    }

    public LinkedHashMap<String, Double> getAllMoney() {
        Set<String> players = Money.getInstance().getPlayers();

        return new LinkedHashMap<String, Double>() {
            {
                players.forEach(player -> put(player, (double) Money.getInstance().getMoney(player, CurrencyType.FIRST)));
            }
        };
    }

    public double myMoney(Player player) {
        return this.myMoney(player.getName());
    }

    public double myMoney(String player) {
        return Money.getInstance().getMoney(player);
    }

    public int setMoney(Player player, double amount) {
        return this.setMoney(player.getName(), amount, false);
    }

    public int setMoney(Player player, double amount, boolean force) {
        return this.setMoney(player.getName(), amount, false);
    }

    public int setMoney(String player, double amount) {
        return this.setMoney(player, amount, false);
    }

    public int setMoney(String player, double amount, boolean force) {
        return Money.getInstance().setMoney(player, (float) amount, CurrencyType.FIRST) ? 1 : -2;
    }

    public int addMoney(Player player, double amount) {
        return this.addMoney(player.getName(), amount, false);
    }

    public int addMoney(Player player, double amount, boolean force) {
        return this.addMoney(player.getName(), amount, false);
    }

    public int addMoney(String player, double amount) {
        return this.addMoney(player, amount, false);
    }

    public int addMoney(String player, double amount, boolean force) {
        return Money.getInstance().addMoney(player, (float) amount, CurrencyType.FIRST) ? 1 : -2;
    }

    public int reduceMoney(Player player, double amount) {
        return this.reduceMoney(player.getName(), amount, false);
    }

    public int reduceMoney(Player player, double amount, boolean force) {
        return this.reduceMoney(player.getName(), amount, false);
    }

    public int reduceMoney(String player, double amount) {
        return this.reduceMoney(player, amount, false);
    }

    public int reduceMoney(String player, double amount, boolean force) {
        return Money.getInstance().reduceMoney(player, (float) amount, CurrencyType.FIRST) ? 1 : -2;
    }

    public String getMonetaryUnit() {
        return Money.getInstance().getCurrency(CurrencyType.FIRST);
    }

    public double getDefaultMoney() {
        return Money.getInstance().getConfig().getDouble("initial-money-1");
    }

    public double getMaxMoney() {
        return Float.MAX_VALUE;
    }

    public void saveAll() {
        Money.getInstance().save();
    }
}
