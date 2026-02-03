package com.fieldnotes;

/**
 * Represents a single field note entry with various ethnographic observation fields.
 * This class stores data related to observations, including date, location, participants,
 * and sensory impressions, and provides a method to export the data to Markdown format.
 */
public class FieldNote {
    private String date;
    private String time;
    private String location;
    private String setting;
    private String participants;
    private String activities;
    private String sensory;
    private String reflections;
    private String culturalContext;
    private String questions;
    private String themes;

    /**
     * Constructs a new FieldNote with all fields initialized to empty strings.
     */
    public FieldNote() {
        this.date = "";
        this.time = "";
        this.location = "";
        this.setting = "";
        this.participants = "";
        this.activities = "";
        this.sensory = "";
        this.reflections = "";
        this.culturalContext = "";
        this.questions = "";
        this.themes = "";
    }

    /**
     * Gets the date of the observation.
     * @return the date string
     */
    public String getDate() { return date; }

    /**
     * Sets the date of the observation.
     * @param date the date string to set
     */
    public void setDate(String date) { this.date = date; }

    /**
     * Gets the time of the observation.
     * @return the time string
     */
    public String getTime() { return time; }

    /**
     * Sets the time of the observation.
     * @param time the time string to set
     */
    public void setTime(String time) { this.time = time; }

    /**
     * Gets the location of the observation.
     * @return the location string
     */
    public String getLocation() { return location; }

    /**
     * Sets the location of the observation.
     * @param location the location string to set
     */
    public void setLocation(String location) { this.location = location; }

    /**
     * Gets the physical and social setting description.
     * @return the setting string
     */
    public String getSetting() { return setting; }

    /**
     * Sets the physical and social setting description.
     * @param setting the setting string to set
     */
    public void setSetting(String setting) { this.setting = setting; }

    /**
     * Gets the participants involved in the observation.
     * @return the participants string
     */
    public String getParticipants() { return participants; }

    /**
     * Sets the participants involved in the observation.
     * @param participants the participants string to set
     */
    public void setParticipants(String participants) { this.participants = participants; }

    /**
     * Gets the activities and interactions observed.
     * @return the activities string
     */
    public String getActivities() { return activities; }

    /**
     * Sets the activities and interactions observed.
     * @param activities the activities string to set
     */
    public void setActivities(String activities) { this.activities = activities; }

    /**
     * Gets the sensory impressions (sights, sounds, smells, etc.).
     * @return the sensory string
     */
    public String getSensory() { return sensory; }

    /**
     * Sets the sensory impressions.
     * @param sensory the sensory string to set
     */
    public void setSensory(String sensory) { this.sensory = sensory; }

    /**
     * Gets the personal reflections of the observer.
     * @return the reflections string
     */
    public String getReflections() { return reflections; }

    /**
     * Sets the personal reflections.
     * @param reflections the reflections string to set
     */
    public void setReflections(String reflections) { this.reflections = reflections; }

    /**
     * Gets the cultural and social context of the observation.
     * @return the cultural context string
     */
    public String getCulturalContext() { return culturalContext; }

    /**
     * Sets the cultural and social context.
     * @param culturalContext the cultural context string to set
     */
    public void setCulturalContext(String culturalContext) { this.culturalContext = culturalContext; }

    /**
     * Gets the questions that arose during observation.
     * @return the questions string
     */
    public String getQuestions() { return questions; }

    /**
     * Sets the questions.
     * @param questions the questions string to set
     */
    public void setQuestions(String questions) { this.questions = questions; }

    /**
     * Gets the emerging themes from the observation.
     * @return the themes string
     */
    public String getThemes() { return themes; }

    /**
     * Sets the emerging themes.
     * @param themes the themes string to set
     */
    public void setThemes(String themes) { this.themes = themes; }

    /**
     * Returns a string representation for display in a list.
     * @return a formatted string containing date and location
     */
    public String getListDisplay() {
        return date + " - " + location;
    }

    /**
     * Converts the field note into a formatted Markdown string.
     * @return the Markdown representation of the field note
     */
    public String toMarkdown() {
        StringBuilder sb = new StringBuilder();
        sb.append("# Field Note: ").append(location).append("\n\n");
        sb.append("**Date:** ").append(date);
        if (!time.isEmpty()) sb.append(" • ").append(time);
        sb.append("\n**Course:** COMM 1131\n\n---\n\n");

        if (!setting.isEmpty()) sb.append("## Setting\n").append(setting).append("\n\n");
        if (!participants.isEmpty()) sb.append("## Participants\n").append(participants).append("\n\n");
        if (!activities.isEmpty()) sb.append("## Activities & Interactions\n").append(activities).append("\n\n");
        if (!sensory.isEmpty()) sb.append("## Sensory Impressions\n").append(sensory).append("\n\n");
        if (!reflections.isEmpty()) sb.append("## Personal Reflections\n").append(reflections).append("\n\n");
        if (!culturalContext.isEmpty()) sb.append("## Cultural/Social Context\n").append(culturalContext).append("\n\n");
        if (!questions.isEmpty()) sb.append("## Questions\n").append(questions).append("\n\n");
        if (!themes.isEmpty()) sb.append("## Emerging Themes\n").append(themes).append("\n");

        return sb.toString();
    }
}