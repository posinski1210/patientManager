package com.example.patientmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.patientmanager.Utils.DatabaseHandler;


public class MainActivity extends AppCompatActivity {

    private Button btnAddPatient;
    private Button btnViewPatients;
    private Button btnDailyView;
    private Button btnSearchView;
    private DatabaseHandler databaseHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHandler = new DatabaseHandler(this);
        databaseHandler.openDatabase();
        databaseHandler.deletePatientsOneDayAfterVisit();



        btnAddPatient = findViewById(R.id.btnAddPatient);
        btnViewPatients = findViewById(R.id.btnViewPatients);
        btnDailyView = findViewById(R.id.btnDailyView);
        btnSearchView = findViewById(R.id.btnSearchView);


        btnAddPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open AddPatientActivity
                Intent intent = new Intent(MainActivity.this, AddPatientActivity.class);
                startActivity(intent);
            }
        });

        btnViewPatients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open ViewPatientsActivity
                Intent intent = new Intent(MainActivity.this, ViewPatientsActivity.class);
                startActivity(intent);
            }
        });
    btnDailyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DailyViewActivity.class);
                startActivity(intent);
            }
        });

    btnSearchView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this,SearchViewActivity.class);
            startActivity(intent);
        }
    });
    }


}

