/**
 * Facial Landmarking system for Face Alchemy: Responsible for identifying necessary Points and Lines
 * on bitmap images.
 */
package wah.giovann.facealchemy2;

import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

import android.util.Log;

public class FacialLandmark {
	private static final String API_KEY = "6d15cee8184d18dc1c31c808e3fa2465";
	private static final String API_SECRET = "nuitzSgcn52p-VK6yBrEF4k5_eKHrRT0";
	private static final String SERVER = "http://api.us.faceplusplus.com/";
	private static final String API = "detection/landmark";
	private Point[] points;
	private ArrayList<Line> lines;
	private Bitmap image;
	
	public boolean done = false;
	
	public FacialLandmark(Bitmap img){
		this.image = img;
		this.points = null;
		this.lines = null;
		getJSONResponse(img);
	}
	public FacialLandmark(){
		this.points = null;
		this.lines = null;
		this.image = null;
	}
	
	public Point[] getPoints(){
		return this.points;
	}
	public void setPoints(Point[] p){
		this.points = p;
	}
	public ArrayList<Line> getLines(){
		return this.lines;
	}
	public void setLines(ArrayList<Line> l){
		this.lines = l;
	}
	public Bitmap getImage(){
		return this.image;
	}
	public void setImage(Bitmap img){
		this.image = img;
	}
	
