package com.androidapp.blooddonate;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import com.androidapp.blooddonate.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    private ActivityMainBinding binding;

    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null){
            TextView nameTextView = findViewById(R.id.nameUserOutput);
            nameTextView.setText(firebaseUser.getDisplayName());
        }

        //TODO delete the signout option and button
        googleSignInClient = GoogleSignIn.getClient(MainActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        findViewById(R.id.signout).setOnClickListener(view -> {
                    // Sign out from google
                    googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Check condition
                            if (task.isSuccessful()) {
                                // When task is successful sign out from firebase
                                firebaseAuth.signOut();
                                // Display Toast
                                Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT).show();
                                // Finish activity
                                finish();
                            }
                        }
                    });
                });

        //binding = ActivityMainBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());
        //setContentView(R.layout.fragment_login);
        /*BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);*/
    }

    public void loginFunc(View view) {
        EditText name_text = findViewById(R.id.username_edit_text);
        EditText pass_text = findViewById(R.id.password_edit_text);
        String name = name_text.getText().toString();
        String password = pass_text.getText().toString();

        if (password.isEmpty() || name.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please fill the details below!", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d("result", name + " " + password);

        firebaseAuth.signInWithEmailAndPassword(name, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_LONG).show();


                            Navigation.findNavController(view).navigate(R.id.activity_main);


                            Log.d("result", "login done!");
                            //Navigation.findNavController(view).navigate(R.id.action_secFragment_to_afterLoginFragment);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Login failed! Try again", Toast.LENGTH_LONG).show();

                        }

                    }
                });
    }

    public boolean passwordsValidation(String pass, String validPass) {
        if (pass.equals(validPass))
            return true;
        return false;
    }


}