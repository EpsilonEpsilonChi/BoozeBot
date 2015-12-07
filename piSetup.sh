#!/bin/bash

# Runs commands to setup environment once the repo has been pulled on Raspberry Pi

function echo_bright_green {
    echo -e "\033[1;32m"$@"\033[m"
}

# Setup tools for compiling/uploading Arduino on Pi
echo_bright_green "Beginning installation..."
sudo apt-get update
echo_bright_green "Installing Arduino package..."
sudo apt-get install arduino
echo_bright_green "Installing Python dev/setup tools..."
sudo apt-get install python-dev&&python-setuptools
echo_bright_green "Cloning inotool repo (handy-dandy Arduino command-line tools)..."
git clone git://github.com/amperka/ino.git
echo_bright_green "Installing inotool..."
cd ino
sudo python setup.py install
echo_bright_green "Installing picocom to make ino's serial terminal functional..."
sudo apt-get install picocom
cd ..



