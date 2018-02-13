package com.Emergency.Patient;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;


import com.Emergency.Patient.Models.Ambulance;
import com.Emergency.Patient.Models.Locate;
import com.Emergency.Patient.Models.Patient;
import com.Emergency.Patient.Models.ambStatus;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class SimpleDirectionActivity extends AppCompatActivity implements
        OnMapReadyCallback ,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
        {

    private LocationCallback mLocationCallback;
    int ambUpdateCnt=0,mylocUpdateCnt=0;

    Location mLastLocation;
    Marker mCurrLocationMarker;
    Marker ambulancePositionMarker;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    DatabaseReference mNotificationDatabase;



    private GoogleMap googleMap;
    private String serverKey = "AIzaSyDi3B9R9hVpC9YTmOCCz_pCR1BKW3tIRGY";
  //      private LatLng origin = new LatLng(21.218681, 80.307411);
  //  private LatLng destination = new LatLng(21.212248, 81.316434);
    TextView distance, duration,mylonglat,amblonglat;
    private LatLng origin = null;
    private LatLng destination = null;
    private Ambulance ambulance=null;
    private Patient patient=null;

    DatabaseReference ambRef,ambLocRef,ambStatusRef,PatientsRef,mUserDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_direction);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mUserDatabase=FirebaseDatabase.getInstance().getReference().child("patients");
        mNotificationDatabase=FirebaseDatabase.getInstance().getReference().child("notifications");
        String device_token= FirebaseInstanceId.getInstance().getToken();
        mUserDatabase.child(MainActivity.account.getId()).child("device_token").setValue(device_token);

        PatientsRef=database.getReference("patients");
        ambRef = database.getReference("ambulance");
        ambLocRef = ambRef.child("location");
        ambStatusRef = ambRef.child("ambStatus");





        distance = findViewById(R.id.distance);
        duration = findViewById(R.id.duration);
        mylonglat=findViewById(R.id.mylonglat);
        amblonglat=findViewById(R.id.amblonglat);


        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    System.out.println("mLocation callback fn running...");
                    mylocUpdateCnt+=1;
                    mylonglat.setText(mylocUpdateCnt + " my location :"+location.getLatitude()+","+location.getLongitude());

                    if(patient!=null ) {
                        PatientsRef.child(MainActivity.account.getId()).child("location").setValue(new Locate(location.getLatitude(), location.getLongitude()));
                    }
                  //  patientRef.child("longitude").setValue(location.getLongitude());
