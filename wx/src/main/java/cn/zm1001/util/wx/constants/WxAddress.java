package cn.zm1001.util.wx.constants;

/**
 * @Desc 微信服务地址
 * @Author Dongd_Zhou
 */
public enum WxAddress {
    WX_API("api.weixin.qq.com"),
    WX_API2("api2.weixin.qq.com"),
    WX_API_SH("sh.api.weixin.qq.com"),
    WX_API_SZ("sz.api.weixin.qq.com"),
    WX_API_HK("hk.api.weixin.qq.com");

    private final String url;

    WxAddress(String url) {
        this.url = url;
    }

    public String url() {
        return url;
    }
}
