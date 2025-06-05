# AI医疗诊断辅助系统需求规格说明书

**版本：** V2.1 **更新日期：** 2025-06-15 **项目代码：** MEDICAL-AI-002 **技术栈：** Spring Boot + Vue3 + MySQL

## 修订历史

|版本| 日期         |修订人|修订说明|
|---|------------|---|---|
|V1.0| 2025-05-28 |项目团队|初始版本|
|V1.5| 2025-06-4  |项目团队|优化用户角色设计|
|V2.0| 2025-06-5  |项目团队|完善功能模块，添加数据统计和导入导出功能|
|V2.1| 2025-06-5  |项目团队|调整AI服务架构，AI诊断改为Java调用大模型，血常规分析保持Python服务|

## 1. 引言

### 1.1 目的

本文档旨在详细定义AI医疗诊断辅助系统的功能需求、非功能需求、接口规范及数据模型。该系统将为医疗机构提供智能化的辅助诊断工具，提升诊疗效率和准确性。本文档将作为项目开发团队进行系统设计、开发、测试和验收的依据。

### 1.2 文档约定

本文档中，需求条目采用统一的编号格式，例如 FR-MODULE-X.Y 表示某个模块下的第 Y 个功能点。除非特别说明，所有日期和时间格式均遵循ISO 8601标准。

### 1.3 预期读者和阅读建议

本文档的预期读者包括项目经理、系统分析师与设计师、开发工程师、测试工程师、医生用户代表和系统管理员代表。建议读者首先阅读"引言"和"总体描述"部分了解系统整体情况，然后根据各自关注点选择阅读相应章节。

### 1.4 项目范围

AI医疗诊断辅助系统是一个基于Spring Boot后端和Vue3前端技术的Web应用系统，主要服务于医疗机构的医生和管理人员。

**系统核心功能包括：**

- 用户管理：支持医生和管理员两种角色，提供完整的用户生命周期管理
- 患者信息管理：维护患者档案、病史记录等基础信息
- AI辅助诊断：通过Java后端调用大模型API进行智能诊断推理，提供诊断建议
- 血常规分析：调用专门的Python服务进行血液检验数据分析和AI辅助解读
- 数据统计分析：提供系统使用情况和医疗数据的统计分析
- 系统审计：记录关键操作，保证系统安全性和可追溯性
- 数据导入导出：支持批量数据处理，提升工作效率
- 医学知识库：提供疾病和药品等医学知识的查询功能

**系统边界：**

- 不包含与外部HIS/LIS系统的数据对接功能
- 不包含医学影像的AI分析功能
- 不包含财务结算等非诊疗功能

### 1.5 参考资料

- 《IEEE Std 830-1998 Recommended Practice for Software Requirements Specifications》
- 相关医疗信息标准（如ICD-10）
- Spring Boot官方文档
- Vue.js官方文档

## 2. 总体描述

### 2.1 产品描述

AI医疗诊断辅助系统是一个独立的Web应用程序，采用B/S架构设计。系统通过直观的Web界面为医生和管理员提供服务，后端集成了两种AI服务：AI诊断通过Java后端直接调用大模型API实现，血常规分析通过RESTful API调用独立的Python服务实现。

**系统架构组件：**

- 前端：基于Vue3的单页面应用，提供响应式用户界面
- 后端：Spring Boot应用，提供业务逻辑和数据服务
- 数据库：MySQL，负责业务数据存储
- AI诊断：Java后端集成大模型API，提供诊断推理能力
- 血常规分析服务：独立的Python服务，提供血常规AI分析能力

### 2.2 产品功能

系统包含以下主要功能模块：

1. **用户管理**：用户注册、登录认证、角色权限管理
2. **患者管理**：患者档案创建、查询、维护和病史管理
3. **AI诊断**：症状录入、调用大模型进行智能诊断推理、诊断结果确认
4. **血常规分析**：检验数据录入、调用Python AI服务分析、结果可视化展示
5. **数据统计**：系统使用统计、医疗数据分析、可视化报表
6. **系统审计**：操作日志记录、安全监控、行为追踪
7. **数据管理**：Excel数据导入导出、批量数据处理
8. **知识库**：医学知识查询、疾病信息检索

```
@startuml
left to right direction
actor 医生
actor 管理员

rectangle "AI医疗诊断辅助系统" {
  usecase UC1 as "用户管理"
  usecase UC2 as "患者信息管理"
  usecase UC3 as "AI辅助诊断"
  usecase UC4 as "血常规分析"
  usecase UC5 as "数据统计分析"
  usecase UC6 as "系统审计"
  usecase UC7 as "数据导入导出"
  usecase UC8 as "医学知识库"

  医生 --> UC2
  医生 --> UC3
  医生 --> UC4
  医生 --> UC7
  医生 --> UC8

  管理员 --> UC1
  管理员 --> UC5
  管理员 --> UC6
  管理员 --> UC7
  管理员 --> UC8
}
@enduml
```
### 2.3 用户特征

**医生用户：**

- 系统的主要使用者，负责日常诊疗工作
- 具备专业医学知识和临床经验
- 熟悉基本的计算机操作和医疗信息系统
- 期望系统能提供准确高效的诊断辅助，操作简便

**管理员用户：**

- 负责系统的运维管理和用户管理
- 具备一定的IT技术背景和系统管理经验
- 需要监控系统运行状态和用户使用情况
- 期望系统管理功能完善，操作界面清晰

### 2.4 运行环境

**服务器端：**

- 操作系统：Linux (推荐 CentOS 7+, Ubuntu 18.04+)
- Web服务器：Nginx 1.18+
- 应用服务器：Java环境 (JDK 11+)
- 血常规分析服务：Python 3.8+
- 数据库：MySQL 8.0+
- 容器化：Docker 20.10+ (可选)

**客户端：**

- 操作系统：Windows 7/10/11, macOS, Linux
- 浏览器：Chrome (最新版), Firefox (最新版), Edge (最新版)

