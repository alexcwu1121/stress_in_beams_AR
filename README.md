# stress_in_beams_AR
Augmented reality visualization of stresses in beams.

# Setup Guide

https://docs.google.com/document/d/1kPT82dE_BzUuAHPY2UtxD6zXL-2t52tvAKhk561Pn2o/edit?usp=sharing

# Build and run

To build:

`javac -cp (your classpath) -parameters driver/*.java markerdetector/*.java crosssection/*.java userinterface/*.java simulation/*.java config/*.java`

Once built, run ConfigGenerator to generate config files (you only have to do this once):

`java config/ConfigGenerator`

To run the program:

`java -cp (your classpath) -Djava.library.path="(your opencv installation)\build\lib" userinterface/StrengthsGUI`

The program will ask you to calibrate your camera on your first run.
