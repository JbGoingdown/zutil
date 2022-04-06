package cn.zm1001.util.crypto.asymmetric;

import org.junit.Assert;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.security.KeyPair;

/**
 * @Desc DH Crypto Test
 * @Author Dongd_Zhou
 */
public class DHUtilsTest {

    @Test
    public void testDH() {
        // 解决异常 java.security.NoSuchAlgorithmException: Unsupported secret key algorithm: DES
        System.setProperty("jdk.crypto.KeyAgreement.legacyKDF", "true");
        // 1.发送方构建公钥私钥
        KeyPair senderKeyPair = DHUtils.generateSenderKey();
        // 2.发送方发布公钥
        byte[] senderPublicKey = senderKeyPair.getPublic().getEncoded();
        // 3.接收方通过发送发公钥构建公钥私钥
        KeyPair receiverKeyPair = DHUtils.generateReceiverKey(senderPublicKey);
        // 4.接收方发布公钥
        byte[] receiverPublicKey = receiverKeyPair.getPublic().getEncoded();
        // 5.接收方依据发送方公钥和己方（接收方）的公钥私钥构建DES密钥
        SecretKey receiverDesKey = DHUtils.generateSecretKey(receiverKeyPair, senderPublicKey);
        // 6.发送方依据接收方公钥和己方（发送方）的公钥私钥构建DES密钥
        SecretKey senderDesKey = DHUtils.generateSecretKey(senderKeyPair, receiverPublicKey);

        Assert.assertEquals(receiverDesKey, senderDesKey);
        String plainText = "zm1001.cn";
        // 加密
        String cipherText = DHUtils.encrypt(senderDesKey, plainText);
        // 解密
        String result = DHUtils.decrypt(receiverDesKey, cipherText);
        Assert.assertEquals(plainText, result);
    }

}
