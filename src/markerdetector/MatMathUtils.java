package markerdetector;

import java.util.*;
import util.*;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;

public class MatMathUtils {
	private MatMathUtils(){}

	public static Mat predictRotation(Mat rotation, Pose offset){
      Mat predictedRotation = new Mat(3, 1, CvType.CV_64FC1);
      Mat rotationMatrix = new Mat();
      Calib3d.Rodrigues(rotation, rotationMatrix);

      Mat rotOffset = Mat.zeros(1, 3, CvType.CV_64FC1);
      rotOffset.put(0, 0, offset.xRotation());
      rotOffset.put(0, 1, offset.yRotation());
      rotOffset.put(0, 2, offset.zRotation());

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

   public static Mat predictTranslation(Mat rotation, Mat translation, Pose offset){
      Mat rotationMatrix = new Mat();
      Calib3d.Rodrigues(rotation, rotationMatrix);
      Mat predictedTranslation = new Mat(3, 1, CvType.CV_64FC1);

      Mat transOffset = Mat.zeros(3, 1, CvType.CV_64FC1);
      transOffset.put(0, 0, offset.xTranslation());
      transOffset.put(1, 0, offset.yTranslation());
      transOffset.put(2, 0, offset.zTranslation());

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

   public static Double norm(Mat vector){
      return Math.pow(Math.pow(vector.get(0, 0)[0], 2) +
            Math.pow(vector.get(1, 0)[0], 2) +
            Math.pow(vector.get(2, 0)[0], 2), .5);
   }

   public static Mat unitVector(Mat vector, Double norm){
      Mat normalized = new Mat(3, 1, CvType.CV_64FC1);
      normalized.put(0, 0, new double[]{vector.get(0, 0)[0]/norm});
      normalized.put(1, 0, new double[]{vector.get(1, 0)[0]/norm});
      normalized.put(2, 0, new double[]{vector.get(2, 0)[0]/norm});
      return normalized;
   }

   public static Mat crossProduct(Mat vA, Mat vB){
      // Assuming only R3 vectors 
      Mat cross = new Mat(3, 1, CvType.CV_64FC1);
      cross.put(0, 0, new double[]{vA.get(1, 0)[0] * vB.get(2, 0)[0] - vA.get(2, 0)[0] * vB.get(1, 0)[0]});
      cross.put(1, 0, new double[]{-1 * (vA.get(2, 0)[0] * vB.get(0, 0)[0] - vA.get(0, 0)[0] * vB.get(2, 0)[0])});
      cross.put(2, 0, new double[]{vA.get(0, 0)[0] * vB.get(1, 0)[0] - vA.get(1, 0)[0] * vB.get(0, 0)[0]});
      return cross;
   }

   public static double[] matToArray(Mat m){
      double[] matarr = new double[3];
      matarr[0] = m.get(0, 0)[0];
      matarr[1] = m.get(1, 0)[0];
      matarr[2] = m.get(2, 0)[0];
      return matarr;
   }

   public static Double dotProduct(Mat vA, Mat vB){
      // Assuming only R3 vectors 
      Double dot = 0d;
      dot = vA.get(0, 0)[0] * vB.get(0, 0)[0] +
            vA.get(1, 0)[0] * vB.get(1, 0)[0] +
            vA.get(2, 0)[0] * vB.get(2, 0)[0];
      return dot;
   }

   public static Mat xRot(double theta){
      // Assuming only R3 vectors 
      Mat xRot = Mat.zeros(3, 3, CvType.CV_64FC1);
      xRot.put(0, 0, 1);
      xRot.put(1, 1, Math.cos(theta));
      xRot.put(1, 2, -Math.sin(theta));
      xRot.put(2, 1, Math.sin(theta));
      xRot.put(2, 2, Math.cos(theta));
      return xRot;
   }

   public static Mat yRot(double theta){
      // Assuming only R3 vectors 
      Mat yRot = Mat.zeros(3, 3, CvType.CV_64FC1);
      yRot.put(0, 0, Math.cos(theta));
      yRot.put(2, 0, -Math.sin(theta));
      yRot.put(0, 2, Math.sin(theta));
      yRot.put(2, 2, Math.cos(theta));
      yRot.put(1, 1, 1);
      return yRot;
   }

   public static Mat zRot(double theta){
      // Assuming only R3 vectors 
      Mat zRot = Mat.zeros(3, 3, CvType.CV_64FC1);
      zRot.put(0, 0, Math.cos(theta));
      zRot.put(1, 0, Math.sin(theta));
      zRot.put(0, 1, -Math.sin(theta));
      zRot.put(1, 1, Math.cos(theta));
      zRot.put(2, 2, 1);
      return zRot;
   }

   /*
   This requires a pure rotation matrix 'm' as input.
   */
   public static Mat toAxisAngle(Mat m) {
      double angle,x,y,z; // variables for result
      double epsilon = 0.01; // margin to allow for rounding errors
      double epsilon2 = 0.1; // margin to distinguish between 0 and 180 degrees'

      Mat result = Mat.zeros(4, 1, CvType.CV_64FC1);

      if ((Math.abs(m.get(0,1)[0]-m.get(1,0)[0])< epsilon)
         && (Math.abs(m.get(0,2)[0]-m.get(2,0)[0])< epsilon)
         && (Math.abs(m.get(1,2)[0]-m.get(2,1)[0])< epsilon)) {
         // singularity found
         // first check for identity matrix which must have +1 for all terms
         // in leading diagonaland zero in other terms
         if ((Math.abs(m.get(0,1)[0]+m.get(1,0)[0]) < epsilon2)
            && (Math.abs(m.get(0,2)[0]+m.get(2,0)[0]) < epsilon2)
            && (Math.abs(m.get(1,2)[0]+m.get(2,1)[0]) < epsilon2)
            && (Math.abs(m.get(0,0)[0]+m.get(1,1)[0]+m.get(2,2)[0]-3) < epsilon2)) {
            // this singularity is identity matrix so angle = 0
            result.put(1,0,1);
            return result; // zero angle, arbitrary axis
         }
         angle = Math.PI;
         double xx = (m.get(0,0)[0]+1)/2;
         double yy = (m.get(1,1)[0]+1)/2;
         double zz = (m.get(2,2)[0]+1)/2;
         double xy = (m.get(0,1)[0]+m.get(1,0)[0])/4;
         double xz = (m.get(0,2)[0]+m.get(2,0)[0])/4;
         double yz = (m.get(1,2)[0]+m.get(2,1)[0])/4;
         if ((xx > yy) && (xx > zz)) { // m[0][0] is the largest diagonal term
            if (xx< epsilon) {
               x = 0;
               y = 0.7071;
               z = 0.7071;
            } else {
               x = Math.sqrt(xx);
               y = xy/x;
               z = xz/x;
            }
         } else if (yy > zz) { // m[1][1] is the largest diagonal term
            if (yy< epsilon) {
               x = 0.7071;
               y = 0;
               z = 0.7071;
            } else {
               y = Math.sqrt(yy);
               x = xy/y;
               z = yz/y;
            }  
         } else { // m[2][2] is the largest diagonal term so base result on this
            if (zz< epsilon) {
               x = 0.7071;
               y = 0.7071;
               z = 0;
            } else {
               z = Math.sqrt(zz);
               x = xz/z;
               y = yz/z;
            }
         }
         result.put(0,0,angle);
         result.put(1,0,x);
         result.put(2,0,y);
         result.put(3,0,z);
         return result; // return 180 deg rotation
      }
      // as we have reached here there are no singularities so we can handle normally
      double s = Math.sqrt((m.get(2,1)[0] - m.get(1,2)[0])*(m.get(2,1)[0] - m.get(1,2)[0])
         +(m.get(0,2)[0] - m.get(2,0)[0])*(m.get(0,2)[0] - m.get(2,0)[0])
         +(m.get(1,0)[0] - m.get(0,1)[0])*(m.get(1,0)[0] - m.get(0,1)[0])); // used to normalise
      if (Math.abs(s) < 0.001) s=1; 
         // prevent divide by zero, should not happen if matrix is orthogonal and should be
         // caught by singularity test above, but I've left it in just in case
      angle = Math.acos(( m.get(0,0)[0] + m.get(1,1)[0] + m.get(1,1)[0] - 1)/2);
      x = (m.get(2,1)[0] - m.get(1,2)[0])/s;
      y = (m.get(0,2)[0] - m.get(2,0)[0])/s;
      z = (m.get(1,0)[0] - m.get(0,1)[0])/s;

      result.put(0,0,angle);
      result.put(1,0,x);
      result.put(2,0,y);
      result.put(3,0,z);
      return result;
   }
}