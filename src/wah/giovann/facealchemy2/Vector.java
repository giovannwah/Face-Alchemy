package wah.giovann.facealchemy2;

public class Vector extends Point {

	 public Vector() {
		 this.x = 0.0f;
		 this.y = 0.0f;
	 }
	   
	   /**
	   * Constructor for the Point class
	   * @param x1 float value for the x-coordinate of the point
	   * @param y1 float value for the y-coordinate of the point
	   */
	   public Vector(float x1, float y1) {
	       this.x = x1;
	       this.y = y1;  
	   } 
	   
	   /**
	   * Construct Vector from line
	   */
	   public Vector(Line l) {
	       this.x = l.getEnd().getX() - l.getStart().getX();
	       this.y = l.getEnd().getY() - l.getStart().getY();
	   }
	   
	   /**
	   * Constructor for the Point class
	   * @param p1 an existing point
	   */
	   public Vector(Point p1) {
	       this.x = p1.getX() ;
	       this.y = p1.getY() ;
	   }
	   
	   /**
	   * Constructor for the Point class.
	   * Creates vector directed from second point to first
	   * @param p1 the first point/ start
	   * @param p2 the second point/ end
	   */
	   public Vector(Point p1, Point p2) {
		   /*
	       x = p1.getX() - p2.getX();
	       y = p1.getY() - p2.getY();
	       */
		   this.x = p2.getX() - p1.getX();
		   this.y = p2.getY() - p1.getY();
	   }
	   
	   /**
	   * Returns magnitude of the vector
	   * @return
	   */
	   public float getMagnitude() {
	       return (float) Math.sqrt((double) x*x + y*y); 
	   }
	   
	   /**
	   * Returns square of magnitude of the vector
	   * @return the square of the distance from origin (0,0) to the point
	   */
	   public float getMagnitude2() {
	       return (x*x + y*y);
	   }
	   
	   /**
	    * Function to compute the dot product of two vector
	    * @param v2 a vector
	    * @return the dot product of the vector 'v2' and itself
	    */
	   public float dotproduct(Vector v2) {
	       return (float) v2.x*x + v2.y*y;
	   }
	   
	   /**
	    * Function to find a vector perpendicular to the original vector
	    * @return a perpendicular vector
	    */
	   public Vector perpendicular() {
	       Vector perp = new Vector(-y,x);
	       return perp;
	   }
	   
	   /**
	    * Function to carry scalar multiplication of a vector
	    */
	   public Vector scalar(float s) {
	       Vector vec = new Vector(s*x,s*y);
	       return vec;
	   }
	    
	  /**
	   * Function to carry vector addition 
	   */
	   public Vector add(Vector v2) {
	       Vector vec = new Vector(x + v2.x , y + v2.y);
	       return vec;
	  }
	  
	   /**
	    * Function to carry vector addition 
	    */
	   public Vector sub(Vector v2) {
	       Vector vec = new Vector(x - v2.x , y - v2.y);
	       return vec;
	       
	   }
}
  
  
