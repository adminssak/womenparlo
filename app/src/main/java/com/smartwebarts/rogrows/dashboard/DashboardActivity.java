package com.smartwebarts.rogrows.dashboard;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.smartwebarts.rogrows.ContactUsActivity;
import com.smartwebarts.rogrows.MyApplication;
import com.smartwebarts.rogrows.R;
import com.smartwebarts.rogrows.dashboard.ui.home.HomeFragment;
import com.smartwebarts.rogrows.models.CategoryModel;
import com.smartwebarts.rogrows.models.PincodeModel;
import com.smartwebarts.rogrows.retrofit.UtilMethods;
import com.smartwebarts.rogrows.retrofit.mCallBackResponse;
import com.smartwebarts.rogrows.shopbycategory.ShopByCategoryActivity;
import com.smartwebarts.rogrows.SignInActivity;
import com.smartwebarts.rogrows.WebViewActivity;
import com.smartwebarts.rogrows.history.MyHistoryActivity;
import com.smartwebarts.rogrows.profile.ProfileActivity;
import com.smartwebarts.rogrows.utils.Toolbar_Set;
import com.smartwebarts.rogrows.vendors.VendorActivity;
import com.smartwebarts.rogrows.wallet.WalletActivity;
import com.smartwebarts.rogrows.wishlist.WishListActivity;
import com.smartwebarts.rogrows.cart.CartActivity;
import com.smartwebarts.rogrows.database.DatabaseClient;
import com.smartwebarts.rogrows.shared_preference.AppSharedPreferences;
import com.smartwebarts.rogrows.utils.ApplicationConstants;
public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_LOCATION = 202;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private GoogleApiClient googleApiClient;
    private EditText searchView;
    private ImageView search;
    private TextView ed_search;
    @Override

    private TextView deliverLocation,pincode;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        MyApplication application = (MyApplication) getApplication();
        AppSharedPreferences preferences = new AppSharedPreferences(getApplication());
        application.logLeonEvent("Dashboard", "Dashboard Viewed " + "by "+ preferences.getLoginMobile(), 0);
        searchView=findViewById(R.id.searchView);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        Toolbar_Set.INSTANCE.setBottomNav(this);
        setSupportActionBar(toolbar);
        getCartList();
        checkPermission();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        setNavigationDrawer();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration mAppBarConfiguration = new AppBarConfiguration.Builder(
                /*R.id.nav_home,*/ R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();

        UtilMethods.INSTANCE.version(this, null);

        deliverLocation = findViewById(R.id.deliverLocation);
        pincode = findViewById(R.id.pincode2);

        search = findViewById(R.id.search);
        ed_search = findViewById(R.id.ed_search);
        ed_search.setOnClickListener(v -> {

            final NavController controller = Navigation.findNavController(this, R.id.nav_host_fragment);
            if (controller.getCurrentDestination()!=null && controller.getCurrentDestination().getId() == R.id.nav_home) {
                HomeFragment.openSearchFragment(v);

            }
        });

        search.setOnClickListener(v -> {

            final NavController controller = Navigation.findNavController(this, R.id.nav_host_fragment);
            if (controller.getCurrentDestination()!=null && controller.getCurrentDestination().getId() == R.id.nav_home) {
                HomeFragment.openSearchFragment(v);

            }
        });
         showPincode();
    }

    void showPincode() {
        UtilMethods.INSTANCE.getPincode(this, new mCallBackResponse() {
            @Override
            public void success(String from, String message) {
                Type listType = new TypeToken<List<PincodeModel>>(){}.getType();
                List<PincodeModel> list = new Gson().fromJson(message, listType);
                PincodeModel pincodeModel = list.get(0);
                String showPin = String.format("%s", pincodeModel.getPin_code());
                System.out.println("pin---from---getPincode()"+showPin);
//                pincode.setText(showPin);
                  pincode.setText("Lucknow, Uttar Pradesh, 226012");
               /* pincode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });*/
            }

            @Override
            public void fail(String from) {
            }
        });
    }

    private void turnongps() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
//            Toast.makeText(getApplicationContext(),"Gps already enabled",Toast.LENGTH_SHORT).show();
            //getActivity().finish();
        }
        // Todo Location Already on  ... end

        if (!hasGPSDevice(this)) {
//            Toast.makeText(getApplicationContext(),"Gps not Supported",Toast.LENGTH_SHORT).show();
        }

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
            // Log.e("Neha","Gps already enabled");
