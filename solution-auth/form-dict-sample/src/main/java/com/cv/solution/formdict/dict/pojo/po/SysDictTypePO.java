/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.dict.pojo.po;

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
 * 系统字典类型表 实体类
 *
 * @author xutu
 * @date 2025-10-28 09:22:53
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_dict_type")
public class SysDictTypePO extends BasePO  implements Serializable {

	private static final long serialVersionUID = 1761614573348L;

    /**
     * 字典编码（唯一）
     */
    @TableField("dict_code")
	private String dictCode;
    /**
     * 字典名称
     */
    @TableField("dict_name")
	private String dictName;
    /**
     * 描述
     */
	private String description;
    /**
     * 状态 1=启用 0=禁用
     */
	private Integer status;
    /**
     * 是否系统内置 1=系统 0=自定义
     */
    @TableField("is_system")
	private Integer isSystem;

}
