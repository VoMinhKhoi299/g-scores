package vn.gscores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.gscores.entity.DatasetMetadata;

public interface DatasetMetadataRepository extends JpaRepository<DatasetMetadata, Integer> {
}
