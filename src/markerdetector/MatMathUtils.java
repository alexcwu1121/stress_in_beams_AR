package markerdetector;

import java.util.*;
import util.*;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.json.*;

/**Class containing various utility methods for use in marker detection.
@author Owen Kulik
*/

//TODO: add putSafe

public class MatMathUtils {
	private MatMathUtils(){}

   /**Returns true if and only if the given matrices are the same dimensions and 
	each value of the given matrices are exactly equal, or if both mats are null.
	@return a boolean indicating whether the mats are exactly equal.
	*/
	public static boolean matEquals(Mat first, Mat second){
		return matEquals(first, second, 0.0);
	}

	/**Returns true if and only if the given matrices are the same dimensions and 
	each value of the given matrices are equal within the given tolerance, or if both mats are null.
	@return a boolean indicating whether the mats are equal within the given tolerance.
	*/
	public static boolean matEquals(Mat first, Mat second, double tolerance){
		if(first == null && second == null){
			return true;
		}
		if(first == second){
			return true;
		}
		if(first.rows() != second.rows() || first.cols() != second.cols() || first.channels() != second.channels()){
			return false;
		}
		for(int i = 0; i < first.rows(); i++){
			for(int j = 0; j < first.cols(); j++){
				for(int k = 0; k < first.channels(); k++){
					if(Math.abs(first.get(i, j)[k] - second.get(i, j)[k]) > tolerance){
						return false;
					}
				}
			}
		}
		return true;
	}

   /**Performs cross multiplication of the two matrices and returns the result.<br>
	The matrices must have the same number of channels.<br>
	The ith channel of the result matrix will contain the cross product of the ith channel of the first and second matrices.
	@throws NullPointerException if either argument is null.
	@throws IllegalArgumentException if first.cols() != second.rows() or if the matrices have different numbers of channels.
	@return the result of the cross multiplication.
	*/
	public static Mat matMultiply(Mat first, Mat second){
		if(first.cols() != second.rows()){
			throw new IllegalArgumentException("First matrix columns and second matrix rows do not match.");
		}
		if(first.channels() != second.channels()){
			throw new IllegalArgumentException("Matrices had different numbers of channels.");
		}
		Mat answer = new Mat(first.rows(), second.cols(), first.type());
		for(int i = 0; i < answer.rows(); i++){
			for(int j = 0; j < answer.cols(); j++){
				double[] data = new double[answer.channels()];
				for(int k = 0; k < answer.channels(); k++){
					for(int l = 0; l < first.cols(); l++){
						data[k] += first.get(i, l)[k] * second.get(l, j)[k];
					}
				}
				answer.put(i, j, data);
			}
		}
		return answer;
	}

   /**Returns the dot product of the given mats.
	@param first the first matrix
	@param second the second matrix
	@throws NullPointerException if any parameter is null.
	@throws IllegalArgumentException if first and second are different sizes.
	@return the dot product of the given mats.
	*/
	public static Mat dotMultiply(Mat first, Mat second){
		if(first.rows() != second.rows() || first.cols() != second.cols() || first.channels() != second.channels()){
			throw new IllegalArgumentException("Matrix dimensions did not match.");
		}
		Mat answer = new Mat(first.rows(), first.cols(), first.type());
		for(int i = 0; i < answer.rows(); i++){
			for(int j = 0; j < answer.cols(); j++){
				double[] data = new double[answer.channels()];
				for(int k = 0; k < answer.channels(); k++){
					data[k] = first.get(i, j)[k] * second.get(i, j)[k];
				}
				answer.put(i, j, data);
			}
		}
		return answer;
	}

   /**Multiples the given mat by the given scalar value and returns the result.
	@throws NullPointerExcpetion if any parameter is null.
	@return the result of the scalar multiplication.
	*/
	public static Mat scalarMultiply(Mat mat, double scalar){
		Mat answer = new Mat(mat.rows(), mat.cols(), mat.type());
		for(int i = 0; i < answer.rows(); i++){
			for(int j = 0; j < answer.cols(); j++){
				double[] data = new double[answer.channels()];
				for(int k = 0; k < answer.channels(); k++){
					data[k] = mat.get(i, j)[k] * scalar;
				}
				answer.put(i, j, data);
			}
		}
		return answer;
	}

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
      zRotated = matMultiply(rotationMatrix, zRotOffset);
      yRotated = matMultiply(zRotated, yRotOffset);
      xRotated = matMultiply(yRotated, xRotOffset);

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

