package userinterface;

public class Point3D{

	private final double x;
	private final double y;
	private final double z;

	public Point3D(double _x, double _y, double _z){
		x = _x;
		y = _y;
		z = _z;
	}

	public double x(){
		return x;
	}

	public double y(){
		return y;
	}

	public double z(){
		return z;
	}
}