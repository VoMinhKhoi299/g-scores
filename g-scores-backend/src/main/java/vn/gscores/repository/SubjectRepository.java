package vn.gscores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.gscores.entity.SubjectEntity;

import java.util.Collection;
import java.util.List;

public interface SubjectRepository extends JpaRepository<SubjectEntity, String> {
    List<SubjectEntity> findByCodeIn(Collection<String> codes);
}
