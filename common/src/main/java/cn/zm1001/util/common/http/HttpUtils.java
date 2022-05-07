package cn.zm1001.util.common.http;

import cn.zm1001.util.common.JacksonUtils;
import cn.zm1001.util.common.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @Desc HTTP工具类
 * @Author Dongd_Zhou
 */
@Slf4j
public class HttpUtils {

    /**
     * 获取请求地址，提出URL参数
     *
     * @param url 请求URL
     * @return 请求地址
     */
    private static String getUrl(String url) {
        if (url.contains("?")) {
            int index = url.indexOf("?");
            return StringUtils.substring(url, 0, index);
        }
        return url;
    }

    /**
     * 设置头部请求
     *
     * @param request   请求
     * @param headerMap 头部
     */
    private static void setHeader(HttpMessage request, Map<String, String> headerMap) {
        if (MapUtils.isNotEmpty(headerMap)) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * GET请求
     *
     * @param url 请求地址及参数
     * @return 响应消息
     */
    public static String doGet(String url) {
        return doGetWithHeader(url, null);
    }

    /**
     * GET请求
     *
     * @param url 请求地址及参数
     * @return 响应消息
     */
    public static String doGetWithHeader(String url, Map<String, String> headerMap) {
        String content = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            // 设置头信息
            setHeader(httpGet, headerMap);

            // 执行请求
            CloseableHttpResponse response = httpClient.execute(httpGet);

            // 返回200
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                content = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
            }
        } catch (Exception e) {
            log.error("#get# #exception# #{}# {} {}", getUrl(url), url, JacksonUtils.toJson(headerMap), e);
        } finally {
            log.info("#get# #result# #{}# {} {} {}", getUrl(url), url, JacksonUtils.toJson(headerMap), content);
        }
        return content;
    }

    /**
     * POST请求，请求体转为XML数据
     *
     * @param url 请求地址
     * @param xml xml字符串
     * @return 响应消息
     */
    public static String doPostXml(String url, String xml) {
        String content = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "text/html;charset=UTF-8");
            // 解决中文乱码问题
            StringEntity stringEntity = new StringEntity(xml, Consts.UTF_8);
            stringEntity.setContentEncoding(Consts.UTF_8.name());
            httpPost.setEntity(stringEntity);

            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                content = EntityUtils.toString(httpResponse.getEntity(), Consts.UTF_8);
            }
        } catch (Exception e) {
            log.error("#postXml# #exception# #{}# {} {}", getUrl(url), url, xml, e);
        } finally {
            log.info("#postXml# #result# #{}# {} {} {}", getUrl(url), url, xml, content);
        }
        return content;
    }

    /**
     * 表单提交
     *
     * @param url      请求地址
     * @param paramMap 参数
     * @return 响应消息
     */
    public static String doPostForm(String url, Map<String, String> paramMap) {
        return doPostFormWithHeader(url, null, paramMap);
    }

    /**
     * 表单提交
     *
     * @param url       请求地址
     * @param headerMap 头部信息
     * @param paramMap  参数
     * @return 响应消息
     */
    public static String doPostFormWithHeader(String url, Map<String, String> headerMap, Map<String, String> paramMap) {
        String content = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            // 请求头部
            setHeader(httpPost, headerMap);
            // 请求体
            List<NameValuePair> formParams = new ArrayList<>();
            for (Entry<String, String> entry : paramMap.entrySet()) {
                formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(formParams, Consts.UTF_8));

            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

            // 返回200
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                content = EntityUtils.toString(httpResponse.getEntity(), Consts.UTF_8);
            }
        } catch (Exception e) {
            log.error("#post# #exception# #{}# {} {} {}", getUrl(url), url, JacksonUtils.toJson(headerMap), JacksonUtils.toJson(paramMap), e);
        } finally {
            log.info("#post# #result# #{}# {} {} {} {}", getUrl(url), url, JacksonUtils.toJson(headerMap), JacksonUtils.toJson(paramMap), content);
        }
        return content;
    }

    /**
     * POST请求，请求体转为JSON数据
     *
     * @param url      请求地址
     * @param paramMap 参数
     * @return 响应消息
     */
    public static String doPost(String url, Map<String, Object> paramMap) {
        return doPostWithHeader(url, null, paramMap);
    }

    /**
     * POST请求，请求体转为JSON数据
     *
     * @param url       请求地址
     * @param headerMap 头部信息
     * @param paramMap  参数
     * @return 响应消息
     */
    public static String doPostWithHeader(String url, Map<String, String> headerMap, Map<String, Object> paramMap) {
        String content = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);

            // 请求头部
            httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
            setHeader(httpPost, headerMap);

            // 请求体
            String json = JacksonUtils.toJson(paramMap);
            httpPost.setEntity(new StringEntity(json, Consts.UTF_8));

            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            // 返回200
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                content = EntityUtils.toString(httpResponse.getEntity(), Consts.UTF_8);
            }
        } catch (Exception e) {
            log.error("#post# #exception# #{}# {} {} {}", getUrl(url), url, JacksonUtils.toJson(headerMap), JacksonUtils.toJson(paramMap), e);
        } finally {
            log.info("#post# #result# #{}# {} {} {} {}", getUrl(url), url, JacksonUtils.toJson(headerMap), JacksonUtils.toJson(paramMap), content);
        }
        return content;
    }

    /**
     * POST请求，请求体为JSON数据
     *
     * @param url  地址
     * @param json 请求体（JSON）
     * @return 响应消息
     */
    public static String doPostJson(String url, String json) {
        return doPostJsonWithHeader(url, null, json);
    }

    /**
     * POST请求，请求体为JSON数据，含头部
     *
     * @param url       地址
     * @param headerMap 头部
     * @param json      请求体（JSON）
     * @return 响应消息
     */
    public static String doPostJsonWithHeader(String url, Map<String, String> headerMap, String json) {
        String content = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            // 请求头部
            httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
            setHeader(httpPost, headerMap);
            // 请求体
            httpPost.setEntity(new StringEntity(json, Consts.UTF_8));

            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

            // 返回200
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                content = EntityUtils.toString(httpResponse.getEntity(), Consts.UTF_8);
            }
        } catch (Exception e) {
            log.error("#post# #exception# #{}# {} {} {}", getUrl(url), url, JacksonUtils.toJson(headerMap), json, e);
        } finally {
            log.info("#post# #result# #{}# {} {} {} {}", getUrl(url), url, JacksonUtils.toJson(headerMap), json, content);
        }
        return content;
    }

    /**
     * PUT请求，请求体转为JSON数据
     *
     * @param url      请求地址
     * @param paramMap 参数
     * @return 响应消息
     */
    public static String doPut(String url, Map<String, Object> paramMap) {
        return doPutWithHeader(url, null, paramMap);
    }

    /**
     * PUT请求，请求体转为JSON数据
     *
     * @param url       请求地址
     * @param headerMap 头部
     * @param paramMap  参数
     * @return 响应消息
     */
    public static String doPutWithHeader(String url, Map<String, String> headerMap, Map<String, Object> paramMap) {
        String content = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut httpPut = new HttpPut(url);

            // 请求头部
            httpPut.setHeader("Content-Type", "application/json;charset=UTF-8");
            setHeader(httpPut, headerMap);

            // 请求体
            String json = JacksonUtils.toJson(paramMap);
            httpPut.setEntity(new StringEntity(json, Consts.UTF_8));

            CloseableHttpResponse httpResponse = httpClient.execute(httpPut);

            // 返回200
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                content = EntityUtils.toString(httpResponse.getEntity(), Consts.UTF_8);
            }
        } catch (Exception e) {
            log.error("#put# #exception# #{}# {} {} {}", getUrl(url), url, JacksonUtils.toJson(headerMap), JacksonUtils.toJson(paramMap), e);
        } finally {
            log.info("#put# #result# #{}# {} {} {} {}", getUrl(url), url, JacksonUtils.toJson(headerMap), JacksonUtils.toJson(paramMap), content);
        }
        return content;
    }

    /**
     * PUT请求，请求体为JSON数据
     *
     * @param url  地址
     * @param json 请求体（JSON）
     * @return 响应消息
     */
    public static String doPutJson(String url, String json) {
        return doPutJsonWithHeader(url, null, json);
    }

    /**
     * PUT请求，请求体为JSON体，含头部
     *
     * @param url       地址
     * @param headerMap 头部
     * @param json      请求体（JSON）
     * @return 响应消息
     */
    public static String doPutJsonWithHeader(String url, Map<String, String> headerMap, String json) {
        String content = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut httpPut = new HttpPut(url);

            // 请求头部
            httpPut.setHeader("Content-Type", "application/json;charset=UTF-8");
            setHeader(httpPut, headerMap);

            // 请求体
            httpPut.setEntity(new StringEntity(json, Consts.UTF_8));

            CloseableHttpResponse httpResponse = httpClient.execute(httpPut);

            // 返回200
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                content = EntityUtils.toString(httpResponse.getEntity(), Consts.UTF_8);
            }
        } catch (Exception e) {
            log.error("#put# #exception# #{}# {} {} {}", getUrl(url), url, JacksonUtils.toJson(headerMap), json, e);
        } finally {
            log.info("#put# #result# #{}# {} {} {} {}", getUrl(url), url, JacksonUtils.toJson(headerMap), json, content);
        }
        return content;
    }

    /**
     * Delete请求
     *
     * @param url 地址
     * @return 响应消息
     */
    public static String doDelete(String url) {
        return doDeleteWithHeader(url, null);
    }

    /**
     * Delete请求
     *
     * @param url       请求地址及参数
     * @param headerMap 头部
     * @return 响应消息
     */
    public static String doDeleteWithHeader(String url, Map<String, String> headerMap) {
        String content = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpDelete httpDel = new HttpDelete(url);
            // 设置头信息
            setHeader(httpDel, headerMap);

            // 执行请求
            CloseableHttpResponse httpResponse = httpClient.execute(httpDel);

            // 返回200
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                content = EntityUtils.toString(httpResponse.getEntity(), Consts.UTF_8);
            }
        } catch (Exception e) {
            log.error("#delete# #exception# #{}# {} {}", getUrl(url), url, JacksonUtils.toJson(headerMap), e);
        } finally {
            log.info("#delete# #result# #{}# {} {} {}", getUrl(url), url, JacksonUtils.toJson(headerMap), content);
        }
        return content;
    }

    /**
     * Delete请求，请求体转为JSON数据
     *
     * @param url      请求地址
     * @param paramMap 参数
     * @return 响应消息
     */
    public static String doDeleteWithBody(String url, Map<String, Object> paramMap) {
        return doDeleteWithBody(url, null, paramMap);
    }

    /**
     * Delete请求，请求体转为JSON数据
     *
     * @param url       请求地址
     * @param headerMap 头部
     * @param paramMap  参数
     * @return 响应消息
     */
    public static String doDeleteWithBody(String url, Map<String, String> headerMap, Map<String, Object> paramMap) {
        String content = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpDeleteWithBody httpDel = new HttpDeleteWithBody(url);

            // 请求头部
            httpDel.setHeader("Content-Type", "application/json;charset=UTF-8");
            setHeader(httpDel, headerMap);

            // 请求体
            String json = JacksonUtils.toJson(paramMap);
            httpDel.setEntity(new StringEntity(json, Consts.UTF_8));

            CloseableHttpResponse httpResponse = httpClient.execute(httpDel);

            // 返回200
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                content = EntityUtils.toString(httpResponse.getEntity(), Consts.UTF_8);
            }
        } catch (Exception e) {
            log.error("#delete# #exception# #{}# {} {} {}", getUrl(url), url, JacksonUtils.toJson(headerMap), JacksonUtils.toJson(paramMap), e);
        } finally {
            log.info("#delete# #result# #{}# {} {} {} {}", getUrl(url), url, JacksonUtils.toJson(headerMap), JacksonUtils.toJson(paramMap), content);
        }
        return content;
    }

    /**
     * Delete请求，请求体为JSON数据
     *
     * @param url  地址
     * @param json 请求体（JSON）
     * @return 响应消息
     */
    public static String doDeleteJsonWithBody(String url, String json) {
        return doDeleteJsonWithBody(url, null, json);
    }

    /**
     * Delete请求，请求体为JSON体，含头部
     *
     * @param url       地址
     * @param headerMap 头部
     * @param json      请求体（JSON）
     * @return 响应消息
     */
    public static String doDeleteJsonWithBody(String url, Map<String, String> headerMap, String json) {
        String content = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpDeleteWithBody httpDel = new HttpDeleteWithBody(url);

            // 请求头部
            httpDel.setHeader("Content-Type", "application/json;charset=UTF-8");
            setHeader(httpDel, headerMap);

            // 请求体
            httpDel.setEntity(new StringEntity(json, Consts.UTF_8));

            CloseableHttpResponse httpResponse = httpClient.execute(httpDel);

            // 返回200
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                content = EntityUtils.toString(httpResponse.getEntity(), Consts.UTF_8);
            }
        } catch (Exception e) {
            log.error("#delete# #exception# #{}# {} {} {}", getUrl(url), url, JacksonUtils.toJson(headerMap), json, e);
        } finally {
            log.info("#delete# #result# #{}# {} {} {} {}", getUrl(url), url, JacksonUtils.toJson(headerMap), json, content);
        }
        return content;
    }

    /**
     * 上传
     *
     * @param url  请求地址
     * @param file 上传文件
     * @return 响应消息
     */
    public static String upload(String url, File file) {
        return upload(url, file, null);
    }

    /**
     * 上传
     *
     * @param url      请求地址
     * @param file     上传文件
     * @param paramMap 参数
     * @return 响应消息
     */
    public static String upload(String url, File file, Map<String, String> paramMap) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", file);
        if (MapUtils.isNotEmpty(paramMap)) {
            for (Entry<String, String> entry : paramMap.entrySet()) {
                builder.addTextBody(entry.getKey(), entry.getValue());
            }
        }
        final HttpEntity dataEntity = builder.build();
        return upload(url, null, dataEntity);
    }

    /**
     * 上传
     *
     * @param url      请求地址
     * @param bytes    图片字节码
     * @param paramMap 参数
     * @return 响应消息
     */
    public static String upload(String url, byte[] bytes, Map<String, String> paramMap) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", bytes);
        if (MapUtils.isNotEmpty(paramMap)) {
            for (Entry<String, String> entry : paramMap.entrySet()) {
                builder.addTextBody(entry.getKey(), entry.getValue());
            }
        }
        final HttpEntity dataEntity = builder.build();
        return upload(url, null, dataEntity);
    }

    /**
     * 上传
     *
     * @param url      请求地址
     * @param is       图片流
     * @param paramMap 参数
     * @return 响应消息
     */
    public static String upload(String url, InputStream is, Map<String, String> paramMap) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", is);
        if (MapUtils.isNotEmpty(paramMap)) {
            for (Entry<String, String> entry : paramMap.entrySet()) {
                builder.addTextBody(entry.getKey(), entry.getValue());
            }
        }
        final HttpEntity dataEntity = builder.build();
        return upload(url, null, dataEntity);
    }

    /**
     * 上传
     *
     * @param url        请求地址
     * @param headerMap  头部
     * @param dataEntity 上传信息
     * @return 响应消息
     */
    public static String upload(String url, Map<String, String> headerMap, HttpEntity dataEntity) {
        String content = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            // 请求头部
            setHeader(httpPost, headerMap);
            // 请求体
            httpPost.setEntity(dataEntity);

            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

            // 返回200
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                content = EntityUtils.toString(httpResponse.getEntity(), Consts.UTF_8);
            }
        } catch (Exception e) {
            log.error("#upload# #exception# #{}# {} ", getUrl(url), url, e);
        } finally {
            log.info("#upload# #result# #{}# {} {}", getUrl(url), url, content);
        }
        return content;
    }
}
