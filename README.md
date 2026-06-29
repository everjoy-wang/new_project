# AI Gateway Platform (Java)

一个面向企业的 AI 网关平台样例项目，核心目标是统一解决 AI 应用落地中的通用工程问题：

- 多模型接入与路由降级
- 上下文吞吐与 Prompt 裁剪
- 结构化 JSON 输出校验
- Redis 缓存与配额治理
- MQ 异步任务削峰

> 这不是单一业务 Demo，而是可复用到客服、工单、运营分析、审批自动化等大多数 AI 项目的中台骨架。

## 1. 项目亮点（企业真实问题映射）

### 1.1 模型路由与降级
- 通过 `ModelRoutingService` 根据任务类型和输入规模选择模型。
- 首选模型失败时自动降级到备用模型，减少业务中断。

### 1.2 JSON 结构化输出治理
- 通过 `SchemaGuardService` 对模型输出进行 schema 校验。
- 当前内置 `support_ticket` 示例 schema（title/priority/summary）。

### 1.3 上下文吞吐控制
- `ContextEngine` 提供输入裁剪（避免超长上下文）与 token 粗估计。
- 为后续“记忆压缩、RAG 注入、预算控制”预留扩展点。

### 1.4 Redis 治理能力
- 响应缓存（热点请求降成本）
- 租户日配额计数（防止滥用）
- Redis 不可用时 fail-open，不阻断主流程

### 1.5 MQ 异步执行
- 提供异步接口：提交任务 -> 返回 taskId -> 轮询任务状态
- RabbitMQ 可用时走队列；不可用时自动回退为本地处理

## 2. 技术栈

- Java 21
- Spring Boot 3
- Spring Web / Validation / Actuator
- Spring Data Redis
- Spring AMQP (RabbitMQ)
- Resilience4j（已引入，可继续补充细粒度策略）
- Maven

## 3. 项目结构

```text
src/main/java/com/everjoy/aigateway
├── api                 # HTTP 接口与异常处理
├── async               # 异步任务提交/消费
├── config              # RabbitMQ 配置
├── context             # 上下文裁剪与 token 估算
├── model               # 请求/响应模型
├── provider            # 模型 Provider 抽象与实现
├── routing             # 模型路由策略
├── schema              # JSON Schema 守卫
└── service             # 核心业务编排（缓存/配额/路由/降级）
```

## 4. 快速启动

### 4.1 环境要求
- JDK 21
- Maven 3.9+
- 可选：Redis 7+ / RabbitMQ 3.12+

### 4.2 运行

```bash
mvn spring-boot:run
```

默认地址：`http://localhost:8080`

## 5. API 示例

### 5.1 同步调用

`POST /api/v1/chat`

```json
{
  "tenantId": "acme",
  "userId": "u1001",
  "taskType": "analysis",
  "prompt": "请总结本周线上事故并给出工单摘要",
  "requireJson": true,
  "schemaType": "support_ticket"
}
```

### 5.2 异步调用

1) 提交：`POST /api/v1/chat/async`  
2) 查询：`GET /api/v1/chat/async/{taskId}`

## 6. 测试

当前包含单测：
- 路由策略测试
- Schema 校验测试
- 上下文裁剪测试

```bash
mvn test
```

## 7. 后续建议（你可按阶段继续做）

1. 接入真实 LLM Provider（OpenAI / Azure OpenAI / 本地 vLLM）
2. 引入统一 Prompt 模板中心与版本管理
3. 增加审计日志、traceId 与调用成本看板
4. 引入 RAG（向量检索）降低幻觉
5. 增加策略中心（按租户/部门路由模型与预算）
