package com.nvivx.vixhealthsystem.model.medical;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Surgery {

    private LocalDateTime dateTime;
    private String name;
    private String description;
    private String room;

    public Surgery(LocalDateTime dateTime,String name,String description,String room){
        this.dateTime = dateTime;
        this.name = name;
        this.description = description;
        this.room = room;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getName() {
        return name;
    }

    public String getRoom() {
        return room;
    }


    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
