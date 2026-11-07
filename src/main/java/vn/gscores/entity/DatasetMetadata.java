package vn.gscores.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "dataset_metadata")
public class DatasetMetadata {

    @Id
    @Column(name="id", nullable = false)
    private int id;

    @Column(name="source_file", length = 255, nullable = false)
    private String sourceFile;

    @Column(name="total_rows", nullable = false)
    private long totalRows;

    @Column(name="seeded_at", nullable = false)
    private OffsetDateTime seededAt;
}
