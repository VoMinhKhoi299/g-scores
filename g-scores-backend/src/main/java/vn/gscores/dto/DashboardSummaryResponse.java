package vn.gscores.dto;

import java.time.OffsetDateTime;

/**
 * Tong ke toan bo dashboard.
 */
public record DashboardSummaryResponse(
        String sourceFile,
        long totalStudents,
        long totalScores,
        int subjectCount,
        int groupCount,
        OffsetDateTime seededAt) {
}
