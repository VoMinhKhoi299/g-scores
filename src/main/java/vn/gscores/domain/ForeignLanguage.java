package vn.gscores.domain;

public enum ForeignLanguage {
    N1("N1", 1, "Nhập mức"),
    N2("N2", 2, "Trung cấp"),
    N3("N3", 3, "Khá"),
    N4("N4", 4, "Giỏi"),
    N5("N5", 5, "Rất giỏi"),
    N6("N6", 6, "Xuất sắc"),
    N7("N7", 7, "Hoàn hảo");

    private final String code;
    private final int level;
    private final String displayName;

    ForeignLanguage(String code, int level, String displayName) {
        this.code = code;
        this.level = level;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public int getLevel() {
        return level;
    }

    public String getDisplayName() {
        return displayName;
    }
}