**网络环境：**

- 支持HTTPS协议访问
- 稳定的内网或互联网连接
- 大模型API访问网络连通性

### 2.5 设计和实现约束

**技术栈限制：**

- 后端框架：Spring Boot 3.5.x, Spring Security, MyBatis Plus
- 前端框架：Vue 3, Element Plus, JavaScript, ECharts
- 数据库：MySQL 8.0+
- AI诊断：Java后端 + 大模型API（如通义千问、ChatGPT等）
- 血常规分析：Python, Flask/FastAPI

**开发规范：**

- 遵循RESTful API设计规范
- 采用JWT进行身份认证
- 遵循主流编码规范和最佳实践
- 支持容器化部署

**业务约束：**

- 系统需支持20名医生并发使用
- 患者数据必须加密存储
- 关键操作必须记录审计日志
- AI诊断结果仅供参考，最终诊断权在医生
- 大模型API调用需要考虑费用和频次限制

### 2.6 假设和依赖

**假设：**

- 用户具备基本的计算机操作能力
- 部署环境满足系统运行要求
- 大模型API服务稳定可用
- 血常规AI模型已经过充分训练和验证
- 输入的医疗数据准确完整

**依赖：**

- 稳定的网络连接以支持系统正常运行
- 大模型API服务的正常运行和响应
- Python血常规分析服务的正常运行和响应
- 管理员进行必要的系统配置和用户管理
- 服务器时间的准确配置

## 3. 功能需求

### 3.1 用户管理模块 (FR-USER)

```
@startuml
actor 医生
actor 管理员
rectangle 用户管理模块 {
  usecase "用户注册" as UC_REG
  usecase "用户登录" as UC_LOGIN
  usecase "个人信息管理" as UC_PROFILE
  usecase "密码管理" as UC_PWD
  usecase "用户账户管理" as UC_ACCOUNT
  usecase "角色权限管理" as UC_ROLE
}
医生 --> UC_REG
医生 --> UC_LOGIN
医生 --> UC_PROFILE
医生 --> UC_PWD
管理员 --> UC_ACCOUNT
管理员 --> UC_ROLE
@enduml
```
#### 3.1.1 用户认证与注册 (FR-USER-1)

- **FR-USER-1.1 用户注册**
    - **描述**：医生可以自主注册账户，管理员可以创建其他管理员账户
    - **输入**：用户名、密码、真实姓名、科室、联系方式等
    - **处理**：验证用户名唯一性，密码加密存储，默认分配医生角色
    - **输出**：注册成功提示，可直接登录系统
- **FR-USER-1.2 用户登录**
    - **描述**：已注册用户通过用户名和密码登录系统
    - **输入**：用户名、密码
    - **处理**：验证凭据，生成JWT令牌，记录登录日志
    - **输出**：登录成功跳转到相应主页或登录失败提示
- **FR-USER-1.3 个人信息管理**
    - **描述**：用户可以查看和修改个人基本信息
    - **输入**：待修改的个人信息
    - **处理**：更新用户信息，记录修改日志
    - **输出**：信息更新成功提示
- **FR-USER-1.4 密码管理**
    - **描述**：用户可以修改登录密码，管理员可以重置用户密码
    - **输入**：原密码、新密码（用户修改时）
    - **处理**：验证原密码，新密码加密存储
    - **输出**：密码修改成功提示

#### 3.1.2 用户账户管理 (FR-USER-2)

- **FR-USER-2.1 账户查询与管理**
    - **描述**：管理员可以查询、修改和管理医生账户
    - **输入**：查询条件（姓名、科室、状态等）
    - **处理**：检索用户信息，支持账户启用/禁用操作
    - **输出**：用户列表，账户状态变更结果
- **FR-USER-2.2 角色权限管理**
    - **描述**：管理员可以分配和管理用户角色权限
    - **处理**：基于RBAC模型，支持医生和管理员两种角色
    - **输出**：角色权限分配结果

### 3.2 患者信息管理模块 (FR-PATIENT)

```
@startuml
actor 医生
rectangle 患者信息管理模块 {
  usecase "创建患者档案" as UC_CREATE
  usecase "查询患者信息" as UC_QUERY
  usecase "修改患者档案" as UC_EDIT
  usecase "病史管理" as UC_HISTORY
  usecase "过敏史管理" as UC_ALLERGY
}
医生 --> UC_CREATE
医生 --> UC_QUERY
医生 --> UC_EDIT
医生 --> UC_HISTORY
医生 --> UC_ALLERGY
@enduml
```
#### 3.2.1 患者档案管理 (FR-PATIENT-1)

- **FR-PATIENT-1.1 创建患者档案**
    - **描述**：医生为新患者建立电子档案
    - **输入**：患者基本信息（姓名、性别、出生日期、身份证号、联系方式等）
    - **处理**：生成唯一患者ID，保存患者信息
    - **输出**：患者档案创建成功，返回患者ID
- **FR-PATIENT-1.2 患者信息查询**
    - **描述**：支持多条件组合查询患者档案
    - **输入**：查询条件（患者ID、姓名、身份证号、电话等）
    - **处理**：模糊查询和精确查询相结合
    - **输出**：符合条件的患者列表
- **FR-PATIENT-1.3 患者档案维护**
    - **描述**：医生可以修改患者基本信息
    - **输入**：更新后的患者信息
    - **处理**：验证数据有效性，更新患者档案
    - **输出**：档案更新成功提示

#### 3.2.2 医疗史管理 (FR-PATIENT-2)

- **FR-PATIENT-2.1 既往病史管理**
    - **描述**：记录和维护患者的重要病史信息
    - **输入**：疾病名称、确诊日期、治疗情况、备注
    - **处理**：关联医学知识库疾病信息，存储病史记录
    - **输出**：病史记录列表
