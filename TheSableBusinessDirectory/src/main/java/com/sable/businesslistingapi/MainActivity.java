package com.sable.businesslistingapi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.login.widget.LoginButton;


import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.provider.Contacts.SettingsColumns.KEY;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.appevents.ml.ModelManager.initialize;


public class MainActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {


    /**
     * permissions request code
     */
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};


    public static Double latitude, longitude, lstKnownLat, lstKnownLng;

    public

    TextView tvAddress, tvUserName, tvUserEmail, tvWpStatus, tvWpMsg, tvWpUserId, tvWpCookie, tvWpUserLogin;
    RecyclerView verticalRecyclerView, horizontalRecyclervView, verticalRecyclerView2;
    private ProgressBar progressBar;
    LinearLayoutManager mLayoutManager, hLayoutManager, mLayoutManager2;
    VerticalAdapter verticalAdapter, verticalAdapter2;
    // HorizontalAdapter horizontalAdapter;
    public static List<BusinessListings> mListPost;
    public static List<WooProducts> hListPost;
    public static List<UserAuthPOJO> userinfo;
    public static String baseURL = "https://www.thesablebusinessdirectory.com", radius, address, state, country,
            zipcode, city, street, bldgno, todayRange, username = "android_app", isOpen, email,
            password = "mroK zH6o wOW7 X094 MTKy fwmY", userName, userEmail, userImage, userId, firstName, lastName;

    /* Animation animFadeIn,animFadeOut,animBlink,animZoomIn,animZoomOut,animRotate
            ,animMove,animSlideUp,animSlideDown,animBounce,animSequential,animTogether,animCrossFadeIn,animCrossFadeOut; */

    ArrayList<ListingsModel> verticalList;
    ArrayList<ListingsModel> locationMatch = new ArrayList<>();
    // private LoginButton loginButton;
    List<String> spinnerArrayRad = new ArrayList<>();
    List<String> category = new ArrayList<>();
    ArrayList<String> userActivityArray = new ArrayList<>();
    Spinner spnCategory, spnRadius;
    ImageView ivUserImage, spokesperson;
    private static final int toValue = 20;
    private static final int fromValue = 0;
    private static final int FRAME_TIME_MS = 15000;
    Thread updateMsg;

    ImageButton btnAdd, btnShop;


    SearchView searchView;
    Location location;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    Location bestLocation = null;
    LocationManager locationManager;
    List<String> providers = new ArrayList<>();

    CallbackManager fbLogincallbackManager;
    private AccessTokenTracker accessTokenTracker;


    AccessToken accessToken = AccessToken.getCurrentAccessToken();

    private TextSwitcher textSwitcher;
    // private int count =0;

    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    //logging.setLevel(HttpLoggingInterceptor.Level.BODY);

    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(new BasicAuthInterceptor(username, password))
            .addInterceptor(logging)
            .build();

    //String currentTime = String.valueOf(System.currentTimeMillis());

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

