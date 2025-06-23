#CODE


package S_manager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class SnippetManager extends JFrame implements ActionListener {

    // ✅ Inner Class: Snippet
    static class Snippet implements Serializable {
        String title, language, code, tags;

        public Snippet(String title, String language, String code, String tags) {
            this.title = title;
            this.language = language;
            this.code = code;
            this.tags = tags;
        }

        public String toString() {
            return "Title: " + title + "\nLanguage: " + language + "\nTags: " + tags + "\nCode:\n" + code + "\n";
        }
    }

    JTextField titleField, tagField;
    JComboBox<String> languageBox;
    JTextArea codeArea;
    JButton saveButton, viewButton, searchButton;
    JButton deleteButton, editButton, tagSearchButton;

    static ArrayList<Snippet> snippets = new ArrayList<>();
    final String FILE_NAME = "snippets.dat";

    public SnippetManager() {
        setTitle("Code Snippet Manager");
        setSize(500, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel topPanel = new JPanel(new GridLayout(4, 2));

        topPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        topPanel.add(titleField);

        topPanel.add(new JLabel("Language:"));
        String[] languages = {"Java", "Python", "C++", "JavaScript"};
        languageBox = new JComboBox<>(languages);
        topPanel.add(languageBox);

        topPanel.add(new JLabel("Tags:"));
        tagField = new JTextField();
        topPanel.add(tagField);

        add(topPanel, BorderLayout.NORTH);

        codeArea = new JTextArea();
        add(new JScrollPane(codeArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        // Main buttons
        saveButton = new JButton("Save Snippet");
        viewButton = new JButton("View All Snippets");
        searchButton = new JButton("Search by Title");

        // 🔸 New buttons
        deleteButton = new JButton("Delete Snippet");
        editButton = new JButton("Edit Snippet");
        tagSearchButton = new JButton("Search by Tag");

        // Add listeners
        saveButton.addActionListener(this);
        viewButton.addActionListener(this);
        searchButton.addActionListener(this);
        deleteButton.addActionListener(this);
        editButton.addActionListener(this);
        tagSearchButton.addActionListener(this);

        // Add to panel
        buttonPanel.add(saveButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(tagSearchButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadSnippets();
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            String title = titleField.getText();
            String language = (String) languageBox.getSelectedItem();
            String tags = tagField.getText();
            String code = codeArea.getText();

            if (title.isEmpty() || code.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title and Code are required.");
                return;
            }

            snippets.add(new Snippet(title, language, code, tags));
            saveSnippets();
            JOptionPane.showMessageDialog(this, "Snippet saved successfully.");
            clearFields();

        } else if (e.getSource() == viewButton) {
            StringBuilder sb = new StringBuilder();
            for (Snippet s : snippets) {
                sb.append(s).append("\n---\n");
            }
            JTextArea outputArea = new JTextArea(sb.toString());
            outputArea.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(outputArea), "All Snippets", JOptionPane.INFORMATION_MESSAGE);

        } else if (e.getSource() == searchButton) {
            String searchTitle = JOptionPane.showInputDialog(this, "Enter title to search:");
            StringBuilder sb = new StringBuilder();
            for (Snippet s : snippets) {
                if (s.title.equalsIgnoreCase(searchTitle)) {
                    sb.append(s).append("\n---\n");
                }
            }
            if (sb.length() == 0) {
                JOptionPane.showMessageDialog(this, "No snippet found with that title.");
            } else {
                JTextArea outputArea = new JTextArea(sb.toString());
                outputArea.setEditable(false);
                JOptionPane.showMessageDialog(this, new JScrollPane(outputArea), "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }

        } else if (e.getSource() == deleteButton) {
            String delTitle = JOptionPane.showInputDialog(this, "Enter title to delete:");
            boolean removed = snippets.removeIf(s -> s.title.equalsIgnoreCase(delTitle));
            if (removed) {
                saveSnippets();
                JOptionPane.showMessageDialog(this, "Snippet deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "No snippet found with that title.");
            }

        } else if (e.getSource() == editButton) {
            String editTitle = JOptionPane.showInputDialog(this, "Enter title to edit:");
            for (Snippet s : snippets) {
                if (s.title.equalsIgnoreCase(editTitle)) {
                    String newCode = JOptionPane.showInputDialog(this, "Enter new code:");
                    String newTags = JOptionPane.showInputDialog(this, "Enter new tags:");
                    s.code = newCode != null ? newCode : s.code;
                    s.tags = newTags != null ? newTags : s.tags;
                    saveSnippets();
                    JOptionPane.showMessageDialog(this, "Snippet updated successfully.");
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "No snippet found with that title.");

        } else if (e.getSource() == tagSearchButton) {
            String tag = JOptionPane.showInputDialog(this, "Enter tag to search:");
            StringBuilder sb = new StringBuilder();
            for (Snippet s : snippets) {
                if (s.tags != null && Arrays.asList(s.tags.toLowerCase().split(",")).contains(tag.toLowerCase())) {
                    sb.append(s).append("\n---\n");
                }
            }
            if (sb.length() == 0) {
                JOptionPane.showMessageDialog(this, "No snippets found with that tag.");
            } else {
                JTextArea outputArea = new JTextArea(sb.toString());
                outputArea.setEditable(false);
                JOptionPane.showMessageDialog(this, new JScrollPane(outputArea), "Search by Tag", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    void clearFields() {
        titleField.setText("");
        tagField.setText("");
        codeArea.setText("");
    }

    void saveSnippets() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(snippets);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void loadSnippets() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            snippets = (ArrayList<Snippet>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new SnippetManager();
    }
}
