package com.example.news_sqlite.Auth;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;


import com.example.news_sqlite.MainActivity;
import com.example.news_sqlite.MainActivitySec;
import com.example.news_sqlite.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import androidx.biometric.BiometricPrompt;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.Executor;

public class Auth extends AppCompatActivity {
    Button sign;
    TextView reg;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    Spinner spinner;
    ConstraintLayout root;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    int x = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        sign = findViewById(R.id.sign);
        reg = findViewById(R.id.register);
        spinner = findViewById(R.id.name1);
        root = findViewById(R.id.root_element);
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                notifyUser("?????????????????????? ????????????????!");
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference usuariosRef = rootRef.child("????????????????????????");
                usuariosRef.child(uid).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        TextView pri = findViewById(R.id.textView2);
                        String prov = String.valueOf(task.getResult().getValue());
                        pri.setText("."+prov+".");
                        String priemcomb = "??????????????????????????";
                        String priemcomb1 = "????????????????";

                        if (prov.length() == priemcomb.length())
                        {
                            Intent i = new Intent(new Intent(Auth.this, MainActivity.class));
                            startActivity(i);
                            finish();
                        }
                        else if (prov.length() == priemcomb1.length())
                        {
                            Intent i = new Intent(new Intent(Auth.this, MainActivitySec.class));
                            startActivity(i);
                            finish();
                        }
                        else if (x == 2)
                        {
                            notifyUser("????????????!");
                        }
                    }
                });
            }
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                notifyUser("?????????????????????? ???? ????????????????!");
            }
        });
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("????????????????????????");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent i = new Intent(new Intent(Auth.this, MainActivity.class));
            startActivity(i);
        }else{
            reg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRegisterWindow();
                }
            });
            sign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSignInWindow();
                }
            });
        }

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Who are you?")
                .setSubtitle("Travis Travis Scott")
                .setNegativeButtonText("????????????")
                .build();
    }

    private void showSignInWindow(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("??????????");
        dialog.setMessage("?????????????? ?????? ???????????? ?????? ??????????");

        LayoutInflater inflater = LayoutInflater.from(this);
        View sing_in_window = inflater.inflate(R.layout.activity_sign, null);
        dialog.setView(sing_in_window);

        TextView email = sing_in_window.findViewById(R.id.email);
        TextView pass = sing_in_window.findViewById(R.id.password);
        Spinner spinner = sing_in_window.findViewById(R.id.name1);
        dialog.setNegativeButton("????????????????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        dialog.setPositiveButton("??????????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(email.getText().toString())){
                    Snackbar.make(root, "?????????????? ???????? ??????????", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (pass.getText().toString().length() < 5){
                    Snackbar.make(root, "?????????????? ?????? ????????????", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                auth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                biometricPrompt.authenticate(promptInfo);


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(root,  e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });
        dialog.show();
    }

    private void notifyUser(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showRegisterWindow() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("????????????????????????????????????");
        dialog.setMessage("?????????????? ?????? ???????????? ?????? ??????????????????????");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_window = inflater.inflate(R.layout.activity_register, null);
        dialog.setView(register_window);

        TextView email = register_window.findViewById(R.id.email);
        TextView pass = register_window.findViewById(R.id.password);
        Spinner spinner = register_window.findViewById(R.id.name1);


        dialog.setNegativeButton("????????????????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        dialog.setPositiveButton("????????????????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(email.getText().toString())){
                    Snackbar.make(root, "?????????????? ???????? ??????????", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(pass.getText().toString())){
                    Snackbar.make(root, "?????????????? ???????????? ???????????? 5-???? ???????????? ???? ????????. ??????????????????", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                String priemcomb = spinner.getSelectedItem().toString();
                if (TextUtils.isEmpty(priemcomb)){
                    Snackbar.make(root, "?????????????? ???????? ????????", Snackbar.LENGTH_SHORT).show();
                    return;
                }

//              ?????????????????????? ????????????????????????
                auth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                User user = new User();
                                user.setEmail(email.getText().toString());
                                user.setPass(pass.getText().toString());
                                user.setName(priemcomb);

                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(avoid -> Snackbar.make(root, "???????????????????????? ????????????????!", Snackbar.LENGTH_SHORT).show()).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(root, "???????????? ???????????????? ????????????????????????!" + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
            }
        });
        dialog.show();
    }
}