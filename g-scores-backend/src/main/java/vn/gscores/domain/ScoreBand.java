package vn.gscores.domain;

import java.math.BigDecimal;

public enum ScoreBand {
    VERY_GOOD(new BigDecimal("8"), "Tốt"),
    GOOD(new BigDecimal("6"), "Khá"),
    FAIR(new BigDecimal("4"), "Trung bình"),
    POOR(new BigDecimal("0"), "Yếu");

    private final BigDecimal minScore;
    private final String label;

    ScoreBand(BigDecimal minScore, String label) {
        this.minScore = minScore;
        this.label = label;
    }

    public BigDecimal getMinScore() {
        return minScore;
    }

    public String getLabel() {
        return label;
    }

    public static ScoreBand of(BigDecimal score) {
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
