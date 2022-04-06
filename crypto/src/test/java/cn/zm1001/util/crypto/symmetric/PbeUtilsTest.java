package cn.zm1001.util.crypto.symmetric;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Desc PBE Crypto Test
 * @Author Dongd_Zhou
 */
public class PbeUtilsTest {

    @Test
    public void testGenerateSalt() {
        String salt = PbeUtils.generateSalt();
        System.out.println(salt);
        Assert.assertEquals(16, Base64.decodeBase64(salt).length);
    }

    @Test
    public void testEncrypt() {
        String cipherText = PbeUtils.encrypt("zm10012017", "FijfaeViejEtN5TqKQnuHQ==", "zm1001.cn");
        Assert.assertEquals("27RdjHGgUnmp3AOxdtle+g==", cipherText);
    }

    @Test
    public void testDecrypt() {
        String plainText = PbeUtils.decrypt("zm10012017", "FijfaeViejEtN5TqKQnuHQ==", "27RdjHGgUnmp3AOxdtle+g==");
        Assert.assertEquals("zm1001.cn", plainText);
    }

}
