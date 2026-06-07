package com.exam;

import com.exam.ui.MainMenu;

public class Main {
    public static void main(String[] args) {
        ExamDao dao = new CsvExamDao("questions.csv");
        ResultDao resultDao = new CsvResultDao("results.csv");
        ExamCatalog catalog = new ExamCatalog(dao);
        catalog.loadExams();
        ExamService examService = new ExamService();

        MainMenu mainMenu = new MainMenu(catalog, dao, resultDao, examService);
        mainMenu.start();
    }
}
