package com.cv.i18n.processor;

import com.cv.i18n.annotation.I18nField;
import com.cv.i18n.context.I18nContextHolder;
import com.cv.i18n.core.I18nEnum;
import com.cv.i18n.core.I18nMessageResolver;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class I18nValueProcessor {

    private final I18nMessageResolver messageResolver;
    private final ObjectMapper objectMapper;

    public I18nValueProcessor(I18nMessageResolver messageResolver, ObjectMapper objectMapper) {
        this.messageResolver = messageResolver;
        this.objectMapper = objectMapper;
    }

    public Object process(Object source) {
        return processValue(source, new IdentityHashMap<>());
    }

    private Object processValue(Object value, IdentityHashMap<Object, Boolean> visited) {
        if (value == null || isSimpleValue(value)) {
            return translateEnum(value);
        }
        if (visited.containsKey(value)) {
            return null;
        }
        if (value instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) value;
            visited.put(value, Boolean.TRUE);
            List<Object> list = new ArrayList<>(collection.size());
            for (Object item : collection) {
                list.add(processValue(item, visited));
            }
            visited.remove(value);
            return list;
        }
        if (value.getClass().isArray()) {
            return processValue(objectMapper.convertValue(value, List.class), visited);
        }
        if (value instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) value;
            visited.put(value, Boolean.TRUE);
            Map<Object, Object> result = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                result.put(entry.getKey(), processValue(entry.getValue(), visited));
            }
            visited.remove(value);
            return result;
        }
        return processBean(value, visited);
    }

    private Map<String, Object> processBean(Object bean, IdentityHashMap<Object, Boolean> visited) {
        visited.put(bean, Boolean.TRUE);
        Map<String, Object> result = new LinkedHashMap<>();
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
        for (PropertyDescriptor descriptor : wrapper.getPropertyDescriptors()) {
            String propertyName = descriptor.getName();
            Method readMethod = descriptor.getReadMethod();
            if ("class".equals(propertyName) || readMethod == null || readMethod.getAnnotation(JsonIgnore.class) != null) {
                continue;
            }
            Field field = findField(bean.getClass(), propertyName);
            if (field != null && (Modifier.isStatic(field.getModifiers()) || field.getAnnotation(JsonIgnore.class) != null)) {
                continue;
            }
            String jsonName = resolveJsonName(propertyName, field, readMethod);
            Object propertyValue = wrapper.getPropertyValue(propertyName);
            I18nField i18nField = findI18nField(field, readMethod);
            Object processedValue = processValue(propertyValue, visited);
            if (i18nField != null) {
                Object translated = translateAnnotatedValue(i18nField, propertyName, propertyValue, wrapper, visited);
                if (i18nField.replace()) {
                    processedValue = translated;
                } else {
                    result.put(resolveTargetName(i18nField, jsonName), translated);
                }
            }
            result.put(jsonName, processedValue);
        }
        visited.remove(bean);
        return result;
    }

    private Object translateAnnotatedValue(I18nField i18nField, String propertyName, Object value,
                                           BeanWrapper wrapper, IdentityHashMap<Object, Boolean> visited) {
        if (value instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) value;
            List<Object> list = new ArrayList<>(collection.size());
            for (Object item : collection) {
                list.add(translateSingleValue(i18nField, propertyName, item, wrapper, visited));
            }
            return list;
        }
        if (value instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) value;
            Map<Object, Object> result = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                result.put(entry.getKey(), translateSingleValue(i18nField, propertyName, entry.getValue(), wrapper, visited));
            }
            return result;
        }
        return translateSingleValue(i18nField, propertyName, value, wrapper, visited);
    }

    private Object translateSingleValue(I18nField i18nField, String propertyName, Object value,
                                        BeanWrapper wrapper, IdentityHashMap<Object, Boolean> visited) {
        if (value == null) {
            return null;
        }
        if (value instanceof I18nEnum) {
            I18nEnum i18nEnum = (I18nEnum) value;
            return resolve(i18nEnum.getI18nCode(), i18nField.args(), i18nEnum.getDefaultMessage(), wrapper, visited);
        }
        String code = StringUtils.hasText(i18nField.value()) ? i18nField.value() : String.valueOf(value);
        if (StringUtils.hasText(i18nField.prefix()) && !code.startsWith(i18nField.prefix())) {
            code = i18nField.prefix() + code;
        }
        String defaultMessage = StringUtils.hasText(i18nField.defaultMessage()) ? i18nField.defaultMessage() : String.valueOf(value);
        return resolve(code, i18nField.args(), defaultMessage, wrapper, visited);
    }

    private Object translateEnum(Object value) {
        if (value instanceof I18nEnum) {
            I18nEnum i18nEnum = (I18nEnum) value;
            return resolve(i18nEnum.getI18nCode(), new String[0], i18nEnum.getDefaultMessage(), null, null);
        }
        return value;
    }

    private String resolve(String code, String[] argNames, String defaultMessage, BeanWrapper wrapper,
                           IdentityHashMap<Object, Boolean> visited) {
        Object[] args = new Object[argNames.length];
        for (int i = 0; i < argNames.length; i++) {
            Object argValue = wrapper != null && wrapper.isReadableProperty(argNames[i])
                    ? wrapper.getPropertyValue(argNames[i])
                    : argNames[i];
            args[i] = processValue(argValue, visited == null ? new IdentityHashMap<>() : visited);
        }
        Locale locale = I18nContextHolder.getLocale();
        return messageResolver.resolve(code, locale, args, defaultMessage);
    }

    private boolean isSimpleValue(Object value) {
        Class<?> type = value.getClass();
        return type.isPrimitive()
                || value instanceof CharSequence
                || value instanceof Number
                || value instanceof Boolean
                || value instanceof Character
                || value instanceof Enum<?>
                || value instanceof TemporalAccessor
                || value instanceof java.util.Date;
    }

    private I18nField findI18nField(Field field, Method method) {
        I18nField annotation = findAnnotation(field);
        return annotation == null ? findAnnotation(method) : annotation;
    }

    private I18nField findAnnotation(AnnotatedElement element) {
        return element == null ? null : AnnotationUtils.findAnnotation(element, I18nField.class);
    }

    private String resolveTargetName(I18nField i18nField, String jsonName) {
        return StringUtils.hasText(i18nField.target()) ? i18nField.target() : jsonName + "Text";
    }

    private String resolveJsonName(String propertyName, Field field, Method readMethod) {
        JsonProperty fieldProperty = field == null ? null : field.getAnnotation(JsonProperty.class);
        if (fieldProperty != null && StringUtils.hasText(fieldProperty.value())) {
            return fieldProperty.value();
        }
        JsonProperty methodProperty = readMethod.getAnnotation(JsonProperty.class);
        if (methodProperty != null && StringUtils.hasText(methodProperty.value())) {
            return methodProperty.value();
        }
        return propertyName;
    }

    private Field findField(Class<?> type, String propertyName) {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            try {
                Field field = current.getDeclaredField(propertyName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        return null;
    }
}
