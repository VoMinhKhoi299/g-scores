package vn.gscores.dto;

import java.util.List;

/**
 * Chi tiet diem cua mot hoc sinh: cac mon da thi va tong hop theo nhom.
 */
public record StudentDetailResponse(
        String sbd,
        String foreignLanguage,
        int subjectCount,
        List<StudentSubjectScore> scores,
        List<StudentGroupSummary> groups) {
}
