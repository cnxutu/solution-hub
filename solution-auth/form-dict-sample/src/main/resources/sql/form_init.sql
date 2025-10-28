CREATE TABLE tpl_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '模板ID',
    template_code VARCHAR(64) NOT NULL COMMENT '模板编码，唯一',
    template_name VARCHAR(128) NOT NULL COMMENT '模板名称',
    template_type VARCHAR(64) DEFAULT NULL COMMENT '模板类型，可用于分类',
    status TINYINT DEFAULT 1 COMMENT '状态 1=启用 0=停用',
    remark VARCHAR(256) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by BIGINT DEFAULT NULL COMMENT '创建人',
    updated_by BIGINT DEFAULT NULL COMMENT '更新人',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除 0=否 1=是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板表';


CREATE TABLE tpl_template_field (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '字段ID',
    template_id BIGINT NOT NULL COMMENT '所属模板ID',
    field_code VARCHAR(64) NOT NULL COMMENT '字段编码',
    field_name VARCHAR(128) NOT NULL COMMENT '字段名称',
    field_type VARCHAR(32) NOT NULL COMMENT '字段类型，如 text, number, select, date',
    option_source TINYINT DEFAULT 1 COMMENT '选项来源：1=字典(dict_code)，2=模板专属选项，3=自由输入',
    dict_code VARCHAR(64) DEFAULT NULL COMMENT '绑定的字典类型编码（option_source=1时使用）',
    required TINYINT DEFAULT 0 COMMENT '是否必填 0=否 1=是',
    default_value VARCHAR(256) DEFAULT NULL COMMENT '默认值',
    field_sort INT DEFAULT 0 COMMENT '字段排序',
    status TINYINT DEFAULT 1 COMMENT '字段状态 1=启用 0=停用',
    remark VARCHAR(256) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by BIGINT DEFAULT NULL COMMENT '创建人',
    updated_by BIGINT DEFAULT NULL COMMENT '更新人',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除 0=否 1=是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板字段表';


CREATE TABLE tpl_template_field_option (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '选项ID',
    template_id BIGINT NOT NULL COMMENT '所属模板ID',
    field_id BIGINT NOT NULL COMMENT '所属字段ID',
    option_code VARCHAR(64) NOT NULL COMMENT '选项编码',
    option_name VARCHAR(128) NOT NULL COMMENT '选项名称',
    option_value VARCHAR(256) DEFAULT NULL COMMENT '选项值（实际存储）',
    version VARCHAR(32) DEFAULT NULL COMMENT '版本号，可用于不同版本选项区分',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 1=启用 0=停用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by BIGINT DEFAULT NULL COMMENT '创建人',
    updated_by BIGINT DEFAULT NULL COMMENT '更新人',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除 0=否 1=是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板字段专属可选项表';


CREATE TABLE tpl_template_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    template_id BIGINT NOT NULL COMMENT '所属模板ID',
    record_id BIGINT NOT NULL COMMENT '录入记录ID，标识一次完整录入',
    field_code VARCHAR(64) NOT NULL COMMENT '字段编码',
    field_value TEXT COMMENT '字段值',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by BIGINT DEFAULT NULL COMMENT '创建人',
    updated_by BIGINT DEFAULT NULL COMMENT '更新人',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除 0=否 1=是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板录入数据表';