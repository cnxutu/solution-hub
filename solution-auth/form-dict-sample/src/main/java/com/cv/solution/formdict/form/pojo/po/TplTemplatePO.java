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
 * 模板表 实体类
 *
 * @author xutu
 * @date 2025-10-28 19:28:01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("tpl_template")
public class TplTemplatePO extends BasePO  implements Serializable {

	private static final long serialVersionUID = 1761650881364L;

    /**
     * 模板编码，唯一
     */
    @TableField("template_code")
	private String templateCode;
    /**
     * 模板名称
     */
    @TableField("template_name")
	private String templateName;
    /**
     * 模板类型，可用于分类
     */
    @TableField("template_type")
	private String templateType;
    /**
     * 状态 1=启用 0=停用
     */
	private Integer status;
    /**
     * 备注
     */
	private String remark;

}
