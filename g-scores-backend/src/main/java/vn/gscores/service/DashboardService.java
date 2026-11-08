package vn.gscores.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.gscores.dto.DashboardSummaryResponse;
import vn.gscores.entity.DatasetMetadata;
import vn.gscores.exception.ResourceNotFoundException;
import vn.gscores.repository.DatasetMetadataRepository;
import vn.gscores.repository.ScoreDistributionRepository;
import vn.gscores.repository.SubjectGroupRepository;
import vn.gscores.repository.SubjectRepository;

/**
 * Tong ke dashboard. Chi doc cac bang nho (metadata, subjects, nhom),
 * khong quet bang 6 trieu dong student_scores.
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DatasetMetadataRepository datasetMetadataRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectGroupRepository subjectGroupRepository;
    private final ScoreDistributionRepository scoreDistributionRepository;

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary() {
        DatasetMetadata metadata = datasetMetadataRepository.findById((short) 1)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chua co du lieu: dataset chua duoc nap vao database"));

        // SUM tra ve null neu bang phan bo rong; dung 0 de tra ve co nghia.
        Long totalScoresRaw = scoreDistributionRepository.sumStudentCount();
        long totalScores = totalScoresRaw == null ? 0L : totalScoresRaw;

        return new DashboardSummaryResponse(
                metadata.getSourceFile(),
                metadata.getTotalRows(),
                totalScores,
                (int) subjectRepository.count(),
                (int) subjectGroupRepository.count(),
                metadata.getSeededAt());
    }
}
