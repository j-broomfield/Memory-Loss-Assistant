package com.example.memorylosscompanion;

public class User {

    private String id;
    private String username;
    String image;

    public User (String id, String image, String username){
        this.id = id;
        this.image = image;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String id) {
        this.image = image;
    }



    public User(){


    }

}
