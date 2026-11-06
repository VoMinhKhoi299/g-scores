-- G-Scores: schema cho dữ liệu kết quả thi THPT Quốc gia 2024
-- Nguồn: dataset/diem_thi_thpt_2024.csv — 1.061.605 thí sinh, seeder nạp bằng PostgreSQL COPY

CREATE TABLE subjects (
    code        VARCHAR(20) PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL,
    group_code  VARCHAR(5)   NOT NULL,
    CONSTRAINT chk_subject_group CHECK (group_code IN ('A', 'B', 'C'))
);

CREATE TABLE students (
    sbd           CHAR(8)      PRIMARY KEY,
    toan          NUMERIC(4,2) CHECK (toan BETWEEN 0 AND 10),
    ngu_van       NUMERIC(4,2) CHECK (ngu_van BETWEEN 0 AND 10),
    ngoai_ngu     NUMERIC(4,2) CHECK (ngoai_ngu BETWEEN 0 AND 10),
    vat_li        NUMERIC(4,2) CHECK (vat_li BETWEEN 0 AND 10),
    hoa_hoc       NUMERIC(4,2) CHECK (hoa_hoc BETWEEN 0 AND 10),
    sinh_hoc      NUMERIC(4,2) CHECK (sinh_hoc BETWEEN 0 AND 10),
    lich_su       NUMERIC(4,2) CHECK (lich_su BETWEEN 0 AND 10),
    dia_li        NUMERIC(4,2) CHECK (dia_li BETWEEN 0 AND 10),
    gdcd          NUMERIC(4,2) CHECK (gdcd BETWEEN 0 AND 10),
    ma_ngoai_ngu  VARCHAR(5)   CHECK (ma_ngoai_ngu IN ('N1','N2','N3','N4','N5','N6','N7')),
    CONSTRAINT chk_student_sbd CHECK (sbd ~ '^[0-9]{8}$')
);

-- Điểm NULL nghĩa là học sinh không thi môn đó, không phải 0 điểm,
-- nên các cột điểm không có DEFAULT 0.
-- NUMERIC thay FLOAT: điểm có bước 0.25, cần so sánh chính xác tuyệt đối.

CREATE TABLE score_distribution (
    subject_code  VARCHAR(20) NOT NULL REFERENCES subjects (code),
    band          VARCHAR(20) NOT NULL,
    student_count BIGINT      NOT NULL CHECK (student_count >= 0),
    percentage    NUMERIC(6,2) NOT NULL CHECK (percentage BETWEEN 0 AND 100),
    PRIMARY KEY (subject_code, band),
    CONSTRAINT chk_band CHECK (band IN ('VERY_GOOD', 'GOOD', 'FAIR', 'POOR'))
);

-- Thứ tự 4 mức do enum ScoreBand quyết định, không lưu trong DB.
-- Tổng student_count của một môn luôn bằng số thí sinh đã thi môn đó.

CREATE TABLE group_a_top (
    rank         INTEGER PRIMARY KEY,
    sbd          CHAR(8)      NOT NULL REFERENCES students (sbd) ON DELETE CASCADE,
    toan         NUMERIC(4,2) NOT NULL,
    vat_li       NUMERIC(4,2) NOT NULL,
    hoa_hoc      NUMERIC(4,2) NOT NULL,
    total        NUMERIC(5,2) NOT NULL,
    avg_score    NUMERIC(4,2) NOT NULL,
    toan_rank    INTEGER      NOT NULL,
    vat_li_rank  INTEGER      NOT NULL,
    hoa_hoc_rank INTEGER      NOT NULL,
    CONSTRAINT chk_gat_total CHECK (total = toan + vat_li + hoa_hoc),
    CONSTRAINT chk_gat_avg   CHECK (avg_score = ROUND(total / 3, 2))
);

-- Xếp hạng nhóm A theo thứ tự: total giảm dần, tie-break toan, vat_li,
-- hoa_hoc giảm dần rồi sbd tăng dần. Thứ hạng từng môn tính trên tập
-- 343.800 thí sinh đủ cả 3 môn.
-- ON DELETE CASCADE: xoá thí sinh thì dòng top tương ứng cũng bị xoá.

CREATE TABLE dataset_metadata (
    id           SMALLINT     PRIMARY KEY DEFAULT 1,
    source_file  VARCHAR(255) NOT NULL,
    total_rows   BIGINT       NOT NULL,
    seeded_at    TIMESTAMPTZ  NOT NULL,
    CONSTRAINT chk_metadata_single CHECK (id = 1)
);

-- Bảng này luôn chỉ có đúng 1 dòng, dùng để biết dataset đã nạp chưa
-- và lấy số liệu tổng quan mà không hardcode trong code.
