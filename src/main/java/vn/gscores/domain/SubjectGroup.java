package vn.gscores.domain;

import java.util.List;

/**
 * A group of subjects that a candidate can combine into an exam combination.
 *
 * Groups are stored in the database so that new groups can be added later
 * without changing the code; this definition is only used when seeding.
 */
public record SubjectGroup(String code, String name, List<Subject> subjects) {

    public static List<SubjectGroup> standard() {
        return List.of(
                new SubjectGroup("A", "Nhóm A",
                        List.of(Subject.MATH, Subject.PHYSICS, Subject.CHEMISTRY)),
                new SubjectGroup("B", "Nhóm B",
                        List.of(Subject.LITERATURE, Subject.HISTORY, Subject.GEOGRAPHY,
                                Subject.CIVIC_EDUCATION)),
                new SubjectGroup("C", "Nhóm C",
                        List.of(Subject.FOREIGN_LANGUAGE, Subject.BIOLOGY))
        );
    }
}