      transOffset = matMultiply(rotationMatrix, transOffset);

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
      // sqrt(x^2 + y^2 + z^2)
      return Math.pow(Math.pow(vector.get(0, 0)[0], 2) +
            Math.pow(vector.get(1, 0)[0], 2) +
            Math.pow(vector.get(2, 0)[0], 2), .5);
   }

   public static Mat unitVector(Mat vector, Double norm){
      Mat normalized = new Mat(3, 1, CvType.CV_64FC1);
      // <x,y,z> / sqrt(x^2 + y^2 + z^2)
      normalized.put(0, 0, new double[]{vector.get(0, 0)[0]/norm});
      normalized.put(1, 0, new double[]{vector.get(1, 0)[0]/norm});
      normalized.put(2, 0, new double[]{vector.get(2, 0)[0]/norm});
      return normalized;
   }

   public static Mat crossProduct(Mat vA, Mat vB){
      // Assuming only R3 vectors 
      Mat cross = new Mat(3, 1, CvType.CV_64FC1);
      // c_x = vAy * vBz - vAz * vBy
      cross.put(0, 0, new double[]{vA.get(1, 0)[0] * vB.get(2, 0)[0] - vA.get(2, 0)[0] * vB.get(1, 0)[0]});
      // c_y = - (vAz * vBx - vAx * vBz) = vAx * vBz - vAz * vBx
      cross.put(1, 0, new double[]{-1 * (vA.get(2, 0)[0] * vB.get(0, 0)[0] - vA.get(0, 0)[0] * vB.get(2, 0)[0])});
      // c_z = vAx * vBy - vAy * vBx
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
      // vAx * vBx + vAy * vBy + vAz * vBz
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

   /**Returns a copy of the provided mat, or null if the provided mat is null.
	@param m The mat to copy.
	@return a copy of the provided mat, or null if the provided mat is null.
	*/
	public static Mat copyof(Mat m){
		if(m == null){
			return null;
		}
		Mat answer = new Mat();
		m.copyTo(answer);
		return answer;
	}

	/**Prints the provided mat.<br> 
	Note that ", " indicates the next value in the row, whereas "->" indicates a value in the same column but in the next channel.
	@param m The mat to print.
	*/
	public static void printmat(Mat m){
		System.out.println(matToString(m));
	}

	/**Returns a String representation of the provided Mat, including all data within the mat.
	@return a String representation of the provided Mat.
	*/
	public static String matToString(Mat m){
		if(m == null){
			return "null";
		}
		StringBuilder sb = new StringBuilder(m.toString());
		sb.append("\n");
        for(int i = 0; i < m.rows(); i++){
            for(int j = 0; j < m.cols(); j++){
            	double[] data = m.get(i, j);
            	for(int k = 0; k < data.length; k++){
            		sb.append(m.get(i, j)[k]);
            		if(k != data.length - 1){
            			sb.append("->");
            		}
            	}
            	if(j != m.cols() - 1){
            		sb.append(", ");
            	}
                
            }
            sb.append("\n");
        }
        return sb.toString();
	}

	/**Converts a mat to a JSON format which can be read back by jsonToMat.<br> 
	Note that this function is currently only capable of storing mats with one channel.
	@param m The mat to store.
	@throws NullPointerException if m is null.
	@return a JSONObject representing the given mat.
	*/
	public static JSONObject matToJSON(Mat m){
      JSONObject answer = new JSONObject();
      answer.put("type", m.type());
      answer.put("rows", m.rows());
      answer.put("cols", m.cols());
      JSONArray data = new JSONArray();
      for(int i = 0; i < m.rows(); i++){
         for(int j = 0; j < m.cols(); j++){
               data.put(m.get(i, j)[0]);
         }
      }
      answer.put("data", data);
      return answer;
   }

   /**Parses and returns a mat from the given JSON object.<br>
   The JSON object must have the format of objects created by the matToJson function.<br>
   Note that these functions are only capable of parsing mats with one channel.
   @param obj the JSON object
   @throws NullPointerException if obj is null
   @throws JSONException if the JSON object is missing a field
   @return the parsed mat.
   */
   public static Mat jsonToMat(JSONObject obj){
      int rows = obj.getInt("rows");
      int columns = obj.getInt("cols");
      int type = obj.getInt("type");
      Mat answer = new Mat(rows, columns, type);
      JSONArray ja = obj.getJSONArray("data");
      for(int i = 0; i < ja.length(); i++){
         answer.put(i/columns, i%columns, ja.getDouble(i));
      }
      return answer;
   }
}