package cn.zm1001.util.crypto.symmetric;

import cn.zm1001.util.crypto.exception.CryptoException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.Validate;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.Security;

/**
 * @Desc IDEA 加解密
 * @Author Dongd_Zhou
 * <pre>
 *     |----------|----------|----------|--------------------|----------|
 *     |密钥长度    |默认       |工作模式    |填充方式             |实现方     |
 *     |----------|----------|----------|--------------------|----------|
 *     |128       |128       |ECB       |PKCS5Padding        |BC        |
 *     |          |          |          |PKCS7Padding        |          |
 *     |          |          |          |ISO10126Padding     |          |
 *     |          |          |          |ZeroBytePadding     |          |
 *     |----------|----------|----------|--------------------|----------|
 * </pre>
 */
public class IdeaUtils {
    private static final String ALGORITHM = "IDEA";
    private static final String PADDING = "IDEA/ECB/ISO10126Padding";

    /**
     * 生成IDEA密钥key
     *
     * @return 密钥Base64加密字符串
     */
    public static String generateSecretKey() {
        try {
            // 加入BouncyCastleProvider支持
            Security.addProvider(new BouncyCastleProvider());
            // 返回生成指定算法密钥的KeyGenerator对象
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            // 初始化此密钥生成器,使其具有确定的密钥大小
            keyGenerator.init(128);
            // 生成一个密钥
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.encodeBase64String(secretKey.getEncoded());
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    private static Key convertSecretKey(String secretKey) {
        try {
            // AES密钥转换
            return new SecretKeySpec(Base64.decodeBase64(secretKey), ALGORITHM);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * IDEA加密
     *
     * @param secretKey 密钥，Base64加密字符串，生成见{@link IdeaUtils#generateSecretKey()}
     * @param plaintext 明文，需加密的内容
     * @return 密文
     */
    public static String encrypt(String secretKey, String plaintext) {
        Validate.notEmpty(secretKey, "IDEA secret key is required");
        Validate.notEmpty(plaintext, "plaintext is required");
        try {
            // 加入BouncyCastleProvider支持
            Security.addProvider(new BouncyCastleProvider());
            // 生成密钥
            Key convertSecretKey = convertSecretKey(secretKey);
            // 加密
            Cipher cipher = Cipher.getInstance(PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, convertSecretKey);
            byte[] result = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(result);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * IDEA解密
     *
     * @param secretKey  密钥，Base64加密字符串，生成见{@link IdeaUtils#generateSecretKey()}
     * @param ciphertext 密文，需解密的内容
     * @return 明文，解密结果
     */
    public static String decrypt(String secretKey, String ciphertext) {
        Validate.notEmpty(secretKey, "IDEA secret key is required");
        Validate.notEmpty(ciphertext, "ciphertext is required");
        try {
            // 加入BouncyCastleProvider支持
            Security.addProvider(new BouncyCastleProvider());
            // 生成密钥
            Key convertSecretKey = convertSecretKey(secretKey);
            // 解密
            Cipher cipher = Cipher.getInstance(PADDING);
            cipher.init(Cipher.DECRYPT_MODE, convertSecretKey);
            byte[] result = cipher.doFinal(Base64.decodeBase64(ciphertext));
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }
}
