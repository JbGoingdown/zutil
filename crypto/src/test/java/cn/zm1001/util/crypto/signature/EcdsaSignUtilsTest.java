package cn.zm1001.util.crypto.signature;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * @Desc ECDSA Sign Test
 * @Author Dongd_Zhou
 */
public class EcdsaSignUtilsTest {

    @Test
    public void testEcdsaSign() {
        Map<String, String> key = EcdsaSignUtils.generateSecretKey();
        String privateKey = key.get(RsaSignUtils.PRIVATE);
        String publicKey = key.get(RsaSignUtils.PUBLIC);

        String content = "zm1001.cn";
        String sign = EcdsaSignUtils.sign(privateKey, content);
        boolean verify = EcdsaSignUtils.verify(publicKey, content, sign);
        Assert.assertTrue(verify);
    }
}
