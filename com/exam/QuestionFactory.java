package com.exam;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class QuestionFactory {
    public static Question createQuestion(String type, String text, String options, String answer, double score) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        if ("MCQ".equalsIgnoreCase(type)) {
            List<String> optionList = Arrays.asList(options.split("\\|"));
            return new MultipleChoiceQuestion(id, text, score, optionList, answer);
        } else if ("TF".equalsIgnoreCase(type)) {
            boolean correctAnswer = Boolean.parseBoolean(answer);
            return new TrueFalseQuestion(id, text, score, correctAnswer);
        } else {
            throw new IllegalArgumentException("Unknown question type: " + type);
        }
    }
}
