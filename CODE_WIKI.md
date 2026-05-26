# Solution-Hub 项目代码文档

## 1. 项目概述

**solution-hub** 是一个基于 Spring Boot 的解决方案集合，包含认证授权和国际化两大核心模块，为企业级应用提供标准化的技术组件。

### 1.1 项目架构

```
solution-hub/
├── solution-auth/           # 认证及基础服务解决方案
│   ├── form-dict-sample/    # 表单字典模块
│   └── jwt-login-sample/    # JWT登录模块
└── solution-i18n/           # 国际化适配模块
    ├── i18n-core/           # 核心国际化能力
    └── i18n-core-sample/    # 示例工程
```

### 1.2 技术栈

| 分类 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 17 |
| 框架 | Spring Boot | 2.7.x / 3.x |
| ORM | MyBatis-Plus | 3.x |
| 数据库 | MySQL | 8.0+ |
| JWT | jjwt | 0.12.x |
| JSON | Jackson | 2.x |

---

## 2. 模块职责

### 2.1 solution-auth（认证及基础服务）

| 子模块 | 职责 | 核心功能 |
|--------|------|----------|
| **form-dict-sample** | 系统字典与表单模板管理 | 字典类型/项管理、表单模板配置、表单数据录入、动态字段校验 |
| **jwt-login-sample** | JWT登录认证 | 多类型登录（Web/微信/App）、Token生成与校验、登录流程编排 |

### 2.2 solution-i18n（国际化模块）

| 子模块 | 职责 | 核心功能 |
|--------|------|----------|
| **i18n-core** | 国际化核心能力 | HandlerInterceptor语言解析、注解驱动翻译、枚举自动翻译、ResponseBodyAdvice统一处理 |
| **i18n-core-sample** | 示例验证 | 演示国际化功能的实际使用 |

---

## 3. 核心模块详解

### 3.1 i18n-core（国际化核心）

#### 3.1.1 架构设计

```
┌─────────────────────────────────────────────────────────────────┐
│                      请求处理流程                                 │
├─────────────────────────────────────────────────────────────────┤
│  Request → I18nLocaleInterceptor → Controller → I18nResponseBodyAdvice → Response │
│                   │                                       │
│                   ▼                                       ▼
│          I18nContextHolder                        I18nValueProcessor │
│                   │                                       │
│                   ▼                                       ▼
│              Locale存储                          I18nMessageResolver │
│                                                              │
│                                                              ▼
│                                                      SpringMessageSource │
└─────────────────────────────────────────────────────────────────┘
```

**核心特点**：
- 使用 `HandlerInterceptor` 而非 Filter，确保在 `ResponseBodyAdvice` 执行时 locale 仍可用
- 注解驱动，对原有代码无侵入
- 自动处理枚举、集合、Map、嵌套对象的翻译

#### 3.1.2 关键类与函数

**注解类**

| 类名 | 文件位置 | 功能 | 核心属性 |
|------|----------|------|----------|
| `EnableSolutionI18n` | `annotation/EnableSolutionI18n.java` | 启用国际化功能 | 导入自动配置类 |
| `I18nField` | `annotation/I18nField.java` | 标记需翻译字段 | `value`, `prefix`, `target`, `args`, `replace` |
| `I18nParam` | `annotation/I18nParam.java` | 标记方法参数（预留） | `value` |

**核心接口**

| 接口名 | 文件位置 | 功能 | 关键方法 |
|--------|----------|------|----------|
| `I18nEnum` | `core/I18nEnum.java` | 枚举国际化契约 | `getI18nCode()`, `getDefaultMessage()` |
| `I18nMessageResolver` | `core/I18nMessageResolver.java` | 消息解析器接口 | `resolve(code, locale, args, defaultMessage)` |

**实现类**

