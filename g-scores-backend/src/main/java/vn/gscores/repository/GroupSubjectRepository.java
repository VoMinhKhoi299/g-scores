package vn.gscores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.gscores.entity.GroupSubjectEntity;
import vn.gscores.entity.GroupSubjectId;

import java.util.List;

public interface GroupSubjectRepository extends JpaRepository<GroupSubjectEntity, GroupSubjectId> {

    List<GroupSubjectEntity> findByGroupCode(String groupCode);

    long countByGroupCode(String groupCode);
}
