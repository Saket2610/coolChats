package com.example.coolchats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.coolchats.Models.Users;
import com.example.coolchats.databinding.ActivitySignInAcivityBinding;
import com.example.coolchats.databinding.ActivitySignUpBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class SignInAcivity extends AppCompatActivity {

    ActivitySignInAcivityBinding binding;
    FirebaseDatabase firebaseDatabase;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInAcivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(SignInAcivity.this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("please wait\n , validation in progress");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!binding.txtEmail.getText().toString().isEmpty() && !binding.txtPassword.getText().toString().isEmpty()){
                    progressDialog.show();
                    mAuth.signInWithEmailAndPassword(binding.txtEmail.getText().toString(),binding.txtPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(SignInAcivity.this,MainActivity.class);
                                        startActivity(intent);
                                    }
                                    else {
                                        Toast.makeText(SignInAcivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(SignInAcivity.this, "Enter Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(mAuth.getCurrentUser()!=null){
            Intent intent = new Intent(SignInAcivity.this,MainActivity.class);
            startActivity(intent);
        }
        binding.ClickSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInAcivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }
    int RC_SIGN_IN = 65;
    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task= GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            }
            catch (ApiException e){
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d("TAG",  "SignInWithCredential:success");
                    FirebaseUser user = mAuth.getCurrentUser();

                    Users users = new Users();
                    users.setUserId(user.getUid());
                    users.setUserName(user.getDisplayName());
                    users.setProfilePic(user.getPhotoUrl().toString());
                    firebaseDatabase.getReference().child("Users").child(user.getUid()).setValue(users);
                    Intent intent = new Intent(SignInAcivity.this,MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(SignInAcivity.this, "Sign in with Google", Toast.LENGTH_SHORT).show();
                    //updateUI(user);
                }
                else{
                    Log.w("TAG", "SignInWithCredential : failure ", task.getException() );

                }
            }
        });
    }
}