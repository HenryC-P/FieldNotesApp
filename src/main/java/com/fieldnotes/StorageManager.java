package com.fieldnotes;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles persistence and export of FieldNote entries.
 * This class provides methods to save and load notes from a JSON file,
 * as well as exporting individual notes to Markdown files.
 */
public class StorageManager {
    private static final String DATA_FILE = "field_notes.json";

    /**
     * Loads field note entries from the local JSON storage file.
     * If the file does not exist or an error occurs, an empty list is returned.
     * @return a list of FieldNote entries
     */
    public List<FieldNote> loadEntries() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                String json = readAll(reader);
                if (json.trim().isEmpty()) {
                    return new ArrayList<>();
                }
                return parseEntries(json);
            } catch (Exception e) {
                System.err.println("Error loading entries: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    /**
     * Saves the provided list of field note entries to the local JSON storage file.
     * @param entries the list of FieldNote entries to save
     */
    public void saveEntries(List<FieldNote> entries) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(DATA_FILE), StandardCharsets.UTF_8)) {
            List<FieldNote> safeEntries = entries == null ? new ArrayList<>() : entries;
            writer.write("[");
            for (int i = 0; i < safeEntries.size(); i++) {
                if (i > 0) {
                    writer.write(",");
                }
                writer.write(toJson(safeEntries.get(i)));
            }
            writer.write("]");
        } catch (Exception e) {
            System.err.println("Error saving entries: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Exports a single field note entry to a Markdown file.
     * @param entry the FieldNote entry to export
     * @param file the destination file
     * @throws IOException if an I/O error occurs during writing
     */
    public void exportToMarkdown(FieldNote entry, File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.print(entry.toMarkdown());
        } catch (IOException e) {
            System.err.println("Error exporting to Markdown: " + e.getMessage());
            throw e;
        }
    }

    private static String readAll(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[4096];
        int read;
        while ((read = reader.read(buffer)) != -1) {
            sb.append(buffer, 0, read);
        }
        return sb.toString();
    }

    private static String toJson(FieldNote entry) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        appendField(sb, "date", entry.getDate());
        appendField(sb, "time", entry.getTime());
        appendField(sb, "location", entry.getLocation());
        appendField(sb, "setting", entry.getSetting());
        appendField(sb, "participants", entry.getParticipants());
        appendField(sb, "activities", entry.getActivities());
        appendField(sb, "sensory", entry.getSensory());
        appendField(sb, "reflections", entry.getReflections());
        appendField(sb, "culturalContext", entry.getCulturalContext());
        appendField(sb, "questions", entry.getQuestions());
        appendField(sb, "themes", entry.getThemes());
        sb.append("}");
        return sb.toString();
    }

    private static void appendField(StringBuilder sb, String name, String value) {
        if (sb.length() > 1) {
            sb.append(",");
        }
        sb.append("\"").append(name).append("\":").append(jsonString(value));
    }

    private static String jsonString(String value) {
        return "\"" + escapeJson(value == null ? "" : value) + "\"";
    }

    private static String escapeJson(String value) {
        StringBuilder sb = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\\':
                    sb.append("\\\\");
                    break;
                case '"':
                    sb.append("\\\"");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        sb.append("\\u");
                        String hex = Integer.toHexString(c);
                        for (int pad = hex.length(); pad < 4; pad++) {
                            sb.append('0');
                        }
                        sb.append(hex);
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    private static List<FieldNote> parseEntries(String json) throws IOException {
        JsonReader reader = new JsonReader(json);
        return reader.readEntries();
    }

    private static final class JsonReader {
        private final String input;
        private int index;

        private JsonReader(String input) {
            this.input = input;
        }

        private List<FieldNote> readEntries() throws IOException {
            skipWhitespace();
            if (index >= input.length()) {
                return new ArrayList<>();
            }
            expect('[');
            List<FieldNote> entries = new ArrayList<>();
            skipWhitespace();
            if (peek() == ']') {
                index++;
                return entries;
            }
            while (true) {
                entries.add(readEntry());
                skipWhitespace();
                char next = peek();
                if (next == ',') {
                    index++;
                    continue;
                }
                if (next == ']') {
                    index++;
                    break;
                }
                throw error("Expected ',' or ']'");
            }
            return entries;
        }

        private FieldNote readEntry() throws IOException {
            expect('{');
            Map<String, String> values = new HashMap<>();
            skipWhitespace();
            if (peek() == '}') {
                index++;
                return toFieldNote(values);
            }
            while (true) {
                String key = readString();
                expect(':');
                String value = readStringOrNull();
                values.put(key, value);
                skipWhitespace();
                char next = peek();
                if (next == ',') {
                    index++;
                    continue;
                }
                if (next == '}') {
                    index++;
                    break;
                }
                throw error("Expected ',' or '}'");
            }
            return toFieldNote(values);
        }

        private String readStringOrNull() throws IOException {
            skipWhitespace();
            if (match("null")) {
                index += 4;
                return null;
            }
            return readString();
        }

        private String readString() throws IOException {
            expect('"');
            StringBuilder sb = new StringBuilder();
            while (index < input.length()) {
                char c = input.charAt(index++);
                if (c == '"') {
                    return sb.toString();
                }
                if (c == '\\') {
                    if (index >= input.length()) {
                        throw error("Incomplete escape");
                    }
                    char esc = input.charAt(index++);
                    switch (esc) {
                        case '"':
                            sb.append('"');
                            break;
                        case '\\':
                            sb.append('\\');
                            break;
                        case '/':
                            sb.append('/');
                            break;
                        case 'b':
                            sb.append('\b');
                            break;
                        case 'f':
                            sb.append('\f');
                            break;
                        case 'n':
                            sb.append('\n');
                            break;
                        case 'r':
                            sb.append('\r');
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                        case 'u':
                            if (index + 4 > input.length()) {
                                throw error("Invalid unicode escape");
                            }
                            String hex = input.substring(index, index + 4);
                            index += 4;
                            try {
                                sb.append((char) Integer.parseInt(hex, 16));
                            } catch (NumberFormatException e) {
                                throw error("Invalid unicode escape");
                            }
                            break;
                        default:
                            throw error("Invalid escape");
                    }
                } else {
                    sb.append(c);
                }
            }
            throw error("Unterminated string");
        }

        private void expect(char expected) throws IOException {
            skipWhitespace();
            if (peek() != expected) {
                throw error("Expected '" + expected + "'");
            }
            index++;
        }

        private boolean match(String literal) {
            return input.startsWith(literal, index);
        }

        private char peek() {
            return index < input.length() ? input.charAt(index) : '\0';
        }

        private void skipWhitespace() {
            while (index < input.length() && Character.isWhitespace(input.charAt(index))) {
                index++;
            }
        }

        private IOException error(String message) {
            return new IOException(message + " at position " + index);
        }
    }

    private static FieldNote toFieldNote(Map<String, String> values) {
        FieldNote note = new FieldNote();
        note.setDate(valueOrEmpty(values, "date"));
        note.setTime(valueOrEmpty(values, "time"));
        note.setLocation(valueOrEmpty(values, "location"));
        note.setSetting(valueOrEmpty(values, "setting"));
        note.setParticipants(valueOrEmpty(values, "participants"));
        note.setActivities(valueOrEmpty(values, "activities"));
        note.setSensory(valueOrEmpty(values, "sensory"));
        note.setReflections(valueOrEmpty(values, "reflections"));
        note.setCulturalContext(valueOrEmpty(values, "culturalContext"));
        note.setQuestions(valueOrEmpty(values, "questions"));
        note.setThemes(valueOrEmpty(values, "themes"));
        return note;
    }

    private static String valueOrEmpty(Map<String, String> values, String key) {
        String value = values.get(key);
        return value == null ? "" : value;
    }
}