| 类名 | 文件位置 | 功能 | 核心方法 |
|------|----------|------|----------|
| `SpringMessageI18nResolver` | `core/SpringMessageI18nResolver.java` | Spring消息源实现 | 委托Spring MessageSource解析 |
| `I18nValueProcessor` | `processor/I18nValueProcessor.java` | 值处理核心 | `process(value)` - 递归处理对象/集合/Map |
| `I18nResponseBodyAdvice` | `processor/I18nResponseBodyAdvice.java` | 响应体增强 | `beforeBodyWrite()` - 统一翻译响应数据 |
| `I18nLocaleInterceptor` | `interceptor/I18nLocaleInterceptor.java` | 语言解析拦截器 | 从Header提取语言标识，设置到ContextHolder |
| `I18nContextHolder` | `context/I18nContextHolder.java` | 上下文Holder | ThreadLocal存储Locale |
| `SolutionI18nAutoConfiguration` | `config/SolutionI18nAutoConfiguration.java` | 自动配置类 | 注册所有Bean |
| `MessageSourceConfig` | `config/MessageSourceConfig.java` | MessageSource配置 | 配置资源文件路径和编码 |

**I18nValueProcessor 核心逻辑**

```java
// 处理流程
process(value)
  ├─ null或简单值 → translateEnum() - 枚举自动翻译
  ├─ Collection → 遍历递归处理每个元素
  ├─ Map → 遍历递归处理每个value
  └─ Bean → processBean() - 反射遍历字段
      ├─ 字段含@I18nField → translateAnnotatedValue()
      │   └─ resolve() → messageResolver.resolve()
      └─ 嵌套Bean → 递归调用processBean()
```

#### 3.1.3 配置说明

**1. 启用国际化**

在 Spring Boot 启动类上添加 `@EnableSolutionI18n` 注解：

```java
@EnableSolutionI18n
@SpringBootApplication
@ComponentScan(basePackages = "com.cv.i18n")
public class I18nCoreSampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(I18nCoreSampleApplication.class, args);
    }
}
```

**2. 资源文件配置**

在 `application.yml` 中配置：

```yaml
spring:
  messages:
    basename: i18n/messages
    encoding: UTF-8
```

**3. 语言优先级**

1. `X-Language` Header（优先）
2. `Accept-Language` Header
3. 系统默认Locale

---

## 4. i18n-core 使用指南

### 4.1 快速开始

**Step 1: 添加依赖**

```xml
<dependency>
    <groupId>com.cv</groupId>
    <artifactId>i18n-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Step 2: 启用国际化**

```java
@EnableSolutionI18n
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

**Step 3: 创建资源文件**

创建 `src/main/resources/i18n/messages.properties`（默认/英文）：

```properties
order.status.created=Created
order.status.paid=Paid
order.status.shipped=Shipped
order.tag.urgent=Urgent
order.tag.normal=Normal
order.summary={0} paid {1}
```

创建 `src/main/resources/i18n/messages_zh_CN.properties`（中文）：

```properties
order.status.created=已创建
order.status.paid=已支付
order.status.shipped=已发货
order.tag.urgent=紧急
order.tag.normal=普通
order.summary={0} 已支付 {1} 元
```

**Step 4: 请求测试**

```bash
# 请求中文
curl -H "X-Language: zh-CN" http://localhost:8080/api/order

# 请求英文
curl -H "X-Language: en-US" http://localhost:8080/api/order
```

---

### 4.2 场景一：枚举自动翻译

**场景说明**：后端返回枚举类型，前端需要显示对应的中文/英文描述。

**定义国际化枚举**：

```java
public enum OrderStatusEnum implements I18nEnum {
    CREATED("order.status.created", "Created"),
    PAID("order.status.paid", "Paid"),
    SHIPPED("order.status.shipped", "Shipped");

    private final String i18nCode;
    private final String defaultMessage;

    OrderStatusEnum(String i18nCode, String defaultMessage) {
        this.i18nCode = i18nCode;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String getI18nCode() {
        return i18nCode;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }
}
```

**DTO中使用**：

