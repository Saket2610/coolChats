package com.example.coolchats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.coolchats.Models.Users;
import com.example.coolchats.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    FirebaseDatabase database;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
//public void fun(View view){
//    Toast.makeText(this, "ssssss", Toast.LENGTH_SHORT).show();
//}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        Button button = (Button) findViewById(R.id.btnSignUp);

//        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().hide();
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("v r creating ur account");

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!binding.txtUsername.getText().toString().isEmpty() && !binding.txtPassword.getText().toString().isEmpty() &&!binding.txtEmail.getText().toString().isEmpty()){
                    progressDialog.show();
                    mAuth.createUserWithEmailAndPassword(binding.txtEmail.getText().toString(),binding.txtPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if(task.isSuccessful()){
                                        Users user = new Users(binding.txtUsername.getText().toString(),binding.txtEmail.getText().toString(),binding.txtPassword.getText().toString());
                                        String id = task.getResult().getUser().getUid();
                                        database.getReference().child("Users").child(id).setValue(user);

                                        Toast.makeText(SignUpActivity.this, "SignUp successful", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(SignUpActivity.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(SignUpActivity.this, "GIVE UR FKING CREDENTIAL", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.AlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this,SignInAcivity.class);
                startActivity(intent);
            }
        });
    }
}