/**
 * login via facebook
 */
        fbLogincallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(fbLogincallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.e("Facebook Login Successful ", " response " + loginResult);
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.e("Facebook Login Error ", " response " + exception);
                    }
                });

        Boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if (isLoggedIn) {
            useLoginInformation(accessToken);
            Log.e("onCreate Login", " Access Token: " + accessToken.getToken());
        } else {
            // Intent goHome = new Intent(v.getContext(), ListReviewActivity.class);
            LinearLayout userImageLayout = findViewById(R.id.userImageLayout);
            LinearLayout userNameLayout = findViewById(R.id.userNameLayout);
            userImageLayout.setVisibility(View.GONE);
            userNameLayout.setVisibility(View.GONE);
        }

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken != null) {
                    accessToken = currentAccessToken;
                    useLoginInformation(currentAccessToken);
                    startActivity(getIntent());
                } else {
                    // Intent goHome = new Intent(v.getContext(), ListReviewActivity.class);
                    startActivity(getIntent());
                    LinearLayout userImageLayout = findViewById(R.id.userImageLayout);
                    LinearLayout userNameLayout = findViewById(R.id.userNameLayout);
                    userImageLayout.setVisibility(View.GONE);
                    userNameLayout.setVisibility(View.GONE);
                }
            }
        };

        spnRadius = findViewById(R.id.spnRadius);
        spnCategory = findViewById(R.id.spnCategory);

        tvAddress = findViewById(R.id.tvAddress);
        tvUserName = findViewById(R.id.tvUserName);
        ivUserImage = findViewById(R.id.ivUserImage);
        tvWpUserId = findViewById(R.id.tvWpUserId);
        textSwitcher = findViewById(R.id.textSwitcher);

        Animation fadeIn = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in);

        Animation fadeOut = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out);

        textSwitcher.setInAnimation(fadeIn);
        textSwitcher.setOutAnimation(fadeOut);


        textSwitcher.setCurrentText("The Sable Business Directory is a perfect platform for supporting black owned businesses and services providers of any kind.");


        /*
            BEGIN vertical Recycler View
         */
        verticalRecyclerView = findViewById(R.id.verticalRecyclerView);
        progressBar = findViewById(R.id.progressbar);
        mLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        verticalRecyclerView.setLayoutManager(mLayoutManager);
        verticalList = new ArrayList<>();
        locationMatch = new ArrayList<>();
        verticalAdapter = new VerticalAdapter(verticalList, userName, userEmail, userImage, userId, MainActivity.this);
        verticalRecyclerView.setAdapter(verticalAdapter);
        btnAdd = findViewById(R.id.btnAdd);
        btnShop = findViewById(R.id.btnShop);
        spokesperson = findViewById(R.id.spokesperson);

        final TextView texty = findViewById(R.id.texty);
        /**
         *  radius spinner
         */

        spinnerArrayRad.add("Search Radius");
        //spinnerArrayRad.add("5");
        spinnerArrayRad.add("10");
        spinnerArrayRad.add("15");
        spinnerArrayRad.add("20");

        ArrayAdapter<String> adapterRad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArrayRad);
        adapterRad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRadius.setAdapter(adapterRad);

        spnRadius.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Map<String, String> query = new HashMap<>();
                if (parent != null) {
                    switch (position) {
                        case 1:
                            radius = "Within' 10 miles of your current location";

                            query.put("latitude", Double.toString(longitude));
                            query.put("longitude", Double.toString(latitude));
                            query.put("distance", "10");
                            query.put("order", "asc");
                            query.put("orderby", "distance");
                            // 10 mile distance query
                            getRetrofit(query);
                            texty.setText(radius);
                            break;
                        case 2:
                            radius = "Within' 15 miles of your current location";

                            query.put("latitude", Double.toString(longitude));
                            query.put("longitude", Double.toString(latitude));
                            query.put("distance", "15");
                            query.put("order", "asc");
                            query.put("orderby", "distance");
                            //15 mile distance query
                            getRetrofit(query);
                            texty.setText(radius);
                            break;
                        case 3:
                            radius = "Within' 20 miles of your current location";

                            query.put("latitude", Double.toString(longitude));
                            query.put("longitude", Double.toString(latitude));
                            query.put("distance", "20");
                            query.put("order", "asc");
                            query.put("orderby", "distance");
                            // 20 mile distance query
                            getRetrofit(query);
                            texty.setText(radius);
                            break;
                        default:
                            radius = "Within' 5 miles of your current location";
                            texty.setText(radius);
                            break;
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub
            }
        });

        category.add("Category"); //add heading to category spinner
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, category);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(adapterCategory);

        spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (spnCategory.getSelectedItem() != "Category") {

                    Map<String, String> query = new HashMap<>();
                    //spinner category query
                    query.put("category", spnCategory.getSelectedItem().toString());
                    getRetrofit(query);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub
            }
        });


        /**
         *  location add button
         */

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isLoggedIn) {
                    Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(loginIntent);

                    //goto login activity get username and email via facebook create account, return here to check again and proceed

                    Toast.makeText(getApplicationContext(), "User must be logged in to add a business listing.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, AddListingActivity.class);
                    startActivity(intent);
                }
            }

            //}
        });

        btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WooProductDetail.class);
                startActivity(intent);
            }
        });

        spokesperson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AboutUs.class);
                startActivity(intent);
            }
        });

        /**
         *  directory search
         */

        searchView = findViewById(R.id.search);

        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Tap To Search");
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));


        Intent search = getIntent();
        if (Intent.ACTION_SEARCH.equals(search.getAction())) {
            Map<String, String> query = new HashMap<>();

            //search bar query
            query.put("order", "asc");
            query.put("orderby", "distance");
            query.put("search", search.getStringExtra(SearchManager.QUERY));
            getRetrofit(query);
        }

        updateMsg = new Thread() {
            @Override
            public void run() {
                try {
                    while (!updateMsg.isInterrupted()) {
                        updateMsg.sleep(FRAME_TIME_MS);
                        runOnUiThread(() -> {
                            Random randomGenerator = new Random();
                            int randomInt = randomGenerator.nextInt(3);
                            switch (randomInt) {

                                case 1:
                                    textSwitcher.setText("We provide a one of a kind online platform that combines " +
                                            "a searchable geographical based geo-directory, social media and e-commerce platforms " +
                                            "catered specifically to black owned businesses and service providers. ");
                                    break;

                                case 2:
                                    textSwitcher.setText("The Sable Business Directory is designed to help those wanting to support " +
                                            "and frequent black owned businesses and service providers find black owned " +
                                            "businesses and service providers.");
                                    break;

                                default:
                                    textSwitcher.setText("Tap our spokesman to the right for an introduction and tutorial on what " +
                                            "The Sable Business Directory is and how it works!!!");
                                    break;
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        updateMsg.start();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();
        //userActivityArray = null;
        if (userActivityArray.size() > 0) {

            userActivityArray = this.getIntent().getExtras().getStringArrayList("userActivityArray");
        }

        /**
         *  location manager to get current location
         */
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkPermissions();
            return;
        }

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000,
                400, LocationListener);
    }


   // List<String> providers;


    /**
     * Checks the dynamically-controlled permissions and requests missing permissions from end user.
     */
    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);

            int [] foo = grantResults;
            //startActivity(getIntent());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }  else {
                        startActivity(getIntent());
                    }
                }
                // all permissions were granted
                break;
        }
    }

    public void onStart() {
        super.onStart();
//This starts the access token tracking
        accessTokenTracker.startTracking();
    }

    public void onDestroy() {
        super.onDestroy();
        // We stop the tracking before destroying the activity
        accessTokenTracker.stopTracking();
        deleteCache(this);
    }

    public void useLoginInformation(final AccessToken accessToken) {


        /**
         Creating the GraphRequest to fetch user details
         1st Param - AccessToken
         2nd Param - Callback (which will be invoked once the request is successful)
         **/
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            //OnCompleted is invoked once the GraphRequest is successful
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Picasso.Builder builder = new Picasso.Builder(getApplicationContext());
                try {
                    AccessToken mAccessToken = accessToken;
                    userName = object.getString("name");
                    userEmail = object.getString("email");
                    userImage = object.getJSONObject("picture").getJSONObject("data").getString("url");

                    String[] parts = (object.getString("name").split(" "));
                    firstName = parts[0];
                    lastName = parts[1];
                    tvUserName.setText(firstName);
                    //    tvUserEmail.setText(object.getString("email"));
                    builder.build().load(object.getJSONObject("picture").getJSONObject("data").getString("url")).into(ivUserImage);

                    Map<String, String> query = new HashMap<>();
                    query.put("access_token", mAccessToken.getToken());
                    loginUser(query);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        // We set parameters to the GraphRequest using a Bundle.
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.width(200)");
        request.setParameters(parameters);
        // Initiate the GraphRequest
        request.executeAsync();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fbLogincallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     *  get last known location
     */

    private void fetchLastLocation() {

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Map<String, String> query = new HashMap<>();

                        lstKnownLat = location.getLatitude();
                        lstKnownLng = location.getLongitude();
                        query.put("latitude",  String.valueOf(lstKnownLat));
                        query.put("longitude", String.valueOf(lstKnownLng));
                        //query.put("distance", "5");
                        query.put("order", "asc");
                        query.put("orderby", "distance");

                        // zoom to current location on map
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                                .zoom(17)                   // Sets the zoom
                                .bearing(90)                // Sets the orientation of the camera to east
                                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                                .build();                   // Creates a CameraPosition from the builder
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                });

    }

    /**
     * Location listener to get device current lat/lng
     */

    LocationListener LocationListener = new LocationListener() {

        /**
         * @param location
         */
        @Override
        public void onLocationChanged(Location location) {
            Map<String, String> query = new HashMap<>();

            latitude = location.getLatitude();
            longitude = location.getLongitude();
            query.put("latitude", String.valueOf(latitude));
            query.put("longitude", String.valueOf(longitude));
            //query.put("distance", "5");
            query.put("order", "asc");
            query.put("orderby", "distance");

            // zoom to current location on map
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            getRetrofit(query); //api call; pass current lat/lng to check if current location in database
            setAddress(latitude, longitude);  // method to reverse geocode to physical address
        }

        /**
         * @param provider
         * @param status
         * @param extras
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        /**
         * @param provider
         */
        @Override
        public void onProviderEnabled(String provider) {
        }

        /**
         * @param provider
         */
        @Override
        public void onProviderDisabled(String provider) {
        }

    };

   // LocationManager mLocationManager;

    /**
     * @param map
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        //checkPermissions();
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
    }

    /**
     * @return
     */
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Getting current location...", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    /**
     * @param location
     */
    @Override
    public void onMyLocationClick(Location location) {
        //get location address
      //  this.latitude = location.getLatitude();
        //this.longitude = location.getLongitude();
        Map<String, String> query = new HashMap<>();

        query.put("latitude", Double.toString(latitude));
        query.put("longitude", Double.toString(longitude));
        query.put("distance", "5");

        // zoom to current location on map
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        setAddress(latitude, longitude);
        getRetrofit(query);
    }

    /**
     * geocode location address using lat/lng
     * @param latitude
     * @param longitude
     */
    public void setAddress(Double latitude, Double longitude){
       //this.latitude = latitude;
       //this.longitude = longitude;

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 3); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(addresses != null) {
            Log.d("max", " " + addresses.get(0).getMaxAddressLineIndex());

            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()/*String city = addresses.get(0).getLocality();
            bldgno = addresses.get(0).getSubThoroughfare(); // get bulding number
            street = addresses.get(0).getThoroughfare(); //get street name
            city = addresses.get(0).getLocality(); //get city name
            state = addresses.get(0).getAdminArea(); //get state name
            zipcode = addresses.get(0).getPostalCode(); //get zip code
            country = addresses.get(0).getCountryName(); //get country
            tvAddress.setText(address);
            addresses.get(0).getAdminArea();
        } else {

            Toast.makeText(this, "No GPS location available!  " +
                    "Please check your mobile device for possible service issues." , Toast.LENGTH_LONG).show();
        }

    }
    /**
     * Query API for listings data
     * set URL and make call to API
     */

    public class BasicAuthInterceptor implements Interceptor {

        private String credentials;

        public BasicAuthInterceptor(String user, String password) {
            this.credentials = Credentials.basic(user, password);
        }

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request authenticatedRequest = request.newBuilder()
                    .header("Authorization", credentials).build();
            return chain.proceed(authenticatedRequest);
        }

    }

    private static Retrofit retrofit = null;
    public void getRetrofit(final Map<String, String> query) {

      /*  HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);



        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new BasicAuthInterceptor(username, password))
                .addInterceptor(logging)
                .build();*/


        //if(retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
       // }
        RetrofitArrayApi service = retrofit.create(RetrofitArrayApi.class);

        // pass JSON data to BusinessListings class for filtering
        Call<List<BusinessListings>> call = service.getPostInfo(query);


        // get filtered data from BusinessListings class and add to recyclerView adapter for display on screen
        call.enqueue(new Callback<List<BusinessListings>>() {
            @Override
            public void onResponse(Call<List<BusinessListings>> call, Response<List<BusinessListings>> response) {
                Log.e("getRetrofit_METHOD_SUCCESS ", " response " + response.body());
                //Log.e("main_activity", " response " + response.body());
                if (response.isSuccessful()) {

                    // mListPost = response.body();
                    progressBar.setVisibility(View.GONE); //hide progressBar
                    // loop through JSON response get parse and output to log

                    for (int i = 0; i < response.body().size(); i++) {

                        BusinessListings.BusinessHours businessHours = response.body().get(i).getBusinessHours();

                        if(businessHours == null){
                            String today= "null";
                            //Log.e("Location ", " Today: " +today);
                            //Log.e("Location ", " IsOpen: " +today);
                        } else {
                            todayRange = response.body().get(i).getBusinessHours().getRendered().getExtra().getTodayRange();
                            isOpen =  response.body().get(i).getBusinessHours().getRendered().getExtra().getCurrentLabel();
                        }
                        /**
                         * onLocationMatch
                         * if device lat/lng equals stored listing lat/lng locationMatch = true
                         * add all matching data to array and launch Review Activity
                         *
                         */

                        if (String.valueOf(response.body().get(i).getLatitude()).equals(String.valueOf(latitude)) &&
                                String.valueOf(response.body().get(i).getLongitude()).equals(String.valueOf(longitude)) && userActivityArray.size() > 0 ) {

                            // Boolean timeDiff = response.body().get(i).getDateGmt().compareTo(currentTime) > 4;

                            locationMatch.add(new ListingsModel(ListingsModel.IMAGE_TYPE,
                                    response.body().get(i).getId(),
                                    response.body().get(i).getTitle().getRaw(),
                                    response.body().get(i).getLink(),
                                    response.body().get(i).getStatus(),
                                    response.body().get(i).getPostCategory().get(0).getName(),
                                    response.body().get(i).getFeatured(),
                                    response.body().get(i).getFeaturedImage().getSrc(),
                                    response.body().get(i).getBldgNo(),
                                    response.body().get(i).getStreet(),
                                    response.body().get(i).getCity(),
                                    response.body().get(i).getRegion(),
                                    response.body().get(i).getCountry(),
                                    response.body().get(i).getZip(),
                                    response.body().get(i).getLatitude(),
                                    response.body().get(i).getLongitude(),
                                    response.body().get(i).getRating(),
                                    response.body().get(i).getRatingCount(),
                                    response.body().get(i).getPhone(),
                                    response.body().get(i).getEmail(),
                                    response.body().get(i).getWebsite(),
                                    response.body().get(i).getTwitter(),
                                    response.body().get(i).getFacebook(),
                                    response.body().get(i).getVideo(),
                                    todayRange,
                                    isOpen,
                                    response.body().get(i).getLogo(),
                                    response.body().get(i).getContent().getRaw(),
                                    response.body().get(i).getFeaturedImage().getSrc()));

                            Intent LocationMatch = new Intent(MainActivity.this, ReviewActivity.class);
                            Bundle locationMatchBundle = new Bundle();
                            locationMatchBundle.putParcelableArrayList("locationMatchBundle", locationMatch);
                            LocationMatch.putExtra("locationMatch", locationMatch);
                            startActivity(LocationMatch);
                            break;
                        } else {
                            /**
                             * populate vertical recycler in Main Activity
                             */
                            verticalList.add(new ListingsModel(ListingsModel.IMAGE_TYPE,
                                    response.body().get(i).getId(),
                                    response.body().get(i).getTitle().getRaw(),
                                    response.body().get(i).getLink(),
                                    response.body().get(i).getStatus(),
                                    response.body().get(i).getPostCategory().get(0).getName(),
                                    response.body().get(i).getFeatured(),
                                    response.body().get(i).getFeaturedImage().getSrc(),
                                    response.body().get(i).getBldgNo(),
                                    response.body().get(i).getStreet(),
                                    response.body().get(i).getCity(),
                                    response.body().get(i).getRegion(),
                                    response.body().get(i).getCountry(),
                                    response.body().get(i).getZip(),
                                    response.body().get(i).getLatitude(),
                                    response.body().get(i).getLongitude(),
                                    response.body().get(i).getRating(),
                                    response.body().get(i).getRatingCount(),
                                    response.body().get(i).getPhone(),
                                    response.body().get(i).getEmail(),
                                    response.body().get(i).getWebsite(),
                                    response.body().get(i).getTwitter(),
                                    response.body().get(i).getFacebook(),
                                    response.body().get(i).getVideo(),
                                    todayRange,
                                    isOpen,
                                    response.body().get(i).getLogo(),
                                    response.body().get(i).getContent().getRaw(),
                                    response.body().get(i).getFeaturedImage().getSrc()));

                            // add category name from array to spinner
                            category.add(response.body().get(i).getPostCategory().get(0).getName());
                        }
                        verticalAdapter.notifyDataSetChanged();

                    }
                }
                else {
                    Log.e("getRetrofit_METHOD_noResponse ", " SOMETHING'S FUBAR'd!!! :)");
                }
            }

            @Override
            public void onFailure(Call<List<BusinessListings>> call, Throwable t) {
                Log.e("getRetrofit_METHOD_FAILURE ", " Re-running method...");
                getRetrofit(query);
            }
        });

    }

    //Retrofit retrofit = null;
    public void loginUser(final Map<String, String> query) {
        Retrofit retrofit = null;
    /*
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);



        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                //.addInterceptor(BasicAuthInterceptor(username, password))
                .addInterceptor(logging)
                .build(); */


        if(retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        RetrofitArrayApi service = retrofit.create(RetrofitArrayApi.class);

        // pass JSON data to BusinessListings class for filtering
        Call<UserAuthPOJO> call = service.getUserInfo(query);


        // get filtered data from BusinessListings class and add to recyclerView adapter for display on screen
        call.enqueue(new Callback<UserAuthPOJO>() {
            @Override
            public void onResponse(Call<UserAuthPOJO> call, Response<UserAuthPOJO> response) {
                Log.e("loginUser_METHOD_SUCCESS", " response " + response.body());
                if (response.isSuccessful()) {

                   // String status = response.body().getStatus();

                       // String WpStatus = response.body().getStatus();
                        //String WpMsg = response.body().getMsg();
                        userId = String.valueOf(response.body().getWpUserId());
                        tvWpUserId.setText(String.valueOf(response.body().getWpUserId()));
                       // String WpCookie = response.body().getCookie();
                        //tvWpUserLogin.setText(response.body().getUserLogin());
                } else {
                    Log.e("loginUser_METHOD_noResponse ", " SOMETHING'S FUBAR'd!!! :)");
                }
            }

            @Override
            public void onFailure(Call<UserAuthPOJO> call, Throwable t) {
                Log.e("loginUser_METHOD_FAILURE ", " Re-running method...");
              //  loginUser(query);

            }
        });
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}