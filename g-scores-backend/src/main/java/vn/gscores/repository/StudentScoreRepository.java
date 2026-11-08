package vn.gscores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.gscores.entity.StudentScore;
import vn.gscores.entity.StudentScoreId;

import java.util.List;

public interface StudentScoreRepository extends JpaRepository<StudentScore, StudentScoreId> {
//Diem cua mot hoc sinh, sap xep theo ma mon. Chi doc theo primary key (sbd, subject_code) nen khong can quet bang.
    @Query("SELECT s FROM StudentScore s WHERE s.sbd = :sbd ORDER BY s.subjectCode")
    List<StudentScore> findScoresBySbdOrdered(@Param("sbd") String sbd);
}
