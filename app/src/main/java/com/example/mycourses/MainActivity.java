package com.example.mycourses;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements CourseRVAdapter.CourseClickInterface {

    //the variables for floating action button, firebase database, prog bar, lisr, auth , rv and as well as the relative layout has been created
    private FloatingActionButton addCourseFloatinActionButton;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private RecyclerView courseRecylerView;
    private FirebaseAuth mAuth;
    private ProgressBar loadingProgBar;
    //this is creating an array list of type modal class
    private ArrayList<CourseRVModal> courseRVModalArrayList;
    //this creates a ref to an adapter
    private CourseRVAdapter courseRVAdapter;
    private RelativeLayout homeRLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //here we are initialising all the variables
        courseRecylerView = findViewById(R.id.idRVCourses);
        homeRLayout = findViewById(R.id.idRLBSheet);
        loadingProgBar = findViewById(R.id.idPBLoading);
        addCourseFloatinActionButton = findViewById(R.id.idFABAddCourse);
        //ref to firebase database
        firebaseDatabase = FirebaseDatabase.getInstance();
        //ref to the authentication
        mAuth = FirebaseAuth.getInstance();
        courseRVModalArrayList = new ArrayList<>();

        //here we are getting the reference
        databaseReference = firebaseDatabase.getReference("Courses");
        //databaseReference = firebaseDatabase.getReference().child("Courses");
        //here we are adding the click listener to the add course floating action button
        addCourseFloatinActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this will start the addcourseactivity
                Intent i = new Intent(MainActivity.this, AddCourseActivity.class);
                startActivity(i);
            }
        });
        //here we are initialising the adapter class and then passing the interface
        courseRVAdapter = new CourseRVAdapter(courseRVModalArrayList, this, this::onCourseClick); //interface
        //here we want the recycler view  in vertical manner so sending it in a linear layout manager
        courseRecylerView.setLayoutManager(new LinearLayoutManager(this));
        //here we set the adapter
        courseRecylerView.setAdapter(courseRVAdapter);
        //this will fetch the courses from the database
        getCourses();
    }
    //this helps to read all the course from the database

    private void getCourses() {
        //once the adapter the items will be auto added into the view so therefore to avoid the data get
        //on below line clearing our list.
        courseRVModalArrayList.clear();
        //on below line we are calling add child event listener method to read the data.
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                CourseRVModal obj=snapshot.getValue(CourseRVModal.class);
//                courseRVModalArrayList.add(obj);
                //here there will be iteration in the database schema to the get the content from the databse

                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    //CourseRVModal obj=snapshot.getValue(CourseRVModal.class);
                    //courseRVModalArrayList.add(obj);
                    //here testing has been done to check whether the data can be retrieved or not
                    String test= Objects.requireNonNull(snapshot.child("bestSuitedFor").getValue()).toString();
                    System.out.println(test);
                    courseRVModalArrayList.add(snapshot.getValue(CourseRVModal.class));
                    break;
                }
                //on below line we are hiding our progress bar.
                loadingProgBar.setVisibility(View.GONE);
                //CourseRVModal obj=snapshot.getValue(CourseRVModal.class);
                //courseRVModalArrayList.add(obj);

                //System.out.println("Fine till here");

                //adding snapshot to our array list on below line.
                //courseRVModalArrayList.add(snapshot.getValue(CourseRVModal.class));
                //System.out.println("crash");

                //notifying the adapter that the data has been changed or updated
                courseRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //this method is called when the child has been changed
                //setting the visibility to gone
                loadingProgBar.setVisibility(View.GONE);
                courseRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                //notifying the adapter when the child is removed.
                courseRVAdapter.notifyDataSetChanged();
                loadingProgBar.setVisibility(View.GONE);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //notifying the adapter the child is moved.
                courseRVAdapter.notifyDataSetChanged();
                loadingProgBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();

            }
        });
    }
