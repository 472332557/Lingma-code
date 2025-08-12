# 登录系统 (Login System)

一个基于Spring Boot和Vue的完整登录系统，包含用户注册、登录和JWT Token认证功能。

## 技术栈

### 后端 (Backend)
- Spring Boot 2.7.9
- JDK 1.8
- MyBatis-Plus
- MySQL 8.0
- JWT (JSON Web Token)
- Spring Security Crypto (BCryptPasswordEncoder)
- Maven

### 前端 (Frontend)
- Vue 3
- Axios
- Webpack
- Node.js

## 功能特性

1. 用户注册
2. 用户登录
3. JWT Token认证
4. 密码加密存储 (BCrypt)
5. 表单验证
6. 路由守卫
7. 响应式设计
8. 微信支付集成
9. 支付宝支付集成

## 项目结构

```
.
├── backend                 # 后端项目
│   ├── src/main/java       # Java源代码
│   │   ├── com.example.loginsystem
│   │   │   ├── common      # 通用类
│   │   │   ├── config      # 配置类
│   │   │   ├── controller  # 控制器
│   │   │   ├── dto         # 数据传输对象
│   │   │   ├── entity      # 实体类
│   │   │   ├── exception   # 异常处理
│   │   │   ├── interceptor # 拦截器
│   │   │   ├── mapper      # MyBatis Mapper接口
│   │   │   ├── service     # 服务层
│   │   │   ├── util        # 工具类
│   │   │   └── LoginSystemApplication.java # 启动类
│   ├── src/main/resources  # 资源文件
│   │   ├── application.yml # 配置文件
│   │   └── mapper          # MyBatis XML映射文件(如有)
│   └── pom.xml             # Maven配置文件
├── frontend                # 前端项目
│   ├── public              # 静态资源
│   ├── src                 # Vue源代码
│   │   ├── api             # API接口
│   │   ├── assets          # 静态资源
│   │   ├── components      # 组件
│   │   ├── router          # 路由配置
│   │   ├── utils           # 工具类
│   │   ├── views           # 页面视图
│   │   ├── App.vue         # 根组件
│   │   └── main.js         # 入口文件
│   ├── package.json        # npm配置文件
│   └── webpack.config.js   # Webpack配置文件
└── README.md               # 项目说明文件
```

## 环境要求

- JDK 1.8 或更高版本
- Maven 3.6 或更高版本
- MySQL 8.0
- Node.js 14 或更高版本
- npm 6 或更高版本

## 数据库配置

### 创建数据库

```sql
CREATE DATABASE dessert_shop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 创建表结构

```sql
-- 用户表
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 轮播图表
CREATE TABLE `banner` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `image_url` varchar(255) NOT NULL COMMENT '图片URL',
  `link_url` varchar(255) DEFAULT NULL COMMENT '跳转链接',
  `sort` int DEFAULT '0' COMMENT '排序字段',
  `status` tinyint DEFAULT '1' COMMENT '状态（0:禁用 1:启用）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- 分类表
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `description` varchar(255) DEFAULT NULL COMMENT '分类描述',
  `type` tinyint NOT NULL DEFAULT '1' COMMENT '分类类型（1:甜品系列 2:其他分类）',
  `sort` int DEFAULT '0' COMMENT '排序字段',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- 甜品表
CREATE TABLE `dessert` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '甜品ID',
  `name` varchar(100) NOT NULL COMMENT '甜品名称',
  `description` text COMMENT '甜品描述',
  `image_url` varchar(255) NOT NULL COMMENT '甜品图片URL',
  `price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '甜品价格',
  `category_id` bigint NOT NULL COMMENT '所属分类ID',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '甜品状态（0:停售 1:在售）',
  `sort` int DEFAULT '0' COMMENT '排序字段',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='甜品表';

-- 甜品规格表
CREATE TABLE `dessert_spec` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '规格ID',
  `dessert_id` bigint NOT NULL COMMENT '甜品ID',
  `name` varchar(50) NOT NULL COMMENT '规格名称（如4寸、6寸、草莓味等）',
  `price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '规格价格',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_dessert_id` (`dessert_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='甜品规格表';

-- 订单表
CREATE TABLE `order_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `order_number` varchar(50) NOT NULL COMMENT '订单编号',
  `total_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '订单总金额',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '订单状态（0:待支付 1:已支付 2:已取消 3:已完成）',
  `address` varchar(255) NOT NULL COMMENT '收货地址',
  `phone` varchar(20) NOT NULL COMMENT '联系电话',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_number` (`order_number`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单项表
CREATE TABLE `order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `dessert_id` bigint NOT NULL COMMENT '甜品ID',
  `dessert_name` varchar(100) NOT NULL COMMENT '甜品名称',
  `image_url` varchar(255) NOT NULL COMMENT '甜品图片URL',
  `spec_id` bigint DEFAULT NULL COMMENT '甜品规格ID',
  `spec_name` varchar(50) DEFAULT NULL COMMENT '规格名称',
  `price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '单价',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '数量',
  `subtotal` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '小计金额',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

-- 地址表
CREATE TABLE `address` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `name` varchar(50) NOT NULL COMMENT '收货人姓名',
  `phone` varchar(20) NOT NULL COMMENT '联系电话',
  `province` varchar(50) NOT NULL COMMENT '省份',
  `city` varchar(50) NOT NULL COMMENT '城市',
  `district` varchar(50) NOT NULL COMMENT '区县',
  `detail` varchar(255) NOT NULL COMMENT '详细地址',
  `is_default` tinyint NOT NULL DEFAULT '0' COMMENT '是否默认地址（0:否 1:是）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地址表';
```

