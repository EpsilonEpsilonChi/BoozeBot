# BoozeBot
Code for our house bartending robot, BoozeBot (work in progress)

## Required Packages
 - [node-serialport](https://github.com/voodootikigod/node-serialport) (install with npm)
 - [colors.js](https://github.com/marak/colors.js/) (install with npm)
 - [Firebase](https://github.com/firebase) (install with npm)
 - [Firebase Queue](https://github.com/firebase/firebase-queue) (install with npm)
 - [aJson](https://github.com/interactive-matter/aJson) (import zip file in repo to Arduino IDE before programming)

## The Files
 - **aJson.zip**: zip file of aJson library (for importing into Arduino IDE)
 - **arduino/arduino.ino**: code to be programmed to the BoozeBot Arduino (Uno) for controlling pumps
 - **firebaseFunctions.js**: file with Javascript functions used in test.html (to manipulate Firebase)
 - **raspPi.js**: code to run on Raspberry Pi to communicate between Firebase and Arduino
 - **test.html**: site used for testing functionality

## Parts List
| Part | Cost | Quantity | Total |
|------|------|------|------|
|[SainSmart 16-Channel Relay Module](http://www.amazon.com/SainSmart-16-CH-16-Channel-Relay-Module/dp/B0057OC66U/ref=sr_1_3?ie=UTF8&qid=1434582815&sr=8-3&keywords=sainsmart+relay)|$22.99|1|$22.99|
|[Priming Diaphragm Pump Spray Motor 12V](http://www.amazon.com/gp/product/B00HR8MS7G/ref=ox_sc_act_title_1?ie=UTF8&psc=1&smid=A1THAZDOWP300U)|$6.24|20|$124.80|
|[1000uF Capacitor](http://www.digikey.com/scripts/DkSearch/dksus.dll?Detail&itemSeq=174381113&uq=635701630035665475)|$0.46|20|$18.20|
|[MCP23017 I/O Expander](http://www.digikey.com/product-detail/en/MCP23017-E%2FSP/MCP23017-E%2FSP-ND/894272)|$1.44|2|$2.88|
|[Raspberry Pi T-Cobbler Plus](http://www.adafruit.com/products/1989)|$7.50|1|$7.50|

## Flow of Drink Info
1. Website or app sends drink request to Firebase (transaction under user, as well as queue)
2. Raspberry Pi running server that pulls drink requests from queue
3. Raspberry Pi sends commands to Arduino to pour drink

## To-Do
1. Obtain one diaphragm pump and test it to make sure it is what we are looking for
2. Get code working for Arduino to receive HTTP requests
3. Write iOS app to send those HTTP requests
4. Start constructing BoozeBot shell
5. Wire circuits

### Notes
- Use one of the relays to toggle the 12volt PSU on and off (only needs to be on when making drinks to power pumps)
- Useful for sending recipes to Arduino from RaspPi: https://github.com/interactive-matter/aJson
- Possibly helpful: https://github.com/ytham/barmixvah