//this is interface method generated when we extended the use of interface
    @Override
    public void onCourseClick(int position) {
        //a different method has been made to display bottom sheet which has been called here.
        displayBottomSheet(courseRVModalArrayList.get(position));
    }
    //for the logout I have created a menu resource file
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //here we get the id of the items
        int id = item.getItemId();
        switch (id) {
            //this is for logout on top right corner
            case R.id.idLogOut:
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setMessage("Do you want to Log Out?").setCancelable(false)
                        //sets the yes button for the user
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //if press yes log out
                                mAuth.signOut();
                                Toast.makeText(getApplicationContext(), "User Logged Out", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(i);
                                finish();
                            }



                        })
                        //when pressed no then the dialog should disappear
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

                //this is the toast message when the user logs out
//                Toast.makeText(getApplicationContext(), "User Logged Out", Toast.LENGTH_LONG).show();
                //here we are using the firebase method to sign out ourselves from the application
//                mAuth.signOut();
//                //after the sign out we need to go for login activity page
//                Intent i = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(i);
//                //finish to avoid going back
//                this.finish();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //when the option menu has been created this wll be used to inflate the layout which is menu_main
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //on below line we are inflating our menu file for displaying our menu options.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    private void displayBottomSheet(CourseRVModal modal) {
        //here we are creating the bottom sheet
        final BottomSheetDialog bottomSheetCourseDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        //here we are inflating the the layout for our bottom sheet
        View layout = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_layout, homeRLayout);
        //passing and setting the content view which has to be there to display content
        bottomSheetCourseDialog.setContentView(layout);
        //setting the dialog box to set cancelable as true when pressed back only
        bottomSheetCourseDialog.setCancelable(true);
        //when touched outside it should not be canceled.
        bottomSheetCourseDialog.setCanceledOnTouchOutside(false);
        //this method will display the bottom sheet dialog.
        bottomSheetCourseDialog.show();
        /*
        here we are creating the variables for text views and images
        so as to have what needs to be shown in the dialog box
         */
        TextView courseNameTextView = layout.findViewById(R.id.idTVCourseName);
        TextView courseDescTextView = layout.findViewById(R.id.idTVCourseDesc);
        TextView suitedForTextView = layout.findViewById(R.id.idTVSuitedFor);
        TextView priceTextView = layout.findViewById(R.id.idTVCoursePrice);
        ImageView courseImageView = layout.findViewById(R.id.idIVCourse);
        //here we are setting the data to the different views and also the image
        /*
        the get method is from the data class or the modal class in java
         */
        courseNameTextView.setText(modal.getCourseName());
        courseDescTextView.setText(modal.getCourseDescription());
        suitedForTextView.setText("Suited for " + modal.getBestSuitedFor());
        priceTextView.setText("AUD" + modal.getCoursePrice());
        if(modal.getCourseImg().isEmpty()){
        }
        else{
            Picasso.get().load(modal.getCourseImg()).into(courseImageView);
        }
        //creating the buttons for view details and edit
        Button viewDetailsButton = layout.findViewById(R.id.idBtnVIewDetails);
        Button editDetailsButton = layout.findViewById(R.id.idBtnEditCourse);
        //for the edit button the click listener is added below.
        editDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the edit details of the course has been pressed then we need to have the edit course activity started
                Intent i = new Intent(MainActivity.this, EditCourseActivity.class);
                //here we are passing the data therefore the key and the value has to be same
                i.putExtra("course", modal);
                //this will start the activity
                startActivity(i);
            }
        });
        //the click listener for view details
        viewDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                NOTE: the Intent.ACTION_VIEW is specifically to start the url content the link for the course
                will entered and then the link will be opened
                 */
                Intent i = new Intent(Intent.ACTION_VIEW);
                //uri provides the content by mapping the string properly.
                i.setData(Uri.parse(modal.getCourseLink()));
                //starting the activity
                startActivity(i);
            }
        });
    }
}