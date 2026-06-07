package com.exam.ui;

import com.exam.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

class ModernUI {
    public static final Color BG_COLOR = new Color(234, 241, 251);
    public static final Color CARD_COLOR = Color.WHITE;
    public static final Color PRIMARY_COLOR = new Color(66, 133, 244);
    public static final Color TEXT_PRIMARY = new Color(32, 33, 36);
    public static final Color TEXT_SECONDARY = new Color(95, 99, 104);
    
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.BOLD, 18);
    public static final Font FONT_MAIN = new Font("SansSerif", Font.PLAIN, 14);

    public static void styleButton(JButton btn) {
        btn.setFont(FONT_MAIN);
        btn.setBackground(Color.WHITE);
        btn.setForeground(TEXT_PRIMARY);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true),
            BorderFactory.createEmptyBorder(8, 24, 8, 24)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(248, 249, 250)); }
            public void mouseExited(MouseEvent e) { btn.setBackground(Color.WHITE); }
        });
    }
    
    public static void stylePrimaryButton(JButton btn) {
        btn.setFont(FONT_MAIN);
        btn.setBackground(PRIMARY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(51, 103, 214)); }
            public void mouseExited(MouseEvent e) { btn.setBackground(PRIMARY_COLOR); }
        });
    }
}

class RoundedPanel extends JPanel {
    private int radius;
    private boolean drawShadow;
    private Color bgColor;

    public RoundedPanel(int radius, Color bgColor, boolean drawShadow) {
        super();
        this.radius = radius;
        this.bgColor = bgColor;
        this.drawShadow = drawShadow;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (drawShadow) {
            g2.setColor(new Color(0, 0, 0, 15));
            g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, radius, radius);
        }

        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, 
                         drawShadow ? getWidth() - 4 : getWidth(), 
                         drawShadow ? getHeight() - 4 : getHeight(), 
                         radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}

public class MainMenu {
    private ExamCatalog catalog;
    private ExamDao examDao;
    private ResultDao resultDao;
    private ExamService examService;
    private JFrame frame;

    public MainMenu(ExamCatalog catalog, ExamDao examDao, ResultDao resultDao, ExamService examService) {
        this.catalog = catalog;
        this.examDao = examDao;
        this.resultDao = resultDao;
        this.examService = examService;
    }

    public void start() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        frame = new JFrame("Java Online Exam System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 650);
        frame.setLocationRelativeTo(null);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(ModernUI.BG_COLOR);
        contentPane.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Java Online Exam System - Main Menu", SwingConstants.CENTER);
        titleLabel.setFont(ModernUI.FONT_TITLE);
        titleLabel.setForeground(ModernUI.TEXT_PRIMARY);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        contentPane.add(titleLabel, BorderLayout.NORTH);

        RoundedPanel mainCard = new RoundedPanel(20, ModernUI.CARD_COLOR, true);
        mainCard.setLayout(new GridBagLayout());
        mainCard.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 40, 0);

        // Icon placeholder
        JLabel iconLabel = new JLabel("🎓", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        mainCard.add(iconLabel, gbc);

        gbc.insets = new Insets(0, 0, 30, 0);
        JLabel welcomeLabel = new JLabel("Welcome. Please select your role to continue:");
        welcomeLabel.setFont(ModernUI.FONT_SUBTITLE);
        welcomeLabel.setForeground(ModernUI.TEXT_SECONDARY);
        mainCard.add(welcomeLabel, gbc);

        JPanel rolePanel = new JPanel(new GridLayout(1, 2, 30, 0));
        rolePanel.setOpaque(false);

        RoundedPanel studentCard = createRoleCard("🧑‍🎓", "Student", "Start and manage your exams");
        RoundedPanel instructorCard = createRoleCard("👨‍🏫", "Instructor", "View results, add and edit questions.");

        studentCard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                String name = JOptionPane.showInputDialog(frame, "Enter your name:");
                if (name != null && !name.trim().isEmpty()) {
                    User student = new Student("S_" + System.currentTimeMillis(), name);
                    frame.setVisible(false);
                    student.executeRole(catalog, examDao, resultDao, examService);
                    frame.setVisible(true);
                }
            }
        });

        instructorCard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                User instructor = new Instructor("I_ADMIN", "Admin Instructor");
                frame.setVisible(false);
                instructor.executeRole(catalog, examDao, resultDao, examService);
                frame.setVisible(true);
            }
        });

        rolePanel.add(studentCard);
        rolePanel.add(instructorCard);

        gbc.insets = new Insets(0, 0, 40, 0);
        mainCard.add(rolePanel, gbc);

        JButton quitButton = new JButton("Quit");
        ModernUI.styleButton(quitButton);
        quitButton.addActionListener(e -> System.exit(0));
        gbc.insets = new Insets(0, 0, 0, 0);
        mainCard.add(quitButton, gbc);

        contentPane.add(mainCard, BorderLayout.CENTER);
        frame.setContentPane(contentPane);
        frame.setVisible(true);
    }

    private RoundedPanel createRoleCard(String icon, String role, String desc) {
        RoundedPanel card = new RoundedPanel(15, ModernUI.CARD_COLOR, true);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(30, 20, 30, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setPreferredSize(new Dimension(200, 200));

        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel roleLabel = new JLabel(role, SwingConstants.CENTER);
        roleLabel.setFont(ModernUI.FONT_SUBTITLE);
        roleLabel.setForeground(ModernUI.TEXT_PRIMARY);
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel = new JLabel("<html><div style='text-align: center;'>" + desc + "</div></html>", SwingConstants.CENTER);
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descLabel.setForeground(ModernUI.TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(iconLabel);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(roleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(descLabel);
        card.add(Box.createVerticalGlue());

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ModernUI.PRIMARY_COLOR, 2),
                    new EmptyBorder(29, 19, 29, 19)
                ));
            }
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                    new EmptyBorder(30, 20, 30, 20)
                ));
            }
        });

        return card;
    }
}
