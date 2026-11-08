CREATE TABLE subjects (
    code    VARCHAR(20) PRIMARY KEY,
    name    VARCHAR(50) NOT NULL
);

CREATE TABLE subject_groups (
    group_code  VARCHAR(5)  PRIMARY KEY,
    name    VARCHAR(50) NOT NULL
);

-- Mot mon co the thuoc nhieu nhom -> moi quan he giua nhom va mon la nhieu - nhieu
CREATE TABLE group_subjects (
    group_code  VARCHAR(5)  NOT NULL REFERENCES subject_groups (group_code),
    subject_code    VARCHAR(20) NOT NULL REFERENCES subjects (code),
    PRIMARY KEY (group_code, subject_code)
);

CREATE TABLE students (
    sbd VARCHAR(8) PRIMARY KEY,
    ma_ngoai_ngu    VARCHAR(5) CHECK (ma_ngoai_ngu IN ('N1','N2','N3','N4','N5','N6','N7')),
    CONSTRAINT  chk_student_sbd CHECK (sbd ~ '^[0-9]{8}$')
);

-- Moi dong la mot mon hoc sinh da thi, khong co dong nao nghia la khong thi mon do
CREATE TABLE student_scores (
    sbd VARCHAR(8)  NOT NULL REFERENCES students (sbd) ON DELETE CASCADE,
    subject_code    VARCHAR(20) NOT NULL REFERENCES subjects (code),
    score   NUMERIC(4,2) NOT NULL CHECK (score BETWEEN 0 AND 10),
    PRIMARY KEY (sbd, subject_code)
);

CREATE TABLE score_distribution (
    subject_code    VARCHAR(20) NOT NULL REFERENCES subjects (code),
    band    VARCHAR(20) NOT NULL,
    student_count BIGINT    NOT NULL CHECK (student_count >= 0),
    percentage  NUMERIC(6,2) NOT NULL CHECK (percentage BETWEEN 0 AND 100),
    PRIMARY KEY (subject_code, band),
    CONSTRAINT chk_band CHECK (band IN ('VERY_GOOD', 'GOOD', 'FAIR', 'POOR'))
);


CREATE TABLE group_top_students (
    group_code  VARCHAR(5)  NOT NULL REFERENCES subject_groups (group_code),
    rank    INTEGER NOT NULL,
    sbd VARCHAR(8)   NOT NULL REFERENCES students (sbd) ON DELETE CASCADE,
    total   NUMERIC(5,2) NOT NULL,
    avg_score   NUMERIC(4,2) NOT NULL,
    PRIMARY KEY (group_code, rank),
    CONSTRAINT uq_group_top_sbd UNIQUE (group_code, sbd)
);

CREATE TABLE dataset_metadata (
    id  SMALLINT    PRIMARY KEY DEFAULT 1,
    source_file VARCHAR(255) NOT NULL,
    total_rows  BIGINT  NOT NULL,
    seeded_at   TIMESTAMPTZ  NOT NULL,
    CONSTRAINT chk_metadata_single CHECK (id = 1)
);