	public FacialLandmark copy(){
		FacialLandmark ret = new FacialLandmark();
		ret.image = this.getImage().copy(this.getImage().getConfig(), true);
		ret.points = new Point[this.points.length];
		for (int i = 0; i < ret.points.length; i++){
			ret.points[i] = new Point(this.points[i]);
		}
		ret.lines = new ArrayList<Line>();
		for (Line l : this.lines){
			ret.lines.add(new Line(l));
		}
		return ret;
	}
	/**
	 * Calls the web-service to process the Bitmap and produce the corresponding Points and Lines
	 * representing the facial landmarks
	 * @param img
	 */
	protected void getJSONResponse(final Bitmap img){
		new Thread(new Runnable(){	
			@Override
			public void run(){
				HttpRequests req = new HttpRequests(API_KEY, API_SECRET, false, true);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				float scale = Math.min(1, Math.min(600f / img.getWidth(), 600f / img.getHeight()));
				Matrix matrix = new Matrix();
				matrix.postScale(scale, scale);
		
				Bitmap imgSmall = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, false);
				//Log.v(TAG, "imgSmall size : " + imgSmall.getWidth() + " " + imgSmall.getHeight());
				imgSmall.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] array = stream.toByteArray();
				try {
					//detect
					JSONObject result = req.detectionDetect(new PostParameters().setImg(array));
					Log.d(MainCamera.TAG, "!!! Successful Face Detection JSON response!");
					setLandmarks(result);
				} catch (FaceppParseException e) {
					e.printStackTrace();
					Log.d(MainCamera.TAG, "??? Unsuccessful Face Detection JSON response :( ...");
				}
				done = true;
			}
		}).start();
	}
	
	/**
	 * Creates the appropriate Points and Lines associated with this face, based on the 
	 * JSONObject containing landmark information
	 * @param obj
	 */
	private void setLandmarks(JSONObject obj){
		try {
			//get 83 and 25 point landmarks
			String type83 = "&type=83p";
			String type25 = "&type=25p";
			String faceid = obj.getJSONArray("face").getJSONObject(0).getString("face_id");
			Log.d(MainCamera.TAG, "FACE ID: "+faceid);
			String url83 = SERVER + API + "?" + "api_secret=" + API_SECRET + "&api_key=" + API_KEY + "&face_id=" + faceid + type83;
			String url25 = SERVER + API + "?" + "api_secret=" + API_SECRET + "&api_key=" + API_KEY + "&face_id=" + faceid + type25;
			
			StringBuffer response25 = handleResponse(url25);
			StringBuffer response83 = handleResponse(url83);
			JSONObject landmarks83 = new JSONObject(response83.toString()).getJSONArray("result").getJSONObject(0).getJSONObject("landmark");
			JSONObject landmarks25 = new JSONObject(response25.toString()).getJSONArray("result").getJSONObject(0).getJSONObject("landmark");

			Point[] temp83 = getPoints(landmarks83, 83);
			Point[] temp25 = getPoints(landmarks25, 25);
			this.points = new Point[Point.select25.length+Point.select83.length];
			//
			//Populate points array
			//
			for (int i = 0; i < this.points.length; i++){
				if (i < Point.select25.length) this.points[i] = temp25[Point.select25[i]];
				else this.points[i] = temp83[Point.select83[i-Point.select25.length]];
			}
			//
			//Populate lines list
			//
			ArrayList<Line> temp = new ArrayList<Line>();
			
			temp.add(new Line(this.points[4], this.points[5], 1, "left_eyebrow"));
			temp.add(new Line(this.points[17], this.points[18], 2, "right_eyebrow"));
			temp.add(new Line(this.points[1], this.points[3], 3, "left_eye_left"));
			temp.add(new Line(this.points[3], this.points[2], 4, "left_eye_right"));
			temp.add(new Line(this.points[2], this.points[0], 5, "left_eye_bottom"));
			temp.add(new Line(this.points[14], this.points[16], 6, "right_eye_left"));
			temp.add(new Line(this.points[16], this.points[15], 7, "right_eye_right"));
			temp.add(new Line(this.points[15], this.points[13], 8, "right_eye_bottom"));
			temp.add(new Line(this.points[26], this.points[11], 9, "left_nose"));
			temp.add(new Line(this.points[11], this.points[12], 10, "bottom_nose"));
			temp.add(new Line(this.points[12], this.points[27], 11, "right_nose"));
			temp.add(new Line(this.points[6], this.points[10], 12, "left_mouth_top"));
			temp.add(new Line(this.points[10], this.points[9], 13, "right_mouth_top"));
			temp.add(new Line(this.points[9], this.points[7], 14, "right_mouth_bottom"));
			temp.add(new Line(this.points[6], this.points[8], 15, "left_mouth_middle"));
			temp.add(new Line(this.points[8], this.points[9], 16, "right_mouth_middle"));
			temp.add(new Line(this.points[20], this.points[21], 17, "left_face1"));
			temp.add(new Line(this.points[21], this.points[22], 18, "left_face2"));
			temp.add(new Line(this.points[22], this.points[19], 19, "left_face3"));
			temp.add(new Line(this.points[23], this.points[24], 20, "right_face1"));
			temp.add(new Line(this.points[24], this.points[25], 21, "right_face2"));
			temp.add(new Line(this.points[25], this.points[19], 22, "right_face3"));
			//set this.lines to temp
			this.lines = temp;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private StringBuffer handleResponse(String url){
		try{
			URL u = new URL(url);
			HttpURLConnection con = (HttpURLConnection) u.openConnection();
			con.setRequestMethod("GET");
			Log.d(MainCamera.TAG, "Sending 'GET' request to URL: "+url);
			Log.d(MainCamera.TAG, "Response Code: "+con.getResponseCode());
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null){
				response.append(inputLine);
			}
			in.close();
			return response;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	private Point[] getPoints(JSONObject obj, int size){
		Point[] temp = new Point[size];
		try{
			if (size == 83){
				for (int i = 0; i < Point.marks83.length; i++){
					JSONObject t = obj.getJSONObject(Point.marks83[i]);
					float x = (float) (t.getDouble("x") * this.image.getWidth())/100;
					float y = (float) (t.getDouble("y") * this.image.getHeight())/100;
					temp[i] = new Point(x, y, i, Point.marks83[i]);
				}
			}
			else if (size == 25){
				for (int i = 0; i < Point.marks25.length; i++){
					JSONObject t = obj.getJSONObject(Point.marks25[i]);
					float x = (float) (t.getDouble("x") * this.image.getWidth())/100;
					float y = (float) (t.getDouble("y") * this.image.getHeight())/100;
					temp[i] = new Point(x, y, i, Point.marks25[i]);
				}
			}
			return temp;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
