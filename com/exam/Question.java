package com.exam;

public abstract class Question implements Gradable {
    private String id;
    private String description;
    private double maxScore;

    public Question(String id, String description, double maxScore) {
        this.id = id;
        this.description = description;
        this.maxScore = maxScore;
    }

    public String getId() { return id; }
    public String getDescription() { return description; }
    public double getMaxScore() { return maxScore; }
}
