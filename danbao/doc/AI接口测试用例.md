# AI问答接口测试用例

## 基础信息

- **Base URL**: `http://localhost:8080`
- **接口路径**: `/ai/chat`
- **请求方法**: POST
- **Content-Type**: application/json

---

## 1. 正常提问测试

### 请求信息
```http
POST http://localhost:8080/ai/chat
Content-Type: application/json
Authorization: Bearer your-token-here

{
    "message": "你好，请介绍一下你自己",
    "sessionId": "test-session-001",
    "temperature": 0.7,
    "maxTokens": 2048
}
```

### Postman配置
| 配置项 | 值 |
|--------|-----|
| Method | POST |
| URL | `http://localhost:8080/ai/chat` |
| Headers | Content-Type: application/json |
| Body (raw JSON) | 见上方请求体 |

### 预期响应
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": {
        "status": "success",
        "content": "你好！我是一个AI助手...",
        "model": "your-model-name",
        "totalTokens": 150,
        "responseTime": 1200
    }
}
```

---

## 2. 空消息测试

### 请求信息
```http
POST http://localhost:8080/ai/chat
Content-Type: application/json
Authorization: Bearer your-token-here

{
    "message": "",
    "sessionId": "test-session-002"
}
```

### Postman配置
| 配置项 | 值 |
|--------|-----|
| Method | POST |
| URL | `http://localhost:8080/ai/chat` |
| Headers | Content-Type: application/json |
| Body (raw JSON) | `{"message": "", "sessionId": "test-session-002"}` |

### 预期响应
```json
{
    "code": 500,
    "msg": "消息内容不能为空"
}
```
或参数校验错误：
```json
{
    "code": 500,
    "msg": "消息内容不能为空"
}
```

---

## 3. 超长消息测试

### 请求信息
```http
POST http://localhost:8080/ai/chat
Content-Type: application/json
Authorization: Bearer your-token-here

{
    "message": "这是一个超长消息测试...（超过4000字符的文本）",
    "sessionId": "test-session-003"
}
```

### 生成测试数据
```java
// 生成4000+字符的测试文本
StringBuilder longMessage = new StringBuilder();
for (int i = 0; i < 500; i++) {
    longMessage.append("这是一段用于测试超长消息的文本内容。");
}
// 结果：约500 * 20 = 10000字符
```

### Postman配置
| 配置项 | 值 |
|--------|-----|
| Method | POST |
| URL | `http://localhost:8080/ai/chat` |
| Headers | Content-Type: application/json |
| Body (raw JSON) | 超长文本（>4000字符） |

### 预期响应
```json
{
    "code": 500,
    "msg": "消息内容长度不能超过4000字符"
}
```

---

## 4. 特殊字符测试

### 请求信息
```http
POST http://localhost:8080/ai/chat
Content-Type: application/json
Authorization: Bearer your-token-here

{
    "message": "测试特殊字符: <script>alert('xss')</script> \"引号\" '单引号' \n换行符 \t制表符 中文测试 🎉emoji",
    "sessionId": "test-session-004"
}
```

### Postman配置
| 配置项 | 值 |
|--------|-----|
| Method | POST |
| URL | `http://localhost:8080/ai/chat` |
| Headers | Content-Type: application/json |
| Body (raw JSON) | 包含特殊字符的消息 |

