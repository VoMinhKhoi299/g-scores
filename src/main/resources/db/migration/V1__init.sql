-- V1__init.sql — G-Scores: schema gốc
-- Dữ liệu điểm thi THPT Quốc gia 2024 (1.061.605 thí sinh)

CREATE TABLE subjects (
    code        VARCHAR(20) PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL,
    group_code  VARCHAR(5)   NOT NULL,
    CONSTRAINT chk_subject_group CHECK (group_code IN ('A', 'B', 'C'))
);

CREATE TABLE students (
    sbd           CHAR(8) PRIMARY KEY,
    toan          NUMERIC(4,2),
    ngu_van       NUMERIC(4,2),
    ngoai_ngu     NUMERIC(4,2),
    vat_li        NUMERIC(4,2),
    hoa_hoc       NUMERIC(4,2),
    sinh_hoc      NUMERIC(4,2),
    lich_su       NUMERIC(4,2),
    dia_li        NUMERIC(4,2),
    gdcd          NUMERIC(4,2),
    ma_ngoai_ngu  VARCHAR(5)
);

CREATE INDEX idx_students_toan ON students (toan);
CREATE INDEX idx_students_vat_li ON students (vat_li);
CREATE INDEX idx_students_hoa_hoc ON students (hoa_hoc);

CREATE TABLE score_distribution (
    subject_code  VARCHAR(20) NOT NULL REFERENCES subjects (code),
    band          VARCHAR(20) NOT NULL,
    student_count BIGINT      NOT NULL,
    percentage    NUMERIC(6,2) NOT NULL,
    PRIMARY KEY (subject_code, band)
);

CREATE TABLE group_a_top (
    rank       INTEGER PRIMARY KEY,
    sbd        CHAR(8)      NOT NULL REFERENCES students (sbd),
    toan       NUMERIC(4,2) NOT NULL,
    vat_li     NUMERIC(4,2) NOT NULL,
    hoa_hoc    NUMERIC(4,2) NOT NULL,
    total      NUMERIC(5,2) NOT NULL,
    avg_score  NUMERIC(4,2) NOT NULL
);

CREATE INDEX idx_group_a_top_total ON group_a_top (total DESC);