## 测试数据

为了方便测试，您可以手动插入以下测试数据到user表中：

```sql
-- 密码为"password123"的BCrypt加密结果
INSERT INTO `user` (`id`, `username`, `password`, `create_time`, `update_time`) 
VALUES (1, 'testuser', '$2a$10$rOz9ONJq9Sf.91Njm92a7O1bU6efzvc2mQrRDkR4Fv8Y7Ia4d4WzG', NOW(), NOW());

-- 密码为"admin123"的BCrypt加密结果
INSERT INTO `user` (`id`, `username`, `password`, `create_time`, `update_time`) 
VALUES (2, 'admin', '$2a$10$w95Y6.YrS90p57lOC8W8dO0z1G5XLHwk4./6gbqHwVFpD4Chb5Z9a', NOW(), NOW());
```

您也可以通过注册功能创建新用户，或者使用以下测试账号登录：

| 用户名 | 密码 | 说明 |
|--------|------|------|
| testuser | password123 | 普通测试用户 |
| admin | admin123 | 管理员测试用户 |

## 微信支付集成

本系统支持微信支付功能，集成步骤如下：

1. 申请微信支付商户号
2. 获取商户API密钥
3. 配置微信支付参数到[application.yml](file:///D:/Program%20Files%20(x86)/own_project/Lingma-code/dessert_project/backend/src/main/resources/application.yml)
4. 实现统一下单接口
5. 处理支付回调通知

### 微信支付配置示例

```yaml
# 微信支付配置
wx:
  pay:
    # 微信支付商户号
    mchId: your_mch_id
    # 微信支付商户密钥
    mchKey: your_mch_key
    # 微信支付appId
    appId: your_app_id
    # 微信支付API地址
    apiUrl: https://api.mch.weixin.qq.com
    # 支付通知地址
    notifyUrl: http://your-domain.com/api/payment/wechat/notify
```

## 支付宝支付集成

本系统也支持支付宝支付功能，集成步骤如下：

1. 申请支付宝开放平台应用
2. 生成应用私钥和获取支付宝公钥
3. 配置支付宝支付参数到[application.yml](file:///D:/Program%20Files%20(x86)/own_project/Lingma-code/dessert_project/backend/src/main/resources/application.yml)
4. 实现支付接口
5. 处理支付回调通知

### 支付宝支付配置示例

```yaml
# 支付宝支付配置
alipay:
  # 应用ID
  app-id: your_app_id
  # 商户私钥
  merchant-private-key: your_merchant_private_key
  # 支付宝公钥
  alipay-public-key: your_alipay_public_key
  # 签名类型
  sign-type: RSA2
  # 字符编码格式
  charset: UTF-8
  # 支付宝网关地址
  gateway-url: https://openapi.alipay.com/gateway.do
  # 支付成功后的回调地址
  return-url: http://your-domain.com/api/payment/alipay/return
  # 支付成功后的异步通知地址
  notify-url: http://your-domain.com/api/payment/alipay/notify
```

## 后端部署

1. 修改 [application.yml](file:///D:/Program%20Files%20(x86)/own_project/Lingma-code/dessert_project/backend/src/main/resources/application.yml) 文件中的数据库连接配置：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/dessert_shop?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
       username: root
       password: lzc2025666
   ```

2. 进入后端项目目录：
   ```bash
   cd backend
   ```

3. 编译并运行项目：
   ```bash
   mvn spring-boot:run
   ```

或者打包后运行：
   ```bash
   mvn clean package
   java -jar target/login-system-0.0.1-SNAPSHOT.jar
   ```

## 前端部署

1. 进入前端项目目录：
   ```bash
   cd frontend
   ```

2. 安装依赖：
   ```bash
   npm install
   ```

3. 开发模式运行：
   ```bash
   npm run dev
   ```

4. 生产环境构建：
   ```bash
   npm run build
   ```

## API接口说明

### 用户登录
- **URL**: `/api/auth/login`
- **方法**: POST
- **参数**:
  ```json
  {
    "username": "用户名",
    "password": "密码"
  }
  ```
- **返回**:
  ```json
  {
    "code": 200,
    "message": "操作成功",
    "data": {
      "token": "JWT_TOKEN"
    }
  }
  ```

### 用户注册
- **URL**: `/api/auth/register`
- **方法**: POST
- **参数**:
  ```json
  {
    "username": "用户名",
    "password": "密码"
  }
  ```
- **返回**:
  ```json
  {
    "code": 200,
    "message": "操作成功",
    "data": {
      "username": "用户名"
    }
  }
  ```

### 测试认证接口
- **URL**: `/api/test/auth`
- **方法**: GET
- **Headers**: 
  ```
  Authorization: JWT_TOKEN
  ```
- **返回**:
  ```json
  {
    "code": 200,
    "message": "操作成功",
    "data": {
      "message": "认证成功，可以访问需要认证的接口"
    }
  }
  ```

## 安全机制

1. **密码加密**: 使用BCryptPasswordEncoder对用户密码进行加密存储
2. **JWT Token**: 用户登录成功后生成JWT Token用于身份验证
3. **认证拦截**: 通过拦截器对需要认证的接口进行保护
4. **参数验证**: 使用Hibernate Validator对请求参数进行验证

## 访问地址

- 后端服务: http://localhost:8080
- 前端页面: http://localhost:3000

## 项目特点

1. **前后端分离**: 后端提供RESTful API，前端通过Ajax调用
2. **安全性**: 密码加密存储，JWT Token认证
3. **可扩展性**: 采用模块化设计，易于扩展新功能
4. **易维护性**: 代码结构清晰，注释完整
5. **响应式设计**: 前端页面适配不同屏幕尺寸

## 开发说明

### 后端代码说明

1. **实体类**: [User.java](file:///D:/Program%20Files%20(x86)/own_project/Lingma-code/dessert_project/backend/src/main/java/com/example/loginsystem/entity/User.java) - 用户实体，使用MyBatis-Plus注解映射数据库表
2. **Mapper接口**: [UserMapper.java](file:///D:/Program%20Files%20(x86)/own_project/Lingma-code/dessert_project/backend/src/main/java/com/example/loginsystem/mapper/UserMapper.java) - 继承BaseMapper，提供基本的CRUD操作
3. **服务层**: [UserService.java](file:///D:/Program%20Files%20(x86)/own_project/Lingma-code/dessert_project/backend/src/main/java/com/example/loginsystem/service/UserService.java) 和 [UserServiceImpl.java](file:///D:/Program%20Files%20(x86)/own_project/Lingma-code/dessert_project/backend/src/main/java/com/example/loginsystem/service/impl/UserServiceImpl.java) - 实现用户相关业务逻辑
4. **控制器**: [AuthController.java](file:///D:/Program%20Files%20(x86)/own_project/Lingma-code/dessert_project/backend/src/main/java/com/example/loginsystem/controller/AuthController.java) - 处理登录和注册请求
5. **JWT工具**: [JwtUtil.java](file:///D:/Program%20Files%20(x86)/own_project/Lingma-code/dessert_project/backend/src/main/java/com/example/loginsystem/util/JwtUtil.java) - 提供JWT Token的生成和验证功能
6. **拦截器**: [AuthInterceptor.java](file:///D:/Program%20Files%20(x86)/own_project/Lingma-code/dessert_project/backend/src/main/java/com/example/loginsystem/interceptor/AuthInterceptor.java) - 对需要认证的接口进行拦截验证
7. **异常处理**: [GlobalExceptionHandler.java](file:///D:/Program%20Files%20(x86)/own_project/Lingma-code/dessert_project/backend/src/main/java/com/example/loginsystem/exception/GlobalExceptionHandler.java) - 统一处理参数验证等异常

### 前端代码说明

1. **路由配置**: [router/index.js](file:///D:/Program%20Files%20(x86)/own_project/Lingma-code/dessert_project/frontend/src/router/index.js) - 配置页面路由和路由守卫
2. **API接口**: [api/auth.js](file:///D:/Program%20Files%20(x86)/own_project/Lingma-code/dessert_project/frontend/src/api/auth.js) - 封装后端API接口调用
3. **请求工具**: [utils/request.js](file:///D:/Program%20Files%20(x86)/own_project/Lingma-code/dessert_project/frontend/src/utils/request.js) - 封装axios，处理请求和响应拦截
4. **页面组件**: 
   - [Login.vue](file:///D:/Program%20Files%20(x86)/own_project/Lingma-code/dessert_project/frontend/src/views/Login.vue) - 登录页面
   - [Register.vue](file:///D:/Program%20Files%20(x86)/own_project/Lingma-code/dessert_project/frontend/src/views/Register.vue) - 注册页面
   - [Home.vue](file:///D:/Program%20Files%20(x86)/own_project/Lingma-code/dessert_project/frontend/src/views/Home.vue) - 主页（受保护页面）

## 注意事项

1. 请确保数据库连接信息正确
2. 前端默认通过代理访问后端API，代理配置在[webpack.config.js](file:///D:/Program%20Files%20(x86)/own_project/Lingma-code/dessert_project/frontend/webpack.config.js)中
3. JWT密钥和过期时间可以在[application.yml](file:///D:/Program%20Files%20(x86)/own_project/Lingma-code/dessert_project/backend/src/main/resources/application.yml)中配置
4. 生产环境部署时请注意修改默认密钥等敏感信息
5. 微信支付需要在正式环境中配置真实的商户信息
6. 支付宝支付需要在正式环境中配置真实的商户信息