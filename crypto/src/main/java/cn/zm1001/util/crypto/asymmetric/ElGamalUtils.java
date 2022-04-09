package cn.zm1001.util.crypto.asymmetric;

import cn.zm1001.util.crypto.exception.CryptoException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.Validate;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.DHParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @Desc ElGamal 加解密
 * @Author Dongd_Zhou
 *
 * <pre>
 *     |--------------------|----------|----------|------------------------------|----------|
 *     |密钥长度             |默认       |工作模式    |填充方式                       |实现方     |
 *     |--------------------|----------|----------|------------------------------|----------|
 *     |160~16384(8倍数)     |1024      |无、ECB    |NoPadding                     |BC        |
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
public class ElGamalUtils {
    private static final String ALGORITHM = "Elgamal";
    public static final String PRIVATE = "privateKey";
    public static final String PUBLIC = "publicKey";

    public static Map<String, String> generateSecretKey() {
        try {
            // 加入对BouncyCastle支持
            Security.addProvider(new BouncyCastleProvider());
            // 初始化发送方密钥
            AlgorithmParameterGenerator algorithmParameterGenerator = AlgorithmParameterGenerator.getInstance(ALGORITHM);
            // 初始化参数生成器
            algorithmParameterGenerator.init(256);
            // 生成算法参数
            AlgorithmParameters algorithmParameters = algorithmParameterGenerator.generateParameters();
            // 构建参数材料
            DHParameterSpec dhParameterSpec = algorithmParameters.getParameterSpec(DHParameterSpec.class);
            // 实例化密钥生成器
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            // 初始化密钥对生成器
            keyPairGenerator.initialize(dhParameterSpec, new SecureRandom());
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
     * 公钥加密，需私钥解密
     *
     * @param publicKey 私钥，Base64加密字符串，生成见{@link ElGamalUtils#generateSecretKey()}
     * @param plaintext 明文，需要加密的内容
     * @return 密文Base64加密字符串
     */
    public static String encrypt(String publicKey, String plaintext) {
        Validate.notEmpty(publicKey, "Elgamal public key is required");
        Validate.notEmpty(plaintext, "plaintext is required");
        try {
            // 加入对BouncyCastle支持
            Security.addProvider(new BouncyCastleProvider());
            // 初始化公钥
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
            // 实例化密钥工厂
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            // 产生公钥
            PublicKey key = keyFactory.generatePublic(x509EncodedKeySpec);
            // 数据加密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(result);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 私钥解密，需要公钥加密
     *
     * @param privateKey 私钥，Base64加密字符串，生成见{@link ElGamalUtils#generateSecretKey()}
     * @param ciphertext 密文，需要加密的内容，Base64加密后字符串
     * @return 明文
     */
    public static String decrypt(String privateKey, String ciphertext) {
        Validate.notEmpty(privateKey, "Elgamal private key is required");
        Validate.notEmpty(ciphertext, "ciphertext is required");
        try {
            // 加入对BouncyCastle支持
            Security.addProvider(new BouncyCastleProvider());
            // 初始化私钥
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
            // 实例化密钥工厂
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            // 产生私钥
            PrivateKey key = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            // 数据解密
            Cipher cipher5 = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher5.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher5.doFinal(Base64.decodeBase64(ciphertext));
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }
}
