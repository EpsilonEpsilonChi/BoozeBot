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
 - **testing/sitetest.html**: site used for testing functionality
 - **testingSite/firebaseFunctions.js**: file with Javascript functions used in test.html (to manipulate Firebase)

<!--## Parts List-->
<!--| Part | Cost | Quantity | Total |-->
<!--|------|------|------|------|-->
<!--|[SainSmart 16-Channel Relay Module](http://www.amazon.com/SainSmart-16-CH-16-Channel-Relay-Module/dp/B0057OC66U/ref=sr_1_3?ie=UTF8&qid=1434582815&sr=8-3&keywords=sainsmart+relay)|$22.99|1|$22.99|-->
<!--|[Priming Diaphragm Pump Spray Motor 12V](http://www.amazon.com/gp/product/B00HR8MS7G/ref=ox_sc_act_title_1?ie=UTF8&psc=1&smid=A1THAZDOWP300U)|$6.24|20|$124.80|-->

## Flow of Drink Info
1. Website or app sends drink request to Firebase (transaction under user, as well as queue)
2. Raspberry Pi running server that pulls drink request from queue
3. Raspberry Pi sends drink recipe to Arduino to pour
4. Arduino pours drink and then sends completion response back to Raspberry Pi
5. Raspberry Pi waits until response is received, then pulls another drink from the queue

## To-Do
1. Write iOS/Android apps
2. Geting NPM to stop being so goddamn annoying on the Raspberry Pi so everything will work
3. Figure out how to compile/upload Arduino sketches on the Raspberry Pi

### Notes
- Use a transistor to toggle the 12volt PSU on and off (only needs to be on when making drinks to power pumps)
- Useful for sending recipes to Arduino from RaspPi: https://github.com/interactive-matter/aJson
- Possibly helpful: https://github.com/ytham/barmixvah
- **IDEA:**: make raspberry pi automatically archive a transaction history every day
