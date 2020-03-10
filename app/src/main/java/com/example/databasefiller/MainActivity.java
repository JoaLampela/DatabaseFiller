package com.example.databasefiller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DatabaseBK database;
    Button insertButton;
    EditText diseaseField;
    EditText mainSymptomsField;
    EditText rareSymptomsField;
    String diseaseName;
    ArrayList<String> mainSymptomParameters;
    ArrayList<String> rareSymptomParameters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = Room.databaseBuilder(this, DatabaseBK.class, "Database").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        insertButton = findViewById(R.id.button);
        diseaseField = findViewById(R.id.editText1);
        mainSymptomsField = findViewById(R.id.editText2);
        rareSymptomsField = findViewById(R.id.editText3);
        Log.d("TEST", "Database's diseases: " + database.getDiseaseDao().getAllDiseaseNames());
    }

    public void onInsertClick(View view) {

        mainSymptomParameters = new ArrayList<>();
        rareSymptomParameters = new ArrayList<>();

        String[] diseaseAttributeList = diseaseField.getText().toString().trim().split(",");
        diseaseName = diseaseAttributeList[0];

        if(database.getDiseaseDao().getAllDiseaseNames().contains(diseaseName.substring(0, 1).toUpperCase() + diseaseName.substring(1).toLowerCase())
                && diseaseAttributeList.length == 2) {
            return;
        }

        int diseaseAgeBias = Integer.valueOf(diseaseAttributeList[1].trim());
        float diseaseSexBias = Float.valueOf(diseaseAttributeList[2].trim());

        String[] mainSymptomList = mainSymptomsField.getText().toString().trim().split(",");
        ArrayList<String> mainSymptomNames = new ArrayList<>();

        for(int i = 0; i < mainSymptomList.length; i++) {

            if(!database.getMainSymptomDao().getAllMainSymptomNames().contains(mainSymptomList[i].trim().substring(0, 1).toUpperCase() + mainSymptomList[i].trim().substring(1).toLowerCase())) {

                mainSymptomNames.add(mainSymptomList[i].trim().substring(0, 1).toUpperCase() + mainSymptomList[i].trim().substring(1).toLowerCase());
            }
            mainSymptomParameters.add(mainSymptomList[i].trim().substring(0, 1).toUpperCase() + mainSymptomList[i].trim().substring(1).toLowerCase());
        }

        String[] rareSymptomList = rareSymptomsField.getText().toString().trim().split(",");
        ArrayList<String> rareSymptomNames = new ArrayList<>();

        for(int i = 0; i < rareSymptomList.length; i++) {

            if(!database.getRareSymptomDao().getAllRareSymptomNames().contains(rareSymptomList[i].trim().substring(0, 1).toUpperCase() + rareSymptomList[i].trim().substring(1).toLowerCase())) {

                rareSymptomNames.add(rareSymptomList[i].trim().substring(0, 1).toUpperCase() + rareSymptomList[i].trim().substring(1).toLowerCase());
            }
            rareSymptomParameters.add(rareSymptomList[i].trim().substring(0, 1).toUpperCase() + rareSymptomList[i].trim().substring(1).toLowerCase());
        }

        ArrayList<String> prospectiveSymptomNames = new ArrayList<>(mainSymptomNames);

        for(int i = 0; i < rareSymptomNames.size(); i++) {

            if(!prospectiveSymptomNames.contains(rareSymptomNames.get(i))) {

                prospectiveSymptomNames.add(rareSymptomNames.get(i));
            }
        }

        ArrayList<String> finalSymptomNames = new ArrayList<>();

        for(int i = 0; i < prospectiveSymptomNames.size(); i++) {

            if(!database.getSymptomDao().getAllSymptomNames().contains(prospectiveSymptomNames.get(i))) {

                finalSymptomNames.add(prospectiveSymptomNames.get(i));
            }
        }
        addToDatabase(diseaseAgeBias, diseaseSexBias, finalSymptomNames, mainSymptomNames, rareSymptomNames);
    }

    public void addToDatabase(int ageBias, float sexBias, ArrayList<String> symptomNames, ArrayList<String> mainSymptomNames, ArrayList<String> rareSymptomNames) {

        database.getDiseaseDao().insertDisease(new Disease(diseaseName, ageBias, sexBias));

        for(int i = 0; i < mainSymptomNames.size(); i++) {

            if(mainSymptomNames.get(i) != null) {
                database.getMainSymptomDao().insertMainSymptom(new MainSymptom(mainSymptomNames.get(i)));
            }
        }

        for(int i = 0; i < rareSymptomNames.size(); i++) {

            if(rareSymptomNames.get(i) != null) {
                database.getRareSymptomDao().insertRareSymptom(new RareSymptom(rareSymptomNames.get(i)));
            }
        }

        for(int i = 0; i < symptomNames.size(); i++) {

            if(symptomNames.get(i) != null) {
                database.getSymptomDao().insertSymptom(new Symptom(symptomNames.get(i)));
            }
        }

        addMainSymptomsToJoiner();
        addRareSymptomsToJoiner();
    }

    public void addMainSymptomsToJoiner() {

        for(int i = 0; i < mainSymptomParameters.size(); i++) {

            database.getJoinerDao().insertJoinerValue(new Joiner(
                    database.getDiseaseDao().getDiseaseIdWithName(diseaseName),
                    database.getSymptomDao().getSymptomIdWithName(mainSymptomParameters.get(i)),
                    database.getMainSymptomDao().getMainSymptomIdWithName(mainSymptomParameters.get(i)), null));
        }
    }

    public void addRareSymptomsToJoiner() {

        for(int i = 0; i < rareSymptomParameters.size(); i++) {

            database.getJoinerDao().insertJoinerValue(new Joiner(
                    database.getDiseaseDao().getDiseaseIdWithName(diseaseName),
                    database.getSymptomDao().getSymptomIdWithName(rareSymptomParameters.get(i)), null,
                    database.getRareSymptomDao().getRareSymptomIdWithName(rareSymptomParameters.get(i))));
        }

        diseaseField.setText("");
        mainSymptomsField.setText("");
        rareSymptomsField.setText("");
        Log.d("TEST", "Database's diseases: " + database.getDiseaseDao().getAllDiseaseNames());
    }
}
