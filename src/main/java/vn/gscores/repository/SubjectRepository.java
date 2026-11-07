package vn.gscores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.gscores.entity.SubjectEntity;

public interface SubjectRepository extends JpaRepository<SubjectEntity, String> {
}
