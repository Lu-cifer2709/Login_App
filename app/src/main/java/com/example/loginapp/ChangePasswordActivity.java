package com.example.loginapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private EditText editTextPwdCurr, editTextPwdNew, editTextPwdConfirmNew;
    private TextView textViewAuthenticated;
    private Button btnChangePwd, btnReAuthenticate;
    private ProgressBar progressBar;
    private String userPwdCurr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextPwdNew = findViewById(R.id.editText_change_pwd_new);
        editTextPwdCurr = findViewById(R.id.editText_change_pwd_current);
        editTextPwdConfirmNew = findViewById(R.id.editText_change_pwd_new_confirm);
        textViewAuthenticated = findViewById(R.id.textView_change_pwd_authenticated);
        progressBar = findViewById(R.id.progressBar_UpE);
        btnReAuthenticate = findViewById(R.id.btn_change_pwd_authenticate);
        btnChangePwd = findViewById(R.id.btn_change_pwd);

        editTextPwdNew.setEnabled(false);
        editTextPwdConfirmNew.setEnabled(false);
        btnChangePwd.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser.equals("")) {
            Toast.makeText(ChangePasswordActivity.this, "Something Went Wrong! User Details Not Available", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePasswordActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
        } else {
            reAuthenticateUser(firebaseUser);
        }
    }

    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        btnReAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPwdCurr = editTextPwdCurr.getText().toString();

                if(TextUtils.isEmpty(userPwdCurr)) {
                    Toast.makeText(ChangePasswordActivity.this, "Password is needed to continue", Toast.LENGTH_SHORT).show();
                    editTextPwdCurr.setError("Please Enter Your Password for Authentication");
                    editTextPwdCurr.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwdCurr);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);

                                editTextPwdCurr.setEnabled(false);
                                editTextPwdNew.setEnabled(true);
                                editTextPwdConfirmNew.setEnabled(true);

                                btnReAuthenticate.setEnabled(false);
                                btnChangePwd.setEnabled(true);

                                textViewAuthenticated.setText("You are authenticated. You can Change your Password Now!");

                                Toast.makeText(ChangePasswordActivity.this, "Password has been Verified." +
                                        "You Can Change Password Now!", Toast.LENGTH_LONG).show();

                                btnChangePwd.setBackgroundTintList(ContextCompat.getColorStateList(ChangePasswordActivity.this,
                                        R.color.teal_700));

                                btnChangePwd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        changePwd(firebaseUser);
                                    }
                                });

                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void changePwd(FirebaseUser firebaseUser) {
        String userPwdNew = editTextPwdNew.getText().toString();
        String userPwdConfirmNew = editTextPwdConfirmNew.getText().toString();

        if(TextUtils.isEmpty(userPwdNew)) {
            Toast.makeText(ChangePasswordActivity.this, "New Password is needed", Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("Please Enter Your New Password");
            editTextPwdNew.requestFocus();
        }else if(TextUtils.isEmpty(userPwdConfirmNew)) {
            Toast.makeText(ChangePasswordActivity.this, "Please Confirm your New Password", Toast.LENGTH_SHORT).show();
            editTextPwdConfirmNew.setError("Please Re-Enter Your New Password");
            editTextPwdConfirmNew.requestFocus();
        }else if(!userPwdNew.matches(userPwdConfirmNew)) {
            Toast.makeText(ChangePasswordActivity.this, "Password Didn't Match", Toast.LENGTH_SHORT).show();
            editTextPwdConfirmNew.setError("Please Re-enter Same Password");
            editTextPwdConfirmNew.requestFocus();
        }else if(userPwdCurr.matches(userPwdNew)) {
            Toast.makeText(ChangePasswordActivity.this, "New Password cannot be Same as Old Password", Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("Please Enter a New Password");
            editTextPwdNew.requestFocus();
        } else {
            progressBar.setVisibility(View.VISIBLE);

            firebaseUser.updatePassword(userPwdNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                  if(task.isSuccessful()) {
                      Toast.makeText(ChangePasswordActivity.this, "Password has been Changed", Toast.LENGTH_SHORT).show();
                      Intent intent = new Intent(ChangePasswordActivity.this, UserProfileActivity.class);
                      startActivity(intent);
                      finish();
                  } else {
                      try {
                          throw task.getException();
                      } catch (Exception e) {
                          Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                      }
                  }
                  progressBar.setVisibility(View.GONE);
                }
            });
        }
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
            NavUtils.navigateUpFromSameTask(ChangePasswordActivity.this);
        } else if (id == R.id.menu_refresh) {
            startActivity(getIntent());
            finish();
        } else if (id == R.id.menu_update_profile) {
            Intent intent = new Intent(ChangePasswordActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(ChangePasswordActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(ChangePasswordActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        }else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(ChangePasswordActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(ChangePasswordActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(ChangePasswordActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

}