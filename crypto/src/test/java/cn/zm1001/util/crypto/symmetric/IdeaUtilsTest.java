package cn.zm1001.util.crypto.symmetric;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Desc IDEA Crypto Test
 * @Author Dongd_Zhou
 */
public class IdeaUtilsTest {
    @Test
    public void testGenerateSecretKey() {
        String key = IdeaUtils.generateSecretKey();
        System.out.println(key);
        Assert.assertEquals(16, Base64.decodeBase64(key).length);
    }

    @Test
    public void testIdea() {
        String cipherText = IdeaUtils.encrypt("vUm0slvWuVZtnTQa7qhxaw==", "zm1001.cn");
        String plainText = IdeaUtils.decrypt("vUm0slvWuVZtnTQa7qhxaw==", cipherText);
        Assert.assertEquals("zm1001.cn", plainText);
    }
}
