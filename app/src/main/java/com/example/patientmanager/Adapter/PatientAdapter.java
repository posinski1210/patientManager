package com.example.patientmanager.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patientmanager.EditPatientActivity;
import com.example.patientmanager.MainActivity;
import com.example.patientmanager.Model.Patient;
import com.example.patientmanager.R;
import com.example.patientmanager.Utils.DatabaseHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {


    private MainActivity activity;
    private List<Patient> patientList;
    private Context context;


    public PatientAdapter(List<Patient> patientList, Context context) {
        this.context = context;
        this.patientList = patientList;
    }

    public Context getContext() {
        return context;
    }


    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_item, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        Patient patient = patientList.get(position);
        holder.bind(patient);
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }


    public void deleteItem(int position) {
        // Delete item from your data source and notify adapter
        int patientId = patientList.get(position).getId();
        DatabaseHandler db = new DatabaseHandler(context);
        db.openDatabase();
        db.deletePatient(patientId);

        patientList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        // Edit item from your data source and notify adapter
        Patient patient = patientList.get(position);
        Intent intent = new Intent(context, EditPatientActivity.class);
        intent.putExtra("patientId", patient.getId());
        context.startActivity(intent);
    }

    public int getPatientIdAtPosition(int position) {
        if (position >= 0 && position < patientList.size()) {
            return patientList.get(position).getId();
        }
        return -1; // Return -1 to indicate an invalid position
    }

    public void clear() {
        int size = patientList.size();
        patientList.clear();
        notifyItemRangeChanged(0,size);
    }

    static class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView surnameTextView;
        TextView phoneTextView;
        TextView dateTextView;
        TextView timeTextView;
        TextView treatmentTextView;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            surnameTextView = itemView.findViewById(R.id.surnameTextView);
            phoneTextView = itemView.findViewById(R.id.phoneTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            treatmentTextView = itemView.findViewById(R.id.treatmentTextView);
        }


        public void bind(Patient patient) {
            nameTextView.setText(patient.getName());
            surnameTextView.setText(patient.getSurname());
            phoneTextView.setText(patient.getPhoneNumber());
            treatmentTextView.setText(patient.getTreatment());

            Date visitDate = patient.getVisitDate();
            if (visitDate != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("pl", "PL"));
                String formattedDate = dateFormat.format(visitDate);
                dateTextView.setText(formattedDate); // Adjust formatting if needed
            } else {
                dateTextView.setText("N/A"); // Set a default value or handle it in a way that makes sense for your application
            }

            Date visitTime = patient.getVisitTime();
            if (visitTime != null) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", new Locale("pl", "PL"));
                String formattedTime = timeFormat.format(visitTime);
                timeTextView.setText(formattedTime); // Adjust formatting if needed
            } else {
                timeTextView.setText("N/A"); // Set a default value or handle it in a way that makes sense for your application
            }
        }
    }

    public void setFilteredList(List<Patient> filteredList){
        this.patientList = filteredList;
        notifyDataSetChanged();
    }


}