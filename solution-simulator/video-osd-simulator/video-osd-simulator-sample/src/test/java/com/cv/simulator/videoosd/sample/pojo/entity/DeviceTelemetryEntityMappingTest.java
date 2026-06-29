package com.cv.simulator.videoosd.sample.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DeviceTelemetryEntityMappingTest {

    @Test
    void marksRawJsonAsNonPersistentCompatibilityField() throws Exception {
        Field field = DeviceTelemetryEntity.class.getDeclaredField("rawJson");
        TableField tableField = field.getAnnotation(TableField.class);

        assertNotNull(tableField);
        assertFalse(tableField.exist());
    }
}
