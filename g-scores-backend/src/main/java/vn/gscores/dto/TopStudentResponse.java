package vn.gscores.dto;

import java.math.BigDecimal;

/**
 * Mot hoc sinh trong bang xep hang top cua mot nhom.
 */
public record TopStudentResponse(
        int rank,
        String sbd,
        BigDecimal total,
        BigDecimal average) {
}
