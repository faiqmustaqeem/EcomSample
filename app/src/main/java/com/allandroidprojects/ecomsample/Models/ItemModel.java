package com.allandroidprojects.ecomsample.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CodianSoft on 27/04/2018.
 */

public class ItemModel {
    private String id;
    private String name;
    private String description;
    private String price;
    private List<String> imageLink=new ArrayList<>();
    private boolean favourite;

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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }



    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public List<String> getImageLink() {
        return imageLink;
    }

    public void setImageLink(List<String> imageLink) {
        this.imageLink = imageLink;
    }
}
