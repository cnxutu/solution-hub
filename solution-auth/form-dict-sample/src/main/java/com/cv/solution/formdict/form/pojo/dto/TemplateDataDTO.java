/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.pojo.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**   
 * 模板录入数据表 service转换 DTO 类
 *
 * 主要用于 dubbo rpc 调用序列化对象数据传输
 *
 * @author xutu
 * @date 2025-10-28 19:44:00
 */
@Data
public class TemplateDataDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @NotNull(message = "唯一标识不能为空")
    private Long id;
    /**
     * 所属模板ID
     */
    @NotNull(message = "所属模板ID不能为空")
	private Long templateId;
    /**
     * 录入记录ID，标识一次完整录入
     */
    @NotNull(message = "录入记录ID，标识一次完整录入不能为空")
	private Long recordId;
    /**
     * 字段编码
     */
    @NotBlank(message = "字段编码不能为空")
	private String fieldCode;
    /**
     * 字段值
     */
    @NotBlank(message = "字段值不能为空")
	private String fieldValue;

}
