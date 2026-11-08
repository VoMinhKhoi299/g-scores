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
@Table(name="student_scores")
@IdClass(StudentScoreId.class)
public class StudentScore {

    @Id
    @Column(name="sbd", length=8, nullable = false)
    private String sbd;

    @Id
    @Column(name="subject_code", length=20, nullable = false)
    private String subjectCode;

    @Column(name="score", precision = 4, scale = 2, nullable = false)
    private BigDecimal score;
}
