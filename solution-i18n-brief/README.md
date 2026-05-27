# solution-i18n-brief

简化版国际化处理模块，支持默认返回中文、英文环境自动翻译的业务场景。

## 核心特性

1. **默认返回中文**：字段值直接存储中文，默认情况下不做任何转换
2. **英文环境翻译**：仅当请求头指定英文环境时，自动将中文翻译为英文
3. **支持上下文区分**：通过 `prefix` 属性区分不同业务场景的翻译
4. **全局通用翻译**：不指定 `prefix` 时使用全局通用翻译 key

## 快速开始

### 1. 引入依赖

```xml
<dependency>
    <groupId>com.cv</groupId>
    <artifactId>i18n-core-brief</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 启用国际化

```java
@SpringBootApplication
@EnableSolutionI18n
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 3. 配置消息文件

在 `resources/i18n/` 目录下创建消息配置文件：

**messages.properties（英文）**：
```properties
# 带前缀的翻译 - 订单状态
order.status.已创建=Created
order.status.已支付=Paid
order.status.已发货=Shipped

# 带前缀的翻译 - 订单标签
order.tag.紧急=Urgent
order.tag.线上=Online
order.tag.普通=Normal
order.tag.赠品=Gift

# 全局通用翻译（不带前缀）
已创建=Created
已支付=Paid
已发货=Shipped
紧急=Urgent

# 动态参数翻译
order.summary={0} paid {1}
```

**messages_zh_CN.properties（中文）**：
```properties
# 带前缀的翻译 - 订单状态
order.status.已创建=已创建
order.status.已支付=已支付
order.status.已发货=已发货

# 带前缀的翻译 - 订单标签
order.tag.紧急=紧急
order.tag.线上=线上
order.tag.普通=普通
order.tag.赠品=赠品

# 全局通用翻译（不带前缀）
已创建=已创建
已支付=已支付
已发货=已发货
紧急=紧急

# 动态参数翻译
order.summary={0} 已支付 {1} 元
```

## 用法说明

### 1. @I18nField 注解

#### 带 prefix（区分业务场景）

```java
public class OrderDetailDTO {
    
    @I18nField(prefix = "order.status.")
    private String statusCode;  // 值：已支付
    
    @I18nField(prefix = "order.tag.", target = "tagName")
    private String tag;  // 值：紧急
}
```

翻译逻辑：
- 中文环境：`statusCode` = "已支付"，`statusCodeText` = "已支付"
- 英文环境：`statusCode` = "已支付"，`statusCodeText` = "Paid"（根据 `order.status.已支付` 查找）

#### 不带 prefix（全局通用）

```java
public class CommonDTO {
    
    @I18nField
    private String status;  // 值：已支付
}
```

翻译逻辑：
- 中文环境：`status` = "已支付"，`statusText` = "已支付"
- 英文环境：`status` = "已支付"，`statusText` = "Paid"（根据全局 key `已支付` 查找）

#### replace 属性（替换原字段）

```java
public class OrderDTO {
    
    @I18nField(prefix = "order.status.", replace = true)
    private String status;  // 值：已支付
}
```

翻译逻辑：
- 中文环境：`status` = "已支付"（不生成 statusText）
- 英文环境：`status` = "Paid"（直接替换原值）

#### 动态参数

```java
public class OrderDTO {
    
    @I18nField(value = "order.summary", target = "summary", args = {"customerName", "amount"})
    private String customerName;  // 值：Tom
    
    private BigDecimal amount;  // 值：128.50
}
```

翻译结果：
- 中文环境：`summary` = "Tom 已支付 128.50 元"
- 英文环境：`summary` = "Tom paid 128.50"

### 2. I18nEnum 接口（枚举自动翻译）

```java
public enum OrderStatusEnum implements I18nEnum {
    
    CREATED("已创建"),
    PAID("已支付"),
    SHIPPED("已发货");
    
    private final String defaultMessage;
    
    OrderStatusEnum(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }
    
    @Override
    public String getI18nCode() {
        return defaultMessage;
    }
    
    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }
}
```

使用示例：
```java
// 中文环境
OrderStatusEnum status = OrderStatusEnum.PAID;
// 输出：已支付

// 英文环境  
OrderStatusEnum status = OrderStatusEnum.PAID;
// 输出：Paid（根据全局 key "已支付" 查找）
```

### 3. List 和 Map 字段翻译

#### List 字段

```java
public class OrderDTO {
    
    @I18nField(prefix = "order.status.", target = "statusNameList")
    private List<String> statusList;  // 值：["已支付", "已发货"]
}
```

