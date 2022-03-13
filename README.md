# stress_in_beams_AR
Augmented reality visualization of stresses in beams.

# Setup Guide (For OpenCV updates only)

Look at [SETUP.md](https://github.com/alexcwu1121/stress_in_beams_AR/blob/master/SETUP.md)

# Build and run

To build (requires OpenCV jar file, JSON jar file located in https://github.com/stleary/JSON-java, and cloned repository):

`javac -cp (your classpath) -parameters driver/*.java markerdetector/*.java crosssection/*.java userinterface/*.java simulation/*.java config/*.java`

Specifically, you should be in the src/ directory where you type this command, where your classpath is in quotation marks ("") and each classpath is separated by semicolons (for Windows) or colons (for Ubuntu).

Example as shown below (for Ubuntu): </br>
`javac -cp "../../opencvjar_directory/*:../../jsonjar_directory/*:." -parameters driver/*.java markerdetector/*.java crosssection/*.java userinterface/*.java simulation/*.java config/*.java`


Once built, run ConfigGenerator to generate config files (you only have to do this once):

`java config/ConfigGenerator`

To run the program:

`java -cp (your classpath) -Djava.library.path="(your opencv installation)\build\lib" userinterface/StrengthsGUI`

The program will ask you to calibrate your camera on your first run.
