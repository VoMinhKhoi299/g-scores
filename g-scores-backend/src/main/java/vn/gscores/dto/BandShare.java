package vn.gscores.dto;

import java.math.BigDecimal;

/**
 * Ty le hoc sinh theo tung bang diem.
 */
public record BandShare(
        String band,
        String label,
        long studentCount,
        BigDecimal percentage) {
}
