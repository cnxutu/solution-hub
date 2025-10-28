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
 * 系统字典项表 实体类
 *
 * @author xutu
 * @date 2025-10-28 09:22:53
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_dict_item")
public class SysDictItemPO extends BasePO  implements Serializable {

	private static final long serialVersionUID = 1761614573662L;

    /**
     * 所属字典编码
     */
    @TableField("dict_code")
	private String dictCode;
    /**
     * 字典项值
     */
    @TableField("item_value")
	private String itemValue;
    /**
     * 字典项名称
     */
    @TableField("item_name")
	private String itemName;
    /**
     * 排序值
     */
    @TableField("item_sort")
	private Integer itemSort;
    /**
     * 父级值（支持层级结构）
     */
    @TableField("parent_value")
	private String parentValue;
    /**
     * 标签颜色
     */
    @TableField("tag_color")
	private String tagColor;
    /**
     * 状态 1=启用 0=禁用
     */
	private Integer status;
    /**
     * 备注
     */
	private String remark;

}
