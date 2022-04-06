package cn.zm1001.util.crypto.symmetric;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Dongd_Zhou
 * @Desc Aes Crypto Test
 */
public class AesUtilsTest {
    @Test
    public void testGenerateSecretKey() {
        String key = AesUtils.generateSecretKey();
        System.out.println(key);
        Assert.assertEquals(16, Base64.decodeBase64(key).length);
    }

    @Test
    public void testEncrypt() {
        String cipherText = AesUtils.encrypt("3T3ODgS4XX06Q3gSGg2z2w==", "zm1001.cn");
        Assert.assertEquals("S1aTLjrllFzGCk1Bu9+Bng==", cipherText);
    }

    @Test
    public void testDecrypt() {
        String plainText = AesUtils.decrypt("3T3ODgS4XX06Q3gSGg2z2w==", "S1aTLjrllFzGCk1Bu9+Bng==");
        Assert.assertEquals("zm1001.cn", plainText);
    }
}
