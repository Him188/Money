package money.utils;

import cn.nukkit.Server;
import cn.nukkit.command.CommandReader;
import cn.nukkit.utils.Logger;
import cn.nukkit.utils.TextFormat;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 语言选择器.
 * Help admin choose language
 *
 * @author Him188moe @ Money Project
 */
public final class LanguageChooser {
    private final FutureTask<LanguageType> task;
    private final Logger logger;

    public LanguageChooser(final Logger logger) {
        this.logger = logger;
        this.task = new FutureTask<>(this::_getLanguage);
    }

    public void startChoosing() {
        this.task.run();
    }

    /**
     * 阻塞线程直到用户完成语言选择
     *
     * @return 语言类型
     */
    public LanguageType getLanguage() {
        try {
            return this.task.get(120, TimeUnit.SECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException timeout) {
            this.logger.notice("Language selecting timeout. English is selected");
            return LanguageType.DEFAULT_LANGUAGE;
        }
    }

    private LanguageType _getLanguage() {
        CommandReader reader = CommandReader.getInstance();
        reader.interrupt();//暂时中断nk服务
        LanguageType type = _getLanguage0(reader);
        reader.start();
        return type;
    }

    private LanguageType _getLanguage0(CommandReader reader) {
        printLanguageHelp(logger);

        int errorTimes = 0;

        String line;
        while ((line = reader.readLine()) != null) {
            if (errorTimes++ > 3) {//error too many times
                this.logger.notice("You typed error too many times. English is selected");
                break;
            }

            switch (line) {
                case "stop": {
                    this.task.cancel(true);
                    logger.notice("Language selecting canceled");
                    Server.getInstance().shutdown();
                    break;
                }

                default: {
                    LanguageType type;
                    try {
                        type = LanguageType.fromString(line);
                    } catch (IllegalArgumentException ignored) {
                        logger.info("");
                        logger.warning("你输入了错误的数据. 请选择 chs, cht, eng 其中一个输入.");
                        printLanguageList(logger);
                        continue;
                    }

                    if (type == null) {
                        continue;
                    }

                    return type;
                }
            }
        }

        return LanguageType.DEFAULT_LANGUAGE;
    }

    private static void printLanguageList(Logger logger) {
        logger.info(TextFormat.AQUA + "chs: 简体中文");
        logger.info(TextFormat.AQUA + "cht: 繁體中文");
        logger.info(TextFormat.AQUA + "eng: English");
    }

    private static void printLanguageHelp(Logger logger) {
        logger.info("");
        logger.notice("欢迎使用经济插件, 请选择语言: (输入 3 次错误或 120 秒内不选择自动选择英文)");
        logger.notice("Hello. Please choose a language: (It will choose English automatically when inputting error 3 times or 120s-timeout)");
        printLanguageList(logger);
        logger.notice("请输入 chs/cht/eng 选择对应语言.");
        logger.notice("Please type chs/cht/eng to choose.");
    }
}
