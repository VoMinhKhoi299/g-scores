package vn.gscores.domain;

/**
 * The nine exam subjects. The code matches both the column name in the raw
 * CSV file and the subject_code column in the database.
 */
public enum Subject {
    MATH("toan", "Toán"),
    LITERATURE("ngu_van", "Ngữ văn"),
    FOREIGN_LANGUAGE("ngoai_ngu", "Ngoại ngữ"),
    PHYSICS("vat_li", "Vật lý"),
    CHEMISTRY("hoa_hoc", "Hóa học"),
    BIOLOGY("sinh_hoc", "Sinh học"),
    HISTORY("lich_su", "Lịch sử"),
    GEOGRAPHY("dia_li", "Địa lý"),
    CIVIC_EDUCATION("gdcd", "GDCD");

    private final String code;
    private final String name;

    Subject(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
