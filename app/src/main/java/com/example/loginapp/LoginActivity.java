package com.example.loginapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText editText_LEmail, editText_LPassword;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG = "LoginActivity23";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editText_LEmail = findViewById(R.id.editText_LEmail);
        editText_LPassword = findViewById(R.id.editText_LPassword);
        progressBar = findViewById(R.id.progressBar_L);

        authProfile = FirebaseAuth.getInstance();

        TextView textViewLinkResetPwd = findViewById(R.id.textview_forgot_password_link);
        textViewLinkResetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "You Can Reset Your Password Now!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        TextView textViewLinkRegister = findViewById(R.id.textview_register_link);
        textViewLinkRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "You Can Reset Your Password Now!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        ImageView imageViewShowhidePwd = findViewById(R.id.imageView_SHPassword);
        imageViewShowhidePwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewShowhidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText_LPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    editText_LPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewShowhidePwd.setImageResource(R.drawable.ic_hide_pwd);
                } else {
                    editText_LPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowhidePwd.setImageResource(R.drawable.ic_show_pwd);  
                }
            }
        });

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail = editText_LEmail.getText().toString();
                String textPassword = editText_LPassword.getText().toString();

                if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(LoginActivity.this, "Please Enter Your Email", Toast.LENGTH_LONG).show();
                    editText_LEmail.setError("Email is Required");
                    editText_LEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(LoginActivity.this, "Please Re-Enter Your Email", Toast.LENGTH_LONG).show();
                    editText_LEmail.setError("Valid Email is Required");
                    editText_LEmail.requestFocus();
                } else if (TextUtils.isEmpty(textPassword)) {
                    Toast.makeText(LoginActivity.this, "Please Enter Your Password", Toast.LENGTH_LONG).show();
                    editText_LPassword.setError("Password is Required");
                    editText_LPassword.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail, textPassword);
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        authProfile.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser firebaseUser = authProfile.getCurrentUser();
                    Toast.makeText(LoginActivity.this, "You are Logged in now", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
                    finish();
                    
//                    if(firebaseUser.isEmailVerified()){
//                        Toast.makeText(LoginActivity.this, "You are Logged in now", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
//                        finish();
//                    } else {
//                        firebaseUser.sendEmailVerification();
////                        authProfile.signOut();
//                        showAlertDialog();
//                        Toast.makeText(LoginActivity.this, "You are Logged in now", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
//                        finish();
//                    }

                }else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        editText_LEmail.setError("User Doesn't Exist or is No Longer Valid, Please Register Again");
                        editText_LEmail.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        editText_LEmail.setError("Invalid Password, Kindly Check Again");
                        editText_LEmail.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage() , Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please Verify Your Email Now, You Can't Login Without Email Verification.");

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(authProfile.getCurrentUser() != null){
            Toast.makeText(LoginActivity.this, "Already Logged In!", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
            finish();
        } else {
            Toast.makeText(LoginActivity.this, "You Can Login Now!", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(LoginActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }
}