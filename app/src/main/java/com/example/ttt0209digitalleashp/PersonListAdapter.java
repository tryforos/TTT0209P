package com.example.ttt0209digitalleashp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

// class extending ArrayAdapter which will be holding a list of Person object
public class PersonListAdapter extends ArrayAdapter<Person> {

    //variables for log printing
    private final String TAG = "PersonListAdapter";
    private int createCounter = 0;

    //constructor
    public PersonListAdapter(Context context, List<Person> values) {
        // call super passing the custom row layout and Person list
        //super(context, R.layout.row_layout, values); // this works
        super(context, 0, values); // this also works
    }

    // Override getView to populate the view correctly.
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Person person = getItem(position);

        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) this.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // this dictates the layout of each row
            View rowView = inflater.inflate(R.layout.child_list_layout, parent, false);

            convertView = rowView;

            //counts number of times this is created
            // and prints to Log
            // NOTE: it only creates a number of row objects to fill up
            // screen + some extra, then re-uses them w new data as you scroll
            createCounter++;
            Log.i(TAG, "View Got Created " + createCounter);
        }

        // below dictates what fills the rows
        TextView textViewFullName = (TextView) convertView.findViewById(R.id.textChildName);

        textViewFullName.setText(person.getFullName());
        //textViewFullName.setText("Holler at " + person.getFullName() + "!");


        return convertView;
    }
}