```java
public class OrderDTO {
    private String orderNo;
    private OrderStatusEnum status;  // 枚举字段，会自动翻译

    public OrderStatusEnum getStatus() {
        return status;
    }
}
```

**Controller**：

```java
@RestController
@RequestMapping("/api")
public class OrderController {
    @GetMapping("/order")
    public OrderDTO getOrder() {
        OrderDTO dto = new OrderDTO();
        dto.setOrderNo("SO20260526001");
        dto.setStatus(OrderStatusEnum.PAID);
        return dto;
    }
}
```

**请求响应**：

| 请求头 | 响应 `status` 字段 |
|--------|-------------------|
| `X-Language: zh-CN` | `"已支付"` |
| `X-Language: en-US` | `"Paid"` |

---

### 4.3 场景二：字段值翻译（带prefix）

**场景说明**：数据库存储状态码（如 `paid`、`shipped`），需要翻译为中文/英文显示。

**资源文件**：

```properties
order.status.created=已创建
order.status.paid=已支付
order.status.shipped=已发货
```

**DTO中使用 `@I18nField(prefix=...)`**：

```java
public class OrderDTO {
    private String orderNo;

    @I18nField(prefix = "order.status.")  // 会拼接为 order.status.paid
    private String statusCode;  // 原始值: "paid"

    // 翻译结果会输出到 statusCodeText 字段
}
```

**请求响应**：

```json
{
    "orderNo": "SO20260526001",
    "statusCode": "paid",         // 原始值不变
    "statusCodeText": "已支付"     // 新增翻译字段
}
```

---

### 4.4 场景三：字段值翻译（指定key）

**场景说明**：需要使用特定的国际化key，而非拼接prefix。

**资源文件**：

```properties
order.status.paid=已支付
order.status.shipped=已发货
```

**DTO中使用 `@I18nField(value=...)`**：

```java
public class OrderDTO {
    @I18nField(value = "order.status.paid")  // 指定完整key
    private String statusCode;
}
```

---

### 4.5 场景四：动态参数翻译

**场景说明**：翻译文本中包含动态参数，如订单金额、客户名称等。

**资源文件**：

```properties
order.summary={0} 已支付 {1} 元
order.summary={0} paid {1}
```

**DTO中使用 `@I18nField(args=...)`**：

```java
public class OrderDTO {
    private String customerName;
    private BigDecimal amount;

    @I18nField(value = "order.summary", target = "summary", args = {"customerName", "amount"})
    private String customerName;  // 第一个参数来源
    // amount 作为第二个参数
}
```

**请求响应**：

```json
{
    "customerName": "Tom",
    "amount": 128.50,
    "summary": "Tom 已支付 128.50 元"  // 自动填充参数
}
```

| 参数 | 说明 |
|------|------|
| `value` | 国际化消息key |
| `target` | 输出字段名（可选，默认原字段名+Text） |
| `args` | 参数数组，引用DTO中的其他字段 |

---

### 4.6 场景五：List字段翻译

**场景说明**：一个字段包含多个状态码列表，需要全部翻译。

**资源文件**：

```properties
order.status.paid=已支付
order.status.shipped=已发货
```

**DTO中使用**：

```java
public class OrderDTO {
    @I18nField(prefix = "order.status.", target = "statusNameList")
    private List<String> statusList;  // 原始值: ["paid", "shipped"]
}
```

**请求响应**：

```json
{
    "statusList": ["paid", "shipped"],          // 原始值不变
    "statusNameList": ["已支付", "已发货"]       // 新增翻译字段
}
```

---

### 4.7 场景六：Map字段翻译

**场景说明**：一个字段包含键值对Map，需要翻译所有value。

**资源文件**：

```properties
order.tag.urgent=紧急
order.tag.online=线上
order.tag.normal=普通
```

**DTO中使用**：

```java
public class OrderDTO {
    @I18nField(prefix = "order.tag.", target = "tagNameMap")
    private Map<String, String> tags;  // 原始值: {"priority": "urgent", "channel": "online"}
}
```

