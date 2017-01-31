package com.theironyard.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by michaelmernin on 1/3/17.
 */

@Entity
@Table(name = "photos")
public class Photo {

    @Id
    @GeneratedValue
    int id;

    @ManyToOne
    User sender; //any user could send many photos

    @ManyToOne
    User recipient; //any user could receive many photos

    @Column(nullable = false)
    String filename;

    @Column(nullable = false)
    boolean isPublic;

    @Column(nullable = false)
    LocalDateTime phTime;

    @Column(nullable = false)
    Long exists;

    public Long getExists() {
        return exists;
    }

    public void setExists(Long exists) {
        this.exists = exists;
    }

    public Photo(Long exists) {
        this.exists = exists;
    }

    public Photo(boolean isPublic, LocalDateTime phTime) {
        this.isPublic = isPublic;
        this.phTime = phTime;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public LocalDateTime getPhTime() {
        return phTime;
    }

    public void setPhTime(LocalDateTime phTime) {
        this.phTime = phTime;
    }

    public Photo() {
    }

    public Photo(User sender, User recipient, String filename) {
        this.sender = sender;
        this.recipient = recipient;
        this.filename = filename;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
