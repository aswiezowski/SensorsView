package kis.agh.edu.pl.sensorsview;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlayLocationFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status>, LocationListener {

    protected GoogleApiClient mGoogleApiClient;


    protected Location mLastLocation;

    protected TextView mLatitudeText;
    protected TextView mLongitudeText;
    protected TextView mAddressText;

    protected ActivityDetectionBroadcastReceiver mBroadcastReceiver;
    private ListView mDetectedActivitiesListView;
    private DetectedActivitiesAdapter mAdapter;
    private ArrayList<DetectedActivity> mDetectedActivities;
    private OnFragmentInteractionListener mListener;
    private LocationRequest mLocationRequest;
    private Geocoder geocoder;

    public PlayLocationFragment() {
    }


    public static PlayLocationFragment newInstance() {
        PlayLocationFragment fragment = new PlayLocationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_play_location, container, false);

        mLatitudeText = (TextView) v.findViewById((R.id.tVLatValue));
        mLongitudeText = (TextView) v.findViewById((R.id.tVLongValue));
        mAddressText = (TextView) v.findViewById((R.id.tVAddressValue));

        mDetectedActivitiesListView = (ListView) v.findViewById(R.id.lVDetectedActivity);
        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();

            mDetectedActivities = new ArrayList<DetectedActivity>();

            for (int i = 0; i < Constants.MONITORED_ACTIVITIES.length; i++) {
                mDetectedActivities.add(new DetectedActivity(Constants.MONITORED_ACTIVITIES[i], 0));
            }

        mAdapter = new DetectedActivitiesAdapter(v.getContext(), mDetectedActivities);
        mDetectedActivitiesListView.setAdapter(mAdapter);
        setListViewHeightBasedOnChildren(mDetectedActivitiesListView);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        geocoder = new Geocoder(this.getActivity(), Locale.getDefault());
        buildGoogleApiClient();

        return v;
    }
    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this.getActivity(), DetectedActivitiesIntentService.class);
        return PendingIntent.getService(this.getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .build();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this.getActivity()).unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Constants.BROADCAST_ACTION));
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient,
                Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
    }




    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this.getActivity(), "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {

        } else {
            Toast.makeText(this.getActivity(), "Error adding or removing activity detection: " + status.getStatusMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mLastLocation != null) {
            mLatitudeText.setText(Double.toString(mLastLocation.getLatitude()));
            mLongitudeText.setText(Double.toString(mLastLocation.getLongitude()));
            try {
                List<Address> addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                String addressesText = "";
                for(Address adr: addresses){
                    for(int i=0;i<adr.getMaxAddressLineIndex();i++){
                        addressesText+="Address: "+Utilities.getNotNullValue(adr.getAddressLine(i))+"\n";
                    }
                    addressesText+="Admin area "+Utilities.getNotNullValue(adr.getAdminArea())+
                    "\nCountry code "+Utilities.getNotNullValue(adr.getCountryCode())+
                    "\nCountry name "+Utilities.getNotNullValue(adr.getCountryName())+
                    "\nFeature name "+Utilities.getNotNullValue(adr.getFeatureName())+
                    "\nPhone "+Utilities.getNotNullValue(adr.getPhone())+
                    "\nPostal code "+Utilities.getNotNullValue(adr.getPostalCode()) +
                    "\nPremises " + Utilities.getNotNullValue(adr.getPremises()) +
                    "\nLocality " + Utilities.getNotNullValue(adr.getLocality()) +
                    "\nSub admin area " + Utilities.getNotNullValue(adr.getSubAdminArea()) +
                    "\nSub locality " + Utilities.getNotNullValue(adr.getSubLocality());
                }
               mAddressText.setText(addressesText);
            } catch (IOException e) {
                Toast.makeText(getActivity(), "Can't get street address",Toast.LENGTH_LONG);
            }

        } else {
            Toast.makeText(this.getActivity(), "No location detected", Toast.LENGTH_LONG).show();
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    protected void updateDetectedActivitiesList(ArrayList<DetectedActivity> detectedActivities) {
        mAdapter.updateActivities(detectedActivities);
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {
        protected static final String TAG = "activity-detection-response-receiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<DetectedActivity> updatedActivities =
                    intent.getParcelableArrayListExtra(Constants.ACTIVITY_EXTRA);
            updateDetectedActivitiesList(updatedActivities);
        }
    }
}
