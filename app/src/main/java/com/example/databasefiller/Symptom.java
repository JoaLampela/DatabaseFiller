package com.example.databasefiller;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Symptoms")
public class Symptom {

    @PrimaryKey(autoGenerate = true)
    int id;
    String name;

    public Symptom(String name) {
        this.name = name;
    }
}
