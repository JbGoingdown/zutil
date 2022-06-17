package cn.zm1001.util.common.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Dongd_Zhou
 * @desc Xml数据的序列化与反序列化
 * 在属性上添加注解
 * <pre>
 * @XmlJavaTypeAdapter(AdapterCDATA.class)
 * private String fieldName;
 * </pre>
 */
public class AdapterCDATA extends XmlAdapter<String, String> {
    @Override
    public String marshal(String value) throws Exception {
        return "<![CDATA[" + value + "]]>";
    }

    @Override
    public String unmarshal(String value) throws Exception {
        return value;
    }
}
