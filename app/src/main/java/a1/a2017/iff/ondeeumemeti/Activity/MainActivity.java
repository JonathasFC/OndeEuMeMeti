package a1.a2017.iff.ondeeumemeti.Activity;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;

import a1.a2017.iff.ondeeumemeti.Manifest;
import a1.a2017.iff.ondeeumemeti.R;
import a1.a2017.iff.ondeeumemeti.Utils.PermissionUtils;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private TextView txtInfo;
    private Button btnStart;
    private EditText etxtLatitude, etxtLongitude, etxtEndereco;
    private GoogleApiClient googleApiClient;
    private Address endereco;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private String end = null;

    String[] permissoes = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtInfo = (TextView) findViewById(R.id.txtInfo);

        etxtLatitude = (EditText) findViewById(R.id.etxtLatitude);
        etxtLongitude = (EditText) findViewById(R.id.etxtLongitude);
        etxtEndereco = (EditText) findViewById(R.id.etxtEndereco);

        PermissionUtils.validate(this, 0, permissoes);

        callConnection();

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etxtLatitude.getText().toString().length() != 0){
                    latitude = Double.parseDouble(etxtLatitude.getText().toString());
                }

                if (etxtLongitude.getText().toString().length() != 0){
                    longitude = Double.parseDouble(etxtLongitude.getText().toString());
                }

                end = etxtEndereco.getText().toString();

                if (end.length() != 0) {

                    try {
                        endereco = buscarEndereco(end);
                        txtInfo.setText("Latitude: " + endereco.getLatitude()
                                + "\nLongitude: " + endereco.getLongitude()
                        );
                    } catch (IOException e) {
                        Log.i("GPS", "Método buscar endereço: " + e.getMessage());
                    }

                } else {

                    try {
                        endereco = buscarCoordenadas(latitude, longitude);
                        txtInfo.setText("Rua: " + endereco.getAddressLine(0));
                    } catch (IOException e) {
                        Log.i("GPS", "Método buscar coordenadas: " + e.getMessage());
                    }

                }

            }

        });

    }

    private Address buscarEndereco(String end) throws IOException {

        Geocoder geocoder;
        Address address = null;
        List<Address> addresses;

        geocoder = new Geocoder(getApplicationContext());
        addresses = geocoder.getFromLocationName(end,1);

        if (addresses.size() > 0) {
            address = addresses.get(0);
        }
        return address;

    }

    private Address buscarCoordenadas(double latitude, double longitude) throws IOException {

        Geocoder geocoder;
        Address address = null;
        List<Address> addresses;

        geocoder = new Geocoder(getApplicationContext());
        addresses = geocoder.getFromLocation(latitude, longitude, 1);

        if (addresses.size() > 0) {
            address = addresses.get(0);
        }

        return address;

    }

    private synchronized void callConnection() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("LOG", "onConnected(" + bundle + ")");

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.i("LOG", "GPS não permitido!");

            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null){
            Log.i("LOG", "Latitude" + location.getLatitude());
            Log.i("LOG", "Longitude" + location.getLongitude());

        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("LOG", "onConnectionSuspended(" + i + ")");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("LOG", "onConnectionFailed(" + connectionResult + ")");

    }
}
