# Adapters

This is adapters for Money and EconomyAPI.

## EconomyToMoney

In this case,  
Your server should install plugins:
- Money
- EconomyToMoney

And you can use plugins which depends on EconomyAPI.  
The EconomyToMoney will automatically transfer api-calling from EconomyAPI to Money.

## MoneyToEconomy

In this case,  
Your server should install plugins:
- EconomyAPI
- MoneyToEconomy

And you can use plugins which depends on Money.  
The MoneyToEconomy will automatically transfer api-calling from Money to EconomyAPI.


# Samples

Plugin SkyWars depend on Money, but you are using EconomyAPI,  
and your plugins also depends on EconomyAPI.  
Now, you need MoneyToEconomy, then you can use SkyWars.