//                    Map<String, Object> hopperUpdates = new HashMap<>();
//                    hopperUpdates.put(MainActivity.account.getId(), new Locate(location.getLatitude(),location.getLongitude()));
//
//                    ambRef.updateChildren(hopperUpdates);
                   //Toast.makeText(getApplicationContext(),"fk location coming "+location.getLongitude()+":"+location.getLongitude(),Toast.LENGTH_LONG).show();
               //distance.setText(location.getLatitude()+","+location.getLongitude());
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    mLastLocation = location;
                    if (mCurrLocationMarker != null) {
                   //     Toast.makeText(getApplicationContext(),"my location changed",Toast.LENGTH_SHORT).show();
                        mCurrLocationMarker.setPosition(latLng);

                    }else{
                        Toast.makeText(getApplicationContext(),"my location set",Toast.LENGTH_SHORT).show();
                        origin =new LatLng(location.getLatitude(),location.getLongitude());
                        Toast.makeText(SimpleDirectionActivity.this,location.getLatitude()+":"+location.getLongitude(),Toast.LENGTH_LONG).show();

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title("Current Position");
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                        mCurrLocationMarker = googleMap.addMarker(markerOptions);
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(40));
                    }


                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                }
            }
        };


    }

    public void Emergency(View view) {
        HashMap<String,String> notificationData=new HashMap<>();
        notificationData.put("from",MainActivity.account.getDisplayName());
        mNotificationDatabase.child("101078650104813911105").push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() { @Override
        public void onSuccess(Void aVoid) {
            Toast.makeText(SimpleDirectionActivity.this, "data successfully sent ", Toast.LENGTH_SHORT).show();
        }
        });
        System.out.println("Emergency button clicked");
        try {
            System.out.println("patient :"+new Gson().toJson(patient));
            System.out.println("ambulance :"+new Gson().toJson(ambulance));
            if (patient == null) {
                if (ambulance != null) {


//                        Map<String, Object> map = new HashMap<>();
//                        map.put("ambStatus", new ambStatus("busy", 102, MainActivity.account.getId()));
//                        ambRef.updateChildren(map);
//                        patient = new Patient();
//                        patient.setPatientStatus("Emergency");
//                        Map<String, Object> map2 = new HashMap<>();
//                        map2.put(MainActivity.account.getId(), patient);
//                        PatientsRef.updateChildren(map2);
//                    } else if (ambulance.getAmbStatus().getStatus().equals("busy")) {
//                        Toast.makeText(getApplicationContext(), "amb busy,patientInQue ", Toast.LENGTH_SHORT).show();
                        patient = new Patient();
                        patient.setPatientStatus("Emergency");
                        Map<String, Object> map2 = new HashMap<>();
                        map2.put(MainActivity.account.getId(), patient);
                        PatientsRef.updateChildren(map2);
//                    }
                } else if (ambulance == null) {
                    Toast.makeText(getApplicationContext(), "ambulance data is null", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "unknown else", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "patient is not null", Toast.LENGTH_SHORT).show();
            }
            view.setVisibility(View.GONE);
            findViewById(R.id.btn_CancelEmergency).setVisibility(View.VISIBLE);

        }catch (Exception e){
            System.out.println("emergency on click :error");
            e.printStackTrace();
        }
    }

    public void CancelEmergency(final View view){
        System.out.println("cancel Emergency clicked");
        System.out.println(new Gson().toJson(patient));
        if(patient !=null){

            PatientsRef.child(MainActivity.account.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
//                        if(ambulance.getAmbStatus().getAllottedPatient().equals(MainActivity.account.getId())){
//                            Map<String, Object> map = new HashMap<>();
//                            map.put("ambStatus", new ambStatus("free"));
//                            ambRef.updateChildren(map);
//                        }
                        Toast.makeText(getApplicationContext(),"Emergency Canceled ",Toast.LENGTH_SHORT).show();
                        view.setVisibility(View.GONE);
                        findViewById(R.id.btn_Emergency).setVisibility(View.VISIBLE);
                    }else{
                        Toast.makeText(getApplicationContext(),"error cancelling",Toast.LENGTH_SHORT).show();
                        System.out.println(task.getException());
                    }
                }
            });

        }else{
            Toast.makeText(getApplicationContext(),"patient is null",Toast.LENGTH_SHORT).show();
            view.setVisibility(View.GONE);
            findViewById(R.id.btn_Emergency).setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                googleMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            googleMap.setMyLocationEnabled(true);
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }




    @Override
    protected void onStart() {
        super.onStart();



        checkLocationPermission();
        ambRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("ambref ,dataSnapshot :"+dataSnapshot.toString());
                ambulance=dataSnapshot.getValue(Ambulance.class);
                System.out.println("from listener ,ambstatus:"+new Gson().toJson(ambulance.getAmbStatus()));
               Iterator i= dataSnapshot.getChildren().iterator();
                while(i.hasNext()){
                    System.out.println(i.next().toString());
                }
                System.out.println("from listener ,ambulance :"+new Gson().toJson(ambulance));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        PatientsRef.child(MainActivity.account.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("patientref ,dataSnapshot :"+dataSnapshot.toString());
                patient=dataSnapshot.getValue(Patient.class);
                System.out.println("from listener ,patient :"+new Gson().toJson(patient));
         //       System.out.println("ambulance :"+new Gson().toJson(ambulance));
                if(patient!=null){
                    findViewById(R.id.btn_Emergency).setVisibility(View.GONE);
                    findViewById(R.id.btn_CancelEmergency).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ambRef.child("location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("ambulance location changed....");
                System.out.println("amblocation, snapshot :"+dataSnapshot.toString());
                Locate location = dataSnapshot.getValue(Locate.class);

                Log.d("ambref , amb location :",  new Gson().toJson(location));

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    ambUpdateCnt += 1;
                    amblonglat.setText(ambUpdateCnt + " ambulance:" + location.getLatitude() + "," + location.getLongitude());
                    if (ambulancePositionMarker != null) {
                        //  Toast.makeText(getApplicationContext(), "ambulance location changed...", Toast.LENGTH_SHORT).show();
                        System.out.println("ambulance marker already created....");
                        ambulancePositionMarker.setPosition(latLng);
                    } else {
                        Toast.makeText(getApplicationContext(), "ambulance location set", Toast.LENGTH_SHORT).show();
                        System.out.println("no marker ......creating");
                        destination = new LatLng(location.getLatitude(), location.getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title("Ambulance Position");
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        ambulancePositionMarker = googleMap.addMarker(markerOptions);
                    }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

        ambStatusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("ambStatusRef , snapshot :"+dataSnapshot.toString());
                ambStatus AmbStatus=dataSnapshot.getValue(ambStatus.class);
                System.out.println("ambStatusRef , ambstatus :"+new Gson().toJson(AmbStatus));
                if(patient!=null && AmbStatus.getAllottedPatient().equals(MainActivity.account.getId())) {
                    duration.setText("timebyGMap");
                    distance.setText("distbyGMap");

                }else if(patient!=null && !AmbStatus.getAllottedPatient().equals(MainActivity.account.getId())){
                    duration.setText("dependsOnDriver");
                    distance.setText("dependsOnDriver");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Failed to read value.", databaseError.toException());
            }
        });



    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
     //   mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
         //   LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,null );
         LocationServices.getFusedLocationProviderClient(getApplicationContext()).requestLocationUpdates(mLocationRequest, mLocationCallback, null /* Looper */);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        googleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }


 }
