package markerdetector;

import org.opencv.core.*;
import java.util.*;

public class MultiMarkerBody{

	// private final int idMaster;
	// private final int id90;
	// private final int id180;
	// private final int id270;
	private List<MarkerOffset> offsets = new LinkedList<MarkerOffset>();
   //private MarkerInformation prediction = new MarkerInformation();

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

   public MarkerInformation predictCenter(DetectorResults results){
      //MarkerInformation prediction = new MarkerInformation();
      // repeat for all available markerinformation objects (all markers)
      //while(true){
         if(results.getMarkerInformation(0) == null){
            //break;
            return null;
         }
         MarkerInformation intermediate = results.getMarkerInformation(0);
         Mat rotation = intermediate.rotationVector3D();
         Mat translation = intermediate.translationVector3D();

         Mat transOffset = Mat.zeros(1, 3, CvType.CV_64FC1);
         transOffset.put(0, 0, offsets.get(0).xTranslation());
         transOffset.put(0, 1, offsets.get(0).yTranslation());
         transOffset.put(0, 2, offsets.get(0).zTranslation());

         Mat rotOffset = Mat.zeros(1, 3, CvType.CV_64FC1);
         rotOffset.put(0, 0, offsets.get(0).xRotation());
         rotOffset.put(0, 1, offsets.get(0).yRotation());
         rotOffset.put(0, 2, offsets.get(0).zRotation());

         Mat xRot = Mat.zeros(3, 3, CvType.CV_64FC1);
         xRot.put(0, 0, 1);
         xRot.put(1, 1, Math.cos(rotation.get(0, 0)[0]));
         xRot.put(1, 2, -Math.sin(rotation.get(0, 0)[0]));
         xRot.put(2, 1, Math.sin(rotation.get(0, 0)[0]));
         xRot.put(2, 2, Math.cos(rotation.get(0, 0)[0]));

         Mat yRot = Mat.zeros(3, 3, CvType.CV_64FC1);
         yRot.put(0, 0, Math.cos(rotation.get(1, 0)[0]));
         yRot.put(2, 0, -Math.sin(rotation.get(1, 0)[0]));
         yRot.put(0, 2, Math.sin(rotation.get(1, 0)[0]));
         yRot.put(2, 2, Math.cos(rotation.get(1, 0)[0]));
         yRot.put(1, 1, 1);

         Mat zRot = Mat.zeros(3, 3, CvType.CV_64FC1);
         zRot.put(0, 0, Math.cos(rotation.get(2, 0)[0]));
         zRot.put(1, 0, Math.sin(rotation.get(2, 0)[0]));
         zRot.put(0, 1, -Math.sin(rotation.get(2, 0)[0]));
         zRot.put(1, 1, Math.cos(rotation.get(2, 0)[0]));
         zRot.put(2, 2, 1);

         Mat xRotated = new Mat(3, 1, CvType.CV_64FC1);
         Mat yRotated = new Mat(3, 1, CvType.CV_64FC1);
         Mat zRotated = new Mat(3, 1, CvType.CV_64FC1);
         //Core.gemm(transOffset, xRot, 1, new Mat(), 0, xRotated);
         xRotated = MarkerUtils.crossMultiply(transOffset, xRot);
         yRotated = MarkerUtils.crossMultiply(xRotated, yRot);
         zRotated = MarkerUtils.crossMultiply(yRotated, zRot);

         double[] xtransFinal = translation.get(0, 0);
         double[] ytransFinal = translation.get(1, 0);
         double[] ztransFinal = translation.get(2, 0);
         xtransFinal[0] = translation.get(0, 0)[0] + zRotated.get(0, 0)[0];
         ytransFinal[0] = translation.get(1, 0)[0] + zRotated.get(0, 1)[0];
         ztransFinal[0] = translation.get(2, 0)[0] + zRotated.get(0, 2)[0];
         translation.put(0, 0, xtransFinal);
         translation.put(1, 0, ytransFinal);
         translation.put(2, 0, ztransFinal);

         double[] xRotFinal = rotation.get(0, 0);
         double[] yRotFinal = rotation.get(1, 0);
         double[] zRotFinal = rotation.get(2, 0);
         xRotFinal[0] = rotation.get(0, 0)[0] + rotOffset.get(0, 0)[0];
         yRotFinal[0] = rotation.get(1, 0)[0] + rotOffset.get(0, 1)[0];
         zRotFinal[0] = rotation.get(2, 0)[0] + rotOffset.get(0, 2)[0];
         rotation.put(0, 0, xRotFinal);
         rotation.put(1, 0, yRotFinal);
         rotation.put(2, 0, zRotFinal);


      //}
      return null;
   }
}
