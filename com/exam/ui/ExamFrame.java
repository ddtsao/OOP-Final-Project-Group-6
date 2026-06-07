package com.exam.ui;

import com.exam.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ExamFrame extends JDialog {
    private ExamController controller;
    private JPanel sidebarPanel;
    private JLabel progressLabel;
    private JTextArea questionArea;
    private JPanel optionsPanel;
    private ButtonGroup buttonGroup;
    private JButton nextButton;
    private JButton prevButton;
    private JProgressBar pBar;

    public ExamFrame(ExamController controller) {
        this.controller = controller;
        setupUI();
    }

    private void setupUI() {
        setTitle("Java Online Exam System");
        setSize(950, 650);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(ModernUI.BG_COLOR);

        // Sidebar
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(ModernUI.BG_COLOR);
        sidebarPanel.setBorder(new EmptyBorder(30, 20, 20, 20));

        JLabel brandLabel = new JLabel("☕");
        brandLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        brandLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(brandLabel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        mainContainer.add(sidebarPanel, BorderLayout.WEST);

        // Right Main Area
        JPanel rightArea = new JPanel(new BorderLayout(0, 20));
        rightArea.setBackground(ModernUI.BG_COLOR);
        rightArea.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top Info
        RoundedPanel topPanel = new RoundedPanel(15, ModernUI.CARD_COLOR, true);
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Java Online Exam System");
        titleLabel.setFont(ModernUI.FONT_SUBTITLE);
        titleLabel.setForeground(ModernUI.TEXT_PRIMARY);
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel progressWrapper = new JPanel(new BorderLayout(15, 0));
        progressWrapper.setOpaque(false);
        progressWrapper.setBorder(new EmptyBorder(15, 0, 0, 0));

        progressLabel = new JLabel("Question 1 of N");
        progressLabel.setFont(ModernUI.FONT_MAIN);
        progressLabel.setForeground(ModernUI.TEXT_SECONDARY);

        pBar = new JProgressBar(0, 100);
        pBar.setValue(0);
        pBar.setPreferredSize(new Dimension(200, 8));
        pBar.setForeground(ModernUI.PRIMARY_COLOR);
        pBar.setBorderPainted(false);
        pBar.setBackground(new Color(220, 220, 220));

        progressWrapper.add(progressLabel, BorderLayout.WEST);
        progressWrapper.add(pBar, BorderLayout.CENTER);
        
        topPanel.add(progressWrapper, BorderLayout.SOUTH);
        rightArea.add(topPanel, BorderLayout.NORTH);

        // Question Content
        JPanel centerWrapper = new JPanel(new BorderLayout(0, 20));
        centerWrapper.setOpaque(false);

        RoundedPanel questionCard = new RoundedPanel(15, ModernUI.CARD_COLOR, true);
        questionCard.setLayout(new BorderLayout());
        questionCard.setBorder(new EmptyBorder(25, 25, 25, 25));

        questionArea = new JTextArea();
        questionArea.setFont(ModernUI.FONT_TITLE);
        questionArea.setForeground(ModernUI.TEXT_PRIMARY);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionArea.setEditable(false);
        questionArea.setOpaque(false);
        questionCard.add(questionArea, BorderLayout.CENTER);

        centerWrapper.add(questionCard, BorderLayout.NORTH);

        // Options
        optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setOpaque(false);

        JScrollPane scrollOptions = new JScrollPane(optionsPanel);
        scrollOptions.setBorder(null);
        scrollOptions.setOpaque(false);
        scrollOptions.getViewport().setOpaque(false);

        centerWrapper.add(scrollOptions, BorderLayout.CENTER);
        rightArea.add(centerWrapper, BorderLayout.CENTER);

        // Bottom Controls
        RoundedPanel bottomPanel = new RoundedPanel(15, ModernUI.CARD_COLOR, true);
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 10));

        prevButton = new JButton("← Previous");
        ModernUI.styleButton(prevButton);
        prevButton.addActionListener(e -> {
            ButtonModel selected = buttonGroup.getSelection();
            String ans = selected != null ? selected.getActionCommand() : null;
            controller.previousQuestion(ans);
        });

        nextButton = new JButton("Next →");
        ModernUI.stylePrimaryButton(nextButton);
        nextButton.addActionListener(e -> {
            ButtonModel selected = buttonGroup.getSelection();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select an answer!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            controller.submitAnswerAndNext(selected.getActionCommand());
        });

        bottomPanel.add(prevButton);
        bottomPanel.add(nextButton);
        rightArea.add(bottomPanel, BorderLayout.SOUTH);

        mainContainer.add(rightArea, BorderLayout.CENTER);
        setContentPane(mainContainer);
    }

    public void displayQuestion(Question q, int currentIndex, int total, boolean isLast) {
        progressLabel.setText(String.format("Question %d of %d", currentIndex, total));
        questionArea.setText(q.getDescription());
        
        pBar.setValue((int) (((double) currentIndex / total) * 100));
        prevButton.setVisible(currentIndex > 1);

        // Update Sidebar Indicators
        sidebarPanel.removeAll();
        JLabel brandLabel = new JLabel("☕");
        brandLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        brandLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(brandLabel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        for (int i = 1; i <= total; i++) {
            JLabel numLabel = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            numLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            numLabel.setOpaque(true);
            numLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            if (i == currentIndex) {
                numLabel.setBackground(new Color(171, 130, 184)); // Purple tint
                numLabel.setForeground(Color.WHITE);
                numLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            } else {
                numLabel.setBackground(ModernUI.BG_COLOR);
                numLabel.setForeground(ModernUI.TEXT_SECONDARY);
                numLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            }
            sidebarPanel.add(numLabel);
            sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        sidebarPanel.revalidate();
        sidebarPanel.repaint();

        // Update Options (Radio Buttons instead of JTextField)
        optionsPanel.removeAll();
        buttonGroup = new ButtonGroup();
        String savedAns = controller.getSavedAnswer();

        if (q instanceof MultipleChoiceQuestion) {
            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) q;
            List<String> opts = mcq.getOptions();
            String[] labels = { "A", "B", "C", "D", "E", "F" };
            for (int i = 0; i < opts.size(); i++) {
                String letter = (i < labels.length) ? labels[i] : "?";
                String text = opts.get(i);
                addOptionButton(text, letter, savedAns);
            }
        } else if (q instanceof TrueFalseQuestion) {
            addOptionButton("True", "true", savedAns);
            addOptionButton("False", "false", savedAns);
        } else {
            addOptionButton("Yes", "yes", savedAns);
            addOptionButton("No", "no", savedAns);
        }

        optionsPanel.revalidate();
        optionsPanel.repaint();

        if (isLast) {
            nextButton.setText("Submit Exam");
            nextButton.setBackground(new Color(39, 174, 96));
        } else {
            nextButton.setText("Next →");
            nextButton.setBackground(ModernUI.PRIMARY_COLOR);
        }
    }

    private void addOptionButton(String text, String command, String savedAns) {
        RoundedPanel p = new RoundedPanel(10, ModernUI.CARD_COLOR, true);
        p.setLayout(new BorderLayout());
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225), 1),
                new EmptyBorder(12, 15, 12, 15)));
        p.setMaximumSize(new Dimension(800, 50));
        p.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JRadioButton rb = new JRadioButton(text);
        rb.setFont(ModernUI.FONT_MAIN);
        rb.setForeground(ModernUI.TEXT_PRIMARY);
        rb.setOpaque(false);
        rb.setFocusPainted(false);
        rb.setActionCommand(command);
        rb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (command.equals(savedAns)) {
            rb.setSelected(true);
        }

        p.add(rb, BorderLayout.CENTER);

        // Make the whole panel clickable to select the radio button
        p.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                rb.setSelected(true);
            }

            public void mouseEntered(java.awt.event.MouseEvent e) {
                p.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ModernUI.PRIMARY_COLOR, 1),
                        new EmptyBorder(12, 15, 12, 15)));
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                p.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(225, 225, 225), 1),
                        new EmptyBorder(12, 15, 12, 15)));
            }
        });

        buttonGroup.add(rb);
        optionsPanel.add(p);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    public void displayResult(Result result) {
        String message = String.format("Exam Finished!\n\nYour Total Score: %.1f\nFeedback: %s",
                result.getFinalScore(), result.getFeedback());
        int icon = result.getFinalScore() >= 60 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE;

        // Clean dialog for results
        JOptionPane.showMessageDialog(this, message, "Exam Result", icon);
        this.dispose();
    }
}
