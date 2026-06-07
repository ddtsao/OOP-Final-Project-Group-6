package com.exam;
import java.util.List;

public class MultipleChoiceQuestion extends Question {
    private List<String> options;
    private String correctAnswer;

    public MultipleChoiceQuestion(String id, String description, double maxScore, List<String> options, String correctAnswer) {
        super(id, description, maxScore);
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public List<String> getOptions() { return options; }
    public String getCorrectAnswer() { return correctAnswer; }

    @Override
    public double calculateScore(Answer answer) {
        if (answer != null && answer.getContent() != null && correctAnswer != null) {
            return correctAnswer.trim().equalsIgnoreCase(answer.getContent().trim()) ? getMaxScore() : 0;
        }
        return 0;
    }
}
