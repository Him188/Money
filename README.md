# Money
## 特性
1. 多语言. (简体中文, 繁体中文, 英文), 并且可以在 Language.properties 中修改语言. (命令提示, 配置文件提示都支持多语言)  
2. 自定义命令. /money, /钱包, /wallet ...  喜欢哪个就用哪个!  
3. 支持 2 种货币. 并且可以关闭第二种货币, 货币名称当然也可以自定义.  
4. 银行有利息  
5. 支持多种数据保存方式.  
6. 已通过测试, 几乎无 bug  
7. 支持全服批量修改货币数量  
8. 配置文件含详细注释  
  
## 指令
指令自己去 Command.yml 看咯...反正可以自定义说了也没用  
  
## 关于重写
Money 2.0.0 将几乎所有代码重写(自己终于能看得顺眼了)   
重写修改内容:  
- 更多的事件(共7个). 利息也有事件了, 其他插件可以拦截利息发放  
- 添加/删除 API 更新  
- 货币种类由 boolean 更换为 enum CurrencyType, 此 enum 中有非常详细的注释  
- 添加了更多的API (详见 interface MoneyAPI)  
- 指令由原本压缩在一个onCommand方法改为多指令类  
- 指令不规范问题解决  
- 支持修改指令权限  
- 指令玩家名参数更便捷  
- 使用 properties 作为语言配置文件, 更简单. 旧数据自动转换  
- 终于支持了数据库 (使用我所在小组的一个数据库插件作为前置). 旧数据自动转换  
- 事件从 Main 分离, 缩减 Main 体积(API多了还不是有25k字符850行). 简洁  
- 事件归了个类 (虽然基本没啥用)  

## 主要 API 修改:
- enum 货币类型  
- 部分事件修改包名  

开发者请立即更新你的插件, 以免给服主带来不必要的麻烦

## 依赖
Money 2.0.0 开始, 数据库依赖插件 [MoeDB]("https://www.github.com/Him188/MoeDB"). 安装新版 Money 时请确保已经安装该插件.  
若未安装将无法启动


## How to use
### Maven repository

1. Add `repository` in `repositories`
    ```xml
    <repository>
        <id>him188-money</id>
        <url>http://repo.him188.moe:8081/repository/money/</url>
    </repository>
    ```
2. Add `dependency` in `build.dependencies`
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