**请求响应**：

```json
{
    "tags": {"priority": "urgent", "channel": "online"},  // 原始值不变
    "tagNameMap": {"priority": "紧急", "channel": "线上"}   // 新增翻译字段
}
```

---

### 4.8 场景七：嵌套对象翻译

**场景说明**：DTO中包含子对象，子对象中的字段也需要翻译。

**定义子DTO**：

```java
public class OrderItemDTO {
    private String skuName;
    private Integer quantity;

    @I18nField(prefix = "order.tag.")
    private String tag;  // 子对象中的翻译字段
}
```

**主DTO**：

```java
public class OrderDTO {
    private String orderNo;
    private List<OrderItemDTO> items;  // 嵌套对象
}
```

**Controller**：

```java
@RestController
public class OrderController {
    @GetMapping("/order")
    public OrderDTO getOrder() {
        OrderDTO dto = new OrderDTO();
        dto.setOrderNo("SO20260526001");
        dto.setItems(Arrays.asList(
            new OrderItemDTO("iPhone Case", 2, "normal"),
            new OrderItemDTO("USB-C Cable", 1, "gift")
        ));
        return dto;
    }
}
```

**请求响应**：

```json
{
    "orderNo": "SO20260526001",
    "items": [
        {"skuName": "iPhone Case", "quantity": 2, "tag": "normal", "tagText": "普通"},
        {"skuName": "USB-C Cable", "quantity": 1, "tag": "gift", "tagText": "赠品"}
    ]
}
```

---

### 4.9 场景八：替换原字段值

**场景说明**：不希望保留原始值，直接用翻译结果替换。

**DTO中使用 `@I18nField(replace=true)`**：

```java
public class OrderDTO {
    @I18nField(prefix = "order.status.", replace = true)
    private String statusCode;  // replace=true 时，原始值会被翻译结果替换
}
```

**请求响应**：

```json
{
    "statusCode": "已支付"      // 直接替换为翻译结果
    // 不会生成 statusCodeText
}
```

---

### 4.10 场景九：指定输出字段名

**场景说明**：不希望使用默认的 `{字段名}Text` 格式。

**DTO中使用 `@I18nField(target=...)`**：

```java
public class OrderDTO {
    @I18nField(prefix = "order.status.", target = "statusDisplayName")
    private String statusCode;
}
```

**请求响应**：

```json
{
    "statusCode": "paid",
    "statusDisplayName": "已支付"  // 使用指定的输出字段名
}
```

---

### 4.11 完整示例

**OrderDetailDTO**：

```java
public class OrderDetailDTO {
    private String orderNo;
    private OrderStatusEnum status;
    @I18nField(prefix = "order.status.")
    private String statusCode;
    @I18nField(value = "order.summary", target = "summary", args = {"customerName", "amount"})
    private String customerName;
    private BigDecimal amount;
    @I18nField(prefix = "order.status.", target = "statusNameList")
    private List<String> statusList;
    @I18nField(prefix = "order.tag.", target = "tagNameMap")
    private Map<String, String> tags;
    private List<OrderItemDTO> items;
}
```

**Controller**：

```java
@RestController
@RequestMapping("/i18n/demo")
public class I18nDemoController {
    @GetMapping("/order")
    public OrderDetailDTO order() {
        Map<String, String> tagMap = new LinkedHashMap<>();
        tagMap.put("priority", "urgent");
        tagMap.put("channel", "online");
        return new OrderDetailDTO(
            "SO20260526001",
            OrderStatusEnum.PAID,
            "paid",
            "Tom",
            new BigDecimal("128.50"),
            Arrays.asList("paid", "shipped"),
            tagMap,
            Arrays.asList(
                new OrderItemDTO("iPhone Case", 2, "normal"),
                new OrderItemDTO("USB-C Cable", 1, "gift")
            )
        );
    }
}
```

**中文响应** (`X-Language: zh-CN`)：

