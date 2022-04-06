package cn.zm1001.util.crypto.symmetric;

import cn.zm1001.util.common.asserts.ParamAssert;
import cn.zm1001.util.crypto.exception.CryptoException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;

/**
 * @Desc Des 加解密
 * @Author Dongd_Zhou
 *
 * <pre>
 *     |----------|----------|------------------------------|------------------------------|----------|
 *     |密钥长度    |默认       |工作模式                       |填充方式                        |实现方     |
 *     |----------|----------|------------------------------|------------------------------|----------|
 *     |56        |56        |ECB、CBC、PCBC、CTR、CTS、CFB、 |NoPadding、PKCS5Padding        |JDK       |
 *     |          |          |CFB8到128、OFB、OFB8到128      |ISO10126Padding                |          |
 *     |----------|----------|------------------------------|------------------------------|----------|
 *     |64        |56        |ECB、CBC、PCBC、CTR、CTS、CFB、 |PKCS7Padding、ISO10126d2Padding|BC        |
 *     |          |          |CFB8到128、OFB、OFB8到128      |X932Padding、ISO7816d4Padding  |          |
 *     |          |          |                              |ZeroBytePadding               |          |
 *     |----------|----------|------------------------------|------------------------------|----------|
 * </pre>
 */
public class DesUtils {
    private static final String ALGORITHM = "DES";
    private static final String PADDING = "DES/ECB/PKCS5Padding";

    /**
     * 生成DES密钥key
     *
     * @return 密钥Base64加密字符串
     */
    public static String generateSecretKey() {
        try {
            // 返回生成指定算法密钥的KeyGenerator对象
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            // 初始化此密钥生成器,使其具有确定的密钥大小
            keyGenerator.init(56);
            // 生成一个密钥
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.encodeBase64String(secretKey.getEncoded());
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    private static Key convertSecretKey(String secretKey) {
        try {
            // 实例化DES密钥规则
            DESKeySpec desKeySpec = new DESKeySpec(Base64.decodeBase64(secretKey));
            // 实例化密钥工厂
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            // 生成密钥
            return factory.generateSecret(desKeySpec);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * DES加密
     *
     * @param secretKey 密钥，Base64加密字符串，生成见{@link DesUtils#generateSecretKey()}
     * @param plaintext 明文，需加密的内容
     * @return 密文
     */
    public static String encrypt(String secretKey, String plaintext) {
        ParamAssert.isNotEmpty(secretKey, "DES secret key is required");
        ParamAssert.isNotEmpty(plaintext, "plaintext is required");
        try {
            // 生成密钥
            Key convertSecretKey = convertSecretKey(secretKey);
            // 加密
            Cipher cipher = Cipher.getInstance(PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, convertSecretKey);
            byte[] result = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(result);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * DES解密
     *
     * @param secretKey  密钥，Base64加密字符串，生成见{@link DesUtils#generateSecretKey()}
     * @param ciphertext 密文，需解密的内容
     * @return 明文，解密结果
     */
    public static String decrypt(String secretKey, String ciphertext) {
        ParamAssert.isNotEmpty(secretKey, "DES secret key is required");
        ParamAssert.isNotEmpty(ciphertext, "ciphertext is required");
        try {
            // 生成密钥
            Key convertSecretKey = convertSecretKey(secretKey);
            // 解密
            Cipher cipher = Cipher.getInstance(PADDING);
            cipher.init(Cipher.DECRYPT_MODE, convertSecretKey);
            byte[] result = cipher.doFinal(Hex.decodeHex(ciphertext));
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }
}
