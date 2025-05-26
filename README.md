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

## 🚀 项目结构

```
medical-diagnosis-system/
├── backend/                              # 后端项目
│   ├── src/main/java/com/medical/
│   │   ├── MedicalApplication.java       # 启动类
│   │   ├── config/                       # 配置类
│   │   ├── controller/                   # 控制器层
│   │   ├── service/                      # 业务逻辑层
│   │   │   ├── impl/                     # 实现类
│   │   ├── mapper/                       # 数据访问层
│   │   ├── entity/                       # 实体类
│   │   │   ├── User.java                 # 用户实体
│   │   ├── dto/                          # 数据传输对象
│   │   │   ├── UserDTO/                  # 请求DTO
│   │   ├── common/                       # 通用类
│   │   │   ├── Result.java               # 统一响应结果
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


## 🔄 开发流程

### 需求分析
收集并分析用户需求
编写需求规格说明书
确定项目范围和边界
创建用例和用户故事 
### 系统设计
架构设计（前后端架构、模块划分）
数据库设计（表结构、关系、索引）
API接口设计（RESTful规范）
UI/UX设计（界面原型）
安全设计（认证授权方案）
### 开发环境搭建
版本控制系统配置
开发工具选型与配置
开发规范制定
数据库环境搭建
### 编码实现
后端开发（控制器、服务、数据访问）
前端开发（界面、逻辑、交互）
单元测试编写
代码审查
### 测试验证
单元测试执行
集成测试
系统测试
性能测试
用户验收测试
### 部署上线
环境配置
打包构建
数据迁移
生产环境部署


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