//            Toast.makeText(getApplicationContext(),"Gps not enabled",Toast.LENGTH_SHORT).show();
            enableLoc();
        } else {
            // Log.e("Neha","Gps already enabled");
            // Toast.makeText(getApplicationContext(),"Gps already enabled",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }


    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(7 * 1000);  //30 * 1000
            locationRequest.setFastestInterval(5 * 1000); //5 * 1000
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(DashboardActivity.this, REQUEST_LOCATION);

                                // getActivity().finish();
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;

                    }
                }
            });
        }
    }


    private void setNavigationDrawer() {

        View headerLayout = navigationView.getHeaderView(0);
        Menu nav_Menu = navigationView.getMenu();
        TextView tvUser = headerLayout.findViewById(R.id.tvName);
        TextView tvEmail = headerLayout.findViewById(R.id.tvEmail);
        ImageView p = findViewById(R.id.person);

        AppSharedPreferences preferences = new AppSharedPreferences(getApplication());

        if (preferences.getLoginUserName() != null && !preferences.getLoginUserName().isEmpty()) {
            String[] s = preferences.getLoginUserName().trim().split("\\s+");
            tvUser.setText("Welcome " + s[0]);

        } else {
            nav_Menu.findItem(R.id.nav_gallery).setVisible(false);
            nav_Menu.findItem(R.id.my_wishlist).setVisible(false);
            nav_Menu.findItem(R.id.my_account).setVisible(false);
            nav_Menu.findItem(R.id.logout).setVisible(false);
            nav_Menu.findItem(R.id.my_wallet).setVisible(false);
            nav_Menu.findItem(R.id.share_app).setVisible(false);
            p.setVisibility(View.GONE);
        }
        p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DashboardActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });
        if (preferences.getLoginEmail() != null && !preferences.getLoginEmail().isEmpty()) {
            tvEmail.setText("" + preferences.getLoginEmail());
        } else {
            tvEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(DashboardActivity.this, SignInActivity.class));
                }
            });
        }

    }


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
        }

        final NavController controller = Navigation.findNavController(this, R.id.nav_host_fragment);

        if (controller.getCurrentDestination().getId() == R.id.searchFragment)
            super.onBackPressed();
        else {

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(
                    new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    },
                    2000
            );
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.nav_home: {
                startActivity(new Intent(DashboardActivity.this, ShopByCategoryActivity.class));
                break;
            }
            case R.id.nav_gallery: {
                startActivity(new Intent(DashboardActivity.this, MyHistoryActivity.class));
                break;
            }
            case R.id.my_basket: {
                startActivity(new Intent(DashboardActivity.this, CartActivity.class));
                break;
            }
            case R.id.my_wishlist: {
                startActivity(new Intent(DashboardActivity.this, WishListActivity.class));
                break;
            }
            case R.id.vendors: {
                startActivity(new Intent(DashboardActivity.this, VendorActivity.class));
                break;
            }
            case R.id.my_account: {
                AppSharedPreferences preferences = new AppSharedPreferences(getApplication());
                if (!preferences.getLoginUserLoginId().isEmpty()) {
                    startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
                } else {
                    Toast.makeText(this, "Login or Create a Account", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.my_wallet: {
                AppSharedPreferences preferences = new AppSharedPreferences(getApplication());
                if (!preferences.getLoginUserLoginId().isEmpty()) {

                    drawer.closeDrawer(GravityCompat.START);
                    startActivity(new Intent(DashboardActivity.this, WalletActivity.class));
                } else {
                    Toast.makeText(this, "Login or Create a Account", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.terms: {
                Intent intent = new Intent(DashboardActivity.this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.DATA, ApplicationConstants.INSTANCE.TERMS_CONDITION);
                intent.putExtra(WebViewActivity.TITLE, "Terms & Conditions");
                startActivity(intent);
                break;
            }
            case R.id.privacy_policy: {
                Intent intent = new Intent(DashboardActivity.this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.DATA, ApplicationConstants.INSTANCE.PRIVACY_POLICY);
                intent.putExtra(WebViewActivity.TITLE, "Privacy Policy");
                startActivity(intent);
                break;
            }
           /* case R.id.refund_policy: {
                Intent intent = new Intent(DashboardActivity.this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.DATA, ApplicationConstants.INSTANCE.REFUND_POLICY);
                intent.putExtra(WebViewActivity.TITLE, "Refund Policy");
                startActivity(intent);
                break;
            }*/
            case R.id.about_us: {
                Intent intent = new Intent(DashboardActivity.this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.DATA, ApplicationConstants.INSTANCE.ABOUT_US);
                intent.putExtra(WebViewActivity.TITLE, "About Us");
                startActivity(intent);
                break;
            }
            case R.id.live_chat: {
                try {
//                    String url = "https://chat.whatsapp.com/";
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    String url = "https://api.whatsapp.com/send?phone="+"+91";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (Exception e) {
                    Toast.makeText(this, "Unable to open your whatsapp", Toast.LENGTH_SHORT).show();
                }
//                Intent intent = new Intent(DashboardActivity.this, WebViewActivity.class);
//                intent.putExtra(WebViewActivity.DATA, ApplicationConstants.INSTANCE.LIVE_CHAT);
////                intent.putExtra(WebViewActivity.DATA, "https://www.google.com/maps/dir/26.8610992,80.8462559/Smart Web Arts, Kailash Plaza,Kanpur Road, Sector G, LDA Colony, Lucknow, Uttar Pradesh 226012/");
//                intent.putExtra(WebViewActivity.TITLE, "Live Chat");
//                startActivity(intent);
                break;
            }
            case R.id.share_app: {
                createlink();
                break;
            }
            case R.id.contact_us: {
                startActivity(new Intent(DashboardActivity.this, ContactUsActivity.class));
                break;
            }
            case R.id.faq: {
                Intent intent = new Intent(DashboardActivity.this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.DATA, ApplicationConstants.INSTANCE.FAQ);
                intent.putExtra(WebViewActivity.TITLE, "FAQs");
                startActivity(intent);
                break;
            }
            case R.id.logout: {
                logout();
                break;
            }
        }
        //close navigation drawer
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void createlink() {


//        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
//                .setLink(Uri.parse(ApplicationConstants.INSTANCE.SITE_URL))
//                .setDomainUriPrefix("globalfreshbasket.page.link")
//                // Open links with this app on Android
//                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
//                // Open links with com.example.ios on iOS
//                .setIosParameters(new DynamicLink.IosParameters.Builder(BuildConfig.APPLICATION_ID).build())
//                .buildDynamicLink();
//
//        Uri dynamicLinkUri = dynamicLink.getUri();

        AppSharedPreferences preferences = new AppSharedPreferences(getApplication());
        String referlink = "https://rogroz.page.link/6RQi?" +
                "apn=" + BuildConfig.APPLICATION_ID +
                "&st=" + getString(R.string.app_name) +
                "&sd=" + getString(R.string.tagline) +
                "&si=https:///assets/img/web/logo/1601636146finle%20logo.png" +
                "&link=" + ApplicationConstants.INSTANCE.SITE_URL + "api.php?custid=" + preferences.getLoginUserLoginId();

        Log.e("referlink", referlink);
        /*short the link*/
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
//                .setLongLink(dynamicLinkUri)
                .setLongLink(Uri.parse(referlink))
//                .setDomainUriPrefix("https://example.page.link")
                // Set parameters
                // ...
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, shortLink.toString());
                            sendIntent.setType("text/plain");
                            startActivity(sendIntent);
                        } else {

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Log.e("TAG", "onFailure: "+e.getMessage() );
//                        e.printStackTrace();

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                    }
                });
    }

    private void logout() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.default_progress_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progress);
        DoubleBounce doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);
        dialog.show();

        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().logOut();
        AccessToken.setCurrentAccessToken(null);

        FirebaseAuth.getInstance().signOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(DashboardActivity.this, gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                AppSharedPreferences preferences = new AppSharedPreferences(DashboardActivity.this.getApplication());
                preferences.logout(DashboardActivity.this);
                dialog.dismiss();
            }
        });
    }

    public void showCart(View view) {
        startActivity(new Intent(this, CartActivity.class));
    }

    public void refresh(View view) {
        getCartList();
    }

    public void profile(View view) {
        Intent intent=new Intent(DashboardActivity.this,ProfileActivity.class);
        startActivity(intent);
    }

    private void getCartList() {

        class GetTasks extends AsyncTask<Void, Void, ArrayList<com.smartwebarts.rogrows.database.Task>> {

            @Override
            protected ArrayList<com.smartwebarts.rogrows.database.Task> doInBackground(Void... voids) {
                List<com.smartwebarts.rogrows.database.Task> tasks = DatabaseClient.getmInstance(getApplicationContext())
                        .getAppDatabase()
                        .taskDao()
                        .getAll();
                return new ArrayList<>(tasks);
            }

            @Override
            protected void onPostExecute(ArrayList<com.smartwebarts.rogrows.database.Task> tasks) {
//                Toast.makeText(getApplicationContext(), ""+tasks.size(), Toast.LENGTH_SHORT).show();
                int size = tasks != null ? tasks.size() : 0;
                TextView cartItemsCount = findViewById(R.id.cartItemsCount);
                cartItemsCount.setText("" + size);
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Toolbar_Set.INSTANCE.getCartList(this);
        setNavigationDrawer();

    }
    public void openDrawer(View view) {
        drawer.openDrawer(GravityCompat.START);
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // You can use the API that requires the permission.
            turnongps();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 101:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    turnongps();
                } else {
                    turnongps();
                }
                return;
        }
    }
    public void change_pincode(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DashboardActivity.this);
        alertDialog.setTitle("Select Location");
        String[] items = {"Adhartal, Jabalpur - 234561",
                            "Vijaynagar, Jabalpur - 434210",
                            "Lanka, Varanasi - 221005",
                            "Durgakund, Varanasi - 221007"};
        int checkedItem = 1;
        alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Toast.makeText(DashboardActivity.this, "Clicked on "+items[which], Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(DashboardActivity.this, "Clicked on "+items[which], Toast.LENGTH_LONG).show();

                        break;
                    case 2:
                        Toast.makeText(DashboardActivity.this, "Clicked on "+items[which], Toast.LENGTH_LONG).show();
                          break;
                    case 3:
                        Toast.makeText(DashboardActivity.this, "Clicked on "+items[which], Toast.LENGTH_LONG).show();
                        break;
                }
                pincode.setText(items[which]);
            }
        });
        alertDialog.setPositiveButton("Submit", (dialog, which) -> {
            System.out.println(Arrays.toString(items));
            Toast.makeText(DashboardActivity.this, "Location set..", Toast.LENGTH_LONG).show();
        });
        AlertDialog alert = alertDialog.create();

        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}
