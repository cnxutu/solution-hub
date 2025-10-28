/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.dict.pojo.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**   
 * 系统字典项表 service转换 DTO 类
 *
 * 主要用于 dubbo rpc 调用序列化对象数据传输
 *
 * @author xutu
 * @date 2025-10-28 09:22:53
 */
@Data
public class SysDictItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @NotNull(message = "唯一标识不能为空")
    private Long id;
    /**
     * 所属字典编码
     */
    @NotBlank(message = "所属字典编码不能为空")
	private String dictCode;
    /**
     * 字典项值
     */
    @NotBlank(message = "字典项值不能为空")
	private String itemValue;
    /**
     * 字典项名称
     */
    @NotBlank(message = "字典项名称不能为空")
	private String itemName;
    /**
     * 排序值
     */
    @NotNull(message = "排序值不能为空")
	private Integer itemSort;
    /**
     * 父级值（支持层级结构）
     */
    @NotBlank(message = "父级值（支持层级结构）不能为空")
	private String parentValue;
    /**
     * 标签颜色
     */
    @NotBlank(message = "标签颜色不能为空")
	private String tagColor;
    /**
     * 状态 1=启用 0=禁用
     */
    @NotNull(message = "状态 1=启用 0=禁用不能为空")
	private Integer status;
    /**
     * 备注
     */
    @NotBlank(message = "备注不能为空")
	private String remark;

}
