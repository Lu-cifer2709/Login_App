package com.example.loginapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfileActivity extends AppCompatActivity {
    private EditText editTextUpdateName, editTextUpdateDoB, editTextUpdateMobile;
    private RadioGroup radioGroupUpdateGender;
    private RadioButton radioButtonUpdateGenderSelected;
    private String textFullName, textDoB, textGender, textMobile;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        getSupportActionBar().setTitle("Update Profile Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressBar = findViewById(R.id.progressBar_Up);
        editTextUpdateName = findViewById(R.id.editText_update_profile_name);
        editTextUpdateDoB = findViewById(R.id.editText_update_profile_dob);
        editTextUpdateMobile = findViewById(R.id.editText_update_profile_mobile);

        radioGroupUpdateGender = findViewById(R.id.radio_group_update_gender);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        showProfile(firebaseUser);

        Button btnUploadProfilePic = findViewById(R.id.button_upload_profile_pic);
        btnUploadProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateProfileActivity.this, UploadProfilePicActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button btnUploadProfileEmail = findViewById(R.id.button_update_profile_email);
        btnUploadProfileEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateProfileActivity.this, UpdateEmailActivity.class);
                startActivity(intent);
                finish();
            }
        });

        editTextUpdateDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textSADob[] = textDoB.split("/");

                int day = Integer.parseInt(textSADob[0]);
                int month = Integer.parseInt(textSADob[1]) - 1;
                int year = Integer.parseInt(textSADob[2]);

                DatePickerDialog picker;
                picker = new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextUpdateDoB.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        Button btnUpdateProfile = findViewById(R.id.button_update_profile);
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile(firebaseUser);
            }
        });
    }

    private void updateProfile(FirebaseUser firebaseUser) {
        int selectedGenderID = radioGroupUpdateGender.getCheckedRadioButtonId();
        radioButtonUpdateGenderSelected = findViewById(selectedGenderID);

        String mobileRegex = "[6-9][0-9]{9}";
        Matcher mobileMatcher;
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        mobileMatcher = mobilePattern.matcher(textMobile);

        if (TextUtils.isEmpty(textFullName)){
            Toast.makeText(UpdateProfileActivity.this, "Please Enter Your Full Name", Toast.LENGTH_LONG).show();
            editTextUpdateName.setError("Full Name is Required");
            editTextUpdateName.requestFocus();
        }else if (TextUtils.isEmpty(textDoB)){
            Toast.makeText(UpdateProfileActivity.this, "Please Enter Your Date Of Birth", Toast.LENGTH_LONG).show();
            editTextUpdateDoB.setError("Date of Birth is Required");
            editTextUpdateDoB.requestFocus();
        }else if (TextUtils.isEmpty(radioButtonUpdateGenderSelected.getText())) {
            Toast.makeText(UpdateProfileActivity.this, "Please Select Your Gender", Toast.LENGTH_LONG).show();
            radioButtonUpdateGenderSelected.setError("Gender is Required");
            radioButtonUpdateGenderSelected.requestFocus();
        }else if (TextUtils.isEmpty(textMobile)){
            Toast.makeText(UpdateProfileActivity.this, "Please Enter Your Mobile No.", Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile No. is Required");
            editTextUpdateMobile.requestFocus();
        }else if (textMobile.length() != 10){
            Toast.makeText(UpdateProfileActivity.this, "Please Re-Enter Your Mobile No.", Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile No. should be of 10 Digits");
            editTextUpdateMobile.requestFocus();
        }else if(!mobileMatcher.find()){
            Toast.makeText(UpdateProfileActivity.this, "Please Re-Enter Your Mobile No.", Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile No. is Not Valid");
            editTextUpdateMobile.requestFocus();
        } else {
            textGender = radioButtonUpdateGenderSelected.getText().toString();
            textFullName = editTextUpdateName.getText().toString();
            textDoB = editTextUpdateDoB.getText().toString();
            textMobile = editTextUpdateMobile.getText().toString();

            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDoB, textGender, textMobile);

            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

            String userID = firebaseUser.getUid();
            progressBar.setVisibility(View.VISIBLE);

            referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        UserProfileChangeRequest prfileUpdates = new UserProfileChangeRequest.Builder().
                                setDisplayName(textFullName).build();
                        firebaseUser.updateProfile(prfileUpdates);

                        Toast.makeText(UpdateProfileActivity.this, "Updating Successful!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(UpdateProfileActivity.this, UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void showProfile(FirebaseUser firebaseUser) {
        String userIDofRegistered = firebaseUser.getUid();

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

        progressBar.setVisibility(View.VISIBLE);

        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readWriteUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if(readWriteUserDetails != null) {
                    textFullName = firebaseUser.getDisplayName();
                    textDoB = readWriteUserDetails.dob;
                    textGender = readWriteUserDetails.gender;
                    textMobile = readWriteUserDetails.mobile;

                    editTextUpdateName.setText(textFullName);
                    editTextUpdateDoB.setText(textDoB);
                    editTextUpdateMobile.setText(textMobile);

                    if(textGender.equals("Male")) {
                        radioButtonUpdateGenderSelected = findViewById(R.id.radio_male);
                    } else {
                        radioButtonUpdateGenderSelected = findViewById(R.id.radio_female);
                    }
                    radioButtonUpdateGenderSelected.setChecked(true);
                } else {
                    Toast.makeText(UpdateProfileActivity.this, "Something Went Wrong! Read", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this, "Something Went Wrong! Database", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(UpdateProfileActivity.this);
        } else if (id == R.id.menu_refresh) {
            startActivity(getIntent());
            finish();
        } else if (id == R.id.menu_update_profile) {
            Intent intent = new Intent(UpdateProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(UpdateProfileActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(UpdateProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        }else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(UpdateProfileActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(UpdateProfileActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UpdateProfileActivity.this, MainActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(UpdateProfileActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}