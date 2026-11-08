package vn.gscores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.gscores.entity.SubjectGroupEntity;

import java.util.List;

public interface SubjectGroupRepository extends JpaRepository<SubjectGroupEntity, String> {
    @Query("SELECT g FROM SubjectGroupEntity g ORDER BY g.groupCode")
    List<SubjectGroupEntity> findGroupsOrderedByCode();
}
