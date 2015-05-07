package wah.giovann.facealchemy2;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.util.Log;

public class CrossDissolve {
	
	public static Bitmap dissolve(Bitmap img1, Bitmap img2, double t) throws Exception{
		Log.d(MainCamera.TAG, "dissolve() called.");
		Log.d(MainCamera.TAG, "Image1: "+img1.getWidth()+"x"+img1.getHeight());
		Log.d(MainCamera.TAG, "Image2: "+img2.getWidth()+"x"+img2.getHeight());
		if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()){
			throw new Exception("Images not the same dimensions. (width and height mismatch).");
		}
		else{
			double t2 = 1-t;
			int width = img1.getWidth();
			int height = img1.getHeight();
			int len = width*height;
			int [] pix1 = new int[len];
			int [] pix2 = new int[len];
			int [] r = new int[len];
			img1.getPixels(pix1, 0, width, 0, 0, width, height);
			img2.getPixels(pix2, 0, width, 0, 0, width, height);
			Config c = Config.ARGB_8888;
			Bitmap ret = Bitmap.createBitmap(width, height, c);
			for (int i = 0; i < r.length; i++){
				int red1 = Color.red(pix1[i]);
				int red2 = Color.red(pix2[i]);
				int green1 = Color.green(pix1[i]);
				int green2 = Color.green(pix2[i]);
				int blue1 = Color.blue(pix1[i]);
				int blue2 = Color.blue(pix2[i]);
				int red = (int) Math.round((red1*t)+(red2*t2));
				int green = (int) Math.round((green1*t)+(green2*t2));
				int blue = (int) Math.round((blue1*t)+(blue2*t2));
				r[i] = Color.rgb(red, green, blue);
			}
			ret.setPixels(r, 0, width, 0, 0, width, height);
			return ret;
			
		}
	}
}
