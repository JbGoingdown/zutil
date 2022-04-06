package cn.zm1001.util.crypto.asymmetric;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * @Desc ElGamal Crypto Test
 * @Author Dongd_Zhou
 */
public class ElGamalUtilsTest {
    @Test
    public void testElGamal() {
        Map<String, String> map = ElGamalUtils.generateSecretKey();
        String privateKey = map.get(ElGamalUtils.PRIVATE);
        String publicKey = map.get(ElGamalUtils.PUBLIC);

        String plainText = "zm1001.cn";
        String cipherText = ElGamalUtils.encrypt(publicKey, plainText);
        String decrypt = ElGamalUtils.decrypt(privateKey, cipherText);
        Assert.assertEquals(plainText, decrypt);
    }
}
