package com.example.vybe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * This Activity displays the screen for a vibe event and all its available details
 */
public class ViewVibeActivity extends AppCompatActivity {

    private static final String TAG = "ViewVibeActivity";

    private VibeEvent vibeEvent;

    private ImageView vibeImage;
    private TextView dateField;
    private TextView reasonField;
    private TextView reasonLabel;
    private TextView socialSituationField;
    private TextView socialSituationLabel;
    private ImageView reasonImage;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_vibe);
        Log.d(TAG, "onCreate: In View vibes");

        vibeImage = findViewById(R.id.view_vibe_image_view);
        dateField = findViewById(R.id.view_date_text_view);
        reasonField = findViewById(R.id.view_reason_text_view);
        reasonLabel = findViewById(R.id.view_reason_label);
        socialSituationField = findViewById(R.id.view_social_situation_text_view);
        socialSituationLabel = findViewById(R.id.view_social_situation_label);
        reasonImage = findViewById(R.id.reason_image);

        reasonImage.setDrawingCacheEnabled(true);
        reasonImage.buildDrawingCache();
        toolbar = findViewById(R.id.view_vibes_toolbar);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras.containsKey("vibeEvent")) {
            vibeEvent = (VibeEvent) extras.getSerializable("vibeEvent");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" d, YYYY h:mm a", Locale.ENGLISH);
            LocalDateTime dateTime = vibeEvent.getDateTime();
            String month = dateTime.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.ENGLISH);
            dateField.setText(month + dateTime.format(formatter));
            String reason = vibeEvent.getReason();
            String socialSituation = vibeEvent.getSocialSituation();
            // TODO: missing location - do that later once done
            String reasonImagePath = vibeEvent.getImage();

            if (reasonImagePath != null){
                loadImageFirebase(reasonImage, reasonImagePath);
            }

            if (vibeEvent.getVibe() != null) {
                vibeImage.setImageResource(vibeEvent.getVibe().getEmoticon());
                toolbar.setBackgroundResource(vibeEvent.getVibe().getColor());
            }

            if (reason == null || reason.equals("")) {  // Reason is optional
                reasonLabel.setVisibility(TextView.GONE);
                reasonField.setVisibility(TextView.GONE);
            } else {
                reasonField.setText(reason);
            }

            if (socialSituation == null || socialSituation.equals("")) { // Social Situation is optional
                socialSituationLabel.setVisibility(TextView.GONE);
                socialSituationField.setVisibility(TextView.GONE);
            } else {
                socialSituationField.setText(socialSituation);
            }
        }

    }

    /**
     * This will load an image from Firebase Storage into an ImageView
     * @param imageView
     *      The destination ImageView
     * @param imagePath
     *      Path to the image in Firebase Storage
     */
    public void loadImageFirebase(ImageView imageView, String imagePath){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // Get the path to the image
        StorageReference imageRef = storageRef.child(imagePath);

        // Get the download URL for Glide
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext() /* context */)
                        .load(uri) // Load the image
                        .into(imageView); // Destination to load image into
            }
        });
    }
}
