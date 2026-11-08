-- Thong ke tong hop theo mon. Tinh san luc seed de API khong phai quet
-- 1 trieu dong student_scores moi request (diem trung binh, so thu sinh).
CREATE TABLE subject_statistics (
    subject_code    VARCHAR(20) PRIMARY KEY REFERENCES subjects (code),
    average_score   NUMERIC(4,2) NOT NULL,
    student_count   BIGINT      NOT NULL CHECK (student_count >= 0),
    max_score       NUMERIC(4,2) NOT NULL,
    min_score       NUMERIC(4,2) NOT NULL
);
