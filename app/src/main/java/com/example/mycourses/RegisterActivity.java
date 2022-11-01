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

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {


    //we need to create the variables for edit text, text views and firebase auth so we can use them later to initialise, since in java we have to initialise the variables.
     /*
    The TextInputEditText is sub class EditText which gives a special floating action to the
    input fields.
     */
    private TextInputEditText userNameEditText, passwordEditText, confirmPasswordEditText;
    private TextView loginTV;
    //this is for firebase authentication
    private FirebaseAuth mAuth;
    private Button registerButton;

    //this is for loading button
    private ProgressBar loadingProgBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //here, I have initialised all the variables.
        userNameEditText = findViewById(R.id.idEdtUserName); //comes from the username id defined in Register xml
        passwordEditText = findViewById(R.id.idEdtPassword); //comes from the password id defined in Register xml
        loadingProgBar = findViewById(R.id.idPBLoading); //comes from the progress bar id defined in Register xml
        confirmPasswordEditText = findViewById(R.id.idEdtConfirmPassword); //comes from the conf password defined in Register xml
        //this for "Already a user? Login here"
        loginTV = findViewById(R.id.idTVLoginUser); //comes from the login text view id defined in Register xml
        registerButton = findViewById(R.id.idBtnRegister); //comes from the register id defined in Register xml and desingned in res folder
        //this will be used to get the instance of the firebase.
        mAuth = FirebaseAuth.getInstance(); //it gets the FireBase instance for the specified URL using the specified FireBase App.
        /*
        this on click listener is for login text view which if the user is already registered then it
        should take the user from RegisterActivity page to LoginActivity

         */

        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this will take the activity from the Registration Page to Login Page if the user is already registered.
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                //this will start the intent activity
                startActivity(i);
            }
        });
        /*
        this is the click listener for registering the user since the first time will register
        to get the access to the account the user needs to register oneself
         */
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this will set the visibility of the progress bar to visible.
                loadingProgBar.setVisibility(View.VISIBLE);
                //this step involves to retrieve the data from the edit texts
                //requireNonNull is like a null checker used in kotlin such as !! to throw NullPointerException if there's an error
                String userName = Objects.requireNonNull(userNameEditText.getText()).toString(); //the username which will be inputted
                String inputPassword = Objects.requireNonNull(passwordEditText.getText()).toString(); //here the password will be received as an iniput
                String confirmPassword = Objects.requireNonNull(confirmPasswordEditText.getText()).toString(); //now this will be used as a confirmation password.
                /*
                this if-else-else if block is used to verify whether the password and the
                confirmed password is same or not.
                 */
                if (!inputPassword.equals(confirmPassword)) {
                    //this toast will be displayed when the entered password and the confirmed passwords are not same.
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
                //else if condition will be a validator to make sure there is no empty inputs.
                /*
                TextUtils returns a boolean value and is a null checker i.e it checks that the length should not be zero as well as
                the string should not be null
                 */
                else if (TextUtils.isEmpty(userName) && TextUtils.isEmpty(inputPassword) && TextUtils.isEmpty(confirmPassword)) {

                    Toast.makeText(RegisterActivity.this, "Please Enter all the Fields..", Toast.LENGTH_SHORT).show();
                } else {
                    //now the conditions have been checked and from here the user authentication will start since new member has been created.
                    /*
                    since while downloading the dependencies, I had used create user with email and password
                     */
                    mAuth.createUserWithEmailAndPassword(userName, inputPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            /*
                            here the actual dependency of com.google.firebase.auth.AuthResult is used which will
                            authenticate and if the task is successful then following lines will be generated
                            this is default onCompleteListener
                             */
                            if (task.isSuccessful()) {
                                //when the user is login successfully then the progress bar will be hidden again and the toast message will be displayed
                                loadingProgBar.setVisibility(View.GONE);
                                Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                //here there will be an intent which will take to LoginActivity when the registration is confirmed
                                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                                //this command is for starting the activity.
                                startActivity(i);
                                finish();
                            } else {
                                //inside else block there will be a message for the failure, if the user does not register itself.
                                loadingProgBar.setVisibility(View.GONE);
                                Toast.makeText(RegisterActivity.this, "Fail to Register the user", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}