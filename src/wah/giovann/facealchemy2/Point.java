/**
 *	The following class represents a point in 2D image space
 *	author: Giovann Wah
 */
package wah.giovann.facealchemy2;

public class Point {
	public static final String[] marks83 = new String[]{
		"contour_chin", //0
		"contour_left1", //1
		"contour_left2", //2
		"contour_left3", //3
		"contour_left4", //4
		"contour_left5", //5
		"contour_left6", //6
		"contour_left7", //7
		"contour_left8", //8
		"contour_left9", //9
		"contour_right1", //10
		"contour_right2", //11
		"contour_right3", //12
		"contour_right4", //13
		"contour_right5", //14
		"contour_right6", //15
		"contour_right7", //16
		"contour_right8", //17
		"contour_right9", //18
		"left_eye_bottom", //19
		"left_eye_center", //20
		"left_eye_left_corner", //21
		"left_eye_lower_left_quarter", //22 
		"left_eye_lower_right_quarter", //23
		"left_eye_pupil", //24
		"left_eye_right_corner", //25
		"left_eye_top", //26
		"left_eye_upper_left_quarter", //27
		"left_eye_upper_right_quarter", //28
		"left_eyebrow_left_corner", //29
		"left_eyebrow_lower_left_quarter", //30
		"left_eyebrow_lower_middle", //31
		"left_eyebrow_lower_right_quarter", //32
		"left_eyebrow_right_corner", //33
		"left_eyebrow_upper_left_quarter", //34 
		"left_eyebrow_upper_middle", //35
		"left_eyebrow_upper_right_quarter", //36 
		"mouth_left_corner", //37
		"mouth_lower_lip_bottom", //38
		"mouth_lower_lip_left_contour1", //39
		"mouth_lower_lip_left_contour2", //40
		"mouth_lower_lip_left_contour3", //41
		"mouth_lower_lip_right_contour1", //42
		"mouth_lower_lip_right_contour2", //43
		"mouth_lower_lip_right_contour3", //44
		"mouth_lower_lip_top", //45
		"mouth_right_corner", //46
		"mouth_upper_lip_bottom", //47
		"mouth_upper_lip_left_contour1", //48 
		"mouth_upper_lip_left_contour2", //49
		"mouth_upper_lip_left_contour3", //50
		"mouth_upper_lip_right_contour1", //51
		"mouth_upper_lip_right_contour2", //52
		"mouth_upper_lip_right_contour3", //53
		"mouth_upper_lip_top", //54
		"nose_contour_left1", //55
		"nose_contour_left2", //56
		"nose_contour_left3", //57
		"nose_contour_lower_middle", //58
		"nose_contour_right1", //59
		"nose_contour_right2", //60
		"nose_contour_right3", //61
		"nose_left", //62
		"nose_right", //63
		"nose_tip", //64
		"right_eye_bottom", //65 
		"right_eye_center", //66
		"right_eye_left_corner", //67
		"right_eye_lower_left_quarter", //68 
		"right_eye_lower_right_quarter", //69
		"right_eye_pupil", //70
		"right_eye_right_corner", //71 
		"right_eye_top", //72
		"right_eye_upper_left_quarter", //73
		"right_eye_upper_right_quarter", //74
		"right_eyebrow_left_corner", //75
		"right_eyebrow_lower_left_quarter", //76 
		"right_eyebrow_lower_middle", //77
		"right_eyebrow_lower_right_quarter", //78 
		"right_eyebrow_right_corner", //79
		"right_eyebrow_upper_left_quarter", //80 
		"right_eyebrow_upper_middle", //81
		"right_eyebrow_upper_right_quarter" //82
	};
	public static final String[] marks25 = new String[]{
		"left_eye_bottom", //0
	    "left_eye_center", //1
	    "left_eye_left_corner", //2
	    "left_eye_pupil", //3 
	    "left_eye_right_corner", //4 
	    "left_eye_top", //5
	    "left_eyebrow_left_corner", //6 
	    "left_eyebrow_right_corner", //7
	    "mouth_left_corner", //8
	    "mouth_lower_lip_bottom", //9 
	    "mouth_lower_lip_top", //10
	    "mouth_right_corner", //11
	    "mouth_upper_lip_bottom", //12
	    "mouth_upper_lip_top", //13
	    "nose_left", //14
	    "nose_right", //15
	    "nose_tip", //16
	    "right_eye_bottom", //17 
	    "right_eye_center", //18
	    "right_eye_left_corner", //19
	    "right_eye_pupil", //20
	    "right_eye_right_corner", //21 
	    "right_eye_top", //22
	    "right_eyebrow_left_corner", //23 
	    "right_eyebrow_right_corner" //24
	};
	public static int[] select83 = new int[]{0, 1, 4, 7, 10, 13, 16, 56, 60};
	public static int[] select25 = new int[]{0, 2, 4, 5, 6, 7, 8, 9, 10, 11, 13, 14, 15, 17, 19, 21, 22, 23, 24};
	public static String[] finalPointNames = new String[]{	marks25[0], marks25[2], marks25[4], marks25[5], marks25[6],
															marks25[7], marks25[8], marks25[9], marks25[10], marks25[11], 
															marks25[13], marks25[14], marks25[15], marks25[17], marks25[19], 
															marks25[21], marks25[22], marks25[23], marks25[24], marks83[0], 
															marks83[1], marks83[4], marks83[7], marks83[10], marks83[13], 
															marks83[16], marks83[56], marks83[60]};
	private int id;
	protected float x;
	protected float y;
	private String name;
	
