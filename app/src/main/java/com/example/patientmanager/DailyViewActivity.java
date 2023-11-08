package com.example.patientmanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patientmanager.Adapter.PatientAdapter;
import com.example.patientmanager.Model.Patient;
import com.example.patientmanager.Utils.DatabaseHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class  DailyViewActivity extends AppCompatActivity {
    private Button btnBackToMain;
    private Button btnSelectDate;
    private List<Patient> patientList;
    private PatientAdapter patientAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_view);
        btnBackToMain = findViewById(R.id.btnBackToMain);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        patientList = new ArrayList<>();
        patientAdapter = new PatientAdapter(patientList, this);
        recyclerView.setAdapter(patientAdapter);


        RecyclerViewItemTouchHelper itemTouchHelper = new RecyclerViewItemTouchHelper(patientAdapter);
        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerView);

        btnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // This will close the current activity and go back to the previous one (MainActivity).
            }
        });

        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });


    }


    @Override
    protected void onResume(){
        super.onResume();
        if(patientAdapter != null) {
            patientAdapter.notifyDataSetChanged();
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String selectedDate = dateFormat.format(calendar.getTime());

                        filterList(selectedDate);
                    }
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }


    private void filterList(String selectedDate) {
        DatabaseHandler db = new DatabaseHandler(this);
        db.openDatabase();
        List<Patient> filteredList = db.searchPatientsByDate(selectedDate);

        if (filteredList.isEmpty()) {
            Log.d("DailyViewActivity", "No data found");
            patientAdapter.clear();
        } else {
            patientAdapter.setFilteredList(filteredList);
        }
    }
}