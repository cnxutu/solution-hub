package com.cv.solution.formdict.form.common.validation.type;

import com.cv.solution.formdict.form.common.enums.FieldTypeEnum;
import com.cv.solution.formdict.form.common.enums.InputSubTypeEnum;
import com.cv.solution.formdict.form.common.validation.FieldValidator;
import com.cv.solution.formdict.form.common.validation.InputSubTypeValidator;
import com.cv.solution.formdict.form.common.validation.FieldValidationService;
import com.cv.solution.formdict.form.pojo.po.TemplateFieldPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author: xutu
 * @since: 2025/11/3 17:02
 */
@Component
public class TextFieldValidator implements FieldValidator {

    private final FieldValidationService validationService;

    @Autowired
    @Lazy
    public TextFieldValidator(FieldValidationService validationService) {
        this.validationService = validationService;
    }

    @Override
    public FieldTypeEnum getFieldType() {
        return FieldTypeEnum.TEXT;
    }

    @Override
    public void validate(TemplateFieldPO field, Object value) {
        if (!(value instanceof String)) {
            throw new IllegalArgumentException(field.getFieldName() + " 必须是字符串");
        }

        if (field.getInputSubType() != null) {
            InputSubTypeEnum subType = InputSubTypeEnum.fromCode(field.getInputSubType());
            InputSubTypeValidator subValidator = validationService.getSubTypeValidator(subType);
            if (subValidator != null) {
                subValidator.validate((String) value, field.getFieldName());
            }
        }
    }

}


