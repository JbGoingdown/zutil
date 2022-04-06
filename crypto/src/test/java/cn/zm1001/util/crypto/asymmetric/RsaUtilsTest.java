package cn.zm1001.util.crypto.asymmetric;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * @author Dongd_Zhou
 * @description RSA Crypto Test
 * @date 2021/10/29
 */
public class RsaUtilsTest {

    @Test
    public void testRsa1() {
        Map<String, String> secretKey = RsaUtils.generateSecretKey();
        String privateKey = secretKey.get(RsaUtils.PRIVATE);
        String publicKey = secretKey.get(RsaUtils.PUBLIC);
        // 私钥加密，公钥解密
        String plainText = "zm1001.cn";
        String cipherText = RsaUtils.encryptByPrivate(privateKey, plainText);
        String decrypt = RsaUtils.decryptByPublic(publicKey, cipherText);
        Assert.assertEquals(plainText, decrypt);
    }

    @Test
    public void testRsa2() {
        Map<String, String> secretKey = RsaUtils.generateSecretKey();
        String privateKey = secretKey.get(RsaUtils.PRIVATE);
        String publicKey = secretKey.get(RsaUtils.PUBLIC);
        // 公钥加密，私钥解密
        String plainText = "zm1001.cn";
        String cipherText = RsaUtils.encryptByPublic(publicKey, plainText);
        String decrypt = RsaUtils.decryptByPrivate(privateKey, cipherText);
        Assert.assertEquals(plainText, decrypt);
    }
}
