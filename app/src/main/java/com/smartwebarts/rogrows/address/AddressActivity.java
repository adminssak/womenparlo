package com.smartwebarts.rogrows.address;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.libraries.places.api.Places;
//import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.seatgeek.placesautocomplete.DetailsCallback;
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.seatgeek.placesautocomplete.model.AddressComponent;
import com.seatgeek.placesautocomplete.model.AddressComponentType;
import com.seatgeek.placesautocomplete.model.Place;
import com.seatgeek.placesautocomplete.model.PlaceDetails;
import com.smartwebarts.rogrows.R;
import com.smartwebarts.rogrows.database.Task;
import com.smartwebarts.rogrows.models.AddressModel;
import com.smartwebarts.rogrows.retrofit.UtilMethods;
import com.smartwebarts.rogrows.retrofit.mCallBackResponse;
import com.smartwebarts.rogrows.shared_preference.AppSharedPreferences;
import com.smartwebarts.rogrows.utils.AppLocationService;
import com.smartwebarts.rogrows.utils.GPSTracker;
import com.smartwebarts.rogrows.utils.LocationAddress;
import com.smartwebarts.rogrows.utils.Toolbar_Set;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.smartwebarts.rogrows.address.DeliveryOptionActivity.*;

public class AddressActivity extends AppCompatActivity {

    private static final String TAG = AddressActivity.class.getSimpleName();
    private static final int AUTOCOMPLETE_REQUEST_CODE = 101;
    TextInputEditText txtHouse, txtPin, txtCity, txtState, txtArea;
    String txtLandmark;
    private PlacesClient placesClient;

    private ArrayList<Task> list;
    public static final String PRODUCT_LIST  = "product";
    public static final String AMOUNT  = "amount";
    public static final String HASHMAP  = "hashmap";
    private PlacesAutocompleteTextView placesAutocomplete;
    private String amount;
    private HashMap<String, String> hashMap;
    GPSTracker gpsTracker;
    private AppSharedPreferences appSharedPreferences;
    private AddressModel addressModel;
    private RadioGroup rgLocationType;
    private RadioButton rbCurrentLocation, rbCustomLocation;
    private TextInputLayout input_layout_HOuseNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        Toolbar_Set.INSTANCE.setToolbar(this, "Add a Full Address");
        list = (ArrayList<Task>) getIntent().getExtras().getSerializable(PRODUCT_LIST);
        hashMap = (HashMap<String, String>) getIntent().getExtras().get(HASHMAP);
        System.out.println(hashMap);
        amount = getIntent().getExtras().getString(AMOUNT, "");
        txtArea = findViewById(R.id.txtArea);
        txtCity = findViewById(R.id.txtCity);
        txtState = findViewById(R.id.txtState);
        txtHouse = findViewById(R.id.txtHouse);
        txtPin = findViewById(R.id.txtPincode);
        rgLocationType = findViewById(R.id.rgLocationType);
        rbCurrentLocation = findViewById(R.id.rbCurrentLocation);
        rbCustomLocation = findViewById(R.id.rbCustomLocation);
        placesAutocomplete = findViewById(R.id.places_autocomplete);

        input_layout_HOuseNo = findViewById(R.id.input_layout_HOuseNo);
        appSharedPreferences = new AppSharedPreferences(getApplication());

//        getCurrentLocation(null);
        getSavedAddress();

