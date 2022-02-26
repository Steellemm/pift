package com.griddynamics.pift.utils;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * TemplateUtils - util class for filling the template
 * with values persisted in the EntityMap
 */
public class TemplateUtils {

    private static final int quote = '"';
    private static final int point = '.';
    private static final Set<String> SYSTEM_WORDS = Set.of("true", "false", "null");

    /**
     * Returns the request URL.
     * If the template contains characters like {, },
     * then it's content will be filled with the values of real object from the EntityMap.
     *
     * @param  template  URL that can contains template
     * @param  params the EntityMap
     * @return the URL
     */
    public static String format(String template, Map<String, Object> params) {
        boolean isWord = false;

        StringBuilder word = new StringBuilder();
        StringBuilder builder = new StringBuilder();

        for (char ch: template.toCharArray()) {
            if (ch == '}') {
                isWord = false;
                builder.append(getVar(word.toString(), params));
                word = new StringBuilder();
            }
            if (isWord) {
                word.append(ch);
            } else if (ch != '{' && ch != '}'){
                builder.append(ch);
            }
            if (ch == '{') {
                isWord = true;
            }
        }
        return builder.toString();
    }

    /**
     * @param  inputStream  the inputStream of json template
     * @param  params the EntityMap
     * @return the template in which all patterns are filled with real values from the EntityMap
     */
    public static String getJsonAsString(InputStream inputStream, Map<String, Object> params) {
        try (BufferedInputStream bis = new BufferedInputStream(inputStream)) {
            StringBuilder builder = new StringBuilder();
            StringBuilder word = new StringBuilder();
            int result = bis.read();
            boolean isJsonWord = false;
            boolean isTemplateWord = false;

            while (result != -1) {
                if (result == quote) {
                    isJsonWord = !isJsonWord;
                }

                if (!isJsonWord && firstLetterOfTemplate(result)) {
                    isTemplateWord = true;
                }

                if (isTemplateWord && !isTemplateChar(result)) {
                    isTemplateWord = false;
                    String str = word.toString();
                    if (!SYSTEM_WORDS.contains(str)) {
                        builder.append(getVarForJson(str, params));
                    } else {
                        builder.append(str);
                    }
                    word = new StringBuilder();
                }

                if (isTemplateWord) {
                    word.append((char) result);
                } else {
                    builder.append((char) result);
                }
                result = bis.read();
            }
            return builder.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static boolean firstLetterOfTemplate(int ch) {
        return Character.isLetter((char) ch);
    }

    private static boolean isTemplateChar(int ch) {
        return ch == point || Character.isLetterOrDigit((char) ch);
    }

    private static String getVarForJson(String word, Map<String, Object> params) {
        Object obj = getVar(word, params);
        if (obj instanceof Number) {
            return obj.toString();
        } else {
            return "\"" + obj + "\"";
        }
    }
    private static Object getVar(String word, Map<String, Object> params) {
        String[] split = word.split("\\.");
        String objectName = split[0];
        Object obj = params.get(objectName);
        for(int i = 1; i < split.length; i++) {
            String field = split[i];
            obj = getFieldValue(obj, field);
            if (obj == null) {
                throw new IllegalArgumentException("Field " + word + " is empty");
            }
        }
        return obj;
    }

    private static Object getFieldValue(Object obj, String fieldName) {
        Field field = Arrays.stream(obj.getClass().getDeclaredFields())
                .filter(f -> f.getName().equals(fieldName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No such field: " + fieldName));
        try {
            return FieldUtils.readField(field, obj, true);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
