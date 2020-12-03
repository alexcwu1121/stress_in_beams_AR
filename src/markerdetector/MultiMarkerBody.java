package markerdetector;

import org.opencv.core.*;
import java.util.*;
import util.*;
import java.io.*;
import org.json.*;
import org.opencv.calib3d.Calib3d;

public class MultiMarkerBody{
	private SortedMap<Integer, MarkerOffset> offsets;
   private double filterTol;

   public MultiMarkerBody(double filterTol, List<MarkerOffset> offsets){
      this.offsets = new TreeMap<>();
      for(MarkerOffset offset: offsets){
         this.offsets.put(offset.id(), offset);
      }
      this.filterTol = filterTol;
   }

   public MultiMarkerBody(double filterTol, MarkerOffset... offsets){
      this(filterTol, Arrays.asList(offsets));
      this.filterTol = filterTol;
   }

   public MultiMarkerBody(double filterTol, Map<Integer, MarkerOffset> offsets){
      this.offsets = new TreeMap<>(offsets);
      this.filterTol = filterTol;
   }

   /**Constructs and returns a MultiMarkerBody containing the Marker Offsets as specified in the given JSON object. (use test_marker_offset.json as a template)
   @param source The JSON object.
   @throws NullPointerException if source is null.
   @return a MultiMarkerBody containing the Marker Offsets as specified in the given JSON object.
   */
   public static MultiMarkerBody fromJSONObject(double filterTol, JSONObject source){
      Map<Integer, MarkerOffset> answer = new HashMap<Integer, MarkerOffset>();
      for(String s : source.keySet()){
         int id = Integer.valueOf(s);
         JSONObject js = source.getJSONObject(s);
         answer.put(id, new MarkerOffset(id, js.getDouble("xRot"), js.getDouble("yRot"), js.getDouble("zRot"), js.getDouble("xTrans"), js.getDouble("yTrans"), js.getDouble("zTrans")));
      }
      return new MultiMarkerBody(filterTol, answer);
   }

   /**Returns a JSONObject representation of this MultiMarkerBody, suitable for use with the fromJSONObject method.
   @return a JSONObject representation of this MultiMarkerBody
   */
   public JSONObject toJSONObject(){
      JSONObject answer = new JSONObject();
      answer.put("filterTol", this.filterTol);
      JSONArray offs = new JSONArray();
      for(MarkerOffset mo : this.offsets.values()){
         offs.put(mo);
      }
      answer.put("offsets", offs);
      return answer;
   }

   /**Constructs and returns a MultiMarkerBody from the given JSONObject.<br>
   JSONObjects produced by the toJSONObject() method are compatible for use with this method.
   @param source the JSONObject to parse
   @throws JSONException if source is malformed
   @throws NullPointerException if source is null
   @return the parsed MultiMarkerBody
   */
   public static MultiMarkerBody fromJSONObject(JSONObject source){
      double filterTol = source.getDouble("filterTol");
      JSONArray data = source.getJSONArray("offsets");
      List<MarkerOffset> offsets = new ArrayList<>();
      for(Object o : data){
         JSONObject jo = (JSONObject)o;
         offsets.add(MarkerOffset.fromJSONObject(jo));
      }
      return new MultiMarkerBody(filterTol, offsets);
   }

   /**Returns this MultiMarkerBody's filter tolerance.
   @return this MultiMarkerBody's filter tolerance.
   */
   public double getFilterTol(){
      return this.filterTol;
   }

   /**Returns this MultiMarkerBody's offsets, in a set sorted by marker ID.
   @return this MultiMarkerBody's offsets
   */
   public SortedSet<MarkerOffset> getOffsets(){
      TreeSet<MarkerOffset> answer = new TreeSet<>((offset1, offset2) -> {
         return ((Integer)offset1.id()).compareTo(offset2.id());
      });
      answer.addAll(this.offsets.values());
      return answer;
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

      Mat transOffset = Mat.zeros(3, 1, CvType.CV_64FC1);
      transOffset.put(0, 0, offsets.get(id).xTranslation());
      transOffset.put(1, 0, offsets.get(id).yTranslation());
      transOffset.put(2, 0, offsets.get(id).zTranslation());

      transOffset = MarkerUtils.matMultiply(rotationMatrix, transOffset);

      double[] xtransFinal = translation.get(0, 0);
      double[] ytransFinal = translation.get(0, 0);
      double[] ztransFinal = translation.get(0, 0);
      xtransFinal[0] = translation.get(0, 0)[0] + transOffset.get(0, 0)[0];
      ytransFinal[0] = translation.get(1, 0)[0] + transOffset.get(1, 0)[0];
      ztransFinal[0] = translation.get(2, 0)[0] + transOffset.get(2, 0)[0];
      predictedTranslation.put(0, 0, xtransFinal);
      predictedTranslation.put(1, 0, ytransFinal);
      predictedTranslation.put(2, 0, ztransFinal);

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
      zRotated = MarkerUtils.matMultiply(rotationMatrix, zRotOffset);
      yRotated = MarkerUtils.matMultiply(zRotated, yRotOffset);
      xRotated = MarkerUtils.matMultiply(yRotated, xRotOffset);

      Mat finalRotVector = new Mat();
      Calib3d.Rodrigues(xRotated, finalRotVector);

      return finalRotVector;
   }

