package vn.gscores.dto;

import java.math.BigDecimal;

/**
 * Ket qua cua hoc sinh trong mot nhom mon.
 *
 * <p>{@code completed} cho biet hoc sinh co duoc thi day du cac mon trong nhom hay khong.
 * Chi nhom hoan thanh moi duoc tinh tong diem va diem trung binh theo nhom.
 */
public record StudentGroupSummary(
        String groupCode,
        String groupName,
        boolean completed,
        int subjectCount,
        BigDecimal total,
        BigDecimal average) {
}
