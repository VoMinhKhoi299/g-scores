package vn.gscores.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.gscores.domain.ScoreBand;
import vn.gscores.dto.BandShare;
import vn.gscores.dto.DistributionResponse;
import vn.gscores.dto.GroupSummaryResponse;
import vn.gscores.dto.GroupSubjectInfo;
import vn.gscores.dto.TopGroupResponse;
import vn.gscores.dto.TopStudentResponse;
import vn.gscores.entity.GroupSubjectEntity;
import vn.gscores.entity.ScoreDistribution;
import vn.gscores.entity.SubjectEntity;
import vn.gscores.entity.SubjectGroupEntity;
import vn.gscores.entity.SubjectStatistic;
import vn.gscores.exception.ResourceNotFoundException;
import vn.gscores.repository.GroupSubjectRepository;
import vn.gscores.repository.GroupTopStudentRepository;
import vn.gscores.repository.ScoreDistributionRepository;
import vn.gscores.repository.SubjectGroupRepository;
import vn.gscores.repository.SubjectRepository;
import vn.gscores.repository.SubjectStatisticRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Cac bao cao da tinh san: phan bo diem theo mon va top 10 theo nhom.
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final SubjectRepository subjectRepository;
    private final SubjectGroupRepository subjectGroupRepository;
    private final GroupSubjectRepository groupSubjectRepository;
    private final ScoreDistributionRepository scoreDistributionRepository;
    private final SubjectStatisticRepository subjectStatisticRepository;
    private final GroupTopStudentRepository groupTopStudentRepository;

    @Transactional(readOnly = true)
    public DistributionResponse getDistribution(String subjectCode) {
        SubjectEntity subject = requireSubject(subjectCode);

        // Phan bo da tinh san, chi doi tra bang nhieu bang (subject_code, band).
        List<ScoreDistribution> rows = scoreDistributionRepository.findBySubjectCode(subjectCode);
        long totalStudents = rows.stream().mapToLong(ScoreDistribution::getStudentCount).sum();

        List<BandShare> bands = rows.stream()
                .sorted(Comparator.comparingInt(row -> ScoreBand.valueOf(row.getBand()).ordinal()))
                .map(row -> {
                    ScoreBand band = ScoreBand.valueOf(row.getBand());
                    return new BandShare(band.name(), band.getLabel(), row.getStudentCount(), row.getPercentage());
                })
                .toList();

        // Diem trung binh doc tu bang thong ke tinh san, khong quet student_scores.
        BigDecimal average = subjectStatisticRepository.findById(subjectCode)
                .map(SubjectStatistic::getAverageScore)
                .orElse(BigDecimal.ZERO);
        return new DistributionResponse(subjectCode, subject.getName(), totalStudents, average, bands);
    }

    @Transactional(readOnly = true)
    public List<DistributionResponse> getAllDistributions() {
        List<SubjectEntity> subjects = subjectRepository.findAll();
        return subjects.stream()
                .map(subject -> getDistribution(subject.getCode()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GroupSummaryResponse> listGroups() {
        Map<String, List<String>> subjectsByGroup = groupSubjectRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        GroupSubjectEntity::getGroupCode,
                        Collectors.mapping(GroupSubjectEntity::getSubjectCode, Collectors.toList())));

        List<String> allCodes = subjectsByGroup.values().stream()
                .flatMap(List::stream)
                .distinct()
                .toList();
        Map<String, String> subjectNames = subjectRepository.findByCodeIn(allCodes).stream()
                .collect(Collectors.toMap(SubjectEntity::getCode, SubjectEntity::getName, (a, b) -> a));

        return subjectGroupRepository.findGroupsOrderedByCode().stream()
                .map(group -> {
                    List<String> codes = subjectsByGroup.getOrDefault(group.getGroupCode(), List.of());
                    List<GroupSubjectInfo> subjects = codes.stream()
                            .map(code -> new GroupSubjectInfo(code,
                                    subjectNames.getOrDefault(code, code)))
                            .toList();
                    return new GroupSummaryResponse(group.getGroupCode(), group.getName(),
                            subjects.size(), subjects);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public TopGroupResponse getGroupTop(String groupCode) {
        SubjectGroupEntity group = requireGroup(groupCode);
        int subjectCount = (int) groupSubjectRepository.countByGroupCode(groupCode);

        // Doc bang da tinh san, khong tinh lai top tren student_scores.
        List<TopStudentResponse> students =
                groupTopStudentRepository.findByGroupCodeOrderByRank(groupCode).stream()
                        .map(t -> new TopStudentResponse(t.getRank(), t.getSbd(), t.getTotal(), t.getAvgScore()))
                        .toList();

        return new TopGroupResponse(groupCode, group.getName(), subjectCount, students);
    }

    private SubjectEntity requireSubject(String subjectCode) {
        return subjectRepository.findById(subjectCode)
                .orElseThrow(() -> new ResourceNotFoundException("Mon hoc khong ton tai: " + subjectCode));
    }

    private SubjectGroupEntity requireGroup(String groupCode) {
        return subjectGroupRepository.findById(groupCode)
                .orElseThrow(() -> new ResourceNotFoundException("Nhom mon khong ton tai: " + groupCode));
    }
}
