/**
 * 数字签名：带有密钥（公钥，私钥）的消息摘要算法。私钥用于签名，公钥用于验证
 * <pre>
 *     RSA：目前最有影响力的公钥加密算法，它能够抵抗到目前为止已知的绝大多数密码攻击，已被ISO推荐为公钥数据加密标准；主要包括两类：MD、SHA
 *     DSA：是Schnorr和ElGamal签名算法的变种，被美国NIST作为DSfS(DigitalSignature Standard)；基于整数有限域离散对数难题的，其安全性与RSA相比差不多
 *     ECDSA：椭圆曲线数字签名算法（Elliptic Curve Digital Signatrue Algorithm）；特点：速度快，强度高，签名短
 * </pre>
 */
package cn.zm1001.util.crypto.signature;
