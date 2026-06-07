package com.exam;

import com.exam.ui.InstructorMenu;

public class Instructor extends User {
    public Instructor(String id, String name) { super(id, name); }

    @Override
    public void executeRole(ExamCatalog catalog, ExamDao examDao, ResultDao resultDao, ExamService examService) {
        InstructorMenu menu = new InstructorMenu(this, catalog, examDao, resultDao, examService);
        menu.display();
    }
}
