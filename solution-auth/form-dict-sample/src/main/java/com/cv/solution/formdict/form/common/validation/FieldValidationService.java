package com.cv.solution.formdict.form.common.validation;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.cv.solution.formdict.form.common.enums.FieldTypeEnum;
import com.cv.solution.formdict.form.common.enums.InputSubTypeEnum;
import com.cv.solution.formdict.form.pojo.param.FormFieldDataParam;
import com.cv.solution.formdict.form.pojo.po.TemplateFieldPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author: xutu
 * @since: 2025/11/3 17:08
 */
@Component
public class FieldValidationService {

    private final Map<FieldTypeEnum, FieldValidator> validatorMap;
    private final Map<InputSubTypeEnum, InputSubTypeValidator> subTypeValidatorMap;

    @Autowired
    public FieldValidationService(List<FieldValidator> fieldValidators,
                                  List<InputSubTypeValidator> subTypeValidators) {
        this.validatorMap = fieldValidators.stream()
                .collect(Collectors.toMap(FieldValidator::getFieldType, v -> v));
        this.subTypeValidatorMap = subTypeValidators.stream()
                .collect(Collectors.toMap(InputSubTypeValidator::getSubType, v -> v));
    }

    public void validateFieldData(List<TemplateFieldPO> templateFields,
                                  Map<String, FormFieldDataParam> fieldDataMap) {

        for (TemplateFieldPO field : templateFields) {
            FormFieldDataParam data = fieldDataMap.get(field.getFieldCode());
            Object value = (data != null) ? data.getFieldValue() : null;

            // ✅ 必填校验
            if (field.getRequired() != null && field.getRequired() == 1
                    && (value == null || StringUtils.isBlank(String.valueOf(value)))) {
                throw new IllegalArgumentException(field.getFieldName() + " 是必填项");
            }

            if (value == null) continue;

            // ✅ 主类型校验
            FieldValidator validator = validatorMap.get(FieldTypeEnum.valueOf(field.getFieldType()));
            if (validator != null) {
                validator.validate(field, value);
            }

            // ✅ 通用校验（长度 / 正则）
            if (value instanceof String) {
                String str = (String) value;
                if (field.getMinLength() != null && str.length() < field.getMinLength()) {
                    throw new IllegalArgumentException(field.getFieldName() + " 长度不能小于 " + field.getMinLength());
                }
                if (field.getMaxLength() != null && str.length() > field.getMaxLength()) {
                    throw new IllegalArgumentException(field.getFieldName() + " 长度不能大于 " + field.getMaxLength());
                }
            }

            if (StringUtils.isNotBlank(field.getPattern()) && value instanceof String) {
                if (!Pattern.matches(field.getPattern(), (String) value)) {
                    throw new IllegalArgumentException(field.getFieldName() + " 格式不合法");
                }
            }
        }
    }

    /**
     * 提供 TEXT 下的 inputSubType 分发器供 TextFieldValidator 使用
     */
    public InputSubTypeValidator getSubTypeValidator(InputSubTypeEnum subType) {
        return subTypeValidatorMap.get(subType);
    }
}

