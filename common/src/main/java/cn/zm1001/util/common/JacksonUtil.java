package cn.zm1001.util.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * @Desc Jackson工具类
 * @Author Dongd_Zhou
 */
@Slf4j
public class JacksonUtil {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 序列化时，包含对象的所有字段
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        // 序列化时，取消默认转换Timestamps形式
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // 序列化时，忽略空Bean转Json的错误
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 反序列化时，忽略在Json字符串中存在，但Java对象中不存在对应属性的情况
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 反序列化时，默认所有的日期格式为yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(DateUtils.YYYY_MM_DD_HH_MM_SS));
        // 反序列化时，实际是数组，但只有一个单值的情况
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        // 反序列化时，将空字符串转成空对象
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    }

    /**
     * 对象转Json字符串
     *
     * @param obj 对象实例
     * @return Json字符串
     */
    public static String toJson(Object obj) {
        if (null == obj) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("#toJson# ## ## Object convert to json error", e);
        }
        return null;
    }

    /**
     * 对象转Json字符串(空值输出空字符)
     *
     * @param obj 对象
     * @return Json字符串
     */
    public static String toJsonEmpty(Object obj) {
        if (null == obj) {
            return null;
        }
        try {
            objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
                @Override
                public void serialize(Object param, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                    // 设置返回null 转为 空字符串""
                    jsonGenerator.writeString(StringUtils.EMPTY);
                }
            });
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("#toJsonEmpty# ## ## Object convert to json error", e);
        }
        return null;
    }

    /**
     * Json字符串转为对象
     *
     * @param json  Json字符串
     * @param clazz 对象类型
     * @return 对象类型
     */
    public static <T> T parse(String json, Class<T> clazz) {
        if (StringUtils.isEmpty(json) || null == clazz) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("#parse# ## ## json string convert to clazz error: {}", json, e);
        }
        return null;
    }


    /**
     * Json字符串转为Map
     *
     * @param json Json字符串
     * @return Map实例
     */
    public static <K, V> Map<K, V> toMap(String json) {
        try {
            return toObject(json, new TypeReference<Map<K, V>>() {
            });
        } catch (Exception e) {
            log.error("#toMap# ## ## json string convert to map error: {}", json, e);
        }
        return null;
    }


    /**
     * Json字符串转为List
     *
     * @param json Json字符串
     * @return List实例
     */
    public static <T> List<T> toList(String json) {
        try {
            return toObject(json, new TypeReference<List<T>>() {
            });
        } catch (Exception e) {
            log.error("#toList# ## ## json string convert to list error: {}", json, e);
        }
        return null;
    }

    /**
     * Json字符串转为特定对象
     */
    /**
     * @param json          字符串
     * @param typeReference 指定类型
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T> T toObject(String json, TypeReference<T> typeReference) {
        try {
            if (StringUtils.isEmpty(json) || null == typeReference) {
                return null;
            }
            return (T) (typeReference.getType().equals(String.class) ? json : objectMapper.readValue(json, typeReference));
        } catch (Exception e) {
            log.error("#toObject# ## ## json string convert to object error: {}", json, e);
        }
        return null;
    }

}

