package vn.gscores.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import vn.gscores.domain.ForeignLanguage;
import vn.gscores.domain.Subject;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "students")
public class Student {

    @Id
    @Column(name = "sbd", length = 8, nullable = false)
    private String sbd;

    @Column(name = "toan")
    private BigDecimal math;

    @Column(name = "ngu_van")
    private BigDecimal literature;

    @Column(name = "ngoai_ngu")
    private BigDecimal foreignLanguage;

    @Column(name = "vat_li")
    private BigDecimal physics;

    @Column(name = "hoa_hoc")
    private BigDecimal chemistry;

    @Column(name = "sinh_hoc")
    private BigDecimal biology;

    @Column(name = "lich_su")
    private BigDecimal history;

    @Column(name = "dia_li")
    private BigDecimal geography;

    @Column(name = "gdcd")
    private BigDecimal civicEducation;

    // Null means the student did not take a foreign language exam,
    // which is different from having a score of zero.
    @Enumerated(EnumType.STRING)
    @Column(name = "ma_ngoai_ngu", length = 5)
    private ForeignLanguage foreignLanguageCode;

    /**
     * Returns the score of the given subject, or null when the student
     * did not take that subject.
     */
    public BigDecimal getScore(Subject subject) {
        return switch (subject) {
            case MATH -> math;
            case LITERATURE -> literature;
            case FOREIGN_LANGUAGE -> foreignLanguage;
            case PHYSICS -> physics;
            case CHEMISTRY -> chemistry;
            case BIOLOGY -> biology;
            case HISTORY -> history;
            case GEOGRAPHY -> geography;
            case CIVIC_EDUCATION -> civicEducation;
        };
    }
}
