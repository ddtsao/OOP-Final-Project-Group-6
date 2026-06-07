package com.exam.ui;

import com.exam.Exam;
import com.exam.ExamService;
import com.exam.Question;
import com.exam.Result;
import com.exam.Student;
import com.exam.ResultDao;

import java.util.HashMap;
import java.util.Map;

public class ExamController {
    private Student student;
    private Exam exam;
    private ExamService examService;
    private ResultDao resultDao;
    private ExamFrame view;
    private int currentQuestionIndex = 0;
    private Map<String, String> answers = new HashMap<>();

    public ExamController(Student student, Exam exam, ExamService examService, ResultDao resultDao) {
        this.student = student;
        this.exam = exam;
        this.examService = examService;
        this.resultDao = resultDao;
    }

    public void setView(ExamFrame view) { this.view = view; }

    public void startExam() {
        currentQuestionIndex = 0;
        answers.clear();
        showCurrentQuestion();
    }

    private void showCurrentQuestion() {
        if (currentQuestionIndex < exam.getQuestions().size()) {
            Question q = exam.getQuestions().get(currentQuestionIndex);
            boolean isLast = (currentQuestionIndex == exam.getQuestions().size() - 1);
            view.displayQuestion(q, currentQuestionIndex + 1, exam.getQuestions().size(), isLast);
        }
    }

    public void previousQuestion(String currentAnswer) {
        if (currentAnswer != null && !currentAnswer.isEmpty()) {
            Question q = exam.getQuestions().get(currentQuestionIndex);
            answers.put(q.getId(), currentAnswer);
        }
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            showCurrentQuestion();
        }
    }

    public String getSavedAnswer() {
        if (currentQuestionIndex < exam.getQuestions().size()) {
            Question q = exam.getQuestions().get(currentQuestionIndex);
            return answers.get(q.getId());
        }
        return null;
    }

    public void submitAnswerAndNext(String answer) {
        Question q = exam.getQuestions().get(currentQuestionIndex);
        answers.put(q.getId(), answer);
        currentQuestionIndex++;
        if (currentQuestionIndex < exam.getQuestions().size()) {
            showCurrentQuestion();
        } else {
            finishExam();
        }
    }

    private void finishExam() {
        Result result = examService.conductExam(student, exam, answers);
        resultDao.saveResult(result);
        view.displayResult(result);
    }
}
