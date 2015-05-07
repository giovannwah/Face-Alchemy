package wah.giovann.facealchemy2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class PhotoHandler implements PictureCallback {
	private final Context context;
	private final Activity activity;
//	protected File image1;
//	protected File image2;
	protected Bitmap image1;
	protected Bitmap image2;
	protected String image1URL;
	protected String image2URL;
	protected FacialLandmark image1Landmarks;
	protected FacialLandmark image2Landmarks;
	protected boolean firstPic;
	protected boolean secondPic;
  
  public PhotoHandler(Context context, Activity activity) {
    this.context = context;
    this.activity = activity;
    firstPic = false;
    secondPic = false;
    image1URL = "";
    image2URL = "";
    image1 = null;
    image2 = null;
    image1Landmarks = null;
    image2Landmarks = null;
  }

  @Override
  public void onPictureTaken(byte[] data, Camera camera){
	  Matrix m = new Matrix();
	  Camera.CameraInfo info = new Camera.CameraInfo();
      Camera.getCameraInfo(MainCamera.currentCam, info);
      int rotation = this.activity.getWindowManager().getDefaultDisplay().getRotation();
      int degrees = 0;
      switch (rotation) {
          case Surface.ROTATION_0: degrees = 0; break; //Natural orientation
              case Surface.ROTATION_90: degrees = 90; break; //Landscape left
              case Surface.ROTATION_180: degrees = 180; break;//Upside down
              case Surface.ROTATION_270: degrees = 270; break;//Landscape right
          }
      int rotate = (info.orientation - degrees + 360) % 360;
	  m.postRotate(rotate);
	  //Scale Bitmaps
	  //Scale to half size...
	  Bitmap p = scaleBitmap(BitmapFactory.decodeByteArray(data, 0, data.length), 0.50);
	  //Rotate Bitmaps
	  Bitmap picture = Bitmap.createBitmap(p , 0, 0, p.getWidth(), p.getHeight(), m, true);
	  //Save Bitmaps
	  try{
		  if (!firstPic){
			  image1URL = saveToFile(picture, "temp_", "Face Alchemy");
			  Log.d(MainCamera.TAG, "Image 1: "+image1URL);
	    	  image1 = picture;
	    	  image1Landmarks = new FacialLandmark(image1);
	    	  Toast.makeText(context, "Image 1 saved", Toast.LENGTH_LONG).show();
	    	  firstPic = true;
	    	  Log.d(MainCamera.TAG, "Image 1 saved.");
	    	  camera.startPreview();
	      }
	      else if (firstPic && !secondPic){
	    	  image2URL = saveToFile(picture, "temp_", "Face Alchemy");
			  Log.d(MainCamera.TAG, "Image 2: "+image2URL);
	    	  image2 = picture;
	    	  image2Landmarks = new FacialLandmark(image2);
	    	  Toast.makeText(context, "Image 2 saved", Toast.LENGTH_LONG).show();
	    	  secondPic = true;
	    	  MainCamera.ready = true;
	    	  Log.d(MainCamera.TAG, "Image 2 saved.");
	    	  camera.startPreview();
	      }
	  } catch (Exception e){
		  Log.d(MainCamera.TAG, "onPictureTaken Error: "+e.getMessage());
	  }
  }
  
  /**
   * Returns the public external storage directory for this app's pictures.
   * @return
   */
  private static File getDir(String name) {
    File sdDir = Environment
      .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    return new File(sdDir, name);
  }
  
  /**
   * Saves the Bitmap image to the File system with the given String pref as a prefix
   * @param img
   */
  public static String saveToFile(Bitmap img, String pref, String folderName){
	  File dir = getDir(folderName);
	  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
	  String date = dateFormat.format(new Date());
	  String photoFile = pref + date + ".png";

	  String filename = dir.getPath() + File.separator + photoFile;
	  
	  FileOutputStream out = null;
	  try {
	      out = new FileOutputStream(filename);
	      img.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
	      // PNG is a lossless format, the compression factor (100) is ignored
	  } catch (Exception e) {
	      e.printStackTrace();
	  } finally {
	      try {
	          if (out != null) {
	              out.close();
	          }
	      } catch (IOException e) {
	          e.printStackTrace();
	      }
	  }
	  return filename;
  }
  
  /**
	 * Returns the Bitmap representation of the file.
	 * @param f
	 * @return
	 */
	public static Bitmap getBitmap(File f){
		Bitmap img = null;
		InputStream i1 = null;
		try {
			i1 = new FileInputStream(f);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			img = BitmapFactory.decodeStream(i1);
		} catch (FileNotFoundException e) {
			Log.d(MainCamera.TAG, "Error creating bitmap: "+e.getMessage());
		}
		finally{
			try {
				i1.close();
				i1 = null;
			} catch (IOException e) {
				Log.d(MainCamera.TAG, "Error closing InputStream: "+e.getMessage());
			}
		}
		return img;
	}
	
	/**
	 * Resizes the bitmap based on the width and height arguments.
	 * @param b
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap scaleBitmap(Bitmap b, int reqWidth, int reqHeight){
		return Bitmap.createScaledBitmap(b, reqWidth, reqHeight, true);
	}
	
	/**
	 * Resizes the bitmap based on the scale argument. Scale can be anywhere from 0 to 1.
	 * @param b
	 * @param scale
	 * @return
	 */
	public static Bitmap scaleBitmap(Bitmap b, double scale){
		if (scale > 1 || scale < 0){
			Log.e(MainCamera.TAG, "scaleBitmap: scale must be between 0 - 1");
		}
		int width = (int) Math.round(b.getWidth()*scale);
		int height = (int) Math.round(b.getHeight()*scale);
		return scaleBitmap(b, width, height);
	}
} 