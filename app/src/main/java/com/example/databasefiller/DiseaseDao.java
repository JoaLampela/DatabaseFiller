package com.example.databasefiller;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface DiseaseDao {

    @Insert
    void insertDisease(Disease... diseases);

    @Query("SELECT * FROM Diseases")
    List<Disease> getAllDiseases();

    @Query("SELECT Diseases.id FROM Diseases WHERE Diseases.name = :name")
    int getDiseaseIdWithName(String name);

    @Query("SELECT Diseases.name FROM Diseases")
    List<String> getAllDiseaseNames();

    @Query("SELECT Diseases.ageBias FROM Diseases WHERE Diseases.name = :name")
    int getDiseaseAgeBiasWithName(String name);
}
