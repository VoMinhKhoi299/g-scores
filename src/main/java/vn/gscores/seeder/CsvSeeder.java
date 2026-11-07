package vn.gscores.seeder;

import org.postgresql.core.BaseConnection;
import org.postgresql.copy.CopyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import vn.gscores.domain.ScoreBand;
import vn.gscores.domain.Subject;
import vn.gscores.domain.SubjectGroup;

/**
 * Nap du lieu tu file CSV vao database.
 *
 * Chay mot lan khi app start. Neu da nap roi (co dong trong dataset_metadata)
 * thi bo qua, de app chay nhanh lan sau. Toan bo duoc bao trong mot transaction:
 * co loi o giua thi rollback, khong de lai du lieu ban trong.
 */
@Component
public class CsvSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CsvSeeder.class);

    private final JdbcTemplate jdbcTemplate;

    @Value("${app.dataset.path}")
    private String datasetPath;

    public CsvSeeder(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (isAlreadySeeded()) {
            log.info("Dataset da duoc nap, bo qua seeding.");
            return;
        }

        long start = System.currentTimeMillis();
        log.info("Bat dau nap dataset tu {}", datasetPath);

        copyCsvIntoStaging();
        log.info("Da copy CSV vao bang staging");

        seedSubjectsAndGroups();
        log.info("Da nap subjects, subject_groups, group_subjects");

        seedStudents();
        log.info("Da nap bang students");

        seedStudentScores();
        log.info("Da nap bang student_scores");

        seedScoreDistribution();
        log.info("Da tinh bang score_distribution");

        seedGroupTop();
        log.info("Da tinh bang group_top_students");

        seedDatasetMetadata();
        log.info("Da nap dataset_metadata");

        log.info("Nap dataset hoan tat sau {} giay",
                (System.currentTimeMillis() - start) / 1000);
    }

    // ------------------------------------------------------------------
    // COPY CSV truc tiep (nhanh nhat)
    // ------------------------------------------------------------------

    private void copyCsvIntoStaging() {
        Path csvFile = Paths.get(datasetPath);
        if (!Files.isRegularFile(csvFile)) {
            throw new UncheckedIOException("Khong tim thay file dataset: " + datasetPath,
                    new IOException("File khong ton tai: " + csvFile.toAbsolutePath()));
        }

        try (InputStream input = Files.newInputStream(csvFile)) {
            jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
                CopyManager copyManager =
                        new CopyManager(connection.unwrap(BaseConnection.class));
                copyManager.copyIn(
                        "COPY staging FROM STDIN WITH (FORMAT csv, HEADER true)",
                        input);
                return null;
            });
        } catch (IOException e) {
            throw new UncheckedIOException("Khong doc duoc file dataset", e);
        }
    }

    // ------------------------------------------------------------------
    // Tach staging ra students va student_scores
    // ------------------------------------------------------------------

    private void seedSubjectsAndGroups() {
        for (Subject subject : Subject.values()) {
            jdbcTemplate.update(
                    "INSERT INTO subjects (code, name) VALUES (?, ?)",
                    subject.getCode(), subject.getName());
        }

        for (SubjectGroup group : SubjectGroup.standard()) {
            jdbcTemplate.update(
                    "INSERT INTO subject_groups (group_code, name) VALUES (?, ?)",
                    group.code(), group.name());

            for (Subject subject : group.subjects()) {
                jdbcTemplate.update(
                        "INSERT INTO group_subjects (group_code, subject_code) VALUES (?, ?)",
                        group.code(), subject.getCode());
            }
        }
    }

    // Trong CSV, o trong nghia la hoc sinh khong thi mon do.
    // Ma ngoai ngu rong duoc chuyen thanh NULL de khac voi "diem 0".
    private void seedStudents() {
        jdbcTemplate.update(
                "INSERT INTO students (sbd, ma_ngoai_ngu) "
                + "SELECT sbd, NULLIF(ma_ngoai_ngu, '') FROM staging");
    }

    // Moi cot mon trong staging cho ra mot dong student_scores.
    // Dong nao gia tri rong thi bo qua (hoc sinh khong thi mon do).
    private void seedStudentScores() {
        jdbcTemplate.update(
                "INSERT INTO student_scores (sbd, subject_code, score) "
                + "SELECT sbd, 'toan', toan::numeric FROM staging WHERE toan <> '' "
                + "UNION ALL SELECT sbd, 'ngu_van', ngu_van::numeric FROM staging WHERE ngu_van <> '' "
                + "UNION ALL SELECT sbd, 'ngoai_ngu', ngoai_ngu::numeric FROM staging WHERE ngoai_ngu <> '' "
                + "UNION ALL SELECT sbd, 'vat_li', vat_li::numeric FROM staging WHERE vat_li <> '' "
                + "UNION ALL SELECT sbd, 'hoa_hoc', hoa_hoc::numeric FROM staging WHERE hoa_hoc <> '' "
                + "UNION ALL SELECT sbd, 'sinh_hoc', sinh_hoc::numeric FROM staging WHERE sinh_hoc <> '' "
                + "UNION ALL SELECT sbd, 'lich_su', lich_su::numeric FROM staging WHERE lich_su <> '' "
                + "UNION ALL SELECT sbd, 'dia_li', dia_li::numeric FROM staging WHERE dia_li <> '' "
                + "UNION ALL SELECT sbd, 'gdcd', gdcd::numeric FROM staging WHERE gdcd <> ''");
    }

    // ------------------------------------------------------------------
    // Tinh bang pre-compute (phan bo 4 muc, top 10)
    // ------------------------------------------------------------------

    private void seedScoreDistribution() {
        String bandCase = bandCaseExpression();

        for (Subject subject : Subject.values()) {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT " + bandCase + " AS band, COUNT(*) AS cnt "
                    + "FROM student_scores WHERE subject_code = ? GROUP BY 1",
                    subject.getCode());

            // Map band -> so luong, va tong so luong de tinh tiem le.
            Map<String, Long> counts = new HashMap<>();
            long total = 0;
            for (Map<String, Object> row : rows) {
                String band = (String) row.get("band");
                long cnt = ((Number) row.get("cnt")).longValue();
                counts.put(band, cnt);
                total += cnt;
            }

            for (ScoreBand band : ScoreBand.values()) {
                long cnt = counts.getOrDefault(band.name(), 0L);
                BigDecimal percentage = total == 0
                        ? BigDecimal.ZERO
                        : BigDecimal.valueOf(cnt)
                                .multiply(BigDecimal.valueOf(100))
                                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);

                jdbcTemplate.update(
                        "INSERT INTO score_distribution "
                        + "(subject_code, band, student_count, percentage) VALUES (?, ?, ?, ?)",
                        subject.getCode(), band.name(), cnt, percentage);
            }
        }
    }

    // ScoreBand dat theo thu tu minScore giam dan (8, 6, 4, 0).
    // CASE xet tu tren xuong, tra band dau tien thoa man.
    // POOR la muc duoi cung nen khong can dieu kien rieng.
    private String bandCaseExpression() {
        StringBuilder sb = new StringBuilder("CASE");
        for (ScoreBand band : ScoreBand.values()) {
            if (band == ScoreBand.POOR) {
                continue;
            }
            sb.append(" WHEN score >= ")
                    .append(band.getMinScore())
                    .append(" THEN '").append(band.name()).append("'");
        }
        sb.append(" ELSE '").append(ScoreBand.POOR).append("' END");
        return sb.toString();
    }

    private void seedGroupTop() {
        for (SubjectGroup group : SubjectGroup.standard()) {
            seedGroupTopFor(group);
        }
    }

    private void seedGroupTopFor(SubjectGroup group) {
        List<Subject> subjects = group.subjects();

        String inList = subjects.stream()
                .map(s -> "'" + s.getCode() + "'")
                .collect(Collectors.joining(", "));
        String pivotColumns = subjects.stream()
                .map(s -> "MAX(CASE WHEN subject_code = '" + s.getCode()
                        + "' THEN score END) AS " + s.getCode())
                .collect(Collectors.joining(", "));
        String totalExpression = subjects.stream()
                .map(s -> s.getCode())
                .collect(Collectors.joining(" + "));
        String tieBreakOrder = subjects.stream()
                .map(s -> s.getCode() + " DESC")
                .collect(Collectors.joining(", "));

        // Lay nhung hoc sinh thi du ca bo mon cua nhom (HAVING COUNT = so mon).
        // Xep theo tong giam, khi bang thi gan tie-break theo diem tung mon,
        // cuoi cung la sbd tang de co thu tu duy nhat.
        String sql = "WITH score_per_student AS ("
                + "  SELECT sbd, " + pivotColumns + " "
                + "  FROM student_scores "
                + "  WHERE subject_code IN (" + inList + ") "
                + "  GROUP BY sbd "
                + "  HAVING COUNT(*) = " + subjects.size() + " "
                + ") "
                + "SELECT sbd, " + totalExpression + " AS total "
                + "FROM score_per_student "
                + "ORDER BY total DESC, " + tieBreakOrder + ", sbd ASC "
                + "LIMIT 10";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        BigDecimal subjectCount = BigDecimal.valueOf(subjects.size());
        int rank = 1;
        for (Map<String, Object> row : rows) {
            String sbd = (String) row.get("sbd");
            BigDecimal total = (BigDecimal) row.get("total");
            BigDecimal average = total.divide(subjectCount, 2, RoundingMode.HALF_UP);

            jdbcTemplate.update(
                    "INSERT INTO group_top_students "
                    + "(group_code, rank, sbd, total, avg_score) VALUES (?, ?, ?, ?, ?)",
                    group.code(), rank, sbd, total, average);
            rank++;
        }
    }

    // ------------------------------------------------------------------
    // Thong tin nguon va kiem tra idempotent
    // ------------------------------------------------------------------

    private void seedDatasetMetadata() {
        Long totalRows = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM students", Long.class);
        String sourceFile = datasetPath.substring(datasetPath.lastIndexOf('/') + 1);

        jdbcTemplate.update(
                "INSERT INTO dataset_metadata (id, source_file, total_rows, seeded_at) "
                + "VALUES (1, ?, ?, NOW())",
                sourceFile, totalRows);
    }

    private boolean isAlreadySeeded() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM dataset_metadata", Integer.class);
        return count != null && count > 0;
    }
}
