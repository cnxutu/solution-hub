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
 * 模板录入数据表 实体类
 *
 * @author xutu
 * @date 2025-10-28 19:44:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("tpl_template_data")
public class TemplateDataPO extends BasePO  implements Serializable {

	private static final long serialVersionUID = 1761651841326L;

    /**
     * 所属模板ID
     */
    @TableField("template_id")
	private Long templateId;
    /**
     * 录入记录ID，标识一次完整录入
     */
    @TableField("record_id")
	private Long recordId;
    /**
     * 字段编码
     */
    @TableField("field_code")
	private String fieldCode;
    /**
     * 字段值
     */
    @TableField("field_value")
	private String fieldValue;

}
