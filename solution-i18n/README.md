# solution-i18n

`solution-i18n` 是一个面向 Spring Boot 的国际化适配模块，目前包含：

- `i18n-core`：核心国际化能力。
- `i18n-core-sample`：用于验证 core 行为的示例工程。

当前实现覆盖第一、第二阶段目标，第三阶段的 Redis、DB 国际化、热更新、starter 自动装配、SPI、多租户暂不处理，后续可以在现有扩展点上演进。

## 已实现能力

### 第一阶段

- Header 语言切换：默认读取 `X-Language`，未传时读取 `Accept-Language`。
- 自动国际化：Controller 返回对象时自动处理，无需业务手动调用翻译。
- Jackson 统一输出：通过 `ResponseBodyAdvice` 在 JSON 写出前统一转换，最终仍交给 Jackson 序列化。
- DTO 无侵入：DTO 不需要增加 `statusName`、`xxxText` 等字段。
- 注解驱动：通过 `@I18nField` 标记需要翻译的字段。

### 第二阶段

- Enum 自动翻译：枚举实现 `I18nEnum` 后会自动输出翻译文案。
- 动态参数：`@I18nField(args = {"customerName", "amount"})` 可引用同 DTO 里的属性作为 message 参数。
- List 自动处理：注解标记 List 字段后，会逐项翻译。
- Map 自动处理：注解标记 Map 字段后，会逐个翻译 value。
- 嵌套对象处理：对象、集合、Map 中的嵌套 DTO 会递归处理。

## 快速使用

### 1. 启用国际化

```java
@EnableSolutionI18n
@SpringBootApplication
public class Application {
}
```

### 2. 配置资源文件

```yaml
spring:
  messages:
    basename: i18n/messages
    encoding: UTF-8
```

示例：

```properties
order.status.paid=Paid
order.summary={0} paid {1}
```

```properties
order.status.paid=已支付
order.summary={0} 已支付 {1} 元
```

### 3. 标记 DTO 字段

```java
public class OrderDTO {

    @I18nField(prefix = "order.status.")
    private String statusCode;

    @I18nField(value = "order.summary", target = "summary", args = {"customerName", "amount"})
    private String customerName;
}
```

默认会额外输出 `{字段名}Text`，例如 `statusCodeText`。也可以通过 `target` 指定输出字段名。如果希望原字段直接替换为翻译文案，可以使用 `replace = true`。

### 4. 枚举翻译

```java
public enum OrderStatusEnum implements I18nEnum {
    PAID("order.status.paid", "Paid");

    private final String i18nCode;
    private final String defaultMessage;
}
```

返回 DTO 中如果包含该枚举，会自动输出为当前语言文案。

## 示例接口

启动 sample：

```bash
cd solution-i18n
mvn -pl i18n-core -am install -DskipTests
cd i18n-core-sample
mvn spring-boot:run
```

请求中文：

```bash
curl -H "X-Language: zh-CN" http://localhost:8088/i18n/demo/order
```

请求英文：

```bash
curl -H "X-Language: en-US" http://localhost:8088/i18n/demo/order
```

## 后续演进建议

- 将 `i18n-core` 拆为真正的 `spring-boot-starter`，使用 `AutoConfiguration.imports` 完成自动装配。
- 抽象 `I18nMessageResolver` 的 Redis、DB、远程配置中心实现。
- 增加资源热更新、缓存淘汰、多租户 locale/message namespace。
- 增加 SPI，用于项目侧扩展语言解析、key 生成、缺省文案策略。
