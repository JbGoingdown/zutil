package cn.zm1001.util.crypto.symmetric;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Desc Des Crypto Test
 * @Author Dongd_Zhou
 */
public class DesUtilsTest {
    @Test
    public void testGenerateSecretKey() {
        String key = DesUtils.generateSecretKey();
        System.out.println(key);
        Assert.assertEquals(8, Base64.decodeBase64(key).length);
    }

    @Test
    public void testEncrypt() {
        String cipherText = DesUtils.encrypt("eSNtMuaSDkM=", "zm1001.cn");
        Assert.assertEquals("ac57bf41855db6b57396fbca34cf89b7", cipherText);
    }

    @Test
    public void testDecrypt() {
        String plainText = DesUtils.decrypt("eSNtMuaSDkM=", "ac57bf41855db6b57396fbca34cf89b7");
        Assert.assertEquals("zm1001.cn", plainText);
    }
}
