package cn.zm1001.util.crypto.signature;

import cn.zm1001.util.crypto.exception.CryptoException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.Validate;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @Desc DSA 数据签名
 * @Author Dongd_Zhou
 *
 * <pre>
 *     |-------------------------|--------------------|----------|----------|
 *     |算法                      |密钥长度             |默认       |实现方     |
 *     |-------------------------|--------------------|----------|----------|
 *     |SHA1withDSA              |512~1024(64倍数)     |1024      |JDK       |
 *     |-------------------------|                    |          |----------|
 *     |SHA224withDSA            |                    |          |BC        |
 *     |SHA256withDSA            |                    |          |          |
 *     |SHA384withDSA            |                    |          |          |
 *     |SHA512withDSA            |                    |          |          |
 *     |-------------------------|--------------------|----------|----------|
 * </pre>
 */
public class DsaSignUtils {
    private static final String ALGORITHM_DSA = "DSA";
    private static final String ALGORITHM_SIGN = "SHA1WithDSA";
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
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_DSA);
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
     * 签名（私钥签名）
     *
     * @param privateKey 私钥，Base64加密字符串，生成见{@link DsaSignUtils#generateSecretKey()}
     * @param content    需要签名的内容
     * @return 签名结果
     */
    public static String sign(String privateKey, String content) {
        Validate.notEmpty(privateKey, "DSA sign private key is required");
        Validate.notEmpty(content, "content to be signed is required");
        try {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_DSA);
            PrivateKey key = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            Signature signature = Signature.getInstance(ALGORITHM_SIGN);
            signature.initSign(key);
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            byte[] result = signature.sign();
            return Hex.encodeHexString(result);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 签名校验（公钥验证）
     *
     * @param publicKey 公钥，Base64加密字符串，生成见{@link DsaSignUtils#generateSecretKey()}
     * @param content   需要校验签名的内容
     * @param sign      待校验的签名
     * @return 校验结果，true：验证通过，false：验证不通过
     */
    public static boolean verify(String publicKey, String content, String sign) {
        Validate.notEmpty(publicKey, "DSA sign public key is required");
        Validate.notEmpty(content, "content to be signed is required");
        Validate.notEmpty(sign, "sign to be verified is required");
        try {
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_DSA);
            PublicKey key = keyFactory.generatePublic(x509EncodedKeySpec);
            Signature signature = Signature.getInstance(ALGORITHM_SIGN);
            signature.initVerify(key);
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Hex.decodeHex(sign));
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }
}
