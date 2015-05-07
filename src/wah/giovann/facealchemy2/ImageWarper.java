package wah.giovann.facealchemy2;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.util.Log;

public class ImageWarper {
	
	private FacialLandmark image1;
	private FacialLandmark image2;
	private ArrayList<Line> image1Lines;
	private ArrayList<Line> image2Lines;
	private ArrayList<Line> intermediateLines;
	
	public FacialLandmark getImage1(){
		return image1;
	}
	public FacialLandmark getImage2(){
		return image2;
	}
	public ArrayList<Line> getImage1Lines(){
		return image1Lines;
	}
	public ArrayList<Line> getImage2Lines(){
		return image2Lines;
	}
	public ArrayList<Line> getIntermediateLines(){
		return intermediateLines;
	}
	public void setImage1(FacialLandmark i){
		this.image1 = i;
	}
	public void setImage2(FacialLandmark i){
		this.image2 = i;
	}
	public void setImage1Lines(ArrayList<Line> l){
		this.image1Lines = l;
	}
	public void setImage2Lines(ArrayList<Line> l){
		this.image2Lines = l;
	}
	public void setIntermediateLines(ArrayList<Line> l){
		this.intermediateLines = l;
	}
	
	public ImageWarper(FacialLandmark img1, FacialLandmark img2){
		if (img1 != null && img2 != null){
			this.image1 = img1;
			this.image2 = img2;
			this.image1Lines = image1.getLines();
			this.image2Lines = image2.getLines();
			this.intermediateLines = interLines(image1Lines, image2Lines);
		}
		else{
			Log.e(MainCamera.TAG, "ImageWarper error: A FacialLandmark is null.");
		}
	}
	
	public static Point[] interPoints(FacialLandmark one, FacialLandmark two){
		Point[] ret = new Point[one.getPoints().length];
		for (int i = 0; i < ret.length; i++){
			ret[i] = new Point((one.getPoints()[i].getX()+two.getPoints()[i].getX())/2,
								(one.getPoints()[i].getY()+two.getPoints()[i].getY())/2,
								0,"");
		}
		return ret;
	}
	
	/**
	 * Returns the interpolated lines between the lines in "one" and the lines in "two"
	 * @param one
	 * @param two
	 * @return
	 */
	public static ArrayList<Line> interLines(ArrayList<Line> one, ArrayList<Line> two){
		ArrayList<Line> interLines = new ArrayList<Line>();
		/**
		 * Create all intermediate lines.
		 */
		for (int i = 0; i < one.size(); i++){
			Line temp1 = one.get(i);
			Line temp2 = two.get(i);
			float startX = (temp1.getStart().getX()+temp2.getStart().getX())/2;
			float startY = (temp1.getStart().getY()+temp2.getStart().getY())/2;
			float endX = (temp1.getEnd().getX()+temp2.getEnd().getX())/2;
			float endY = (temp1.getEnd().getY()+temp2.getEnd().getY())/2;
			Line neo = new Line(new Point(startX,startY,i,""), 
								new Point(endX,endY,i,""),
								temp1.getId(), 
								temp1.getName());
			interLines.add(neo);
		}
		return interLines;
	}
	
	/**
	 * Returns the u component of the (u,v) combination
	 * @param l
	 * @param p
	 * @return
	 */
	private float getU(Line l, Point x){
		Vector qp = new Vector(l);
		Vector xp = new Vector(l.getStart(),x);
		return xp.dotproduct(qp)/l.lengthSqr();
	}
	
	/**
	 * Returns the v component of the (u,v) combination
	 * @param l
	 * @param x
	 * @return
	 */
	private float getV(Line l, Point x){
		Vector qp = new Vector(l);
		Vector perpendicularQP = qp.perpendicular();
		Vector xp = new Vector(l.getStart(),x);
	//	return xp.dotproduct(perpendicularQP)/qp.getMagnitude();
		return xp.dotproduct(perpendicularQP)/l.length();
	}
	
	/**
	 * Return the source pixel that the lines map to based on u and v
	 * @param u
	 * @param v
	 * @param l
	 * @return
	 */
	private Point getSourcePixel(float u, float v, Line l){
	//	log("LINE l: "+l.getStart().toString()+" - "+l.getEnd().toString());
		float x_ = l.getEnd().getX() - l.getStart().getX();
		float y_ = l.getEnd().getY() - l.getStart().getY();
	//	log("x: "+x_);
	//	log("y: "+y_);
		Vector qp = new Vector(x_, y_);
	//	log("qp: ("+qp.getX()+","+qp.getY()+")");
		float length = l.length();
	//	log("l.length() = "+l.length());
		Vector perpendicularQP = qp.perpendicular();
	//	log("perpendicular: ("+perpendicularQP.getX()+","+perpendicularQP.getY()+")");
		qp = qp.scalar(u);
	//	log("qp.scalar(u): ("+qp.getX()+","+qp.getY()+")");
		perpendicularQP = perpendicularQP.scalar(v/length);
	//	log("perpendicular after scalar: ("+perpendicularQP.getX()+","+perpendicularQP.getY()+")");
		Vector vec = l.getStart().createVector();
	//	log("vector vec: ("+vec.getX()+","+vec.getY()+")");
		vec = vec.add(qp);
	//	log("vector after add(qp): ("+vec.getX()+","+vec.getY()+")");
		vec = vec.add(perpendicularQP);
	//	log("vector after add(perpendicularQP): ("+vec.getX()+","+vec.getY()+")");
		Point x = new Point(vec.getX(), vec.getY(), 0, "");
	//	log("u: "+u+", v: "+v);
	//	log("x: "+x.getX()+", y: "+x.getY());
		return x;
	}
	
