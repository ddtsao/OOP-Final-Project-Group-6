package com.exam;

import com.exam.ui.StudentMenu;

public class Student extends User {
    public Student(String id, String name) { super(id, name); }

    @Override
    public void executeRole(ExamCatalog catalog, ExamDao examDao, ResultDao resultDao, ExamService examService) {
        StudentMenu menu = new StudentMenu(this, catalog, examDao, resultDao, examService);
        menu.display();
    }
}
