package cn.zm1001.util.crypto.signature;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * @Desc RSA Sign Test
 * @Author Dongd_Zhou
 */
public class RsaSignUtilsTest {

    @Test
    public void testRsaSign() {
        Map<String, String> key = RsaSignUtils.generateSecretKey();
        String privateKey = key.get(RsaSignUtils.PRIVATE);
        String publicKey = key.get(RsaSignUtils.PUBLIC);

        String content = "zm1001.cn";
        String sign = RsaSignUtils.sign(privateKey, content);
        boolean verify = RsaSignUtils.verify(publicKey, content, sign);
        Assert.assertTrue(verify);
    }
}
