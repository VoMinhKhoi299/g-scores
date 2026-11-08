package vn.gscores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.gscores.entity.ScoreDistribution;
import vn.gscores.entity.ScoreDistributionId;

import java.util.List;

public interface ScoreDistributionRepository extends JpaRepository<ScoreDistribution, ScoreDistributionId> {
    List<ScoreDistribution> findBySubjectCode(String subjectCode);

//Tong so diem da ghi nhanh trong toan bo dataset. Tinh tu bang phan bo da tinh san thay vi COUNT(*) tren 6 trieu dong student_scores. Tra ve null neu chua co du lieu nao.
    @Query("SELECT SUM(d.studentCount) FROM ScoreDistribution d")
    Long sumStudentCount();
}
