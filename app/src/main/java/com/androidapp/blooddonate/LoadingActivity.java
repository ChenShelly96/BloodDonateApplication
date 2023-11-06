package com.androidapp.blooddonate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoadingActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        continueToMainActivity();
    }

    private void continueToMainActivity(){
        FirebaseUser user = firebaseAuth.getCurrentUser();



        if(user == null){
            Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        DocumentReference doc = firestore.collection(getString(R.string.users_database_name)).document(user.getUid());

        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot snapshot = task.getResult();

                Intent intent;

                if(snapshot.exists()){
                    intent = new Intent(LoadingActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                else{
                    intent = new Intent(LoadingActivity.this, RegistrationActivity.class);
                    intent.putExtra("registered", true);
                }
                startActivity(intent);
            }
        });
    }
}