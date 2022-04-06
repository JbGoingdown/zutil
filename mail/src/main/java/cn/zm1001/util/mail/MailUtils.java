package cn.zm1001.util.mail;

import cn.zm1001.util.mail.exception.MailException;
import cn.zm1001.util.mail.pojo.MailAuthenticator;
import cn.zm1001.util.mail.pojo.MailInfo;
import com.sun.mail.util.MailSSLSocketFactory;
import lombok.extern.slf4j.Slf4j;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
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
     * 发送邮件
     * @param mailInfo 邮件信息
     * @return 成功或失败
     */
    public static boolean send(MailInfo mailInfo) {
        try {
            // 判断是否需要身份认证
            MailAuthenticator authenticator = null;
            if (mailInfo.isValidate()) {
                authenticator = new MailAuthenticator(mailInfo.getFromAddress(), mailInfo.getPassword());
            }

            Properties properties = new Properties();
            // 设置邮件服务器
            properties.put("mail.transport.protocol", "smtp");
            properties.put("mail.smtp.host", mailInfo.getHost());
            properties.put("mail.smtp.port", mailInfo.getPort());
            properties.put("mail.smtp.auth", mailInfo.isValidate());
//            properties.put("mail.debug", "true");

            if (mailInfo.isSsl()) {
                properties.put("mail.smtp.ssl.enable", "true");
                MailSSLSocketFactory sf = new MailSSLSocketFactory();
                sf.setTrustAllHosts(true);
                properties.put("mail.smtp.ssl.socketFactory", sf);
            }

            // 根据邮件发送的属性和密码验证器构造一个发送邮件的session
            Session session = Session.getInstance(properties, authenticator);
            // 根据session创建一个邮件消息
            Message mailMessage = new MimeMessage(session);
            // 防止邮件被当然垃圾邮件处理，披上Outlook的马甲
            mailMessage.addHeader("X-Mailer", "Microsoft Outlook Express 6.00.2900.2869");
            // 创建邮件发送者地址
            Address from = new InternetAddress(mailInfo.getFromAddress());
            mailMessage.setFrom(from);
            // 创建邮件的接收者地址，并设置到邮件消息中
            String[] toEmails = mailInfo.getToAddress().split(",");
            InternetAddress[] to = new InternetAddress[toEmails.length];
            for (int i = 0, length = toEmails.length; i < length; i++) {
                to[i] = new InternetAddress(toEmails[i]);
            }
            mailMessage.addRecipients(Message.RecipientType.TO, to);
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
            return true;
        } catch (Exception e) {
            throw new MailException(e);
        }
    }
}
