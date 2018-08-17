# Money: Economy System Plugin

## Status

| Newest Version | Update Date | Update Info                            |
|:--------------:|:-----------:|:---------------------------------------|
|      3.3       | 2018/08/17  | Fix bugs; Automatic language selecting |

## Introduction
This plugin is like  [EcocomyAPI](https://github.com/EconomyS/EconomyAPI).  
We have wallet, bank(with interest), database(Using [MoeDB](https://github.com/Him188/MoeDB)) and so on...

While EconomyAPI is used in global, Money is used in China, generally.  
So, many plugins made by Chinese depended on this.

Emm, Of cause you don't all like this plugin.  
So, **I made adapter for EconomyAPI and Money**

**With the adapter, you can use the api of EconomyAPI to access Money, and vice versa.**

That means, you can use both plugins depend on Money or EconomyAPI, without installing two economy plugin.

## Features
1. Multi languages(Chinese, English), customizable.
2. Multi **database** (Now support Redis and Yaml)(Using [MoeDB](https://github.com/Him188/MoeDB)).
3. Custom commands
4. Two currency types, customizable type name.

## Commands
Too many to introduce.  
Please check more in `Command.yml`.
- /moneyselectlang <chs|eng|cht>  Change language type.(Can't modify in command.yml)
- /coin, /point.  Gets wallet ballance.
- /paycoin, /paypoint.  Pays to someone.
- /givecoin, /givepoint.  (OP)Gives someone.
- /coinlist, /pointlist.  See list
- /banksave, /banktake, /bank.  Bank

## Permissions
  `money.command.{command}`  
   {command} represents "coin", "pay", "give", etc.

## Development

### CurrencyType
Learn about [money.CurrencyType](/src/main/java/money/CurrencyType.java)  
Preview:
```java
package money;
public enum CurrencyType{
    FIRST, //coin
    SECOND, //point
}
```

### MoneyAPI
**Look up all APIs in [MoneyAPI](/src/main/java/money/MoneyAPI.java)**
Preview **general** uses:
```java
package money;
public interface MoneyAPI{
    static Money getInstance();
    
    float getMoney(Player player, CurrencyType type);
    boolean setMoney(Player player, CurrencyType type, float amount);
    boolean addMoney(Player player, CurrencyType type, float amount);
    boolean reduceMoney(Player player, CurrencyType type, float amount);
    
    float getBank(Player player);
    boolean setBank(Player player);
    boolean addBank(Player player, float amount);
    boolean reduceBank(Player player, float amount);
    
    boolean createAccount(Player player, float money1, float money2, float bank);
}
```

## Dependency
- Database support: **MoeDB([GitHub](https://github.com/Him188/MoeDB))**

## How to use
### Maven repository

1. Add `repository` in `repositories`
    ```xml
    <repository>
        <id>him188-money</id>
        <url>http://repo.him188.moe:8081/repository/money/</url>
    </repository>
    ```
2. Add `dependency` in `dependencies`
    ```xml
    <dependency>
        <groupId>money</groupId>
        <artifactId>money</artifactId>
        <version>LATEST</version>
    </dependency>
    ```
3. Don't forget to add `depend` into `plugin.yml`
    ```yaml
    depend:
    - Money
    ```

### Package JAR file

1. 在项目根目录中运行 `mvn clean package`

2. 在 `target/` 中找到构建完成的 jar