```json
{
    "orderNo": "SO20260526001",
    "status": "已支付",
    "statusCode": "paid",
    "statusCodeText": "已支付",
    "customerName": "Tom",
    "amount": 128.50,
    "summary": "Tom 已支付 128.50 元",
    "statusList": ["paid", "shipped"],
    "statusNameList": ["已支付", "已发货"],
    "tags": {"priority": "urgent", "channel": "online"},
    "tagNameMap": {"priority": "紧急", "channel": "线上"},
    "items": [
        {"skuName": "iPhone Case", "quantity": 2, "tag": "normal", "tagText": "普通"},
        {"skuName": "USB-C Cable", "quantity": 1, "tag": "gift", "tagText": "赠品"}
    ]
}
```

**英文响应** (`X-Language: en-US`)：

```json
{
    "orderNo": "SO20260526001",
    "status": "Paid",
    "statusCode": "paid",
    "statusCodeText": "Paid",
    "customerName": "Tom",
    "amount": 128.50,
    "summary": "Tom paid 128.5",
    "statusList": ["paid", "shipped"],
    "statusNameList": ["Paid", "Shipped"],
    "tags": {"priority": "urgent", "channel": "online"},
    "tagNameMap": {"priority": "Urgent", "channel": "Online"},
    "items": [
        {"skuName": "iPhone Case", "quantity": 2, "tag": "normal", "tagText": "Normal"},
        {"skuName": "USB-C Cable", "quantity": 1, "tag": "gift", "tagText": "Gift"}
    ]
}
```

---

### 4.12 @I18nField 注解属性详解

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `value` | String | `""` | 国际化消息key，若为空则使用 prefix + 字段值 |
| `prefix` | String | `""` | 消息key前缀，会拼接在字段值前 |
| `target` | String | `""` | 输出字段名，默认 `{原字段名}Text`，如 `statusCodeText` |
| `defaultMessage` | String | `""` | 默认消息，当找不到国际化key时使用 |
| `args` | String[] | `{}` | 动态参数数组，引用同DTO中的其他字段 |
| `replace` | boolean | `false` | 是否替换原字段值，true时原字段会被翻译结果替换 |

**属性优先级**：`replace` > `target` > 默认 `{字段名}Text`

---

## 5. jwt-login-sample（JWT登录）

### 5.1 架构设计

```
┌─────────────────────────────────────────────────────────────┐
│                    登录流程架构                              │
├─────────────────────────────────────────────────────────────┤
│  AuthController → LoginHandlerFactory → AbstractLoginHandler │
│                                              │
│                    ┌─────────────────────────┼─────────────────────────┐
│                    ▼                         ▼                         ▼
│            WebLoginHandler           WeChatLoginHandler          AppLoginHandler │
│                    │                         │                         │
│                    └─────────────────────────┴─────────────────────────┘
│                                              │
│                                              ▼
│                                        TokenService → JwtUtils → Token │
└─────────────────────────────────────────────────────────────┘
```

### 5.2 关键类与函数

**枚举类**

| 枚举 | 功能 | 值 |
|------|------|----|
| `LoginTypeEnum` | 登录类型标识 | `WEB(1)`, `WECHAT(2)`, `APP(3)` |

**接口**

| 接口 | 功能 | 方法 |
|------|------|------|
| `ILoginFlow<Q,R>` | 登录流程契约 | `validateLoginParams()`, `authenticate()`, `afterAuthenticated()` |

**核心类**

| 类名 | 功能 | 核心方法 |
|------|------|----------|
| `JwtUtils` | JWT工具类 | `generateToken()`, `parseToken()`, `isTokenValid()` |
| `JwtProperties` | JWT配置 | `secret`, `expiration` |
| `AbstractLoginHandler` | 模板方法抽象类 | `login()` - 定义登录流程骨架 |
| `LoginHandlerFactory` | 工厂模式 | `getHandler(loginType)` - 获取对应处理器 |
| `WebLoginHandler` | Web登录处理器 | 实现账号密码认证 |
| `WeChatLoginHandler` | 微信登录处理器 | 实现微信OAuth认证 |

