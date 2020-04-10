package markerdetector;

import org.opencv.core.*;

public class MultiMarkerBeam{

	private final int idMaster;
	private final int id90;
	private final int id180;
	private final int id270;

	public MultiMarkerBeam(int idMaster, int id90, int id180, int id270, ){
    	this.idMaster = idMaster;
		this.id90 = id90;
		this.id180 = id180;
		this.id270 = id270;
   	}