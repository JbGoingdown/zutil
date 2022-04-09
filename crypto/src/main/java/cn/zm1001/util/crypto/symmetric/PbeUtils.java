package cn.zm1001.util.crypto.symmetric;

import cn.zm1001.util.crypto.exception.CryptoException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.Validate;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;

/**
 * @Desc PBE 加解密
 * @Author Dongd_Zhou
 *
 * <pre>
 *     |-------------------------|---------------|----------|----------|--------------------|----------|
 *     |算法                      |密钥长度        |默认       |工作模式    |填充方式              |实现方     |
 *     |-------------------------|---------------|----------|----------|--------------------|----------|
 *     |PBEWithMD5AndDES         |56             |56        |CBC       |PKCS5Padding        |JDK       |
 *     |-------------------------|---------------|----------|----------|--------------------|----------|
 *     |PBEWithMD5AndTripleDES   |112、168       |168       |CBC       |PKCS5Padding        |JDK       |
 *     |-------------------------|---------------|----------|----------|--------------------|----------|
 *     |PBEWithSHA1AndDES        |112、168       |168       |CBC       |PKCS5Padding        |JDK       |
 *     |-------------------------|---------------|----------|----------|--------------------|----------|
 *     |PBEWithSHA1AndRC2_40     |40~1024(8倍数)  |128       |CBC       |PKCS5Padding        |JDK       |
 *     |-------------------------|---------------|----------|----------|--------------------|----------|
 *
 * </pre>
 */
public class PbeUtils {
    private static final String ALGORITHM = "PBEWithSHA1AndRC2_40";

    /**
     * PBE盐
     *
     * @return 盐Base64加密字符串
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        return Base64.encodeBase64String(random.generateSeed(16));
    }

    private static Key convertSecretKey(String secretKey) {
        try {
            // 实例化DES密钥规则
            PBEKeySpec pbeKeySpec = new PBEKeySpec(secretKey.toCharArray());
            // 实例化密钥工厂
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            // 生成密钥
            return factory.generateSecret(pbeKeySpec);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * PBE加密
     *
     * @param secretKey 密钥，SHA1签名用
     * @param salt      盐值，Base64加密字符串，生成见{@link PbeUtils#generateSalt()}
     * @param plaintext 明文，需加密的内容
     * @return 返回密文
     */
    public static String encrypt(String secretKey, String salt, String plaintext) {
        Validate.notEmpty(secretKey, "PBE secret key is required");
        Validate.notEmpty(secretKey, "PBE salt is required");
        Validate.notEmpty(plaintext, "plaintext is required");
        try {
            // 生成密钥
            Key key = convertSecretKey(secretKey);
            PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(Base64.decodeBase64(salt), 100);
            // 加密
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, pbeParameterSpec);
            byte[] result = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(result);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * PBE解密
     *
     * @param secretKey  密钥，SHA1签名用
     * @param salt       盐值，Base64加密字符串，生成见{@link PbeUtils#generateSalt()}
     * @param ciphertext 密文，需解密的内容
     * @return 明文，解密结果
     */
    public static String decrypt(String secretKey, String salt, String ciphertext) {
        Validate.notEmpty(secretKey, "PBE secret key is required");
        Validate.notEmpty(secretKey, "PBE salt is required");
        Validate.notEmpty(ciphertext, "ciphertext is required");
        try {
            // 生成密钥
            Key convertSecretKey = convertSecretKey(secretKey);
            PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(Base64.decodeBase64(salt), 100);
            // 解密
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, convertSecretKey, pbeParameterSpec);
            byte[] result = cipher.doFinal(Base64.decodeBase64(ciphertext));
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }
}
