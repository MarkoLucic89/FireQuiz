package com.example.firequiz.models;

import com.google.firebase.firestore.DocumentId;

public class Quiz {

    @DocumentId
    private String id;
    private String name;
    private String description;
    private String image;
    private String level;
    private String visibility;
    private long questions;

    public Quiz() {
    }

    public Quiz(String id, String name, String description, String image, String level, String visibility, long questions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.level = level;
        this.visibility = visibility;
        this.questions = questions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public long getQuestions() {
        return questions;
    }

    public void setQuestions(long questions) {
        this.questions = questions;
    }
}
