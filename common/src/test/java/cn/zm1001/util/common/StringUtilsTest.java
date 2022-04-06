package cn.zm1001.util.common;

import org.junit.Assert;
import org.junit.Test;

/**
 * @Desc 字符串工具类测试
 * @Author Dongd_Zhou
 */
public class StringUtilsTest {

    @Test
    public void testFormat() {
        String result1 = StringUtils.format("It is {} and {}", "red", "blue");
        String result2 = StringUtils.format("It is \\{} and {}", "red", "blue");
        String result3 = StringUtils.format("It is \\\\{} and {}", "red", "blue");
        Assert.assertEquals("It is red and blue", result1);
        Assert.assertEquals("It is {} and red", result2);
        Assert.assertEquals("It is \\red and blue", result3);
    }
}
