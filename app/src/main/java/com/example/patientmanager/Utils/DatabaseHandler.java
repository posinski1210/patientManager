package com.example.patientmanager.Utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.patientmanager.Model.Patient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String NAME = "patientDatabase";
    private static final String PATIENT_TABLE = "patients";
    private static final String ID = "id";
    private static final String NAME_COLUMN = "name";
    private static final String SURNAME_COLUMN = "surname";
    private static final String PHONE_COLUMN = "phone";
    private static final String DATE_COLUMN = "date";
    private static final String TIME_COLUMN = "time";
    private static final String TREATMENT_COLUMN = "treatment";

    private static final String CREATE_PATIENT_TABLE = "CREATE TABLE " + PATIENT_TABLE + "(" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME_COLUMN + " TEXT, " +
            SURNAME_COLUMN + " TEXT, " +
            PHONE_COLUMN + " TEXT, " +
            DATE_COLUMN + " TEXT, " +
            TIME_COLUMN + " TEXT, " +
            TREATMENT_COLUMN + " TEXT)";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PATIENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the older tables
        db.execSQL("DROP TABLE IF EXISTS " + PATIENT_TABLE);
        // Create tables again
        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertPatient(Patient patient) {
        ContentValues cv = new ContentValues();
        cv.put(NAME_COLUMN, patient.getName());
        cv.put(SURNAME_COLUMN, patient.getSurname());
        cv.put(PHONE_COLUMN, patient.getPhoneNumber());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        cv.put(DATE_COLUMN, dateFormat.format(patient.getVisitDate()));
        cv.put(TIME_COLUMN, timeFormat.format(patient.getVisitTime()));
        cv.put(TREATMENT_COLUMN, patient.getTreatment());

        db.insert(PATIENT_TABLE, null, cv);
    }


    @SuppressLint("Range")
    public List<Patient> getAllPatients() {
        List<Patient> patientList = new ArrayList<>();
        Cursor cur = null;
        if (db != null) {
            db.beginTransaction();

            try {
                cur = db.query(PATIENT_TABLE, null, null, null, null, null, null, null);
                if (cur != null) {
                    if (cur.moveToFirst()) {
                        do {
                            Patient patient = new Patient();
                            patient.setId(cur.getInt(cur.getColumnIndex(ID)));
                            patient.setName(cur.getString(cur.getColumnIndex(NAME_COLUMN)));
                            patient.setSurname(cur.getString(cur.getColumnIndex(SURNAME_COLUMN)));
                            patient.setPhoneNumber(cur.getString(cur.getColumnIndex(PHONE_COLUMN)));
                            patient.setTreatment(cur.getString(cur.getColumnIndex(TREATMENT_COLUMN)));

                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

                            try {
                                String dateStr = cur.getString(cur.getColumnIndex(DATE_COLUMN));
                                String timeStr = cur.getString(cur.getColumnIndex(TIME_COLUMN));

                                if (dateStr != null && timeStr != null) {
                                    Date visitDate = dateFormat.parse(dateStr);
                                    Date visitTime = timeFormat.parse(timeStr);
                                    patient.setVisitDate(visitDate);
                                    patient.setVisitTime(visitTime);
                                }
                            } catch (ParseException | NullPointerException e) {
                                e.printStackTrace(); // Handle the exception as needed
                            }

                            patientList.add(patient);
                        } while (cur.moveToNext());
                    }
                }
            } finally {
                db.endTransaction();
                if (cur != null && !cur.isClosed()) {
                    cur.close();
                }
            }
            Collections.sort(patientList, new Comparator<Patient>() {
                @Override
                public int compare(Patient p1, Patient p2) {
                    int dateComparison = p1.getVisitDate().compareTo(p2.getVisitDate());
                    if(dateComparison == 0){
                        return p1.getVisitTime().compareTo(p2.getVisitTime());
                    }
                    return dateComparison;
                }
            });
        }
        return patientList;
    }


    public void updatePatient(int id, Patient updatedPatient) {
        ContentValues cv = new ContentValues();
        cv.put(NAME_COLUMN, updatedPatient.getName());
        cv.put(SURNAME_COLUMN, updatedPatient.getSurname());
        cv.put(PHONE_COLUMN, updatedPatient.getPhoneNumber());
        cv.put(TREATMENT_COLUMN, updatedPatient.getTreatment());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String visitDate = dateFormat.format(updatedPatient.getVisitDate());
        cv.put(DATE_COLUMN, visitDate);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String visitTime = timeFormat.format(updatedPatient.getVisitTime());
        cv.put(TIME_COLUMN, visitTime);

        db.update(PATIENT_TABLE, cv, ID + "=?", new String[]{String.valueOf(id)});
    }


    public void deletePatient(int id) {
        db.delete(PATIENT_TABLE, ID + "=?", new String[]{String.valueOf(id)});
    }

    @SuppressLint("Range")
    public Patient getPatientById(int patientId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                PATIENT_TABLE,
                new String[]{ID, NAME_COLUMN, SURNAME_COLUMN, PHONE_COLUMN, DATE_COLUMN, TIME_COLUMN, TREATMENT_COLUMN},
                ID + "=?",
                new String[]{String.valueOf(patientId)},
                null,
                null,
                null
        );
        Patient patient = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                try {
                    patient = new Patient();
                    patient.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                    patient.setName(cursor.getString(cursor.getColumnIndex(NAME_COLUMN)));
                    patient.setSurname(cursor.getString(cursor.getColumnIndex(SURNAME_COLUMN)));
                    patient.setPhoneNumber(cursor.getString(cursor.getColumnIndex(PHONE_COLUMN)));
                    patient.setTreatment(cursor.getString(cursor.getColumnIndex(TREATMENT_COLUMN)));

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

                    String dateStr = cursor.getString(cursor.getColumnIndex(DATE_COLUMN));
                    String timeStr = cursor.getString(cursor.getColumnIndex(TIME_COLUMN));

                    if (dateStr != null && timeStr != null) {
                        Date visitDate = dateFormat.parse(dateStr);
                        Date visitTime = timeFormat.parse(timeStr);
                        patient.setVisitDate(visitDate);
                        patient.setVisitTime(visitTime);
                    }

                } catch (ParseException | NullPointerException e) {
                    e.printStackTrace(); // Handle the exception as needed
                } finally {
                    cursor.close();
                }
            }
        }

        return patient;
    }


    @SuppressLint("Range")
    public List<Patient> searchPatientsBySurname(String surname) {
        List<Patient> filteredList = new ArrayList<>();
        Cursor cursor = db.query(PATIENT_TABLE, null, SURNAME_COLUMN + " LIKE ?", new String[]{"%" + surname + "%"}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Patient patient = new Patient();
                    patient.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                    patient.setName(cursor.getString(cursor.getColumnIndex(NAME_COLUMN)));
                    patient.setSurname(cursor.getString(cursor.getColumnIndex(SURNAME_COLUMN)));
                    patient.setPhoneNumber(cursor.getString(cursor.getColumnIndex(PHONE_COLUMN)));
                    patient.setTreatment(cursor.getString(cursor.getColumnIndex(TREATMENT_COLUMN)));

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

                    try {
                        String dateStr = cursor.getString(cursor.getColumnIndex(DATE_COLUMN));
                        String timeStr = cursor.getString(cursor.getColumnIndex(TIME_COLUMN));

                        if (dateStr != null && timeStr != null) {
                            Date visitDate = dateFormat.parse(dateStr);
                            Date visitTime = timeFormat.parse(timeStr);
                            patient.setVisitDate(visitDate);
                            patient.setVisitTime(visitTime);
                        }
                    } catch (ParseException | NullPointerException e) {
                        e.printStackTrace(); // Handle the exception as needed
                    }

                    filteredList.add(patient);
                } while (cursor.moveToNext());
            }
            cursor.close();
            Collections.sort(filteredList, new Comparator<Patient>() {
                @Override
                public int compare(Patient p1, Patient p2) {
                    int dateComparison = p1.getVisitDate().compareTo(p2.getVisitDate());
                    if(dateComparison == 0){
                        return p1.getVisitTime().compareTo(p2.getVisitTime());
                    }
                    return dateComparison;
                }
            });
        }
        return filteredList;
    }


    @SuppressLint("Range")
    public List<Patient> searchPatientsByDate(String selectedDate) {
        List<Patient> filteredList = new ArrayList<>();
        Cursor cursor = db.query(PATIENT_TABLE, null, DATE_COLUMN + "=?", new String[]{selectedDate}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Patient patient = new Patient();
                    patient.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                    patient.setName(cursor.getString(cursor.getColumnIndex(NAME_COLUMN)));
                    patient.setSurname(cursor.getString(cursor.getColumnIndex(SURNAME_COLUMN)));
                    patient.setPhoneNumber(cursor.getString(cursor.getColumnIndex(PHONE_COLUMN)));
                    patient.setTreatment(cursor.getString(cursor.getColumnIndex(TREATMENT_COLUMN)));

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

                    try {
                        String dateStr = cursor.getString(cursor.getColumnIndex(DATE_COLUMN));
                        String timeStr = cursor.getString(cursor.getColumnIndex(TIME_COLUMN));

                        if (dateStr != null && timeStr != null) {
                            Date visitDate = dateFormat.parse(dateStr);
                            Date visitTime = timeFormat.parse(timeStr);
                            patient.setVisitDate(visitDate);
                            patient.setVisitTime(visitTime);
                        }
                    } catch (ParseException | NullPointerException e) {
                        e.printStackTrace(); // Handle the exception as needed
                    }

                    filteredList.add(patient);
                } while (cursor.moveToNext());
            }
            cursor.close();
            Collections.sort(filteredList, new Comparator<Patient>() {
                @Override
                public int compare(Patient p1, Patient p2) {
                    int dateComparison = p1.getVisitDate().compareTo(p2.getVisitDate());
                    if(dateComparison == 0){
                        return p1.getVisitTime().compareTo(p2.getVisitTime());
                    }
                    return dateComparison;
                }
            });
        }
        return filteredList;
    }

    public void deletePatientsOneDayAfterVisit(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        calendar.add(Calendar.HOUR_OF_DAY,1);
        String yesterday = dateFormat.format(calendar.getTime());

        db.delete(PATIENT_TABLE,DATE_COLUMN + "<=?", new String[]{yesterday});
    }

}
