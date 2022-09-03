package org.first.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetActivity extends AppCompatActivity {
        private TextInputEditText editTextForget;
        private Button btnForget;

        FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.forget)));
        getWindow().setStatusBarColor(getResources().getColor(R.color.forget));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        editTextForget = findViewById(R.id.editTextForget);
        btnForget = findViewById(R.id.buttonForget);

        btnForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextForget.getText().toString();
                if(!email.equals("")){
                    passwordReset(email);
                }
            }
        });

        auth = FirebaseAuth.getInstance();
    }
    public void passwordReset(String email){
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgetActivity.this, "Please check your email.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ForgetActivity.this, "There is a problem", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}