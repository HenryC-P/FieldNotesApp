package com.fieldnotes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Main application class for the Field Notes App.
 * This class provides a graphical user interface (GUI) using Swing for creating,
 * viewing, and managing field notes for ethnography coursework.
 */
public class FieldNotesApp extends JFrame {
    // Colors
    public static final Color TEAL = new Color(13, 148, 136);
    public static final Color ROSE = new Color(225, 29, 72);
    public static final Color AMBER = new Color(217, 119, 6);
    public static final Color BG_LIGHT = new Color(240, 253, 250);

    private final StorageManager storage;
    private List<FieldNote> entries;
    private final DefaultListModel<String> listModel;
    private JList<String> entryList;
    private int editingIndex = -1;

    // Panels
    private JPanel mainPanel;
    private JPanel viewPanel;
    private CardLayout cardLayout;
    private JLabel entriesLabel;

    // Form fields
    private JTextField dateField, timeField, locationField;
    private JTextArea settingArea, participantsArea, activitiesArea;
    private JTextArea sensoryArea, reflectionsArea;
    private JTextArea culturalContextArea, questionsArea, themesArea;

    /**
     * Entry point for the application.
     * Sets the system look and feel and launches the FieldNotesApp.
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new FieldNotesApp();
        });
    }

    /**
     * Constructs the FieldNotesApp, initializes storage, and sets up the GUI.
     */
    public FieldNotesApp() {
        storage = new StorageManager();
        entries = storage.loadEntries();
        listModel = new DefaultListModel<>();

        setTitle("Field Notes - COMM 1131");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 700);
        setLocationRelativeTo(null);

        initComponents();
        refreshList();