**登录流程（模板方法）**

```java
public final AuthResult<R> login(LoginQuery query) {
    // 1️⃣ 参数校验
    validateLoginParams(query);

    // 2️⃣ 前置校验（验证码、签名等）
    preLoginCheck(query);

    // 3️⃣ 用户认证（子类实现）
    UserDetailDTO user = authenticate(query);

    // 4️⃣ 后置处理
    afterAuthenticated(user, query);

    // 5️⃣ 生成Token
    String token = tokenService.generateToken(user);

    // 6️⃣ 构建响应
    return buildResult(user, token);
}
```

---

## 6. form-dict-sample（表单字典）

### 6.1 模块结构

```
form-dict-sample/
├── form/                     # 表单模块
│   ├── controller/           # REST API
│   ├── service/              # 业务逻辑
│   ├── mapper/               # 数据访问
│   ├── pojo/                 # 数据对象
│   │   ├── po/               # 持久层对象
│   │   ├── dto/              # 数据传输对象
│   │   ├── vo/               # 视图对象
│   │   ├── param/            # 请求参数
│   │   └── query/            # 查询参数
│   ├── facade/               # 门面层
│   └── common/               # 通用组件
│       ├── enums/            # 枚举定义
│       ├── validation/       # 校验服务
│       └── util/             # 工具类
└── dict/                     # 字典模块（结构同form）
```

### 6.2 关键类与函数

**枚举类**

| 枚举 | 功能 | 值 |
|------|------|----|
| `FieldTypeEnum` | 字段类型 | TEXT, TEXTAREA, NUMBER, SELECT, DATE, BOOLEAN等 |
| `InputSubTypeEnum` | 输入子类型 | PHONE, EMAIL, ID_CARD, CREDIT_CODE等 |

**校验服务**

| 类名 | 功能 | 核心方法 |
|------|------|----------|
| `FieldValidationService` | 字段校验服务 | `validateFieldData()` - 统一校验入口 |
| `FieldValidator` | 字段类型校验器接口 | `validate(field, value)` |
| `TextFieldValidator` | 文本字段校验器 | 处理TEXT类型字段 |
| `NumberFieldValidator` | 数字字段校验器 | 处理NUMBER类型字段 |
| `InputSubTypeValidator` | 子类型校验器接口 | 如PhoneValidator, EmailValidator等 |

**服务层**

| 接口 | 功能 | 主要方法 |
|------|------|----------|
| `ITemplateService` | 模板服务 | `pageList()`, `add()`, `edit()`, `delete()`, `detail()` |
| `ITemplateFieldService` | 字段服务 | CRUD操作 |
| `ITemplateFieldOptionService` | 字段选项服务 | 下拉选项管理 |
| `ITemplateDataService` | 表单数据服务 | 数据录入与查询 |
| `ISysDictTypeService` | 字典类型服务 | 字典类型管理 |
| `ISysDictItemService` | 字典项服务 | 字典项管理 |

**字段校验流程**

```java
public void validateFieldData(List<TemplateFieldPO> templateFields,
                              Map<String, FormFieldDataParam> fieldDataMap) {
    for (TemplateFieldPO field : templateFields) {
        // ✅ 必填校验
        if (field.getRequired() == 1 && value == null) {
            throw new IllegalArgumentException("必填项");
        }
        // ✅ 主类型校验（TextFieldValidator/NumberFieldValidator）
        FieldValidator validator = validatorMap.get(FieldTypeEnum.valueOf(field.getFieldType()));
        validator.validate(field, value);
        // ✅ 通用校验（长度/正则）
        if (str.length() > field.getMaxLength()) { ... }
        if (!Pattern.matches(field.getPattern(), str)) { ... }
    }
}
```

---

## 7. 依赖关系

### 7.1 模块依赖图