翻译结果：
- 中文环境：`statusNameList` = ["已支付", "已发货"]
- 英文环境：`statusNameList` = ["Paid", "Shipped"]

#### Map 字段

```java
public class OrderDTO {
    
    @I18nField(prefix = "order.tag.", target = "tagNameMap")
    private Map<String, String> tags;  // 值：{"priority": "紧急", "channel": "线上"}
}
```

翻译结果：
- 中文环境：`tagNameMap` = {"priority": "紧急", "channel": "线上"}
- 英文环境：`tagNameMap` = {"priority": "Urgent", "channel": "Online"}

## 请求头配置

通过 `Accept-Language` 请求头指定语言环境：

```http
GET /api/orders
Accept-Language: en  # 英文环境

GET /api/orders  
Accept-Language: zh-CN  # 中文环境（默认）
```

## 设计优势

### 1. 默认返回中文
- 字段直接存储中文值，无需额外转换
- 中文环境下性能更好，无需查找翻译

### 2. 上下文区分
- 通过 `prefix` 区分不同业务场景
- 避免同一中文值在不同场景下的翻译冲突

### 3. 灵活配置
- 支持带前缀和不带前缀两种模式
- 支持替换原字段或生成新字段

## 完整示例

### DTO 定义

```java
public class OrderDetailDTO {
    
    private String orderNo;
    
    private OrderStatusEnum status;  // 枚举自动翻译
    
    @I18nField(prefix = "order.status.")
    private String statusCode;  // 带前缀
    
    @I18nField(value = "order.summary", target = "summary", args = {"customerName", "amount"})
    private String customerName;
    
    private BigDecimal amount;
    
    @I18nField(prefix = "order.tag.", target = "tagNameMap")
    private Map<String, String> tags;
}
```

### 输入数据

```java
OrderDetailDTO dto = new OrderDetailDTO();
dto.setOrderNo("SO20260526001");
dto.setStatus(OrderStatusEnum.PAID);
dto.setStatusCode("已支付");
dto.setCustomerName("Tom");
dto.setAmount(new BigDecimal("128.50"));

Map<String, String> tags = new HashMap<>();
tags.put("priority", "紧急");
tags.put("channel", "线上");
dto.setTags(tags);
```

### 输出结果

**中文环境（Accept-Language: zh-CN）**：
```json
{
    "orderNo": "SO20260526001",
    "status": "已支付",
    "statusCode": "已支付",
    "statusCodeText": "已支付",
    "customerName": "Tom",
    "amount": 128.50,
    "summary": "Tom 已支付 128.50 元",
    "tags": {"priority": "紧急", "channel": "线上"},
    "tagNameMap": {"priority": "紧急", "channel": "线上"}
}
```

**英文环境（Accept-Language: en）**：
```json
{
    "orderNo": "SO20260526001",
    "status": "Paid",
    "statusCode": "已支付",
    "statusCodeText": "Paid",
    "customerName": "Tom",
    "amount": 128.50,
    "summary": "Tom paid 128.50",
    "tags": {"priority": "紧急", "channel": "线上"},
    "tagNameMap": {"priority": "Urgent", "channel": "Online"}
}
```

## 注意事项

1. **消息文件格式**：key 为中文值（或 prefix + 中文值），value 为目标语言翻译
2. **枚举实现**：枚举必须实现 `I18nEnum` 接口
3. **嵌套对象**：嵌套对象中的 `@I18nField` 注解也会被处理
4. **默认语言**：未指定 `Accept-Language` 时默认使用中文

## 项目结构

```
solution-i18n-brief/
├── i18n-core-brief/           # 核心模块
│   ├── src/main/java/com/cv/i18n/
│   │   ├── annotation/        # 注解定义
│   │   │   ├── EnableSolutionI18n.java
│   │   │   ├── I18nField.java
│   │   │   └── I18nParam.java
│   │   ├── config/            # 配置类
│   │   ├── context/           # 上下文管理
│   │   ├── core/              # 核心接口
│   │   ├── filter/            # 过滤器
│   │   ├── interceptor/       # 拦截器
│   │   └── processor/         # 处理器
│   └── pom.xml
├── i18n-core-brief-sample/    # 示例模块
│   ├── src/main/
│   │   ├── java/com/cv/i18n/sample/
│   │   │   ├── controller/    # 控制器示例
│   │   │   ├── dto/           # 数据传输对象
│   │   │   ├── enums/         # 枚举定义
│   │   │   └── I18nCoreSampleApplication.java
│   │   └── resources/
│   │       ├── i18n/          # 消息配置
│   │       └── application.yml
│   └── pom.xml
└── pom.xml
```