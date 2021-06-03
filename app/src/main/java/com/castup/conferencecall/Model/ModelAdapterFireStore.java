package com.castup.conferencecall.Model;

public class ModelAdapterFireStore {

    private String ID ;
    private String FirstName ;
    private String LastName ;
    private String Email ;
    private String Photo ;
    private String Token ;

    public ModelAdapterFireStore() {
    }

    public ModelAdapterFireStore(String ID, String firstName, String lastName, String email, String photo, String token) {
        this.ID = ID;
        FirstName = firstName;
        LastName = lastName;
        Email = email;
        Photo = photo;
        Token = token;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }
}
