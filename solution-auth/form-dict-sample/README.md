

## form-dict 数据库 ER 图

好的，我给你画一个文字版的 ER 图，展示模板模块的整体结构和字段关系：

```yaml
tpl_template (模板表)
+--------------------+
| id (PK)            |
| template_code      |
| template_name      |
| template_type      |
| status             |
| remark             |
| create_time        |
| update_time        |
| created_by         |
| updated_by         |
| is_deleted         |
+--------------------+
|
| 1:N
v
tpl_template_field (模板字段表)
+-----------------------------+
| id (PK)                     |
| template_id (FK) → tpl_template.id |
| field_code                  |
| field_name                  |
| field_type                  |
| option_source (1=字典,2=专属选项,3=自由输入) |
| dict_code (当 option_source=1) |
| required                    |
| default_value               |
| field_sort                  |
| status                      |
| remark                      |
| create_time                 |
| update_time                 |
| created_by                  |
| updated_by                  |
| is_deleted                  |
+-----------------------------+
|
| 1:N (仅当 option_source=2 使用)
v
tpl_template_field_option (模板字段专属选项表)
+-----------------------------+
| id (PK)                     |
| template_id (FK) → tpl_template.id |
| field_id (FK) → tpl_template_field.id |
| option_code                 |
| option_name                 |
| option_value                |
| version                     |
| sort_order                  |
| status                      |
| create_time                 |
| update_time                 |
| created_by                  |
| updated_by                  |
| is_deleted                  |
+-----------------------------+

tpl_template_data (模板录入数据表)
+-----------------------------+
| id (PK)                     |
| template_id (FK) → tpl_template.id |
| record_id (标识一次完整录入) |
| field_code                  |
| field_value                 |
| create_time                 |
| update_time                 |
| created_by                  |
| updated_by                  |
| is_deleted                  |
+-----------------------------+
```
🔹 说明
1.	tpl_template → tpl_template_field
•	一个模板有多个字段
2.	tpl_template_field → tpl_template_field_option
•	仅当字段来源为“模板专属选项”时才有多条选项
3.	tpl_template_data
•	纵表存储录入的数据
•	每次录入生成一个 record_id，对应模板下的多个字段数据
4.	字典绑定
•	当 tpl_template_field.option_source=1 时，dict_code 关联系统字典表 sys_dict / sys_dict_item
•	前端显示时根据来源动态选择来源：字典或专属选项

⸻

如果你愿意，我可以顺带帮你画一个 前端交互流程图，展示：
•	模板列表 → 字段配置 → 录入数据 → 下拉选项来源动态判断



## form-dict 模板+字段+字典+专属选项 数据录入流程

明白了，我帮你画一个文字版的 前端交互流程图，结合你当前模板+字段+字典+专属选项的设计：

```yaml
用户进入模板管理页面
    |
    v
tpl_template 列表分页展示
+--------------------------+
| 模板编码 | 模板名称 | 状态 |
+--------------------------+
| 增删改查按钮             |
+--------------------------+
    |
    | 点击“查看/配置字段”
    v
tpl_template_field 列表分页展示
+-----------------------------------------------------------+
| 字段编码 | 字段名称 | 类型 | 来源 | 必填 | 默认值 | 状态 |
+-----------------------------------------------------------+
| 增删改查按钮                                             |
+-----------------------------------------------------------+
    |
    | 点击“查看选项” （仅 option_source=2）
    v
tpl_template_field_option 列表分页展示
+---------------------------------------------+
| 选项编码 | 选项名称 | 选项值 | 版本 | 排序 |
+---------------------------------------------+
| 增删改查按钮                               |
+---------------------------------------------+

数据录入流程：
用户选择模板 → 展示模板字段
    |
    v
循环字段：
- 若 option_source=1 → 使用 sys_dict / sys_dict_item 作为下拉选项
- 若 option_source=2 → 使用 tpl_template_field_option 作为下拉选项
- 若 option_source=3 → 自由输入
      |
      v
  用户填写表单 → 点击保存
      |
      v
  tpl_template_data 写入纵表：
  (record_id, template_id, field_code, field_value)
      |
      v
  录入完成
```
🔹 特点
1.	字段来源动态判断
•	后端统一提供接口：getFieldOptions(fieldId)
•	前端无需关心是字典还是模板专属选项
2.	纵表数据录入
•	每次录入生成一个 record_id，对应模板字段的多条数据
3.	版本/扩展兼容
•	模板字段选项支持版本
•	后续可做字段版本化或多租户差异化表单

⸻