- **FR-PATIENT-2.2 过敏史管理**
    - **描述**：记录患者的药物和食物过敏史
    - **输入**：过敏原、过敏反应、严重程度、记录日期
    - **处理**：支持常见过敏原选择和自定义输入
    - **输出**：过敏史记录列表

### 3.3 AI诊断核心模块 (FR-DIAGNOSIS)

```
@startuml
actor 医生
rectangle AI诊断核心模块 {
  usecase "症状信息录入" as UC_SYMPTOM
  usecase "大模型诊断推理" as UC_LLM_INFER
  usecase "诊断结果查看" as UC_VIEW_RESULT
  usecase "诊断确认" as UC_CONFIRM
  usecase "诊断报告生成" as UC_REPORT
}
医生 --> UC_SYMPTOM
UC_SYMPTOM --> UC_LLM_INFER
UC_LLM_INFER --> UC_VIEW_RESULT
医生 --> UC_CONFIRM
UC_CONFIRM --> UC_REPORT

note right of UC_LLM_INFER
  Java后端调用
  大模型API
end note
@enduml
```
#### 3.3.1 症状信息录入 (FR-DIAGNOSIS-1)

- **描述**：医生为患者录入当前症状和体征信息
- **输入**：
    - 结构化症状：通过症状分类树选择常见症状及属性
    - 自由文本：主诉、现病史、体格检查等描述
    - 生命体征：体温、血压、心率、呼吸频率等
- **处理**：系统记录症状信息，准备大模型推理数据
- **输出**：症状信息录入界面，已录入信息列表

#### 3.3.2 大模型诊断推理 (FR-DIAGNOSIS-2)

- **描述**：基于录入的症状信息通过Java后端调用大模型API进行诊断推理

- **输入**：患者ID、症状数据、生命体征、相关检查结果

- **处理**：

    1. Java后端构建结构化的医疗提示词
    2. 整合患者信息、症状描述和医学知识
    3. 调用大模型API（如通义千问、ChatGPT等）
    4. 解析大模型返回的诊断建议
    5. 格式化并验证诊断结果
- **输出**：AI诊断建议列表，包含：

    ```json
    {
      "diagnoses": [
        {
          "disease_name": "上呼吸道感染",
          "disease_code": "J06.9",
          "confidence": "较高可能性",
          "evidence_summary": "基于发热、咳嗽、咽痛等症状组合分析",
          "recommendations": "建议查血常规、胸片检查"
        }
      ],
      "suggested_tests": ["血常规", "胸片"],
      "risk_assessment": "低风险",
      "differential_diagnosis": ["感冒", "急性咽炎"],
      "treatment_suggestions": "对症治疗，多休息，多饮水"
    }
    ```


#### 3.3.3 诊断结果管理 (FR-DIAGNOSIS-3)

- **FR-DIAGNOSIS-3.1 诊断确认**
    - **描述**：医生查看大模型建议后确认最终诊断
    - **输入**：医生确认的诊断结论、治疗方案
    - **处理**：保存最终诊断结果，更新记录状态
    - **输出**：诊断确认成功提示
- **FR-DIAGNOSIS-3.2 诊断报告生成**
    - **描述**：生成标准化的诊断报告
    - **处理**：整合患者信息、症状、AI建议、最终诊断
    - **输出**：可打印的PDF诊断报告

### 3.4 血常规分析模块 (FR-BLOODTEST)

```
@startuml
actor 医生
rectangle 血常规分析模块 {
  usecase "血常规数据录入" as UC_INPUT
  usecase "历史数据查询" as UC_HISTORY
  usecase "Python AI分析" as UC_PY_ANALYSIS
  usecase "结果可视化展示" as UC_VISUAL
  usecase "分析报告生成" as UC_BT_REPORT
}
医生 --> UC_INPUT
医生 --> UC_HISTORY
UC_INPUT --> UC_PY_ANALYSIS
UC_PY_ANALYSIS --> UC_VISUAL
UC_VISUAL --> UC_BT_REPORT

note right of UC_PY_ANALYSIS
  调用Python血常规
  分析服务API
end note
@enduml
```
#### 3.4.1 血常规数据管理 (FR-BLOODTEST-1)

- **FR-BLOODTEST-1.1 数据录入**
    - **描述**：医生手动录入血常规检验结果
    - **输入**：血常规各项指标值和检查日期
    - **必需字段**：ID, WBC, LYMp, MIDp, NEUTp, LYMn, MIDn, NEUTn, RBC, HGB, HCT, MCV, MCH, MCHC, RDWSD, RDWCV, PLT, MPV, PDW, PCT, PLCR
    - **处理**：数据验证、异常值标记、参考范围对比
    - **输出**：录入成功提示，异常值高亮显示
- **FR-BLOODTEST-1.2 历史数据查询**
    - **描述**：查询患者历次血常规检查结果
    - **输入**：患者ID、查询时间范围
    - **处理**：检索历史记录，支持多次结果对比
    - **输出**：历史数据列表，趋势变化图表

#### 3.4.2 Python AI血常规分析 (FR-BLOODTEST-2)

- **描述**：调用独立的Python服务进行血常规AI模式识别和辅助解读

- **输入**：患者血常规指标数据

- **处理**：

    1. Java后端构建血常规分析请求数据
    2. 调用Python血常规分析服务API
    3. Python服务进行无监督学习识别异常模式
    4. Python服务进行监督学习预测疾病风险
    5. 返回分析结果和建议给Java后端
    6. Java后端处理和存储分析结果
- **输出**：血常规AI分析结果：

    ```json
    {
      "analysis_summary": "提示可能存在细菌感染",
      "abnormal_indicators": [
        {
          "indicator": "WBC",
          "value": 12.5,
          "reference_range": "4.0-10.0",
          "interpretation": "轻度升高",
          "clinical_significance": "可能提示感染或炎症"
        }
      ],
      "risk_patterns": [
        {
          "pattern_name": "细菌感染模式",
          "confidence": 0.75,
          "supporting_indicators": ["WBC", "NEUTp"]
        }
      ],
      "suggestions": "建议结合临床症状进一步检查，考虑感染指标检测"
    }
    ```


