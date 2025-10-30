/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.pojo.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**   
 * 模板表 新增接口入参
 *
 * @author xutu
 * @date 2025-10-28 19:44:00
 */
@Data
public class TemplateParam {

    /**
     * 主键 id
     */
    private Long id;

    /**
     * 模板编码，唯一
     */
    @NotBlank(message = "模板编码，唯一不能为空")
	private String templateCode;
    /**
     * 模板名称
     */
    @NotBlank(message = "模板名称不能为空")
	private String templateName;
    /**
     * 模板类型，可用于分类
     */
    @NotBlank(message = "模板类型，可用于分类不能为空")
	private Integer templateType;
    /**
     * 状态 1=启用 0=停用
     */
    @NotNull(message = "状态 1=启用 0=停用不能为空")
	private Integer status;
    /**
     * 备注
     */
	private String remark;

    private List<TemplateFieldParam> templateFields;

}
