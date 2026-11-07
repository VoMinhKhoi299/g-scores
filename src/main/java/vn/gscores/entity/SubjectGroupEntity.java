package vn.gscores.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "subject_groups")
public class SubjectGroupEntity {

    @Id
    @Column(name = "group_code", length = 5, nullable = false)
    private String groupCode;

    @Column(name = "name", length = 50, nullable = false)
    private String name;
}
