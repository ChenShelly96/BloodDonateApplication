package com.androidapp.blooddonate;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity2 extends AppCompatActivity {
    private FirebaseAuth mAuth;
    public MediaPlayer mMediaPlayer = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button1 = (Button) findViewById(R.id.button_appointment);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SpinnerActivity.class);
                view.getContext().startActivity(intent);}
        });

        Log.d("result", "main activity");



    }


    public void loginFunc(View view) {
        EditText email_text = findViewById(R.id.username_edit_text);
        EditText pass_text = findViewById(R.id.password_edit_text);
        String email = email_text.getText().toString();
        String password = pass_text.getText().toString();

        if (password.isEmpty() || email.isEmpty()) {
            Toast.makeText(MainActivity2.this, "Please fill the details below!", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d("result", email + " " + password);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(MainActivity2.this, "Login successful!", Toast.LENGTH_LONG).show();
                            //added:
                            //setContentView(R.layout.activity_sec);

                            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_navigation_home);


                            Log.d("result", "login done!");
                            //Navigation.findNavController(view).navigate(R.id.action_secFragment_to_afterLoginFragment);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity2.this, "Login failed! Try again", Toast.LENGTH_LONG).show();

                        }

                    }
                });
    }

    public boolean passwordsValidation(String pass, String validPass) {
        if (pass.equals(validPass))
            return true;
        return false;
    }



    public void RegisterFunc(View view) {


        EditText email_text = findViewById(R.id.email_edittext);
        EditText pass_text = findViewById(R.id.password_edit_text);
        //EditText passValid_text = findViewById(R.id.tbPasswordValidation);
        EditText phone_text = findViewById(R.id.phone_number_edit_text);
        EditText name_text = findViewById(R.id.userName);

        String phone = phone_text.getText().toString();
        String name = name_text.getText().toString();
        //String passwordValid = passValid_text.getText().toString();
        String email = email_text.getText().toString();
        String password = pass_text.getText().toString();

        if (phone.isEmpty() || name.isEmpty() || password.isEmpty() || email.isEmpty() ) {
            Toast.makeText(MainActivity2.this, "Please fill all the form!", Toast.LENGTH_LONG).show();
            return;
        }

      /*  if (!passwordsValidation(password, passwordValid)) {
            Toast.makeText(MainActivity2.this, "Passwords didn't match!", Toast.LENGTH_LONG).show();
            return;
        }*/


        Log.d("result", email + " " + password);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(MainActivity2.this, "Register successful!", Toast.LENGTH_LONG).show();
                            //setContentView(R.layout.activity_sec);
                            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_navigation_home);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity2.this, "Register failed!", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

}