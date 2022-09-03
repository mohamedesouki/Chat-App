package org.first.mychatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

            private CircleImageView imageViewCircle;
            private TextInputEditText editTextEmailSignUp,editTextPasswordSignUp,editTextNameSignUp;
            private Button btnregister;
            Boolean imageControl = false;
            Uri imageUri;

            FirebaseAuth auth;
            FirebaseDatabase database;
            DatabaseReference reference;

            FirebaseStorage firebaseStorage;
            StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.mine)));
        getWindow().setStatusBarColor(getResources().getColor(R.color.mine));

        imageViewCircle = findViewById(R.id.imageViewCircle);
        editTextEmailSignUp = findViewById(R.id.editTextEmailSignUp);
        editTextNameSignUp=findViewById(R.id.editTextNameSignUp);
        editTextPasswordSignUp = findViewById(R.id.editTextPasswordSignUp);
        btnregister = findViewById(R.id.buttonforSignUp);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();



        imageViewCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmailSignUp.getText().toString();
                String userName = editTextNameSignUp.getText().toString();
                String password = editTextPasswordSignUp.getText().toString();

                if(!email.equals("") && !userName.equals("") &&!password.equals("")){
                    signUP(email,password,userName);
                }
            }
        });
    }

    public void imageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode == RESULT_OK && data!=null){
             imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageViewCircle);
            imageControl=true;
        }else {
            imageControl = false;
        }
    }
    public void signUP(String email,String password , final String userName){
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        reference.child("Users").child(auth.getUid()).child("userName").setValue(userName);

                        if(imageControl){
                            UUID randomID = UUID.randomUUID();
                           final String imageName = "images/" + randomID+".jpg";
                            storageReference.child(imageName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    StorageReference myStorageRef = firebaseStorage.getReference(imageName);
                                    myStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String filePath = uri.toString();
                                            reference.child("Users").child(auth.getUid()).child("image").setValue(filePath).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(SignUpActivity.this, "Successful", Toast.LENGTH_LONG).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(SignUpActivity.this, "Failed", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }else {
                            reference.child("Users").child(auth.getUid()).child("image").setValue("null");
                        }
                        Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                        //intent.putExtra("userName" , userName);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(SignUpActivity.this, "There is a problem", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
}