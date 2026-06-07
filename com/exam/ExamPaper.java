package com.exam;

import java.util.ArrayList;
import java.util.List;

public class ExamPaper {
    private String title;
    private String description;
    private List<Question> questions;

    public ExamPaper(String title, String description) {
        this.title = title;
        this.description = description;
        this.questions = new ArrayList<>();
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    
    public void addQuestion(Question q) { this.questions.add(q); }
    public List<Question> getQuestions() { return this.questions; }

    public double getTotalPossibleScore() {
        double total = 0;
        for (Question q : questions) { total += q.getMaxScore(); }
        return total;
    }
    
    @Override
    public String toString() {
        return title + " - " + description + " (Total Score: " + getTotalPossibleScore() + ")";
    }
}
