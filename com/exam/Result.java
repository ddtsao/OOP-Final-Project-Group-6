package com.exam;

public class Result {
    private String studentName;
    private String examTitle;
    private double finalScore;
    private String feedback;

    public Result(String studentName, String examTitle, double finalScore, String feedback) {
        this.studentName = studentName;
        this.examTitle = examTitle;
        this.finalScore = finalScore;
        this.feedback = feedback;
    }

    public String getStudentName() { return studentName; }
    public String getExamTitle() { return examTitle; }
    public double getFinalScore() { return finalScore; }
    public String getFeedback() { return feedback; }
}
