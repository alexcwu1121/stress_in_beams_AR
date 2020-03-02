package userinterface;

import markerdetector.*;

public class Point3D{

	private final double x;
	private final double y;
	private final double z;

	public Point3D(double _x, double _y, double _z){
		x = _x;
		y = _y;
		z = _z;
	}

	public Point3D(MarkerInformation marker){
		x = marker.translationVector().get(0, 0)[0];
		y = marker.translationVector().get(0, 0)[1];
		z = marker.translationVector().get(0, 0)[2];
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