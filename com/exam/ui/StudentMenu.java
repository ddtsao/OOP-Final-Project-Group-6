package com.exam.ui;

import com.exam.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class StudentMenu {
    private Student student;
    private ExamCatalog catalog;
    private ExamDao examDao;
    private ResultDao resultDao;
    private ExamService examService;

    public StudentMenu(Student student, ExamCatalog catalog, ExamDao examDao, ResultDao resultDao, ExamService examService) {
        this.student = student;
        this.catalog = catalog;
        this.examDao = examDao;
        this.resultDao = resultDao;
        this.examService = examService;
    }

    public void display() {
        List<ExamPaper> papers = catalog.getAvailablePapers();
        if (papers == null || papers.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No exam papers available at the moment!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame)null, "Student Dashboard", true);
        dialog.setSize(550, 400);
        dialog.setLocationRelativeTo(null);
        
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(ModernUI.BG_COLOR);
        contentPane.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Welcome, " + student.getName() + "!", SwingConstants.CENTER);
        titleLabel.setFont(ModernUI.FONT_TITLE);
        titleLabel.setForeground(ModernUI.TEXT_PRIMARY);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        contentPane.add(titleLabel, BorderLayout.NORTH);

        RoundedPanel mainCard = new RoundedPanel(20, ModernUI.CARD_COLOR, true);
        mainCard.setLayout(new GridBagLayout());
        mainCard.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel selectLabel = new JLabel("Please select an exam to start:");
        selectLabel.setFont(ModernUI.FONT_SUBTITLE);
        selectLabel.setForeground(ModernUI.TEXT_SECONDARY);
        selectLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainCard.add(selectLabel, gbc);

        ExamPaper[] paperArray = papers.toArray(new ExamPaper[0]);
        JComboBox<ExamPaper> examCombo = new JComboBox<>(paperArray);
        examCombo.setFont(ModernUI.FONT_MAIN);
        examCombo.setBackground(Color.WHITE);
        examCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(5, 5, 5, 5)
        ));
        mainCard.add(examCombo, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setOpaque(false);

        JButton cancelButton = new JButton("Cancel");
        ModernUI.styleButton(cancelButton);
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton startButton = new JButton("Start Exam");
        ModernUI.stylePrimaryButton(startButton);
        
        btnPanel.add(cancelButton);
        btnPanel.add(startButton);

        gbc.insets = new Insets(30, 0, 0, 0);
        mainCard.add(btnPanel, gbc);

        contentPane.add(mainCard, BorderLayout.CENTER);
        dialog.setContentPane(contentPane);

        startButton.addActionListener(e -> {
            ExamPaper selectedPaper = (ExamPaper) examCombo.getSelectedItem();
            if (selectedPaper != null) {
                dialog.dispose();
                startExamSession(selectedPaper);
            }
        });

        dialog.setVisible(true);
    }

    private void startExamSession(ExamPaper selectedPaper) {
        Instructor systemInstructor = new Instructor("SYS", "System");
        Exam exam = examService.createExam(systemInstructor, "E_" + System.currentTimeMillis(), selectedPaper.getTitle());
        for (Question q : selectedPaper.getQuestions()) {
            examService.addQuestion(exam, q);
        }

        ExamController controller = new ExamController(student, exam, examService, resultDao);
        ExamFrame frame = new ExamFrame(controller);
        controller.setView(frame);
        
        frame.setModal(true);
        controller.startExam();
        frame.setVisible(true); 
    }
}
