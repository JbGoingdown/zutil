package cn.zm1001.util.crypto.symmetric;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Desc 3Des Crypto Test
 * @Author Dongd_Zhou
 */
public class Des3UtilsTest {
    @Test
    public void testGenerateSecretKey() {
        String key = Des3Utils.generateSecretKey();
        System.out.println(key);
        Assert.assertEquals(24, Base64.decodeBase64(key).length);
    }

    @Test
    public void testEncrypt() {
        String cipherText = Des3Utils.encrypt("XurQtnlo8tZXzY8jyM4EEx8ZKlvxEDhY", "zm1001.cn");
        Assert.assertEquals("cd6d45be8a43c330c8efe214b666bdd9", cipherText);
    }

    @Test
    public void testDecrypt() {
        String plainText = Des3Utils.decrypt("XurQtnlo8tZXzY8jyM4EEx8ZKlvxEDhY", "cd6d45be8a43c330c8efe214b666bdd9");
        Assert.assertEquals("zm1001.cn", plainText);
    }
}
