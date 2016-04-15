package kis.agh.edu.pl.sensorsview;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class BluetoothFragment extends Fragment {
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVERABLE_BT = 0;
    private OnFragmentInteractionListener mListener;

    private TextView tVInfo;
    private TextView tVState;

    public BluetoothFragment() {
    }

    public static BluetoothFragment newInstance() {
        BluetoothFragment fragment = new BluetoothFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        final TextView out=(TextView) v.findViewById(R.id.tVBTInfo);
        final Button btnEnable = (Button) v.findViewById(R.id.btnStart);
        final Button btnDiscover = (Button) v.findViewById(R.id.btnDiscover);
        final Button btnDisable = (Button) v.findViewById(R.id.btnStop);
        tVInfo = (TextView) v.findViewById(R.id.tVInfoValue);
        tVState = (TextView) v.findViewById(R.id.tVStateValue);
        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            out.append("device not supported");
        }else {
            String bltInfo="";
            bltInfo+="Address: "+mBluetoothAdapter.getAddress()+"\n"+
                    "Name: "+mBluetoothAdapter.getName();
            tVInfo.setText(bltInfo);
            setTVStateText(getBluetoothStateString(mBluetoothAdapter.getState()));
            btnEnable.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                }
            });
            btnDiscover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (!mBluetoothAdapter.isDiscovering()) {
                        Context context = getActivity().getApplicationContext();
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        startActivityForResult(enableBtIntent, REQUEST_DISCOVERABLE_BT);

                    }
                }
            });
            btnDisable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    mBluetoothAdapter.disable();
                    Context context = getActivity().getApplicationContext();
                }
            });
        }
        return v;
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
    public void setTVStateText(String text){
        tVState.setText(text);
    }

    public String getBluetoothStateString(int bluetoothState){
        String state = "";
        switch (bluetoothState) {
            case BluetoothAdapter.STATE_OFF:
                state="Bluetooth off";
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                state="Turning Bluetooth off...";
                break;
            case BluetoothAdapter.STATE_ON:
                state="Bluetooth on";
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                state="Turning Bluetooth on...";
                break;
        }
        return state;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                setTVStateText(getBluetoothStateString(state));
            }
        }
    };
}
