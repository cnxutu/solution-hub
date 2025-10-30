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
 * 模板表 新增接口入参
 *
 * @author xutu
 * @date 2025-10-28 19:44:00
 */
@Data
public class TemplatePageVO {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 模板编码，唯一
     */
	private String templateCode;
    /**
     * 模板名称
     */
	private String templateName;
    /**
     * 模板类型，可用于分类
     */
	private Integer templateType;
    /**
     * 状态 1=启用 0=停用
     */
	private Integer status;
    /**
     * 备注
     */
	private String remark;

}
