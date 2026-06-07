package com.exam;

public class TrueFalseQuestion extends Question {
    private boolean correctAnswer;

    public TrueFalseQuestion(String id, String description, double maxScore, boolean correctAnswer) {
        super(id, description, maxScore);
        this.correctAnswer = correctAnswer;
    }

    public boolean isCorrectAnswer() { return correctAnswer; }

    @Override
    public double calculateScore(Answer answer) {
        if (answer != null && String.valueOf(correctAnswer).equalsIgnoreCase(answer.getContent())) {
            return getMaxScore();
        }
        return 0;
    }
}
