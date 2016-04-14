package kis.agh.edu.pl.sensorsview;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CallStateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CallStateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CallStateFragment extends Fragment {

    TextView textOut;
    TelephonyManager telephonyManager;
    PhoneStateListener listener;


    private OnFragmentInteractionListener mListener;

    public CallStateFragment() {
    }

    public static CallStateFragment newInstance(String param1, String param2) {
        CallStateFragment fragment = new CallStateFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_call_state, container, false);
        textOut = (TextView) v.findViewById(R.id.tVCallState);

        telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);

        listener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                String stateString = "N/A";
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        stateString = "Idle";
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        stateString = "Off Hook";
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        stateString = "Ringing";
                        break;
                }
                textOut.append(String.format("\nonCallStateChanged: %s", stateString));
            }
        };

        telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
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
}
