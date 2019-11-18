package com.example.vybe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import android.widget.Spinner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.TimeZone;

import static com.example.vybe.util.Constants.*;


/**
 * This Activity displays the screen for a user to view a list of their
 * vibe event history, sorted by date and time in reverse chronological
 * order
 */

public class MyVibesActivity extends AppCompatActivity {

    private static final String TAG = "MyVibesActivity";

    ArrayList<VibeEvent> vibeEventList;
    MyVibesAdapter myVibesAdapter;

    private boolean mLocationPermissionGranted = false;

    private Spinner filterSpinner;
    private ListView vibesListView;
    private Button addVibeEventBtn;
    private Button myMapBtn;
    private Button socialBtn;
    private ImageButton profileBtn;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private boolean allFlag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vibes);
        Intent intent = getIntent();
        Log.d(TAG, "onCreate: In my vibes");

        filterSpinner = findViewById(R.id.filter_spinner);
        vibesListView = findViewById(R.id.my_vibe_list);
        addVibeEventBtn = findViewById(R.id.add_vibe_event_btn);
        myMapBtn = findViewById(R.id.my_map_btn);
        socialBtn = findViewById(R.id.social_btn);
        profileBtn = findViewById(R.id.profile_btn);

        vibeEventDBPath = "Users/" + mAuth.getCurrentUser().getUid() + "/VibeEvents";
        
        allFlag = true; // Ask jakey

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyVibesActivity.this, ViewProfileActivity.class);
                // Get the current user's profile information
                db.collection("Users").document(mAuth.getCurrentUser().getUid()).get()
                        .addOnSuccessListener((DocumentSnapshot doc) -> {
                            String username = (String) doc.get("username");
                            String email = (String) doc.get("email");
                            intent.putExtra("user", new User(username, email));
                            startActivity(intent);
                        });

            }
        });

        // --- Vibes Dropdown ---
        String[] vibes = new String[]{"Filter Vibe", "Angry", "Disgusted", "Happy", "Sad", "Scared", "Surprised"};
        ArrayAdapter<String> vibesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, vibes);
        filterSpinner.setAdapter(vibesAdapter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    String filterVibe = vibes[position].toLowerCase();
                    allFlag = true;
                    if (position != 0){ allFlag = false;}
                    db.collection("VibeEvent")
                            .orderBy("datetime", Query.Direction.DESCENDING)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    // TODO: Stub out with other query below
                                    vibeEventList.clear();
                                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots){

                                            LocalDateTime ldt = doc.getDate("datetime").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                                            Log.d(TAG, ldt.toString());
                                            String reason = (String) doc.getData().get("reason");
                                            String socSit = (String) doc.getData().get("socSit");
                                            String id = (String) doc.getData().get("ID");
                                            String vibe = (String) doc.getData().get("vibe");
                                            String image = (String) doc.getData().get("image");
                                            double latitude = 0;
                                            double longitude = 0;
                                            if ((doc.getData().get("latitude") != null) && (doc.getData().get("latitude")!= null)) {
                                                latitude = (double) doc.getData().get("latitude");
                                                longitude = (double) doc.getData().get("longitude");
                                            }
                                        if (allFlag) {
                                            vibeEventList.add(new VibeEvent(vibe, ldt, reason, socSit, id, image, latitude, longitude));
                                        } else {
                                            if (filterVibe.equals(vibe)){
                                                vibeEventList.add(new VibeEvent(vibe, ldt, reason, socSit, id, image, latitude, longitude));
                                            }
                                        }
                                    }
                                    myVibesAdapter.notifyDataSetChanged();
                                }
                            });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

//        Vibe vibe, Date date, String reason, String socialSituation
        vibeEventList = new ArrayList<>();

        myVibesAdapter = new MyVibesAdapter(this, R.layout.my_vibe_item, vibeEventList);
        vibesListView.setAdapter(myVibesAdapter);

        final CollectionReference collectionReference = db.collection("VibeEvent");
        Query query = collectionReference.orderBy("datetime", Query.Direction.DESCENDING);

        query.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                // TODO: Stub out with other query above
                vibeEventList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
//                    LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(doc.getDate("datetime").getTime()),
//                            TimeZone.getDefault().toZoneId());
                    LocalDateTime ldt = doc.getDate("datetime").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    Log.d(TAG, ldt.toString());
                    String reason = (String) doc.getData().get("reason");
                    String socSit = (String) doc.getData().get("socSit");
                    String id = (String) doc.getData().get("ID");
                    String vibe = (String) doc.getData().get("vibe");
                    String image = (String) doc.getData().get("image");
                    double latitude = 0;
                    double longitude = 0;
                    if ((doc.getData().get("latitude") != null) && (doc.getData().get("longitude") != null)) {
                        latitude = (double) doc.getData().get("latitude");
                        longitude = (double) doc.getData().get("longitude");
                    }
                    vibeEventList.add(new VibeEvent(vibe, ldt, reason, socSit, id, image, latitude, longitude));
                }
                myVibesAdapter.notifyDataSetChanged();
            }
        });

        vibesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MyVibesActivity.this, ViewVibeActivity.class);
                VibeEvent vibeEvent = vibeEventList.get(i);
                intent.putExtra("vibeEvent", vibeEvent);
                startActivity(intent);
            }
        });

        vibesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyVibesActivity.this);
                
                builder.setCancelable(true);

                // Delete ride if user clicks on "Yes" button
                builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(MyVibesActivity.this, AddEditVibeActivity.class);
                        VibeEvent vibeEvent = vibeEventList.get(position);
                        intent.putExtra("vibeEvent", vibeEvent);
                        startActivity(intent);
                    }
                });

                // Close dialog if user clicks on "No" button
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        VibeEvent vibeEvent = vibeEventList.get(position);
                        db.collection("VibeEvent").document(vibeEvent.getId()).delete();
                        myVibesAdapter.notifyDataSetChanged();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });

        addVibeEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyVibesActivity.this, AddEditVibeActivity.class);
                startActivity(intent);
            }
        });

        myMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLocationPermissionGranted) {
                    Intent intent = new Intent(MyVibesActivity.this, MapViewActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MyVibesActivity.this, "Please enable GPS services", Toast.LENGTH_SHORT);
                }
            }
        });

        socialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyVibesActivity.this, SocialActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //this is just the easiest way to consistently make sure the user has gps enabled
        if (checkMapServices()) {
            if (mLocationPermissionGranted){
                //do stuff
            }
            else {
                getLocationPermission();
            }
        }
    }

    private boolean checkMapServices() {
        if (GoogleServicesWorks()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    public boolean GoogleServicesWorks(){
        //Makes sure that google api services are installed and enabled
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MyVibesActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MyVibesActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        return false;
    }

    public boolean isMapsEnabled() {
        //Makes sure user has gps services enabled
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            NoGpsMessage();
            return false;
        }
        return true;
    }

    private void NoGpsMessage() {
        //alert for if user does not have gps enabled
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("We need you to vybe with us using your GPS services, please enable it for us")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent enableGpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    protected void OnActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted){
                    //everything is fine
                }
                else {
                    getLocationPermission();
                }
            }
        }
    }

    private void getLocationPermission() {
        //asks user for permission
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            //do stuff
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        //method ran after user closes the get permission window
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }
}