#### 3.4.3 结果展示与报告 (FR-BLOODTEST-3)

- **FR-BLOODTEST-3.1 可视化展示**
    - **描述**：图表化展示分析结果和趋势
    - **处理**：使用ECharts生成雷达图、折线图、柱状图
    - **输出**：交互式数据可视化界面
- **FR-BLOODTEST-3.2 报告生成**
    - **描述**：生成规范化的血常规分析报告
    - **处理**：整合原始数据、Python AI分析、可视化图表
    - **输出**：PDF格式分析报告，支持打印

### 3.5 数据统计分析模块 (FR-STATISTICS)

```
@startuml
actor 管理员
actor 医生
rectangle 数据统计分析模块 {
  usecase "系统使用统计" as UC_USAGE
  usecase "医疗数据统计" as UC_MEDICAL
  usecase "个人工作统计" as UC_PERSONAL
  usecase "数据可视化展示" as UC_CHART
}
管理员 --> UC_USAGE
管理员 --> UC_MEDICAL
医生 --> UC_PERSONAL
管理员 --> UC_CHART
医生 --> UC_CHART
@enduml
```
#### 3.5.1 系统统计功能 (FR-STATISTICS-1)

- **FR-STATISTICS-1.1 系统使用统计**
    - **描述**：管理员查看系统整体使用情况
    - **统计维度**：用户活跃度、功能使用频次、诊断数量、血常规分析次数、大模型调用次数
    - **时间范围**：日、周、月、季度、年度统计
    - **输出**：统计图表和数据报表
- **FR-STATISTICS-1.2 医疗数据统计**
    - **描述**：统计分析医疗业务数据
    - **统计内容**：
        - 疾病分布统计
        - 血常规异常率统计
        - AI诊断准确率分析
        - 各科室工作量统计
        - 大模型API使用统计
    - **输出**：医疗数据分析报表

#### 3.5.2 个人工作统计 (FR-STATISTICS-2)

- **描述**：医生查看个人工作数据统计
- **统计内容**：
    - 个人诊断数量统计
    - 常见疾病分布
    - 血常规分析统计
    - 工作量趋势分析
- **输出**：个人工作台数据展示

#### 3.5.3 数据可视化 (FR-STATISTICS-3)

- **描述**：使用图表形式展示统计数据
- **图表类型**：折线图、柱状图、饼图、雷达图
- **交互功能**：时间范围筛选、数据钻取、图表导出
- **技术实现**：基于ECharts图表库

### 3.6 系统审计模块 (FR-AUDIT)

```
@startuml
actor 管理员
rectangle 系统审计模块 {
  usecase "操作日志记录" as UC_LOG
  usecase "审计日志查询" as UC_QUERY
  usecase "安全监控" as UC_MONITOR
  usecase "行为分析" as UC_ANALYSIS
}
管理员 --> UC_QUERY
管理员 --> UC_MONITOR
管理员 --> UC_ANALYSIS
note right of UC_LOG : 系统自动记录
@enduml
```
#### 3.6.1 日志记录功能 (FR-AUDIT-1)

- **描述**：系统自动记录用户关键操作
- **记录内容**：
    - 用户登录/登出
    - 患者信息创建/修改
    - 诊断确认操作
    - 血常规数据录入
    - 数据导入导出
    - 系统配置修改
    - 大模型API调用记录
    - Python服务调用记录
- **记录信息**：操作人、操作时间、IP地址、操作类型、操作对象、操作结果
- **技术实现**：使用AOP切面编程自动记录

#### 3.6.2 审计查询分析 (FR-AUDIT-2)

- **FR-AUDIT-2.1 日志查询**
    - **描述**：管理员查询和检索审计日志
    - **查询条件**：用户、时间范围、操作类型、结果状态
    - **输出**：审计日志列表，支持导出
- **FR-AUDIT-2.2 安全监控**
    - **描述**：监控异常操作和安全风险
    - **监控内容**：频繁登录失败、异常操作时间、大批量数据操作、API调用异常
    - **输出**：安全告警和风险提示

### 3.7 数据导入导出模块 (FR-IMPORT-EXPORT)

```
@startuml
actor 医生
actor 管理员
rectangle 数据导入导出模块 {
  usecase "Excel数据导入" as UC_IMPORT
  usecase "数据导出" as UC_EXPORT
  usecase "批量数据处理" as UC_BATCH
  usecase "导入导出日志" as UC_IE_LOG
}
医生 --> UC_IMPORT
医生 --> UC_EXPORT
管理员 --> UC_BATCH
管理员 --> UC_IE_LOG
@enduml
```
#### 3.7.1 数据导入功能 (FR-IMPORT-EXPORT-1)

- **FR-IMPORT-EXPORT-1.1 Excel数据导入**
    - **描述**：支持Excel格式的血常规数据批量导入
    - **输入**：符合模板格式的Excel文件
    - **处理**：
        - 文件格式验证
        - 数据解析和字段映射
        - 数据有效性检查
        - 批量保存到数据库
    - **输出**：导入结果统计，错误数据报告
- **FR-IMPORT-EXPORT-1.2 导入进度反馈**
    - **描述**：提供数据导入的进度提示
    - **处理**：前端显示导入进度，后端同步处理
    - **输出**：进度条显示，实时状态更新

#### 3.7.2 数据导出功能 (FR-IMPORT-EXPORT-2)

- **FR-IMPORT-EXPORT-2.1 报表导出**
    - **描述**：支持各类数据和报表的Excel导出
    - **导出内容**：
        - 患者信息列表
        - 诊断记录报表
        - 血常规数据报表
        - 统计分析报表
    - **输出**：Excel格式文件下载
