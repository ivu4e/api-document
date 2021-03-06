package com.github.liuanxin.api.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@SuppressWarnings("unchecked")
public class Tools {

    private static final Logger LOGGER = LoggerFactory.getLogger(Tools.class);

    public static final String EMPTY = "";
    private static final String SPACE = " ";
    private static final String SPLIT = ",";

    private static final String FILE_TYPE = "file";

    /** just basic type: int long double array etc... */
    private static final Map<String, Class<?>> BASIC_CLAZZ_MAP = maps(
            boolean.class.getSimpleName(), boolean.class,
            Boolean.class.getSimpleName(), boolean.class,

            byte.class.getSimpleName(), byte.class,
            Byte.class.getSimpleName(), byte.class,

            char.class.getSimpleName(), char.class,
            Character.class.getSimpleName(), char.class,

            short.class.getSimpleName(), short.class,
            Short.class.getSimpleName(), short.class,

            int.class.getSimpleName(), int.class,
            Integer.class.getSimpleName(), int.class,

            long.class.getSimpleName(), long.class,
            Long.class.getSimpleName(), long.class,

            float.class.getSimpleName(), float.class,
            Float.class.getSimpleName(), float.class,

            double.class.getSimpleName(), double.class,
            Double.class.getSimpleName(), double.class,

            BigInteger.class.getSimpleName(), long.class,
            BigDecimal.class.getSimpleName(), double.class,
            String.class.getSimpleName(), String.class,

            // up type, down array type

            boolean[].class.getSimpleName(), boolean[].class,
            Boolean[].class.getSimpleName(), boolean[].class,

            byte[].class.getSimpleName(), byte[].class,
            Byte[].class.getSimpleName(), byte[].class,

            char[].class.getSimpleName(), char[].class,
            Character[].class.getSimpleName(), char[].class,

            short[].class.getSimpleName(), short[].class,
            Short[].class.getSimpleName(), short[].class,

            int[].class.getSimpleName(), int[].class,
            Integer[].class.getSimpleName(), int[].class,

            long[].class.getSimpleName(), long[].class,
            Long[].class.getSimpleName(), long[].class,

            float[].class.getSimpleName(), float[].class,
            Float[].class.getSimpleName(), float[].class,

            double[].class.getSimpleName(), double[].class,
            Double[].class.getSimpleName(), double[].class,

            BigInteger[].class.getSimpleName(), long[].class,
            BigDecimal[].class.getSimpleName(), double[].class,
            String[].class.getSimpleName(), String[].class
    );

    /** { basic-type : default-value } */
    private static final Map<String, Object> BASIC_TYPE_VALUE_MAP = maps(
            boolean.class.getSimpleName(), false,
            Boolean.class.getSimpleName(), false,

            byte.class.getSimpleName(), (byte) 0,
            Byte.class.getSimpleName(), (byte) 0,

            char.class.getSimpleName(), (char) 0,
            Character.class.getSimpleName(), (char) 0,

            short.class.getSimpleName(), (short) 0,
            Short.class.getSimpleName(), (short) 0,

            int.class.getSimpleName(), 0,
            Integer.class.getSimpleName(), 0,

            long.class.getSimpleName(), 0L,
            Long.class.getSimpleName(), 0L,

            float.class.getSimpleName(), 0F,
            Float.class.getSimpleName(), 0F,

            double.class.getSimpleName(), 0D,
            Double.class.getSimpleName(), 0D,

            BigInteger.class.getSimpleName(), BigInteger.ZERO,
            BigDecimal.class.getSimpleName(), BigDecimal.ZERO,
            String.class.getSimpleName(), EMPTY,

            // up type, down array type

            boolean[].class.getSimpleName(), new boolean[] { false },
            Boolean[].class.getSimpleName(), new Boolean[] { false },

            byte[].class.getSimpleName(), new byte[] { (byte) 0 },
            Byte[].class.getSimpleName(), new Byte[] { (byte) 0 },

            char[].class.getSimpleName(), new char[] { (char) 0 },
            Character[].class.getSimpleName(), new Character[] { (char) 0 },

            short[].class.getSimpleName(), new short[] { (short) 0 },
            Short[].class.getSimpleName(), new Short[] { (short) 0 },

            int[].class.getSimpleName(), new int[] { 0 },
            Integer[].class.getSimpleName(), new Integer[] { 0 },

            long[].class.getSimpleName(), new long[] { 0L },
            Long[].class.getSimpleName(), new Long[] { 0L },

            float[].class.getSimpleName(), new float[] { 0F },
            Float[].class.getSimpleName(), new Float[] { 0F },

            double[].class.getSimpleName(), new double[] { 0D },
            Double[].class.getSimpleName(), new Double[] { 0D },

            BigInteger[].class.getSimpleName(), new BigInteger[] { BigInteger.ZERO },
            BigDecimal[].class.getSimpleName(), new BigDecimal[] { BigDecimal.ZERO },
            String[].class.getSimpleName(), new String[] { EMPTY }
    );

