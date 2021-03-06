package cn.zm1001.util.mail.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @Desc 邮件信息
 * @Author Dongd_Zhou
 */
@Data
@Accessors(chain = true)
public class MailInfo {
    /** 调试 */
    private boolean debug = false;
    /** 邮件服务器 */
    private String host;
    /** 邮件服务端口号 */
    private String port;
    /** 是否启用SSL */
    private boolean ssl = false;
    /** 是否验证账号 */
    private boolean auth = false;
    /** 验证账号,为空取值fromAddress */
    private String userName;
    /** 验证账号密钥 */
    private String password;
    /** 发送邮件邮箱 */
    private String fromAddress;
    /** 接收者 */
    private String toAddress;
    /** 抄送者 */
    private String ccAddress;
    /** 密送者 */
    private String bccAddress;
    /** 主题 */
    private String subject;
    /** 邮件内容 */
    private String content;

    /** 附件 */
    private List<String> attaches = new ArrayList<>();

    public String getUserName() {
        if (null != userName && 0 != userName.length()) {
            return userName;
        }
        return fromAddress;
    }
}
