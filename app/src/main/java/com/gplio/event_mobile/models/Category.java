package com.gplio.event_mobile.models;

/**
 * Created by goncalopalaio on 01/05/18.
 */

public class Category {
    public String title;
    public String description;

    public Category(String title, String description) {
        this.title = title;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Category{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
