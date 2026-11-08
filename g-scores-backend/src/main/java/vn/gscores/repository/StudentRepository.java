package vn.gscores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.gscores.entity.Student;

public interface StudentRepository extends JpaRepository<Student, String> {
}
