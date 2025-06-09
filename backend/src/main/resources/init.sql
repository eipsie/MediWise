-- 医生表
CREATE TABLE doctor (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '登录用户名',
    password VARCHAR(255) NOT NULL COMMENT '登录密码(MD5加密)',
    real_name VARCHAR(100) NOT NULL COMMENT '医生真实姓名',
    role VARCHAR(20) NOT NULL DEFAULT 'DOCTOR' COMMENT '角色(DOCTOR/ADMIN)',
    department VARCHAR(50) COMMENT '科室',
    email VARCHAR(100) COMMENT '电子邮箱',
    phone VARCHAR(20) COMMENT '联系电话',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1:启用,0:禁用)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '医生信息表';

-- 创建医生表的索引
CREATE INDEX idx_doctor_role ON doctor(role) COMMENT '医生角色索引';
CREATE INDEX idx_doctor_status ON doctor(status) COMMENT '医生状态索引';

-- 患者表
CREATE TABLE patient (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    patient_no VARCHAR(50) UNIQUE NOT NULL COMMENT '患者编号',
    name VARCHAR(100) NOT NULL COMMENT '姓名',
    gender TINYINT COMMENT '性别(0:女,1:男,2:未知)',
    birth_date DATE COMMENT '出生日期',
    id_card VARCHAR(20) COMMENT '身份证号',
    phone VARCHAR(20) COMMENT '电话',
    address TEXT COMMENT '地址',
    allergies TEXT COMMENT '过敏史',
    medical_history TEXT COMMENT '既往病史',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    creator_id BIGINT NOT NULL COMMENT '创建医生ID',
    FOREIGN KEY (creator_id) REFERENCES doctor(id)
) COMMENT '患者信息表';

-- 创建患者表的索引
CREATE INDEX idx_patient_name ON patient(name) COMMENT '患者姓名索引';
CREATE INDEX idx_patient_id_card ON patient(id_card) COMMENT '患者身份证索引';
CREATE INDEX idx_patient_phone ON patient(phone) COMMENT '患者电话索引';
CREATE INDEX idx_patient_creator ON patient(creator_id) COMMENT '患者创建医生索引';

-- 诊断记录表
CREATE TABLE diagnosis_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    doctor_id BIGINT NOT NULL COMMENT '医生ID',
    symptoms_text TEXT COMMENT '症状文本',
    symptoms_structured JSON COMMENT '结构化症状',
    vital_signs JSON COMMENT '生命体征',
    llm_request_data JSON COMMENT '大模型请求数据',
    llm_response_data JSON COMMENT '大模型响应数据',
    final_diagnosis TEXT COMMENT '最终诊断',
    treatment_plan TEXT COMMENT '治疗方案',
    status VARCHAR(20) NOT NULL COMMENT '状态',
    diagnosis_time DATETIME NOT NULL COMMENT '诊断时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (patient_id) REFERENCES patient(id),
    FOREIGN KEY (doctor_id) REFERENCES doctor(id)
) COMMENT '诊断记录表';

-- 创建诊断记录表的索引
CREATE INDEX idx_diagnosis_patient ON diagnosis_record(patient_id) COMMENT '诊断-患者关联索引';
CREATE INDEX idx_diagnosis_doctor ON diagnosis_record(doctor_id) COMMENT '诊断-医生关联索引';
CREATE INDEX idx_diagnosis_time ON diagnosis_record(diagnosis_time) COMMENT '诊断时间索引';
CREATE INDEX idx_diagnosis_status ON diagnosis_record(status) COMMENT '诊断状态索引';

-- 血常规表 (更新为需求文档中定义的结构)
CREATE TABLE blood_test (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    test_date DATETIME NOT NULL COMMENT '检测日期',
    WBC DECIMAL(6,2) COMMENT '白细胞计数',
    RBC DECIMAL(6,2) COMMENT '红细胞计数',
    HGB DECIMAL(6,1) COMMENT '血红蛋白',
    PLT DECIMAL(6,1) COMMENT '血小板计数',
    NEUTp DECIMAL(5,1) COMMENT '中性粒细胞百分比',
    LYMp DECIMAL(5,1) COMMENT '淋巴细胞百分比',
    ai_analysis_result JSON COMMENT 'AI分析结果',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (patient_id) REFERENCES patient(id)
) COMMENT '血常规检查表';

-- 创建血常规表的索引
CREATE INDEX idx_blood_test_patient ON blood_test(patient_id) COMMENT '血常规-患者关联索引';
CREATE INDEX idx_blood_test_date ON blood_test(test_date) COMMENT '血常规-检测日期索引';

-- 医学知识库表
CREATE TABLE medical_knowledge (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    entry_type VARCHAR(20) NOT NULL COMMENT '条目类型(DISEASE/DRUG)',
    title VARCHAR(255) NOT NULL COMMENT '标题',
    disease_name VARCHAR(200) COMMENT '疾病名称',
    disease_code VARCHAR(50) COMMENT '疾病编码',
    drug_name VARCHAR(200) COMMENT '药品名称',
    category VARCHAR(100) COMMENT '分类',
    description TEXT COMMENT '描述',
    symptoms JSON COMMENT '相关症状',
    treatment_guide TEXT COMMENT '治疗指南',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1:启用,0:禁用)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '医学知识库表';

-- 创建医学知识库表的索引
CREATE INDEX idx_knowledge_entry_type ON medical_knowledge(entry_type) COMMENT '知识库-条目类型索引';
CREATE INDEX idx_knowledge_disease ON medical_knowledge(disease_name) COMMENT '知识库-疾病名称索引';
CREATE INDEX idx_knowledge_drug ON medical_knowledge(drug_name) COMMENT '知识库-药品名称索引';
CREATE INDEX idx_knowledge_status ON medical_knowledge(status) COMMENT '知识库-状态索引';

-- 系统日志表 (按照需求文档中的新表定义)
CREATE TABLE system_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    log_type VARCHAR(20) NOT NULL COMMENT '日志类型：OPERATION/ERROR/AI_CALL',
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    action_type VARCHAR(50) NOT NULL COMMENT '操作类型',
    action_desc VARCHAR(200) COMMENT '操作描述',
    target_id VARCHAR(50) COMMENT '操作对象ID',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    status VARCHAR(20) NOT NULL COMMENT '状态：SUCCESS/FAILED/ERROR',
    error_message VARCHAR(500) COMMENT '错误信息',
    log_time DATETIME NOT NULL COMMENT '操作时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES doctor(id) ON DELETE SET NULL
) COMMENT '系统日志表';

-- 创建系统日志表的索引
CREATE INDEX idx_syslog_type ON system_log(log_type) COMMENT '系统日志-类型索引';
CREATE INDEX idx_syslog_user ON system_log(user_id) COMMENT '系统日志-用户索引';
CREATE INDEX idx_syslog_action ON system_log(action_type) COMMENT '系统日志-操作类型索引';
CREATE INDEX idx_syslog_time ON system_log(log_time) COMMENT '系统日志-操作时间索引';
CREATE INDEX idx_syslog_status ON system_log(status) COMMENT '系统日志-状态索引';

-- 插入默认管理员账号(密码: 123456)
INSERT INTO doctor (id, username, password, real_name, role, status) VALUES 
(1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', 'ADMIN', 1); 