package vn.gscores.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "score_distribution")
@IdClass(ScoreDistributionId.class)
public class ScoreDistribution {
    @Id
    @Column(name = "subject_code", length = 20, nullable = false)
    private String subjectCode;

    @Id
    @Column(name = "band", length = 20, nullable = false)
    private String band;

    @Column(name = "student_count", nullable = false)
    private long studentCount;

    @Column(name = "percentage", precision = 6, scale = 2, nullable = false)
    private BigDecimal percentage;
}
