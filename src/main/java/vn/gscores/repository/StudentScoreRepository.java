package vn.gscores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.gscores.entity.StudentScore;
import vn.gscores.entity.StudentScoreId;

import java.util.List;

public interface StudentScoreRepository extends JpaRepository<StudentScore, StudentScoreId> {

    List<StudentScore> findBySbd(String sbd);

    @Query("SELECT s.subjectCode, s.score FROM StudentScore s WHERE s.sbd = :sbd")
    List<Object[]> findScoresBySbd(@Param("sbd") String sbd);
}
