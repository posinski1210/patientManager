package com.example.patientmanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patientmanager.Adapter.PatientAdapter;
import com.example.patientmanager.Model.Patient;
import com.example.patientmanager.Utils.DatabaseHandler;

import java.util.List;

public class ViewPatientsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Patient> patientList;
    private PatientAdapter patientAdapter;
    private Button btnBackToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patients);
        btnBackToMain = findViewById(R.id.btnBackToMain);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Populate patientList with data (you need to fetch the data from your database)
        patientList = getPatientsFromDatabase();


        //Check if the patient list is empty
        if (patientList.isEmpty()) {
            Log.d("ViewPatientsActivity", "Patient list is empty.");
        } else {
            // Create and set the adapter
            patientAdapter = new PatientAdapter(patientList,this);
            recyclerView.setAdapter(patientAdapter);
        }

        //Set up ItemTouchHelper for swipe gestures
        RecyclerViewItemTouchHelper itemTouchHelper = new RecyclerViewItemTouchHelper(patientAdapter);
        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerView);

        btnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewPatientsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        patientList = getPatientsFromDatabase();
        if(patientAdapter != null) {
            patientAdapter.notifyDataSetChanged();
        }
    }


    // Implement a method to fetch patients from your database
    private List<Patient> getPatientsFromDatabase() {
        // Use your DatabaseHandler class to get the patient list
        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        databaseHandler.openDatabase();
        return databaseHandler.getAllPatients();
    }
}