var colors   = require('colors/safe');
var arduino  = require('duino');
var Queue    = require('firebase-queue');
    Firebase = require('firebase');
    board 	 = new arduino.Board();

// Configuration variables
var verbose 		  = true;
var timeToPourOneFlOz = 2.2;
var arduinoPort		  = "/dev/cu.usbmodem14131"

// Create serial port
var serialPort = new SerialPort(arduinoPort, {
	baudrate: 9600,
	parser: serialport.parsers.readline("\n")
});

// Will instruct Arduino to pour the liquor
function pourLiquor(bottleNum, amount) {
    if (verbose) { console.log("  Pouring " + colors.yellow(amount) + "oz of liquor in bottle #" + colors.yellow(bottleNum)); }

}

// Wait for Arduino to initialize
board.on('ready', function() {
	// Set bottle relay pins
	board.pinMode(2, 'out');
	board.pinMode(3, 'out');
	board.pinMode(4, 'out');
	board.pinMode(5, 'out');
	board.pinMode(6, 'out');
	board.pinMode(7, 'out');
	board.pinMode(8, 'out');
	board.pinMode(9, 'out');
	board.pinMode(10, 'out');
	board.pinMode(11, 'out');
	board.pinMode(12, 'out');
	board.pinMode(13, 'out');
	board.pinMode(14, 'out');
	board.pinMode(15, 'out');
	board.pinMode(16, 'out');

	// Waiting for new drinks sitting in queue and pours them
	var queueRef = new Firebase('https://boozebot.firebaseio.com/drinkQueue');
	var queue = new Queue(queueRef, function(data, progress, resolve, reject) {
	    if (verbose) { console.log(colors.green("BoozeBot making drink: ") + colors.green.underline(data["recipeUsed"])); }
	    for (var attributeName in data) {
	        if (attributeName.substring(0, 10) == "ingredient") {
	            pourLiquor(data[attributeName]["liquorBottleNum"], data[attributeName]["amountUsed"]);
	        }
	    }
	    resolve();
	});
});
