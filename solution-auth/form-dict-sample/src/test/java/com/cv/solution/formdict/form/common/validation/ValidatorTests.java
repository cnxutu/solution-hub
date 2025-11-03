package com.cv.solution.formdict.form.common.validation;

import com.cv.solution.formdict.form.common.enums.FieldTypeEnum;
import com.cv.solution.formdict.form.common.enums.InputSubTypeEnum;
import com.cv.solution.formdict.form.pojo.param.FormFieldDataParam;
import com.cv.solution.formdict.form.pojo.po.TemplateFieldPO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.*;
/**
 *
 * 校验器综合测试示例
 * 测试内容：
 * - 手机号 / 邮箱 / 身份证 / 统一社会信用代码
 * - 数值范围 / 正则表达式 / 长度限制
 *
 * @author: xutu
 * @since: 2025/11/3 17:32
 */
@SpringBootTest
public class ValidatorTests {

    @Autowired
    private FieldValidationService validationService;

    private Map<String, FormFieldDataParam> dataMap;

    @BeforeEach
    public void setup() {
        dataMap = new HashMap<>();
    }

    private void runTest(String testName, TemplateFieldPO field, Object value) {
        try {
            FormFieldDataParam param = new FormFieldDataParam();
            param.setFieldValue(value);
            dataMap.clear();
            dataMap.put(field.getFieldCode(), param);

            validationService.validateFieldData(Collections.singletonList(field), dataMap);
            System.out.println("✅ " + testName + "：校验通过");
        } catch (Exception e) {
            System.out.println("❌ " + testName + "：校验失败 → " + e.getMessage());
        }
    }

    @Test
    public void testAllValidators() {

        // 1️⃣ 手机号
        TemplateFieldPO phoneField = new TemplateFieldPO();
        phoneField.setFieldName("手机号");
        phoneField.setFieldCode("phone");
        phoneField.setFieldType(FieldTypeEnum.TEXT.name());
        phoneField.setInputSubType(InputSubTypeEnum.VALIDATE_PHONE.getCode());

        runTest("手机号_正确", phoneField, "13812345678");
        runTest("手机号_错误", phoneField, "112233");

        // 2️⃣ 邮箱
        TemplateFieldPO emailField = new TemplateFieldPO();
        emailField.setFieldName("邮箱");
        emailField.setFieldCode("email");
        emailField.setFieldType(FieldTypeEnum.TEXT.name());
        emailField.setInputSubType(InputSubTypeEnum.VALIDATE_EMAIL.getCode());

        runTest("邮箱_正确", emailField, "test@example.com");
        runTest("邮箱_错误", emailField, "invalid@@mail");

        // 3️⃣ 身份证
        TemplateFieldPO idCardField = new TemplateFieldPO();
        idCardField.setFieldName("身份证");
        idCardField.setFieldCode("idCard");
        idCardField.setFieldType(FieldTypeEnum.TEXT.name());
        idCardField.setInputSubType(InputSubTypeEnum.VALIDATE_ID_CARD.getCode());

        runTest("身份证_正确", idCardField, "110101199003076876");
        runTest("身份证_错误", idCardField, "12345");

        // 4️⃣ 统一社会信用代码
        TemplateFieldPO creditField = new TemplateFieldPO();
        creditField.setFieldName("统一社会信用代码");
        creditField.setFieldCode("creditCode");
        creditField.setFieldType(FieldTypeEnum.TEXT.name());
        creditField.setInputSubType(InputSubTypeEnum.VALIDATE_CREDIT_CODE.getCode());

        runTest("信用代码_正确", creditField, "91350200704024776P");
        runTest("信用代码_错误", creditField, "91350200704024776p"); // 小写 p

        // 5️⃣ 数值范围
        TemplateFieldPO numberField = new TemplateFieldPO();
        numberField.setFieldName("金额");
        numberField.setFieldCode("amount");
        numberField.setFieldType(FieldTypeEnum.NUMBER.name());
        numberField.setMinValue(new BigDecimal("10"));
        numberField.setMaxValue(new BigDecimal("100"));
        numberField.setDecimalScale(2);

        runTest("数值_正确", numberField, "55.12");
        runTest("数值_过小", numberField, "9");
        runTest("数值_过大", numberField, "101");
        runTest("数值_小数超限", numberField, "88.123");

        // 6️⃣ 正则匹配
        TemplateFieldPO regexField = new TemplateFieldPO();
        regexField.setFieldName("自定义字段");
        regexField.setFieldCode("regexField");
        regexField.setFieldType(FieldTypeEnum.TEXT.name());
        regexField.setPattern("^[A-Z]{3}\\d{2}$"); // 3位大写字母+2位数字

        runTest("正则_正确", regexField, "ABC12");
        runTest("正则_错误", regexField, "abc12");

        // 7️⃣ 长度限制
        TemplateFieldPO lengthField = new TemplateFieldPO();
        lengthField.setFieldName("备注");
        lengthField.setFieldCode("remark");
        lengthField.setFieldType(FieldTypeEnum.TEXTAREA.name());
        lengthField.setMinLength(3);
        lengthField.setMaxLength(10);

        runTest("长度_正确", lengthField, "hello");
        runTest("长度_过短", lengthField, "hi");
        runTest("长度_过长", lengthField, "this is too long");
    }
}


