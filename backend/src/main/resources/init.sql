-- MediWise 数据库初始化脚本
-- 包含所有表结构和基础数据

-- ====================== 表结构定义 ======================

-- 医生表
CREATE TABLE IF NOT EXISTS doctor (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '登录用户名',
    password VARCHAR(255) NOT NULL COMMENT '登录密码(MD5加密)',
    real_name VARCHAR(100) NOT NULL COMMENT '医生真实姓名',
    role VARCHAR(20) NOT NULL DEFAULT 'DOCTOR' COMMENT '角色(DOCTOR/ADMIN)',
    department VARCHAR(50) COMMENT '科室',
    title VARCHAR(50) COMMENT '职称',
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
CREATE TABLE IF NOT EXISTS patient (
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
CREATE TABLE IF NOT EXISTS diagnosis_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    doctor_id BIGINT NOT NULL COMMENT '医生ID',
    symptoms_text TEXT COMMENT '症状文本',
    symptoms_structured TEXT COMMENT '结构化症状（JSON格式）',
    vital_signs TEXT COMMENT '生命体征（JSON格式）',
    llm_request_data TEXT COMMENT '大模型请求数据（JSON格式）',
    llm_response_data TEXT COMMENT '大模型响应数据（JSON格式）',
    final_diagnosis TEXT COMMENT '最终诊断',
    treatment_plan TEXT COMMENT '治疗方案',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态(DRAFT:草稿,ANALYZING:分析中,PENDING:待确认,COMPLETED:已完成)',
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

-- 血常规表
CREATE TABLE IF NOT EXISTS blood_test (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    test_date DATETIME NOT NULL COMMENT '检测日期',
    wbc DECIMAL(6,2) COMMENT '白细胞计数',
    rbc DECIMAL(6,2) COMMENT '红细胞计数',
    hgb DECIMAL(6,1) COMMENT '血红蛋白',
    plt DECIMAL(6,1) COMMENT '血小板计数',
    neutp DECIMAL(5,1) COMMENT '中性粒细胞百分比',
    lymp DECIMAL(5,1) COMMENT '淋巴细胞百分比',
    ai_analysis_result TEXT COMMENT 'AI分析结果（JSON格式）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (patient_id) REFERENCES patient(id)
) COMMENT '血常规检查表';

-- 创建血常规表的索引
CREATE INDEX idx_blood_test_patient ON blood_test(patient_id) COMMENT '血常规-患者关联索引';
CREATE INDEX idx_blood_test_date ON blood_test(test_date) COMMENT '血常规-检测日期索引';

-- 医学知识库表
CREATE TABLE IF NOT EXISTS medical_knowledge (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    entry_type VARCHAR(20) NOT NULL COMMENT '条目类型(DISEASE/DRUG)',
    title VARCHAR(255) NOT NULL COMMENT '标题',
    disease_name VARCHAR(200) COMMENT '疾病名称',
    disease_code VARCHAR(50) COMMENT '疾病编码',
    drug_name VARCHAR(200) COMMENT '药品名称',
    category VARCHAR(100) COMMENT '分类',
    description TEXT COMMENT '描述',
    symptoms TEXT COMMENT '相关症状（JSON格式）',
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

-- 系统日志表
CREATE TABLE IF NOT EXISTS system_log (
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
    request_url VARCHAR(255) COMMENT '请求URL',
    request_method VARCHAR(20) COMMENT '请求方法',
    operation_result TEXT COMMENT '操作结果摘要',
    execution_time BIGINT COMMENT '执行时间(毫秒)',
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

-- ====================== 基础数据 ======================

-- 插入默认管理员账号(密码: 123456)
INSERT INTO doctor (id, username, password, real_name, role, status) VALUES 
(1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', 'ADMIN', 1);

-- 插入测试医生
INSERT INTO doctor (id, username, password, real_name, role, department, title, email, phone, status) VALUES 
(2, 'doctor', 'e10adc3949ba59abbe56e057f20f883e', '张医生', 'DOCTOR', '内科', '主任医师', 'doctor@example.com', '13800138001', 1);

-- 插入测试患者
INSERT INTO patient (id, patient_no, name, gender, birth_date, id_card, phone, address, allergies, medical_history, creator_id) VALUES
(1, 'P20240601001', '王患者', 1, '1980-01-01', '110101198001010011', '13800138001', '北京市海淀区', '青霉素过敏', '高血压病史5年', 2),
(2, 'P20240601002', '赵患者', 0, '1990-05-15', '110101199005150022', '13800138002', '北京市朝阳区', '无', '无', 2);

-- 插入测试诊断记录
INSERT INTO diagnosis_record (id, patient_id, doctor_id, symptoms_text, symptoms_structured, vital_signs, llm_request_data, llm_response_data, final_diagnosis, treatment_plan, status, diagnosis_time) VALUES
(1, 1, 2, '患者主诉头痛三天，伴有发热38.5℃，全身酸痛', 
   '{"头痛":"严重","发热":"38.5℃","全身酸痛":"中度"}', 
   '{"体温":"38.5","血压":"120/80","心率":"85"}', 
   '{"prompt":"分析头痛发热症状"}', 
   '{"response":"根据症状分析，可能为上呼吸道感染"}', 
   '上呼吸道感染', 
   '建议服用布洛芬缓解症状，多喝水，充分休息', 
   'COMPLETED', 
   NOW()),
(2, 2, 2, '患者主诉咳嗽一周，有少量白痰，无发热', 
   '{"咳嗽":"中度","痰":"少量白痰","发热":"无"}', 
   '{"体温":"36.5","血压":"110/70","心率":"75"}', 
   NULL, 
   NULL, 
   NULL, 
   NULL, 
   'DRAFT', 
   NOW());

-- ====================== 医学知识库数据 ======================

-- 疾病数据
INSERT INTO medical_knowledge (entry_type, title, disease_name, disease_code, category, description, symptoms, treatment_guide, status) VALUES

-- 呼吸系统疾病
('DISEASE', '上呼吸道感染', '上呼吸道感染', 'J06.9', '呼吸系统疾病', '上呼吸道感染是指鼻腔、咽部、喉部的急性炎症的总称，是最常见的感染性疾病。90%以上由病毒感染引起。', '["发热", "咽痛", "咳嗽", "鼻塞", "流涕", "头痛", "乏力"]', '以对症治疗为主：1.休息，多饮水；2.退热药物：对乙酰氨基酚或布洛芬；3.抗病毒药物：奥司他韦（流感病毒感染时）；4.抗生素仅在合并细菌感染时使用。', 1),

('DISEASE', '感冒', '普通感冒', 'J00', '呼吸系统疾病', '普通感冒是由病毒感染引起的上呼吸道急性炎症，以鼻咽部症状为主要表现。', '["鼻塞", "流涕", "喷嚏", "咽痛", "轻度咳嗽", "低热"]', '以对症治疗为主：1.多休息，多饮水；2.鼻塞可用生理盐水冲洗；3.发热可用退热药；4.一般不需要抗生素治疗；5.病程通常1周左右自愈。', 1),

('DISEASE', '慢性支气管炎', '慢性支气管炎', 'J41.0', '呼吸系统疾病', '慢性支气管炎是指气管、支气管黏膜及其周围组织的慢性非特异性炎症。临床以慢性咳嗽、咳痰为主要症状。', '["慢性咳嗽", "咳痰", "喘息", "活动后气短"]', '1.戒烟是最重要的措施；2.支气管扩张剂：沙丁胺醇、异丙托溴铵；3.抗炎治疗：糖皮质激素；4.祛痰药物：乙酰半胱氨酸；5.抗感染：急性加重期使用抗生素。', 1),

('DISEASE', '肺炎', '社区获得性肺炎', 'J18.9', '呼吸系统疾病', '肺炎是指终末气道、肺泡和肺间质的炎症。可由细菌、病毒、真菌等多种病原体引起。', '["发热", "咳嗽", "咳痰", "胸痛", "呼吸困难", "乏力"]', '1.抗感染治疗：根据病原菌选择抗生素；2.对症治疗：退热、止咳、化痰；3.支持治疗：氧疗、营养支持；4.重症患者需住院治疗。', 1),

('DISEASE', '哮喘', '支气管哮喘', 'J45.9', '呼吸系统疾病', '哮喘是由多种细胞和细胞组分参与的气道慢性炎症性疾病，以可逆性气流受限为特征。', '["反复喘息", "胸闷", "咳嗽", "呼吸困难", "夜间或清晨症状加重"]', '1.长期控制：吸入糖皮质激素；2.急性缓解：沙丁胺醇气雾剂；3.避免诱发因素：过敏原、冷空气等；4.监测肺功能；5.制定个体化治疗方案。', 1),

-- 心血管系统疾病
('DISEASE', '高血压', '原发性高血压', 'I10', '心血管系统疾病', '高血压是指在未使用抗高血压药物的情况下，收缩压≥140mmHg和/或舒张压≥90mmHg。高血压是心脑血管疾病的主要危险因素。', '["头痛", "头晕", "心悸", "疲劳", "视物模糊"]', '1.生活方式干预：限盐、减重、戒烟限酒、适量运动；2.药物治疗：ACEI/ARB、钙通道阻滞剂、利尿剂、β受体阻滞剂；3.目标血压：一般患者<140/90mmHg。', 1),

('DISEASE', '冠心病', '冠状动脉粥样硬化性心脏病', 'I25.9', '心血管系统疾病', '冠心病是指冠状动脉粥样硬化使血管腔狭窄或阻塞，导致心肌缺血缺氧而引起的心脏病。', '["胸痛", "胸闷", "气短", "心悸", "乏力", "劳累后症状加重"]', '1.生活方式改善：戒烟、控制血脂血压血糖；2.抗血小板聚集：阿司匹林；3.他汀类药物降脂；4.硝酸酯类扩冠；5.严重狭窄需介入或手术治疗。', 1),

('DISEASE', '心律失常', '房性期前收缩', 'I49.1', '心血管系统疾病', '房性期前收缩是最常见的心律失常，起源于心房的异位激动。多数为良性，少数可能提示器质性心脏病。', '["心悸", "胸闷", "心脏咯噔感", "部分患者无症状"]', '1.去除诱因：避免咖啡因、酒精、情绪激动；2.频发且有症状者可用β受体阻滞剂；3.定期监测心电图；4.评估有无器质性心脏病。', 1),

('DISEASE', '心力衰竭', '慢性心力衰竭', 'I50.9', '心血管系统疾病', '心力衰竭是各种心脏结构或功能性疾病导致心室充盈或射血能力受损的综合征。', '["呼吸困难", "疲劳乏力", "液体潴留", "活动耐量下降", "夜间阵发性呼吸困难"]', '1.ACEI/ARB+β受体阻滞剂+利尿剂三联治疗；2.限制钠盐和水分摄入；3.适度运动训练；4.监测体重变化；5.必要时植入心脏起搏器。', 1),

-- 消化系统疾病
('DISEASE', '急性胃炎', '急性胃炎', 'K29.1', '消化系统疾病', '急性胃炎是由多种病因引起的胃黏膜急性炎症。常见病因包括药物、酒精、感染等。', '["上腹痛", "恶心", "呕吐", "腹胀", "食欲不振"]', '1.去除病因：停用刺激性药物、禁酒；2.对症治疗：质子泵抑制剂、H2受体拮抗剂；3.保护胃黏膜：硫糖铝、胶体果胶铋；4.饮食调理：清淡易消化食物。', 1),

('DISEASE', '慢性胃炎', '慢性胃炎', 'K29.5', '消化系统疾病', '慢性胃炎是由各种病因引起的胃黏膜慢性炎症。幽门螺杆菌感染是主要病因。', '["上腹不适", "饱胀感", "食欲减退", "嗳气", "反酸", "恶心"]', '1.根除幽门螺杆菌：三联或四联疗法；2.抑制胃酸：质子泵抑制剂；3.保护胃黏膜；4.改善胃动力；5.定期内镜随访。', 1),

('DISEASE', '消化性溃疡', '胃溃疡', 'K25.9', '消化系统疾病', '消化性溃疡是指胃或十二指肠黏膜的缺失，深达黏膜肌层或更深。主要病因为幽门螺杆菌感染和NSAIDs。', '["上腹痛", "饥饿痛或餐后痛", "反酸", "嗳气", "恶心", "黑便"]', '1.根除幽门螺杆菌；2.质子泵抑制剂抑酸治疗4-8周；3.停用NSAIDs；4.并发症如出血、穿孔需紧急处理；5.愈合后维持治疗。', 1);

-- 药品数据
INSERT INTO medical_knowledge (entry_type, title, drug_name, category, description, symptoms, treatment_guide, status) VALUES

-- 抗生素类
('DRUG', '阿莫西林胶囊', '阿莫西林', '抗生素类', '阿莫西林是青霉素类抗生素，具有广谱抗菌作用，主要用于敏感菌引起的各种感染。', '["细菌感染", "发热", "炎症"]', '口服给药，成人常用量：每次0.5g，每8小时1次。儿童按体重20-40mg/kg/日，分3次服用。饭前或饭后服用均可。青霉素过敏者禁用。', 1),

('DRUG', '头孢拉定胶囊', '头孢拉定', '抗生素类', '头孢拉定是第一代头孢菌素类抗生素，主要用于敏感细菌引起的各种感染。', '["呼吸道感染", "泌尿道感染", "皮肤软组织感染"]', '口服给药，成人常用量：每次0.25-0.5g，每6小时1次。儿童按体重25-50mg/kg/日，分4次服用。餐前1小时或餐后2小时服用。', 1),

('DRUG', '阿奇霉素片', '阿奇霉素', '抗生素类', '阿奇霉素是大环内酯类抗生素，具有抗菌谱广、半衰期长的特点。', '["呼吸道感染", "皮肤感染", "泌尿生殖道感染"]', '口服给药，成人常用量：首次0.5g，以后每次0.25g，每日1次，连用3天。或每次0.5g，每日1次，连用3天。空腹服用。', 1),

-- 解热镇痛药
('DRUG', '对乙酰氨基酚片', '对乙酰氨基酚', '解热镇痛药', '对乙酰氨基酚是常用的解热镇痛药，用于缓解轻至中度疼痛和发热。', '["发热", "头痛", "肌肉疼痛", "关节痛"]', '口服给药，成人常用量：每次0.5-1g，每4-6小时1次，24小时内不超过4g。儿童按体重10-15mg/kg/次。肝功能不全者慎用。', 1),

('DRUG', '布洛芬缓释胶囊', '布洛芬', '非甾体抗炎药', '布洛芬是丙酸类非甾体抗炎药，具有解热、镇痛、抗炎作用。', '["发热", "头痛", "关节痛", "肌肉痛", "痛经"]', '口服给药，成人常用量：每次0.3-0.6g，每日2-3次，餐后服用以减少胃肠道刺激。儿童按体重20-30mg/kg/日，分3-4次服用。', 1),

-- 消化系统药物
('DRUG', '奥美拉唑肠溶胶囊', '奥美拉唑', '质子泵抑制剂', '奥美拉唑是质子泵抑制剂，能强效抑制胃酸分泌，用于治疗胃食管反流病、消化性溃疡等。', '["胃痛", "胃酸过多", "烧心", "反酸"]', '口服给药，成人常用量：每次20mg，每日1-2次，餐前30分钟服用。治疗消化性溃疡疗程4-8周。整粒吞服，不得嚼碎。', 1),

('DRUG', '蒙脱石散', '蒙脱石', '肠道吸附剂', '蒙脱石散具有层纹状结构和非均匀性电荷分布，对消化道内的病毒、病菌及其产生的毒素有固定、抑制作用。', '["急性腹泻", "慢性腹泻", "食管胃十二指肠疾病相关性疼痛"]', '口服给药，成人每次3g，每日3次。儿童<1岁每日3g，1-2岁每日6g，>2岁每日9g。餐前或餐后服用均可。', 1);

-- 更新创建时间（模拟不同时间创建的数据）
UPDATE medical_knowledge SET
    create_time = DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY),
    update_time = DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY); 