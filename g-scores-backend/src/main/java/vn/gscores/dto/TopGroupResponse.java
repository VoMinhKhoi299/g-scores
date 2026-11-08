package vn.gscores.dto;

import java.util.List;

/**
 * Bang xep hang top cua mot nhom.
 */
public record TopGroupResponse(
        String groupCode,
        String name,
        int subjectCount,
        List<TopStudentResponse> students) {
}
