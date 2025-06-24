# TCP文字反转系统 - 运行说明文档

## 运行环境要求
- Java 8 或更高版本
- Windows/Linux/MacOS 操作系统
- 网络环境支持本地回环连接

## 文件结构
```
src/
├── Server.java          # 服务器端程序
├── Client.java          # 客户端程序
├── input.txt           # 输入文件（可自定义）
└── output.txt          # 输出文件（程序生成）
```

## 编译程序
```bash
cd src
javac Server.java
javac Client.java
```

## 运行步骤

### 1. 启动服务器
```bash
java Server <端口号>
```
示例：
```bash
java Server 8888
```

### 2. 启动客户端
```bash
java Client <服务器IP> <端口号> <最小块大小> <最大块大小> <输入文件> <输出文件>
```
示例：
```bash
java Client 127.0.0.1 8888 10 50 input.txt output.txt
```

## 参数说明

### 服务器参数
- **端口号**: 服务器监听的端口号（建议1024-65535）

### 客户端参数
- **服务器IP**: 服务器的IP地址
- **端口号**: 服务器监听的端口号
- **最小块大小**: 文件分块的最小字节数（Lmin）
- **最大块大小**: 文件分块的最大字节数（Lmax）
- **输入文件**: 要处理的原始文件路径
- **输出文件**: 处理结果的输出文件路径

## 协议说明
程序使用自定义TCP协议，包含4种报文类型：
1. TYPE_INIT (1): 初始化报文
2. TYPE_AGREE (2): 同意报文
3. TYPE_REQUEST (3): 请求报文
4. TYPE_ANSWER (4): 应答报文

## 工作原理
1. 客户端将输入文件随机分块
2. 与服务器建立连接并发送初始化信息
3. 逐块发送数据给服务器进行反转处理
4. 接收反转结果并重新组合
5. 将最终结果写入输出文件

## 注意事项
- 确保input.txt文件存在且有内容
- 服务器需要先启动，再启动客户端
- 支持多客户端并发连接
- 建议使用ASCII字符以避免编码问题
- 使用Wireshark抓包时选择本地回环接口

## 常见错误处理
- "Connection refused": 检查服务器是否启动
- "File not found": 检查输入文件路径是否正确
- "Unexpected answer packet": 可能是网络传输问题，重新运行程序

## 示例运行
```bash
# 终端1：启动服务器
java Server 8888

# 终端2：运行客户端
java Client 127.0.0.1 8888 10 50 input.txt output.txt
```

运行成功后，output.txt将包含反转后的文本内容。 