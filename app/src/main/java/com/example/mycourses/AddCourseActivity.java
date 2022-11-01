package com.example.mycourses;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AddCourseActivity extends AppCompatActivity {

    //creating variables for button, text input,reference , database and progress bar.
    private Button addCourseButton;
    private TextInputEditText courseNameEditText, courseDescriEditText, coursePriceEditText, bestSuitedEditText, courseImgEditText, courseLinkEditText;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ProgressBar loadingProgBar;
    private String courseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        //here the variables will be initialised
        addCourseButton = findViewById(R.id.idBtnAddCourse); //this is the button for adding the course
        courseNameEditText = findViewById(R.id.idEdtCourseName); //this will be used as an EditText to give the name as the input
        courseDescriEditText = findViewById(R.id.idEdtCourseDescription); //this will be used as an EditText to give the Course Description as the input
        coursePriceEditText = findViewById(R.id.idEdtCoursePrice); //this will be used as an EditText to give the price as the input
        bestSuitedEditText = findViewById(R.id.idEdtSuitedFor); //this will be used as an EditText to give the level of the course as the input
        courseImgEditText = findViewById(R.id.idEdtCourseImageLink); //this will be used as an EditText to give the course image as the input
        courseLinkEditText = findViewById(R.id.idEdtCourseLink); //this will be used as an EditText to give the course link as the input
        loadingProgBar = findViewById(R.id.idPBLoading);  //initialising progress bar
        firebaseDatabase = FirebaseDatabase.getInstance();
        //here we are creating the reference to the firebase.
        databaseReference = firebaseDatabase.getReference("Courses");
        //this is the click listener for add course button
        addCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgBar.setVisibility(View.VISIBLE);
              //the inputs from the editText will be received and stored in these variables.
                String courseName = Objects.requireNonNull(courseNameEditText.getText()).toString();
                String courseDesc = Objects.requireNonNull(courseDescriEditText.getText()).toString();
                String coursePrice = Objects.requireNonNull(coursePriceEditText.getText()).toString();
                String bestSuited = Objects.requireNonNull(bestSuitedEditText.getText()).toString();
                String courseImg = Objects.requireNonNull(courseImgEditText.getText()).toString();
                String courseLink = Objects.requireNonNull(courseLinkEditText.getText()).toString();
                //courseID will be initialised with the courseName
                courseID = courseName;
                //the data from here is passed into the modal class or the data class
                /*
                here we are creating the object of class CourseRVModal and the syntax is
                ClassName objectName=new ClassName();
                 */

                CourseRVModal courseRVModal = new CourseRVModal(courseID, courseName, courseDesc, coursePrice, bestSuited, courseImg, courseLink);

                /*
                we are now calling an add value event which will pass the data to the firebase realtime database.
                the methods generated were by default used for further actions in the database.
                 */
                databaseReference.addValueEventListener(new ValueEventListener() {
                    /*
                    This method is triggered when a new child is added to the location to which this listener was added
                     */
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //now from here we are the data in the firebase
                        /*
                        the database reference will now take the value within child parameter
                        to have the course id whose value will be taken using .setValue()
                        set to courseRVModal which basically sets the user and then helps to perform
                        CRUD operations and therefore for th same reason an empty constructor was setup.

                         */
                        databaseReference.child(courseID).setValue(courseRVModal);
                        //this will display a toast message when the course is added.
                        Toast.makeText(AddCourseActivity.this, "Course Added", Toast.LENGTH_SHORT).show();
                        //now when the course has been added it should now jump on MainActivity.
                        startActivity(new Intent(AddCourseActivity.this, MainActivity.class));
                    }

                    /*
                    This method will be triggered in the event that this listener either failed at the server,
                    or is removed as a result of the security and Firebase rules.
                     */
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //displaying a failure message on below line.
                        Toast.makeText(AddCourseActivity.this, "Failed to Add The Course", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}