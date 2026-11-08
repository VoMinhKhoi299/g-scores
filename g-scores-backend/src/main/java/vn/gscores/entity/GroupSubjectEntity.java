package vn.gscores.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "group_subjects")
@IdClass(GroupSubjectId.class)
public class GroupSubjectEntity {
    @Id
    @Column(name = "group_code", length = 5, nullable = false)
    private String groupCode;

    @Id
    @Column(name = "subject_code", length = 20, nullable = false)
    private String subjectCode;
}
