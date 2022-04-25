# stress_in_beams_AR
Augmented reality visualization of stresses in beams.

# Setup Guide (For OpenCV updates only)

Look at [SETUP.md](https://github.com/alexcwu1121/stress_in_beams_AR/blob/master/SETUP.md)

# Build and run

To build (requires OpenCV jar file, its corresponding .dll/.so file, JSON jar file located in https://mvnrepository.com/artifact/org.json/json/20190722, and cloned repository):

For the JSON jar file, click the bundle hyperlink on the webpage listed above to download the appropriate JSON version.

`javac -cp (your classpath) -parameters driver/*.java markerdetector/*.java crosssection/*.java userinterface/*.java simulation/*.java config/*.java`

Specifically, you should be in the src/ directory where you type this command, where your classpath is in quotation marks ("") and each classpath is separated by semicolons (for Windows) or colons (for Ubuntu).

Example as shown below (for Ubuntu): </br>
`javac -cp "../../opencvjar_directory/*:../../jsonjar_directory/*:." -parameters driver/*.java markerdetector/*.java crosssection/*.java userinterface/*.java simulation/*.java config/*.java`

Once built, run ConfigGenerator to generate config files (you only have to do this once):

`java -cp (your classpath) config/ConfigGenerator`

To run the program (due to issues with WSL detecting video cameras on Windows, Powershell should be used instead):

`java -cp (your classpath) "-Djava.library.path=(your opencv installation)\build\lib" userinterface/StrengthsGUI`

This command is specifically for Powershell, where -Djava.library.path is in the quotes as it is misinterpreted by the compiler.

The program will ask you to calibrate your camera on your first run. See [CALIBRATION.md](https://github.com/alexcwu1121/stress_in_beams_AR/blob/master/CALIBRATION.md).
