package vn.gscores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.gscores.entity.GroupTopStudent;
import vn.gscores.entity.GroupTopStudentId;

import java.util.List;

public interface GroupTopStudentRepository extends JpaRepository<GroupTopStudent, GroupTopStudentId> {

    List<GroupTopStudent> findByGroupCodeOrderByRank(String groupCode);
}
