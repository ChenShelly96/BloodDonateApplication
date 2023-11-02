package com.androidapp.blooddonate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    EditText firstName, secondName, id, email, phone, password;
    Button signupBtn;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_registration);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        firstName = (EditText) findViewById(R.id.userName);
        secondName = (EditText) findViewById(R.id.userFamName);
        id = (EditText) findViewById(R.id.idUser);
        email = (EditText) findViewById(R.id.email_edit_text);
        phone = (EditText) findViewById(R.id.phone_edit_text);
        password = (EditText) findViewById(R.id.password_edit_text);
        signupBtn = (Button)findViewById(R.id.register_button);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fNameStr = checkName(firstName);
                String lNameStr = checkName(secondName);
                String idText = checkId();
                String emailText = checkEmail();
                String phoneText = checkPhone();
                String passwordText = checkPassword();

                if(fNameStr != null && lNameStr != null && idText != null && emailText != null
                        && phoneText != null && passwordText != null){
                    firebaseAuth.createUserWithEmailAndPassword(emailText, passwordText)
                            .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(fNameStr + " " + lNameStr).build();

                                        Map<String, Object> userData = new HashMap<String, Object>();
                                        userData.put("First name", fNameStr);
                                        userData.put("Last name", lNameStr);
                                        userData.put("Id", idText);
                                        userData.put("Email", emailText);
                                        userData.put("Phone", phoneText);
                                        userData.put("Password", passwordText);//TODO save password in more secure way

                                        firestore.collection(getString(R.string.users_database_name))
                                                        .document(user.getUid()).set(userData);

                                        user.updateProfile(changeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                                            }
                                        });
                                    }
                                    else{
                                        Log.e("sign up", task.getException().getMessage());
                                        Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });

    }

    private String checkName(EditText name){
        String regEx = "\\b([A-ZÀ-ÿא-ת][-,a-z. 'א-ת]+[ ]*)+";

        String nameText = name.getText().toString().trim();

        if(nameText.isEmpty()){
            name.setError("שם חייב להיות לא ריק");
            name.requestFocus();
            return null;
        }
        if(!nameText.matches(regEx)){
            name.setError("שם צריך להכיל רק אותיות ורווחים");
            name.requestFocus();
            return null;
        }

        name.setError(null);
        return nameText;
    }

    private String checkId(){
        String textId = id.getText().toString().trim();

        if(textId.length() != 9){
            id.setError("תעודת זהות צריכה להיות עם 9 ספרות");
            id.requestFocus();
            return null;
        }

        int checkSum = 0;
        for(int i = 0; i < textId.length(); i++){
            int num = Character.getNumericValue(textId.charAt(i));
            num *= i%2 + 1;
            if(num >= 10){
                num = num / 10 + num % 10;
            }
            checkSum += num;
        }

        if(checkSum % 10 != 0){
            id.setError("תעודת זהות איזה תקינה");
            id.requestFocus();
            return null;
        }

        id.setError(null);
        return textId;
    }

    private String checkEmail(){
        String emailText = email.getText().toString().trim();

        if(emailText.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            email.setError("אימייל לא תקין");
            email.requestFocus();
            return null;
        }

        email.setError(null);
        return  emailText;
    }

    private String checkPhone(){
        String phoneText = phone.getText().toString().trim();

        if(phoneText.length() != 10){
            phone.setError("מספר טלפון לא תקין");
            phone.requestFocus();
            return null;
        }

        phone.setError(null);
        return phoneText;
    }

    private String checkPassword(){
        String passwordText = password.getText().toString().trim();

        if(passwordText.length() < 6){
            password.setError("סיסמה חייבת להיות ארוכה מ6 תווים");
            password.requestFocus();
            return null;
        }

        return passwordText;
    }
}