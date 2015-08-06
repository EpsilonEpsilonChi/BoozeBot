var SerialPort = require("serialport");
var colors     = require("colors/safe");
var Queue      = require("firebase-queue");
    Firebase   = require("firebase");

// Configuration variables
var verbose = true;

var queueRef = new Firebase('https://boozebot.firebaseio.com/drinkQueue');

var serialPort = new SerialPort.SerialPort("/dev/tty.usbmodem1421", {
    baudrate: 9600,
    parser: SerialPort.parsers.readline("\n")
});

serialPort.on("open", function() {
    console.log(colors.blue.bgWhite("Serial port open"));

    // Waiting for new drinks sitting in queue and pours them
    var queue = new Queue(queueRef, function(data, progress, resolve, reject) {
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

        // Send compressed recipe to Arduino
        console.log(colors.yellow("  Sending recipe data to Arduino..."));
        var dataToWrite = JSON.stringify(packet);
        if (verbose) { console.log(colors.white("  Recipe data packet: " + dataToWrite)); }
        serialPort.write(dataToWrite, function(err, results) {
            if (err != null) { console.log(colors.red("  Write errors: " + err)); }

            // Listen for response from Arduino
            serialPort.on('data', function(responseData) {
                if (verbose) { console.log('  Response packet: ' + responseData); }

                // Close and reopen serial port around parsing
                serialPort.close(function(closeErr) {
                    if (closeErr != null) { console.log(colors.red("  Port close error: " + closeErr)); }

                    var responseObj = JSON.parse(responseData);
                    serialPort.open(function(openErr) {
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
    });
});
