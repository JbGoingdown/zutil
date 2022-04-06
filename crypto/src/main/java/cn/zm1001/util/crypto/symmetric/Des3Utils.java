package cn.zm1001.util.crypto.symmetric;

import cn.zm1001.util.common.asserts.ParamAssert;
import cn.zm1001.util.crypto.exception.CryptoException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;

/**
 * @Author Dongd_Zhou
 * @Desc 3Des 加解密
 * <pre>
 *     |----------|----------|------------------------------|------------------------------|----------|
 *     |密钥长度    |默认       |工作模式                       |填充方式                        |实现方     |
 *     |----------|----------|------------------------------|------------------------------|----------|
 *     |112、168  |168       |ECB、CBC、PCBC、CTR、CTS、CFB、  |NoPadding、PKCS5Padding       |JDK       |
 *     |          |          |CFB8~128、OFB、OFB8~128        |ISO10126Padding               |          |
 *     |----------|----------|------------------------------|------------------------------|----------|
 *     |128、192  |168       |ECB、CBC、PCBC、CTR、CTS、CFB、  |PKCS7Padding、ISO10126d2Padding|BC        |
 *     |          |          |CFB8~128、OFB、OFB8~128        |X932Padding、ISO7816d4Padding |          |
 *     |          |          |                              |ZeroBytePadding               |          |
 *     |----------|----------|------------------------------|------------------------------|----------|
 * </pre>
 */
public class Des3Utils {
    private static final String ALGORITHM = "DESede";
    private static final String PADDING = "DESede/ECB/PKCS5Padding";

    /**
     * 生成3DES密钥key
     *
     * @return 密钥Base64加密字符串
     */
    public static String generateSecretKey() {
        try {
            // 返回生成指定算法密钥的KeyGenerator对象
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            // 初始化此密钥生成器,使其具有确定的密钥大小
            keyGenerator.init(168);
            // 生成一个密钥
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.encodeBase64String(secretKey.getEncoded());
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    private static Key convertSecretKey(String secretKey) {
        try {
            // 实例化3DES密钥规则
            DESedeKeySpec desKeySpec = new DESedeKeySpec(Base64.decodeBase64(secretKey));
            // 实例化密钥工厂
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            // 生成密钥
            return factory.generateSecret(desKeySpec);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 3DES加密
     *
     * @param secretKey 密钥，Base64加密字符串，生成见{@link Des3Utils#generateSecretKey()}
     * @param plaintext 明文，需加密的内容
     * @return 密文
     */
    public static String encrypt(String secretKey, String plaintext) {
        ParamAssert.isNotEmpty(secretKey, "3DES secret key is required");
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
     * 3DES解密
     *
     * @param secretKey  密钥，Base64加密字符串，生成见{@link Des3Utils#generateSecretKey()}
     * @param ciphertext 密文，需解密的内容
     * @return 明文，解密结果
     */
    public static String decrypt(String secretKey, String ciphertext) {
        ParamAssert.isNotEmpty(secretKey, "3DES secret key is required");
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
