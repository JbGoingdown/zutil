/**
 * 非对称加密
 * <p>
 *     非对称加密算法的秘钥分为公钥和私钥，公钥和私钥通常情况下是成对出现的，使用公钥加密的数据只有和它对应的私钥才能解开，反之亦然
 * </p>
 * <pre>
 *      DH：即 Diffie-Hellman，秘钥交换算法
 *      RSA：基于大数因子分解，支持公钥加密、私钥解密；支持私钥加密、公钥解密
 *      ElGamal：基于离散对数，只支持公钥加密、私钥解密
 *      ECC：即 Elliptical curve Cryptograhpy，椭圆曲线加密
 * </pre>
 */
package cn.zm1001.util.crypto.asymmetric;
