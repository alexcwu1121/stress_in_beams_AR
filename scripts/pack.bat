:: Command line arguments: path to class files, path to config files, path to OpenCV installation, any other dependencies.
:: ./pack.bat ../src ../src/config ../../jar1 ../../jar2
:: Assumes opencv and its dll file is in jar1 folder
:: To clean: ./pack.bat clean

::Cleanup
if "%1" == "clean" (
    rmdir /s /q application
    rmdir /s /q trashcan
    exit /b
)

::Setup
setlocal EnableDelayedExpansion
mkdir application
mkdir trashcan
set jarpath=%1

::Copy dependencies
xcopy /y /i "%2" application\strengths\config
mkdir \application\OpenCV\opencv\build\lib
mkdir \application\OpenCV\opencv\build\bin
xcopy /y "%3\opencv_java455.dll" "application\OpenCV\opencv\build\lib\"
(echo opencv_test) > trashcan\exclude.txt
xcopy /y /exclude:trashcan\exclude.txt "%3\opencv-455.jar" "application\OpenCV\opencv\build\bin\"
:loop
if [%4]==[] goto :done
xcopy /y /i "%4" "application/dependencies"
shift
goto :loop
:done

::Compile jar
set fmrcwd=%cd%
cd "%jarpath%"
(echo Main-Class: userinterface.StrengthsGUI&& echo |set /p="Class-Path: opencv/opencv/build/bin/opencv-455.jar") > manifest.txt
dir %fmrcwd%\application\dependencies\*.jar /b > dir.txt
for /f "Tokens=* Delims=" %%x in (dir.txt) do set text=!text! dependencies/%%x
echo !text! >> manifest.txt
jar cfm "%fmrcwd%\application/Strengths.jar" manifest.txt .
del /q manifest.txt
del /q dir.txt
cd "%fmrcwd%"

::Package into exe
jpackage --input application --win-shortcut --main-jar Strengths.jar --java-options "-Djava.library.path=$APPDIR\OpenCV\opencv\build\lib"
