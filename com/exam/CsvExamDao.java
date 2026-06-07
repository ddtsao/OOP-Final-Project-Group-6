package com.exam;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CsvExamDao implements ExamDao {
    private String csvFilePath;

    public CsvExamDao(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    @Override
    public List<ExamPaper> loadExams() {
        Map<String, ExamPaper> paperMap = new LinkedHashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }
                
                // Simple CSV parse handling quotes:
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (parts.length < 7) continue;

                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].trim();
                    if (parts[i].startsWith("\"") && parts[i].endsWith("\"") && parts[i].length() >= 2) {
                        parts[i] = parts[i].substring(1, parts[i].length() - 1);
                    }
                    parts[i] = parts[i].replace("\"\"", "\"");
                }

                String examTitle = parts[0];
                String description = parts[1];
                String type = parts[2];

                ExamPaper paper = paperMap.computeIfAbsent(examTitle, k -> new ExamPaper(examTitle, description));
                
                if (type == null || type.isEmpty()) {
                    continue; // Register the paper in paperMap but do not create questions
                }

                String text = parts[3].replace("\\n", "\n");
                String options = parts[4];
                String answer = parts[5];
                double score = 0.0;
                try {
                    score = Double.parseDouble(parts[6]);
                } catch (NumberFormatException e) {
                    // default to 0.0
                }

                Question q = QuestionFactory.createQuestion(type, text, options, answer, score);
                paper.addQuestion(q);
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }

        return new ArrayList<>(paperMap.values());
    }

    @Override
    public void addQuestion(String examTitle, Question q) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(csvFilePath, true))) {
            String type = "";
            String options = "";
            String answer = "";
            if (q instanceof MultipleChoiceQuestion) {
                type = "MCQ";
                MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) q;
                options = String.join("|", mcq.getOptions()).replace("\"", "\"\"");
                answer = mcq.getCorrectAnswer().replace("\"", "\"\"");
            } else if (q instanceof TrueFalseQuestion) {
                type = "TF";
                TrueFalseQuestion tfq = (TrueFalseQuestion) q;
                answer = String.valueOf(tfq.isCorrectAnswer()).replace("\"", "\"\"");
            }
            
            String text = q.getDescription().replace("\n", "\\n").replace("\"", "\"\"");
            pw.printf("\n\"%s\",\"\",\"%s\",\"%s\",\"%s\",\"%s\",%.1f", 
                examTitle.replace("\"", "\"\""), type, text, options, answer, q.getMaxScore());
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }

    @Override
    public void saveAllExams(List<ExamPaper> papers) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(csvFilePath, false))) { // false to overwrite
            pw.print("ExamTitle,Description,QuestionType,QuestionText,Options,CorrectAnswer,Score");
            for (ExamPaper paper : papers) {
                String examTitle = paper.getTitle().replace("\"", "\"\"");
                String examDesc = paper.getDescription().replace("\"", "\"\"");
                
                if (paper.getQuestions().isEmpty()) {
                    pw.printf("\n\"%s\",\"%s\",\"\",\"\",\"\",\"\",0.0", examTitle, examDesc);
                } else {
                    for (Question q : paper.getQuestions()) {
                        String type = "";
                        String options = "";
                        String answer = "";
                        if (q instanceof MultipleChoiceQuestion) {
                            type = "MCQ";
                            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) q;
                            options = String.join("|", mcq.getOptions()).replace("\"", "\"\"");
                            answer = mcq.getCorrectAnswer().replace("\"", "\"\"");
                        } else if (q instanceof TrueFalseQuestion) {
                            type = "TF";
                            TrueFalseQuestion tfq = (TrueFalseQuestion) q;
                            answer = String.valueOf(tfq.isCorrectAnswer()).replace("\"", "\"\"");
                        }
                        
                        String text = q.getDescription().replace("\n", "\\n").replace("\"", "\"\"");
                        pw.printf("\n\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%.1f", 
                            examTitle, examDesc, type, text, options, answer, q.getMaxScore());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }
}
