# 基于Spring Boot的AI医疗诊断辅助系统

## 📋 项目概述

本项目是一个智能化的医疗诊断辅助系统，旨在通过人工智能技术协助医生进行疾病诊断。系统能够分析患者症状、检查结果等医疗数据，利用AI算法提供可能的疾病诊断和治疗建议，从而提高医疗诊断的效率和准确性。

### 🎯 项目目标

- 辅助医生快速准确地进行疾病诊断
- 提供基于AI的智能诊断建议
- 规范化病例管理和数据存储
- 提升医疗服务质量和效率

## 🛠️ 技术栈

### 后端技术

- **框架**: Spring Boot 3.x
- **AI集成**: Spring AI
- **数据访问**: MyBatis-Plus
- **项目管理**: Maven
- **数据库**: MySQL 8.0+
- **安全框架**: Spring Security
- **缓存**: Redis
- **文档**: Swagger/OpenAPI 3

### 前端技术

- **框架**: Vue 3
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **UI组件**: Element Plus
- **HTTP客户端**: Axios
- **构建工具**: Vite
- **代码规范**: ESLint + Prettier

## 🏗️ 系统架构

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   前端 (Vue3)    │────│  后端 (Spring)   │────│   数据库 (MySQL) │
│                 │    │                 │    │                 │
│ - 用户界面       │    │ - REST API      │    │ - 患者信息       │
│ - 数据展示       │    │ - 业务逻辑       │    │ - 诊断记录       │
│ - 交互操作       │    │ - AI服务集成     │    │ - 用户数据       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                    ┌─────────────────┐
                    │   AI服务层       │
                    │                 │
                    │ - 症状分析       │
                    │ - 诊断推理       │
                    │ - 治疗建议       │
                    └─────────────────┘
