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

### Verifying the installation worked:
If the build completed successfully, you should see opencv-xxx.jar in build/bin, </br>
where xxx is the OpenCV version number. In build/lib, you should see a file called </br>
libopencv_javaxxx.so. (so for Ubuntu, dll for Windows)

# Windows:

This is done using MSYS2, where cmake and make are required for creating OpenCV. (cannot be done separately with WSL, must be CMake+Make in same environment)
- Note: Make sure your CMakeLists.txt file is reset if transitioning from WSL (as WSL cannot detect webcam currently)

- Install MSYS2 so that you can use CMake and Make. https://www.msys2.org/
- Install Ant zip file and extract. https://ant.apache.org/bindownload.cgi
- Install Java JDK

Follow MSYS2 guide properly, installing all necessary packages. Then install cmake: </br>
`pacman -S mingw-w64-x86_64-cmake` </br>
Make as well (x86_64 as well as i686 just in case): </br>
```
pacman -S mingw-w64-x86_64-toolchain
pacman -S mingw-w64-i686-toolchain
```

## CMake:

CMake on its own is already pretty complicated. Detecting JDK is one thing, while Ant is another. </br>
- Go to MinGW x64 (installed with MSYS2) and create a new folder. `mkdir build && cd build`
- Export JAVA_HOME and ANT_HOME directly, because for some reason it doesn't work otherwise.
```
export JAVA_HOME="/c/Program Files/Java/jdk-16.0.1"
export ANT_HOME="/c/apache-ant-1.10.12"
export PATH=$PATH:${JAVA_HOME}/bin:${ANT_HOME}/bin;
echo $PATH                                                //verify Java and Ant path were properly concatenated to path
```
- Attempt CMake. `cmake -G "MinGW Makefiles" -DOPENCV_EXTRA_MODULES_PATH=../opencv_contrib/modules ../opencv`

## After cmake:

Verify that CMake has worked (Ant was properly detected in the right directory, as well as Java. Java wrappers is indicated as Yes) </br>
If it doesn't work properly, unfortunately you will have to try to rebind Ant and Java to the PATH variable. </br>
There are multiple ways to do this in Windows but none of the other ways worked for me.

## Make:

You may be wondering why we needed to install x86_64 and i686 toolchain. both were necessary because I had issues running make without them. </br>
We need to make a symlink by hand, which has to be done in Command Prompt (admin mode).
```
cd c:\<your msys installation path>\mingw64\bin
mklink make mingw32-make.exe
```
The next part seems extremely weird, but this is the only way I was able to get it to work (somewhat). </br>
- Go to MinGW x64 again and run `mingw32-make` in the build directory.
- If there are errors that spawn, you can then attempt running `make` and switch them if they don't work.
- DO NOT USE -j# flag, ie; `mingw32-make -j5`. This will break, and there is no way to circumvent this.

Compared to Ubuntu, you will have to deal with just mingw32-make without any extra flags.