- **FR-IMPORT-EXPORT-2.2 批量数据处理**
    - **描述**：支持大批量数据的导入导出操作
    - **处理限制**：单次处理不超过1000条记录
    - **错误处理**：详细的错误信息反馈和数据验证

### 3.8 医学知识库模块 (FR-KNOWLEDGE)

```
@startuml
actor 医生
actor 管理员
rectangle 医学知识库模块 {
  usecase "疾病信息查询" as UC_DISEASE
  usecase "药品信息查询" as UC_DRUG
  usecase "知识库维护" as UC_MAINTAIN
}
医生 --> UC_DISEASE
医生 --> UC_DRUG
管理员 --> UC_MAINTAIN
@enduml
```
#### 3.8.1 知识查询功能 (FR-KNOWLEDGE-1)

- **FR-KNOWLEDGE-1.1 疾病信息查询**
    - **描述**：提供疾病基础信息的查询功能
    - **查询方式**：疾病名称、ICD-10编码、症状关键词
    - **查询结果**：疾病描述、主要症状、诊断要点、治疗建议
- **FR-KNOWLEDGE-1.2 药品信息查询**
    - **描述**：提供基础的药品信息查询
    - **查询内容**：药品名称、用法用量、适应症、禁忌症
    - **输出**：药品详细信息页面

#### 3.8.2 知识库维护 (FR-KNOWLEDGE-2)

- **描述**：管理员维护医学知识库内容
- **维护内容**：疾病信息、药品信息的增删改查
- **数据来源**：权威医学资料和临床指南
- **更新机制**：定期更新和版本管理

## 4. 非功能需求

### 4.1 性能需求

- **NFR-PERF-1 响应时间**：
    - 用户登录响应时间 ≤ 2秒
    - 页面加载时间 ≤ 3秒
    - 大模型API诊断推理时间 ≤ 10秒
    - Python血常规AI分析时间 ≤ 5秒
    - 数据查询响应时间 ≤ 3秒
- **NFR-PERF-2 并发性能**：
    - 支持20名医生同时在线使用
    - 支持日均1000次诊断请求
    - 支持日均500次血常规分析请求
    - 系统可用性 ≥ 99%
- **NFR-PERF-3 数据容量**：
    - 支持10万份患者档案存储
    - 支持100万条血常规记录
    - 数据库查询优化和索引设计

### 4.2 安全需求

- **NFR-SEC-1 数据安全**：
    - 患者敏感信息加密存储
    - 用户密码哈希加密
    - 数据传输HTTPS加密
    - 大模型API调用数据脱敏
- **NFR-SEC-2 访问控制**：
    - 基于角色的权限控制
    - JWT身份认证机制
    - 会话超时自动登出
    - API调用频次限制
- **NFR-SEC-3 安全防护**：
    - 防SQL注入攻击
    - 防XSS攻击
    - 防CSRF攻击
    - 文件上传安全检查
    - API密钥安全管理

### 4.3 可靠性需求

- **NFR-REL-1 容错性**：
    - 数据库连接失败自动重试
    - 大模型API服务不可用时优雅降级
    - Python血常规服务不可用时优雅降级
    - 异常情况下的错误提示
- **NFR-REL-2 数据备份**：
    - 数据库定期自动备份
    - 关键数据冗余存储
    - 数据恢复机制
- **NFR-REL-3 服务监控**：
    - 大模型API调用监控
    - Python服务健康检查
    - 系统性能监控

### 4.4 可用性需求

- **NFR-USA-1 用户体验**：
    - 直观友好的用户界面
    - 响应式设计适配不同屏幕
    - 清晰的操作提示和错误信息
    - AI分析过程进度提示
- **NFR-USA-2 易用性**：
    - 简化的操作流程
    - 快捷操作支持
    - 在线帮助文档

### 4.5 可维护性需求

- **NFR-MAIN-1 代码质量**：
    - 模块化设计架构
    - 清晰的代码注释
    - 统一的编码规范
- **NFR-MAIN-2 系统维护**：
    - 配置文件化管理
    - 详细的系统日志
    - 容器化部署支持
    - 大模型API配置灵活切换

## 5. 外部接口需求

### 5.1 用户界面

- **GUI-1 设计风格**：采用现代化、扁平化设计风格，色彩搭配专业医疗风格
- **GUI-2 响应式布局**：支持1920x1080及以上分辨率，优先桌面端体验
- **GUI-3 交互设计**：操作流程简洁明了，重要操作有确认提示
- **GUI-4 数据展示**：表格支持排序筛选分页，图表支持交互操作
- **GUI-5 表单设计**：输入验证提示，必填项明确标识
- **GUI-6 AI分析界面**：清晰展示AI分析过程和结果，支持结果对比

### 5.2 软件接口

#### 5.2.1 内部API接口

**API设计规范**：

- 遵循RESTful设计风格
- 使用标准HTTP方法和状态码
- JSON格式数据交换
- 统一的响应格式

**核心API示例**：

```
用户管理：
POST   /api/v1/auth/login           # 用户登录
POST   /api/v1/auth/register        # 用户注册
GET    /api/v1/users               # 获取用户列表
PUT    /api/v1/users/{id}          # 更新用户信息

患者管理：
GET    /api/v1/patients            # 获取患者列表
POST   /api/v1/patients            # 创建患者
GET    /api/v1/patients/{id}       # 获取患者详情
PUT    /api/v1/patients/{id}       # 更新患者信息

诊断管理：
POST   /api/v1/diagnosis           # 创建诊断记录
POST   /api/v1/diagnosis/{id}/llm-analyze    # 大模型诊断分析
PUT    /api/v1/diagnosis/{id}/confirm       # 确认诊断

血常规：
POST   /api/v1/blood-tests         # 创建血常规记录
POST   /api/v1/blood-tests/{id}/analyze     # Python AI分析
GET    /api/v1/patients/{id}/blood-tests    # 患者血常规历史
```

**统一响应格式**：

```json
{
  "code": 200,
  "message": "success",
  "data": {},
}
```

