package cn.zm1001.util.log;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;
import cn.zm1001.util.log.exception.LogException;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * @Author Dongd_Zhou
 * @Desc 日志配置信息加载
 */
public class LogbackConfigLoader {
    private static final String LOG_CONF_FILE = "log.properties";
    private static final String APP_UK = "log_app_uk";
    private static final String APP_UK_DEFAULT = "not_provide_appUK";
    private static final String LEVEL = "log_level";
    private static final String LEVEL_DEFAULT = "info";
    private static final String ENV = "log_env";
    private static final String ENV_DEFAULT = "product";
    private static final String PATH = "log_path";
    private static final String PATH_DEFAULT = "/var/log";
    private static final String FILE_NAME = "log_file_name";
    private static final String FILE_NAME_DEFAULT = "logback.log";
    private static final String MAX_HISTORY = "log_max_history";
    private static final String MAX_HISTORY_DEFAULT = "30";
    private static final String MAX_FILE_SIZE = "log_max_file_size";
    private static final String MAX_FILE_SIZE_DEFAULT = "100MB";

    /**
     * <p>初始化方法</p>
     * <p>文件全路径：${log_path}/log-${log_appUk}/%d{yyyy-MM-dd}-%i-${log_file_name}</p>
     * <p>读取项目根目录下log.properties配置</p>
     * <p>log.appUk=项目唯一标识，默认not_provide_appUK</p>
     * <p>log.level=打印日志级别，默认info</p>
     * <p>log.env=日志使用环境（product/test）,默认product</p>
     * <p>log.path=日志存放文件夹，默认/var/log</p>
     * <p>log.fileName=日志文件名，默认logback.log</p>
     * <p>log.maxHistory=日志存放天数，默认30天</p>
     * <p>log.maxFileSize=单日志文件大小，默认100MB</p>
     */
    public void init() {
        try (InputStream in = LogbackConfigLoader.class.getClassLoader().getResourceAsStream(LOG_CONF_FILE)) {
            Properties p = new Properties();
            p.load(in);
            // 若空设置参数默认值
            getAndSet(p, APP_UK, APP_UK_DEFAULT);
            getAndSet(p, LEVEL, LEVEL_DEFAULT);
            getAndSet(p, ENV, ENV_DEFAULT);
            getAndSet(p, PATH, PATH_DEFAULT);
            getAndSet(p, FILE_NAME, FILE_NAME_DEFAULT);
            getAndSet(p, MAX_HISTORY, MAX_HISTORY_DEFAULT);
            getAndSet(p, MAX_FILE_SIZE, MAX_FILE_SIZE_DEFAULT);

            //加载logback配置
            load(p);
        } catch (Exception e) {
            throw new LogException("log init fail", e);
        }
    }

    /**
     * 取出参数值，为空设置为默认值
     *
     * @param p            {@link Properties}
     * @param key          参数键值
     * @param defaultValue 默认值
     */
    private void getAndSet(Properties p, String key, String defaultValue) {
        String value = p.getProperty(key, defaultValue);
        System.setProperty(key, value);
    }

    /**
     * 读取logback配置
     *
     * @param p {@link Properties}
     */
    private void load(Properties p) {
        // 根据环境读取不同的logback配置
        String env = p.getProperty(ENV);
        if (null != env) {
            String filePath = "META-INF/logger/logback.xml";
            if (!ENV_DEFAULT.equalsIgnoreCase(env)) {
                filePath = "META-INF/logger/logback-test.xml";
            }

            try (InputStream is = LogbackConfigLoader.class.getClassLoader().getResourceAsStream(filePath)) {
                LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
                JoranConfigurator configurator = new JoranConfigurator();
                configurator.setContext(lc);
                lc.reset();
                configurator.doConfigure(is);
                StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
            } catch (Exception e) {
                throw new LogException("log load fail", e);
            }
        }
    }
}
