package cn.zm1001.util.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;

import javax.servlet.ServletRequest;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @Desc HTTP工具类
 * @Author Dongd_Zhou
 */
@Slf4j
public class HttpUtils {
    public static final String HEADER_ACCEPT = "accept";
    public static final String HEADER_CONNECTION = "connection";
    public static final String HEADER_USER_AGENT = "user-agent";
    public static final String HEADER_ACCEPT_CHARSET = "Accept-Charset";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";

    private static URLConnection getURLConnection(URL url, Map<String, String> headers) throws IOException {
        if (null == headers) {
            headers = new HashMap<>();
        }
        URLConnection conn = url.openConnection();
        conn.setRequestProperty(HEADER_ACCEPT, MapUtils.getString(headers, HEADER_ACCEPT, "*/*"));
        conn.setRequestProperty(HEADER_CONNECTION, MapUtils.getString(headers, HEADER_CONNECTION, "Keep-Alive"));
        conn.setRequestProperty(HEADER_USER_AGENT, MapUtils.getString(headers, HEADER_USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.75 Safari/537.36"));
        conn.setRequestProperty(HEADER_ACCEPT_CHARSET, MapUtils.getString(headers, HEADER_ACCEPT_CHARSET, "UTF-8"));
        conn.setRequestProperty(HEADER_CONTENT_TYPE, MapUtils.getString(headers, HEADER_CONTENT_TYPE, "text/html; charset=UTF-8"));
        headers.remove(HEADER_ACCEPT);
        headers.remove(HEADER_CONNECTION);
        headers.remove(HEADER_USER_AGENT);
        headers.remove(HEADER_ACCEPT_CHARSET);
        headers.remove(HEADER_CONTENT_TYPE);
        if (MapUtils.isNotEmpty(headers)) {
            for (Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        return conn;
    }

    /**
     * 发送GET请求
     *
     * @param url   请求URL
     * @param param 请求参数，请求参数name1=value1&name2=value2
     * @return 响应结果
     */
    public static String sendGet(String url, Map<String, Object> param) {
        return sendGet(url, param, null, StandardCharsets.UTF_8);
    }

    /**
     * 发送GET请求
     *
     * @param url     请求URL
     * @param param   请求参数
     * @param headers 请求头信息
     * @param charset 编码类型({@link StandardCharsets})
     * @return 响应结果
     */
    public static String sendGet(String url, Map<String, Object> param, Map<String, String> headers, Charset charset) {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {
            // 参数拼接
            StringBuilder params = new StringBuilder();
            if (MapUtils.isNotEmpty(param)) {
                for (Entry<String, Object> entry : param.entrySet()) {
                    if (params.length() > 0) {
                        params.append("&");
                    }
                    params.append(entry.getKey()).append("=").append(entry.getValue());
                }
            }

            String urlStr = StringUtils.isNotEmpty(params.toString()) ? url + "?" + param : url;
            log.info("#sendGet# #req# #{}# {} {}", url, param, headers);
            URL requestUrl = new URL(urlStr);
            URLConnection conn = getURLConnection(requestUrl, headers);
            conn.connect();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset.name()));

            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            log.info("#sendGet# #rsp# #{}# {}", url, result);
        } catch (Exception e) {
            log.error("#sendGet# #exception# #{}# {}", url, param, e);
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (Exception ex) {
                log.error("#sendGet# #exception# #{}# {}", url, param, ex);
            }
        }
        return result.toString();
    }

    /**
     * 发送POST请求
     *
     * @param url   请求URL
     * @param param 请求参数
     * @return 响应结果
     */
    public static String sendPost(String url, String param) {
        return sendPost(url, param, null, StandardCharsets.UTF_8);
    }

    /**
     * 发送POST请求
     *
     * @param url     请求URL
     * @param param   请求参数
     * @param headers 请求头信息
     * @param charset 编码类型({@link StandardCharsets})
     * @return 响应结果
     */
    public static String sendPost(String url, String param, Map<String, String> headers, Charset charset) {
        StringBuilder result = new StringBuilder();

        PrintWriter out = null;
        BufferedReader in = null;
        try {
            log.info("#sendPost# #req# #{}# {}", url, param);
            URL requestUrl = new URL(url);
            URLConnection conn = getURLConnection(requestUrl, headers);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            log.info("#sendPost# #rsp# #{}# {}", url, result);
        } catch (Exception e) {
            log.error("#sendPost# #exception# #{}# {}", url, param, e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                log.error("#sendPost# #exception# #{}# {}", url, param, ex);
            }
        }
        return result.toString();
    }

    /**
     * 获取请求体信息
     *
     * @param request HTTP请求
     * @return 请求体信息
     */
    public static String getRequestBody(ServletRequest request) {
        return getRequestBody(request, StandardCharsets.UTF_8);
    }

    /**
     * 获取请求体信息
     *
     * @param request HTTP请求
     * @param charset 请求体字符集
     * @return 请求体信息
     */
    public static String getRequestBody(ServletRequest request, Charset charset) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try (InputStream inputStream = request.getInputStream()) {
            reader = new BufferedReader(new InputStreamReader(inputStream, charset));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            log.warn("#getBodyContent# #exception# ##", e);
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.warn("#getBodyContent# #exception# ## close fail", e);
                }
            }
        }
        return sb.toString();
    }
}
