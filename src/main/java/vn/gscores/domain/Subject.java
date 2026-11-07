package vn.gscores.domain;

import vn.gscores.entity.Student;

public enum Subject {
    MATH("toan", "Toán", SubjectGroup.A),
    LITERATURE("ngu_van", "Ngữ văn", SubjectGroup.B),
    FOREIGN_LANGUAGE("ngoai_ngu", "Ngoại ngữ", SubjectGroup.C),
    PHYSICS("vat_li", "Vật lý", SubjectGroup.A),
    CHEMISTRY("hoa_hoc", "Hóa học", SubjectGroup.A),
    BIOLOGY("sinh_hoc", "Sinh học", SubjectGroup.C),
    HISTORY("lich_su", "Lịch sử", SubjectGroup.B),
    GEOGRAPHY("dia_li", "Địa lý", SubjectGroup.B),
    CIVIC_EDUCATION("gdcd", "GDCD", SubjectGroup.B);

    private final String code;
    private final String name;
    private final SubjectGroup group;

    Subject(String code, String name, SubjectGroup group) {
        this.code = code;
        this.name = name;
        this.group = group;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public SubjectGroup getGroup() {
        return group;
    }

    public boolean isTakenBy(Student student) {
        return student.getScore(this) != null;
    }
}
