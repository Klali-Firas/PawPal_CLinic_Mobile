package com.example.pawpalclinic.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.pawpalclinic.R;
import com.example.pawpalclinic.controller.AnimauxController;
import com.example.pawpalclinic.controller.RendezVousController;
import com.example.pawpalclinic.controller.ServiceController;
import com.example.pawpalclinic.model.Animaux;
import com.example.pawpalclinic.model.RendezVous;
import com.example.pawpalclinic.model.Service;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class AddRendezVousFragment extends Fragment {

    private TextInputEditText inputName;
    private TextInputEditText inputLastName;
    private Spinner selectAnimal;
    private TextInputEditText inputDate;
    private Spinner selectService;
    private Button btnSubmit;
    private TextView workingHoursText;

    private AnimauxController animauxController;
    private ServiceController serviceController;
    private RendezVousController rendezVousController;
    private Calendar calendar;
    private static final String SHARED_PREFS_NAME = "user_prefs";
    private static final String USER_KEY = "user";

    public AddRendezVousFragment() {
        // Required empty public constructor
    }

    public static AddRendezVousFragment newInstance() {
        return new AddRendezVousFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animauxController = new AnimauxController(getContext());
        serviceController = new ServiceController(getContext());
        rendezVousController = new RendezVousController(getContext());
        calendar = Calendar.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_rendez_vous, container, false);

        inputName = view.findViewById(R.id.input_name);
        inputLastName = view.findViewById(R.id.input_last_name);
        selectAnimal = view.findViewById(R.id.select_animal);
        inputDate = view.findViewById(R.id.input_date);
        selectService = view.findViewById(R.id.select_service);
        btnSubmit = view.findViewById(R.id.btn_submit);
        workingHoursText = view.findViewById(R.id.working_hours_text);

        // Set working hours text
        workingHoursText.setText("Working Hours: Mon-Thu 08:00-12:00, 14:00-17:00; Fri 08:00-14:00");

        // Retrieve user data from SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        String userJsonString = sharedPreferences.getString(USER_KEY, null);
        if (userJsonString != null) {
            try {
                JSONObject userJson = new JSONObject(userJsonString);
                String prenom = userJson.getString("prenom");
                String nom = userJson.getString("nom");

                // Set user name and last name
                inputName.setText(prenom);
                inputLastName.setText(nom);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Load animals and services
        loadAnimals();
        loadServices();

        inputDate.setOnClickListener(v -> showDateTimePicker());

        btnSubmit.setOnClickListener(v -> submitForm());

        // Disable submit button initially
        btnSubmit.setEnabled(false);

        // Add text watcher to enable submit button when form is valid
        TextWatcher formTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateForm();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        inputDate.addTextChangedListener(formTextWatcher);

        return view;
    }

    private void loadAnimals() {
        CompletableFuture<List<Animaux>> animauxFuture = animauxController.getAllAnimaux();
        animauxFuture.thenAccept(animauxList -> {
            List<String> animalNames = new ArrayList<>();
            for (Animaux animaux : animauxList) {
                animalNames.add(animaux.getNom());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, animalNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selectAnimal.setAdapter(adapter);
        }).exceptionally(throwable -> {
            Toast.makeText(getContext(), "Error loading animals", Toast.LENGTH_SHORT).show();
            return null;
        });
    }

    private void loadServices() {
        CompletableFuture<List<Service>> serviceFuture = serviceController.getAllServices();
        serviceFuture.thenAccept(serviceList -> {
            List<String> serviceNames = new ArrayList<>();
            for (Service service : serviceList) {
                serviceNames.add(service.getNomService());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, serviceNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selectService.setAdapter(adapter);
        }).exceptionally(throwable -> {
            Toast.makeText(getContext(), "Error loading services", Toast.LENGTH_SHORT).show();
            return null;
        });
    }

    private void showDateTimePicker() {
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DAY_OF_MONTH, 1);
        minDate.set(Calendar.HOUR_OF_DAY, 0);
        minDate.set(Calendar.MINUTE, 0);
        minDate.set(Calendar.SECOND, 0);
        minDate.set(Calendar.MILLISECOND, 0);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                if (isValidDateTime(calendar)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy HH:mm", Locale.getDefault());
                    inputDate.setText(sdf.format(calendar.getTime()));
                } else {
                    Toast.makeText(getContext(), "Selected date/time is not within working hours", Toast.LENGTH_SHORT).show();
                    inputDate.setText("");
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.show();
    }

    private boolean isValidDateTime(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Check if the selected date is a weekend
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            return false;
        }

        // Check if the selected time is within working hours
        if (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.THURSDAY) {
            if ((hourOfDay < 8) || (hourOfDay == 8 && minute < 0) || ((hourOfDay >= 12 && minute >15) && hourOfDay < 14) || (hourOfDay >= 17) || (hourOfDay == 13)) {
                return false;
            }
        } else if (dayOfWeek == Calendar.FRIDAY) {
            if ((hourOfDay < 8) || (hourOfDay == 8 && minute < 0) || (hourOfDay >= 14)) {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }

    private void validateForm() {
        boolean isValid = !inputDate.getText().toString().isEmpty();
        btnSubmit.setEnabled(isValid);
    }

    private void submitForm() {
        int selectedAnimalPosition = selectAnimal.getSelectedItemPosition();
        int selectedServicePosition = selectService.getSelectedItemPosition();

        if (selectedAnimalPosition == -1 || selectedServicePosition == -1 || inputDate.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields and select a valid date/time", Toast.LENGTH_SHORT).show();
            return;
        }

        Date dateRendezVous = calendar.getTime();

        Animaux selectedAnimal = animauxController.getAllAnimaux().join().get(selectedAnimalPosition);
        Service selectedService = serviceController.getAllServices().join().get(selectedServicePosition);

        RendezVous rendezVous = new RendezVous(
                0,
                selectedAnimal.getId(),
                null,
                dateRendezVous,
                "en_attente",
                selectedService.getId(),
                new Date(),
                null
        );

        rendezVousController.createRendezVous(rendezVous);
        Toast.makeText(getContext(), "RendezVous created successfully", Toast.LENGTH_SHORT).show();

        // Reset form
        selectAnimal.setSelection(0);
        selectService.setSelection(0);
        inputDate.setText("");
        btnSubmit.setEnabled(false);
    }
}