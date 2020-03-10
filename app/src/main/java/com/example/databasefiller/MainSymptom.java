package com.example.databasefiller;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "MainSymptoms")
public class MainSymptom {

    @PrimaryKey(autoGenerate = true)
    int id;
    String name;

    public MainSymptom(String name) {
        this.name = name;
    }
}
