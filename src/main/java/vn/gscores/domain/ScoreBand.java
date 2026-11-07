package vn.gscores.domain;

import java.math.BigDecimal;

public enum ScoreBand {
    VERY_GOOD,
    GOOD,
    FAIR,
    POOR;

    private static final BigDecimal VERY_GOOD_MIN = new BigDecimal("8");
    private static final BigDecimal GOOD_MIN = new BigDecimal("6");
    private static final BigDecimal FAIR_MIN = new BigDecimal("4");

    public static ScoreBand of(BigDecimal score) {
        // BigDecimal must be compared with compareTo, not with >= or ==
        if (score.compareTo(VERY_GOOD_MIN) >= 0) {
            return VERY_GOOD;
        }
        if (score.compareTo(GOOD_MIN) >= 0) {
            return GOOD;
        }
        if (score.compareTo(FAIR_MIN) >= 0) {
            return FAIR;
        }
        return POOR;
    }
}
