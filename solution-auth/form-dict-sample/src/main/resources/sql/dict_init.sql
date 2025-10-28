
# 字典类型表

CREATE TABLE `sys_dict_type` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dict_code` varchar(100) NOT NULL COMMENT '字典编码（唯一）',
  `dict_name` varchar(200) NOT NULL COMMENT '字典名称',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `status` tinyint DEFAULT 1 COMMENT '状态 1=启用 0=禁用',
  `is_system` tinyint DEFAULT 0 COMMENT '是否系统内置 1=系统 0=自定义',

  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` bigint DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint DEFAULT NULL COMMENT '更新人',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除 0=否 1=是',

  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dict_code` (`dict_code`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统字典类型表';


CREATE TABLE `sys_dict_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dict_code` varchar(100) NOT NULL COMMENT '关联字典类型编码（逻辑外键: sys_dict.dict_code）',
  `item_value` varchar(100) NOT NULL COMMENT '字典项值',
  `item_name` varchar(200) NOT NULL COMMENT '字典项名称',
  `item_sort` int DEFAULT 0 COMMENT '排序值',
  `parent_value` varchar(100) DEFAULT NULL COMMENT '父级值（支持层级结构）',
  `tag_color` varchar(32) DEFAULT NULL COMMENT '标签颜色',
  `status` tinyint DEFAULT 1 COMMENT '状态 1=启用 0=禁用',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',

  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` bigint DEFAULT NULL COMMENT '创建人',
  `updated_by` bigint DEFAULT NULL COMMENT '更新人',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除 0=否 1=是',

  PRIMARY KEY (`id`),
  KEY `idx_dict_code` (`dict_code`),
  KEY `idx_parent_value` (`parent_value`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统字典项表';


-- 字典类型
INSERT INTO `sys_dict_type` (`dict_code`, `dict_name`, `description`, `is_system`)
VALUES
('GENDER', '性别', '系统内置性别字典', 1),
('DEVICE_STATUS', '设备状态', '设备运行状态', 1);

-- 字典项
INSERT INTO `sys_dict_item` (`dict_code`, `item_value`, `item_name`, `item_sort`)
VALUES
('GENDER', '1', '男', 1),
('GENDER', '2', '女', 2),
('DEVICE_STATUS', 'ONLINE', '在线', 1),
('DEVICE_STATUS', 'OFFLINE', '离线', 2),
('DEVICE_STATUS', 'MAINTAIN', '维护中', 3);
