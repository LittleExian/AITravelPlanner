# MongoDB 配置教程

本文档提供了在Windows系统上安装、配置MongoDB并将其与AITravelPlanner后端应用集成的详细步骤。

## 重要说明：MongoDB Compass vs MongoDB服务器

MongoDB Compass是MongoDB的图形用户界面(GUI)工具，用于可视化管理MongoDB数据库，但它**不是**MongoDB服务器本身。要使AITravelPlanner应用正常工作，您需要安装完整的MongoDB服务器。

如果您目前只安装了MongoDB Compass，请继续阅读以下安装MongoDB服务器的步骤。

## 1. 安装MongoDB服务器

### 1.1 下载MongoDB Community Server

1. 访问MongoDB官方下载页面：[MongoDB Download Center](https://www.mongodb.com/try/download/community)

2. 在下载页面，确保选择的是MongoDB服务器而不仅仅是Compass：
   - 在页面顶部，确保您看到的是"MongoDB Community Server"选项卡（而不是"MongoDB Compass"）
   - Version: 选择最新的稳定版本（推荐6.0+）
   - Platform: Windows
   - Package: msi（图形安装程序）

3. 注意：下载页面可能会默认选中"Include MongoDB Compass"选项，这是可选的，您可以保留选中以同时安装GUI工具

4. 点击Download按钮下载安装程序

### 1.2 安装MongoDB服务器

1. 双击下载的MSI安装程序以启动安装向导

2. 在初始欢迎页面，点击"Next"继续

3. 接受许可协议，然后点击"Next"

4. 在"Setup Type"页面，选择"Complete"安装类型，然后点击"Next"

5. 在"Service Configuration"步骤中（这是关键步骤，请确保正确配置）：
   - 必须勾选"Install MongoDB as a Service"选项
   - 选择"Run service as Network Service user"（默认推荐选项）
   - 保持默认的服务名称"MongoDB"
   - 数据目录默认为"C:\\Program Files\\MongoDB\\Server\\{版本}\\data"
   - 日志目录默认为"C:\\Program Files\\MongoDB\\Server\\{版本}\\log"
   - 点击"Next"

6. 在"Install MongoDB Compass"页面（如果出现），您可以选择安装或不安装，然后点击"Next"

7. 点击"Install"开始安装

8. 安装完成后，点击"Finish"关闭向导

### 1.3 验证MongoDB服务器安装

#### 步骤1：检查MongoDB服务状态

1. 打开命令提示符（管理员权限）：
   - 按下Win + X键，然后选择"命令提示符（管理员）"或"Windows PowerShell（管理员）"

2. 检查MongoDB服务是否已启动：
   ```cmd
   net start
   ```
   - 在列表中查找"MongoDB"服务

3. 如果服务未启动，使用以下命令启动它：
   ```cmd
   net start MongoDB
   ```
   - 如果启动成功，您将看到："MongoDB 服务已启动成功。"
   - 如果启动失败，请检查安装过程是否正确，特别是服务配置部分

#### 步骤2：连接到MongoDB服务器

1. 使用MongoDB Shell连接到服务器：
   ```cmd
   mongo
   ```
   - 或者使用完整路径（如果未将MongoDB添加到环境变量）：
   ```cmd
   "C:\Program Files\MongoDB\Server\{版本}\bin\mongo.exe"
   ```

2. 如果连接成功，您将看到MongoDB shell提示符，类似：
   ```
   MongoDB shell version v6.0.0
   connecting to: mongodb://127.0.0.1:27017/
   ...
   >
   ```

#### 步骤3：使用MongoDB Compass连接（如果已安装）

1. 启动MongoDB Compass

2. 在连接窗口中，使用默认连接字符串：
   ```
   mongodb://localhost:27017
   ```

3. 点击"Connect"按钮

4. 如果连接成功，您将看到Compass界面显示可用的数据库

## 2. 配置MongoDB

### 2.1 创建数据库

由于我们找不到MongoDB Shell工具，我们将使用MongoDB Compass（图形界面工具）来创建数据库。

#### 使用MongoDB Compass创建数据库

1. **启动MongoDB Compass**：
   - 从开始菜单或桌面快捷方式启动MongoDB Compass

2. **连接到MongoDB服务器**：
   - 在Compass的连接界面，使用默认连接字符串：`mongodb://localhost:27017`
   - 点击"Connect"按钮连接到MongoDB服务器

3. **创建数据库**：
   - 在左侧导航栏，点击绿色的"Create Database"按钮
   - 在弹出的对话框中：
     - Database Name: 输入`aitravelplanner`
     - Collection Name: 输入`users`（项目的第一个集合）
     - 点击"Create Database"按钮

4. **验证数据库创建成功**：
   - 新创建的数据库`aitravelplanner`应该出现在左侧导航栏中
   - 点击数据库名可以查看其包含的集合

#### 如果您仍然需要使用命令行创建数据库

如果您希望使用命令行，请按照以下步骤安装MongoDB Shell：

1. 访问官方MongoDB下载页面：https://www.mongodb.com/try/download/shell
2. 下载适合您系统的MongoDB Shell版本
3. 安装完成后，将安装目录添加到系统环境变量PATH中
4. 然后您可以使用`mongosh`命令连接到MongoDB并创建数据库：

```cmd
mongosh
use aitravelplanner
db.users.insertOne({"test": "data"})
```

### 2.2 插入测试数据（可选）

#### 使用MongoDB Compass插入测试数据

1. 在MongoDB Compass中，确保已连接到MongoDB服务器
2. 从左侧导航栏中选择`aitravelplanner`数据库
3. 选择`users`集合
4. 点击"Insert Document"按钮
5. 在弹出的编辑器中，输入以下测试数据：

```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "role": "user",
  "createdAt": { "$date": "2023-11-10T00:00:00Z" }
}
```

6. 点击"Insert"按钮保存文档
7. 验证数据插入成功：新插入的文档应该显示在文档列表中

#### 如果需要使用命令行插入测试数据

如果您已经安装了MongoDB Shell（mongosh），可以使用以下命令：

```javascript
// 插入一条简单的测试文档
use aitravelplanner
db.test.insertOne({name: "测试数据", date: new Date()})

// 验证数据是否插入成功
db.test.find()
```

### 2.3 创建用户（可选，用于生产环境）

#### 使用MongoDB Compass创建用户

1. 在MongoDB Compass中，点击顶部的"Security"选项卡
2. 选择"Database Access"选项
3. 点击"Add New Database User"按钮
4. 在用户创建表单中：
   - 用户名：输入`aitravelplannerUser`
   - 密码：输入`aitravelplannerPassword`（请记住这个密码，将在Spring Boot配置中使用）
   - 角色：
     - 数据库：选择`aitravelplanner`
     - 角色：选择`readWrite`
5. 点击"Add User"按钮保存

#### 如果需要使用命令行创建用户

为了安全起见，建议为数据库创建用户：

```javascript
// 1. 切换到admin数据库
use admin

// 2. 创建用户管理员（仅需执行一次）
db.createUser({
  user: "adminUser",
  pwd: "adminPassword",  // 请替换为安全的密码
  roles: [ { role: "userAdminAnyDatabase", db: "admin" } ]
})

// 3. 切换回应用数据库
use aitravelplanner

// 4. 创建应用数据库用户
db.createUser({
  user: "aitravelplannerUser",
  pwd: "aitravelplannerPassword",  // 请替换为安全的密码
  roles: [ { role: "readWrite", db: "aitravelplanner" } ]
})

// 5. 验证用户创建成功
show users
```

**注意**：如果启用了用户认证，您需要在应用配置中更新连接字符串以包含用户名和密码：

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://aitravelplannerUser:aitravelplannerPassword@localhost:27017/aitravelplanner
```

## 3. 配置Spring Boot应用连接MongoDB

### 3.1 修改application.yml配置

编辑`src/main/resources/application.yml`文件，配置MongoDB连接信息：

```yaml
spring:
  data:
    mongodb:
      # 使用默认端口和数据库名称
      uri: mongodb://localhost:27017/aitravelplanner
      # 如果使用了认证，请使用以下URI格式
      # uri: mongodb://aitravelplannerUser:aitravelplannerPassword@localhost:27017/aitravelplanner
      database: aitravelplanner
      auto-index-creation: true
```

### 3.2 临时配置：在MongoDB不可用时运行应用

如果您暂时没有MongoDB服务器但想让应用能够启动和运行，可以修改配置以允许应用在数据库连接失败时仍能启动：

```yaml
spring:
  data:
    mongodb:
      # 连接字符串保持不变
      uri: mongodb://localhost:27017/aitravelplanner
      database: aitravelplanner
      # 设置较短的连接超时时间
      socket-timeout: 5000
      connect-timeout: 5000
      # 禁用重试写入
      retry-writes: false
  # 启用应用在数据库连接失败时仍能启动
  main:
    allow-bean-definition-overriding: true
```

**注意**：这只是临时解决方案，用于让应用能够启动。实际使用时，您需要安装并配置MongoDB服务器以获得完整功能。

### 3.3 添加MongoDB依赖（如果不存在）

确保`pom.xml`文件中包含MongoDB依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

如果您想暂时排除MongoDB依赖，可以在pom.xml中添加排除配置（但不推荐，因为应用可能需要数据库功能）：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
    <scope>provided</scope> <!-- 仅在提供时使用 -->
</dependency>
```

## 4. 测试MongoDB连接

### 4.1 确保MongoDB服务器正在运行

在启动应用之前，请确保MongoDB服务器已启动：

```cmd
net start MongoDB
```

### 4.2 启动后端应用

```cmd
cd c:\Users\29799\project\AITravelPlanner\backend
mvn spring-boot:run
```

### 4.3 验证连接成功

在应用启动日志中，查找以下信息确认MongoDB连接成功：

```
INFO 1234 --- [           main] org.mongodb.driver.cluster               : Cluster created with settings {hosts=[localhost:27017], mode=SINGLE, requiredClusterType=UNKNOWN, serverSelectionTimeout='30000 ms'}
INFO 1234 --- [           main] org.mongodb.driver.cluster               : Opened connection [connectionId{localValue:1, serverValue:1}] to localhost:27017
INFO 1234 --- [           main] org.mongodb.driver.cluster               : Monitor thread successfully connected to server with description ServerDescription{address=localhost:27017, type=STANDALONE, state=CONNECTED, ok=true, version=ServerVersion{versionList=[6, 0, 0]}, minWireVersion=0, maxWireVersion=21, maxDocumentSize=16777216, logicalSessionTimeoutMinutes=30, roundTripTimeNanos=12345678}
```

### 4.4 常见连接错误及处理

如果看到以下错误，请按照相应的解决方案处理：

1. **连接被拒绝错误**：
   ```
   com.mongodb.MongoSocketOpenException: Exception opening socket
   Caused by: java.net.ConnectException: Connection refused: no further information
   ```
   - 解决方案：确保MongoDB服务器已启动，检查端口27017是否被占用

2. **认证失败错误**：
   ```
   com.mongodb.MongoSecurityException: Exception authenticating MongoCredential
   ```
   - 解决方案：检查连接字符串中的用户名和密码是否正确，确保用户有适当的权限

3. **数据库不存在错误**：
   ```
   com.mongodb.MongoCommandException: Command failed with error 13 (Unauthorized)
   ```
   - 解决方案：确保数据库已创建，用户有访问该数据库的权限

## 5. MongoDB常用操作

### 5.1 查看数据库列表

```javascript
show dbs
```

### 5.2 查看集合（表）列表

```javascript
use aitravelplanner
show collections
```

### 5.3 插入测试数据

```javascript
use aitravelplanner
db.users.insertOne({
  username: "testuser",
  email: "test@example.com",
  password: "hashedpassword",
  createdAt: new Date()
})
```

### 5.4 查询数据

```javascript
use aitravelplanner
db.users.find()
```

## 6. 故障排除

### 6.1 MongoDB服务无法启动

- 检查数据目录权限
- 查看MongoDB日志文件（通常位于`C:\Program Files\MongoDB\Server\{版本}\log\mongod.log`）

### 6.2 连接被拒绝错误

- 确认MongoDB服务正在运行：`net start MongoDB`
- 检查连接字符串中的主机名和端口号是否正确
- 验证防火墙设置是否允许连接到27017端口

### 6.3 认证失败

- 确认用户名和密码正确
- 验证用户是否有正确的数据库访问权限

## 7. 停止MongoDB服务

当需要停止MongoDB服务时，可使用以下命令：

```cmd
net stop MongoDB
```

---

完成以上配置后，AITravelPlanner后端应用应该能够成功连接到MongoDB数据库并正常工作。