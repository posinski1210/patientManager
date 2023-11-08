package com.example.patientmanager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.patientmanager.Model.Patient;
import com.example.patientmanager.Utils.DatabaseHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddPatientActivity extends AppCompatActivity {

    private EditText etName, etSurname, etPhoneNumber, etDate, etTime, etTreatment;
    private Button btnSave, btnBackToMain, btnClearFields, btnSelectDate, btnSelectTime;
    private DatabaseHandler db;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sendSMS();
        } else {
            Log.d("AddPatientActivity", "Permission Denied");
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

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

        btnSave = findViewById(R.id.btnSave);
        btnBackToMain = findViewById(R.id.btnBackToMain);
        btnClearFields = findViewById(R.id.btnClearFields);


        btnSave.setOnClickListener(new View.OnClickListener() {
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

                Log.d("AddPatientActivity", "Save button clicked");

                // Save the patient's information
                Patient patient = new Patient();
                patient.setName(name);
                patient.setSurname(surname);
                patient.setPhoneNumber(phoneNumber);
                patient.setVisitDate(date);
                patient.setVisitTime(time);
                patient.setTreatment(treatment);

                db.insertPatient(patient);

                // Show a confirmation message
                showToast("Patient saved");
                int requestCode = (int) System.currentTimeMillis();


                if (ContextCompat.checkSelfPermission(AddPatientActivity.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    sendSMS();
                } else {
                    ActivityCompat.requestPermissions(AddPatientActivity.this, new String[]{Manifest.permission.SEND_SMS}, 100);
                }
                scheduleReminderSMS(patient,requestCode);


                // Optionally, you can navigate back to the main activity or perform any other action
                finish(); // This will close the current activity
            }


            private void showToast(String message) {
                Toast.makeText(AddPatientActivity.this, "Patient saved", Toast.LENGTH_SHORT).show();
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
                        AddPatientActivity.this,
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

    public void scheduleReminderSMS(Patient patient,int requestCode) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, SmsBroadcastReceiver.class);
        String date = etDate.getText().toString();
        String time = etTime.getText().toString();
        String msg = "Gabinet Stomatologiczny lek. stom. Marzena Parzonka przypomina o wizycie w dniu "+ date + " o godzinie " + time + ". Prosze o potwierdzenie (TAK/NIE) smsem.";
        intent.putExtra("message", msg);
        intent.putExtra("phoneNumber", etPhoneNumber.getText().toString());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar visitDateTime = Calendar.getInstance();
        visitDateTime.setTime(patient.getVisitDate());
        Calendar reminderTime = (Calendar) visitDateTime.clone();
        reminderTime.add(Calendar.DAY_OF_MONTH, -1); // Subtract 1 day for the reminder

        reminderTime.set(Calendar.HOUR_OF_DAY, patient.getVisitTime().getHours());
        reminderTime.set(Calendar.MINUTE, patient.getVisitTime().getMinutes());
        reminderTime.set(Calendar.SECOND, 0);

        long reminderTimeInMillis = reminderTime.getTimeInMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderTimeInMillis, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTimeInMillis, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, reminderTimeInMillis, pendingIntent);
        }

    }


    private void sendSMS() {
        String phoneNumber = etPhoneNumber.getText().toString();
        String message = "Wizyta odbędzie się " + etDate.getText().toString() + " o " + etTime.getText().toString() + ".";

        if (!phoneNumber.isEmpty()) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);

            Log.d("AddPatientActivity", " Sms sent.");
        } else {
            Log.d("AddPatientActivity", "Something wrong");
        }

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

    }

}