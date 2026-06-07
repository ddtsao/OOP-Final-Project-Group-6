package com.exam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CsvResultDao implements ResultDao {
    private String csvFilePath;

    public CsvResultDao(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    @Override
    public void saveResult(Result result) {
        boolean isNewFile = !new File(csvFilePath).exists();
        try (PrintWriter pw = new PrintWriter(new FileWriter(csvFilePath, true))) {
            if (isNewFile) {
                pw.println("StudentName,ExamTitle,Score,Feedback");
            }
            pw.printf("\"%s\",\"%s\",%.1f,\"%s\"\n", 
                result.getStudentName(), result.getExamTitle(), result.getFinalScore(), result.getFeedback());
        } catch (IOException e) {
            System.err.println("Error saving result: " + e.getMessage());
        }
    }

    @Override
    public List<Result> getAllResults() {
        List<Result> results = new ArrayList<>();
        File file = new File(csvFilePath);
        if (!file.exists()) return results;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean isFirst = true;
            while ((line = br.readLine()) != null) {
                if (isFirst) { isFirst = false; continue; }
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (parts.length < 4) continue;
                
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].trim();
                    if (parts[i].startsWith("\"") && parts[i].endsWith("\"") && parts[i].length() >= 2) {
                        parts[i] = parts[i].substring(1, parts[i].length() - 1);
                    }
                }
                
                String studentName = parts[0];
                String examTitle = parts[1];
                double score = Double.parseDouble(parts[2]);
                String feedback = parts[3];
                
                results.add(new Result(studentName, examTitle, score, feedback));
            }
        } catch (IOException e) {
            System.err.println("Error loading results: " + e.getMessage());
        }
        return results;
    }
}
