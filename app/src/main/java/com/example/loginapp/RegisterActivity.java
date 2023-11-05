package com.example.loginapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private EditText editText_RFullName, editText_REmail, editText_RDob, editText_RMobile, editText_RPassword, editText_RConfirm_Password;
    private ProgressBar progressBar;
    private RadioGroup radioGroup_Gender;
    private RadioButton radioButton_RGenderSelected;
    private DatePickerDialog picker;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Toast.makeText(RegisterActivity.this, "You can Register now", Toast.LENGTH_LONG).show();

        progressBar = findViewById(R.id.progressBar);
        editText_RFullName = findViewById(R.id.editText_RFullName);
        editText_REmail = findViewById(R.id.editText_REmail);
        editText_RDob = findViewById(R.id.editText_RDob);
        editText_RMobile = findViewById(R.id.editText_RMobile);
        editText_RPassword = findViewById(R.id.editText_RPassword);
        editText_RConfirm_Password = findViewById(R.id.editText_RConfirm_Password);

        radioGroup_Gender = findViewById(R.id.radioGroup_Gender);
        radioGroup_Gender.clearCheck();

        editText_RDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);


                picker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editText_RDob.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        ImageView imageViewShowhidePwd = findViewById(R.id.imageView_RSHPwd);
        imageViewShowhidePwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewShowhidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText_RPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    editText_RPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewShowhidePwd.setImageResource(R.drawable.ic_hide_pwd);
                } else {
                    editText_RPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowhidePwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        ImageView imageViewShowhideCoPwd = findViewById(R.id.imageView_RSHCoPwd);
        imageViewShowhideCoPwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewShowhideCoPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText_RConfirm_Password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    editText_RConfirm_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewShowhideCoPwd.setImageResource(R.drawable.ic_hide_pwd);
                } else {
                    editText_RConfirm_Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowhideCoPwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectGenderId = radioGroup_Gender.getCheckedRadioButtonId();
                radioButton_RGenderSelected = findViewById(selectGenderId);

                String textFullName = editText_RFullName.getText().toString();
                String textEmail = editText_REmail.getText().toString();
                String textDob = editText_RDob.getText().toString();
                String textMobile = editText_RMobile.getText().toString();
                String textPassword = editText_RPassword.getText().toString();
                String textConfirmPassword = editText_RConfirm_Password.getText().toString();
                String textGender;

                String mobileRegex = "[6-9][0-9]{9}";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(textMobile);


// Usage:
                if (TextUtils.isEmpty(textFullName)) {
                    showErrorAndFocus(editText_RFullName, "Full Name is Required", "Please Enter Your Full Name");
                } else if (TextUtils.isEmpty(textEmail)) {
                    showErrorAndFocus(editText_REmail, "Email is Required", "Please Enter Your Email");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    showErrorAndFocus(editText_REmail, "Valid Email is Required", "Please Re-Enter Your Email");
                } else if (TextUtils.isEmpty(textDob)) {
                    showErrorAndFocus(editText_RDob, "Date of Birth is Required", "Please Enter Your Date Of Birth");
                } else if (radioGroup_Gender.getCheckedRadioButtonId() == -1){
                    Toast.makeText(RegisterActivity.this, "Please Select Your Gender", Toast.LENGTH_LONG).show();
                    radioButton_RGenderSelected.setError("Gender is Required");
                    radioButton_RGenderSelected.requestFocus();
                } else if (TextUtils.isEmpty(textMobile)) {
                    showErrorAndFocus(editText_RMobile, "Mobile No. is Required", "Please Enter Your Mobile No.");
                } else if (textMobile.length() != 10) {
                    showErrorAndFocus(editText_RMobile, "Mobile No. should be of 10 Digits", "Please Re-Enter Your Mobile No.");
                } else if (!mobileMatcher.find()) {
                    showErrorAndFocus(editText_RMobile, "Mobile No. is Not Valid", "Please Re-Enter Your Mobile No.");
                } else if (TextUtils.isEmpty(textPassword)) {
                    showErrorAndFocus(editText_RPassword, "Password is Required", "Please Enter Your Password");
                } else if (textPassword.length() < 6) {
                    showErrorAndFocus(editText_RPassword, "Password is too weak, try a combination of numbers, special characters, and alphabets", "Please Re-Enter Your Password");
                } else if (!containsSpecialCharacter(textPassword)) {
                    showErrorAndFocus(editText_RPassword, "Password must contain at least one special character", "Please Re-Enter Your Password");
                } else if (TextUtils.isEmpty(textConfirmPassword)) {
                    showErrorAndFocus(editText_RConfirm_Password, "Password Confirmation is Required", "Please Confirm Your Password");
                } else if (!textPassword.equals(textConfirmPassword)) {
                    showErrorAndFocus(editText_RConfirm_Password, "Password Confirmation is Required", "Please Enter the Same Password");
                    editText_RPassword.clearComposingText();
                    editText_RConfirm_Password.clearComposingText();
                } else {
                    textGender = radioButton_RGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName, textEmail, textDob, textGender, textMobile, textPassword);
                    progressBar.setVisibility(View.GONE);
                }

            }
        });
    }

    private boolean containsSpecialCharacter(String password) {
        // Define a regular expression to check for special characters
        String specialCharacterPattern = "[!@#$%^&*()_+\\-=\\[\\]{};':\",.<>?]";
        return password.matches(".*" + specialCharacterPattern + ".*");
    }

    private void showErrorAndFocus(EditText editText, String errorMessage, String toastMessage) {
        Toast.makeText(RegisterActivity.this, toastMessage, Toast.LENGTH_LONG).show();
        editText.setError(errorMessage);
        editText.requestFocus();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                editText.setError(null);
            }
        }, 4000);
    }

    private void registerUser(String textFullName, String textEmail, String textDob, String textGender, String textMobile, String textPassword){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDob, textGender, textMobile);

                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                firebaseUser.sendEmailVerification();
                                Toast.makeText(RegisterActivity.this, "User Registered Successfully, Please verify your Email", Toast.LENGTH_LONG).show();

//                                //Open User Profile After Verification
                                Intent intent = new Intent(RegisterActivity.this, UserProfileActivity.class);
//
//                                //To Prevent user from going back to Registeration Page
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish(); //To Close Register Activity


                            }else {
                                Toast.makeText(RegisterActivity.this, "Registration Failed, Try Again", Toast.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });


                }else {
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e) {
                        editText_RPassword.setError("Your Password is too weak, Use a mix of characters of alphanumeric and symbols");
                        editText_RPassword.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e) {
                        editText_REmail.setError("Your Email is invalid or Already in Use, Please Check Again");
                        editText_REmail.requestFocus();
                    }catch (FirebaseAuthUserCollisionException e) {
                        editText_REmail.setError("User is already register with this Email.Use Another Email");
                        editText_REmail.requestFocus();
                    }catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(RegisterActivity.this);
        }
            return super.onOptionsItemSelected(item);
    }

}