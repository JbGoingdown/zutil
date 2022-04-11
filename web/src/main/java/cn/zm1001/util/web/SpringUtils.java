package cn.zm1001.util.web;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @Desc spring工具类
 * @Author Dongd_Zhou
 */
public class SpringUtils implements BeanFactoryPostProcessor, ApplicationContextAware {
    /** Spring应用上下文环境 */
    private static ApplicationContext applicationContext;
    private static ConfigurableListableBeanFactory beanFactory;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        SpringUtils.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }

    /**
     * 判断指定名称的bean是否单例
     * 如果指定名称的bean没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
     *
     * @param name bean名称
     * @return boolean 是否单例
     * @throws NoSuchBeanDefinitionException 未找到bean异常
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.isSingleton(name);
    }

    /**
     * 判断是否存在指定名称的Bean
     *
     * @param name bean名称
     * @return boolean 是否包含bean
     */
    public static boolean containsBean(String name) {
        return beanFactory.containsBean(name);
    }

    /**
     * 获取指定名称的Bean对象
     *
     * @param name bean名称
     * @return Object 一个以所给名字注册的bean的实例
     * @throws BeansException bean异常
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T) beanFactory.getBean(name);
    }

    /**
     * 获取指定类型的Bean对象
     *
     * @param clazz bean类型
     * @return bean对象
     * @throws BeansException bean异常
     */
    public static <T> T getBean(Class<T> clazz) throws BeansException {
        return beanFactory.getBean(clazz);
    }

    /**
     * 获取指定名称的Bean的对象类型
     *
     * @param name bean类型
     * @return Class 注册对象的类型
     * @throws NoSuchBeanDefinitionException 未找到bean异常
     */
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getType(name);
    }

    /**
     * 获取指定名称的Bean的别名
     *
     * @param name bean名称
     * @return bean别名
     * @throws NoSuchBeanDefinitionException 未找到bean异常
     */
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getAliases(name);
    }

    /**
     * 获取aop代理对象
     *
     * @param invoker 需要代理的类
     * @return 代理对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAopProxy(T invoker) {
        return (T) AopContext.currentProxy();
    }

    /**
     * 获取当前的环境配置，无配置返回null
     *
     * @return 当前的环境配置
     */
    public static String[] getActiveProfiles() {
        return applicationContext.getEnvironment().getActiveProfiles();
    }

    /**
     * 获取当前的环境配置，当有多个环境配置时，只获取第一个
     *
     * @return 当前的环境配置
     */
    public static String getFirstActiveProfile() {
        final String[] activeProfiles = getActiveProfiles();
        return ArrayUtils.isNotEmpty(activeProfiles) ? activeProfiles[0] : null;
    }
}
