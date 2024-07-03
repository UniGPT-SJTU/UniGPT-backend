package com.ise.unigpt.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Field;

/**
 * @brief 反射测试工具类
 */
public class ReflectionTestUtils {

    /**
     * @brief 保证对象的所有字段都不为null
     * @param obj 对象
     * @throws IllegalAccessException
     */
    public static void assertNoNullFields(Object obj) throws IllegalAccessException {
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(obj);
            assertNotNull(value, field.getName() + " should not be null");
        }
    }
}
