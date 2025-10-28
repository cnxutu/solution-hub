/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.pojo.vo;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**   
 * 模板录入数据表 新增接口入参
 *
 * @author xutu
 * @date 2025-10-28 19:44:00
 */
@Data
public class TemplateDataPageVO {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 所属模板ID
     */
	private Long templateId;
    /**
     * 录入记录ID，标识一次完整录入
     */
	private Long recordId;
    /**
     * 字段编码
     */
	private String fieldCode;
    /**
     * 字段值
     */
	private String fieldValue;

}
