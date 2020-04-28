package markerdetector;

import org.opencv.core.*;
import java.util.*;
import util.*;

public class MultiMarkerBody{

	private HashMap<Integer, MarkerOffset> offsets = new HashMap<Integer, MarkerOffset>();

	public MultiMarkerBody(List<MarkerOffset> offsets){
      for(MarkerOffset offset: offsets){
         this.offsets.put(offset.id(), offset);
      }
   }

   public MultiMarkerBody(MarkerOffset... offsets){
      for(MarkerOffset offset: offsets){
         this.offsets.put(offset.id(), offset);
      }
   }

	public MultiMarkerBody(){
		MarkerOffset testOffset = new MarkerOffset(0, 1, 1, 1, 3, 3, 3);
      this.offsets.put(testOffset.id(), testOffset);
   }

   public Mat averagePrediction(LinkedList<Mat> entries){
      Mat average = new Mat(3, 1, CvType.CV_64FC1);
      double runningSumX = 0;
      double runningSumY = 0;
      double runningSumZ = 0;
      for(Mat entry: entries){
         runningSumX = runningSumX + entry.get(0, 0)[0];
         runningSumY = runningSumY + entry.get(1, 0)[0];
         runningSumZ = runningSumZ + entry.get(2, 0)[0];
      }
      runningSumX = runningSumX/entries.size();
      runningSumY = runningSumY/entries.size();
      runningSumZ = runningSumZ/entries.size();
      average.put(0, 0, new double[]{runningSumX});
      average.put(1, 0, new double[]{runningSumY});
      average.put(2, 0, new double[]{runningSumZ});
      return average;
   }

   public Pair<Mat, Mat> predictCenter(DetectorResults results){
      Mat ids = results.getIds();
      if(ids.rows() == 0){
         return null;
      }

      LinkedList<Mat> translationPredictions = new LinkedList<Mat>();
      LinkedList<Mat> rotationPredictions = new LinkedList<Mat>();

      for(int i = 0; i < ids.rows(); i++){
         int id = (int)ids.get(i, 0)[0];
         if(!offsets.keySet().contains(id)){
            continue;
         }

         MarkerInformation intermediate = results.getMarkerInformation(id);
         Mat rotation = intermediate.rotationVector3D();
         Mat translation = intermediate.translationVector3D();
         /*
         rotation seems to be in xzy
         Translation seems to be in xyz
         */

         Mat transOffset = Mat.zeros(1, 3, CvType.CV_64FC1);
         transOffset.put(0, 0, offsets.get(id).xTranslation());
         transOffset.put(0, 1, offsets.get(id).yTranslation());
         transOffset.put(0, 2, offsets.get(id).zTranslation());

         Mat rotOffset = Mat.zeros(1, 3, CvType.CV_64FC1);
         rotOffset.put(0, 0, offsets.get(id).xRotation());
         rotOffset.put(0, 1, offsets.get(id).yRotation());
         rotOffset.put(0, 2, offsets.get(id).zRotation());

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

         translationPredictions.add(translation);
         rotationPredictions.add(rotation);
      }
      return new Pair<Mat, Mat>(averagePrediction(translationPredictions), averagePrediction(rotationPredictions));
   }
}
