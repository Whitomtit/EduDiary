package il.whitomtit.edudiary.util;

import il.whitomtit.edudiary.R;
import il.whitomtit.edudiary.model.Subject;

import java.util.ArrayList;
import java.util.List;

public class Subjects {
    private List<Subject> subjects;
    private Subject defaultSubject;
    private static Subjects instance;


    private Subjects() {
        this.subjects = new ArrayList<>();
        subjects.add(new Subject("Law", "#9C27B0", R.drawable.subject_law));
        subjects.add(new Subject("Sport", "#F70900", R.drawable.subject_sport));
        subjects.add(new Subject("Literature", "#448AFF", R.drawable.subject_literature));
        subjects.add(new Subject("Physics", "#7100F7", R.drawable.subject_physics));
        subjects.add(new Subject("Chemistry", "#64DD17", R.drawable.subject_chemistry));
        subjects.add(new Subject("Biology", "#43A047", R.drawable.subject_biology));
        subjects.add(new Subject("Programming", "#FDD835", R.drawable.subject_programming));
        subjects.add(new Subject("Robotics", "#FB8C00", R.drawable.subject_robotics));
        subjects.add(new Subject("English", "#3949AB", R.drawable.subject_english));
        subjects.add(new Subject("Math", "#F50057", R.drawable.subject_math));

        this.defaultSubject = new Subject("", "#263238", R.drawable.subject_default);
    }

    public static Subjects getInstance() {
        if (instance == null)
            instance = new Subjects();
        return instance;
    }

    public List<String> getSubjectsName() {
        ArrayList<String> subjectsName = new ArrayList<>();
        for (Subject subject : this.subjects)
            subjectsName.add(subject.getName());
        return subjectsName;
    }

    public Subject getSubjectByName(String name) {
        for (Subject subject : subjects)
            if (subject.getName().equalsIgnoreCase(name))
                return subject;
        return defaultSubject;
    }
}
