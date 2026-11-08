package vn.gscores.dto;

import java.math.BigDecimal;

/**
 * Diem cua mot hoc sinh o mot mon, kem bang diem tuong ung.
 */
public record StudentSubjectScore(
        String subjectCode,
        String subjectName,
        BigDecimal score,
        String band) {
}
