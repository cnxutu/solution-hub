package com.cv.solution.formdict.form.common.validation.type;

import com.cv.solution.formdict.form.common.enums.FieldTypeEnum;
import com.cv.solution.formdict.form.common.validation.FieldValidator;
import com.cv.solution.formdict.form.pojo.po.TemplateFieldPO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author: xutu
 * @since: 2025/11/3 17:03
 */
@Component
public class NumberFieldValidator implements FieldValidator {

    @Override
    public FieldTypeEnum getFieldType() {
        return FieldTypeEnum.NUMBER;
    }

    @Override
    public void validate(TemplateFieldPO field, Object value) {
        BigDecimal num;
        try {
            num = new BigDecimal(value.toString());
        } catch (Exception e) {
            throw new IllegalArgumentException(field.getFieldName() + " 必须是数字");
        }

        if (field.getDecimalScale() != null && num.scale() > field.getDecimalScale()) {
            throw new IllegalArgumentException(field.getFieldName() + " 小数位不能超过 " + field.getDecimalScale());
        }
        if (field.getMinValue() != null && num.compareTo(field.getMinValue()) < 0) {
            throw new IllegalArgumentException(field.getFieldName() + " 不能小于 " + field.getMinValue());
        }
        if (field.getMaxValue() != null && num.compareTo(field.getMaxValue()) > 0) {
            throw new IllegalArgumentException(field.getFieldName() + " 不能大于 " + field.getMaxValue());
        }
    }
}


