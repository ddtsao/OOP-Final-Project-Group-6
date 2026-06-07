package com.exam.ui;

import com.exam.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class InstructorMenu {
    private Instructor instructor;
    private ExamCatalog catalog;
    private ExamDao examDao;
    private ResultDao resultDao;
    private ExamService examService;
    private JDialog dashboardDialog;

    public InstructorMenu(Instructor instructor, ExamCatalog catalog, ExamDao examDao, ResultDao resultDao, ExamService examService) {
        this.instructor = instructor;
        this.catalog = catalog;
        this.examDao = examDao;
        this.resultDao = resultDao;
        this.examService = examService;
    }

    public void display() {
        dashboardDialog = new JDialog((Frame)null, "Instructor Dashboard", true);
        dashboardDialog.setSize(750, 550);
        dashboardDialog.setLocationRelativeTo(null);
        
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(ModernUI.BG_COLOR);
        contentPane.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Welcome, Instructor " + instructor.getName(), SwingConstants.CENTER);
        titleLabel.setFont(ModernUI.FONT_TITLE);
        titleLabel.setForeground(ModernUI.TEXT_PRIMARY);
        titleLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        contentPane.add(titleLabel, BorderLayout.NORTH);

        RoundedPanel mainCard = new RoundedPanel(20, ModernUI.CARD_COLOR, true);
        mainCard.setLayout(new GridLayout(2, 2, 25, 25));
        mainCard.setBorder(new EmptyBorder(40, 40, 40, 40));

        mainCard.add(createDashboardCard("📊", "View Results", "View student scores", () -> viewResults()));
        mainCard.add(createDashboardCard("📝", "Edit Question", "Modify or add questions", () -> editQuestion()));
        mainCard.add(createDashboardCard("➕", "Add Exam Paper", "Create new exams", () -> addExamPaper()));
        mainCard.add(createDashboardCard("🚪", "Logout", "Return to main menu", () -> dashboardDialog.dispose()));

        contentPane.add(mainCard, BorderLayout.CENTER);
        dashboardDialog.setContentPane(contentPane);
        dashboardDialog.setVisible(true);
    }

    private RoundedPanel createDashboardCard(String icon, String title, String desc, Runnable action) {
        RoundedPanel card = new RoundedPanel(15, ModernUI.BG_COLOR, false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(25, 10, 25, 10)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(ModernUI.FONT_SUBTITLE);
        titleLabel.setForeground(ModernUI.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel = new JLabel("<html><div style='text-align: center;'>" + desc + "</div></html>", SwingConstants.CENTER);
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descLabel.setForeground(ModernUI.TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(iconLabel);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(descLabel);
        card.add(Box.createVerticalGlue());

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ModernUI.PRIMARY_COLOR, 2),
                    new EmptyBorder(24, 9, 24, 9)
                ));
            }
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                    new EmptyBorder(25, 10, 25, 10)
                ));
            }
            public void mouseClicked(MouseEvent e) { action.run(); }
        });

        return card;
    }

    private void viewResults() {
        List<Result> results = resultDao.getAllResults();
        
        String[] columns = {"Student", "Exam", "Score", "Feedback"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        for (Result r : results) {
            model.addRow(new Object[]{r.getStudentName(), r.getExamTitle(), String.format("%.1f", r.getFinalScore()), r.getFeedback()});
        }
        
        JTable table = new JTable(model);
        table.setFont(ModernUI.FONT_MAIN);
        table.setRowHeight(35);
        table.getTableHeader().setFont(ModernUI.FONT_SUBTITLE);
        table.getTableHeader().setBackground(Color.WHITE);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        JDialog dialog = new JDialog(dashboardDialog, "Student Results", true);
        dialog.setSize(650, 450);
        dialog.setLocationRelativeTo(dashboardDialog);
        
        JPanel p = new JPanel(new BorderLayout(0, 20));
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        p.setBackground(ModernUI.BG_COLOR);
        
        JLabel t = new JLabel("All Student Results");
        t.setFont(ModernUI.FONT_TITLE);
        p.add(t, BorderLayout.NORTH);
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scroll.getViewport().setBackground(Color.WHITE);
        p.add(scroll, BorderLayout.CENTER);
        
        JButton closeBtn = new JButton("Close");
        ModernUI.stylePrimaryButton(closeBtn);
        closeBtn.addActionListener(e -> dialog.dispose());
        
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bp.setBackground(ModernUI.BG_COLOR);
        bp.add(closeBtn);
        p.add(bp, BorderLayout.SOUTH);
        
        dialog.setContentPane(p);
        dialog.setVisible(true);
    }

    private void addExamPaper() {
        JDialog dialog = new JDialog(dashboardDialog, "Create New Exam Paper", true);
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(dashboardDialog);
        
        JPanel p = new JPanel(new BorderLayout(0, 20));
        p.setBorder(new EmptyBorder(25, 30, 25, 30));
        p.setBackground(ModernUI.BG_COLOR);
        
        JLabel t = new JLabel("Create New Exam", SwingConstants.CENTER);
        t.setFont(ModernUI.FONT_TITLE);
        p.add(t, BorderLayout.NORTH);

        RoundedPanel form = new RoundedPanel(15, ModernUI.CARD_COLOR, true);
        form.setLayout(new GridLayout(2, 2, 10, 20));
        form.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField titleField = new JTextField();
        titleField.setFont(ModernUI.FONT_MAIN);
        JTextField descField = new JTextField();
        descField.setFont(ModernUI.FONT_MAIN);
        
        form.add(new JLabel("Exam Title:")); form.add(titleField);
        form.add(new JLabel("Description:")); form.add(descField);
        
        p.add(form, BorderLayout.CENTER);

        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        bp.setOpaque(false);
        
        JButton cancelBtn = new JButton("Cancel");
        ModernUI.styleButton(cancelBtn);
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton createBtn = new JButton("Create");
        ModernUI.stylePrimaryButton(createBtn);
        createBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            String desc = descField.getText().trim();
            if (title.isEmpty() || desc.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Title and Description cannot be empty!");
                return;
            }
            
            for (ExamPaper paper : catalog.getAvailablePapers()) {
                if (paper.getTitle().equalsIgnoreCase(title)) {
                    JOptionPane.showMessageDialog(dialog, "An exam with this title already exists!");
                    return;
                }
            }
            catalog.getAvailablePapers().add(new ExamPaper(title, desc));
            examDao.saveAllExams(catalog.getAvailablePapers());
            catalog.loadExams();
            JOptionPane.showMessageDialog(dialog, "Exam created successfully!");
            dialog.dispose();
        });

        bp.add(cancelBtn);
        bp.add(createBtn);
        p.add(bp, BorderLayout.SOUTH);
        
        dialog.setContentPane(p);
        dialog.setVisible(true);
    }

    private void editQuestion() {
        List<ExamPaper> papers = catalog.getAvailablePapers();
        if (papers == null || papers.isEmpty()) {
            JOptionPane.showMessageDialog(dashboardDialog, "No exams available!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // To keep logic identical but looking cleaner, we use JOptionPane for the selection
        // but with updated aesthetics for the core forms.
        UIManager.put("Panel.background", ModernUI.BG_COLOR);
        UIManager.put("OptionPane.background", ModernUI.BG_COLOR);
        
        ExamPaper[] paperArray = papers.toArray(new ExamPaper[0]);
        ExamPaper selectedPaper = (ExamPaper) JOptionPane.showInputDialog(dashboardDialog, "Select Exam Paper:",
                "Edit Question", JOptionPane.QUESTION_MESSAGE, null, paperArray, paperArray[0]);
        
        if (selectedPaper == null) return;

        List<Question> questions = selectedPaper.getQuestions();
        int size = (questions == null) ? 0 : questions.size();

        class QuestionWrapper {
            Question q;
            QuestionWrapper(Question q) { this.q = q; }
            @Override
            public String toString() {
                if (q == null) return "[➕ Add New Question]";
                String type = (q instanceof MultipleChoiceQuestion) ? "MCQ" : "TF";
                String desc = q.getDescription().replace("\n", " ");
                if (desc.length() > 50) desc = desc.substring(0, 47) + "...";
                return String.format("[%s] %s (%.1f pts)", type, desc, q.getMaxScore());
            }
        }

        QuestionWrapper[] qWrappers = new QuestionWrapper[size + 1];
        for (int i = 0; i < size; i++) qWrappers[i] = new QuestionWrapper(questions.get(i));
        qWrappers[size] = new QuestionWrapper(null);

        QuestionWrapper selectedWrapper = (QuestionWrapper) JOptionPane.showInputDialog(dashboardDialog, 
                "Select Question to Edit, or select Add New:", "Select Question",
                JOptionPane.QUESTION_MESSAGE, null, qWrappers, qWrappers[0]);
        
        if (selectedWrapper == null) return;
        Question selectedQuestion = selectedWrapper.q;

        if (selectedQuestion == null) {
            String[] qTypes = {"MCQ", "TF"};
            String type = (String) JOptionPane.showInputDialog(dashboardDialog, "Select Type:", "Type",
                    JOptionPane.QUESTION_MESSAGE, null, qTypes, qTypes[0]);
            if (type == null) return;

            if ("MCQ".equals(type)) openMCQForm(selectedPaper, null);
            else if ("TF".equals(type)) openTFForm(selectedPaper, null);
        } else {
            if (selectedQuestion instanceof MultipleChoiceQuestion) openMCQForm(selectedPaper, (MultipleChoiceQuestion) selectedQuestion);
            else if (selectedQuestion instanceof TrueFalseQuestion) openTFForm(selectedPaper, (TrueFalseQuestion) selectedQuestion);
        }
    }

    private void openMCQForm(ExamPaper paper, MultipleChoiceQuestion existing) {
        JDialog dialog = new JDialog(dashboardDialog, existing == null ? "Add MCQ" : "Edit MCQ", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(dashboardDialog);
        
        JPanel p = new JPanel(new BorderLayout(0, 20));
        p.setBorder(new EmptyBorder(20, 30, 20, 30));
        p.setBackground(ModernUI.BG_COLOR);

        RoundedPanel form = new RoundedPanel(15, ModernUI.CARD_COLOR, true);
        form.setLayout(new GridLayout(7, 2, 10, 15));
        form.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField qText = new JTextField(20);
        JTextField optA = new JTextField(20);
        JTextField optB = new JTextField(20);
        JTextField optC = new JTextField(20);
        JTextField optD = new JTextField(20);
        JComboBox<String> ansCombo = new JComboBox<>(new String[]{"A", "B", "C", "D"});
        JTextField scoreField = new JTextField("10.0", 20);

        if (existing != null) {
            String desc = existing.getDescription();
            // Parsing existing
            int idxA = desc.indexOf("\n(A) ");
            if (idxA != -1) {
                qText.setText(desc.substring(0, idxA).trim());
            } else {
                qText.setText(desc);
            }
            List<String> opts = existing.getOptions();
            if (opts.size() > 0) optA.setText(opts.get(0));
            if (opts.size() > 1) optB.setText(opts.get(1));
            if (opts.size() > 2) optC.setText(opts.get(2));
            if (opts.size() > 3) optD.setText(opts.get(3));
            ansCombo.setSelectedItem(existing.getCorrectAnswer().toUpperCase());
            scoreField.setText(String.valueOf(existing.getMaxScore()));
        }

        form.add(new JLabel("Question Text:")); form.add(qText);
        form.add(new JLabel("Option A:")); form.add(optA);
        form.add(new JLabel("Option B:")); form.add(optB);
        form.add(new JLabel("Option C:")); form.add(optC);
        form.add(new JLabel("Option D:")); form.add(optD);
        form.add(new JLabel("Correct Answer:")); form.add(ansCombo);
        form.add(new JLabel("Score:")); form.add(scoreField);

        p.add(form, BorderLayout.CENTER);

        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        bp.setOpaque(false);
        JButton cancelBtn = new JButton("Cancel"); ModernUI.styleButton(cancelBtn);
        JButton saveBtn = new JButton("Save"); ModernUI.stylePrimaryButton(saveBtn);
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        saveBtn.addActionListener(e -> {
            try {
                String fullDesc = qText.getText().trim() + "\n(A) " + optA.getText().trim() +
                                  "\n(B) " + optB.getText().trim() + "\n(C) " + optC.getText().trim() +
                                  "\n(D) " + optD.getText().trim();
                List<String> newOptions = java.util.Arrays.asList(optA.getText().trim(), optB.getText().trim(), optC.getText().trim(), optD.getText().trim());
                String answer = (String) ansCombo.getSelectedItem();
                double score = Double.parseDouble(scoreField.getText().trim());

                if (existing == null) {
                    Question q = new MultipleChoiceQuestion("Q" + System.currentTimeMillis(), fullDesc, score, newOptions, answer);
                    paper.addQuestion(q);
                } else {
                    int index = paper.getQuestions().indexOf(existing);
                    Question newQ = new MultipleChoiceQuestion(existing.getId(), fullDesc, score, newOptions, answer);
                    paper.getQuestions().set(index, newQ);
                }
                
                examDao.saveAllExams(catalog.getAvailablePapers());
                catalog.loadExams();
                JOptionPane.showMessageDialog(dialog, "Saved successfully!");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        bp.add(cancelBtn); bp.add(saveBtn);
        p.add(bp, BorderLayout.SOUTH);
        
        dialog.setContentPane(p);
        dialog.setVisible(true);
    }

    private void openTFForm(ExamPaper paper, TrueFalseQuestion existing) {
        JDialog dialog = new JDialog(dashboardDialog, existing == null ? "Add T/F" : "Edit T/F", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(dashboardDialog);
        
        JPanel p = new JPanel(new BorderLayout(0, 20));
        p.setBorder(new EmptyBorder(20, 30, 20, 30));
        p.setBackground(ModernUI.BG_COLOR);

        RoundedPanel form = new RoundedPanel(15, ModernUI.CARD_COLOR, true);
        form.setLayout(new GridLayout(3, 2, 10, 20));
        form.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField qText = new JTextField(20);
        JComboBox<String> ansCombo = new JComboBox<>(new String[]{"true", "false"});
        JTextField scoreField = new JTextField("10.0", 20);

        if (existing != null) {
            qText.setText(existing.getDescription());
            ansCombo.setSelectedItem(String.valueOf(existing.isCorrectAnswer()));
            scoreField.setText(String.valueOf(existing.getMaxScore()));
        }

        form.add(new JLabel("Question Text:")); form.add(qText);
        form.add(new JLabel("Correct Answer:")); form.add(ansCombo);
        form.add(new JLabel("Score:")); form.add(scoreField);

        p.add(form, BorderLayout.CENTER);

        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        bp.setOpaque(false);
        JButton cancelBtn = new JButton("Cancel"); ModernUI.styleButton(cancelBtn);
        JButton saveBtn = new JButton("Save"); ModernUI.stylePrimaryButton(saveBtn);
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        saveBtn.addActionListener(e -> {
            try {
                String fullDesc = qText.getText().trim();
                boolean answer = Boolean.parseBoolean((String) ansCombo.getSelectedItem());
                double score = Double.parseDouble(scoreField.getText().trim());

                if (existing == null) {
                    Question q = new TrueFalseQuestion("Q" + System.currentTimeMillis(), fullDesc, score, answer);
                    paper.addQuestion(q);
                } else {
                    int index = paper.getQuestions().indexOf(existing);
                    Question newQ = new TrueFalseQuestion(existing.getId(), fullDesc, score, answer);
                    paper.getQuestions().set(index, newQ);
                }
                
                examDao.saveAllExams(catalog.getAvailablePapers());
                catalog.loadExams();
                JOptionPane.showMessageDialog(dialog, "Saved successfully!");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        bp.add(cancelBtn); bp.add(saveBtn);
        p.add(bp, BorderLayout.SOUTH);
        
        dialog.setContentPane(p);
        dialog.setVisible(true);
    }
}
