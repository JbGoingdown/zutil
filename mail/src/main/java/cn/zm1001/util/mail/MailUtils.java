package cn.zm1001.util.mail;

import cn.zm1001.util.mail.pojo.MailAuthenticator;
import cn.zm1001.util.mail.pojo.MailInfo;
import com.sun.mail.util.MailSSLSocketFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @Desc 邮件工具类
 * @Author Dongd_Zhou
 */
@Slf4j
public class MailUtils {

    /**
     * 发送邮件(javax.mail)
     *
     * @param mailInfo 邮件信息
     * @return 成功或失败
     */
    public static boolean send(MailInfo mailInfo) {
        boolean flag = false;
        try {
            Properties properties = new Properties();
            properties.put("mail.debug", mailInfo.isDebug());
            // 设置邮件服务器
            properties.put("mail.transport.protocol", "smtp");
            properties.put("mail.smtp.host", mailInfo.getHost());
            properties.put("mail.smtp.port", mailInfo.getPort());

            if (mailInfo.isSsl()) {
                properties.put("mail.smtp.ssl.enable", "true");
                MailSSLSocketFactory sf = new MailSSLSocketFactory();
                sf.setTrustAllHosts(true);
                properties.put("mail.smtp.ssl.socketFactory", sf);
            }

            properties.put("mail.smtp.auth", mailInfo.isAuth());

            // 判断是否需要身份认证
            MailAuthenticator authenticator = null;
            if (mailInfo.isAuth()) {
                authenticator = new MailAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
            }

            // 根据邮件发送的属性和密码验证器构造一个发送邮件的session
            Session session = Session.getInstance(properties, authenticator);
            // 根据session创建一个邮件消息
            Message mailMessage = new MimeMessage(session);
            // 防止邮件被当然垃圾邮件处理，披上Outlook的马甲
            mailMessage.addHeader("X-Mailer", "Microsoft Outlook Express 6.00.2900.2869");
            // 创建邮件发送者地址
            mailMessage.setFrom(new InternetAddress(mailInfo.getFromAddress()));
            // 创建邮件的接收者地址，并设置到邮件消息中
            String[] toEmails = mailInfo.getToAddress().split(",");
            InternetAddress[] to = new InternetAddress[toEmails.length];
            for (int i = 0, length = toEmails.length; i < length; i++) {
                to[i] = new InternetAddress(toEmails[i]);
            }
            mailMessage.addRecipients(RecipientType.TO, to);

            // 设置抄送人
            if (null != mailInfo.getCcAddress() && 0 != mailInfo.getCcAddress().length()) {
                final String[] ccEmails = mailInfo.getCcAddress().split(",");
                InternetAddress[] cc = new InternetAddress[ccEmails.length];
                for (int i = 0, length = ccEmails.length; i < length; i++) {
                    cc[i] = new InternetAddress(ccEmails[i]);
                }
                mailMessage.addRecipients(RecipientType.CC, cc);
            }

            // 设置密送人
            if (null != mailInfo.getBccAddress() && 0 != mailInfo.getBccAddress().length()) {
                final String[] bccEmails = mailInfo.getBccAddress().split(",");
                InternetAddress[] bcc = new InternetAddress[bccEmails.length];
                for (int i = 0, length = bccEmails.length; i < length; i++) {
                    bcc[i] = new InternetAddress(bccEmails[i]);
                }
                mailMessage.addRecipients(RecipientType.BCC, bcc);
            }

            // 设置邮件消息的主题
            mailMessage.setSubject(mailInfo.getSubject());
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());

            // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
            Multipart mainPart = new MimeMultipart();
            // 创建一个包含HTML内容的MimeBodyPart
            BodyPart html = new MimeBodyPart();
            // 设置HTML内容
            html.setContent(mailInfo.getContent(), "text/html; charset=UTF-8");
            mainPart.addBodyPart(html);

            // 为邮件添加附件
            List<String> attaches = mailInfo.getAttaches();
            if (null != attaches && !attaches.isEmpty()) {
                // 存放邮件附件的MimeBodyPart
                MimeBodyPart attachment;
                File file;

                for (String attach : attaches) {
                    attachment = new MimeBodyPart();
                    // 根据附件文件创建文件数据源
                    file = new File(attach);
                    FileDataSource fds = new FileDataSource(file);
                    attachment.setDataHandler(new DataHandler(fds));
                    // 为附件设置文件名
                    attachment.setFileName(file.getName());
                    mainPart.addBodyPart(attachment);
                }
            }
            // 将MiniMultipart对象设置为邮件内容
            mailMessage.setContent(mainPart);
            // 发送邮件
            Transport.send(mailMessage);
            flag = true;
        } catch (Exception e) {
            log.error("#send# #exception# ## send email error, to: {}, subject: {}, msg: {}",
                    mailInfo.getToAddress(), mailInfo.getSubject(), mailInfo.getContent(), e);
        } finally {
            log.info("#send# #result# ## send email result: {}, to: {}, subject: {}, msg: {}", flag,
                    mailInfo.getToAddress(), mailInfo.getSubject(), mailInfo.getContent());
        }
        return flag;
    }

    /**
     * 设置通用信息
     *
     * @param mail     邮件
     * @param mailInfo 邮件信息
     */
    private static void setCommonMailInfo(Email mail, MailInfo mailInfo) throws EmailException {
        // Debug模式
        mail.setDebug(mailInfo.isDebug());
        // 字符集
        mail.setCharset(StandardCharsets.UTF_8.name());
        // 发送时间
        mail.setSentDate(new Date());
        // 邮件服务器
        mail.setHostName(mailInfo.getHost());
        // 支持SSL/TLS
        mail.setSSLOnConnect(mailInfo.isSsl());
        if (mailInfo.isSsl()) {
            mail.setSslSmtpPort(mailInfo.getPort());
        } else {
            mail.setSmtpPort(Integer.parseInt(mailInfo.getPort()));
        }
        // 账号验证
        if (mailInfo.isAuth()) {
            mail.setAuthentication(mailInfo.getUserName(), mailInfo.getPassword());
        }
        // 发送人
        mail.setFrom(mailInfo.getFromAddress());
        // 接收人
        mail.addTo(mailInfo.getToAddress().split(","));
        // 抄送人
        if (null != mailInfo.getCcAddress() && 0 != mailInfo.getCcAddress().length()) {
            mail.addCc(mailInfo.getCcAddress().split(","));
        }
        // 密送人
        if (null != mailInfo.getBccAddress() && 0 != mailInfo.getBccAddress().length()) {
            mail.addBcc(mailInfo.getBccAddress().split(","));
        }
        // 主题
        mail.setSubject(mailInfo.getSubject());
    }

    /**
     * 发送文本邮件
     *
     * @param mailInfo 邮件信息
     * @return 成功或失败
     */
    public static boolean sendText(MailInfo mailInfo) {
        boolean flag = false;
        SimpleEmail mail = new SimpleEmail();
        try {
            // 设置通用信息
            setCommonMailInfo(mail, mailInfo);
            // 设置邮件内容
            mail.setMsg(mailInfo.getContent());
            mail.send();
            flag = true;
        } catch (Exception e) {
            log.error("#sendText# #exception# ## send email error, to: {}, subject: {}, msg: {}",
                    mailInfo.getToAddress(), mailInfo.getSubject(), mailInfo.getContent(), e);
        } finally {
            log.info("#sendText# #result# ## send email result: {}, to: {}, subject: {}, msg: {}", flag,
                    mailInfo.getToAddress(), mailInfo.getSubject(), mailInfo.getContent());
        }
        return flag;
    }

    /**
     * 发送网页邮件
     *
     * @param mailInfo 邮件信息
     * @return 成功或失败
     */
    public static boolean sendHtml(MailInfo mailInfo) {
        boolean flag = false;
        HtmlEmail mail = new HtmlEmail();
        try {
            // 设置通用信息
            setCommonMailInfo(mail, mailInfo);
            // 设置邮件内容
            mail.setHtmlMsg(mailInfo.getContent());
            mail.send();
            flag = true;
        } catch (Exception e) {
            log.error("#sendHtml# #exception# ## send email error, to: {}, subject: {}, msg: {}",
                    mailInfo.getToAddress(), mailInfo.getSubject(), mailInfo.getContent(), e);
        } finally {
            log.info("#sendHtml# #result# ## send email result: {}, to: {}, subject: {}, msg: {}", flag,
                    mailInfo.getToAddress(), mailInfo.getSubject(), mailInfo.getContent());
        }
        return flag;
    }

    /**
     * 发送附件邮件
     *
     * @param mailInfo 邮件信息
     * @return 成功或失败
     */
    public static boolean sendAttach(MailInfo mailInfo) {
        boolean flag = false;
        MultiPartEmail mail = new MultiPartEmail();
        try {
            // 设置通用信息
            setCommonMailInfo(mail, mailInfo);
            // 设置邮件内容
            mail.setMsg(mailInfo.getContent());
            // 设置附件
            if (null != mailInfo.getAttaches() && !mailInfo.getAttaches().isEmpty()) {
                EmailAttachment attachment;
                for (String attach : mailInfo.getAttaches()) {
                    attachment = new EmailAttachment();
                    attachment.setPath(attach);
                    mail.attach(attachment);
                }
            }
            mail.send();
            flag = true;
        } catch (Exception e) {
            log.error("#sendAttach# #exception# ## send email error, to: {}, subject: {}, msg: {}",
                    mailInfo.getToAddress(), mailInfo.getSubject(), mailInfo.getContent(), e);
        } finally {
            log.info("#sendAttach# #result# ## send email result: {}, to: {}, subject: {}, msg: {}", flag,
                    mailInfo.getToAddress(), mailInfo.getSubject(), mailInfo.getContent());
        }
        return flag;
    }
}