### 预期响应
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": {
        "status": "success",
        "content": "AI的正常回复内容...",
        "model": "your-model-name",
        "responseTime": 800
    }
}
```

---

## 5. 并发压力测试

### 使用Postman Collection Runner

1. 创建一个Collection，添加上述4个测试用例
2. 设置迭代次数：10-100次
3. 设置延迟：100-500ms
4. 运行Collection Runner观察响应时间和成功率

### 预期结果
- 成功率：> 95%
- 平均响应时间：< 3000ms
- 无内存溢出或连接池耗尽错误

---

## 6. 网络异常测试

### 测试场景
1. **断开网络连接**
   - 断开本地网络
   - 发送请求
   - 预期：`网络连接失败，请检查网络设置`

2. **修改错误的服务器地址**
   - 修改application.yml中的base-url为无效地址
   - 重启服务
   - 发送请求
   - 预期：`无法连接到AI服务，请检查服务地址配置`

3. **配置错误的API Key**
   - 修改application.yml中的api-key为无效值
   - 重启服务
   - 发送请求
   - 预期：`AI服务返回错误，状态码: 401`

---

## Postman Collection JSON

```json
{
    "info": {
        "_postman_id": "ai-chat-test-collection",
        "name": "AI聊天接口测试",
        "description": "AI问答接口完整测试用例集合",
        "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
    },
    "item": [
        {
            "name": "1. 正常提问测试",
            "request": {
                "method": "POST",
                "header": [
                    {
                        "key": "Content-Type",
                        "value": "application/json"
                    },
                    {
                        "key": "Authorization",
                        "value": "Bearer {{token}}"
                    }
                ],
                "body": {
                    "mode": "raw",
                    "raw": "{\n    \"message\": \"你好，请介绍一下你自己\",\n    \"sessionId\": \"test-session-001\",\n    \"temperature\": 0.7,\n    \"maxTokens\": 2048\n}"
                },
                "url": {
                    "raw": "http://localhost:8080/ai/chat",
                    "protocol": "http",
                    "host": ["localhost"],
                    "port": "8080",
                    "path": ["ai", "chat"]
                }
            }
        },
        {
            "name": "2. 空消息测试",
            "request": {
                "method": "POST",
                "header": [
                    {
                        "key": "Content-Type",
                        "value": "application/json"
                    }
                ],
                "body": {
                    "mode": "raw",
                    "raw": "{\n    \"message\": \"\",\n    \"sessionId\": \"test-session-002\"\n}"
                },
                "url": {
                    "raw": "http://localhost:8080/ai/chat",
                    "protocol": "http",
                    "host": ["localhost"],
                    "port": "8080",
                    "path": ["ai", "chat"]
                }
            }
        },
        {
            "name": "3. 超长消息测试",
            "request": {
                "method": "POST",
                "header": [
                    {
                        "key": "Content-Type",
                        "value": "application/json"
                    }
                ],
                "body": {
                    "mode": "raw",
                    "raw": "{\n    \"message\": \"这是一段超长消息测试内容...（重复500次达到4000+字符）\",\n    \"sessionId\": \"test-session-003\"\n}"
                },
                "url": {
                    "raw": "http://localhost:8080/ai/chat",
                    "protocol": "http",
                    "host": ["localhost"],
                    "port": "8080",
                    "path": ["ai", "chat"]
                }
            }
        },
        {
            "name": "4. 特殊字符测试",
            "request": {
                "method": "POST",
                "header": [
                    {
                        "key": "Content-Type",
                        "value": "application/json"
                    }
                ],
                "body": {
                    "mode": "raw",
                    "raw": "{\n    \"message\": \"测试特殊字符: <script>alert('xss')</script> \\\"引号\\\" '单引号' \\n换行符 \\t制表符 中文测试 🎉emoji\",\n    \"sessionId\": \"test-session-004\"\n}"
                },
                "url": {
                    "raw": "http://localhost:8080/ai/chat",
                    "protocol": "http",
                    "host": ["localhost"],
                    "port": "8080",
                    "path": ["ai", "chat"]
                }
            }
        },
        {
            "name": "5. 健康检查",
            "request": {
                "method": "GET",
                "header": [],
                "url": {
                    "raw": "http://localhost:8080/ai/chat/health",
                    "protocol": "http",
                    "host": ["localhost"],
                    "port": "8080",
                    "path": ["ai", "chat", "health"]
                }
            }
        }
    ]
}
```

---

## 测试执行步骤

1. **导入Collection**
   - 打开Postman
   - 点击 Import → Paste Raw Text
   - 粘贴上面的JSON
   - 点击 Import

2. **设置环境变量**
   - 创建Environment
   - 添加变量 `token`（从登录接口获取）
   - 添加变量 `baseUrl` = `http://localhost:8080`

3. **执行测试**
   - 逐个执行测试用例
   - 或使用Collection Runner批量执行

4. **查看结果**
   - 检查响应状态码
   - 验证响应数据结构
   - 确认错误提示信息友好
