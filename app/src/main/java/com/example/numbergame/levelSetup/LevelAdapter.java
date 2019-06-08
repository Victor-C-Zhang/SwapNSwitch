package com.example.numbergame.levelSetup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.numbergame.NumberActivity;
import com.example.numbergame.R;

import java.util.ArrayList;

public class LevelAdapter extends ArrayAdapter<Level> {
    private Context context;
    public LevelAdapter(Context context, ArrayList<Level> levels) {
        super(context, 0, levels);
        this.context=context;
    }
    public LevelAdapter(Context context, LevelSet levelSet){
        super(context, 0,levelSet.levels);
        this.context=context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Level level = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.button_level, parent, false);
        }
        // Lookup view for data population
        TextView levelNumber = (TextView) convertView.findViewById(R.id.levelText);
        // Populate the data into the template view using the data object
        levelNumber.setText(Integer.toString(level.getLevelNumber()));
        Button btButton = (Button) convertView.findViewById(R.id.levelButton);
        // Cache row position inside the button using `setTag`
        btButton.setTag(position);
        // Attach the click event handler
        btButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                // Access the row position here to get the correct data item
                Level level = getItem(position);
                // Do what you want here...
                String prefFile = "NumberGamePreferences";
                SharedPreferences preferences = context.getSharedPreferences(prefFile, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                int arr[][] = level.getConfig();
                for (int i=0;i<arr.length;i++)
                    for (int j=0;j<arr[i].length;j++)
                        editor.putInt(Integer.toString(i*arr.length+j),arr[i][j]);
                editor.commit();
                Intent i = new Intent(context, NumberActivity.class);
                context.startActivity(i);
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }
}
