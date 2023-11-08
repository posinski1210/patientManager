package com.example.patientmanager;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.patientmanager.Model.Patient;
import com.example.patientmanager.Utils.DatabaseHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditPatientActivity extends AppCompatActivity {
    private EditText etName, etSurname, etPhoneNumber, etDate, etTime,etTreatment;
    private Button btnUpdate, btnBackToMain, btnClearFields, btnSelectDate, btnSelectTime;
    private int patientId;
    private DatabaseHandler db;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_patients);

        db = new DatabaseHandler(this);
        db.openDatabase();

        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etTreatment = findViewById(R.id.etTreatment);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnBackToMain = findViewById(R.id.btnBackToMain);
        btnClearFields = findViewById(R.id.btnClearFields);

        patientId = getIntent().getIntExtra("PATIENT_ID", -1);

        if (patientId != -1) {
            Patient patient = db.getPatientById(patientId);

            if (patient != null) {
                etName.setText(patient.getName());
                etSurname.setText(patient.getSurname());
                etPhoneNumber.setText(patient.getPhoneNumber());

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                etDate.setText(dateFormat.format(patient.getVisitDate()));

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                etTime.setText(timeFormat.format(patient.getVisitTime()));

                etTreatment.setText(patient.getTreatment());
            } else {

            }
        }


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve data from EditTexts
                String name = etName.getText().toString();
                String surname = etSurname.getText().toString();
                String phoneNumber = etPhoneNumber.getText().toString();
                Date date = null;
                Date time = null;
                String treatment = etTreatment.getText().toString();


                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

                    date = dateFormat.parse(etDate.getText().toString());
                    time = timeFormat.parse(etTime.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // Save the patient's information
                Patient updatedPatient = new Patient();
                updatedPatient.setName(name);
                updatedPatient.setSurname(surname);
                updatedPatient.setPhoneNumber(phoneNumber);
                updatedPatient.setVisitDate(date);
                updatedPatient.setVisitTime(time);
                updatedPatient.setTreatment(treatment);

                db.updatePatient(patientId, updatedPatient);

                // Show a confirmation message
                showToast("Patient saved");

                // Optionally, you can navigate back to the main activity or perform any other action
                Intent intent = new Intent(EditPatientActivity.this, ViewPatientsActivity.class);
                startActivity(intent);
                finish(); // This will close the current activity
            }

            private void showToast(String message) {
                Toast.makeText(EditPatientActivity.this, "Patient saved", Toast.LENGTH_SHORT).show();
            }
        });

        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        btnSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog();
            }

            private void showTimePickerDialog() {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        EditPatientActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Handle the selected time
                                String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);

                                // Set the selected time in the EditText
                                etTime.setText(selectedTime);
                            }
                        },
                        hour,
                        minute,
                        true
                );
                timePickerDialog.show();
            }
        });


        btnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // This will close the current activity and go back to the previous one (MainActivity).
            }
        });

        btnClearFields.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllFields();
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Handle the selected date
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String selectedDate = dateFormat.format(calendar.getTime());

                        // Set the selected date in the EditText
                        etDate.setText(selectedDate);
                    }
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }


    private void clearAllFields() {
        etName.setText("");
        etSurname.setText("");
        etPhoneNumber.setText("");
        etDate.setText("");
        etTime.setText("");
        etTreatment.setText("");
    }

}
