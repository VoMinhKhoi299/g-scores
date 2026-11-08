package vn.gscores.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Phan bo phan tram diem cua mot mon.
 */
public record DistributionResponse(
        String subjectCode,
        String subjectName,
        long totalStudents,
        BigDecimal averageScore,
        List<BandShare> bands) {
}
