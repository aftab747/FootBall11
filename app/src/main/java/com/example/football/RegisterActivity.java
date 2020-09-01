package com.example.football;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText usernameIn,emailIn,passwordIn;
    Button register;
    TextView text_login;

    FirebaseAuth mFirebaseAuth;
    DatabaseReference mDatabaseReference;
    ProgressDialog mProgressDialog;
    FirebaseUser firebaseUser;
    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        //redirected if user is not null
        if(firebaseUser!=null){
            startActivity(new Intent(RegisterActivity.this,MainActivity.class));
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameIn=findViewById(R.id.username);
        emailIn=findViewById(R.id.email);
        passwordIn=findViewById(R.id.pasword);
        register=findViewById(R.id.register);
        text_login=findViewById(R.id.text_login);

        mFirebaseAuth=FirebaseAuth.getInstance();
        text_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = new ProgressDialog(RegisterActivity.this);
                mProgressDialog.setMessage("Please wait...");
                mProgressDialog.show();

                String str_username=usernameIn.getText().toString();
                String str_email=emailIn.getText().toString();
                String str_password=passwordIn.getText().toString();

                if(TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_email) ||
                        TextUtils.isEmpty(str_password )) {

                    mProgressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                }
                else if(str_password.length()<6){
                    mProgressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Password must have 6 characters", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    registeruser(str_username,str_email,str_password);
                }


            }
        });

    }
    private void registeruser(final String username, final String email, final String password){
        mFirebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            firebaseUser=mFirebaseAuth.getCurrentUser();
                            String userid=firebaseUser.getUid();
                            mDatabaseReference= FirebaseDatabase.getInstance().getReference()
                                    .child("Users").child(userid);

                            HashMap<String,Object> hashMap=new HashMap<>();
                            hashMap.put("id",userid);
                            hashMap.put("username",username.toLowerCase());
                            hashMap.put("password",password);
                            hashMap.put("Email",email);
                            mDatabaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        mProgressDialog.dismiss();
                                        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            });

                        }
                        else {
                            mProgressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });







    }
}
