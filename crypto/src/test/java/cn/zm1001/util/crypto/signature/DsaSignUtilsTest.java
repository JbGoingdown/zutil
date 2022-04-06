package cn.zm1001.util.crypto.signature;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * @Desc DSA Sign Test
 * @Author Dongd_Zhou
 */
public class DsaSignUtilsTest {

    @Test
    public void testDsaSign() {
        Map<String, String> key = DsaSignUtils.generateSecretKey();
        String privateKey = key.get(RsaSignUtils.PRIVATE);
        String publicKey = key.get(RsaSignUtils.PUBLIC);

        String content = "zm1001.cn";
        String sign = DsaSignUtils.sign(privateKey, content);
        boolean verify = DsaSignUtils.verify(publicKey, content, sign);
        Assert.assertTrue(verify);
    }
}
