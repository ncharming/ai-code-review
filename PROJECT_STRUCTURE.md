# Spring Boot 3 项目结构

## 项目结构概览

```
ai-code-review/
├── pom.xml                                 # Maven 配置文件（Spring Boot 3）
├── src/
│   ├── main/
│   │   ├── java/com/practice/aicodereview/
│   │   │   ├── AiCodeReviewApplication.java          # Spring Boot 主启动类
│   │   │   ├── config/                                # 配置层
│   │   │   │   ├── ChatGlmConfig.java                 # ChatGLM 配置类
│   │   │   │   └── ChatGlmProperties.java             # ChatGLM 配置属性
│   │   │   ├── controller/                            # 控制器层
│   │   │   │   ├── CodeReviewController.java          # 代码评审控制器
│   │   │   │   └── GlobalExceptionHandler.java        # 全局异常处理器
│   │   │   ├── service/                               # 服务层
│   │   │   │   ├── CodeReviewService.java             # 代码评审服务接口
│   │   │   │   └── impl/
│   │   │   │       └── CodeReviewServiceImpl.java     # 代码评审服务实现
│   │   │   ├── model/                                 # 模型层
│   │   │   │   ├── dto/                               # 数据传输对象
│   │   │   │   │   ├── ChatCompletionRequest.java     # ChatGLM 请求 DTO
│   │   │   │   │   ├── ChatCompletionSyncResponse.java # ChatGLM 响应 DTO
│   │   │   │   │   ├── CodeReviewRequest.java         # 代码评审请求 DTO
│   │   │   │   │   └── CodeReviewResponse.java        # 代码评审响应 DTO
│   │   │   │   └── enums/                             # 枚举类
│   │   │   │       └── GlmModel.java                  # ChatGLM 模型枚举
│   │   │   └── utils/                                 # 工具类
│   │   │       └── BearerTokenUtils.java               # JWT Token 工具类
│   │   └── resources/
│   │       └── application.yml                        # 应用配置文件
│   └── test/                                          # 测试目录
└── .github/workflows/main.yml                         # GitHub Actions 工作流
```

## 核心模块说明

### 1. 主启动类
- **AiCodeReviewApplication.java**: Spring Boot 3 应用入口，使用 `@SpringBootApplication` 注解

### 2. 配置层（config/）
- **ChatGlmProperties.java**: 使用 `@ConfigurationProperties` 从 application.yml 读取配置
- **ChatGlmConfig.java**: 配置 Token 缓存等 Bean

### 3. 控制器层（controller/）
- **CodeReviewController.java**: 提供 REST API 接口
  - `POST /api/review/code`: 执行代码评审
  - `GET /api/review/git/{fromHash}/{toHash`: Git Diff 评审
  - `GET /api/review/health`: 健康检查
- **GlobalExceptionHandler.java**: 全局异常处理

### 4. 服务层（service/）
- **CodeReviewService.java**: 服务接口，定义业务逻辑
- **CodeReviewServiceImpl.java**: 服务实现，调用 ChatGLM API

### 5. 模型层（model/）
- **dto/**: 数据传输对象，用于 API 请求和响应
- **enums/**: 枚举类，定义 ChatGLM 模型

### 6. 工具类（utils/）
- **BearerTokenUtils.java**: JWT Token 生成和缓存

### 7. 配置文件（resources/）
- **application.yml**: 应用配置，包含服务器、ChatGLM API、日志等配置

## 主要变更

### 从原项目迁移到 Spring Boot 3 的变更：

1. **依赖管理**: 使用 Spring Boot Starter 父项目，统一管理依赖版本
2. **Java 版本**: 升级到 Java 17（Spring Boot 3 最低要求）
3. **包结构**: 重构为标准的 Spring Boot 分层架构
4. **配置管理**: 使用 application.yml 替代硬编码配置
5. **依赖注入**: 使用 Spring 的依赖注入（`@Autowired`, `@Component` 等）
6. **REST API**: 添加 RESTful API 接口
7. **异常处理**: 统一的异常处理机制
8. **日志**: 使用 SLF4J + Logback
9. **验证**: 使用 Jakarta Validation API
10. **Lombok**: 使用 Lombok 简化代码

## 如何运行

### 1. 本地运行
```bash
mvn spring-boot:run
```

### 2. 打包运行
```bash
mvn clean package
java -jar target/ai-review-1.0.0.jar
```

### 3. 指定配置文件
```bash
java -jar target/ai-review-1.0.0.jar --spring.config.location=classpath:/application.yml
```

## API 使用示例

### 执行代码评审
```bash
curl -X POST http://localhost:8080/api/review/code \
  -H "Content-Type: application/json" \
  -d '{
    "diffContent": "你的 Git Diff 内容",
    "model": "glm-4-flash"
  }'
```

### Git Diff 评审
```bash
curl http://localhost:8080/api/review/git/HEAD~1/HEAD
```

## 环境变量

可以通过环境变量覆盖配置：

```bash
export CHATGLM_API_KEY_SECRET="your_api_key_here"
java -jar target/ai-review-1.0.0.jar
```

## 技术栈

- **Spring Boot**: 3.2.5
- **Java**: 17
- **FastJSON2**: 2.0.49
- **Guava**: 33.3.0-jre
- **Java JWT**: 4.4.0
- **Lombok**: (Spring Boot 管理)

## 后续优化建议

1. 添加单元测试和集成测试
2. 添加 API 文档（Swagger/OpenAPI）
3. 添加数据库支持（存储评审历史）
4. 添加认证和授权
5. 添加限流和熔断机制
6. 添加监控和指标（Prometheus）
7. 优化错误处理和日志记录
8. 添加 Docker 支持