    private static final Map<String, Object> DEFAULT_MAP_KEY = maps(
            Boolean.class.getSimpleName(), false,
            Byte.class.getSimpleName(), (byte) 0,
            Character.class.getSimpleName(), (char) 0,
            Short.class.getSimpleName(), (short) 0,
            Integer.class.getSimpleName(), 0,
            Long.class.getSimpleName(), 0L,
            Float.class.getSimpleName(), 0F,
            Double.class.getSimpleName(), 0D,
            BigInteger.class.getSimpleName(), BigInteger.ZERO,
            BigDecimal.class.getSimpleName(), BigDecimal.ZERO,
            String.class.getSimpleName(), "?"
    );

    // ========== string ==========
    public static boolean isBlank(Object obj) {
        return obj == null;
    }
    public static boolean isNotBlank(Object obj) {
        return obj != null;
    }
    public static boolean isEmpty(Object obj) {
        return obj == null || EMPTY.equals(obj.toString().trim());
    }
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    public static String addPrefix(String src) {
        if (isEmpty(src)) {
            return "/";
        }
        if (src.startsWith("/")) {
            return src;
        }
        return "/" + src;
    }

    // ========== json ==========
    private static final ObjectMapper RENDER = new ObjectMapper();

    private static final ObjectWriter PRETTY_RENDER = RENDER.writerWithDefaultPrettyPrinter();
    public static String toJson(Object obj) {
        if (isEmpty(obj)) {
            return EMPTY;
        }
        try {
            return RENDER.writeValueAsString(obj);
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error(String.format("obj(%s) to json exception", obj.toString()), e);
            }
            return EMPTY;
        }
    }
    public static String toPrettyJson(String json) {
        if (isEmpty(json)) {
            return EMPTY;
        }
        try {
            return PRETTY_RENDER.writeValueAsString(RENDER.readValue(json, Object.class));
        } catch (IOException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error(String.format("str(%s) to pretty json exception", json), e);
            }
            return EMPTY;
        }
    }

    // ========== array map ==========
    private static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }
    private static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }
    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }
    public static <T> boolean isNotEmpty(Collection<T> collection) {
        return !isEmpty(collection);
    }
    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }
    public static boolean isNotEmpty(Map map) {
        return !isEmpty(map);
    }
    public static <T> String toStr(Collection<T> collection) {
        if (isEmpty(collection)) {
            return EMPTY;
        }
        StringBuilder sbd = new StringBuilder();
        int i = 0;
        for (T t : collection) {
            sbd.append(t);
            if (i + 1 != collection.size()) {
                sbd.append(SPLIT);
            }
            i++;
        }
        return sbd.toString();
    }

    // ========== list map ==========
    @SuppressWarnings("unchecked")
    static List lists(Object... values) {
        return new ArrayList(Arrays.asList(values));
    }
    public static <T> Set<T> sets() {
        return new HashSet<>();
    }
    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<>();
    }
    public static <K, V> HashMap<K, V> newLinkedHashMap() {
        return new LinkedHashMap<>();
    }

    private static <K, V> HashMap<K, V> maps(Object... keysAndValues) {
        return (HashMap<K, V>) maps(newHashMap(), keysAndValues);
    }
    @SuppressWarnings("unchecked")
    private static <K, V> Map<K, V> maps(Map<K, V> result, Object... keysAndValues) {
        if (isNotEmpty(keysAndValues)) {
            for (int i = 0; i < keysAndValues.length; i += 2) {
                if (keysAndValues.length > (i + 1)) {
                    result.put((K) keysAndValues[i], (V) keysAndValues[i + 1]);
                }
            }
        }
        return result;
    }

    // ========== method ==========
    private static Object getMethod(Object obj, String method) {
        if (isNotEmpty(method)) {
            try {
                return obj.getClass().getDeclaredMethod(method).invoke(obj);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                // ignore
            }

            try {
                return obj.getClass().getMethod(method).invoke(obj);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                // ignore
            }
        }
        return null;
    }

    // ========== enum ==========
    static String enumInfo(Class<?> clazz, String desc) {
        // enum append (code:value)
        String enumInfo = collectEnumInfo(clazz);
        if (isEmpty(desc)) {
            return enumInfo;
        } else {
            if (isEmpty(enumInfo)) {
                return desc;
            } else {
                return desc + String.format("(%s)", enumInfo);
            }
        }
    }
    private static String collectEnumInfo(Class<?> clazz) {
        if (isNotEmpty(clazz) && clazz.isEnum()) {
            Enum[] constants = (Enum[]) clazz.getEnumConstants();
            if (isNotEmpty(constants)) {
                StringBuilder sbd = new StringBuilder();
                String split = SPLIT + SPACE;
                for (Enum em : constants) {
                    Object code = getMethod(em, "getCode");
                    if (isNotEmpty(code)) {
                        // has getCode
                        sbd.append(code).append(":");
                        Object value = getMethod(em, "getValue");
                        // has getValue return <code1: value1, code2: value2 ...>
                        // no getValue return <code1: name1, code2: name2 ...>
                        sbd.append(isNotEmpty(value) ? value : em.name());
                    } else {
                        // no getCode return <name1, name2>
                        // sbd.append(em.ordinal()).append(":");
                        sbd.append(em.name());
                    }
                    sbd.append(split);
                }
                int len = split.length();
                if (sbd.length() > len) {
                    return sbd.delete(sbd.length() - len, sbd.length()).toString();
                }
            }
        }
        return EMPTY;
    }

    // ========== type ==========
    static Class<?> getBasicType(String name) {
        return BASIC_CLAZZ_MAP.get(name);
    }

    private static Object getTypeDefaultValue(Class<?> clazz) {
        return BASIC_TYPE_VALUE_MAP.get(clazz.getSimpleName());
    }
    static boolean basicType(Class<?> clazz) {
        if (isBlank(clazz)) {
            return false;
        }
        Object defaultValue = getTypeDefaultValue(clazz);
        if (!isBlank(defaultValue)) {
            return true;
        }
        // include enum
        return clazz.isEnum();
    }
    static boolean notBasicType(Class<?> clazz) {
        return !basicType(clazz);
    }

    static Object mapKeyDefault(Class<?> clazz) {
        Object keyDefault = DEFAULT_MAP_KEY.get(clazz.getSimpleName());
        if (isNotEmpty(keyDefault)) {
            return keyDefault;
        } else if (clazz.isEnum()) {
            Object[] enumConstants = clazz.getEnumConstants();
            return (enumConstants.length > 0) ? enumConstants[0] : null;
        } else {
            return null;
        }
    }

    static boolean hasFileInput(String fileType) {
        return FILE_TYPE.equals(fileType);
    }
    static String getInputType(Class<?> type) {
        if (isBlank(type)) {
            return EMPTY;
        }

        // upload file
        if (MultipartFile.class.isAssignableFrom(type)) {
            return FILE_TYPE;
        }
        String paramType = type.getSimpleName();
        Class<?> basicClass = getBasicType(paramType);
        if (isNotEmpty(basicClass)) {
            return basicClass.getSimpleName();
        } else if (type.isEnum()) {
            return "Enum(" + paramType + ")";
        } else {
            // string, string[], list, set, map... etc
            return paramType.substring(0, 1).toLowerCase() + paramType.substring(1);
        }
    }

    static Object getReturnType(Class<?> clazz) {
        if (isBlank(clazz)) {
            return null;
        }

        Object defaultValue = getTypeDefaultValue(clazz);
        if (!isBlank(defaultValue)) {
            return defaultValue;
        } else if (clazz.isEnum()) {
            // Enum return first
            Object[] enumConstants = clazz.getEnumConstants();
            return (enumConstants.length > 0) ? enumConstants[0] : null;
        } else {
            return null;
        }
    }

    static Object getReturnTypeExample(Class<?> clazz, String example) {
        Object defaultObj = getReturnType(clazz);
        if (isEmpty(example)) {
            return defaultObj;
        }

        if (clazz == boolean.class || clazz == Boolean.class) {
            return Arrays.asList("true", "1").contains(example);
        }
        else if (clazz == byte.class || clazz == Byte.class) {
            try {
                return Byte.valueOf(example);
            } catch (NumberFormatException e) {
                return defaultObj;
            }
        }
        else if (clazz == char.class || clazz == Character.class) {
            try {
                return example.charAt(0);
            } catch (NumberFormatException e) {
                return defaultObj;
            }
        }
        else if (clazz == short.class || clazz == Short.class) {
            try {
                return Short.valueOf(example);
            } catch (NumberFormatException e) {
                return defaultObj;
            }
        }
        else if (clazz == int.class || clazz == Integer.class) {
            try {
                return Integer.valueOf(example);
            } catch (NumberFormatException e) {
                return defaultObj;
            }
        }
        else if (clazz == long.class || clazz == Long.class) {
            try {
                return Long.valueOf(example);
            } catch (NumberFormatException e) {
                return defaultObj;
            }
        }
        else if (clazz == float.class || clazz == Float.class) {
            try {
                return Float.valueOf(example);
            } catch (NumberFormatException e) {
                return defaultObj;
            }
        }
        else if (clazz == double.class || clazz == Double.class) {
            try {
                return Double.valueOf(example);
            } catch (NumberFormatException e) {
                return defaultObj;
            }
        }
        else if (clazz == BigInteger.class) {
            try {
                return new BigInteger(example);
            } catch (NumberFormatException e) {
                return defaultObj;
            }
        }
        else if (clazz == BigDecimal.class) {
            try {
                return new BigDecimal(example);
            } catch (NumberFormatException e) {
                return defaultObj;
            }
        }

        // up type, down array type

        else if (clazz == boolean[].class) {
            return new boolean[] { Arrays.asList("true", "1").contains(example) };
        } else if (clazz == Boolean[].class) {
            return new Boolean[] { Arrays.asList("true", "1").contains(example) };
        }

        else if (clazz == byte[].class) {
            try {
                return new byte[] { Byte.valueOf(example) };
            } catch (NumberFormatException e) {
                return defaultObj;
            }
        } else if (clazz == Byte[].class) {
            try {
                return new Byte[] { Byte.valueOf(example) };
            } catch (NumberFormatException e) {
                return defaultObj;
            }
        }

        else if (clazz == char[].class) {
            try {
                return new char[] { example.charAt(0) };
            } catch (NumberFormatException e) {
                return defaultObj;
            }
        } else if (clazz == Character[].class) {
            try {
                return new Character[] { example.charAt(0) };
            } catch (NumberFormatException e) {
                return defaultObj;
            }
        }

        else if (clazz == short[].class) {
            try {
                return new short[] { Short.valueOf(example) };
            } catch (NumberFormatException e) {
                return defaultObj;
            }
        } else if (clazz == Short[].class) {
            try {
                return new Short[] { Short.valueOf(example) };
            } catch (NumberFormatException e) {
                return defaultObj;
            }
        }

        else if (clazz == int[].class) {
            try {
                return new int[] { Integer.valueOf(example) };
            } catch (NumberFormatException e) {
                return defaultObj;
            }
        } else if (clazz == Integer[].class) {
            try {
                return new Integer[] { Integer.valueOf(example) };
            } catch (NumberFormatException e) {
                return defaultObj;
            }
        }

        else if (clazz == long[].class) {
            try {
                return new long[] { Long.valueOf(example) };
            } catch (NumberFormatException e) {
                return defaultObj;
            }
        } else if (clazz == Long[].class) {
            try {
                return new Long[] { Long.valueOf(example) };
            } catch (NumberFormatException e) {
                return defaultObj;
            }
        }

        else if (clazz == float[].class) {
            try {
                return new float[] { Float.valueOf(example) };
            } catch (NumberFormatException e) {
                return defaultObj;
            }
        } else if (clazz == Float[].class) {
            try {
                return new Float[] { Float.valueOf(example) };
            } catch (NumberFormatException e) {
                return defaultObj;
            }
        }

        else if (clazz == double[].class) {
            try {
                return new double[] { Double.valueOf(example) };
            } catch (NumberFormatException e) {
                return defaultObj;
            }
        } else if (clazz == Double[].class) {
            try {
                return new Double[] { Double.valueOf(example) };
            } catch (NumberFormatException e) {
                return defaultObj;
            }
        }

        else if (clazz == BigInteger[].class) {
            try {
                return new BigInteger(example);
            } catch (NumberFormatException e) {
                return defaultObj;
            }
        }
        else if (clazz == BigDecimal[].class) {
            try {
                return new BigDecimal(example);
            } catch (NumberFormatException e) {
                return defaultObj;
            }
        }

        else {
            return defaultObj;
        }
    }
}
