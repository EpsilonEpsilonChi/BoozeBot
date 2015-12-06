#!/bin/bash

# Compiles and uploads arduino/arduino.ino
# Only works on Mac, not Rasp Pi 

function echo_bright_green {
    echo -e "\033[1;32m"$@"\033[m"
}

APP_PATH="/Applications/Arduino.app/Contents/MacOS/Arduino"
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
SKETCH_PATH=$DIR/arduino/arduino.ino

echo_bright_green "Compiling and uploading arduino/arduino.ino"
$APP_PATH --upload $SKETCH_PATH
echo_bright_green "Uploaded"