```

## 🎨 核心功能模块

### 1. 用户管理模块

- **医生账户管理**: 注册、登录、权限控制
- **角色权限**: 主治医师、住院医师、管理员等不同权限级别
- **安全认证**: JWT令牌认证、密码加密存储

### 2. 患者信息管理

- **基本信息录入**: 姓名、年龄、性别、联系方式等
- **病史管理**: 既往病史、家族病史、过敏史等
- **检查结果上传**: 支持医学影像、化验报告等文件上传
- **信息检索**: 快速查找患者信息和历史记录

### 3. AI智能诊断核心

- **症状分析**: 自然语言处理分析症状描述
- **疾病匹配**: 基于医学知识库进行疾病匹配
- **概率计算**: 提供各种可能疾病的概率评估
- **差异诊断**: 提供鉴别诊断建议

### 4. 治疗建议系统

- **个性化方案**: 根据患者具体情况生成治疗方案
- **药物推荐**: 基于诊断结果推荐合适药物
- **注意事项**: 提供用药注意事项和禁忌症
- **随访建议**: 制定后续复查和随访计划

### 5. 病例管理系统

- **电子病历**: 结构化存储患者完整病例信息
- **诊断历史**: 追踪诊断过程和结果变化
- **统计分析**: 生成诊断统计报告和趋势分析
- **数据导出**: 支持病例数据的导入导出功能

## 📊 数据库设计

### 核心数据表

#### 医生表 (doctor)

```sql
CREATE TABLE doctor (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '登录用户名',
    password VARCHAR(255) NOT NULL COMMENT '登录密码(MD5加密)',
    real_name VARCHAR(100) COMMENT '医生真实姓名',
    email VARCHAR(100) COMMENT '电子邮箱',
    phone VARCHAR(20) COMMENT '联系电话',
    status TINYINT DEFAULT 1 COMMENT '状态(1:正常,0:禁用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '医生信息表';
```

#### 患者表 (patient)

```sql
CREATE TABLE patient (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    patient_id VARCHAR(50) UNIQUE NOT NULL COMMENT '患者编号',
    name VARCHAR(100) NOT NULL COMMENT '患者姓名',
    gender TINYINT COMMENT '性别(1:男,2:女)',
    birth_date DATE COMMENT '出生日期',
    phone VARCHAR(20) COMMENT '联系电话',
    address TEXT COMMENT '联系地址',
    allergies TEXT COMMENT '过敏史',
    medical_history TEXT COMMENT '既往病史',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '患者信息表';
```

#### 诊断记录表 (diagnosis_record)

```sql
CREATE TABLE diagnosis_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    doctor_id BIGINT NOT NULL COMMENT '医生ID',
    symptoms TEXT COMMENT '患者症状描述',
    ai_diagnosis JSON COMMENT 'AI诊断结果JSON',
    final_diagnosis TEXT COMMENT '最终诊断结果',
    treatment_plan TEXT COMMENT '治疗方案',
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态(draft:草稿,final:确诊)',
    diagnosis_date DATETIME COMMENT '诊断日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '诊断记录表';
```

#### 处方表 (prescription)

```sql
CREATE TABLE prescription (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    diagnosis_id BIGINT NOT NULL COMMENT '关联诊断ID',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    doctor_id BIGINT NOT NULL COMMENT '开方医生ID',
    medicines JSON COMMENT '药品信息JSON(包含药名、用量、用法等)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '开方时间'
) COMMENT '处方信息表';
```

#### 医疗文件表 (medical_file)

```sql
CREATE TABLE medical_file (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    diagnosis_id BIGINT COMMENT '关联诊断ID',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名称',
    file_path VARCHAR(500) NOT NULL COMMENT '文件存储路径',
    file_type VARCHAR(50) COMMENT '文件类型',
    upload_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间'
) COMMENT '医疗文件表';
```

#### 医学知识库表 (medical_knowledge)

```sql
CREATE TABLE medical_knowledge (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    disease_name VARCHAR(200) NOT NULL COMMENT '疾病名称',
    disease_code VARCHAR(50) COMMENT '疾病编码',
    symptoms JSON COMMENT '相关症状JSON',
    treatment_guidelines TEXT COMMENT '治疗指南',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '医学知识库';
```

## 🔧 API 接口设计

### 认证接口

```
POST /api/auth/login          # 用户登录
POST /api/auth/logout         # 用户登出
POST /api/auth/refresh        # 刷新Token
```

### 患者管理接口

```
GET    /api/patients          # 获取患者列表
POST   /api/patients          # 新增患者
GET    /api/patients/{id}     # 获取患者详情
PUT    /api/patients/{id}     # 更新患者信息
DELETE /api/patients/{id}     # 删除患者
```

### 诊断接口

```
POST   /api/diagnosis         # 创建诊断记录
GET    /api/diagnosis/{id}    # 获取诊断详情
POST   /api/diagnosis/ai      # AI诊断分析
GET    /api/diagnosis/history/{patientId}  # 获取患者诊断历史
```

### AI服务接口

```
POST   /api/ai/analyze        # 症状分析
POST   /api/ai/diagnose       # 智能诊断
POST   /api/ai/recommend      # 治疗建议
```

## 🚀 项目结构

```
medical-diagnosis-system/
├── backend/                              # 后端项目
│   ├── src/main/java/com/medical/
│   │   ├── MedicalApplication.java       # 启动类
│   │   ├── config/                       # 配置类
│   │   │   ├── SecurityConfig.java       # 安全配置
│   │   │   ├── SwaggerConfig.java        # API文档配置
│   │   │   └── AIConfig.java             # AI服务配置
│   │   ├── controller/                   # 控制器层
│   │   │   ├── AuthController.java       # 认证控制器
│   │   │   ├── PatientController.java    # 患者管理控制器
│   │   │   ├── DiagnosisController.java  # 诊断控制器
│   │   │   └── AIController.java         # AI服务控制器
│   │   ├── service/                      # 业务逻辑层
│   │   │   ├── impl/                     # 实现类
│   │   │   ├── UserService.java          # 用户服务
│   │   │   ├── PatientService.java       # 患者服务
│   │   │   ├── DiagnosisService.java     # 诊断服务
│   │   │   └── AIService.java            # AI服务
│   │   ├── mapper/                       # 数据访问层
│   │   │   ├── UserMapper.java           # 用户数据访问
│   │   │   ├── PatientMapper.java        # 患者数据访问
│   │   │   └── DiagnosisMapper.java      # 诊断数据访问
│   │   ├── entity/                       # 实体类
│   │   │   ├── User.java                 # 用户实体
│   │   │   ├── Patient.java              # 患者实体
│   │   │   └── DiagnosisRecord.java      # 诊断记录实体
│   │   ├── dto/                          # 数据传输对象
│   │   │   ├── request/                  # 请求DTO
│   │   │   └── response/                 # 响应DTO
│   │   ├── common/                       # 通用类
│   │   │   ├── Result.java               # 统一响应结果
│   │   │   ├── Constants.java            # 常量定义
│   │   │   └── exception/                # 异常处理
│   │   │
│   │   └── utils/                        # 工具类
│   │       ├── JwtUtil.java              # JWT工具
│   │       └── PasswordUtil.java         # 密码工具
│   ├── src/main/resources/
│   │   ├── mapper/                       # MyBatis映射文件
│   │   ├── application.yml               # 应用配置
│   │   └── application-dev.yml           # 开发环境配置
│   └── pom.xml                           # Maven配置
├── frontend/                             # 前端项目
│   ├── public/                           # 静态资源
│   ├── src/
│   │   ├── assets/                       # 资源文件
│   │   │   ├── images/                   # 图片资源
│   │   │   ├── styles/                   # 全局样式
│   │   │   │   └── main.scss             # 主样式文件
│   │   │   └── icons/                    # 图标资源
│   │   │
│   │   ├── components/                   # 通用组件
│   │   │   ├── layout/                   # 布局组件
│   │   │   │   ├── AppHeader.vue         # 应用头部
│   │   │   │   ├── AppSidebar.vue        # 侧边栏
│   │   │   │   └── AppFooter.vue         # 页脚
│   │   │   ├── common/                   # 公共组件
│   │   │   │   ├── BaseButton.vue        # 基础按钮
│   │   │   │   ├── BaseInput.vue         # 基础输入框
│   │   │   │   └── BaseCard.vue          # 基础卡片
│   │   │   └── medical/                  # 医疗相关组件
│   │   │       ├── PatientCard.vue       # 患者信息卡
│   │   │       ├── SymptomInput.vue      # 症状录入
│   │   │       └── DiagnosisResult.vue   # 诊断结果展示
│   │   │
│   │   ├── views/                        # 页面组件
│   │   │   ├── auth/                     # 认证相关页面
│   │   │   │   ├── Login.vue             # 登录页
│   │   │   │   └── Register.vue          # 注册页
│   │   │   ├── dashboard/                # 仪表盘
│   │   │   │   └── Dashboard.vue         # 主仪表盘
│   │   │   ├── patient/                  # 患者相关页面
│   │   │   │   ├── PatientList.vue       # 患者列表
│   │   │   │   ├── PatientDetail.vue     # 患者详情
│   │   │   │   └── PatientCreate.vue     # 创建患者
│   │   │   └── diagnosis/                # 诊断相关页面
│   │   │       ├── DiagnosisList.vue     # 诊断列表
│   │   │       ├── DiagnosisDetail.vue   # 诊断详情
│   │   │       └── NewDiagnosis.vue      # 新建诊断
│   │   │
│   │   ├── router/                       # 路由配置
│   │   │   ├── index.js                  # 路由入口
│   │   │   └── routes/                   # 路由模块
│   │   │       ├── auth.js               # 认证路由
│   │   │       ├── patient.js            # 患者路由
│   │   │       └── diagnosis.js          # 诊断路由
│   │   │
│   │   ├── store/                        # Pinia状态管理
│   │   │   ├── index.js                  # Pinia配置
│   │   │   └── modules/                  # 状态模块
│   │   │       ├── auth.js               # 认证状态
│   │   │       ├── user.js               # 用户状态
│   │   │       ├── patient.js            # 患者状态
│   │   │       └── diagnosis.js          # 诊断状态
│   │   │
│   │   ├── api/                          # API调用层
│   │   │   ├── request.js                # 请求封装
│   │   │   ├── index.js                  # API导出
│   │   │   └── modules/                  # API模块
│   │   │       ├── auth.js               # 认证API
│   │   │       ├── user.js               # 用户API
│   │   │       ├── patient.js            # 患者API
│   │   │       └── diagnosis.js          # 诊断API
│   │   │
│   │   ├── services/                     # 服务层
│   │   │   ├── index.js                  # 服务入口
│   │   │   ├── authService.js            # 认证服务
│   │   │   ├── userService.js            # 用户服务
│   │   │   ├── patientService.js         # 患者服务
│   │   │   └── diagnosisService.js       # 诊断服务
│   │   │
│   │   ├── utils/                        # 工具函数
│   │   │   ├── auth.js                   # 认证工具
│   │   │   ├── date.js                   # 日期处理
│   │   │   ├── validation.js             # 数据验证
│   │   │   └── errorHandler.js           # 错误处理
│   │   │
│   │   ├── hooks/                        # 自定义Hooks
│   │   │   ├── useAuth.js                # 认证相关钩子
│   │   │   └── usePagination.js          # 分页钩子
│   │   │
│   │   ├── locales/                      # 国际化资源
│   │   │   ├── zh-CN.js                  # 中文
│   │   │   └── en-US.js                  # 英文
│   │   │
│   │   ├── constants/                    # 常量定义
│   │   │   ├── api.js                    # API常量
│   │   │   └── enums.js                  # 枚举常量
│   │   │
│   │   ├── main.js                       # 入口文件
│   │   └── App.vue                       # 根组件
│   ├── package.json                      # 前端依赖
│   └── vite.config.js                    # Vite配置
├── docs/                                 # 项目文档
│   ├── API.md                            # API文档
│   ├── DATABASE.md                       # 数据库文档
│   └── DEPLOYMENT.md                     # 部署文档
└── README.md                             # 项目说明
```

## 📦 环境配置

### 开发环境要求

- **Java**: JDK 17+
- **Node.js**: 16.0+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Maven**: 3.6+


## 🔄 开发流程

### Phase 1: 项目初始化 (1-2周)

1. **环境搭建**

    - 创建Spring Boot项目骨架
    - 配置Vue3前端项目
    - 搭建MySQL数据库
    - 配置开发环境
2. **基础框架**

    - 实现用户认证系统
    - 搭建基础的CRUD操作
    - 前后端接口联调

### Phase 2: 核心功能开发 (3-4周)

1. **患者管理模块**

    - 患者信息的增删改查
    - 病史记录管理
    - 检查结果上传
2. **诊断记录模块**

    - 症状录入界面
    - 诊断记录存储
    - 历史记录查询

### Phase 3: AI功能集成 (2-3周)

1. **Spring AI集成**

    - 配置AI服务连接
    - 实现症状分析功能
    - 诊断建议生成
2. **智能诊断优化**

    - 完善prompt模板
    - 优化诊断准确性
    - 添加医学知识库

### Phase 4: 系统完善 (2-3周)

1. **功能完善**

    - 治疗建议系统
    - 统计分析功能
    - 数据导入导出
2. **系统优化**

    - 性能优化
    - 安全性加固
    - 用户体验优化

### Phase 5: 测试与部署 (1-2周)

1. **测试阶段**

    - 单元测试
    - 集成测试
    - 用户验收测试
2. **部署上线**

    - 生产环境部署
    - 监控系统配置
    - 文档整理

## 🚨 注意事项与风险控制

### 数据安全

- **患者隐私保护**: 严格遵守医疗数据保护法规
- **数据加密**: 敏感数据加密存储和传输
- **访问控制**: 细粒度的权限控制机制
- **审计日志**: 完整的操作日志记录

### 医疗责任

- **免责声明**: 明确AI诊断仅供参考，不替代医生专业判断
- **数据准确性**: 确保输入数据的准确性和完整性
- **算法透明度**: 提供诊断依据和置信度指标

### 技术风险

- **AI服务可用性**: 设计降级策略和备用方案
- **数据备份**: 定期数据备份和灾难恢复计划
- **系统监控**: 实时监控系统运行状态

## 📈 未来扩展方向

### 功能扩展

- **多科室支持**: 扩展到不同医学专科
- **影像识别**: 集成医学影像AI分析
- **语音输入**: 支持语音录入病历信息
- **移动端**: 开发移动端应用

### 技术升级

- **微服务架构**: 拆分为微服务架构提高可扩展性
- **容器化部署**: 使用Docker和Kubernetes部署
- **大数据分析**: 集成大数据分析平台
- **区块链**: 利用区块链技术保障数据安全

## 🤝 团队协作

### 开发规范

- **代码规范**: 遵循统一的代码风格和命名规范
- **Git工作流**: 采用GitFlow工作流程
- **代码审查**: 必须进行代码审查才能合并
- **文档维护**: 及时更新项目文档

### 项目管理

- **敏捷开发**: 采用Scrum敏捷开发方法
- **任务跟踪**: 使用项目管理工具跟踪进度
- **定期会议**: 每日站会和迭代评审
- **风险评估**: 定期评估项目风险

---

_本文档将随项目进展持续更新，最后更新时间：2025年5月_