        setVisible(true);
    }

    /**
     * Initializes the UI components and layout.
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        add(createSidebar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.add(createFormPanel(), "form");
        mainPanel.add(createViewPanel(), "view");
        mainPanel.add(createGuidePanel(), "guide");
        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Creates the sidebar panel containing the entry list and navigation buttons.
     * @return the sidebar JPanel
     */
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(BG_LIGHT);
        sidebar.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Title section
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Field Notes");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(TEAL);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("COMM 1131");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(title);
        titlePanel.add(subtitle);
        titlePanel.add(Box.createVerticalStrut(20));

        // Buttons
        JButton newBtn = createButton("+ New Entry", TEAL, Color.WHITE);
        JButton guideBtn = createButton("Guide", new Color(100, 100, 100), Color.WHITE);

        newBtn.addActionListener(e -> {
            clearForm();
            editingIndex = -1;
            cardLayout.show(mainPanel, "form");
        });
        guideBtn.addActionListener(e -> cardLayout.show(mainPanel, "guide"));

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setOpaque(false);
        newBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        guideBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        newBtn.setMaximumSize(new Dimension(190, 40));
        guideBtn.setMaximumSize(new Dimension(190, 40));
        btnPanel.add(newBtn);
        btnPanel.add(Box.createVerticalStrut(8));
        btnPanel.add(guideBtn);
        btnPanel.add(Box.createVerticalStrut(20));

        // Entries label
        entriesLabel = new JLabel("Entries (" + entries.size() + ")");
        entriesLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        entriesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPanel.add(entriesLabel);
        btnPanel.add(Box.createVerticalStrut(8));

        titlePanel.add(btnPanel);

        // Entry list
        entryList = new JList<>(listModel);
        entryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        entryList.setFont(new Font("SansSerif", Font.PLAIN, 12));
        entryList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int idx = entryList.getSelectedIndex();
                    if (idx >= 0) showEntry(idx);
                }
            }
        });

        JScrollPane listScroll = new JScrollPane(entryList);
        listScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        sidebar.add(titlePanel, BorderLayout.NORTH);
        sidebar.add(listScroll, BorderLayout.CENTER);

        return sidebar;
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Creates the form panel for entering and editing field notes.
     * @return the form JPanel
     */
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        formPanel.setBackground(new Color(250, 250, 250));

        // Basic Info
        JPanel basicPanel = createSection("📝 Basic Info", null);
        JPanel basicFields = new JPanel(new GridLayout(1, 3, 15, 0));
        basicFields.setOpaque(false);

        dateField = new JTextField(LocalDate.now().toString());
        timeField = new JTextField(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        locationField = new JTextField();

        dateField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        timeField.setFont(new Font("SansSerif", Font.PLAIN, 14));

        basicFields.add(labeledField("Date * (YYYY-MM-DD)", dateField));
        basicFields.add(labeledField("Time (HH:MM)", timeField));
        basicFields.add(labeledField("Location *", locationField));
        basicPanel.add(basicFields);
        formPanel.add(basicPanel);
        formPanel.add(Box.createVerticalStrut(15));

        // Descriptive Details
        JPanel descPanel = createSection("👁️ Descriptive Details", TEAL);
        settingArea = createTextArea(3);
        participantsArea = createTextArea(3);
        activitiesArea = createTextArea(5);
        descPanel.add(labeledArea("Setting", settingArea, "Describe the environment—what does it look like? What's the vibe?"));
        descPanel.add(Box.createVerticalStrut(10));
        descPanel.add(labeledArea("Participants", participantsArea, "Who's there? Use pseudonyms to protect privacy."));
        descPanel.add(Box.createVerticalStrut(10));
        descPanel.add(labeledArea("Activities & Interactions", activitiesArea, "What's happening? Be specific—quote dialogue, describe body language."));
        formPanel.add(descPanel);
        formPanel.add(Box.createVerticalStrut(15));

        // Sensory & Personal
        JPanel sensoryPanel = createSection("💭 Sensory & Personal", ROSE);
        sensoryArea = createTextArea(3);
        reflectionsArea = createTextArea(5);
        sensoryPanel.add(labeledArea("Sensory Impressions", sensoryArea, "What do you hear, smell, feel? What's the emotional atmosphere?"));
        sensoryPanel.add(Box.createVerticalStrut(10));
        sensoryPanel.add(labeledArea("Personal Reflections", reflectionsArea, "How do you feel? What surprised you or made you uncomfortable?"));
        formPanel.add(sensoryPanel);
        formPanel.add(Box.createVerticalStrut(15));

        // Analysis
        JPanel analysisPanel = createSection("🔍 Analysis", AMBER);
        culturalContextArea = createTextArea(5);
        questionsArea = createTextArea(3);
        themesArea = createTextArea(3);
        analysisPanel.add(labeledArea("Cultural/Social Context", culturalContextArea, "What norms about sex, relationships, or communication are at play?"));
        analysisPanel.add(Box.createVerticalStrut(10));
        analysisPanel.add(labeledArea("Questions", questionsArea, "What questions come up? What would you explore further?"));
        analysisPanel.add(Box.createVerticalStrut(10));
        analysisPanel.add(labeledArea("Emerging Themes", themesArea, "Any patterns? Connections to course concepts?"));
        formPanel.add(analysisPanel);
        formPanel.add(Box.createVerticalStrut(20));

        // Save button
        JButton saveBtn = createButton("Save Entry", TEAL, Color.WHITE);
        saveBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        saveBtn.setMaximumSize(new Dimension(200, 45));
        saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveBtn.addActionListener(e -> saveEntry());

        JPanel saveBtnPanel = new JPanel();
        saveBtnPanel.setOpaque(false);
        saveBtnPanel.add(saveBtn);
        formPanel.add(saveBtnPanel);

        JScrollPane scroll = new JScrollPane(formPanel);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(null);

        JPanel container = new JPanel(new BorderLayout());
        container.add(scroll, BorderLayout.CENTER);
        return container;
    }

    private JPanel createSection(String title, Color accentColor) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                accentColor != null ? BorderFactory.createMatteBorder(0, 5, 0, 0, accentColor) : new EmptyBorder(0,0,0,0),
                new EmptyBorder(15, 20, 15, 20)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        if (accentColor != null) lbl.setForeground(accentColor);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(15));

        return panel;
    }

    private JPanel labeledField(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JPanel labeledArea(String label, JTextArea area, String placeholder) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        area.setToolTipText(placeholder);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(0, area.getRows() * 22 + 10));
        p.add(lbl, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JTextArea createTextArea(int rows) {
        JTextArea area = new JTextArea(rows, 40);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("SansSerif", Font.PLAIN, 14));
        area.setBorder(new EmptyBorder(8, 8, 8, 8));
        return area;
    }

    /**
     * Creates the view panel for displaying field note details.
     * @return the view JPanel
     */
    private JPanel createViewPanel() {
        viewPanel = new JPanel(new BorderLayout());
        viewPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        viewPanel.setBackground(new Color(250, 250, 250));
        return viewPanel;
    }

    /**
     * Creates the guide panel with tips for taking field notes.
     * @return the guide JPanel
     */
    private JPanel createGuidePanel() {
        JPanel guide = new JPanel();
        guide.setLayout(new BoxLayout(guide, BoxLayout.Y_AXIS));
        guide.setBorder(new EmptyBorder(25, 25, 25, 25));
        guide.setBackground(new Color(250, 250, 250));

        Object[][] sections = {
                {"What Are Field Notes?", TEAL,
                        "Field notes are detailed, contemporaneous records of observations and experiences. " +
                                "They document what you see, hear, feel, and think in a specific setting. In communication studies, " +
                                "field notes help you track how communication unfolds in real-life contexts, including interpersonal dynamics, " +
                                "cultural rituals, and unspoken norms.\n\n" +
                                "Field notes are not just descriptive; they are also reflective. This dual purpose allows you to:\n" +
                                "• Capture the objective details of a situation\n" +
                                "• Analyze and interpret those details through your subjective lens"},

                {"Why Field Notes Matter in Autoethnography", TEAL,
                        "Autoethnography bridges the personal and the cultural. Field notes are essential because they:\n\n" +
                                "• Anchor your essay in lived experience — They provide the raw data needed to analyze cultural norms and practices.\n" +
                                "• Highlight patterns and themes — Consistent note-taking helps you identify recurring ideas or behaviors.\n" +
                                "• Capture fleeting moments — Field notes preserve details that might otherwise fade from memory.\n\n" +
                                "By thoughtfully recording your observations, you'll have a rich repository of material to draw from when constructing your final essay."},

                {"Components of Effective Field Notes", AMBER,
                        "a. Descriptive Details\n" +
                                "• Setting: Describe the physical environment. Where are you? What does it look, smell, or feel like?\n" +
                                "• Participants: Note who is present. What are their roles or relationships?\n" +
                                "• Activities: Document actions and interactions. What is happening? Who is saying what?\n\n" +
                                "b. Sensory Impressions\n" +
                                "• Capture sounds, smells, textures, and other sensory details that add depth to your observations.\n\n" +
                                "c. Personal Reflections\n" +
                                "• Record your thoughts, feelings, and reactions in the moment. These reflections will help you connect your personal experiences to larger cultural contexts.\n\n" +
                                "d. Contextual Notes\n" +
                                "• Include any relevant background information. What cultural or social norms might be influencing this setting?\n\n" +
                                "e. Questions\n" +
                                "• Jot down questions that arise during your observations. These can guide further exploration or analysis."},

                {"Best Practices for Writing Field Notes", ROSE,
                        "a. Be Specific and Concrete\n" +
                                "Avoid vague descriptions. Instead of writing \"People were talking,\" detail the conversation: " +
                                "\"Two students debated the merits of direct versus indirect communication, raising their voices and gesturing emphatically.\"\n\n" +
                                "b. Write Immediately After Observing\n" +
                                "The sooner you write, the fresher your memory will be. Aim to capture as much detail as possible within a few hours of your observation.\n\n" +
                                "c. Use a Structured Format\n" +
                                "Organize your notes into sections:\n" +
                                "• Date and Location: Start with the basics.\n" +
                                "• Descriptive Observations: Log the \"what\" of the situation.\n" +
                                "• Reflections and Analysis: Add your \"why\" and \"how.\"\n" +
                                "• Emerging Themes: Highlight patterns or connections.\n\n" +
                                "d. Be Honest and Vulnerable\n" +
                                "Your reflections may include personal biases or discomfort. Acknowledge these feelings. They are part of the autoethnographic process.\n\n" +
                                "e. Balance Description and Reflection\n" +
                                "Your notes should mix factual details with personal insights. Avoid overloading one at the expense of the other."},

                {"Challenges and How to Overcome Them", AMBER,
                        "Staying Objective While Reflecting Subjectively\n" +
                                "• Strike a balance between capturing external details and internal reactions. Use clear distinctions, such as labeling reflections as \"personal notes.\"\n\n" +
                                "Avoiding Overgeneralization\n" +
                                "• Focus on specific moments or interactions rather than making broad statements. Let patterns emerge organically.\n\n" +
                                "Managing Ethical Considerations\n" +
                                "• Respect the privacy and boundaries of those you observe. Use pseudonyms or general descriptions to protect anonymity."},

                {"How to Use Field Notes in Your Autoethnography", TEAL,
                        "When you begin drafting your final essay, your field notes will:\n\n" +
                                "• Provide vivid examples — Use detailed observations to illustrate cultural norms or tensions.\n" +
                                "• Support theoretical connections — Link your experiences to relevant academic frameworks.\n" +
                                "• Reveal personal insights — Share how the observations impacted your understanding of the cultural context."},

                {"Quick Reference Checklist", ROSE,
                        "When in doubt, remember to note:\n\n" +
                                "• Date/Time\n" +
                                "• Descriptive details\n" +
                                "• Sensory impressions\n" +
                                "• Your reflections and interpretations\n\n" +
                                "By observing with intention, writing with detail, and reflecting deeply, you'll create a rich tapestry of insights " +
                                "that bridge personal and cultural understanding. Remember, the goal is not just to document what happened but to explore what it means."}
        };

        for (Object[] s : sections) {
            JPanel sec = new JPanel(new BorderLayout(0, 10));
            sec.setBackground(Color.WHITE);
            sec.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 5, 0, 0, (Color)s[1]),
                    new EmptyBorder(15, 20, 15, 20)
            ));
            sec.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel title = new JLabel((String)s[0]);
            title.setFont(new Font("SansSerif", Font.BOLD, 15));
            title.setForeground((Color)s[1]);

            JTextArea content = new JTextArea((String)s[2]);
            content.setEditable(false);
            content.setLineWrap(true);
            content.setWrapStyleWord(true);
            content.setFont(new Font("SansSerif", Font.PLAIN, 13));
            content.setBackground(Color.WHITE);
            content.setBorder(null);

            sec.add(title, BorderLayout.NORTH);
            sec.add(content, BorderLayout.CENTER);
            sec.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

            guide.add(sec);
            guide.add(Box.createVerticalStrut(15));
        }

        JScrollPane scroll = new JScrollPane(guide);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel container = new JPanel(new BorderLayout());
        container.add(scroll, BorderLayout.CENTER);
        return container;
    }

    /**
     * Shows the details of a field note entry in the view panel.
     * @param index the index of the entry to show
     */
    private void showEntry(int index) {
        FieldNote entry = entries.get(index);
        viewPanel.removeAll();

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel title = new JLabel(entry.getLocation());
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(TEAL);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel date = new JLabel(entry.getDate() + (entry.getTime().isEmpty() ? "" : " • " + entry.getTime()));
        date.setFont(new Font("SansSerif", Font.PLAIN, 13));
        date.setForeground(Color.GRAY);
        date.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(title);
        content.add(Box.createVerticalStrut(5));
        content.add(date);
        content.add(Box.createVerticalStrut(25));

        if (!entry.getSetting().isEmpty()) addViewSection(content, "Setting", entry.getSetting(), TEAL);
        if (!entry.getParticipants().isEmpty()) addViewSection(content, "Participants", entry.getParticipants(), TEAL);
        if (!entry.getActivities().isEmpty()) addViewSection(content, "Activities", entry.getActivities(), TEAL);
        if (!entry.getSensory().isEmpty()) addViewSection(content, "Sensory Impressions", entry.getSensory(), ROSE);
        if (!entry.getReflections().isEmpty()) addViewSection(content, "Personal Reflections", entry.getReflections(), ROSE);
        if (!entry.getCulturalContext().isEmpty()) addViewSection(content, "Cultural Context", entry.getCulturalContext(), AMBER);
        if (!entry.getQuestions().isEmpty()) addViewSection(content, "Questions", entry.getQuestions(), AMBER);
        if (!entry.getThemes().isEmpty()) addViewSection(content, "Emerging Themes", entry.getThemes(), AMBER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton editBtn = createButton("Edit", TEAL, Color.WHITE);
        JButton exportBtn = createButton("Export", new Color(100, 100, 100), Color.WHITE);
        JButton deleteBtn = createButton("Delete", ROSE, Color.WHITE);

        editBtn.addActionListener(e -> { loadEntryToForm(index); cardLayout.show(mainPanel, "form"); });
        exportBtn.addActionListener(e -> exportEntry(entry));
        deleteBtn.addActionListener(e -> {
            if (confirmDelete()) {
                entries.remove(index);
                storage.saveEntries(entries);
                refreshList();
                clearForm();
                cardLayout.show(mainPanel, "form");
            }
        });

        btnPanel.add(editBtn);
        btnPanel.add(exportBtn);
        btnPanel.add(deleteBtn);

        content.add(Box.createVerticalStrut(25));
        content.add(btnPanel);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        viewPanel.add(scroll, BorderLayout.CENTER);
        viewPanel.revalidate();
        viewPanel.repaint();

        cardLayout.show(mainPanel, "view");
    }

    private void addViewSection(JPanel parent, String title, String text, Color color) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(color);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("SansSerif", Font.PLAIN, 14));
        area.setBackground(new Color(250, 250, 250));
        area.setBorder(new EmptyBorder(5, 0, 20, 0));
        area.setAlignmentX(Component.LEFT_ALIGNMENT);

        parent.add(lbl);
        parent.add(area);
    }

    private void loadEntryToForm(int index) {
        FieldNote e = entries.get(index);
        editingIndex = index;
        dateField.setText(e.getDate());
        timeField.setText(e.getTime());
        locationField.setText(e.getLocation());
        settingArea.setText(e.getSetting());
        participantsArea.setText(e.getParticipants());
        activitiesArea.setText(e.getActivities());
        sensoryArea.setText(e.getSensory());
        reflectionsArea.setText(e.getReflections());
        culturalContextArea.setText(e.getCulturalContext());
        questionsArea.setText(e.getQuestions());
        themesArea.setText(e.getThemes());
    }

    private void clearForm() {
        dateField.setText(LocalDate.now().toString());
        timeField.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        locationField.setText("");
        settingArea.setText("");
        participantsArea.setText("");
        activitiesArea.setText("");
        sensoryArea.setText("");
        reflectionsArea.setText("");
        culturalContextArea.setText("");
        questionsArea.setText("");
        themesArea.setText("");
        editingIndex = -1;
    }

    /**
     * Saves the current entry from the form fields to the storage.
     * If editing an existing entry, it updates it; otherwise, it creates a new entry.
     */
    private void saveEntry() {
        if (dateField.getText().trim().isEmpty() || locationField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in date and location.", "Missing Fields", JOptionPane.WARNING_MESSAGE);
            return;
        }

        FieldNote note = new FieldNote();
        note.setDate(dateField.getText().trim());
        note.setTime(timeField.getText().trim());
        note.setLocation(locationField.getText().trim());
        note.setSetting(settingArea.getText().trim());
        note.setParticipants(participantsArea.getText().trim());
        note.setActivities(activitiesArea.getText().trim());
        note.setSensory(sensoryArea.getText().trim());
        note.setReflections(reflectionsArea.getText().trim());
        note.setCulturalContext(culturalContextArea.getText().trim());
        note.setQuestions(questionsArea.getText().trim());
        note.setThemes(themesArea.getText().trim());

        if (editingIndex >= 0) {
            entries.set(editingIndex, note);
        } else {
            entries.add(note);
        }

        storage.saveEntries(entries);
        refreshList();
        clearForm();
        JOptionPane.showMessageDialog(this, "Entry saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Exports the given field note to a Markdown file chosen by the user.
     * @param entry the field note to export
     */
    private void exportEntry(FieldNote entry) {
        JFileChooser chooser = new JFileChooser();
        String filename = "field-note-" + entry.getDate() + "-" + entry.getLocation().replaceAll("[^a-zA-Z0-9]", "-") + ".md";
        chooser.setSelectedFile(new File(filename));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                storage.exportToMarkdown(entry, chooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Exported successfully!", "Export", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean confirmDelete() {
        return JOptionPane.showConfirmDialog(this, "Delete this entry?", "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    /**
     * Refreshes the entries list display in the sidebar.
     */
    private void refreshList() {
        listModel.clear();
        for (FieldNote e : entries) {
            listModel.addElement(e.getListDisplay());
        }
        entriesLabel.setText("Entries (" + entries.size() + ")");
    }
}