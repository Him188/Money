package money;

import cn.nukkit.Player;
import me.onebone.economyapi.EconomyAPI;

/**
 * Plugin adapter that allows developer using MoneyAPI to access EconomyAPI <br>
 *
 * @author Him188moe @ Money Project
 */
public class Money implements MoneyAPI {
    private static Money instance = new Money();

    public static Money getInstance() {
        return instance;
    }


    public String getMoneyUnit1() {
        return EconomyAPI.getInstance().getConfig().getSection("money").getString("monetary-unit", "$");
    }

    @Deprecated
    public String getMonetaryUnit1() {
        return getMoneyUnit1();
    }

    public String getCurrency1() {
        return getMoneyUnit1();
    }

    public String getMoneyUnit2() {
        return getMoneyUnit1();
    }

    public String getMonetaryUnit2() {
        return getMoneyUnit1();
    }

    public String getCurrency2() {
        return getMoneyUnit1();
    }

    public String getCurrency(CurrencyType type) {
        return getMoneyUnit1();
    }

    @Deprecated
    public String getMoneyUnit(CurrencyType type) {
        return getMoneyUnit1();
    }

    @Deprecated
    public String getMonetaryUnit(CurrencyType type) {
        return getMoneyUnit1();
    }

    @Deprecated
    public String getMoneyUnit(boolean unit) {
        return getMoneyUnit1();
    }

    @Deprecated
    public String getMonetaryUnit(boolean unit) {
        return getMoneyUnit1();
    }

    public boolean isMoneyUnit2Enabled() {
        return false;
    }

    public boolean isCurrency2Enabled() {
        return false;
    }

    @Deprecated
    public float getMoney(String player, boolean type) {
        return getMoney(player);
    }

    @Deprecated
    public float getMoney(Player player, boolean type) {
        return getMoney(player);
    }

    public float getMoney(String player, CurrencyType type) {
        return getMoney(player);
    }

    public float getMoney(Player player, CurrencyType type) {
        return getMoney(player);
    }

    public float getMoney(String player) {
        return (float) EconomyAPI.getInstance().myMoney(player);
    }

    public float getMoney(Player player) {
        return (float) EconomyAPI.getInstance().myMoney(player);
    }

    @Deprecated
    public boolean setMoney(String player, float money, boolean type) {
        return setMoney(player, money);
    }

    @Deprecated
    public boolean setMoney(Player player, float money, boolean type) {
        return setMoney(player, money);
    }

    public boolean setMoney(String player, float money, CurrencyType type) {
        return setMoney(player, money);
    }

    public boolean setMoney(Player player, float money, CurrencyType type) {
        return setMoney(player, money);
    }

    public boolean setMoney(String player, float money) {
        return EconomyAPI.getInstance().setMoney(player, money) == 1;
    }

    public boolean setMoney(Player player, float money) {
        return EconomyAPI.getInstance().setMoney(player, money) == 1;
    }

    public float getBank(Player player) {
        return 0;
    }

    public float getBank(String player) {
        return 0;
    }

    public boolean setBank(String player, float money) {
        return false;
    }

    public boolean setBank(Player player, float money) {
        return false;
    }

    public boolean addMoney(String player, float amount) {
        return EconomyAPI.getInstance().addMoney(player, amount) == 1;
    }

    public boolean addMoney(Player player, float amount) {
        return EconomyAPI.getInstance().addMoney(player, amount) == 1;
    }

    public boolean addMoney(String player, float amount, CurrencyType type) {
        return addMoney(player, amount);
    }

    public boolean addMoney(Player player, float amount, CurrencyType type) {
        return addMoney(player, amount);
    }

    @Deprecated
    public boolean addMoney(Player player, float amount, boolean type) {
        return addMoney(player, amount);
    }

    @Deprecated
    public boolean addMoney(String player, float amount, boolean type) {
        return addMoney(player, amount);
    }

    public boolean reduceMoney(String player, float amount) {
        return EconomyAPI.getInstance().reduceMoney(player, amount) == 1;
    }

    public boolean reduceMoney(Player player, float amount) {
        return EconomyAPI.getInstance().reduceMoney(player, amount) == 1;
    }

    public boolean reduceMoney(String player, float amount, CurrencyType type) {
        return reduceMoney(player, amount);
    }

    public boolean reduceMoney(Player player, float amount, CurrencyType type) {
        return reduceMoney(player, amount);
    }

    @Deprecated
    public boolean reduceMoney(Player player, float amount, boolean type) {
        return reduceMoney(player, amount);
    }

    @Deprecated
    public boolean reduceMoney(String player, float amount, boolean type) {
        return reduceMoney(player, amount);
    }

    @Deprecated
    public int setAllMoney(float amount) {
        return 0;
    }

    @Deprecated
    public int setAllMoney(float amount, CurrencyType type) {
        return 0;
    }

    @Deprecated
    public int addAllMoney(float amount) {
        return 0;
    }

    @Deprecated
    public int addAllMoney(float amount, CurrencyType type) {
        return 0;
    }

    @Deprecated
    public int reduceAllMoney(float amount) {
        return 0;
    }

    @Deprecated
    public int reduceAllMoney(float amount, CurrencyType type) {
        return 0;
    }

    public boolean addBank(String player, float amount) {
        return false;
    }

    public boolean addBank(Player player, float amount) {
        return false;
    }

    public boolean reduceBank(String player, float amount) {
        return false;
    }

    public boolean reduceBank(Player player, float amount) {
        return false;
    }

    @Deprecated
    public int setAllBank(float amount) {
        return 0;
    }
}
