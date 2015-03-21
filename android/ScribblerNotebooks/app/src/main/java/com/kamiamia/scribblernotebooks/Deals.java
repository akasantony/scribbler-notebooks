package com.kamiamia.scribblernotebooks;

/**
 * Created by akas on 12/3/15.
 */
public class Deals {

    private String id;
    private String title;
    private String category;
    private String description1;
    private String logoPath;

    public Deals(String id, String title,String category, String description1, String logoPath) {
        this.id = id;
        this.title = title;
        this.description1 = description1;
        this.category = category;
        this.logoPath = logoPath;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription1() {
        return description1;
    }

    public String getCategory() {
        return category;
    }
    public String getLogoPath() { return logoPath; }
}
