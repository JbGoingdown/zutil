package cn.zm1001.util.crypto.signature;

import cn.zm1001.util.crypto.asymmetric.RsaUtils;
import cn.zm1001.util.crypto.exception.CryptoException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.Validate;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

/**
 * @Desc RSA 数字签名
 * @Author Dongd_Zhou
 *
 * <pre>
 *     |-------------------------|--------------------|----------|---------------|----------|
 *     |算法                      |密钥长度             |默认       |签名长度         |实现方     |
 *     |-------------------------|--------------------|----------|---------------|----------|
 *     |MD2withRSA               |512~65536(64倍数)    |1024      |与密钥长度相同    |JDK       |
 *     |MD5withRSA               |                    |          |               |          |
 *     |SHA1withRSA              |                    |          |               |          |
 *     |-------------------------|                    |----------|               |----------|
 *     |SHA224withRSA            |                    |2048      |               |BC        |
 *     |SHA256withRSA            |                    |          |               |          |
 *     |SHA384withRSA            |                    |          |               |          |
 *     |SHA512withRSA            |                    |          |               |          |
 *     |RIPEMD128withRSA         |                    |          |               |          |
 *     |RIPEMD160withRSA         |                    |          |               |          |
 *     |-------------------------|--------------------|----------|---------------|----------|
 * </pre>
 */
public class RsaSignUtils {
    private static final String ALGORITHM_RSA = "RSA";
    private static final String ALGORITHM_SIGN = "MD5withRSA";
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
        return RsaUtils.generateSecretKey();
    }

    /**
     * 签名（私钥签名）
     *
     * @param privateKey 私钥，Base64加密字符串，生成见{@link RsaSignUtils#generateSecretKey()}
     * @param content    需要签名的内容
     * @return 签名结果
     */
    public static String sign(String privateKey, String content) {
        Validate.notEmpty(privateKey, "RSA sign private key is required");
        Validate.notEmpty(content, "content to be signed is required");
        try {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
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
     * @param publicKey 公钥，Base64加密字符串，生成见{@link RsaSignUtils#generateSecretKey()}
     * @param content   需要校验签名的内容
     * @param sign      待校验的签名
     * @return 校验结果，true：验证通过，false：验证不通过
     */
    public static boolean verify(String publicKey, String content, String sign) {
        Validate.notEmpty(publicKey, "RSA sign public key is required");
        Validate.notEmpty(content, "content to be signed is required");
        Validate.notEmpty(sign, "sign to be verified is required");
        try {
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
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
