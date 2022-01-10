package cn.dev33.satoken.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SaTokenConfigFactory {
    public static String configPath = "sa-token.properties";

    public static SaTokenConfig createConfig() {
        Map<String, String> map = readPropToMap(configPath);
        return (SaTokenConfig) initPropByMap(map, new SaTokenConfig());
    }

    private static Object initPropByMap(Map<String, String> map, Object obj) {
        if (map == null) {
            map = new HashMap<String, String>(16);
        }
        Class<?> cs = null;
        if (obj instanceof Class) {
            cs = (Class<?>) obj;
            obj = null;
        } else {
            cs = obj.getClass();
        }
        for (Field field : cs.getDeclaredFields()) {
            String value = map.get(field.getName());
            if (value == null) {
                continue;
            }
            try {
                Object valueConvert = getObjectByClass(value, field.getType());
                field.setAccessible(true);
                field.set(obj, valueConvert);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("属性赋值出错：" + field.getName(), e);
            }

        }
        return obj;
    }

    private static Object getObjectByClass(String str, Class<?> cs) {
        Object value;
        if (str == null) {
            value = null;
        } else if (cs.equals(String.class)) {
            value = str;
        } else if (cs.equals(int.class) || cs.equals(Integer.class)) {
            value = Integer.valueOf(str);
        } else if (cs.equals(long.class) || cs.equals(Long.class)) {
            value = Long.valueOf(str);
        } else if (cs.equals(short.class) || cs.equals(Short.class)) {
            value = Short.valueOf(str);
        } else if (cs.equals(float.class) || cs.equals(Float.class)) {
            value = Float.valueOf(str);
        } else if (cs.equals(double.class) || cs.equals(Double.class)) {
            value = Double.valueOf(str);
        } else if (cs.equals(boolean.class) || cs.equals(Boolean.class)) {
            value = Boolean.valueOf(str);
        } else {
            throw new RuntimeException("未能将值：" + str + "，转换类型为：" + cs, null);
        }
        return value;
    }

    private static Map<String, String> readPropToMap(String propertiesPath) {
        Map<String, String> map = new HashMap<String, String>(16);
        try {
            InputStream is = SaTokenConfigFactory.class.getClassLoader().getResourceAsStream(propertiesPath);
            if (is == null) {
                return null;
            }
            Properties prop = new Properties();
            prop.load(is);
            for (String key : prop.stringPropertyNames()) {
                map.put(key, prop.getProperty(key));
            }
        } catch (IOException e) {
            throw new RuntimeException("配置文件(" + propertiesPath + ")加载失败", e);
        }
        return map;

    }
}
