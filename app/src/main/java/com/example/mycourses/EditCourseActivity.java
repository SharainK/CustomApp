package com.example.mycourses;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.metrics.Event;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditCourseActivity extends AppCompatActivity {

    //creating variables for our edit text, firebase database, database reference, course rv modal,progress bar.
    private TextInputEditText courseNameEditText, courseDescriEditext, coursePriceEditext, bestSuitedEditText, courseImgEdit, courseLinkEdit;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    CourseRVModal courseRVModal;
    private ProgressBar loadingProBar;
    //creating a string for our course id.
    private String courseID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);
        //initializing all our variables on below line.
        Button addCourseBtn = findViewById(R.id.idBtnAddCourse);
        courseNameEditText = findViewById(R.id.idEdtCourseName);
        courseDescriEditext = findViewById(R.id.idEdtCourseDescription);
        coursePriceEditext = findViewById(R.id.idEdtCoursePrice);
        bestSuitedEditText = findViewById(R.id.idEdtSuitedFor);
        courseImgEdit = findViewById(R.id.idEdtCourseImageLink);
        courseLinkEdit = findViewById(R.id.idEdtCourseLink);
        loadingProBar = findViewById(R.id.idPBLoading);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //on below line we are getting our modal class on which we have passed.
        //note this parcelable extra should have the same key as mentioned for other activities
        courseRVModal = getIntent().getParcelableExtra("course");
        Button deleteCourseBtn = findViewById(R.id.idBtnDeleteCourse);

        if (courseRVModal != null) {
            //on below line we are setting data to our edit text from our modal class.
            //here, we are putting the data to the edit text from the modal class using the getter method defined in modal class.

            courseNameEditText.setText(courseRVModal.getCourseName()); //this sets the name of the course
            coursePriceEditext.setText(courseRVModal.getCoursePrice()); //this sets the price of the course
            bestSuitedEditText.setText(courseRVModal.getBestSuitedFor()); //this sets the level of the course, to which level it is suited
            courseImgEdit.setText(courseRVModal.getCourseImg()); //this helps to get the image of the course using internet permissions
            courseLinkEdit.setText(courseRVModal.getCourseLink()); //this gets the link to different courses
            courseDescriEditext.setText(courseRVModal.getCourseDescription()); //this gets the description of the courses
            courseID = courseRVModal.getCourseId(); //this gets the id of the course.
        }
        //now we will get the reference
        databaseReference = firebaseDatabase.getReference("Courses").child(courseID);
        //on below line we are adding click listener for our add course button.
        addCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //on below line we are making our progress bar as visible.
                loadingProBar.setVisibility(View.VISIBLE);
                //on below line we are getting data from our edit text.
                String courseName = Objects.requireNonNull(courseNameEditText.getText()).toString();
                String courseDesc = Objects.requireNonNull(courseDescriEditext.getText()).toString();
                String coursePrice = Objects.requireNonNull(coursePriceEditext.getText()).toString();
                String bestSuited = Objects.requireNonNull(bestSuitedEditText.getText()).toString();
                String courseImg = Objects.requireNonNull(courseImgEdit.getText()).toString();
                String courseLink = Objects.requireNonNull(courseLinkEdit.getText()).toString();
                //here we are creating a map which stores data in the key value pair
                //the data comes from the modal class and the names of the variables passed are similar to the ones in the modal class
                Map<String, Object> map = new HashMap<>();
                map.put("courseName", courseName);
                map.put("courseDescription", courseDesc);
                map.put("coursePrice", coursePrice);
                map.put("bestSuitedFor", bestSuited);
                map.put("courseImg", courseImg);
                map.put("courseLink", courseLink);
                map.put("courseId", courseID);

                //here we are calling the value event listener using the database reference
                //after that the onDataChange method will be called
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //making progress bar visibility as gone.
                        loadingProBar.setVisibility(View.GONE);
                        //the map will be passed here to the database and the update will be done there
                        databaseReference.updateChildren(map);
                        //here the toast message will be displayed stating that the course has been updated.
                        Toast.makeText(EditCourseActivity.this, "Course Updated Successfully", Toast.LENGTH_SHORT).show();
                        //opening the MainActivity when the course has been updated successfully.
                        startActivity(new Intent(EditCourseActivity.this, MainActivity.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //if the course has not been updated due to some errors then there should be msg that failed to update
                        Toast.makeText(EditCourseActivity.this, "Failed to Update the Course", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //adding a click listener for our delete course button.
//        deleteCourseBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //calling a method to delete a course.
//                deleteCourse();
//            }
//        });
        /*
        now there will be functionality for the delete button which will be used to delete the course therefore for that
        to prevent the user error I have implemented a dialog to confirm the user before deleting the course
        on click listener has the dialog with a separate function for deleting the course.

         */
        deleteCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this is the method for dialogs
                AlertDialog.Builder dialog = new AlertDialog.Builder(EditCourseActivity.this);
                //the message inside the dialog box is made using this.
                //below is the confirmation method before deleting the course.
                //setCancelable will prevent the user from pressing back from the dialog.
                dialog.setMessage("Do you want to delete this course?").setCancelable(false)
                        //sets the yes button for the user
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //if press yes then hop on to this method
                                deleteCourse();
                                finish();
                            }



                        })
                        //when pressed no then the dialog should dissapear
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                            }
                        });
                //here the alert will be created
                AlertDialog alert=dialog.create();
                //this sets the title
                alert.setTitle("Confirmation");
                alert.show();


            }
        });

    }

//this is the method made to delete the course
    private void deleteCourse() {
        //if there will be a course then there will be an option of deleting the value so for that we use removeValue
        databaseReference.removeValue();
        //when the course is deleted then this will be the message.
        Toast.makeText(this, "Course Deleted Successfully", Toast.LENGTH_SHORT).show();
        //after deleting the course the activity will jump from EditCourse to MainActivity.
        startActivity(new Intent(EditCourseActivity.this, MainActivity.class));
    }
}