   public Pair<LinkedList<Mat>,LinkedList<Mat>> filterPose(LinkedList<Mat> rPreds, LinkedList<Mat> tPreds){
      // Index at which new buckets are introduced
      int bucketIdx = 0;

      // Index of maximum bucket
      int maxBucketIdx = 0;

      // Histogram buckets labeled by first deviated pose
      Pair<LinkedList<Mat>,LinkedList<Mat>>[] histogram = (Pair<LinkedList<Mat>,LinkedList<Mat>>[]) new Pair<?,?>[rPreds.size()];
      
      for(int i = 0; i < rPreds.size(); i++){
         Mat rvec = rPreds.get(i);
         Mat tvec = tPreds.get(i);

         if(bucketIdx == 0){
            histogram[bucketIdx] = new Pair<LinkedList<Mat>,LinkedList<Mat>>(new LinkedList<Mat>(), new LinkedList<Mat>());
            histogram[bucketIdx].first().add(rvec);
            histogram[bucketIdx].second().add(tvec);
            bucketIdx++;
         }else{
            boolean matched = false;
            for(int bucket = 0; bucket < bucketIdx; bucket++){
               if(compareRot(rvec, histogram[bucket].first().get(0))){
                  histogram[bucket].first().add(rvec);
                  histogram[bucket].second().add(tvec);
                  matched = true;

                  if(histogram[bucket].first().size() > histogram[maxBucketIdx].first().size()){
                     maxBucketIdx = bucket;
                  }
                  break;
               }
            }
            if(!matched){
               histogram[bucketIdx] = new Pair<LinkedList<Mat>,LinkedList<Mat>>(new LinkedList<Mat>(), new LinkedList<Mat>());
               histogram[bucketIdx].first().add(rvec);
               histogram[bucketIdx].second().add(tvec);
               bucketIdx++;
            }
         }
      }
      return histogram[maxBucketIdx];
   }

   public boolean compareRot(Mat rot1, Mat rot2){
      boolean rotationMatch = true;
      for(int j = 0; j < 3; j++){
         if(rot1.get(j, 0)[0] + this.filterTol < rot2.get(j, 0)[0] ||
            rot1.get(j, 0)[0] - this.filterTol > rot2.get(j, 0)[0]){
            rotationMatch = false;
            break;
         }
      }
      return rotationMatch;
   }

   public Pair<Mat, Mat> predictCenter(DetectorResults results){
      Mat ids = results.getIds();
      if(ids.rows() == 0){
         return null;
      }

      LinkedList<Mat> translationPredictions = new LinkedList<Mat>();
      LinkedList<Mat> rotationPredictions = new LinkedList<Mat>();

      for(int i = 0; i < ids.rows(); i++){
         // Iterate over all detected ids. If id is not a member of this MMB, skip
         int id = (int)ids.get(i, 0)[0];
         if(!this.offsets.keySet().contains(id)){ continue;}

         MarkerInformation currMarker = results.getMarkerInformation(id);
         Pose currPose = currMarker.pose();

         Mat rotation = currPose.rotationVector();
         Mat translation = currPose.translationVector();

         Mat predictedRotation = predictRotation(id, rotation);
         Mat predictedTranslation = predictTranslation(id, rotation, translation);

         translationPredictions.add(predictedTranslation);
         rotationPredictions.add(predictedRotation);
      }
      
      Pair<LinkedList<Mat>,LinkedList<Mat>> filtered = filterPose(rotationPredictions, translationPredictions);

      // Check agreement
      //System.out.println(filtered.first().size());

      return new Pair<Mat, Mat>(averagePrediction(filtered.first()), averagePrediction(filtered.second()));
   }
}
