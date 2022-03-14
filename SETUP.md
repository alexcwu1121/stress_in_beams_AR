# Ubuntu: 
## Install related packages: </br>

### Ant:
```
sudo apt install ant  //install
ant -version          //verify installation
```

### CMake:
`sudo apt install cmake`

### Java:
```
sudo apt install default-jre
sudo apt install default-jdk
javac -version                  // verify installation
```

### Finding Ant and Java:
```
whereis ant       //outputs path, normally is /usr/share/ant/
whereis jvm       //outputs path, normally is /usr/lib/jvm/

cd <path-to-jvm>
ls                //outputs directories of java here, mine is java-11-openjdk-amd64

sudo nano ~/.bashrc
//edit your bash file to include ant_home directory and java_home directory
export ANT_HOME="/usr/share/ant"
export JAVA_HOME="/usr/lib/jvm/java-11-openjdk-amd64"
```

save changes, then restart Ubuntu (exit)

make sure you set them properly by doing:
```
echo $ANT_HOME
echo $JAVA_HOME
//must output a directory, if it's blank restart your ubuntu or modify your bashrc file again.
```

### Downloading OpenCV and OpenCV_Contrib:

```
git clone https://github.com/opencv/opencv
git clone https://github.com/opencv/opencv_contrib
```

Modify OpenCV's CMakeLists.txt: </br>
ctrl-f to find "set(OPENCV_EXTRA_MODULES_PATH" </br>
underneath this, you add:

```
set(JAVA_AWT_LIBRARY "$ENV{JAVA_HOME}/lib/libjawt.so")
set(JAVA_JVM_LIBRARY "$ENV{JAVA_HOME}/lib/server/libjvm.so")
set(JAVA_INCLUDE_PATH "$ENV{JAVA_HOME}/include")
set(JAVA_INCLUDE_PATH2 "$ENV{JAVA_HOME}/include/linux")
set(JAVA_AWT_INCLUDE_PATH "$ENV{JAVA_HOME}/include")
set(ANT_EXECUTABLE "$ENV{ANT_HOME}/bin/ant")
```

### Making OpenCV with OpenCV_Contrib:

```
mkdir build && cd build
cmake -DOPENCV_EXTRA_MODULES_PATH=../opencv_contrib/modules ../opencv
```

Verify that the cmake worked by looking at output, ie; if it says "Java wrappers: Yes" at the end. </br>
Otherwise set the variables we set above manually by concat-ing arguments into cmake command like so: </br>
`cmake -DANT_EXECUTABLE=/usr/share/ant/bin/ant -DOPENCV_EXTRA_MODULES_PATH=<opencv_contrib>/modules <opencv_source_directory>` </br>
where you add a -D flag with the variable name, ANT_EXECUTABLE, set it to the path.  </br>
`make -j5`

Also look at: https://docs.opencv.org/4.x/d7/d9f/tutorial_linux_install.html

### Change file name:
If the build completed successfully, you should see opencv-xxx.jar in build/bin, </br>
where xxx is the OpenCV version number. In build/lib, you should see a file called </br>
libopencv_javaxxx.so. (so for Ubuntu, dll for Windows)

# Windows:
Basically do the same installations (CMake, Apache Ant, Java), add each to PATH. </br>
Download CMake-GUI, run it. </br>
Follow the below steps:

File->Delete Cache. Next, set the source directory to the location of the OpenCV installation. </br>
In the OpenCV installation, create a directory called build if one does not already exist. If one does already exist, make sure it is empty. </br>
In the CMake GUI, set the build directory to the build folder that was just created. Finally, hit configure and select “Unix Makefiles” from the drop down list. </br>
(doesn’t really matter as long as it outputs the jar file and dll file)

After configuring, uncheck the variable BUILD_SHARED_LIBS. Additionally, set the variable OPENCV_EXTRA_MODULES_PATH to the modules folder in the opencv-contrib installation. </br>
Configure and generate. Hit configure for a second time. Once this is done, hit generate.

## After cmake:
Verify generate worked by looking at output on bottom of cmake-gui. Ctrl-A and copy-paste into a text file for better readability. </br>
If it says “Java wrappers: Yes” as mentioned above, the installation worked.

If having issues, click add entry in cmake-gui, then add accordingly: </br>
```
Name: ANT_EXECUTABLE
Type: FILEPATH
Value: <path where ant exists>
```

And so on for java related variables as well (as shown above).