        rgLocationType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton) findViewById(i);

                if (rb == rbCurrentLocation) {
                    txtHouse.setEnabled(false);
                    txtPin.setEnabled(false);
                    txtCity.setEnabled(false);
                    txtState.setEnabled(false);
                    txtArea.setEnabled(false);
                    input_layout_HOuseNo.setHint("Lat, Lng");
                    getCurrentLocation(rb);
                } else if (rb == rbCustomLocation) {
                    txtHouse.setEnabled(true);
                    txtHouse.setText("");
                    txtPin.setEnabled(true);
                    txtCity.setEnabled(true);
                    txtState.setEnabled(true);
                    txtArea.setEnabled(true);
                    input_layout_HOuseNo.setHint("House No., Building Name");
                    txtHouse.requestFocus();
                }
            }
        });

        rbCurrentLocation.setChecked(true);
    }

    private void getSavedAddress() {
        if (UtilMethods.INSTANCE.isNetworkAvialable(this)) {
            UtilMethods.INSTANCE.getaddress(this, "1", appSharedPreferences.getLoginUserLoginId()
                    , new mCallBackResponse() {
                        @Override
                        public void success(String from, String message) {
                            addressModel = new Gson().fromJson(message, AddressModel.class);
                            try {
                                if (addressModel.getAddress()!=null) {
                                    TextView address = findViewById(R.id.address);
                                    address.setText(String.format("%s %s %s %s", addressModel.getAddress(), addressModel.getCity(), addressModel.getState(), addressModel.getPincode()));
                                }
                            } catch (Exception ignored) {
                            }

                        }

                        @Override
                        public void fail(String from) {
                        }
                    });
        } else {
            UtilMethods.INSTANCE.internetNotAvailableMessage(this);
        }
    }

    public void getCurrentLocation(View view) {

        String apiKey = getString(R.string.location_api_key_id);

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        // Create a new Places client instance.
        placesClient = Places.createClient(this);

        gpsTracker = new GPSTracker(this);
        gpsTracker.getLocation();

        placesAutocomplete.setOnPlaceSelectedListener(
                new OnPlaceSelectedListener() {
                    @Override
                    public void onPlaceSelected(final Place place) {
                        // do something awesome with the selected place
                        placesAutocomplete.getDetailsFor(place, new DetailsCallback() {
                            @Override
                            public void onSuccess(PlaceDetails details) {

                                txtArea.setText("");
                                Log.d("test", "details " + details);
//                                mStreet.setText(details.name);
                                for (AddressComponent component : details.address_components) {
                                    for (AddressComponentType type : component.types) {
                                        switch (type) {
                                            case STREET_NUMBER:
                                                break;
                                            case ROUTE:
                                                break;
                                            case NEIGHBORHOOD:
                                                break;
                                            case SUBLOCALITY_LEVEL_1:
                                                break;
                                            case SUBLOCALITY:
                                                txtArea.append(" "+component.long_name);
                                                break;
                                            case LOCALITY:
                                                txtCity.setText(component.long_name);
                                                break;
                                            case ADMINISTRATIVE_AREA_LEVEL_1:
//                                                txtArea.setText(component.short_name);
                                                break;
                                            case ADMINISTRATIVE_AREA_LEVEL_2:
                                                break;
                                            case COUNTRY:
                                                break;
                                            case POSTAL_CODE:
                                                txtPin.setText(component.long_name);
                                                break;
                                            case POLITICAL:
                                                break;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Throwable throwable) {

                            }
                        });
                    }
                }
        );

        AppLocationService appLocationService = new AppLocationService(this);

        if (gpsTracker.canGetLocation()) {
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();

            txtHouse.setText(String.format("%s,%s", latitude, longitude));

            LocationAddress locationAddress = new LocationAddress();
            locationAddress.getAddressFromLocation(latitude, longitude,
                    getApplicationContext(), new GeocoderHandler());
        } else {
            showSettingsAlert();
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                AddressActivity.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        AddressActivity.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }


    public void next(View view) {

        if (validateform()) {
            Intent intent = new Intent(this, DeliveryOptionActivity.class);
            intent.putExtra(DeliveryOptionActivity.PRODUCT_LIST, list);
            intent.putExtra(ADDRESS,  txtHouse.getText().toString().trim() + " " +
                    txtArea.getText().toString().trim());
            intent.putExtra(CITY_KEY,  txtCity.getText().toString().trim());
            intent.putExtra(STATE_KEY,  txtState.getText().toString().trim());

            intent.putExtra(LANDMARK, placesAutocomplete.getText().toString());
            intent.putExtra(HASHMAP, hashMap);
            intent.putExtra(DeliveryOptionActivity.AMOUNT, amount);
            intent.putExtra(PINCODE, txtPin.getText().toString().trim());

            startActivity(intent);
        }
    }

    private boolean validateform() {

        if (txtHouse.getText().toString().isEmpty() ) {

            if (rbCurrentLocation.isChecked()) {
                txtHouse.setError("Enable GPS");
                getCurrentLocation(null);
            }

            if (rbCustomLocation.isChecked()) {
                txtHouse.setError("Enter House no./Building name");
            }

            return false;
        }

        if (rbCustomLocation.isChecked()) {
            if (txtPin.getText().toString().isEmpty() || txtPin.getText().toString().trim().length()<6) {
                txtPin.setError("Enter 6 digit Pincode");
                return false;
            }
            if (txtCity.getText().toString().isEmpty()) {
                txtCity.setError("Enter City");
                return false;
            }
            if (txtArea.getText().toString().isEmpty()) {
                txtArea.setError("Enter Area");
                return false;
            }
            if (placesAutocomplete.getText().toString().isEmpty() ) {
                placesAutocomplete.setError("Enter Landmark");
                return false;
            }
        }

        if (addressModel==null || (addressModel.getAddress()==null || addressModel.getAddress().isEmpty())
                || (addressModel.getLandmark()==null || addressModel.getLandmark().isEmpty())
                || (addressModel.getCity()==null || addressModel.getCity().isEmpty())
                || (addressModel.getState()==null || addressModel.getState().isEmpty())
                || (addressModel.getPincode()==null || addressModel.getPincode().isEmpty())
        ) {

            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Billing Address")
                    .setContentText("We do not have your complete billing address, Add a complete billing address")
                    .setConfirmText("Yes, Proceed")
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        OpenDialogForAddressChange(null);
                    })
                    .show();
            return false;
        }

        return true;
    }

    public void OpenDialogForAddressChange(View v1) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialogaddress, null);

        TextInputEditText txtArea = view.findViewById(R.id.txtArea);
        TextInputEditText txtCity = view.findViewById(R.id.txtCity);
        TextInputEditText txtState = view.findViewById(R.id.txtState);
        TextInputEditText txtHouse = view.findViewById(R.id.txtHouse);
        TextInputEditText txtPin = view.findViewById(R.id.txtPincode);
        Button FwdokButton = (Button) view.findViewById(R.id.okButton);
        Button cancelButton = (Button) view.findViewById(R.id.cancelButton);
        PlacesAutocompleteTextView placesAutocomplete = view.findViewById(R.id.places_autocomplete);

        if (addressModel!=null) {
            if (addressModel.getAddress()!=null) {
                String house = "", area = "";
                if (addressModel.getAddress().split(",").length>=1) {
                    house = addressModel.getAddress().split(",")[0];
                    txtHouse.setText(addressModel.getAddress().split(",")[0]);
                }

                if (addressModel.getAddress().split(",").length>=2) {
                    area = addressModel.getAddress().replaceFirst(house, "");
                    area = area.replaceFirst(",", "");
                    area = area.trim();
                    txtArea.setText(area);
                }

                if (addressModel.getCity()!= null) {
                    txtCity.setText(addressModel.getCity());
                }

                if (addressModel.getState()!= null) {
                    txtState.setText(addressModel.getState());
                }

                if (addressModel.getPincode()!= null) {
                    txtPin.setText(addressModel.getPincode());
                }

                if (addressModel.getLandmark()!= null) {
                    placesAutocomplete.setText(addressModel.getLandmark());
                }
            }
        }

        placesAutocomplete.setOnPlaceSelectedListener(
                place -> {
                    // do something awesome with the selected place
                    placesAutocomplete.getDetailsFor(place, new DetailsCallback() {
                        @Override
                        public void onSuccess(PlaceDetails details) {
                            Log.d("test", "details " + details);
//                                mStreet.setText(details.name);
                            for (AddressComponent component : details.address_components) {
                                for (AddressComponentType type : component.types) {
                                    switch (type) {
                                        case STREET_NUMBER:
                                            break;
                                        case ROUTE:
                                            break;
                                        case NEIGHBORHOOD:
                                            break;
                                        case SUBLOCALITY_LEVEL_1:
                                            break;
                                        case SUBLOCALITY:
                                            txtArea.append(" "+component.long_name);
                                            break;
                                        case LOCALITY:
                                            txtCity.setText(component.long_name);
                                            break;
                                        case ADMINISTRATIVE_AREA_LEVEL_1:
                                            break;
                                        case ADMINISTRATIVE_AREA_LEVEL_2:
                                            break;
                                        case COUNTRY:
                                            break;
                                        case POSTAL_CODE:
                                            txtPin.setText(component.long_name);
                                            break;
                                        case POLITICAL:
                                            break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Throwable throwable) {

                        }
                    });
                }
        );
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setContentView(view);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        FwdokButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!validate()) {
                    return;
                }

                if (UtilMethods.INSTANCE.isNetworkAvialable(AddressActivity.this)) {

                    UtilMethods.INSTANCE.setAddress(AddressActivity.this,
                            appSharedPreferences.getLoginUserLoginId(),
                            txtArea.getText().toString().trim(),
                            txtCity.getText().toString().trim(),
                            txtState.getText().toString().trim(),
                            txtHouse.getText().toString().trim(),
                            txtPin.getText().toString().trim(),
                            placesAutocomplete.getText().toString().trim(),
                            new mCallBackResponse() {
                                @Override
                                public void success(String from, String message) {
                                    getSavedAddress();
                                }

                                @Override
                                public void fail(String from) {

                                }
                            });

                } else {
                    UtilMethods.INSTANCE.internetNotAvailableMessage(AddressActivity.this);
                }

                dialog.dismiss();
            }

            private boolean validate() {

                if (txtArea.getText().toString().isEmpty()){
                    txtArea.setError("Field Required");
                    return false;
                }
                if (txtPin.getText().toString().isEmpty()){
                    txtPin.setError("Field Required");
                    return false;
                }
                if (txtCity.getText().toString().isEmpty()){
                    txtCity.setError("Field Required");
                    return false;
                }
                if (txtState.getText().toString().isEmpty()){
                    txtState.setError("Field Required");
                    return false;
                }
                if (txtHouse.getText().toString().isEmpty()){
                    txtHouse.setError("Field Required");
                    return false;
                }
                if (placesAutocomplete.getText().toString().isEmpty()){
                    placesAutocomplete.setError("Field Required");
                    return false;
                }
                return true;
            }
        });

        dialog.show();
    }

