package com.example.mycourses;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    //created variables for edit text, button ProgressBar and TextInputEditText i.e the username and the password
    /*
    The TextInputEditText is sub class EditText which gives a special floating action to the
    input fields.
     */

    private TextInputEditText userNameEditText, passwordEditText; //variables to take inputs of the username and the password

    private Button loginButton; //this is the login button
    /*
    if the user is new and not been registered yet so, the user needs to go for the
    registering self first and for which the text inside textView is displayed
     */

    private TextView newUserTextView;
    private FirebaseAuth mAuth;
    private ProgressBar loadingProgBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //for now the initialising the variables will take place
        userNameEditText = findViewById(R.id.idEdtUserName); //comes from the username id defined in Login xml
        passwordEditText = findViewById(R.id.idEdtPassword); //comes from the username id defined in Login xml
        loginButton = findViewById(R.id.idBtnLogin); //comes from the username id defined in Login xml
        newUserTextView = findViewById(R.id.idTVNewUser); //comes from the username id defined in Login xml
        //this will be used to get the instance of the firebase.
        mAuth = FirebaseAuth.getInstance();
        //this will initialise the progress bar in the login page.
        loadingProgBar = findViewById(R.id.idPBLoading);
        //this will take the user on the registration page.
        newUserTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //on below line opening a login activity.
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        //this is the click listener for the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgBar.setVisibility(View.VISIBLE);
                //getting data from our edit text on below line.
                String inputEmail = Objects.requireNonNull(userNameEditText.getText()).toString();
                String inputPassword = Objects.requireNonNull(passwordEditText.getText()).toString();
                //the email and the password will be validated here
                if (TextUtils.isEmpty(inputEmail) && TextUtils.isEmpty(inputPassword)) {
                    Toast.makeText(LoginActivity.this, "Please enter your credentials..", Toast.LENGTH_SHORT).show();
                } else {
                    //here the we are calling the onComplete where the email and password is passed with signIn method.
                    mAuth.signInWithEmailAndPassword(inputEmail, inputPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                             /*
                            here the actual dependency of com.google.firebase.auth.AuthResult is used which will
                            authenticate and if the task is successful then following lines will be generated
                            this is default onCompleteListener
                             */
                            //this will check whether the task which is authentication of the user is a success or not.
                            if (task.isSuccessful()) {
                                //The PBar will go once the task is successful
                                loadingProgBar.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                //when the login is successful then the intent will start the main activity
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);
                                //finish will prevent the user from going back to login page once the user has been logged in.
                                finish();
                            } else {

                                loadingProgBar.setVisibility(View.GONE); //this will hide the progress bar
                                //if the user is not successfully login then it should display this message.
                                Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
//this method is used to check if the user is already signed in?
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            //and if the user is not null and already signed then there be now main activity.
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            this.finish();
        }

    }
}
