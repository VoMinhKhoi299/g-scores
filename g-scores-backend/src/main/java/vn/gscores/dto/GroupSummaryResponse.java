package vn.gscores.dto;

import java.util.List;

/**
 * Thong tin nhom mon va cac mon thuoc nhom.
 */
public record GroupSummaryResponse(
        String groupCode,
        String name,
        int subjectCount,
        List<GroupSubjectInfo> subjects) {
}
