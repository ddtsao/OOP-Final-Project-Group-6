package com.exam;

import java.util.List;

public interface ExamDao {
    List<ExamPaper> loadExams();
    void addQuestion(String examTitle, Question q);
    void saveAllExams(List<ExamPaper> papers);
}

