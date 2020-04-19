package markerdetector;

import org.opencv.core.*;
import java.util.*;

public class MultiMarkerBody{

	// private final int idMaster;
	// private final int id90;
	// private final int id180;
	// private final int id270;
	private List<MarkerOffset> offsets = new LinkedList<MarkerOffset>();

	public MultiMarkerBody(List<MarkerOffset> offsets){
		this.offsets = offsets;
   	}

   	public MultiMarkerBody(MarkerOffset... offsets){
		this.offsets = (Arrays.asList(offsets));
   	}

	public MultiMarkerBody(){
		 	MarkerOffset testOffset = new MarkerOffset(0, 1, 1, 1, 3, 3, 3);
		 	this.offsets.add(testOffset);
    }

   	public void update(DetectorResults results){
   		if (results.getMarkerInformation(0) != null){
   			MarkerInformation info = results.getMarkerInformation(0);
   			Mat rotation = info.rotationVector3D();
   			Mat translation = info.translationVector3D();
   			double xRot = rotation.get(0,0)[0];
   			double yRot = rotation.get(1,0)[0];
   			double zRot = rotation.get(2,0)[0];
   			double xTrans = translation.get(0,0)[0];
   			double yTrans = translation.get(1,0)[0];
   			double zTrans = translation.get(2,0)[0];

   			Mat transCoords = new Mat(1, 4, CvType.CV_64FC1);
   			transCoords.put(0, 0, xTrans);
   			transCoords.put(0, 1, yTrans);
   			transCoords.put(0, 2, zTrans);
   			transCoords.put(0, 3, 1);

   			Mat rotCoords = new Mat(1, 4, CvType.CV_64FC1);
   			transCoords.put(0, 0, xRot);
   			transCoords.put(0, 1, yRot);
   			transCoords.put(0, 2, zRot);
   			transCoords.put(0, 3, 1);

   			Mat translationMatrix = Mat.zeros(4, 4, CvType.CV_64FC1);
   			translationMatrix.put(0,0,1);
   			translationMatrix.put(1,1,1);
   			translationMatrix.put(2,2,1);
   			translationMatrix.put(3,3,1);
   			translationMatrix.put(3,0,offsets.get(0).xTranslation());
   			translationMatrix.put(3,1,offsets.get(0).yTranslation());
   			translationMatrix.put(3,2,offsets.get(0).zTranslation());

   			Mat rotationX =  Mat.zeros(4, 4, CvType.CV_64FC1);
   			rotationX.put(0,0,1);
   			rotationX.put(1,1,Math.cos(xRot));
   			rotationX.put(1,2,-Math.sin(xRot));
   			rotationX.put(2,1,Math.sin(xRot));
   			rotationX.put(2,2,Math.cos(xRot));
   			rotationX.put(3,3,1);

   			Mat rotationY =  Mat.zeros(4, 4, CvType.CV_64FC1);
   			rotationX.put(0,0,Math.cos(yRot));
   			rotationX.put(0,2,Math.sin(yRot));
   			rotationX.put(1,1,1);
   			rotationX.put(2,0,-Math.sin(yRot));
   			rotationX.put(2,2,Math.cos(yRot));
   			rotationX.put(3,3,1);

   			Mat rotationZ =  Mat.zeros(4, 4, CvType.CV_64FC1);
   			rotationX.put(0,0,Math.cos(zRot));
   			rotationX.put(0,1,-Math.sin(zRot));
   			rotationX.put(1,0,Math.sin(zRot));
   			rotationX.put(1,1,Math.cos(zRot));
   			rotationX.put(2,2,1);
   			rotationX.put(3,3,1);

   			Mat rotationXRev = new Mat(4, 4, CvType.CV_64FC1);
   			Core.multiply(rotationX, new Scalar(-1), rotationXRev);

   			Mat rotationYRev = new Mat(4, 4, CvType.CV_64FC1);
   			Core.multiply(rotationY, new Scalar(-1), rotationYRev);

   			Mat rotationZRev = new Mat(4, 4, CvType.CV_64FC1);
   			Core.multiply(rotationZ, new Scalar(-1), rotationZRev);

   			Mat transCoordsRev = new Mat(4, 4, CvType.CV_64FC1);
   			Core.multiply(transCoords, new Scalar(-1), transCoordsRev);



			Mat crossedTranslation = MarkerUtils.crossMultiply(transCoords,translationMatrix);


   			Mat transCoordsRevXY = new Mat(1, 4, CvType.CV_64FC1);
   			transCoords.put(0, 0, -xTrans);
   			transCoords.put(0, 1, -yTrans);
   			transCoords.put(0, 2, 0);
   			transCoords.put(0, 3, 1);


   			Mat centered = MarkerUtils.crossMultiply(transCoordsRev,rotationY);
   			Mat crossedY = MarkerUtils.crossMultiply(transCoordsRev,rotationY);

   			System.out.println(transCoords.get(0,0)[0]);
	   		System.out.println(transCoordsRev.get(0,0)[0]);
   		}
   	}
}
//4 by 1 matrix and x it by matrix, x,y,z,1 of translation
//normalize it with three multiplications
//tx is the offset of the x vector
//multiply the transformation matrix in
//de-normalize it
//add the rotation offsets.
//find the unit vector 