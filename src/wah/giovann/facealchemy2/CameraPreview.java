package wah.giovann.facealchemy2;

import java.io.IOException;

import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;

@SuppressWarnings("deprecation")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder mHolder;
	private Camera mCamera;
	
	public CameraPreview(Context context, Camera camera) {
		super(context);
		// TODO Auto-generated constructor stub
		mCamera = camera;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		   // The Surface has been created, now tell the camera where to draw the preview.
		Log.d(MainCamera.TAG, "surfaceCreated has been called...");
		if (mCamera != null){
	        try {
	            mCamera.setPreviewDisplay(holder);
	            //mCamera.setDisplayOrientation(90);
	            MainCamera.setCameraDisplayOrientation((Activity)this.getContext(), MainCamera.currentCam, mCamera);
	            mCamera.startPreview();
	        } catch (IOException e) {
	            Log.d(MainCamera.TAG, "Error setting camera preview: " + e.getMessage());
	        }
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.d(MainCamera.TAG, "surfaceChanged has been called...");
        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
        //    mCamera.stopPreview();
        } catch (Exception e){
        	Log.e(MainCamera.TAG, "surfaceChanged ERROR: "+e.getMessage());
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e){
            Log.d(MainCamera.TAG, "Error starting camera preview: " + e.getMessage());
        }
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		/*
		if (mCamera != null){
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
		*/
	}
}
