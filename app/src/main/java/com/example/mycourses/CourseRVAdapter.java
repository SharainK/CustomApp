package com.example.mycourses;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CourseRVAdapter extends RecyclerView.Adapter<CourseRVAdapter.ViewHolder> {
    //creating variables for our list, context, interface and position.
    private ArrayList<CourseRVModal> courseRVModalArrayList;
    private Context context;
    private CourseClickInterface courseClickInterface;
    int lastPosition = -1;

    //creating the constructor and initalsing the private fields
    public CourseRVAdapter(ArrayList<CourseRVModal> courseRVModalArrayList, Context context, CourseClickInterface courseClickInterface) {
        this.courseRVModalArrayList = courseRVModalArrayList;
        this.context = context;
        this.courseClickInterface = courseClickInterface;
    }

    @NonNull
    @Override
    //inside the viewHolder we inflate the file which is same as in Kotlin
    public CourseRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating the layout file set to appear as recycler view
        View view = LayoutInflater.from(context).inflate(R.layout.course_rv_item, parent, false);
        //now the view will be returned.
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseRVAdapter.ViewHolder holder, int position) {
        //setting the data to the recycle view
        //this gets the position of the list where the element is present at the moment
        CourseRVModal courseRVModal = courseRVModalArrayList.get(position);
        //presents the name of the course
        holder.courseTextView.setText(courseRVModal.getCourseName());
        //concatenation of the price with "AUD"
        holder.coursePriceTextView.setText("AUD" + courseRVModal.getCoursePrice());
        /*
        here the image will be loaded using the URL
        we have already implemented the internet permissions
         */
        //if block will not have anything and will cause problem if the image is not loaded
        if(courseRVModal.getCourseImg().isEmpty()){

        }
//the else will have the image set
        /*
        the load will get the url of the image and then will be put into the holder
         */
        else{
            Picasso.get().load(courseRVModal.getCourseImg()).into(holder.courseImageView);
        }

        //adding animation to recycler view item on below line.
        setAnimation(holder.itemView, position);
        holder.courseImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                courseClickInterface.onCourseClick(position);
            }
        });
    }
    //adding the item from left to right is a type of animation therefore a separate method has been made to call the file

    private void setAnimation(View itemView, int position) {
        if (position > lastPosition) {
            //here we are setting the animation
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            itemView.setAnimation(animation);
            //and now updating the position
            lastPosition = position;
        }
    }
//here the size of the list will be returned.
    @Override
    public int getItemCount() {

        return courseRVModalArrayList.size();
    }
//here we pass the data to the recyler view
    public static class ViewHolder extends RecyclerView.ViewHolder {
        //here the variables which will appear as in for recycle view will be initalised.
        /*
        the course image , name and price will appear there.
         */
        private ImageView courseImageView;
        private TextView courseTextView, coursePriceTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //initializing the variables with their IDs
            courseImageView = itemView.findViewById(R.id.idIVCourse);
            courseTextView = itemView.findViewById(R.id.idTVCOurseName);
            coursePriceTextView = itemView.findViewById(R.id.idTVCousePrice);
        }
    }

    //this is an interface for onClick
    public interface CourseClickInterface {
        void onCourseClick(int position);
    }
}

