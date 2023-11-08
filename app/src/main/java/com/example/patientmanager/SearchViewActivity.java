package com.example.patientmanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patientmanager.Adapter.PatientAdapter;
import com.example.patientmanager.Model.Patient;
import com.example.patientmanager.Utils.DatabaseHandler;

import java.util.List;

public class SearchViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Patient> patientList;
    private PatientAdapter patientAdapter;
    private Button btnBackToMain;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);

        btnBackToMain = findViewById(R.id.btnBackToMain);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Populate patientList with data (you need to fetch the data from your database)
        patientList = getPatientsFromDatabase();

        //Check if the patient list is empty
        if (patientList.isEmpty()) {
            Log.d("SearchViewActivity", "Patient list is empty.");
        } else {
            // Create and set the adapter
            patientAdapter = new PatientAdapter(patientList, this);
            recyclerView.setAdapter(patientAdapter);
        }

        //Set up ItemTouchHelper for swipe gestures
        RecyclerViewItemTouchHelper itemTouchHelper = new RecyclerViewItemTouchHelper(patientAdapter);
        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerView);

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterList(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });

        btnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchViewActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        patientList = getPatientsFromDatabase();
        if (patientAdapter != null) {
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

    private void filterList(String text) {
        DatabaseHandler db = new DatabaseHandler(this);
        db.openDatabase();
        List<Patient> filteredList = db.searchPatientsBySurname(text);

        if(filteredList.isEmpty()){
            Log.d("SearchViewActivity","No data found");
            patientAdapter.clear();
        }else {
            patientAdapter.setFilteredList(filteredList);
        }
    }
}
