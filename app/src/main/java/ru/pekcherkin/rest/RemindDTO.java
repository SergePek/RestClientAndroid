package ru.pekcherkin.rest;

public class RemindDTO {
   private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RemindDTO(String title) {
        this.title = title;
    }
}
