package vn.gscores.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Thong ke tong hop theo mon, tinh san luc seed.
 */
@Getter
@Setter
@Entity
@Table(name = "subject_statistics")
public class SubjectStatistic {

    @Id
    @Column(name = "subject_code", length = 20, nullable = false)
    private String subjectCode;

    @Column(name = "average_score", precision = 4, scale = 2, nullable = false)
    private BigDecimal averageScore;

    @Column(name = "student_count", nullable = false)
    private long studentCount;

    @Column(name = "max_score", precision = 4, scale = 2, nullable = false)
    private BigDecimal maxScore;

    @Column(name = "min_score", precision = 4, scale = 2, nullable = false)
    private BigDecimal minScore;
}
