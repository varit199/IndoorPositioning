package com.indooratlas.example;

import com.indooratlas.android.*;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.method.Touch;
import android.util.Log;
import android.widget.*;
import android.widget.ImageView;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.view.View;



/*
 * IndoorAtlas API example
 */
public class IndoorAtlasExample extends Activity implements IndoorAtlasListener {

	private static final String TAG = "IndoorAtlasExample";

	private TextView textView;
	private Handler handler = new Handler();
	private IndoorAtlas indoorAtlas;

	private boolean positioningOngoing = false;

    FrameLayout panel;
    ImageView marker;
    ImageView map;

  	// Get these from MyAtlas at www.indooratlas.com
    private final String apiKey = "bff116af-c16f-4111-aba7-19d180df4dcf";
    private final String secretKey = "FUik9(EQRgHUf!jcYckHe2Ec&Gl&cMfA4HoGvfgnmE6qPDUMhRc)J!UTH(li%A%z61qoCO5CwbWbSvPBJiv1FqDBhKAcTnbsmqPHKxe!Bv&z0(SbUXuG5YKT2t)4BRFq";
	
	// Get these from the Floor Plans tool at www.indooratlas.com
    private final String buildingId = "25b49835-ae7d-481a-98ac-946c5a226f34";
    private final String levelId = "27238099-af01-443e-9765-4c3e7e970144";
    private final String floorPlanId = "c30b6696-0d88-4c2a-ba61-0edfb09e6149";

	private long lastPositionTimestamp = 0;

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		textView = (TextView) findViewById(R.id.textView1);

