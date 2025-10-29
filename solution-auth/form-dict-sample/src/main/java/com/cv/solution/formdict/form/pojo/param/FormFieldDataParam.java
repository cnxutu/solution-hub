package com.cv.solution.formdict.form.pojo.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author: xutu
 * @since: 2025/10/29 16:46
 */
@Data
public class FormFieldDataParam {

    /**
     * 字段编码
     */
    @NotBlank(message = "字段编码不能为空")
    private String fieldCode;

    /**
     * 字段值
     * 对于多选/复选可以传数组或逗号分隔字符串
     * 对于时间类型可以直接传 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss
     */
    @NotNull(message = "字段值不能为空")
    private String fieldValue;


}
