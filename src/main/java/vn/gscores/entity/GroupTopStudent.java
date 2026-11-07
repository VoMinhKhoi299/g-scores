package vn.gscores.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "group_top_students")
@IdClass(GroupTopStudentId.class)
public class GroupTopStudent {

    @Id
    @Column(name = "group_code", length = 5, nullable = false)
    private String groupCode;

    @Id
    @Column(name = "rank", nullable = false)
    private int rank;

    @Column(name = "sbd", length = 8, nullable = false)
    private String sbd;

    @Column(name = "total", precision = 5, scale = 2, nullable = false)
    private BigDecimal total;

    @Column(name = "avg_score", precision = 4, scale = 2, nullable = false)
    private BigDecimal avgScore;
}
