package vn.gscores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.gscores.entity.ScoreDistribution;
import vn.gscores.entity.ScoreDistributionId;

public interface ScoreDistributionRepository extends JpaRepository<ScoreDistribution, ScoreDistributionId> {
}
