-- 医生表
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

-- 患者表
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

-- 诊断记录表
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

-- 处方表
CREATE TABLE prescription (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    diagnosis_id BIGINT NOT NULL COMMENT '关联诊断ID',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    doctor_id BIGINT NOT NULL COMMENT '开方医生ID',
    medicines JSON COMMENT '药品信息JSON(包含药名、用量、用法等)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '开方时间'
) COMMENT '处方信息表';

-- 医疗文件表
CREATE TABLE medical_file (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    diagnosis_id BIGINT COMMENT '关联诊断ID',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名称',
    file_path VARCHAR(500) NOT NULL COMMENT '文件存储路径',
    file_type VARCHAR(50) COMMENT '文件类型',
    upload_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间'
) COMMENT '医疗文件表';

-- 医学知识库表
CREATE TABLE medical_knowledge (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    disease_name VARCHAR(200) NOT NULL COMMENT '疾病名称',
    disease_code VARCHAR(50) COMMENT '疾病编码',
    symptoms JSON COMMENT '相关症状JSON',
    treatment_guidelines TEXT COMMENT '治疗指南',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '医学知识库';

-- 创建索引提升查询性能
CREATE INDEX idx_patient_name ON patient(name) COMMENT '患者姓名索引';
CREATE INDEX idx_diagnosis_patient ON diagnosis_record(patient_id) COMMENT '诊断-患者关联索引';
CREATE INDEX idx_diagnosis_doctor ON diagnosis_record(doctor_id) COMMENT '诊断-医生关联索引';

-- 插入默认管理员账号(密码: 123456)
INSERT INTO doctor (id, username, password, real_name, status) VALUES 
(1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', 1); 