	public void log(String s){
		Log.d(MainCamera.TAG, s);
	}
	/**
	 * Returns the two Bitmap images, warped so that they have the same "shape".
	 * @return
	 */
	public Bitmap[] getWarpedBitmaps(){
		return new Bitmap[]{fieldWarpBitmap(image1.getImage(), image1Lines), fieldWarpBitmap(image2.getImage(), image2Lines)};
	}
	
	/**
	 * Performs the field warping algorithm on the given bitmap
	 * @param sBitmap
	 * @param sLines
	 * @return
	 */
	private Bitmap fieldWarpBitmap(Bitmap sBitmap, ArrayList<Line> sLines){
		
		Log.d(MainCamera.TAG, "fieldWarpBitmap() called!");
		float tt = sBitmap.getWidth()*sBitmap.getHeight()*sLines.size();
		float t10 = tt/10;
		float t11 = t10;
		float count = 0.0f;
		
		int oob = 0;
		float a = 0.5f;
		float b = 1.25f;
		float p = 1.0f;
		
		Bitmap ret = sBitmap.copy(sBitmap.getConfig(), true);
		for (int row = 0; row < ret.getHeight(); row++){
			for (int col = 0; col < ret.getWidth(); col++){
				//position in destination array
				Point tempPoint = new Point(col, row, 0, "");
				
			//	Point dsum = new Point(0,0,0,"");
				Vector dsum = new Vector();
				
				float weight_sum = 0;
				for (int i = 0; i < intermediateLines.size(); i++){
					//for debugging...
					if (count > t11){
						float percent = (count/tt)*100f;
						t11 += t10;
						Log.d(MainCamera.TAG, "fieldWarpBitmap(): "+percent+"% complete...");
					}
					
					//debugging complete
					Line tl = intermediateLines.get(i);
					float u = getU(tl, tempPoint);
					float v = getV(tl, tempPoint);
					//Approximated source pixel position
			//		Log.d(MainCamera.TAG, "u: "+u+", v: "+v);
					Point sX = getSourcePixel(u, v, sLines.get(i));
			//		log("d: ("+col+","+row+"), s: ("+sX.getX()+","+sX.getY()+")");
					Vector Di = new Vector(sX.sub(tempPoint));
			//		log("Displacement: "+Di.toString()+", or ("+Di.getX()+","+Di.getY()+")");
					float dist; //get distance of
					if (u >= 1.0){
						Vector x = new Vector(tl.getEnd().sub(tempPoint));
						dist = x.getMagnitude();
					}
					else if (u <= 0.0){
						Point x = new Point(tl.getStart().sub(tempPoint));
						dist = x.length();
					}
					else{
						dist = Math.abs(v);
					}
					float weight = (float) Math.pow((Math.pow(tl.length(), p)/(a+dist)), b);
				//	log("dist: "+dist+", weight: "+weight);
				//	dsum.setX(dsum.getX()+(Di.x * weight));
				//	dsum.setY(dsum.getY()+(Di.y * weight));
				//	log("dsum before add: "+dsum.toString());
					float dsumX = Di.x*weight;
					float dsumY = Di.y*weight;
				//	log("Adding x = "+dsumX+", y = "+dsumY);
					dsum = dsum.add(new Vector(dsumX, dsumY));
				//	log("dsum after add: "+dsum.toString());
					weight_sum += weight;
					count++;
				}
			//	log("Destination Point: "+tempPoint.toString()+", Displacement: "+dsum.toString()+", or "+dsum.scalar(1f/weight_sum));
				float ex = tempPoint.getX()+dsum.getX()/weight_sum; 
				float wy = tempPoint.getY()+dsum.getY()/weight_sum;
			//	float ex = tempPoint.getX()+dsum.getX();
			//	float wy = tempPoint.getY()+dsum.getY();
			//	Log.d(MainCamera.TAG, "col: "+col+", row: "+row);
			//	Log.d(MainCamera.TAG, "Point: ("+ex+", "+wy+")");
				Point srcPoint = new Point((int)(ex), (int)(wy),0,"");
			//	Log.d(MainCamera.TAG, "Point: ("+srcPoint.getX()+", "+srcPoint.getY()+")");
				int color;
				if (srcPoint.getX() >= 0 && srcPoint.getX() < sBitmap.getWidth() && srcPoint.getY() >= 0 && srcPoint.getY() < sBitmap.getHeight()){
					color = sBitmap.getPixel((int) srcPoint.getX(), (int) srcPoint.getY());
				}
				else{
					System.out.println("Pixel out of bounds: "+ (oob++) +"th pixel");
					color = sBitmap.getPixel(col, row);
				} 
				ret.setPixel(col, row, color);
			}
		}
		return ret;
		
		/*
		Line line1 = new Line(new Point(0f,0f,0,""), new Point(0f, 10f,0,""),0,"");
		Line line2 = new Line(new Point(1f, 4f,0,""), new Point(7f, 10f,0,""),0,"");
		Point point = new Point(3f, 10f,0,""); //should get new point at (6, 5)
		float u = getU(line1, point);
		float v = getV(line1, point);
		log("u: "+u+", v: "+v);
		Point source = getSourcePixel(u, v, line2);
		log("Original Point: "+point.toString()+", new point: "+source.toString());
		return null;
		*/
	}
}
