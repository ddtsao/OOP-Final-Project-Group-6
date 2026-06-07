package com.exam;

import java.util.ArrayList;
import java.util.List;

public class ExamCatalog {
    private List<ExamPaper> availablePapers;
    private ExamDao examDao;

    // Dependency Injection
    public ExamCatalog(ExamDao examDao) {
        this.examDao = examDao;
        this.availablePapers = new ArrayList<>();
    }

    public void loadExams() {
        this.availablePapers = examDao.loadExams();
    }

    public List<ExamPaper> getAvailablePapers() {
        return availablePapers;
    }
}
