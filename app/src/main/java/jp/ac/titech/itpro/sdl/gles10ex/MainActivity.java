package jp.ac.titech.itpro.sdl.gles10ex;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, SensorEventListener {
    private final static String TAG = "MainActivity";

    private GLSurfaceView glView;
    private SimpleRenderer renderer;
    private SeekBar rotationBarX, rotationBarY, rotationBarZ;

    private SensorManager sensorMgr;
    private Sensor accelerometer;
    private Sensor gyroSensor;

    private Handler handler;

    private float vx, vy, vz;
    private float rate;
    private int accuracy;
    private long prevts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        glView = (GLSurfaceView) findViewById(R.id.glview);

        rotationBarX = (SeekBar) findViewById(R.id.rotation_bar_x);
        rotationBarY = (SeekBar) findViewById(R.id.rotation_bar_y);
        rotationBarZ = (SeekBar) findViewById(R.id.rotation_bar_z);
        rotationBarX.setOnSeekBarChangeListener(this);
        rotationBarY.setOnSeekBarChangeListener(this);
        rotationBarZ.setOnSeekBarChangeListener(this);

        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroSensor = sensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (accelerometer == null) {
            Toast.makeText(this, getString(R.string.toast_no_accel_error),
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (gyroSensor == null) {
            Toast.makeText(this, getString(R.string.toast_no_light_error),
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        handler = new Handler();

        renderer = new SimpleRenderer();
        renderer.addObj(new Cube(0.4f, 1.0f, 0.2f, 1.0f));
        renderer.addObj(new Pyramid(0.5f, 0, 0, 0));
        renderer.addObj(new TriangularPrism(0.6f, -1.0f, 0, -1.0f));
        glView.setRenderer(renderer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        sensorMgr.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        glView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        sensorMgr.unregisterListener(this);
        glView.onPause();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == rotationBarX)
            renderer.setRotationX(progress);
        else if (seekBar == rotationBarY)
            renderer.setRotationY(progress);
        else if (seekBar == rotationBarZ)
            renderer.setRotationZ(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {


    }

    private final int N = 10;
    private float[] ax = new float[N]; private int idx = 0;
    private float[] ay = new float[N]; private int idy = 0;
    private float[] az = new float[N]; private int idz = 0;
    private final static float alpha = 0.8F;

    @Override
    public void onSensorChanged(SensorEvent event) {

        ax[idx] = event.values[0];
        float sx = 0;
        for (int i = 0; i < N; i++){
            sx = sx + ax[i];
        }
        vx = sx / N;
        idx = (idx + 1) % N;

        ay[idy] = event.values[1];
        float sy = 0;
        for (int i = 0; i < N; i++){
            sy = sy + ay[i];
        }
        vy = sy / N;
        idy = (idy + 1) % N;

        az[idz] = event.values[2];
        float sz = 0;
        for (int i = 0; i < N; i++){
            sz = sz + az[i];
        }
        vz = sz / N;
        idz = (idz + 1) % N;

        /*前回値との重み付き平均による安定化*/
        //vx = alpha * vx + (1 - alpha) * event.values[0];
        //vy = alpha * vy + (1 - alpha) * event.values[1];
        //vz = alpha * vz + (1 - alpha) * event.values[2];
        /*
        rate = ((float) (event.timestamp - prevts)) / (1000 * 1000);
        prevts = event.timestamp;
        final float[] ev = event.values;
        Log.i(TAG, "ax=" + ev[0] + ", ay=" + ev[1] + ", az=" + ev[2]);
        */

        //加速度センサーの値に対して回転量が少ないため、10倍している
        renderer.setAccelRotationX(vx*10);
        renderer.setAccelRotationY(vy*10);
        renderer.setAccelRotationZ(vz*10);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(TAG, "onAccuracyChanged: ");
        this.accuracy = accuracy;
    }
}
