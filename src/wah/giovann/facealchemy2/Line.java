package wah.giovann.facealchemy2;

public class Line {
	private int id;
	private Point start;
	private Point end;
	private String name;
	
	public Line(Point s, Point e, int id_, String n){
		this.id = id_;
		this.start = s;
		this.end = e;
		this.name = n;
	}
	public Line (Line l){
		this.id = l.id;
		this.start = new Point(l.getStart());
		this.end = new Point(l.getEnd());
		this.name = new String(l.getName());
	}
	
	public Vector createVector(){
		Vector ret = new Vector(start, end);
		return ret;
	}
	
	public float lengthSqr(){
	//	return (end.sub(start).lengthSqr());
		return (float) Math.pow(length(), 2);
	}
	
	public float length(){
		return start.distanceFromPoint(end);
	}
	public int getId(){
		return this.id;
	}
	public Point getStart(){
		return this.start;
	}
	public Point getEnd(){
		return this.end;
	}
	public String getName(){
		return this.name;
	}
	public void setId(int id_){
		this.id = id_;
	}
	public void setStart(Point p){
		this.start = p;
	}
	public void setEnd(Point p){
		this.end = p;
	}
	public void setName(String n){
		this.name = n;
	}
}
