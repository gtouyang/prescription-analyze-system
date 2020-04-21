-- 创建糖尿病检验单表
create table `DMCheck`(
    `id`                int             unsigned not null auto_increment comment '检验单ID',
    `patientId`         int             unsigned not null comment '病人编号',
    `visits`            int             unsigned not null comment '就诊次数',
    `mainDiagnosis`     varchar(100)    default null comment '主要诊断',
    `minorDiagnosis1`   varchar(100)    default null comment '次要诊断1',
    `minorDiagnosis2`   varchar(100)    default null comment '次要诊断2',
    `minorDiagnosis3`   varchar(100)    default null comment '次要诊断3',
    `sex`               varchar(10)     not null comment '性别',
    `age`               tinyint         not null comment '年龄',
    `sample`            varchar(100)    default null comment '标本',
    `groupName`         varchar(100)    default null comment '组合名称',
    `projectName`       varchar(100)    default null comment '项目名称',
    `projectCode`       varchar(100)    default null comment '项目代码',
    `result`            varchar(100)    default null comment '结果值',
    `resultUnit`        varchar(100)    default null comment '结果值单位',
    primary key (`id`),
    key (`patientId`)
)charset = utf8
engine = InnoDB comment '糖尿病检验单';

-- 创建糖尿病用药单
create table `DMDrug`(
    `id`                int             unsigned not null auto_increment comment '用药单ID',
    `patientId`         int             unsigned not null comment '病人编号',
    `visits`            int             unsigned not null comment '就诊次数',
    `mainDiagnosis`     varchar(100)    default null comment '主要诊断',
    `minorDiagnosis1`   varchar(100)    default null comment '次要诊断1',
    `minorDiagnosis2`   varchar(100)    default null comment '次要诊断2',
    `minorDiagnosis3`   varchar(100)    default null comment '次要诊断3',
    `sex`               varchar(10)     not null comment '性别',
    `age`               tinyint         not null comment '年龄',
    `project`           varchar(100)    default null comment '项目',
    `amount`            int             not null comment '数量',
    `unit`              varchar(100)    default null comment '单位',
    `usage`             varchar(100)    not null comment '用法',
    `dosage`            double          not null comment '用量',
    `frequency`         varchar(100)    not null comment '频率',
    primary key (`id`),
    key (`patientId`)
)charset = utf8
engine = InnoDB comment '糖尿病用药单';

-- 创建贫血检验单表
create table `AnemiaCheck`(
    `id`                int             unsigned not null auto_increment comment '检验单ID',
    `patientId`         int             unsigned not null comment '病人编号',
    `visits`            int             unsigned not null comment '就诊次数',
    `mainDiagnosis`     varchar(100)    default null comment '主要诊断',
    `minorDiagnosis1`   varchar(100)    default null comment '次要诊断1',
    `minorDiagnosis2`   varchar(100)    default null comment '次要诊断2',
    `minorDiagnosis3`   varchar(100)    default null comment '次要诊断3',
    `sex`               varchar(10)     not null comment '性别',
    `age`               tinyint         not null comment '年龄',
    `sample`            varchar(100)    default null comment '标本',
    `groupName`         varchar(100)    default null comment '组合名称',
    `projectName`       varchar(100)    default null comment '项目名称',
    `projectCode`       varchar(100)    default null comment '项目代码',
    `result`            varchar(100)    default null comment '结果值',
    `resultUnit`        varchar(100)    default null comment '结果值单位',
    primary key (`id`),
    key (`patientId`)
)charset = utf8
 engine = InnoDB comment '贫血检验单';

-- 创建糖尿病用药单
create table `AnemiaDrug`(
    `id`                int             unsigned not null auto_increment comment '用药单ID',
    `patientId`         int             unsigned not null comment '病人编号',
    `visits`            int             unsigned not null comment '就诊次数',
    `mainDiagnosis`     varchar(100)    default null comment '主要诊断',
    `minorDiagnosis1`   varchar(100)    default null comment '次要诊断1',
    `minorDiagnosis2`   varchar(100)    default null comment '次要诊断2',
    `minorDiagnosis3`   varchar(100)    default null comment '次要诊断3',
    `sex`               varchar(10)     not null comment '性别',
    `age`               tinyint         not null comment '年龄',
    `project`           varchar(100)    default null comment '项目',
    `amount`            int             not null comment '数量',
    `unit`              varchar(100)    default null comment '单位',
    `usage`             varchar(100)    not null comment '用法',
    `dosage`            double          not null comment '用量',
    `frequency`         varchar(100)    not null comment '频率',
    primary key (`id`),
    key (`patientId`)
)charset = utf8
 engine = InnoDB comment '贫血用药单';