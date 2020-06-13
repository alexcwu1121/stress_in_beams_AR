package markerdetector;

import org.opencv.core.*;
import java.util.*;
import util.*;
import java.io.*;
import org.json.*;
import org.opencv.calib3d.Calib3d;

public class MultiMarkerBody{
	private Map<Integer, MarkerOffset> offsets;

	public MultiMarkerBody(List<MarkerOffset> offsets){
      this.offsets = new HashMap<Integer, MarkerOffset>();
      for(MarkerOffset offset: offsets){
         offsetIds.add(offset.id());
      }
   }

   public MultiMarkerBody(MarkerOffset... offsets){
      this(Arrays.asList(offsets));
   }

   public MultiMarkerBody(Map<Integer, MarkerOffset> offsets){
      this.offsets = Map.copyOf(offsets);
   }

	public MultiMarkerBody(){
		MarkerOffset testOffset = new MarkerOffset(0, 1, 1, 1, 3, 3, 3);
      this.offsets.put(testOffset.id(), testOffset);
   }

   /**Constructs and returns a MultiMarkerBody containing the Marker Offsets as specified in the given JSON file. (use test_marker_offset.json as a template)
   @param file Path to the JSON file.
   @throws IOException if an IO error occurs.
   @throws NullPointerException if file is null.
   @return a MultiMarkerBody containing the Marker Offsets as specified in the given JSON file.
   */
   public static MultiMarkerBody fromJSONFile(String file) throws IOException {
      String content = new Scanner(new File(file)).useDelimiter("\\Z").next();
        JSONObject obj = new JSONObject(content);
        return fromJSONObject(obj);
   }

   /**Constructs and returns a MultiMarkerBody containing the Marker Offsets as specified in the given JSON object. (use test_marker_offset.json as a template)
   @param source The JSON object.
   @throws NullPointerException if source is null.
   @return a MultiMarkerBody containing the Marker Offsets as specified in the given JSON object.
   */
   public static MultiMarkerBody fromJSONObject(JSONObject source){
      Map<Integer, MarkerOffset> answer = new HashMap<Integer, MarkerOffset>();
      for(String s : source.keySet()){
         int id = Integer.valueOf(s);
         JSONObject js = source.getJSONObject(s);
         answer.put(id, new MarkerOffset(id, js.getDouble("xRot"), js.getDouble("yRot"), js.getDouble("zRot"), js.getDouble("xTrans"), js.getDouble("yTrans"), js.getDouble("zTrans")));
      }
      return new MultiMarkerBody(answer);
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

   public Mat predictTranslation(Integer id, Mat rotation, Mat translation){
      Mat rotationMatrix = new Mat();
      Calib3d.Rodrigues(rotation, rotationMatrix);
      Mat predictedTranslation = new Mat(3, 1, CvType.CV_64FC1);

      Mat transOffset = Mat.zeros(1, 3, CvType.CV_64FC1);
      transOffset.put(0, 0, offsets.get(id).xTranslation());
      transOffset.put(0, 1, offsets.get(id).yTranslation());
      transOffset.put(0, 2, offsets.get(id).zTranslation());

      MarkerUtils.printmat(translation);

      transOffset = MarkerUtils.crossMultiply(transOffset, rotationMatrix);

      double[] xtransFinal = translation.get(0, 0);
      double[] ytransFinal = translation.get(0, 0);
      double[] ztransFinal = translation.get(0, 0);
      xtransFinal[0] = translation.get(0, 0)[0] + transOffset.get(0, 0)[0];
      ytransFinal[0] = translation.get(0, 0)[1] + transOffset.get(0, 1)[0];
      ztransFinal[0] = translation.get(0, 0)[2] + transOffset.get(0, 2)[0];
      predictedTranslation.put(0, 0, xtransFinal);
      predictedTranslation.put(1, 0, ytransFinal);
      predictedTranslation.put(2, 0, ztransFinal);

      MarkerUtils.printmat(predictedTranslation);

      return predictedTranslation;
   }

   public Mat predictRotation(Integer id, Mat rotation){
      Mat predictedRotation = new Mat(3, 1, CvType.CV_64FC1);
      Mat rotationMatrix = new Mat();
      Calib3d.Rodrigues(rotation, rotationMatrix);

      Mat rotOffset = Mat.zeros(1, 3, CvType.CV_64FC1);
      rotOffset.put(0, 0, offsets.get(id).xRotation());
      rotOffset.put(0, 1, offsets.get(id).yRotation());
      rotOffset.put(0, 2, offsets.get(id).zRotation());

      Mat xRotOffset = Mat.zeros(3, 3, CvType.CV_64FC1);
      xRotOffset.put(0, 0, 1);
      xRotOffset.put(1, 1, Math.cos(rotOffset.get(0, 0)[0]));
      xRotOffset.put(1, 2, -Math.sin(rotOffset.get(0, 0)[0]));
      xRotOffset.put(2, 1, Math.sin(rotOffset.get(0, 0)[0]));
      xRotOffset.put(2, 2, Math.cos(rotOffset.get(0, 0)[0]));

      Mat yRotOffset = Mat.zeros(3, 3, CvType.CV_64FC1);
      yRotOffset.put(0, 0, Math.cos(rotOffset.get(0, 1)[0]));
      yRotOffset.put(2, 0, -Math.sin(rotOffset.get(0, 1)[0]));
      yRotOffset.put(0, 2, Math.sin(rotOffset.get(0, 1)[0]));
      yRotOffset.put(2, 2, Math.cos(rotOffset.get(0, 1)[0]));
      yRotOffset.put(1, 1, 1);

      Mat zRotOffset = Mat.zeros(3, 3, CvType.CV_64FC1);
      zRotOffset.put(0, 0, Math.cos(rotOffset.get(0, 2)[0]));
      zRotOffset.put(1, 0, Math.sin(rotOffset.get(0, 2)[0]));
      zRotOffset.put(0, 1, -Math.sin(rotOffset.get(0, 2)[0]));
      zRotOffset.put(1, 1, Math.cos(rotOffset.get(0, 2)[0]));
      zRotOffset.put(2, 2, 1);

      Mat xRotated = new Mat(3, 1, CvType.CV_64FC1);
      Mat yRotated = new Mat(3, 1, CvType.CV_64FC1);
      Mat zRotated = new Mat(3, 1, CvType.CV_64FC1);
      zRotated = MarkerUtils.crossMultiply(rotationMatrix, zRotOffset);
      yRotated = MarkerUtils.crossMultiply(zRotated, yRotOffset);
      xRotated = MarkerUtils.crossMultiply(yRotated, xRotOffset);

      Mat finalRotVector = new Mat();
      Calib3d.Rodrigues(xRotated, finalRotVector);

      return finalRotVector;
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
         if(!this.offsets.keySet().contains(id)){
            continue;
         }

         MarkerInformation intermediate = results.getMarkerInformation(id);
         //Mat rotation = intermediate.rotationVector3D();
         //Mat translation = intermediate.translationVector3D();
         Mat rotation = intermediate.rotationVector();
         Mat translation = intermediate.translationVector();

         Mat predictedRotation = predictRotation(id, rotation);
         Mat predictedTranslation = predictTranslation(id, rotation, translation);

         translationPredictions.add(predictedTranslation);
         rotationPredictions.add(predictedRotation);
      }
      return new Pair<Mat, Mat>(averagePrediction(rotationPredictions), averagePrediction(translationPredictions));
   }
}
