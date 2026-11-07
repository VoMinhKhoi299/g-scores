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

    // Null means the student did not take a foreign language exam,
    // which is different from having a score of zero.
    @Enumerated(EnumType.STRING)
    @Column(name = "ma_ngoai_ngu", length = 5)
    private ForeignLanguage foreignLanguageCode;
}
