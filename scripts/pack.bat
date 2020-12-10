:: Command line arguments: path to class files, path to config files, path to OpenCV installation, any other dependencies.

::Setup
setlocal EnableDelayedExpansion
mkdir application
mkdir trashcan
set jarpath=%1

::Copy dependencies
mkdir application\strengths\config
xcopy /y /i "%2" application\strengths\config
mkdir \application\OpenCV\opencv\build\lib
mkdir \application\OpenCV\opencv\build\bin
xcopy /y /i "%3\opencv\build\lib" "application\OpenCV\opencv\build\lib"
(echo opencv_test) > trashcan\exclude.txt
xcopy /y /i /exclude:trashcan\exclude.txt "%3\opencv\build\bin" "application\OpenCV\opencv\build\bin"
:loop
if [%4]==[] goto :done
xcopy /y /i "%4" "application/dependencies"
shift
goto :loop
:done

::Compile jar
set fmrcwd=%cd%
cd "%jarpath%"
(echo Main-Class: userinterface.StrengthsGUI&& echo |set /p="Class-Path: opencv/opencv/build/bin/opencv-420.jar") > manifest.txt
dir %fmrcwd%\application\dependencies\*.jar /b > dir.txt
for /f "Tokens=* Delims=" %%x in (dir.txt) do set text=!text! dependencies/%%x
echo !text! >> manifest.txt
jar cfm "%fmrcwd%\application/Strengths.jar" manifest.txt .
del /q manifest.txt
del /q dir.txt
cd "%fmrcwd%"

::Package into exe
jpackage --input application --win-shortcut --main-jar Strengths.jar --java-options "-Djava.library.path=$APPDIR\OpenCV\opencv\build\lib"

::Cleanup
rmdir /s /q application
rmdir /s /q trashcan
