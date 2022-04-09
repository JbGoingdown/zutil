package cn.zm1001.util.common;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Desc 网路地址工具类
 * @Author Dongd_Zhou
 */
@Slf4j
public class WebUtils {
    /** IP地址查询 */
    private static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp";
    /** 未知地址 */
    public static final String UNKNOWN = "unknown";
    public static final String DEFAULT_IPV6 = "0:0:0:0:0:0:0:1";
    public static final String DEFAULT_IPV4 = "127.0.0.1";

    /**
     * 通过IP地址获取真实地址
     *
     * @param ip IPv4地址
     * @return 实际现实地址
     */
    public static String getRealAddressByIP(String ip) {
        // 内网地址
        if (internalIp(ip)) {
            return "内网IP";
        }
        try {
            final Map<String, Object> param = new HashMap<>();
            param.put("ip", ip);
            param.put("json", true);
            String rspStr = HttpUtils.sendGet(IP_URL, param, null, Charset.forName("GBK"));
            if (StringUtils.isEmpty(rspStr)) {
                log.error("#getRealAddressByIP# #rsp# #{}# 获取地理位置异常", ip);
                return UNKNOWN;
            }
            JSONObject obj = JSONObject.parseObject(rspStr);
            String region = obj.getString("pro");
            String city = obj.getString("city");
            return String.format("%s %s", region, city);
        } catch (Exception e) {
            log.error("#getRealAddressByIP# #rsp# #{}# 获取地理位置异常", ip, e);
        }
        return UNKNOWN;
    }

    /**
     * 获取请求来源IP
     *
     * @param request 请求信息
     * @return 请求来源IP
     */
    public static String getRequestIP(HttpServletRequest request) {
        if (null == request) {
            return UNKNOWN;
        }
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
            ip = request.getRemoteAddr();
        }
        return DEFAULT_IPV6.equals(ip) ? DEFAULT_IPV4 : ip;
    }

    public static boolean internalIp(String ip) {
        byte[] addr = textToNumericFormatV4(ip);
        return internalIp(addr) || DEFAULT_IPV4.equals(ip);
    }

    /**
     * 将IPv4地址转换成字节
     *
     * @param ip IPv4地址
     * @return byte 字节
     */
    private static byte[] textToNumericFormatV4(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return null;
        }

        byte[] bytes = new byte[4];
        String[] elements = ip.split("\\.", -1);
        try {
            long l;
            int i;
            switch (elements.length) {
                case 1:
                    l = Long.parseLong(elements[0]);
                    if ((l < 0L) || (l > 4294967295L)) {
                        return null;
                    }
                    bytes[0] = (byte) (int) (l >> 24 & 0xFF);
                    bytes[1] = (byte) (int) ((l & 0xFFFFFF) >> 16 & 0xFF);
                    bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 2:
                    l = Integer.parseInt(elements[0]);
                    if ((l < 0L) || (l > 255L)) {
                        return null;
                    }
                    bytes[0] = (byte) (int) (l & 0xFF);
                    l = Integer.parseInt(elements[1]);
                    if ((l < 0L) || (l > 16777215L)) {
                        return null;
                    }
                    bytes[1] = (byte) (int) (l >> 16 & 0xFF);
                    bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 3:
                    for (i = 0; i < 2; ++i) {
                        l = Integer.parseInt(elements[i]);
                        if ((l < 0L) || (l > 255L)) {
                            return null;
                        }
                        bytes[i] = (byte) (int) (l & 0xFF);
                    }
                    l = Integer.parseInt(elements[2]);
                    if ((l < 0L) || (l > 65535L)) {
                        return null;
                    }
                    bytes[2] = (byte) (int) (l >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 4:
                    for (i = 0; i < 4; ++i) {
                        l = Integer.parseInt(elements[i]);
                        if ((l < 0L) || (l > 255L)) {
                            return null;
                        }
                        bytes[i] = (byte) (int) (l & 0xFF);
                    }
                    break;
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return bytes;
    }

    /**
     * 判断是否内网
     *
     * @param addr IP地址Byte数组
     * @return 是否内网地址
     */
    private static boolean internalIp(byte[] addr) {
        if (Objects.isNull(addr) || addr.length < 2) {
            return true;
        }
        final byte b0 = addr[0];
        final byte b1 = addr[1];
        // 10.x.x.x
        final byte SECTION_1 = 0x0A;
        // 172.16-31.x.x
        final byte SECTION_2 = (byte) 0xAC;
        final byte SECTION_3 = (byte) 0x10;
        final byte SECTION_4 = (byte) 0x1F;
        // 192.168.x.x
        final byte SECTION_5 = (byte) 0xC0;
        final byte SECTION_6 = (byte) 0xA8;
        switch (b0) {
            case SECTION_1:
                return true;
            case SECTION_2:
                if (b1 >= SECTION_3 && b1 <= SECTION_4) {
                    return true;
                }
            case SECTION_5:
                if (b1 == SECTION_6) {
                    return true;
                }
            default:
                return false;
        }
    }

}
