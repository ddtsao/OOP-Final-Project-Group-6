package com.exam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Exam {
    private String examId;
    private String title;
    private List<Question> questions;

    public Exam(String examId, String title) {
        this.examId = examId;
        this.title = title;
        this.questions = new ArrayList<>();
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public Result submit(Student student, Map<String, Answer> studentAnswers) {
        double totalScore = 0;
        for (Question question : questions) {
            Answer answer = studentAnswers.get(question.getId());
            totalScore += question.calculateScore(answer);
        }
        String feedback = totalScore >= 60 ? "Pass" : "Fail";
        return new Result(student.getName(), this.title, totalScore, feedback);
    }
}
