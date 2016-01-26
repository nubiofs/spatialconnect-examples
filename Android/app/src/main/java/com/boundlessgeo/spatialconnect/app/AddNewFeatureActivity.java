package com.boundlessgeo.spatialconnect.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.boundlessgeo.spatialconnect.geometries.SCGeometry;
import com.boundlessgeo.spatialconnect.services.SCServiceManager;
import com.boundlessgeo.spatialconnect.stores.SCDataStore;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

import rx.functions.Action1;

public class AddNewFeatureActivity extends Activity implements OnMapReadyCallback {

    private GoogleMap map;
    private MapView mapView;
    private TextView latVal, lonVal;
    private SCServiceManager serviceManager;
    private SCDataStore ds;
    private SCGeometry newFeature;
    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private static final String LOG_TAG = AddNewFeatureActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_feature);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // initialize the map
        mapView = (MapView) findViewById(R.id.edit_point_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // initialize new feature (it's a point by default)
        newFeature = new SCGeometry(geometryFactory.createPoint(new Coordinate(0, 0)));
        newFeature.setStoreId("a5d93796-5026-46f7-a2ff-e5dec85heh6b");
        newFeature.setLayerId("point_features");

        // initialize layout
        final TextView storeIdVal = (TextView) findViewById(R.id.feature_detail_store_value);
        final TextView layerVal = (TextView) findViewById(R.id.feature_detail_layer_value);
        lonVal = (TextView) findViewById(R.id.feature_detail_lon_value);
        latVal = (TextView) findViewById(R.id.feature_detail_lat_value);
        storeIdVal.setText("a5d93796-5026-46f7-a2ff-e5dec85heh6b");
        layerVal.setText("point_features");

        // initialize data store
        serviceManager =  SpatialConnectService.getInstance().getServiceManager(this);
        ds = serviceManager.getDataService().getStoreById("a5d93796-5026-46f7-a2ff-e5dec85heh6b");

        // add callback for when button is clicked
        final Button button = (Button) findViewById(R.id.add_new_feature);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ds.create(newFeature).subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean created) {
                        Log.d(LOG_TAG, "feature was created");
                        finish();
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(0, 0)));

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                lonVal.setText(String.valueOf(cameraPosition.target.longitude));
                latVal.setText(String.valueOf(cameraPosition.target.latitude));
                newFeature.setGeometry(geometryFactory.createPoint(
                        new Coordinate(cameraPosition.target.longitude, cameraPosition.target.latitude)
                ));
            }
        });
    }
}
