package vn.gscores.domain;

import java.util.Arrays;
import java.util.List;

public enum SubjectGroup {
    A,
    B,
    C;

    public List<Subject> getSubjects() {
        return Arrays.stream(Subject.values())
                .filter(subject -> subject.getGroup() == this)
                .toList();
    }
}
