package vn.gscores.domain;

import java.math.BigDecimal;

public enum ScoreBand {
    VERY_GOOD(new BigDecimal("8")),
    GOOD(new BigDecimal("6")),
    FAIR(new BigDecimal("4")),
    POOR(new BigDecimal("0"));

    private final BigDecimal minScore;

    ScoreBand(BigDecimal minScore) {
        this.minScore = minScore;
    }

    public BigDecimal getMinScore() {
        return minScore;
    }

    public static ScoreBand of(BigDecimal score) {
        // BigDecimal must be compared with compareTo, not with >= or ==
        if (score.compareTo(VERY_GOOD.minScore) >= 0) {
            return VERY_GOOD;
        }
        if (score.compareTo(GOOD.minScore) >= 0) {
            return GOOD;
        }
        if (score.compareTo(FAIR.minScore) >= 0) {
            return FAIR;
        }
        return POOR;
    }
}
