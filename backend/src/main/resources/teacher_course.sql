-- 创建教师表
CREATE TABLE IF NOT EXISTS `teacher` (
    `teacher_id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '教师编号',
    `teacher_name` VARCHAR(50) NOT NULL COMMENT '教师姓名',
    `teacher_age` INT COMMENT '教师年龄',
    `teacher_description` VARCHAR(500) COMMENT '教师描述'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师表';

-- 创建课程表
CREATE TABLE IF NOT EXISTS `course` (
    `course_id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '课程编号',
    `course_name` VARCHAR(100) NOT NULL COMMENT '课程名称',
    `course_type` VARCHAR(50) COMMENT '课程类型',
    `course_description` VARCHAR(500) COMMENT '课程描述',
    `teacher_id` INT COMMENT '教师编号',
    CONSTRAINT `fk_course_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`teacher_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 插入示例教师数据
INSERT INTO `teacher` (`teacher_name`, `teacher_age`, `teacher_description`) VALUES
('张三', 35, '计算机科学教授，主要研究方向为人工智能和机器学习'),
('李四', 42, '数学系教授，专注于高等数学和线性代数的教学'),
('王五', 38, '物理系副教授，专注于量子力学和相对论的研究');

-- 插入示例课程数据
INSERT INTO `course` (`course_name`, `course_type`, `course_description`, `teacher_id`) VALUES
('Java程序设计', '计算机', 'Java语言基础、面向对象编程、集合框架、多线程等内容', 1),
('Python数据分析', '计算机', 'Python语言基础、数据处理、可视化、机器学习等内容', 1),
('高等数学', '数学', '极限、导数、积分、微分方程等内容', 2),
('线性代数', '数学', '矩阵、向量空间、线性变换等内容', 2),
('量子力学', '物理', '量子理论、波函数、薛定谔方程等内容', 3); 