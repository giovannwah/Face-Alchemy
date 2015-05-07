package wah.giovann.facealchemy2;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MainCamera extends Activity {
	protected static final int BACK_CAM = 0;
	protected static final int FRONT_CAM = 1;
	protected static final String TAG = "Face Alchemy";
    protected static int currentCam;
    protected static boolean ready;
	
	private Camera mCamera;
    private CameraPreview mPreview;
    private PhotoHandler photoHandler;
    private FrameLayout preview;
    private ImageWarper warper;
    
    public static Resources resources;
    public static String packageName;
    public Context context;
    public static AssetManager am;
    
    public boolean debug;
    public boolean debugStarted;
    public boolean morphingStarted;
        
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate has been called...");
		
        setContentView(R.layout.activity_main_camera);
        
		resources = getResources();
		packageName = getPackageName();
		am = getAssets();
        context = this;
        debug = false;
        debugStarted = false;
        morphingStarted = false;
        currentCam = FRONT_CAM;
        ready = false;
        
        // Create an instance of Camera
        mCamera = getCameraInstance(currentCam);
		mCamera.getParameters().setRotation(90);
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        photoHandler = new PhotoHandler(getApplicationContext(), this);
        
        // Add a listener to the Capture button
        final Button captureButton = (Button) findViewById(R.id.button_capture);
        if (debug) captureButton.setText("Begin Debugging");
        captureButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get an image from the camera
                	if (!debug){
	                	if (!ready){
		                	Log.d(TAG, "BUTTON CLICKED!!");
		                    mCamera.takePicture(null, null, photoHandler);
		                    captureButton.setText("Take Second Photo");
		                    if (photoHandler.firstPic == true){
		                    	captureButton.setText("MORPH");
		                    }
	                	}
	                	else{
	                			captureButton.setText("MORPHING...PLEASE WAIT...");
	                			//perform image warping and cross dissolving
	                			int x;
	                			while(	photoHandler.image1Landmarks == null ||
	                					photoHandler.image2Landmarks == null ||
	                					photoHandler.image1Landmarks.getLines() == null ||
	                					photoHandler.image2Landmarks.getLines() == null){x = 1;}
	                				
	                			warper = new ImageWarper(photoHandler.image1Landmarks, photoHandler.image2Landmarks);
	                			Bitmap[] warpedImage = warper.getWarpedBitmaps();
	                 			try {
	                 				PhotoHandler.saveToFile(CrossDissolve.dissolve(warpedImage[0], warpedImage[1], 0.5), "composite_", "Face Alchemy");
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
	                			Toast.makeText(context, "COMPOSITE SAVED", Toast.LENGTH_LONG).show();
	                			captureButton.setText("DONE.");
	                	}
	                }
                	else{ //DEBUG!!!
                		if (debugStarted == false){
                			debugStarted = true;
	                		captureButton.setText("Running Test...");
	                		Debug.fullAlgorithmTest();
	                		captureButton.setText("Test Complete.");
	             		    Toast.makeText(context, "DEBUGGING COMPLETE. ALL IMAGES SAVED", Toast.LENGTH_LONG).show();
                		}
                		else{
                			Toast.makeText(context, "Already Debugging...", Toast.LENGTH_LONG).show();
                		}
                	}
                }
            }
        );
    }
    
    public void setButtonText2(Button b){
    	b.setText(R.string.capture2);
    }
    public void setButtonText3(Button b){
    	b.setText(R.string.debug);
    }
    public void setButtonText4(Button b){
    	b.setText(R.string.morph);
    }
    protected static void setCameraDisplayOrientation(Activity a, int camID, Camera c) {
	     android.hardware.Camera.CameraInfo info =
	             new android.hardware.Camera.CameraInfo();
	     android.hardware.Camera.getCameraInfo(camID, info);
	     int rotation = a.getWindowManager().getDefaultDisplay().getRotation();
	     int degrees = 0;
	     switch (rotation) {
	         case Surface.ROTATION_0: degrees = 0; break;
	         case Surface.ROTATION_90: degrees = 90; break;
	         case Surface.ROTATION_180: degrees = 180; break;
	         case Surface.ROTATION_270: degrees = 270; break;
	     }

	     int result;
	     if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	         result = (info.orientation + degrees) % 360;
	         result = (360 - result) % 360;  // compensate the mirror
	     } else {  // back-facing
	         result = (info.orientation - degrees + 360) % 360;
	     }
	     c.setDisplayOrientation(result);
 }
    
    @Override
    protected void onPause() {
        super.onPause();
		Log.d(TAG, "onPause has been called...");
		if (mCamera != null){
	        try {
	        	mCamera.setPreviewCallback(null);
	        	mPreview.getHolder().removeCallback(mPreview);
	        	mCamera.release();
	        	mCamera = null;
	        }
	        catch (Exception e){
	        	Log.e(TAG, "ERROR onPause: "+e.getMessage());
	        }
		}
		Log.d(TAG, "onPause has completed...");
    }
    
    @Override
    protected void onStop() {
        super.onStop();
		Log.d(TAG, "onStop has been called...");
		if (mCamera != null){
	        try {
	        	mCamera.setPreviewCallback(null);
	        	mPreview.getHolder().removeCallback(mPreview);
	        	mCamera.release();
	        	mCamera = null;
	        }
	        catch (Exception e){
	        	Log.e(TAG, "ERROR onStop: "+e.getMessage());
	        }
		}
		Log.d(TAG, "onStop has completed...");
    }
    
    @Override
    protected void onResume(){
    	super.onResume();
		Log.d(TAG, "onResume has been called...");
		if (mCamera == null){
	    	try {
	    		mCamera = getCameraInstance(currentCam);
	    		mCamera.getParameters().setRotation(90);
	    		mCamera.setPreviewCallback(null);
	            mPreview = new CameraPreview(this, mCamera);
	            preview.addView(mPreview);
	            mCamera.startPreview();
	    	}
	    	catch (Exception e){
	    		Log.e(TAG, "ERROR onResume: "+e.getMessage());
	    	}
		}
    	Log.d(TAG, "camera = "+mCamera);
		Log.d(TAG, "onResume has completed...");
    }
    
    private static int getCamera(int cam){
    	if (cam == 1){
		  int ret = findFrontFacingCamera();
		  if (ret < 0) ret = findBackFacingCamera();
		  return ret;
    	}
    	else return findBackFacingCamera();
	}

	  private static int findFrontFacingCamera() {
	    int cameraId = -1;
	    // Search for the front facing camera
	    int numberOfCameras = Camera.getNumberOfCameras();
	    for (int i = 0; i < numberOfCameras; i++) {
	      CameraInfo info = new CameraInfo();
	      Camera.getCameraInfo(i, info);
	      if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
	        Log.d(TAG, "Camera found");
	        cameraId = i;
	        break;
	      }
	    }
	    return cameraId;
	  }

	  private static int findBackFacingCamera() {
		    int cameraId = -1;
		    // Search for the front facing camera
		    int numberOfCameras = Camera.getNumberOfCameras();
		    for (int i = 0; i < numberOfCameras; i++) {
		      CameraInfo info = new CameraInfo();
		      Camera.getCameraInfo(i, info);
		      if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
		        Log.d(TAG, "Camera found");
		        cameraId = i;
		        break;
		      }
		    }
		    return cameraId;
	  }
	  
	  public Camera getCameraInstance(int cam){
		    Camera c = null;
		    try {
		        c = Camera.open(getCamera(cam)); // attempt to get a Camera instance
		        
		        //Make sure camera image rotation is correct...
		        Camera.CameraInfo info = new Camera.CameraInfo();
		        Camera.getCameraInfo(cam, info);
		        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
		        int degrees = 0;
		        switch (rotation) {
		            case Surface.ROTATION_0: degrees = 0; break; //Natural orientation
		                case Surface.ROTATION_90: degrees = 90; break; //Landscape left
		                case Surface.ROTATION_180: degrees = 180; break;//Upside down
		                case Surface.ROTATION_270: degrees = 270; break;//Landscape right
		            }
		        int rotate = (info.orientation - degrees + 360) % 360;

		        //STEP #2: Set the 'rotation' parameter
		        Camera.Parameters params = c.getParameters();
		        params.setRotation(rotate); 
		        c.setParameters(params);
		    }
		    catch (Exception e){
		    	Log.e(MainCamera.TAG, "getCameraInstance ERROR: "+e.getMessage());
		    }
		    return c; // returns null if camera is unavailable
	  }
}