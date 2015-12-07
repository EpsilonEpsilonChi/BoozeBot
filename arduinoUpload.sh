#!/bin/bash

# Compiles and uploads arduinoCode/src/sketch.ino

# **Requires piSetup.sh be run first for Raspberry Pi**
# **Requires Arduino IDE be installed for Mac**

function echo_bright_green {
    echo -e "\033[1;32m"$@"\033[m"
}

arch=$(uname -m)
if [ "$arch" == "x86_64" ]
then
	echo "Running on computer"

	APP_PATH="/Applications/Arduino.app/Contents/MacOS/Arduino"
	DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
	SKETCH_PATH=$DIR/arduino/arduino.ino

	echo_bright_green "Compiling and uploading arduino/arduino.ino"
	$APP_PATH --upload $SKETCH_PATH
	echo_bright_green "Uploaded"
elif [ "$arch" == "armv6l" ]
then
	echo "Running on ARM (Raspberry Pi)"

	cd arduinoCode
	sudo ino build
	sudo ino upload
	cd ..
fi
