package com.exam;

import java.util.HashMap;
import java.util.Map;

public class ExamService {
    public Exam createExam(Instructor instructor, String examId, String title) {
        return new Exam(examId, title);
    }

    public void addQuestion(Exam exam, Question question) {
        exam.addQuestion(question);
    }

    public Result conductExam(Student student, Exam exam, Map<String, String> studentInputs) {
        Map<String, Answer> studentAnswers = new HashMap<>();
        for (Question question : exam.getQuestions()) {
            String input = studentInputs.getOrDefault(question.getId(), "");
            studentAnswers.put(question.getId(), new Answer(input));
        }
        return exam.submit(student, studentAnswers);
    }
}
