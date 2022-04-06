package cn.zm1001.util.crypto.asymmetric;

import cn.zm1001.util.common.asserts.ParamAssert;
import cn.zm1001.util.crypto.exception.CryptoException;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @Desc RSA 加解密
 * @Author Dongd_Zhou
 *
 * <pre>
 *     |--------------------|----------|----------|------------------------------|----------|
 *     |密钥长度             |默认       |工作模式    |填充方式                       |实现方     |
 *     |--------------------|----------|----------|------------------------------|----------|
 *     |512~65536(64倍数)    |1024      |ECB       |NoPadding                     |JDK       |
 *     |                    |          |          |PKCS1Padding                  |          |
 *     |                    |          |          |OAEPWITHMD5AndMGF1Pading      |          |
 *     |                    |          |          |OAEPWITHSHA1AndMGF1Pading     |          |
 *     |                    |          |          |OAEPWITHSHA256AndMGF1Pading   |          |
 *     |                    |          |          |OAEPWITHSHA384AndMGF1Pading   |          |
 *     |                    |          |          |OAEPWITHSHA512AndMGF1Pading   |          |
 *     |--------------------|----------|----------|------------------------------|----------|
 *     |512~65536(64倍数)    |2048      |无        |NoPadding                     |BC        |
 *     |                    |          |          |PKCS1Padding                  |          |
 *     |                    |          |          |OAEPWITHMD5AndMGF1Pading      |          |
 *     |                    |          |          |OAEPWITHSHA1AndMGF1Pading     |          |
 *     |                    |          |          |OAEPWITHSHA224AndMGF1Pading   |          |
 *     |                    |          |          |OAEPWITHSHA256AndMGF1Pading   |          |
 *     |                    |          |          |OAEPWITHSHA384AndMGF1Pading   |          |
 *     |                    |          |          |OAEPWITHSHA512AndMGF1Pading   |          |
 *     |                    |          |          |ISO9796-1Padding              |          |
 *     |--------------------|----------|----------|------------------------------|----------|
 * </pre>
 */
public class RsaUtils {
    private static final String ALGORITHM = "RSA";
    public static final String PRIVATE = "privateKey";
    public static final String PUBLIC = "publicKey";

    /**
     * 初始化密钥对
     *
     * @return Map包含公钥私钥，其中Map中的key如下：
     * - privateKey 私钥Base64加密字符串
     * - publicKey 公钥Base64加密字符串
     */
    public static Map<String, String> generateSecretKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(512);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            Map<String, String> map = new HashMap<>();
            map.put(PRIVATE, Base64.encodeBase64String(keyPair.getPrivate().getEncoded()));
            map.put(PUBLIC, Base64.encodeBase64String(keyPair.getPublic().getEncoded()));
            return map;
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 私钥加密，需要公钥解密
     * <p>解密见{@link RsaUtils#decryptByPublic(String, String)}</p>
     *
     * @param privateKey 私钥，Base64加密字符串，生成见{@link RsaUtils#generateSecretKey()}
     * @param plaintext  明文，需要加密的内容
     * @return 密文Base64加密字符串
     */
    public static String encryptByPrivate(String privateKey, String plaintext) {
        ParamAssert.isNotEmpty(privateKey, "RSA private key is required");
        ParamAssert.isNotEmpty(plaintext, "plaintext is required");
        try {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PrivateKey key = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(result);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 公钥解密，需要私钥加密
     * <p>加密见{@link RsaUtils#encryptByPrivate(String, String)}</p>
     *
     * @param publicKey  公钥，Base64加密字符串，生成见{@link RsaUtils#generateSecretKey()}
     * @param ciphertext 密文，需要解密的内容（Base64加密后的字符串）
     * @return 明文，解密结果
     */
    public static String decryptByPublic(String publicKey, String ciphertext) {
        ParamAssert.isNotEmpty(publicKey, "RSA public key is required");
        ParamAssert.isNotEmpty(ciphertext, "ciphertext is required");
        try {
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PublicKey key = keyFactory.generatePublic(x509EncodedKeySpec);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(Base64.decodeBase64(ciphertext));
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 公钥加密，需要私钥解密
     * <p>解密见{@link RsaUtils#decryptByPrivate(String, String)}</p>
     *
     * @param publicKey 公钥，Base64加密字符串，生成见{@link RsaUtils#generateSecretKey()}
     * @param plaintext 明文，需要加密的内容
     * @return 密文Base64加密字符串
     */
    public static String encryptByPublic(String publicKey, String plaintext) {
        ParamAssert.isNotEmpty(publicKey, "RSA public key is required");
        ParamAssert.isNotEmpty(plaintext, "plaintext is required");
        try {
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PublicKey key = keyFactory.generatePublic(x509EncodedKeySpec);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(result);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 私钥解密，需要公钥加密
     * <p>加密见{@link RsaUtils#encryptByPublic(String, String)}</p>
     *
     * @param privateKey 私钥，Base64加密字符串，生成见{@link RsaUtils#generateSecretKey()}
     * @param ciphertext 密文，需要加密的内容，Base64加密后字符串
     * @return 明文
     */
    public static String decryptByPrivate(String privateKey, String ciphertext) {
        ParamAssert.isNotEmpty(privateKey, "RSA private key is required");
        ParamAssert.isNotEmpty(ciphertext, "ciphertext is required");
        try {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PrivateKey key = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(Base64.decodeBase64(ciphertext));
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }
}
