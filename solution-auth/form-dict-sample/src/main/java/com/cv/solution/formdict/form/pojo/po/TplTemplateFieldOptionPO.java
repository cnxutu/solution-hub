/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.pojo.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.cv.boot.mybatisplus.pojo.model.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**   
 * 模板字段专属可选项表 实体类
 *
 * @author xutu
 * @date 2025-10-28 19:28:01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("tpl_template_field_option")
public class TplTemplateFieldOptionPO extends BasePO  implements Serializable {

	private static final long serialVersionUID = 1761650881692L;

    /**
     * 所属模板ID
     */
    @TableField("template_id")
	private Long templateId;
    /**
     * 所属字段ID
     */
    @TableField("field_id")
	private Long fieldId;
    /**
     * 选项编码
     */
    @TableField("option_code")
	private String optionCode;
    /**
     * 选项名称
     */
    @TableField("option_name")
	private String optionName;
    /**
     * 选项值（实际存储）
     */
    @TableField("option_value")
	private String optionValue;
    /**
     * 版本号，可用于不同版本选项区分
     */
	private String version;
    /**
     * 排序
     */
    @TableField("sort_order")
	private Integer sortOrder;
    /**
     * 状态 1=启用 0=停用
     */
	private Integer status;

}
