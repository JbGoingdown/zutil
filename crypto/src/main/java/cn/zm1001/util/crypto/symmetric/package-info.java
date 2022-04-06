/**
 * 对称加密
 * <pre>
 *     安全等级：AES>3DES>DES
 *     DES：采用单钥密码系统的加密方法，同一个密钥可以同时用作信息的加密和解密，这种加密方法称为对称加密，也称为单密钥加密
 *     3DES：3重DES，是DES的一个分支；但由于安全性问题；且违反柯克霍夫原则，使用频率低，也称为Triple DES或DESede
 *     AES：DES的高级替代，也是目前使用最多的对称加密算法；DES有漏洞，所以，产生了3DES；3DES的效率比较低，所以产生了AES；目前还没有被破解
 *     PBE：结合了消息摘要算法和对称加密算法的优点；PBE是基于口令的加密，需要盐值；本质上是对DES/3DES/AES对称加密算法的包装，不是新的算法，不过也是最为牛逼的一种方式
 *     IDEA：International Data Encryption Algorithm，即：国际数据加密标准，使用长度为128位的密钥，数据块大小为64位
 * </pre>
 */
package cn.zm1001.util.crypto.symmetric;
