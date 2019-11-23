package com.example.vybe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * SocialActivity displays the screen for a user to view a list of their followers' vibe events
 */
public class SocialActivity extends AppCompatActivity {

    private static final String TAG = "SocialActivity";

    private Button myVibesBtn;
    private Button searchBtn;
    private Button mapBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);
        Log.d(TAG, "onCreate: In social");

        myVibesBtn = findViewById(R.id.my_vibes_btn);
        searchBtn = findViewById(R.id.search_btn);
        mapBtn = findViewById(R.id.social_map_btn);

        myVibesBtn.setOnClickListener((View v) -> {
            finish();
        });

        searchBtn.setOnClickListener((View v) -> {
            startActivity(new Intent(SocialActivity.this, SearchProfilesActivity.class));
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent MapViewIntent = new Intent(SocialActivity.this, MapViewActivity.class);
                MapViewIntent.putExtra("MapViewMode", "Social");
                startActivity(MapViewIntent);
            }
        });
    }
}
