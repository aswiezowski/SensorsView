package kis.agh.edu.pl.sensorsview;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class FragmentsActivity extends AppCompatActivity implements BluetoothFragment.OnFragmentInteractionListener, CallStateFragment.OnFragmentInteractionListener, CameraFragment.OnFragmentInteractionListener,TelephonyManagerFragment.OnFragmentInteractionListener, PlayLocationFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