## 6. 数据模型与字典

### 6.1 数据模型描述 (ER图)

```
@startuml
entity Doctor {
  * id (PK) : BIGINT
  --
  * username : VARCHAR(50)
  * password : VARCHAR(255)
  * real_name : VARCHAR(100)
  * role : VARCHAR(20)
  * department : VARCHAR(50)
  * email : VARCHAR(100)
  * phone : VARCHAR(20)
  * status : TINYINT
  * create_time : DATETIME
  * update_time : DATETIME
}

entity Patient {
  * id (PK) : BIGINT
  --
  * patient_no : VARCHAR(50) <<unique>>
  * name : VARCHAR(100)
  * gender : TINYINT
  * birth_date : DATE
  * id_card : VARCHAR(20)
  * phone : VARCHAR(20)
  * address : TEXT
  * allergies : TEXT
  * medical_history : TEXT
  * create_time : DATETIME
  * update_time : DATETIME
  * creator_id : BIGINT <<FK>>
}

entity DiagnosisRecord {
  * id (PK) : BIGINT
  --
  * patient_id (FK) : BIGINT
  * doctor_id (FK) : BIGINT
  * symptoms_text : TEXT
  * symptoms_structured : JSON
  * vital_signs : JSON
  * llm_request_data : JSON
  * llm_response_data : JSON
  * final_diagnosis : TEXT
  * treatment_plan : TEXT
  * status : VARCHAR(20)
  * diagnosis_time : DATETIME
  * create_time : DATETIME
  * update_time : DATETIME
}

entity BloodTest {
  * id (PK) : BIGINT
  --
  * patient_id (FK) : BIGINT
  * diagnosis_record_id (FK) : BIGINT <<nullable>>
  * test_date : DATETIME
  * WBC : DECIMAL(6,2)
  * LYMp : DECIMAL(5,1)
  * MIDp : DECIMAL(5,1)
  * NEUTp : DECIMAL(5,1)
  * LYMn : DECIMAL(6,2)
  * MIDn : DECIMAL(6,2)
  * NEUTn : DECIMAL(6,2)
  * RBC : DECIMAL(6,2)
  * HGB : DECIMAL(6,1)
  * HCT : DECIMAL(5,1)
  * MCV : DECIMAL(6,1)
  * MCH : DECIMAL(6,1)
  * MCHC : DECIMAL(6,1)
  * RDWSD : DECIMAL(6,1)
  * RDWCV : DECIMAL(5,1)
  * PLT : DECIMAL(6,1)
  * MPV : DECIMAL(5,1)
  * PDW : DECIMAL(5,1)
  * PCT : DECIMAL(5,3)
  * PLCR : DECIMAL(5,1)
  * python_analysis_result : JSON
  * create_time : DATETIME
  * update_time : DATETIME
}

entity MedicalKnowledge {
  * id (PK) : BIGINT
  --
  * entry_type : VARCHAR(20)
  * title : VARCHAR(255)
  * disease_name : VARCHAR(200)
  * disease_code : VARCHAR(50)
  * drug_name : VARCHAR(200)
  * category : VARCHAR(100)
  * description : TEXT
  * symptoms : JSON
  * treatment_guide : TEXT
  * status : TINYINT
  * create_time : DATETIME
  * update_time : DATETIME
}

entity AuditLog {
  * id (PK) : BIGINT
  --
  * user_id (FK) : BIGINT
  * username : VARCHAR(50)
  * action_type : VARCHAR(50)
  * target_type : VARCHAR(50)
  * target_id : VARCHAR(100)
  * ip_address : VARCHAR(45)
  * user_agent : VARCHAR(255)
  * request_params : JSON
  * response_status : VARCHAR(20)
  * error_message : TEXT
  * operation_time : DATETIME
}

entity LLMCallLog {
  * id (PK) : BIGINT
  --
  * diagnosis_record_id (FK) : BIGINT
  * user_id (FK) : BIGINT
  * llm_provider : VARCHAR(50)
  * model_name : VARCHAR(100)
  * prompt_tokens : INT
  * completion_tokens : INT
  * total_tokens : INT
  * call_duration : INT
  * status : VARCHAR(20)
  * error_message : TEXT
  * call_time : DATETIME
}

Doctor "1" -- "0..*" Patient : creates
Doctor "1" -- "0..*" DiagnosisRecord : diagnoses
Patient "1" -- "0..*" DiagnosisRecord : has
Patient "1" -- "0..*" BloodTest : has
DiagnosisRecord "1" -- "0..*" BloodTest : may_include
Doctor "1" -- "0..*" AuditLog : performs
DiagnosisRecord "1" -- "0..*" LLMCallLog : generates
@enduml
```
### 6.2 核心数据表结构

#### 6.2.1 医生表 (doctor)

| 字段名         | 数据类型         | 约束                  | 说明               |
| ----------- | ------------ | ------------------- | ---------------- |
| id          | BIGINT       | PK, AI              | 主键ID             |
| username    | VARCHAR(50)  | NOT NULL, UNIQUE    | 登录用户名            |
| password    | VARCHAR(255) | NOT NULL            | 登录密码(加密)         |
| real_name   | VARCHAR(100) | NOT NULL            | 真实姓名             |
| role        | VARCHAR(20)  | NOT NULL            | 角色(DOCTOR/ADMIN) |
| department  | VARCHAR(50)  | NULL                | 科室               |
| email       | VARCHAR(100) | NULL                | 邮箱               |
| phone       | VARCHAR(20)  | NULL                | 电话               |
| status      | TINYINT      | NOT NULL, DEFAULT 1 | 状态(1:启用,0:禁用)    |
| create_time | DATETIME     | NOT NULL            | 创建时间             |
| update_time | DATETIME     | NOT NULL            | 更新时间             |

**索引设计：**

- PRIMARY KEY: `id`
- UNIQUE KEY: `username`
- INDEX: `role`, `status`

#### 6.2.2 患者表 (patient)

