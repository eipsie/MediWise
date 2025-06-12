-- 插入诊断记录测试数据
INSERT INTO `diagnosis_record` (`patient_id`, `doctor_id`, `symptoms_text`, `symptoms_structured`, `vital_signs`, `llm_request_data`, `llm_response_data`, `final_diagnosis`, `treatment_plan`, `status`, `diagnosis_time`)
VALUES
(1, 1, '患者主诉头痛三天，伴有发热38.5℃，全身酸痛', 
 '{"头痛":"严重","发热":"38.5℃","全身酸痛":"中度"}', 
 '{"体温":"38.5","血压":"120/80","心率":"85"}', 
 '{"prompt":"分析头痛发热症状"}', 
 '{"response":"根据症状分析，可能为上呼吸道感染"}', 
 '上呼吸道感染', 
 '建议服用布洛芬缓解症状，多喝水，充分休息', 
 'COMPLETED', 
 NOW()),
(2, 1, '患者主诉咳嗽一周，有少量白痰，无发热', 
 '{"咳嗽":"中度","痰":"少量白痰","发热":"无"}', 
 '{"体温":"36.5","血压":"110/70","心率":"75"}', 
 NULL, 
 NULL, 
 NULL, 
 NULL, 
 'DRAFT', 
 NOW()); 