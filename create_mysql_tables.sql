create table zzz_gk_score_sp (
    year int,
    school_id int,
    name varchar(20) comment "school",
    special_id int,
    spname varchar(500),
    local_province_name varchar(20),
    local_batch_name varchar(20),
    local_type_name varchar(20) comment "wen li",
    dual_class_name varchar(20) comment "???",
    zslx_name varchar(20) comment "???",
    min varchar(20),
    max varchar(20),
    average varchar(20),
    min_section varchar(20),
    proscore varchar(20)
) character set=utf8
;

create table zzz_gk_score_rank (
    year int,
    local_province_name varchar(20),
    local_type_name varchar(20) comment "wen li",
    score varchar(20),
    prov_rank int
) character set=utf8
;



create table zzz_gk_sch (
    school_id int,
    name varchar(20) comment "school",
    rank int,
    rank_type int,
    type_name varchar(20) comment "zonghelei",
    view_total int,
    view_month int,
    view_week int,
    level_name varchar(20) comment "putongbegke",
    provice_name varchar(20),
    city_name varchar(20),
    f211 int comment "yes - 1",
    f985 int comment "yes - 1",
    is_top int,
    dual_class int comment "38001一流大学建设，38000一流学科建设，",
    central int comment "中央部委直属1",
    department int comment "教育部直属1",
    admissions int comment "自主招生试点1"
) character set=utf8
;



create table zzz_gk_score_sch (
    year int,
    school_id int,
    name varchar(20) comment "school",
    local_province_name varchar(20),
    local_batch_name varchar(20),
    local_type_name varchar(20) comment "wen li",
    min varchar(20),
    max varchar(20),
    average varchar(20),
    filing varchar(20),
    proscore varchar(20)
) character set=utf8
;
0101||01||01||哲学||6||10212||黑龙江大学


create table zzz_gk_sch_xueke (
    code0 varchar(20),
    code1 varchar(20),
    code2 varchar(20),
    xk_name varchar(20),
    xk_rank int,
    sch_dep_id varchar(20),
    sch_name varchar(20)
) character set=utf8
;

