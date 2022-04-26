# How to make a Simulation

This file is going to outline the basics for making a new simulation.
## Making a Simulation

### Imports:
First, every simulation has a standard set of imports. These will be used in every simulation and some simulations might require more than these, but these are the bare minimum.
```java
package simulation;

import markerdetector.*;
import crosssection.*;

import org.opencv.core.*;
import org.opencv.aruco.*;
import org.opencv.calib3d.Calib3d;
import org.opencv.imgproc.Imgproc;
import util.HumanReadableName;
import java.util.*;
import util.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.image.*;
```
### Basic Class definition:
The class definitions are actually simple. Every Simulation needs to implement the interface "Simulation" so that it holds you to having all the required functions. Of the required Functions there are only 2, a construtor and "run". First lets look at the contructor.
```java
@HumanReadableName("Example Simulation")
public class ExampleSimulation implements Simulation {
    private final MultiMarkerBody trackingGroup;
    private final MultiMarkerBody firstGroup;
    private final MultiMarkerBody secondGroup;

    public ExampleSimulation(@Description("Multi-Marker Body") MultiMarkerBody firstGroup,
        @Description("Multi-Marker Body") MultiMarkerBody middleGroup,
        @Description("Multi-Marker Body") MultiMarkerBody lastGroup){

        this.firstGroup = firstGroup;
        this.trackingGroup = middleGroup;
        this.secondGroup = lastGroup;
    }
```
Every Simulation has 3 MultiMarkerBodies that are used to gain all the information from the beam(marker translations, marker rotations, etc.). These are the basis of every simulation, these will be your main tool for producing visualizations.

Now lets look at the run function.
```java
public Mat run(DetectorResults results){
        Mat answer = results.baseImage();
        //Grabbing the markerpositions
        Pair<Mat, Mat> p_tracking = this.trackingGroup.predictCenter(results);
        Pair<Mat, Mat> p_first = this.firstGroup.predictCenter(results);
        Pair<Mat, Mat> p_second = this.secondGroup.predictCenter(results);

        //Basic check that the camera can see all of the markerbodies
        if(p_tracking == null || p_first == null || p_second == null){
            return results.baseImage();
        }

        //first pose is on the left when looking at the simulation, second on the right
        //This converts the MultiMakerBodies to Poses which have the functions we need to produce simulations
        Pose tracking_pose = new Pose(p_tracking.first(), p_tracking.second());
        Pose first_pose = new Pose(p_first.first(), p_first.second());
        Pose second_pose = new Pose(p_second.first(), p_second.second());

        //Stuff

        //return the final image to be displayed
        return answer;
    }
``` 
This is where all the magic happens. First off, the matrix 'answer' is the image frame that is being pulled from the camera, this is what the run function should draw on(more on that later). The first cluster of lines calling "predictCenter" grabs the postions of the MultiMakerBodies to be used later. The 'if' statement is used as a check to see if all of the MultiMakerBodies are found, if not just return the original image. The last cluster calling "new Pose" converts the information grabbed from the MultiMakerBodies into a usable form. These Poses are what you are going to use for your simulations.

Back to drawing. To draw on the image, 'answer', you will need to look into "Imgproc" and "Calib3d". These will be how you get visualizations to appear. A good place to start and see how these work is [MaskSimulation.java](https://github.com/alexcwu1121/stress_in_beams_AR/blob/master/src/simulation/MaskSimulation.java)

### Summary:

Simulations are fairly simple, it just takes a minute to get used to.

Below is a shell that can be used as a base for a new simualation.
```java
package simulation;

import markerdetector.*;
import crosssection.*;

import org.opencv.core.*;
import org.opencv.aruco.*;
import org.opencv.calib3d.Calib3d;
import org.opencv.imgproc.Imgproc;
import util.HumanReadableName;
import java.util.*;
import util.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.image.*;


@HumanReadableName("Example Simulation")
public class ExampleSimulation implements Simulation {
    private final MultiMarkerBody trackingGroup;
    private final MultiMarkerBody firstGroup;
    private final MultiMarkerBody secondGroup;

    public ExampleSimulation(@Description("Multi-Marker Body") MultiMarkerBody firstGroup,
        @Description("Multi-Marker Body") MultiMarkerBody middleGroup,
        @Description("Multi-Marker Body") MultiMarkerBody lastGroup){

        this.firstGroup = firstGroup;
        this.trackingGroup = middleGroup;
        this.secondGroup = lastGroup;
    }
    public Mat run(DetectorResults results){
        Mat answer = results.baseImage();
        //Grabbing the markerpositions
        Pair<Mat, Mat> p_tracking = this.trackingGroup.predictCenter(results);
        Pair<Mat, Mat> p_first = this.firstGroup.predictCenter(results);
        Pair<Mat, Mat> p_second = this.secondGroup.predictCenter(results);

        //Basic check that the camera can see all of the markerbodies
        if(p_tracking == null || p_first == null || p_second == null){
            return results.baseImage();
        }

        //first pose is on the left when looking at the simulation, second on the right
        //This converts the MultiMakerBodies to Poses which have the functions we need to produce simulations
        Pose tracking_pose = new Pose(p_tracking.first(), p_tracking.second());
        Pose first_pose = new Pose(p_first.first(), p_first.second());
        Pose second_pose = new Pose(p_second.first(), p_second.second());

        //Stuff

        //return the final image to be displayed
        return answer;
    }
}
 ```

 ## Adding a Simulation

 Adding a simulation so it is selectable when you run the software is pretty simple. Most the work for that is taken care of for you. On line 13 in [ConfigGenerator.java](https://github.com/alexcwu1121/stress_in_beams_AR/blob/master/src/config/ConfigGenerator.java) you add the new simulation to the list.

Example:

Before
 ```java
"[\"simulation.CompoundMarkerSimulation\",\"simulation.CoordinateTestSimulation\", \"simulation.CrossSimulation\", \"simulation.DividedSimulation\", \"simulation.SimpleSimulation\", \"simulation.MaskSimulation\", \"simulation.TensorSimulation\"]");
 ```
 After
  ```java
"[\"simulation.CompoundMarkerSimulation\",\"simulation.CoordinateTestSimulation\", \"simulation.CrossSimulation\", \"simulation.DividedSimulation\", \"simulation.SimpleSimulation\", \"simulation.MaskSimulation\", \"simulation.TensorSimulation\",\"simulation.ExampleSimulation\"]");
 ```

 For any more complex simulations that require more than the standard variables, I highly recommend looking at [TensorSim PR](https://github.com/alexcwu1121/stress_in_beams_AR/pull/30/files). This is the perfect example of a new simulation getting added.