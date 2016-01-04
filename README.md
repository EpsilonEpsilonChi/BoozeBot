# BoozeBot
Code for our house bartending robot, BoozeBot (work in progress)

## Required Packages
 - [serialport](https://github.com/voodootikigod/node-serialport) (install with npm)
 - [colors](https://github.com/marak/colors.js/) (install with npm)
 - [firebase](https://github.com/firebase) (install with npm)
 - [firebase-queue](https://github.com/firebase/firebase-queue) (install with npm)
 - [mkdirp](https://github.com/substack/node-mkdirp) (install with npm)
 - [jsonfile](https://github.com/jprichardson/node-jsonfile) (install with npm)
 - [aJson](https://github.com/interactive-matter/aJson) (import zip file in repo to Arduino IDE before programming)

## The Files
 - **arduino/aJson.zip**: zip file of aJson library (for importing into Arduino IDE)
 - **arduino/arduino.ino**: code to be programmed to the BoozeBot Arduino (Uno) for controlling pumps
 - **pullTransactionLogs.sh**: pulls transaction info from each user and puts it into an Excel document for analysis
 - **raspPi.js**: code to run on Raspberry Pi to communicate between Firebase and Arduino
 - **testingSite/test.html**: site used for testing functionality
 - **testingSite/firebaseFunctions.js**: file with Javascript functions used in test.html (to manipulate Firebase)

## Flow of Drink Info
1. Website or app sends drink request to Firebase (transaction under user, as well as queue)
2. Raspberry Pi running server that pulls drink request from queue
3. Raspberry Pi sends drink recipe to Arduino to pour
4. Arduino pours drink and then sends completion response back to Raspberry Pi
5. Raspberry Pi waits until response is received, then pulls another drink from the queue

## To-Do
1. Write iOS/Android apps
2. Make a script for updating Arduino serial ports

### Notes
- Use a transistor to toggle the 12volt PSU on and off (only needs to be on when making drinks to power pumps)
- Useful for sending recipes to Arduino from RaspPi: https://github.com/interactive-matter/aJson
- Possibly helpful: https://github.com/ytham/barmixvah
- **IDEA**: make raspberry pi automatically archive a transaction history every day
 

### Setting up Raspberry Pi serial ports
In order to make sure the Arduinos are assigned to their own serial port at boot (so they don't get mixed up), magic is required. 
- Get the short ID of each Arduino by plugging each one in seperately and running: 
```sudo udevadm info --query=property --name=/dev/ttyACM0 | grep SERIAL```. Copy the ```ID_SERIAL_SHORT```, which is also required for raspPi.js.
- Once you have the short ID for each Arduino, create a new rules file with: ```sudo nano /etc/udev/rules.d/99-usbserial.rules```. Inside this file, add the following two lines and save:

```
 ACTION=="add",ENV{ID_BUS}=="usb",ENV{ID_SERIAL_SHORT}=="<SHORT SERIAL FOR LED ARDUINO HERE>",SYMLINK+="ttyLEDArduino"
 ACTION=="add",ENV{ID_BUS}=="usb",ENV{ID_SERIAL_SHORT}=="<SHORT SERIAL FOR PUMP ARDUINO HERE>",SYMLINK+="ttyPumpArduino"
```

This will allow upload.sh to upload to each Arduino correctly every time.

## Notes for setting up for iOS modifications
Be sure to add the Firebase Pod: https://www.firebase.com/docs/ios/quickstart.html (see section 2)