```
solution-hub (root)
    ├── solution-auth
    │   ├── form-dict-sample (依赖：mybatis-plus, spring-boot-starter-web)
    │   └── jwt-login-sample (依赖：jjwt, spring-boot-starter-web)
    └── solution-i18n
        ├── i18n-core (依赖：spring-boot-starter-web, jackson, slf4j)
        └── i18n-core-sample (依赖：i18n-core, spring-boot-starter-test)
```

### 7.2 核心依赖坐标

| 依赖 | GroupId | ArtifactId | 说明 |
|------|---------|------------|------|
| Spring Boot Starter | org.springframework.boot | spring-boot-starter-web | Web框架 |
| MyBatis-Plus | com.baomidou | mybatis-plus-spring-boot3-starter | ORM框架 |
| JWT | io.jsonwebtoken | jjwt-api | JWT支持 |
| JWT Impl | io.jsonwebtoken | jjwt-impl | JWT实现 |
| JWT Jackson | io.jsonwebtoken | jjwt-jackson | JWT JSON支持 |
| MySQL Driver | com.mysql | mysql-connector-j | 数据库驱动 |

---

## 8. 项目运行

### 8.1 环境要求

- JDK 17+（或 JDK 8+ for Spring Boot 2.7.x）
- Maven 3.8+
- MySQL 8.0+

### 8.2 启动前准备

**1. 创建数据库**

```sql
CREATE DATABASE `solution-hub` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**2. 执行初始化脚本**

```bash
# 字典模块初始化
mysql -u root -p solution-hub < solution-auth/form-dict-sample/src/main/resources/sql/dict_init.sql

# 表单模块初始化
mysql -u root -p solution-hub < solution-auth/form-dict-sample/src/main/resources/sql/form_init.sql
```

### 8.3 启动命令

**方式一：Maven运行**

```bash
# 运行 i18n-core-sample（国际化和单元测试）
cd solution-i18n/i18n-core-sample
mvn spring-boot:run

# 运行 form-dict-sample
cd solution-auth/form-dict-sample
mvn spring-boot:run
```

**方式二：打包运行**

```bash
# 打包
cd solution-hub
mvn clean package -DskipTests

# 运行
java -jar solution-i18n/i18n-core-sample/target/i18n-core-sample-1.0.0.jar
java -jar solution-auth/form-dict-sample/target/form-dict-sample-1.0.0.jar
```

### 8.4 服务端口

| 模块 | 端口 | 配置文件 |
|------|------|----------|
| i18n-core-sample | 8088 | application.yml |
| form-dict-sample | 18081 | application.yml |

---

## 9. API示例

### 9.1 国际化模块

**请求示例**

```bash
# 请求中文
curl -H "X-Language: zh-CN" http://localhost:8088/i18n/demo/order

# 请求英文
curl -H "X-Language: en-US" http://localhost:8088/i18n/demo/order

# 使用 Accept-Language（备选）
curl -H "Accept-Language: zh-CN,zh;q=0.9" http://localhost:8088/i18n/demo/order
```

### 9.2 表单字典模块

**获取模板列表**

```bash
GET /template/page?pageNum=1&pageSize=10
```

**创建模板**

```bash
POST /template
Content-Type: application/json

{
    "templateName": "订单申请表",
    "templateType": "ORDER",
    "description": "订单申请信息录入"
}
```

**录入表单数据**

```bash
POST /template-data
Content-Type: application/json

{
    "templateId": 1,
    "fieldDataList": [
        {"fieldCode": "customerName", "fieldValue": "张三"},
        {"fieldCode": "amount", "fieldValue": "1000"}
    ]
}
```

---

## 10. 扩展指南

### 10.1 扩展新的登录类型

1. 创建新的 Handler 继承 `AbstractLoginHandler`
2. 实现 `authenticate()` 方法
3. 在 `LoginTypeEnum` 中添加新类型

```java
@Component
public class CustomLoginHandler extends AbstractLoginHandler<CustomLoginVO> {
    @Override
    public LoginTypeEnum getLoginType() {
        return LoginTypeEnum.CUSTOM;
    }

