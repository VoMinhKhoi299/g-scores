-- Bang student_scores co ~6 trieu dong, duoc ghi mot lan (seeder) va sau do chi de doc.
-- Index a day giup cac truy van tong hop theo mon (AVG, COUNT) chi doc trong index
-- thay vi quet toan bo bang. Index them o (sbd) khong co ich vi chinh key
-- (sbd, subject_code) da chua cot sbd o dau.
CREATE INDEX idx_student_scores_subject_score
    ON student_scores (subject_code, score);
