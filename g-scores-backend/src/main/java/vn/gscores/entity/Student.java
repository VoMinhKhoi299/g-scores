package vn.gscores.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import vn.gscores.domain.ForeignLanguage;

@Getter
@Setter
@Entity
@Table(name = "students")
public class Student {

    @Id
    @Column(name = "sbd", length = 8, nullable = false)
    private String sbd;

    // Null co nghia hoc sinh khong lam bai thi
    @Enumerated(EnumType.STRING)
    @Column(name = "ma_ngoai_ngu", length = 5)
    private ForeignLanguage foreignLanguageCode;
}
