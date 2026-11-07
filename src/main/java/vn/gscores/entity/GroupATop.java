package vn.gscores.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "group_a_top")
public class GroupATop {

    @Id
    @Column(name = "rank", nullable = false)
    private int rank;

    @Column(name = "sbd", length = 8, nullable = false)
    private String sbd;

    @Column(name = "toan", precision = 4, scale = 2, nullable = false)
    private BigDecimal math;

    @Column(name = "vat_li", precision = 4, scale = 2, nullable = false)
    private BigDecimal physics;

    @Column(name = "hoa_hoc", precision = 4, scale = 2, nullable = false)
    private BigDecimal chemistry;

    @Column(name = "total", precision = 5, scale = 2, nullable = false)
    private BigDecimal total;

    @Column(name = "avg_score", precision = 4, scale = 2, nullable = false)
    private BigDecimal avgScore;

    @Column(name = "toan_rank", nullable = false)
    private int mathRank;

    @Column(name = "vat_li_rank", nullable = false)
    private int physicsRank;

    @Column(name = "hoa_hoc_rank", nullable = false)
    private int chemistryRank;
}