|字段名|数据类型|约束|说明|
|---|---|---|---|
|id|BIGINT|PK, AI|主键ID|
|patient_no|VARCHAR(50)|NOT NULL, UNIQUE|患者编号|
|name|VARCHAR(100)|NOT NULL|姓名|
|gender|TINYINT|NULL|性别(0:女,1:男,2:未知)|
|birth_date|DATE|NULL|出生日期|
|id_card|VARCHAR(20)|NULL|身份证号|
|phone|VARCHAR(20)|NULL|电话|
|address|TEXT|NULL|地址|
|allergies|TEXT|NULL|过敏史|
|medical_history|TEXT|NULL|既往病史|
|create_time|DATETIME|NOT NULL|创建时间|
|update_time|DATETIME|NOT NULL|更新时间|
|creator_id|BIGINT|NOT NULL, FK|创建医生ID|

**索引设计：**

- PRIMARY KEY: `id`
- UNIQUE KEY: `patient_no`
- INDEX: `name`, `id_card`, `phone`, `creator_id`

#### 6.2.3 诊断记录表 (diagnosis_record)

| 字段名                 | 数据类型        | 约束           | 说明      |
| ------------------- | ----------- | ------------ | ------- |
| id                  | BIGINT      | PK, AI       | 主键ID    |
| patient_id          | BIGINT      | NOT NULL, FK | 患者ID    |
| doctor_id           | BIGINT      | NOT NULL, FK | 医生ID    |
| symptoms_text       | TEXT        | NULL         | 症状文本    |
| symptoms_structured | JSON        | NULL         | 结构化症状   |
| vital_signs         | JSON        | NULL         | 生命体征    |
| llm_request_data    | JSON        | NULL         | 大模型请求数据 |
| llm_response_data   | JSON        | NULL         | 大模型响应数据 |
| final_diagnosis     | TEXT        | NULL         | 最终诊断    |
| treatment_plan      | TEXT        | NULL         | 治疗方案    |
| status              | VARCHAR(20) | NOT NULL     | 状态      |
| diagnosis_time      | DATETIME    | NOT NULL     | 诊断时间    |
| create_time         | DATETIME    | NOT NULL     | 创建时间    |
| update_time         | DATETIME    | NOT NULL     | 更新时间    |

**索引设计：**

- PRIMARY KEY: `id`
- INDEX: `patient_id`, `doctor_id`, `diagnosis_time`, `status`

#### 6.2.4 血常规表 (blood_test)

|字段名|数据类型|约束|说明|
|---|---|---|---|
|id|BIGINT|PK, AI|主键ID|
|patient_id|BIGINT|NOT NULL, FK|患者ID|
|diagnosis_record_id|BIGINT|NULL, FK|诊断记录ID|
|test_date|DATETIME|NOT NULL|检测日期|
|WBC|DECIMAL(6,2)|NULL|白细胞计数|
|LYMp|DECIMAL(5,1)|NULL|淋巴细胞百分比|
|MIDp|DECIMAL(5,1)|NULL|中间细胞百分比|
|NEUTp|DECIMAL(5,1)|NULL|中性粒细胞百分比|
|LYMn|DECIMAL(6,2)|NULL|淋巴细胞绝对值|
|MIDn|DECIMAL(6,2)|NULL|中间细胞绝对值|
|NEUTn|DECIMAL(6,2)|NULL|中性粒细胞绝对值|
|RBC|DECIMAL(6,2)|NULL|红细胞计数|
|HGB|DECIMAL(6,1)|NULL|血红蛋白|
|HCT|DECIMAL(5,1)|NULL|红细胞压积|
|MCV|DECIMAL(6,1)|NULL|平均红细胞体积|
|MCH|DECIMAL(6,1)|NULL|平均红细胞血红蛋白含量|
|MCHC|DECIMAL(6,1)|NULL|平均红细胞血红蛋白浓度|
|RDWSD|DECIMAL(6,1)|NULL|红细胞分布宽度SD|
|RDWCV|DECIMAL(5,1)|NULL|红细胞分布宽度CV|
|PLT|DECIMAL(6,1)|NULL|血小板计数|
|MPV|DECIMAL(5,1)|NULL|平均血小板体积|
|PDW|DECIMAL(5,1)|NULL|血小板分布宽度|
|PCT|DECIMAL(5,3)|NULL|血小板压积|
|PLCR|DECIMAL(5,1)|NULL|大血小板比率|
|python_analysis_result|JSON|NULL|Python AI分析结果|
|create_time|DATETIME|NOT NULL|创建时间|
|update_time|DATETIME|NOT NULL|更新时间|

**索引设计：**

- PRIMARY KEY: `id`
- INDEX: `patient_id`, `test_date`, `diagnosis_record_id`

#### 6.2.5 医学知识库表 (medical_knowledge)

|字段名|数据类型|约束|说明|
|---|---|---|---|
|id|BIGINT|PK, AI|主键ID|
|entry_type|VARCHAR(20)|NOT NULL|条目类型(DISEASE/DRUG)|
|title|VARCHAR(255)|NOT NULL|标题|
|disease_name|VARCHAR(200)|NULL|疾病名称|
|disease_code|VARCHAR(50)|NULL|疾病编码|
|drug_name|VARCHAR(200)|NULL|药品名称|
|category|VARCHAR(100)|NULL|分类|
|description|TEXT|NULL|描述|
|symptoms|JSON|NULL|症状信息|
|treatment_guide|TEXT|NULL|治疗指南|
|status|TINYINT|NOT NULL, DEFAULT 1|状态|
|create_time|DATETIME|NOT NULL|创建时间|
|update_time|DATETIME|NOT NULL|更新时间|

**索引设计：**

- PRIMARY KEY: `id`
- INDEX: `entry_type`, `disease_name`, `drug_name`, `status`

#### 6.2.6 审计日志表 (audit_log)

