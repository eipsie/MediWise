-- 创建诊断记录表
CREATE TABLE IF NOT EXISTS `diagnosis_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `patient_id` bigint(20) NOT NULL COMMENT '患者ID',
  `doctor_id` bigint(20) NOT NULL COMMENT '医生ID',
  `symptoms_text` text NOT NULL COMMENT '症状文本',
  `symptoms_structured` text COMMENT '结构化症状（JSON格式）',
  `vital_signs` text COMMENT '生命体征（JSON格式）',
  `llm_request_data` text COMMENT '大模型请求数据',
  `llm_response_data` text COMMENT '大模型响应数据',
  `final_diagnosis` varchar(255) DEFAULT NULL COMMENT '最终诊断',
  `treatment_plan` text COMMENT '治疗方案',
  `status` varchar(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态(DRAFT:草稿,ANALYZING:分析中,PENDING:待确认,COMPLETED:已完成)',
  `diagnosis_time` datetime DEFAULT NULL COMMENT '诊断时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_doctor_id` (`doctor_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='诊断记录表';

-- 创建患者表（如果不存在）
CREATE TABLE IF NOT EXISTS `patient` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `creator_id` bigint(20) NOT NULL COMMENT '创建者ID（医生ID）',
  `name` varchar(50) NOT NULL COMMENT '患者姓名',
  `gender` varchar(10) NOT NULL COMMENT '患者性别',
  `birth_date` date DEFAULT NULL COMMENT '出生日期',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `medical_history` text COMMENT '病史',
  `allergy_history` text COMMENT '过敏史',
  `family_history` text COMMENT '家族病史',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_creator_id` (`creator_id`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者信息表';

-- 创建医生表（如果不存在）
CREATE TABLE IF NOT EXISTS `doctor` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID，关联user表',
  `name` varchar(50) NOT NULL COMMENT '医生姓名',
  `title` varchar(50) DEFAULT NULL COMMENT '医生职称',
  `specialty` varchar(100) DEFAULT NULL COMMENT '医生专业',
  `hospital` varchar(100) DEFAULT NULL COMMENT '医院/机构',
  `introduction` text COMMENT '医生简介',
  `license_number` varchar(50) DEFAULT NULL COMMENT '医生资质证书号',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生信息表';

-- 插入测试数据
INSERT INTO `doctor` (`id`, `user_id`, `name`, `title`, `specialty`, `hospital`, `introduction`, `license_number`, `create_time`, `update_time`)
VALUES
(1, 1, '张医生', '主任医师', '内科', '人民医院', '从事内科临床工作20年，擅长各种常见病、多发病的诊断和治疗。', '1234567890', NOW(), NOW()),
(2, 2, '李医生', '副主任医师', '外科', '协和医院', '从事外科临床工作15年，擅长普外科手术。', '0987654321', NOW(), NOW());

INSERT INTO `patient` (`id`, `creator_id`, `name`, `gender`, `birth_date`, `id_card`, `phone`, `address`, `medical_history`, `allergy_history`, `family_history`, `create_time`, `update_time`)
VALUES
(1, 1, '王患者', '男', '1980-01-01', '110101198001010011', '13800138001', '北京市海淀区', '高血压病史5年', '青霉素过敏', '父亲有高血压', NOW(), NOW()),
(2, 1, '赵患者', '女', '1990-05-15', '110101199005150022', '13800138002', '北京市朝阳区', '无', '无', '无', NOW(), NOW());

INSERT INTO `diagnosis_record` (`id`, `patient_id`, `doctor_id`, `symptoms_text`, `symptoms_structured`, `vital_signs`, `llm_request_data`, `llm_response_data`, `final_diagnosis`, `treatment_plan`, `status`, `diagnosis_time`, `create_time`, `update_time`)
VALUES
(1, 1, 1, '患者主诉头痛三天，伴有发热38.5℃，全身酸痛', '{\"头痛\":\"严重\",\"发热\":\"38.5℃\",\"全身酸痛\":\"中度\"}', '{\"体温\":\"38.5\",\"血压\":\"120/80\",\"心率\":\"85\"}', '分析头痛发热症状', '根据症状分析，可能为上呼吸道感染', '上呼吸道感染', '建议服用布洛芬缓解症状，多喝水，充分休息', 'COMPLETED', NOW(), NOW(), NOW()),
(2, 2, 1, '患者主诉咳嗽一周，有少量白痰，无发热', '{\"咳嗽\":\"中度\",\"痰\":\"少量白痰\",\"发热\":\"无\"}', '{\"体温\":\"36.5\",\"血压\":\"110/70\",\"心率\":\"75\"}', NULL, NULL, NULL, NULL, 'DRAFT', NULL, NOW(), NOW()); 