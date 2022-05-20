package cn.zm1001.util.common.xml;

import cn.zm1001.util.common.StringUtils;
import cn.zm1001.util.common.exception.XmlException;
import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Dongd_Zhou
 * @desc XML工具类
 */
@Slf4j
public class XmlUtils {
    private static final String XML_CONTENT = "<?xml version=\"1.0\" encoding=\"%s\"?>%s";

    private static final XPath xpath = XPathFactory.newInstance().newXPath();

    private XmlUtils() {
        throw new UnsupportedOperationException();
    }


    /** 将对象转为XML,默认字符集UTF-8 */
    public static String format(Object obj) {
        return format(obj, StandardCharsets.UTF_8.name(), obj.getClass().getSimpleName());
    }

    /** 将对象转为XML,自定义字符集 */
    public static String format(Object obj, String encoding) {
        return format(obj, encoding, obj.getClass().getSimpleName());
    }

    /** 将对象转为XML,自定义字符集,自定义别名 */
    public static String format(Object obj, String encoding, String alias) {
        if (null == obj) {
            return null;
        }
        final String charset = StringUtils.defaultIfEmpty(encoding, StandardCharsets.UTF_8.name());
        XStream xStream = new XStream(new DomDriver(charset, new XmlFriendlyNameCoder("$", "_")));
        xStream.alias(alias, obj.getClass());
        String xml = xStream.toXML(obj);
        return String.format(XML_CONTENT, charset, xml);
    }

    /** 将对象转为XML,默认字符集UTF-8 */
    public static String format(Object obj, Map<String, Class<?>> map) {
        return format(obj, map, StandardCharsets.UTF_8.name());
    }

    /** 将对象转为XML */
    public static String format(Object obj, Map<String, Class<?>> map, String encoding) {
        if (null == obj) {
            return null;
        }
        final String charset = StringUtils.defaultIfEmpty(encoding, StandardCharsets.UTF_8.name());
        XStream xStream = new XStream(new DomDriver(charset, new XmlFriendlyNameCoder("$", "_")));
        if (null != map && !map.isEmpty()) {
            for (Entry<String, Class<?>> entry : map.entrySet()) {
                xStream.alias(entry.getKey(), entry.getValue());
            }
        }
        String xml = xStream.toXML(obj);
        return String.format(XML_CONTENT, charset, xml);
    }

    /** 将XML转为对象,默认字符集UTF-8 */
    public static <T> T parse(String xml, Class<T> clazz) {
        return parse(xml, clazz, StandardCharsets.UTF_8.name(), clazz.getSimpleName());
    }

    /** 将XML转为对象,自定义字符集 */
    public static <T> T toBean(String xml, Class<T> clazz, String encoding) {
        return parse(xml, clazz, encoding, clazz.getSimpleName());
    }

    /** 将XML转为对象,自定义字符集,自定义别名 */
    @SuppressWarnings("unchecked")
    public static <T> T parse(String xml, Class<T> clazz, String encoding, String alias) {
        if (null == xml || 0 == xml.length()) {
            return null;
        }
        final String charset = StringUtils.defaultIfEmpty(encoding, StandardCharsets.UTF_8.name());
        XStream xStream = new XStream(new DomDriver(charset));
        xStream.alias(alias, clazz);
        return (T) xStream.fromXML(xml);
    }

    /** 将XML转为对象,默认字符集UTF-8 */
    public static <T> T parse(String xml, Map<String, Class<?>> map) {
        return parse(xml, map, StandardCharsets.UTF_8.name());
    }

    /** 将XML转为对象 */
    @SuppressWarnings("unchecked")
    public static <T> T parse(String xml, Map<String, Class<?>> map, String encoding) {
        if (null == xml || 0 == xml.length()) {
            return null;
        }
        final String charset = StringUtils.defaultIfEmpty(encoding, StandardCharsets.UTF_8.name());
        XStream xStream = new XStream(new DomDriver(charset));
        if (null != map && !map.isEmpty()) {
            for (Entry<String, Class<?>> entry : map.entrySet()) {
                xStream.alias(entry.getKey(), entry.getValue());
            }
        }
        return (T) xStream.fromXML(xml);
    }