	public Point(float x_, float y_, int id_, String n){
		this.id = id_;
		this.x = x_;
		this.y = y_;
		this.name = n;
	}
	
	public Point(Point p){
		this.id = p.id;
		this.x = p.x;
		this.y = p.y;
		this.name = p.name;
	}
	
	public Point(){
		this.id = 0;
		this.x = 0;
		this.y = 0;
		this.name = "";
	}
	
	public Point (Vector v){
		this.x = v.x;
		this.y = v.y;
		this.id = 0;
		this.name = "";
	}
	 /**
	    * Function to find distance from another point
	    * @return 
	    */
	public float distanceFromPoint(Point p1) {
		return (float) Math.sqrt((p1.x - x)*(p1.x - x) + (p1.y - y)*(p1.y - y));
	}
	
	/**
	 * Returns the square of the distance from the origin
	 * @return 
	 */
	public float lengthSqr(){
		float x = this.length();
		return x*x;
	}
	/**
	 * Function to add another point as a vector
	 * Individually adds x and y coordinates of the points
	 * @return 
	 */
	public Point add(Point pt) {
		Point p = new Point(x+pt.x, y+pt.y, 0, "");
	    return p;
	}
	    
	@Override
	public String toString(){
		return "("+this.x+", "+this.y+")";
	}
  /**
	* Function to subtract another point as a vector
	* Individually subtracts x and y coordinates of the points
	* @return 
	*/
	public Point sub(Point pt) {
		Point p = new Point(x-pt.x, y-pt.y, 0, "");
	    return p;
	}
	    
	public Point copy(Point p){
		return new Point(this.x, this.y, this.id, this.name);
	}
	
	public Vector createVector(){
		return new Vector(this.x, this.y);
	}
	/**
	 * Returns distance from origin
	 * @return
	 */
	public float length(){
		return (float) Math.sqrt((this.x*this.x) + (this.y*this.y));
	}
	
	public int getId(){
		return this.id;
	}
	public float getX(){
		return this.x;
	}
	public float getY(){
		return this.y;
	}
	public String getName(){
		return this.name;
	}
	public void setId(int id_){
		this.id = id_;
	}
	public void setX(float x_){
		this.x = x_;
	}
	public void setY(float y_){
		this.y = y_;
	}
	public void setName(String n){
		this.name = n;
	}
}
