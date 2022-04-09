package cn.zm1001.util.crypto.asymmetric;

import cn.zm1001.util.crypto.exception.CryptoException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.Validate;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

/**
 * @Desc DH 加解密
 * @Author Dongd_Zhou
 * <pre>
 *     |--------------------|----------|----------|----------|----------|
 *     |密钥长度             |默认       |工作模式    |填充方式   |实现方      |
 *     |--------------------|----------|----------|----------|----------|
 *     |512~1024(64倍数)     |1024      |无        |无        |JDK       |
 *     |--------------------|----------|----------|----------|----------|
 * </pre>
 *
 * <pre>
 *     1.发送方构建公钥私钥
 *     2.发送方发布公钥
 *     3.接收方通过发送发公钥构建公钥私钥
 *     4.接收方发布公钥
 *     5.接收方依据发送方公钥和己方（接收方）的公钥私钥构建加解密密钥
 *     6.发送方依据接收方公钥和己方（发送方）的公钥私钥构建加解密密钥
 * </pre>
 *
 * <pre>
 *     java.security.NoSuchAlgorithmException: Unsupported secret key algorithm: DES 错误
 *     由于JDK版本不同，在Java 8 update 161版本以后就会出现此问题，根本原因还是DH密钥长度至少为512位，而DES算法密钥没有这么长，密钥长度不一致引起的。
 *     解决方法：配置JVM的系统变量：-Djdk.crypto.KeyAgreement.legacyKDF=true
 * </pre>
 */
public class DHUtils {
    private static final String ALGORITHM_DH = "DH";
    private static final String ALGORITHM_DES = "DES";

    /**
     * 发送发构建公钥私钥
     * <p>需要将密钥中的公钥发送给接收方</p>
     *
     * @return 发送方密钥
     */
    public static KeyPair generateSenderKey() {
        try {
            KeyPairGenerator senderKeyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_DH);
            senderKeyPairGenerator.initialize(512);
            return senderKeyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 根据发送方的公钥构建接收方的公私钥
     * <p>需要将密钥中的公钥发送给发送方</p>
     *
     * @param senderPublicKey 发送方公钥
     * @return 接收方密钥
     */
    public static KeyPair generateReceiverKey(byte[] senderPublicKey) {
        try {
            KeyFactory receiverKeyFactory = KeyFactory.getInstance(ALGORITHM_DH);
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(senderPublicKey);
            PublicKey receiverPublicKey = receiverKeyFactory.generatePublic(x509EncodedKeySpec);
            DHParameterSpec dhParameterSpec = ((DHPublicKey) receiverPublicKey).getParams();
            KeyPairGenerator receiverKeyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_DH);
            receiverKeyPairGenerator.initialize(dhParameterSpec);
            return receiverKeyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 使用己方的公钥私钥与对方的公钥构建对称密钥
     *
     * @param keyPair        己方的公钥私钥
     * @param publicKeyBytes 对方的公钥
     * @return DES密钥
     */
    public static SecretKey generateSecretKey(KeyPair keyPair, byte[] publicKeyBytes) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_DH);
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
            KeyAgreement keyAgreement = KeyAgreement.getInstance(ALGORITHM_DH);
            keyAgreement.init(keyPair.getPrivate());
            keyAgreement.doPhase(publicKey, true);
            return keyAgreement.generateSecret(ALGORITHM_DES);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 加密
     *
     * @param desKey    DES密钥
     * @param plaintext 明文，需要加密的内容
     * @return 密文Base64加密字符串
     */
    public static String encrypt(SecretKey desKey, String plaintext) {
        Validate.notNull(desKey, "dh secret key is required");
        Validate.notEmpty(plaintext, "plaintext is required");
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            cipher.init(Cipher.ENCRYPT_MODE, desKey);
            byte[] result = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(result);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 解密
     *
     * @param desKey     DES密钥
     * @param ciphertext 密文，需要解密的内容（Base64后的字符串）
     * @return 明文
     */
    public static String decrypt(SecretKey desKey, String ciphertext) {
        Validate.notNull(desKey, "dh secret key is required");
        Validate.notEmpty(ciphertext, "ciphertext is required");
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            cipher.init(Cipher.DECRYPT_MODE, desKey);
            byte[] result = cipher.doFinal(Base64.decodeBase64(ciphertext));
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }
}
