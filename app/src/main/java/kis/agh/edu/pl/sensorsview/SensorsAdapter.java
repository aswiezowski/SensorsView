package kis.agh.edu.pl.sensorsview;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by swiezy on 30.03.16.
 */
public class SensorsAdapter extends android.support.v7.widget.RecyclerView.Adapter<SensorsAdapter.CardViewHolder> implements SensorEventListener{

    private int sensorCount = 0;
    private List<Sensor> allSensors;
    private Map<Sensor,float[]> sensorsToValue;
    SensorManager sensorManager;

    public SensorsAdapter(SensorManager sensorManager){
        this.sensorManager=sensorManager;
        allSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        sensorsToValue = new HashMap<>();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_generic_sensor, parent, false);
        CardViewHolder vh = new CardViewHolder(cardView);
        return vh;
    }

    public void onBindViewHolder(CardViewHolder holder, int position) {
        Sensor sensor = allSensors.get(position);
        holder.tvTitle.setText(sensor.getName());
        holder.position = position;
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        float values[]=sensorsToValue.get(sensor);
        if(values!=null){
            int len = (values.length>3)?3:values.length;
            for (int i = 0; i < len; i++) {
                holder.tVVal[i].setText(Float.toString(values[i]));
            }
            for (int i = len; i < 4; i++) {
                holder.tVVal[i].setText("");
            }
        }
        else{
            for (int i = 0; i < 4; i++) {
                holder.tVVal[i].setText("");
            }
        }
        String labels[] = getLabels(sensor.getType(), holder.itemView.getResources());
        if(labels != null) {
            for (int i = 0; i < labels.length; i++) {
                holder.tVLbl[i].setText(labels[i]);
            }
            for (int i = labels.length; i < 4; i++) {
                holder.tVLbl[i].setText("");
            }
        }else{
            for (int i = 0; i < 4; i++) {
                holder.tVLbl[i].setText("");
            }
        }

    }

    public String[] getLabels(int sensorType, Resources resources){
        String[] labels=null;
        if(sensorType==Sensor.TYPE_ACCELEROMETER){
            labels=resources.getStringArray(R.array.val_accelerometr);
        } else if(sensorType==Sensor.TYPE_AMBIENT_TEMPERATURE){
            labels=resources.getStringArray(R.array.val_ambient_temperature);
        }else if(sensorType==Sensor.TYPE_GAME_ROTATION_VECTOR){
            labels=resources.getStringArray(R.array.val_rotation_vector);
        }else if(sensorType==Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR){
            labels=resources.getStringArray(R.array.val_rotation_vector);
        }else if(sensorType==Sensor.TYPE_GRAVITY){
            labels=resources.getStringArray(R.array.val_gravity);
        }else if(sensorType==Sensor.TYPE_GYROSCOPE){
            labels=resources.getStringArray(R.array.val_gyroscope);
        }else if(sensorType==Sensor.TYPE_GYROSCOPE_UNCALIBRATED){
            labels=resources.getStringArray(R.array.val_gyroscope);
        }else if(sensorType==Sensor.TYPE_HEART_RATE){
            labels=resources.getStringArray(R.array.val_heartrate);
        }else if(sensorType==Sensor.TYPE_MAGNETIC_FIELD){
            labels=resources.getStringArray(R.array.val_magnetic_field);
        }else if(sensorType==Sensor.TYPE_LINEAR_ACCELERATION){
            labels=resources.getStringArray(R.array.val_linear_acceleration);
        }else if(sensorType==Sensor.TYPE_LIGHT){
            labels=resources.getStringArray(R.array.val_light);
        }else if(sensorType==Sensor.TYPE_GAME_ROTATION_VECTOR){
            labels=resources.getStringArray(R.array.val_rotation_vector);
        }else if(sensorType==Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED){
            labels=resources.getStringArray(R.array.val_magnetic_field);
        }else if(sensorType==Sensor.TYPE_PRESSURE){
            labels=resources.getStringArray(R.array.val_pressure);
        }else if(sensorType==Sensor.TYPE_PROXIMITY){
            labels=resources.getStringArray(R.array.val_proximity);
        }
        return labels;
    }

    @Override
    public int getItemCount() {
        if(allSensors==null)
            return 0;
        return allSensors.size();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        sensorsToValue.remove(event.sensor);
        sensorsToValue.put(event.sensor, event.values);
        notifyItemChanged(allSensors.indexOf(event.sensor));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        public TextView[] tVVal = new TextView[4];
        public TextView[] tVLbl = new TextView[4];
        public int position;

        public CardViewHolder(View cardView) {
            super(cardView);
            tvTitle = (TextView) cardView.findViewById(R.id.tVTitle);
            tVVal[0] = (TextView) cardView.findViewById(R.id.tVVal1);
            tVVal[1] = (TextView) cardView.findViewById(R.id.tVVal2);
            tVVal[2] = (TextView) cardView.findViewById(R.id.tVVal3);
            tVVal[3] = (TextView) cardView.findViewById(R.id.tVVal4);
            tVLbl[0] = (TextView) cardView.findViewById(R.id.tVLbl1);
            tVLbl[1] = (TextView) cardView.findViewById(R.id.tVLbl2);
            tVLbl[2] = (TextView) cardView.findViewById(R.id.tVLbl3);
            tVLbl[3] = (TextView) cardView.findViewById(R.id.tVLbl4);
        }

    }

}