    @Override
    protected UserDetailDTO authenticate(LoginQuery query) {
        // 自定义认证逻辑
    }
}
```

### 10.2 扩展新的字段校验器

1. 实现 `FieldValidator` 或 `InputSubTypeValidator` 接口
2. Spring会自动注册到 `FieldValidationService`

```java
@Component
public class CustomFieldValidator implements FieldValidator {
    @Override
    public FieldTypeEnum getFieldType() {
        return FieldTypeEnum.CUSTOM;
    }

    @Override
    public void validate(TemplateFieldPO field, Object value) {
        // 自定义校验逻辑
    }
}
```

### 10.3 扩展国际化消息源

1. 实现 `I18nMessageResolver` 接口
2. 可扩展 Redis/DB/远程配置中心实现

```java
@Component
public class RedisMessageResolver implements I18nMessageResolver {
    @Override
    public String resolve(String code, Locale locale, Object[] args, String defaultMessage) {
        // 从Redis读取国际化消息
    }
}
```

---

## 11. 代码约定

### 11.1 命名规范

| 类型 | 规则 | 示例 |
|------|------|------|
| 类名 | PascalCase | `TemplateServiceImpl` |
| 方法名 | camelCase | `pageList()` |
| 变量名 | camelCase | `templateId` |
| 常量名 | UPPER_SNAKE_CASE | `MAX_LENGTH` |
| 包名 | lowercase | `com.cv.solution.formdict` |

### 11.2 分层命名

| 层 | 后缀 | 示例 |
|----|------|------|
| Controller | `Controller` | `TemplateController` |
| Service接口 | `IService` | `ITemplateService` |
| Service实现 | `ServiceImpl` | `TemplateServiceImpl` |
| Mapper | `Mapper` | `TemplateMapper` |
| PO | `PO` | `TemplatePO` |
| VO | `VO` | `TemplateVO` |
| DTO | `DTO` | `TemplateDTO` |
| Param | `Param` | `TemplateParam` |
| Query | `Query` | `TemplatePageQuery` |

### 11.3 国际化资源文件命名

| 文件 | 用途 |
|------|------|
| `messages.properties` | 默认语言（通常为英文） |
| `messages_zh_CN.properties` | 简体中文 |
| `messages_zh_TW.properties` | 繁体中文 |
| `messages_en_US.properties` | 美式英文 |
| `messages_ja_JP.properties` | 日语 |

---

## 12. 后续演进建议

### 12.1 i18n-core

- 拆分为真正的 `spring-boot-starter`，使用 `AutoConfiguration.imports`
- 抽象 `I18nMessageResolver` 的 Redis、DB、远程配置中心实现
- 增加资源热更新、缓存淘汰、多租户 locale/message namespace
- 增加 SPI，用于扩展语言解析、key生成、缺省文案策略
- 支持更多语言变体（如 `zh-Hans` vs `zh-Hant`）

### 12.2 jwt-login-sample

- 完善 TokenService 实现
- 增加 Refresh Token 机制
- 实现登录失败次数限制、验证码功能
- 增加 OAuth2.0 支持

### 12.3 form-dict-sample

- 增加表单数据导出（Excel/PDF）
- 实现表单模板版本管理
- 增加表单数据权限控制
- 支持表单联动、动态显示隐藏逻辑

---

## 附录：测试覆盖

### 单元测试

| 测试类 | 测试内容 |
|--------|----------|
| `I18nCoreSampleApplicationTests` | 枚举翻译、@I18nField注解、List/Map翻译、嵌套对象、replace属性 |

### 集成测试

| 测试类 | 测试内容 |
|--------|----------|
| `I18nDemoControllerIntegrationTest` | X-Language头切换、Accept-Language备选、无语言头默认 |

运行测试：

```bash
cd solution-i18n/i18n-core-sample
mvn test
```