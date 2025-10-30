package com.cv.solution.formdict.form.common.util;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.cv.solution.formdict.form.common.enums.FieldTypeEnum;
import com.cv.solution.formdict.form.pojo.param.FormFieldDataParam;
import com.cv.solution.formdict.form.pojo.param.TemplateFieldParam;
import com.cv.solution.formdict.form.pojo.po.TemplateFieldPO;

import java.util.List;
import java.util.Map;

/**
 * @author: xutu
 * @since: 2025/10/29 17:31
 */
public class TemplateValidatorUtils {

    /**
     * 模板校验字段参数
     *
     * @param field
     */
    public static void validateTemplateFieldParam(TemplateFieldParam field) {
        // 校验字段类型合法性
        try {
            FieldTypeEnum.valueOf(field.getFieldType());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(field.getFieldCode() + " 字段类型不合法: " + field.getFieldType());
        }

        // 校验选项来源
        if (field.getOptionSource() == 1 && (field.getDictCode() == null || field.getDictCode().isEmpty())) {
            throw new IllegalArgumentException(field.getFieldCode() + " optionSource=1 必须绑定 dictCode");
        }

        if (field.getOptionSource() == 2 && CollectionUtils.isEmpty(field.getTemplateFieldOptions())) {
            throw new IllegalArgumentException(field.getFieldCode() + " optionSource=2 必须有模板专属选项");
        }

        // 校验必填字段
        if (field.getRequired() == null) {
            throw new IllegalArgumentException(field.getFieldCode() + " 必填属性不能为空");
        }

        // 校验排序
        if (field.getFieldSort() == null) {
            throw new IllegalArgumentException(field.getFieldCode() + " 字段排序不能为空");
        }

        // 校验状态
        if (field.getStatus() == null) {
            throw new IllegalArgumentException(field.getFieldCode() + " 字段状态不能为空");
        }

        // 可选：校验备注长度
        if (field.getRemark() != null && field.getRemark().length() > 512) {
            throw new IllegalArgumentException(field.getFieldCode() + " 备注过长");
        }
    }


    /**
     * 校验字段值是否合法
     *
     * @param templateFields 模板字段定义列表
     * @param fieldDataMap   录入数据 map（key = fieldCode）
     */
    public static void validateFieldData(List<TemplateFieldPO> templateFields,
                                         Map<String, FormFieldDataParam> fieldDataMap) {

        for (TemplateFieldPO field : templateFields) {
            FormFieldDataParam data = fieldDataMap.get(field.getFieldCode());

            // 必填校验
            if (field.getRequired() != null && field.getRequired() == 1 && (data == null || data.getFieldValue() == null)) {
                throw new IllegalArgumentException(field.getFieldName() + " 是必填项");
            }

            // 类型校验
            if (data != null && data.getFieldValue() != null) {
                switch (FieldTypeEnum.valueOf(field.getFieldType())) {
                    case NUMBER:
                        if (!(data.getFieldValue() instanceof Number)) {
                            throw new IllegalArgumentException(field.getFieldName() + " 必须是数字");
                        }
                        break;
                    case BOOLEAN:
                        if (!(data.getFieldValue() instanceof Boolean)) {
                            throw new IllegalArgumentException(field.getFieldName() + " 必须是布尔值");
                        }
                        break;
                    case DATE:
                    case DATETIME:
                    case TIME:
                        if (!(data.getFieldValue() instanceof String)) {
                            throw new IllegalArgumentException(field.getFieldName() + " 必须是时间字符串");
                        }
                        break;
                    case SELECT:
                    case MULTI_SELECT:
                    case RADIO:
                    case CHECKBOX:
                        if (!(data.getFieldValue() instanceof String || data.getFieldValue() instanceof List)) {
                            throw new IllegalArgumentException(field.getFieldName() + " 必须是选项值或列表");
                        }
                        break;
                    default:
                        // TEXT、TEXTAREA、FILE、IMAGE、JSON 等默认不做额外校验
                }
            }
        }
    }
}
