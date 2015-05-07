/**
 * Debug.java: 
 * Tools for debugging landmarking and morphing algorithms.
 */
package wah.giovann.facealchemy2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class Debug {
	static int red = 255;
	static int green = 105;
	static int blue = 180;
	
	static String tag = "DEBUGGING";
	/**
	 * Tests EVERYTHING and saves results of each step on the device.
	 */
	public static void fullAlgorithmTest(){
		Log.d(tag, "BEGINNING FULL ALGORITHM TEST");
		String dir = "Face Alchemy";

		//Test images, Katy Perry and Gina Gershon <3 <3 <3
		Bitmap katy_perry = null;
		Bitmap gina_gershon = null;
		InputStream open1 = null;
		InputStream open2 = null;
		try {
            open1 = MainCamera.am.open("images/perry.png");
            katy_perry = BitmapFactory.decodeStream(open1);
            open2 = MainCamera.am.open("images/gershon.png");
            gina_gershon = BitmapFactory.decodeStream(open2);
            
        } catch (IOException e) {
            e.printStackTrace();
        } 
		finally{
			try {
				open1.close();
	            open2.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//Save original images
		Log.d(tag, "ACQUIRING IMAGES...");
		PhotoHandler.saveToFile(katy_perry, "perry1_", dir);
		PhotoHandler.saveToFile(gina_gershon,"gina1_", dir);
		Bitmap tkp = katy_perry.copy(katy_perry.getConfig(), true);
		Bitmap tgg = gina_gershon.copy(gina_gershon.getConfig(), true);
		Log.d(MainCamera.TAG, "ORIGINAL IMAGES SAVED. CREATING FACIAL LANDMARK OBJECTS...");
		
		FacialLandmark kp = new FacialLandmark(katy_perry.copy(katy_perry.getConfig(), true));
		FacialLandmark gg = new FacialLandmark(gina_gershon.copy(gina_gershon.getConfig(), true));
		Log.d(tag, "LANDMARKS CREATED. SAVING PREVIEWS");
		int x = 1;
		while (kp.getPoints() == null || kp.getLines() == null){x = 2;} //wait for thread to finish
		green = 255;
		red = 255;
		blue = 0;
		previewPoints(kp, "perry2_", dir);
		previewLines(kp, "perry3_", dir);

		while (gg.getPoints() == null || gg.getLines() == null){x = 2;} //wait for thread to finish
		green = 0;
		blue = 255;
		red = 0;
		previewPoints(gg, "gina2_", dir);
		previewLines(gg, "gina3_", dir);
		
		Log.d(tag, "CREATING INTERMEDIATE LINES.");
		FacialLandmark tempkp = kp.copy();
		FacialLandmark tempgg = gg.copy();
		Point[] tempp = ImageWarper.interPoints(tempkp, tempgg);
		ArrayList<Line> templ = ImageWarper.interLines(tempkp.getLines(), tempgg.getLines());
		tempkp.setPoints(tempp);
		tempkp.setLines(templ);
		tempgg.setPoints(tempp);
		tempgg.setLines(templ);
		tempkp.setImage(tkp);
		tempgg.setImage(tgg);
		green = 255;
		red = 0;
		blue = 0;
		previewPoints(tempkp, "perry4_", dir);
		previewLines(tempkp, "perry5_", dir);
		previewPoints(tempgg,"gina4_",dir);
		previewLines(tempgg,"gina5_",dir);
		
		Log.d(tag, "CREATING WARPED IMAGES.");
		ImageWarper warper = new ImageWarper(kp, gg);
		Bitmap[] warped = warper.getWarpedBitmaps();
		PhotoHandler.saveToFile(warped[0], "perry6_", dir);
		PhotoHandler.saveToFile(warped[1], "gina6_", dir);
		Log.d(tag, "CROSSDISSOLVING...");
		
		try {
			Bitmap last = CrossDissolve.dissolve(warped[0], warped[1], 0.5);
			PhotoHandler.saveToFile(last, "final_", dir);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d(tag, "FULL ALGORITHM TEST COMPLETE");
		
	}
	/**
	 * Save image with landmark points marked as hot-pink
	 * @param img1
	 */
	public static Bitmap drawPoints (FacialLandmark img1){
		Log.d(MainCamera.TAG, "DEBUG: previewPoints() called.");
		Log.d(MainCamera.TAG, "Points: "+img1.getPoints().length);
		int width = img1.getImage().getWidth();
		int height = img1.getImage().getHeight();
	//	Bitmap img = img1.getImage();
		Point[] points = img1.getPoints();
	//	int[] pixels = new int[width*height];
	//	img.getPixels(pixels, 0, width, 0, 0, width, height);
		Config c = Config.ARGB_8888;
	//	Bitmap ret = Bitmap.createBitmap(width, height, c);
		Bitmap ret = img1.getImage().copy(img1.getImage().getConfig(), true);
		Log.d(MainCamera.TAG, "Looping has begun.");
		for (int i = 0; i < points.length; i++){
			int x = (int)Math.round(points[i].getX());
			int y = (int)Math.round(points[i].getY());
			Log.d(MainCamera.TAG, "DEBUG: Placing point at ("+x+", "+y+")");
			int color = Color.rgb(red, green, blue);
			putMark(ret, x, y, color);
		}
		Log.d(MainCamera.TAG, "Looping has ended");
	//	ret.setPixels(pixels, 0, width, 0, 0, width, height);
		return ret;
		//PhotoHandler.saveToFile(ret, "DEBUG_");
	}
	
	/**
	 * Save image with landmark lines marked as hot-pink
	 * @param img1
	 */
	public static Bitmap drawLines(Bitmap img1, ArrayList<Line> lines){
		Bitmap cp = img1.copy(img1.getConfig(), true);
		Canvas c = new Canvas(cp);
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setColor(Color.rgb(red, green, blue));
		for (Line l : lines){
			c.drawLine(l.getStart().getX(), l.getStart().getY(), l.getEnd().getX(), l.getEnd().getY(), p);
		}
		return cp;
	}
	
	/**
	 * Draw Points and Lines on the bitmap and save image
	 * @param img1
	 */
	public static void previewLines(FacialLandmark img1, String suf, String folder){
		Bitmap b = drawLines(drawPoints(img1), img1.getLines());
		PhotoHandler.saveToFile(b,suf, folder);
	}
	/**
	 * Draw Points on the bitmap and save image
	 * @param img1
	 */
	public static void previewPoints(FacialLandmark img1, String suf, String folder){
		Bitmap b = drawPoints(img1);
		PhotoHandler.saveToFile(b,suf, folder);
	}
	
	/**
	 * Place 5x5 mark with the given color at position (x,y) in image b.
	 * @param b
	 * @param x
	 * @param y
	 * @param color
	 */
	public static void putMark(Bitmap b, int x, int y, int color){
		for (int i = x-2; i < x+3; i++){
			for (int j = y-2; j < y+3; j++){
				if (x >= 0 && x < b.getWidth() && y >= 0 && y < b.getHeight()) b.setPixel(i, j, color);
			}
		}
	}
}