|字段名|数据类型|约束|说明|
|---|---|---|---|
|id|BIGINT|PK, AI|主键ID|
|user_id|BIGINT|NOT NULL, FK|用户ID|
|username|VARCHAR(50)|NOT NULL|用户名|
|action_type|VARCHAR(50)|NOT NULL|操作类型|
|target_type|VARCHAR(50)|NULL|目标类型|
|target_id|VARCHAR(100)|NULL|目标ID|
|ip_address|VARCHAR(45)|NULL|IP地址|
|user_agent|VARCHAR(255)|NULL|用户代理|
|request_params|JSON|NULL|请求参数|
|response_status|VARCHAR(20)|NULL|响应状态|
|error_message|TEXT|NULL|错误信息|
|operation_time|DATETIME|NOT NULL|操作时间|

**索引设计：**

- PRIMARY KEY: `id`
- INDEX: `user_id`, `action_type`, `operation_time`

#### 6.2.7 大模型调用日志表 (llm_call_log)

|字段名|数据类型|约束|说明|
|---|---|---|---|
|id|BIGINT|PK, AI|主键ID|
|diagnosis_record_id|BIGINT|NOT NULL, FK|诊断记录ID|
|user_id|BIGINT|NOT NULL, FK|用户ID|
|llm_provider|VARCHAR(50)|NOT NULL|大模型提供商|
|model_name|VARCHAR(100)|NOT NULL|模型名称|
|prompt_tokens|INT|NULL|提示词Token数|
|completion_tokens|INT|NULL|完成Token数|
|total_tokens|INT|NULL|总Token数|
|call_duration|INT|NULL|调用耗时(毫秒)|
|status|VARCHAR(20)|NOT NULL|调用状态|
|error_message|TEXT|NULL|错误信息|
|call_time|DATETIME|NOT NULL|调用时间|

**索引设计：**

- PRIMARY KEY: `id`
- INDEX: `diagnosis_record_id`, `user_id`, `call_time`
- INDEX: `llm_provider`, `status`

## 7. 附录

### 7.1 术语表

|术语|英文全称|中文解释|
|---|---|---|
|AI|Artificial Intelligence|人工智能|
|API|Application Programming Interface|应用程序编程接口|
|JWT|JSON Web Token|JSON网络令牌|
|RBAC|Role-Based Access Control|基于角色的访问控制|
|REST|Representational State Transfer|表现状态转移|
|LLM|Large Language Model|大语言模型|
|血常规|Complete Blood Count|血液常规检验|
|WBC|White Blood Cell|白细胞|
|RBC|Red Blood Cell|红细胞|
|HGB|Hemoglobin|血红蛋白|
|PLT|Platelet|血小板|

### 7.2 系统架构图

```
@startuml
!define RECTANGLE class

RECTANGLE Frontend {
  + Vue 3
  + Element Plus
  + TypeScript
  + ECharts
}

RECTANGLE Backend {
  + Spring Boot
  + Spring Security
  + MyBatis Plus
  + RESTful API
}

RECTANGLE Database {
  + MySQL 8.0
}

RECTANGLE LLMService {
  + 大模型API
  + 通义千问/ChatGPT
  + 诊断推理
}

RECTANGLE PythonService {
  + Python
  + Flask/FastAPI
  + 血常规AI分析
  + Machine Learning
}

Frontend --> Backend : HTTPS/REST API
Backend --> Database : JDBC
Backend --> LLMService : HTTPS API
Backend --> PythonService : HTTP API

note right of LLMService
  Java后端直接调用
  大模型API进行诊断
end note

note right of PythonService
  独立的Python服务
  专门处理血常规分析
end note
@enduml
```
### 7.3 部署架构图

```
@startuml
cloud "大模型服务商" {
  [通义千问API]
  [ChatGPT API]
  [其他LLM API]
}

node "应用服务器" {
  [Spring Boot应用]
  [Vue.js前端]
}

node "Python服务器" {
  [血常规分析服务]
  [机器学习模型]
}

database "数据库服务器" {
  [MySQL 8.0]
}

[Spring Boot应用] --> [MySQL 8.0] : JDBC连接
[Spring Boot应用] --> [通义千问API] : HTTPS调用
[Spring Boot应用] --> [ChatGPT API] : HTTPS调用
[Spring Boot应用] --> [血常规分析服务] : HTTP API调用
[Vue.js前端] --> [Spring Boot应用] : RESTful API

note top of [血常规分析服务]
  独立部署的Python服务
  提供血常规AI分析能力
end note

note top of [通义千问API]
  Java后端直接调用
  进行诊断推理
end note
@enduml
```

**医疗诊断提示词模板**：

```java
@Component
public class MedicalPromptTemplate {
    
    public String buildDiagnosisPrompt(DiagnosisRequest request) {
        return String.format("""
            你是一位经验丰富的临床医生，请根据以下患者信息进行诊断分析：
            
            患者基本信息：
            - 年龄：%s岁
            - 性别：%s
            - 既往病史：%s
            - 过敏史：%s
            
            当前症状：
            %s
            
            生命体征：
            %s
            
            请提供：
            1. 最可能的诊断（按可能性排序）
            2. 鉴别诊断
            3. 建议的进一步检查
            4. 初步治疗建议
            5. 风险评估
            
            请以JSON格式返回结果，确保专业性和准确性。
            """, 
            request.getAge(), 
            request.getGender(),
            request.getMedicalHistory(),
            request.getAllergies(),
            request.getSymptomsText(),
            request.getVitalSigns()
        );
    }
}
```



**版本：** V2.1  
**完成日期：** 2025-06-5  
**项目代码：** MEDICAL-AI-002

本需求规格说明书V2.1版本已更新AI服务架构，明确了AI诊断功能通过Java后端调用大模型API实现，血常规分析功能通过调用独立的Python服务实现。文档涵盖了完整的技术规范、接口定义、数据模型和部署架构，为项目开发、测试和验收提供了详细的技术依据。