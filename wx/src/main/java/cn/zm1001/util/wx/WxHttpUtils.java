package cn.zm1001.util.wx;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * @Desc 微信HTTP工具类
 * @Author Dongd_Zhou
 */
@Slf4j
public class WxHttpUtils {
    /**
     * 上传文件到微信服务器
     *
     * @param url  上传地址
     * @param file 上传文件
     * @return 响应消息
     */
    public static String upload2WX(String url, File file) {
        OutputStream out = null;
        DataInputStream in = null;
        BufferedReader reader = null;

        try {
            /* 第一部分 */
            URL urlObj = new URL(url);
            // 连接
            HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

            /* 设置关键值 */
            con.setRequestMethod("POST"); // 以Post方式提交表单，默认get方式
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false); // post方式不能使用缓存

            // 设置请求头信息
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");

            // 设置边界
            String boundary = "----------" + System.currentTimeMillis();
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            // 请求正文信息
            // 第一部分：
            String sb = "--" + // 必须多两道线
                    boundary +
                    "\r\n" +
                    "Content-Disposition: form-data;name=\"media\";filelength=\"" + file.length() + ";filename=\"" + file.getName() + "\"\r\n" +
                    "Content-Type:application/octet-stream\r\n\r\n";
            byte[] head = sb.getBytes(StandardCharsets.UTF_8);

            // 获得输出流
            out = new DataOutputStream(con.getOutputStream());
            // 输出表头
            out.write(head);

            // 文件正文部分
            // 把文件已流文件的方式 推入到url中
            in = new DataInputStream(new FileInputStream(file));
            int bytes;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            in.close();

            // 结尾部分
            byte[] foot = ("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8);// 定义最后数据分隔线

            out.write(foot);
            out.flush();
            StringBuilder builder = new StringBuilder();
            // 定义BufferedReader输入流来读取URL的响应
            reader = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
            return builder.toString();
        } catch (Exception e) {
            log.error("#upload2WX# ## ## 提交至微信服务器异常", e);
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("#upload2WX# ## ## DataInputStream close exception", e);
                }
            }
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("#upload2WX# ## ## OutputStream close exception", e);
                }
            }
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("#upload2WX# ## ## BufferedReader close exception", e);
                }
            }
        }
        return null;
    }

    /**
     * 上传视频到微信服务器
     *
     * @param url          上传地址
     * @param file         上传文件
     * @param title        标题
     * @param introduction 描述信息
     * @return 响应消息
     */
    public static String uploadVideo2WX(String url, File file, String title, String introduction) {
        OutputStream out = null;
        DataInputStream in = null;
        BufferedReader reader = null;

        try {
            /* 第一部分 */
            URL urlObj = new URL(url);
            // 连接
            HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

            /* 设置关键值 */
            con.setRequestMethod("POST"); // 以Post方式提交表单，默认get方式
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false); // post方式不能使用缓存

            // 设置请求头信息
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");

            // 设置边界
            String boundary = "----------" + System.currentTimeMillis();
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            // 请求正文信息
            // 第一部分：
            StringBuilder sb = new StringBuilder();
            sb.append("--"); // 必须多两道线
            sb.append(boundary);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"media\";filelength=\"").append(file.length()).append(";filename=\"").append(file.getName()).append("\"\r\n");
            sb.append("Content-Type:video/mp4\r\n\r\n");

            byte[] head = sb.toString().getBytes(StandardCharsets.UTF_8);

            // 获得输出流
            out = new DataOutputStream(con.getOutputStream());
            // 输出表头
            out.write(head);

            // 文件正文部分
            // 把文件已流文件的方式 推入到url中
            in = new DataInputStream(new FileInputStream(file));
            int bytes;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            in.close();

            sb = new StringBuilder();
            sb.append("--"); // 必须多两道线
            sb.append(boundary);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"description\";\r\n");
            sb.append(String.format("{\"title\":\"%s\", \"introduction\":\"%s\"}", title, introduction));
            byte[] description = sb.toString().getBytes(StandardCharsets.UTF_8);
            // 输出描述信息
            out.write(description);

            // 结尾部分
            byte[] foot = ("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8);// 定义最后数据分隔线

            out.write(foot);
            out.flush();
            StringBuilder builder = new StringBuilder();
            // 定义BufferedReader输入流来读取URL的响应
            reader = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
            return builder.toString();
        } catch (Exception e) {
            log.error("#uploadVideo2WX# ## ## 提交至微信服务器异常", e);
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("#uploadVideo2WX# ## ## DataInputStream close exception", e);
                }
            }
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("#uploadVideo2WX# ## ## OutputStream close exception", e);
                }
            }
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("#uploadVideo2WX# ## ## BufferedReader close exception", e);
                }
            }
        }
        return null;
    }

}