//    private void getAddress(TextView address) {
//        if (UtilMethods.INSTANCE.isNetworkAvialable(this)) {
//            UtilMethods.INSTANCE.getaddress(this, "1", appSharedPreferences.getLoginUserLoginId()
//                    , new mCallBackResponse() {
//                        @Override
//                        public void success(String from, String message) {
//                            addressModel = new Gson().fromJson(message, AddressModel.class);
//                            try {
//                                if (addressModel.getAddress()!=null) {
//                                    address.setText(String.format("%s, %s, %s", addressModel.getAddress(), addressModel.getCity(), addressModel.getPincode()));
//                                } else {
//                                    address.setText("Add an address");
//                                }
//                            } catch (Exception ignored) {
//                                address.setText("Add an address");
//                            }
//
//                        }
//
//                        @Override
//                        public void fail(String from) {
//                            address.setText("Add an address");
//                        }
//                    });
//        } else {
//            UtilMethods.INSTANCE.internetNotAvailableMessage(this);
//        }
//    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
           placesAutocomplete.setText(locationAddress);

            try {
                String[] strings = locationAddress.split(",");
                txtArea.setText(strings[0].trim());
                txtCity.setText(strings[1].trim());
                txtState.setText(strings[2].trim());
                txtPin.setText(strings[3].trim());
            } catch (Exception ignored) {}
        }
    }

}