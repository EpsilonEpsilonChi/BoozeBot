var Firebase   = require("firebase");
var SerialPort = require("serialport");
var colors     = require("colors/safe");
var Queue      = require("firebase-queue");
var exec       = require('child_process').exec;

// Configuration variables
var verbose                 = true;
var serialBaud              = 115200;
var ledArduinoSerialNum     = "754373038303515150C1";
var pumpArduinoSerialNum    = "75437303830351917221";
var queueRef                = new Firebase('https://boozebot.firebaseio.com/drinkQueue');

// Serial ports
var serialPortLed;
var serialPortPump;

// Listens to Firebase queue for new data
var firebaseListener = function(data, progress, resolve, reject) {
    console.log(colors.green("BoozeBot making drink: ") + colors.green.underline(data["recipeUsed"]));

    // Compress recipe into smaller JSON object
    var packet = {};
    for (var i = 1; i <= parseInt(data["ingredientCount"], 10); i++) {
        var ingredient = data["ingredient" + i]
        packet[i] = {
            "a": ingredient["amountUsed"],
            "b": ingredient["liquorBottleNum"]
        }
    }

    // Send compressed recipe to Pump Arduino
    console.log(colors.yellow("  Sending recipe data to Arduino..."));
    var dataToWrite = JSON.stringify(packet);
    if (verbose) { console.log(colors.white("  Recipe data packet: " + dataToWrite)); }
    serialPortPump.write(dataToWrite, function(err, results) {
        if (err != null) { console.log(colors.red("  Write errors: " + err)); }

        // Listen for response from Arduino
        serialPortPump.on('data', function(responseData) {
            if (verbose) { console.log('  Response packet: ' + responseData); }

            // Close and reopen serial port around parsing
            serialPortPump.close(function(closeErr) {
                if (closeErr != null) { console.log(colors.red("  Port close error: " + closeErr)); }

                var responseObj = JSON.parse(responseData);
                serialPortPump.open(function(openErr) {
                    if (openErr != null) { console.log(colors.red("  Port reopen error: " + openErr)); }
                });

                // Check for success or failure
                if (responseObj["response"] == 1) {
                    console.log(colors.yellow("  Drink made successfully."));
                    resolve();
                } else {
                    console.log(colors.red("  Error making drink: " + responseObj["error"]));
                    reject(responseObj["error"]);
                }
            });
        });
    }); 
}

var ledPortOpen = function() {
    return new Promise(function(resolve, reject) {
        serialPortLed.on("open", function() {
            console.log(colors.blue.bgWhite("Serial port to LED Arduino open"));
            resolve();
        });
    });
}

var pumpPortOpen = function() {
    return new Promise(function(resolve, reject) {
        serialPortPump.on("open", function() {
            console.log(colors.blue.bgWhite("Serial port to Pump Arduino open"));
            resolve();
        });
    });
}

// Find and open serial ports to Arduinos
var promises = [];
SerialPort.list(function(err, ports) {
    ports.forEach(function(port) {
        if (port.serialNumber != undefined) {
            serialNum = port.serialNumber;
            serialNum = serialNum.replace("Arduino__www.arduino.cc__0043_", "");

            if (port.serialNumber == ledArduinoSerialNum) {
                var comName = port.comName;
                comName = comName.replace("cu", "tty");

                // Open serial port to LED Arduino
                serialPortLed = new SerialPort.SerialPort(comName, {
                    baudrate: serialBaud,
                    parser: SerialPort.parsers.readline("\n")
                });
                promises.push(ledPortOpen());
            } else if (port.serialNumber == pumpArduinoSerialNum) {
                var comName = port.comName;
                comName = comName.replace("cu", "tty");

                // Open serial port to LED Arduino
                serialPortPump = new SerialPort.SerialPort(comName, {
                    baudrate: serialBaud,
                    parser: SerialPort.parsers.readline("\n")
                });
                promises.push(pumpPortOpen());
            }   
        }
    });

    // Requires that both Arduinos be connected, exits otherwise
    if (serialPortLed == null) {
        console.log(colors.red("LED Arduino not connected"));
        process.exit(1);
    } else if (serialPortPump == null) {
        console.log(colors.red("Pump Arduino not connected"));
        process.exit(1);
    }
});

// Waits for both serial ports to open, then starts up the Firebase queue listener
Promise.all(promises).then(function() {
    var queue = new Queue(queueRef, firebaseListener);
});