        //add map
        panel = (FrameLayout)findViewById(R.id.map_panel);
        map = new ImageView(getApplicationContext());
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.map);
        LinearLayout.LayoutParams panelParam = (LinearLayout.LayoutParams)panel.getLayoutParams();
        int width = Math.round((float) panelParam.height / (float) bm.getHeight() * (float) bm.getWidth());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(2424,1596);//(width,panelParam.height);
        params.leftMargin = 0;
        params.topMargin = 0;
        map.setLayoutParams(params);
        map.setImageBitmap(bm);
        map.setScaleType(ImageView.ScaleType.FIT_XY);

        panel.addView(map);

        //add marker
        marker = new ImageView(getApplicationContext());
        marker.setImageResource(R.drawable.dot);
        FrameLayout.LayoutParams marker_params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        marker_params.leftMargin = 0;
        marker_params.topMargin = 0;
        marker.setLayoutParams(marker_params);
        panel.addView(marker);

        try {
			
			// Get handle to the IndoorAtlas API
			// Note that this method should be called as early as possible in the application, because
			// the calibration process starts immediately at API creation and is thus likely to finish
			// by the time positioning is started by the user or the application.
			
			// Throws exception when the cloud service cannot be reached
			// Get your Apikey and SecreSit key from IndoorAtlas My Account

			indoorAtlas = IndoorAtlasFactory.createIndoorAtlas(
					this.getApplicationContext(), 
					this,
					apiKey,
					secretKey);

			Log.d(TAG, "onCreate created IndoorAtlas");
			
			
			
		} catch (IndoorAtlasException ex) {
			showMessageOnUI("Failed to connect to IndoorAtlas. Check your credentials.");
			Toast.makeText(this, "Failed to connect to IndoorAtlas. Check your credentials.", Toast.LENGTH_LONG).show();
			Log.e(TAG, "Failed to connect to IndoorAtlas. Check your credentials.");
			
			// Stop all API processes
			if (indoorAtlas != null) indoorAtlas.tearDown();
			
			// stop application
			this.finish();
		}
		

		Log.d(TAG, "onCreate done.");


	}
	
	@Override
	protected void onResume() 
	{
		Log.d(TAG, "onResume() -State- : calibrated = "+indoorAtlas.isCalibrationReady());
		super.onResume();
		
		// After installation of the application, IndoorAtlas API does not have calibration data, and an exception will be thrown
		// if startPositioning() is called at this state. Thus, at first use, the application should guide the user to perform 
		// calibration by moving the device as instructed in the IndoorAtlas Mobile application. Positioning can be started only 
		// after onCalibrationReady() has been called. The calibration will be stored by the API, and on consecutive starts 
		// onCalibrationReady() call immediately follows the call on createIndoorAtlas().
		if(indoorAtlas.isCalibrationReady() == false) {
			// Prompts user to perform calibration motion
			showMessageOnUI("onResume(): Calibrating... Rock your phone gently until onCalibrationReady() is called");
		}
		
		else if(positioningOngoing == false) {			
			showMessageOnUI("onResume(): Starting positioning.");
			
			try {
				positioningOngoing = true;
				
				// Throws an exception if no calibration is done
				indoorAtlas.startPositioning(buildingId, levelId, floorPlanId);
				
			} catch (IndoorAtlasException e) {
				positioningOngoing = false;
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void onStop() {

		Log.d(TAG, "onStop() -State- : calibrated = "+indoorAtlas.isCalibrationReady());

		try {
			//showMessageOnUI("onStop(): Stopping positioning.");

			// Stop positioning when not needed
			indoorAtlas.stopPositioning();
			
			// Stop all API processes
			indoorAtlas.tearDown();

		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onStop();
	}

	@Override
	protected void onPause() 
	{
		Log.d(TAG, "onPause() -State- : calibrated = "+indoorAtlas.isCalibrationReady());
		
		//showMessageOnUI("onPause(): Stopping positioning.");

		// Stop positioning when not needed
		//indoorAtlas.stopPositioning();

		super.onPause();
	}
	
	@Override
	protected void onRestart()
	{
		Log.d(TAG, "onRestart() -State- : calibrated = "+indoorAtlas.isCalibrationReady());

		super.onRestart();
		
		showMessageOnUI("onRestart().");
	}
	
	
	
	// Called on every new location estimate. 
	// Note that when device is not moving, frequency of callbacks may decrease.
	public void onServiceUpdate(ServiceState state) {

		Log.d(TAG, "onServiceUpdate()");
		
		long curTime = SystemClock.elapsedRealtime();
		long diff = curTime - lastPositionTimestamp;
		lastPositionTimestamp = curTime;

        FrameLayout.LayoutParams map_params2 = (FrameLayout.LayoutParams) map.getLayoutParams();
        FrameLayout.LayoutParams marker_params2 = (FrameLayout.LayoutParams) marker.getLayoutParams();//new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        marker_params2.leftMargin = map_params2.leftMargin+state.getImagePoint().getI();
        marker_params2.topMargin = map_params2.topMargin+state.getImagePoint().getJ();


        //marker.setLayoutParams(marker_params2);

		// Use location estimate
        final String s = new String(
                        "Calibration ready : "+ indoorAtlas.isCalibrationReady() + "\n"
                        + "Communication latency : "+ indoorAtlas.getCommunicationLatency() + "s\n\n"
                        + "Lat : "+ state.getGeoPoint().getLatitude() + "\n"
                        + "Lon : "+ state.getGeoPoint().getLongitude() + "\n\n"
                        + "X [meter] : "+ state.getMetricPoint().getX() + "\n"
                        + "Y [meter] : "+ state.getMetricPoint().getY() + "\n\n"
                        + "In-App MapX : "+ map_params2.leftMargin + "\n"
                        + "In-App MapY : "+ map_params2.topMargin + "\n\n"
                        + "I [pixel] : "+ state.getImagePoint().getI() + "\n"
                        + "J [pixel] : "+ state.getImagePoint().getJ() + "\n\n"
                        + "In-App X : "+ marker_params2.leftMargin + "\n"
                        + "In-App Y : "+ marker_params2.topMargin) + "\n\n"
                + "Heading [deg] : "+ state.getHeadingDegrees() + "\n\n"
                + "Uncertainty [meter] : "+ state.getUncertainty();

                showMessageOnUI("Service is working. Enjoy Indoor Positioning :)\n\n" +
                        "Your current position is at "+state.getImagePoint().getI()+" : "+state.getImagePoint().getJ()+" from the map \n\n" +
                        "That is "+marker_params2.leftMargin+" : "+marker_params2.topMargin+" on your screen.");


               //ImageView marker = panel.getChildAt(1);
               //marker.setposition(state.getImagePoint().getI(),state.getImagePoint().getJ());



        //map = (ImageView) findViewById(R.id.map);
        //ImageView image = (ImageView) findViewById(R.id.map);

	}

	// Communication with IndoorAtlas has failed.
	public void onServiceFailure(int errorCode, String reason) {
		Log.d(TAG, "onServiceFailure()");

		switch (errorCode)
		{

		case ErrorCodes.NO_NETWORK:
			showMessageOnUI("onServiceFailure(): No network connections.");
			break;
			
		case ErrorCodes.SENSOR_ERROR:
			showMessageOnUI("onServiceFailure(): Sensor error.");
			break;
			
		case ErrorCodes.LOW_SAMPLING_RATE:
			showMessageOnUI("onServiceFailure(): Too low sampling rate in sensor(s) for reliable positioning.");
			break;
	
		case ErrorCodes.NO_CONNECTION_TO_POSITIONING:
			showMessageOnUI("onServiceFailure(): Connection to positioning service could not be established.");
			break;
			
		case ErrorCodes.INTERNAL_POSITIONING_SERVICE_ERROR:
			showMessageOnUI("onServiceFailure(): Internal positioning service error.");
			break;

		case ErrorCodes.VERSION_MISMATCH:
			showMessageOnUI("onServiceFailure(): API version is not supported.");
			break;

		case ErrorCodes.POSITIONING_SESSION_TIMEOUT:
			showMessageOnUI("onServiceFailure(): Session has timed out.");
			break;
			
		case ErrorCodes.POSITIONING_DENIED:
			showMessageOnUI("onServiceFailure(): Positioning permission denied.");
			break;

		case ErrorCodes.NO_POSITIONING_TIME_LEFT:
			showMessageOnUI("onServiceFailure(): No positioning time left.");
			break;
			
		case ErrorCodes.MAP_NOT_FOUND:
			showMessageOnUI("onServiceFailure(): Positioning service could not retrieve map.");
			break;
			
		case ErrorCodes.NOT_SUPPORTED:
			showMessageOnUI("onServiceFailure(): Selected motion mode is not supported by positioning service.");
			break;

		default:
			showMessageOnUI("onServiceFailure(): Unexpected error: " + reason);
			break;
			
		}
	}

	// Initializing location service
	public void onServiceInitializing() {
		Log.d(TAG, "onServiceInitializing()");
		showMessageOnUI("onServiceInitializing()");
	}

	// Initialization completed
	public void onServiceInitialized() {
		Log.d(TAG, "onServiceInitialized()");
		showMessageOnUI("onServiceInitialized(): Walk to get location fix");
	}

	// Location service initialization failed
	public void onInitializationFailed(String reason) {
		Log.d(TAG, "onInitializationFailed()");
		showMessageOnUI("onInitializationFailed(): "+ reason);
		positioningOngoing = false;
	}

	// Positioning was stopped
	public void onServiceStopped() {
		Log.d(TAG, "onServiceStopped()");
		showMessageOnUI("onServiceStopped(): IndoorAtlas Positioning Service is stopped.");
		positioningOngoing = false;
	}

	// Calibration failed. Called when device is not moved enough during calibration, for example.
	public void onCalibrationFailed(String reason) {
		Log.d(TAG, "onCalibrationFailed(), reason : "+reason);
		
		// Show unrecoverable error to the user. Typically caused by sensor errors in device.
		showMessageOnUI("onCalibrationFailed()");
	}


	public void onCalibrationStatus(CalibrationState calibrationState) {

		Log.d(TAG, "onCalibrationStatus(): calibration event : "+calibrationState.getCalibrationEvent()
												+", percentage : "+calibrationState.getPercentage()
												+", time "+System.currentTimeMillis());

		if(positioningOngoing == false) {
			showMessageOnUI("onCalibrationStatus(): \ncalibration event : "+calibrationState.getCalibrationEvent()
												+"\npercentage : "+calibrationState.getPercentage()
												+", time "+System.currentTimeMillis());	
		}
	}

	public void onNetworkChangeComplete(boolean success) {
		Log.d(TAG, "onNetworkChangeComplete(), success = "+success);

		showMessageOnUI("onNetworkChangeComplete() success = "+success);
	}

	
	// Calibration ready, positioning can be started
	// This is called once after call to IndoorAtlasFactory.createIndoorAtlas()
	public void onCalibrationReady() {
		Log.d(TAG, "onCalibrationReady(), positioningOngoing : "+positioningOngoing);

		showMessageOnUI("onCalibrationReady()");
		
		// Use Floor Plans tool to get IDs for building, level and floor plan 
		
		if(positioningOngoing == false) {
			try {
				indoorAtlas.startPositioning(buildingId, levelId,
						floorPlanId);
				positioningOngoing = true;
			} catch (IndoorAtlasException e) {
				e.printStackTrace();
			}
		}
	}

	
	public void onCalibrationInvalid() {
		Log.d(TAG, "onCalibrationInvalid()");
		
		showMessageOnUI("onCalibrationInvalid()");		
	}
	
	
	// Helper method
	private void showMessageOnUI(final String message) {
		handler.post(new Runnable() {
			public void run() {
				textView.setText(message);
			}
		});
	}

	


}
