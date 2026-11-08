package vn.gscores.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.gscores.domain.ScoreBand;
import vn.gscores.dto.StudentDetailResponse;
import vn.gscores.dto.StudentGroupSummary;
import vn.gscores.dto.StudentSubjectScore;
import vn.gscores.entity.GroupSubjectEntity;
import vn.gscores.entity.Student;
import vn.gscores.entity.StudentScore;
import vn.gscores.entity.SubjectEntity;
import vn.gscores.entity.SubjectGroupEntity;
import vn.gscores.exception.ResourceNotFoundException;
import vn.gscores.repository.GroupSubjectRepository;
import vn.gscores.repository.StudentRepository;
import vn.gscores.repository.StudentScoreRepository;
import vn.gscores.repository.SubjectGroupRepository;
import vn.gscores.repository.SubjectRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Chi tiet diem cua mot hoc sinh va tong hop theo nhom mon.
 */
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentScoreRepository studentScoreRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectGroupRepository subjectGroupRepository;
    private final GroupSubjectRepository groupSubjectRepository;

    @Transactional(readOnly = true)
    public StudentDetailResponse getStudentDetail(String sbd) {
        Student student = studentRepository.findById(sbd)
                .orElseThrow(() -> new ResourceNotFoundException("Hoc sinh khong ton tai: " + sbd));

        List<StudentScore> scores = studentScoreRepository.findScoresBySbdOrdered(sbd);
        Map<String, String> subjectNames = loadSubjectNames(scores.stream().map(StudentScore::getSubjectCode).toList());
        Map<String, BigDecimal> scoreBySubject = scores.stream()
                .collect(Collectors.toMap(StudentScore::getSubjectCode, StudentScore::getScore, (a, b) -> a));

        List<StudentSubjectScore> scoreList = scores.stream()
                .map(s -> new StudentSubjectScore(
                        s.getSubjectCode(),
                        subjectNames.getOrDefault(s.getSubjectCode(), s.getSubjectCode()),
                        s.getScore(),
                        ScoreBand.of(s.getScore()).name()))
                .toList();

        return new StudentDetailResponse(
                sbd,
                student.getForeignLanguageCode() == null ? null : student.getForeignLanguageCode().name(),
                scoreList.size(),
                scoreList,
                buildGroupSummaries(scoreBySubject));
    }

    // Tinh tong diem va diem trung binh theo nhom. Chi tinh khi hoc sinh thi day du, tat ca cac mon trong nhom, khop nghia voi bang group_top_students da tinh san.
    private List<StudentGroupSummary> buildGroupSummaries(Map<String, BigDecimal> scoreBySubject) {
        Map<String, List<String>> subjectsByGroup = groupSubjectRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        GroupSubjectEntity::getGroupCode,
                        Collectors.mapping(GroupSubjectEntity::getSubjectCode,
                                Collectors.toList())));

        List<StudentGroupSummary> result = new ArrayList<>();
        for (SubjectGroupEntity group : subjectGroupRepository.findGroupsOrderedByCode()) {
            List<String> codes = subjectsByGroup.getOrDefault(group.getGroupCode(), List.of());
            BigDecimal total = BigDecimal.ZERO;
            boolean completed = true;

            for (String code : codes) {
                BigDecimal score = scoreBySubject.get(code);
                if (score == null) {
                    completed = false;
                } else {
                    total = total.add(score);
                }
            }

            BigDecimal average = completed && !codes.isEmpty()
                    ? total.divide(BigDecimal.valueOf(codes.size()), 2, RoundingMode.HALF_UP)
                    : null;

            result.add(new StudentGroupSummary(
                    group.getGroupCode(),
                    group.getName(),
                    completed,
                    codes.size(),
                    completed ? total : null,
                    average));
        }
        return result;
    }

    private Map<String, String> loadSubjectNames(List<String> codes) {
        if (codes.isEmpty()) {
            return Map.of();
        }
        return subjectRepository.findByCodeIn(codes).stream()
                .collect(Collectors.toMap(SubjectEntity::getCode, SubjectEntity::getName, (a, b) -> a));
    }
}
