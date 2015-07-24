var colors   = require('colors/safe');
var Gpio 	 = require('onoff').Gpio;
var Queue    = require('firebase-queue');
    Firebase = require('firebase');

// Relay open/closed constants
var RELAYOPEN 	= 1;
var RELAYCLOSED = 0;

// Relays for each bottle
var bottle1  = new Gpio(2,  'out');
var bottle2  = new Gpio(3,  'out');
var bottle3  = new Gpio(4,  'out');
var bottle4  = new Gpio(17, 'out');
var bottle5  = new Gpio(27, 'out');
var bottle6  = new Gpio(22, 'out');
var bottle7  = new Gpio(10, 'out');
var bottle8  = new Gpio(9,  'out');
var bottle9  = new Gpio(11, 'out');
var bottle10 = new Gpio(0,  'out');
var bottle11 = new Gpio(5,  'out');
var bottle12 = new Gpio(6,  'out');
var bottle13 = new Gpio(13, 'out');
var bottle14 = new Gpio(19, 'out');
var bottle15 = new Gpio(26, 'out');

// Array for indexing relays
var relays = [bottle1, bottle2, bottle3, bottle4, bottle5, bottle6, bottle7, bottle8, bottle9, bottle10, bottle11, bottle12, bottle13, bottle14, bottle15];

// Configuration variables
var verbose 		  = true;
var timeToPourOneFlOz = 2200;

// Will instruct Arduino to pour the liquor
function pourLiquor(bottleNum, amount) {
    if (verbose) { console.log("  Pouring " + colors.yellow(amount) + "oz of liquor in bottle #" + colors.yellow(bottleNum)); }

    relays[bottleNum - 1].write(RELAYOPEN, setTimeout(function() {
    	relays[bottleNum - 1].write(RELAYCLOSED);
    }, amount * timeToPourOneFlOz));
}

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