    /**
     * 对象转换为XML
     */
    public static <T> String toXml(T t) {
        if (null == t) {
            return null;
        }
        Marshaller marshaller;
        try {
            marshaller = JAXBContext.newInstance(t.getClass()).createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
            marshaller.setProperty(CharacterEscapeHandler.class.getName(), (CharacterEscapeHandler) (ac, i, j, flag, writer) -> writer.write(ac, i, j));
        } catch (JAXBException e) {
            log.error("#toXml# ## ## JAXB 创建实例异常", e);
            throw new XmlException(e);
        }
        try (StringWriter sw = new StringWriter()) {
            marshaller.marshal(t, sw);
            return sw.toString();
        } catch (Exception e) {
            log.error("#toXml# ## ## 对象转XML失败", e);
            throw new XmlException(e);
        }
    }

    /**
     * XML转换为对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T toBean(String xml, Class<T> clazz) {
        if (null == xml || 0 == xml.length() || null == clazz) {
            return null;
        }
        try {
            Unmarshaller unmarshaller = JAXBContext.newInstance(clazz).createUnmarshaller();
            StringReader sr = new StringReader(xml);
            return (T) unmarshaller.unmarshal(sr);
        } catch (Exception e) {
            log.error("#toBean# ## ## XML为对象失败", e);
            throw new XmlException(e);
        }
    }

    /**
     * XML文本转为Document对象
     */
    public static Document toDoc(String xml) {
        if (null == xml || 0 == xml.length()) {
            return null;
        }
        try (Reader reader = new StringReader(xml)) {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource(reader);
            return builder.parse(is);
        } catch (Exception e) {
            log.error("#toDoc# ## ## XML文本转为Document对象失败", e);
            throw new XmlException(e);
        }
    }

    private static XPath getXpath() {
        return xpath;
    }

    private static Object getObj(Object item, String expression, QName returnType) {
        if (null == item)
            return null;
        try {
            return getXpath().compile(expression).evaluate(item, returnType);
        } catch (Exception e) {
            log.error("#getObj# ## ## 获取XML属性值失败, expression: {}, returnType: {}", expression, returnType.getLocalPart(), e);
            throw new XmlException(e);
        }
    }

    public static String getString(Document doc, String expression) {
        return (String) getObj(doc, expression, XPathConstants.STRING);
    }

    public static String getString(Node node, String expression) {
        return (String) getObj(node, expression, XPathConstants.STRING);
    }

    public static int getInt(Document doc, String expression) {
        Object obj = getObj(doc, expression, XPathConstants.NUMBER);
        return null == obj ? 0 : (Integer) obj;
    }

    public static int getInt(Node node, String expression) {
        Object obj = getObj(node, expression, XPathConstants.NUMBER);
        return null == obj ? 0 : (Integer) obj;
    }

    public static long getLong(Document doc, String expression) {
        Object obj = getObj(doc, expression, XPathConstants.NUMBER);
        return null == obj ? 0L : (Long) obj;
    }

    public static long getLong(Node node, String expression) {
        Object obj = getObj(node, expression, XPathConstants.NUMBER);
        return null == obj ? 0L : (Long) obj;
    }

    public static double getDouble(Document doc, String expression) {
        Object obj = getObj(doc, expression, XPathConstants.NUMBER);
        return null == obj ? 0D : (Double) obj;
    }

    public static double getDouble(Node node, String expression) {
        Object obj = getObj(node, expression, XPathConstants.NUMBER);
        return null == obj ? 0D : (Double) obj;
    }

    public static boolean getBoolean(Document doc, String expression) {
        Object obj = getObj(doc, expression, XPathConstants.BOOLEAN);
        return null != obj && (Boolean) obj;
    }

    public static boolean getBoolean(Node node, String expression) {
        Object obj = getObj(node, expression, XPathConstants.BOOLEAN);
        return null != obj && (Boolean) obj;
    }

    public static NodeList getNodeList(Document doc, String expression) {
        return (NodeList) getObj(doc, expression, XPathConstants.NODESET);
    }

    public static NodeList getNodeList(Node node, String expression) {
        return (NodeList) getObj(node, expression, XPathConstants.NODESET);
    }

    public static Node getNode(Document doc, String expression) {
        return (Node) getObj(doc, expression, XPathConstants.NODE);
    }

    public static Node getNode(Node node, String expression) {
        return (Node) getObj(node, expression, XPathConstants.NODE);
    }

}
