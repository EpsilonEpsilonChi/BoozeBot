var colors   = require('colors/safe')
var Queue    = require('firebase-queue');
    Firebase = require('firebase');

// Will instruct Arduino to pour the liquor
function pourDrink(bottleNum, amount) {
    console.log("  Pouring " + colors.yellow(amount) + "oz of liquor in bottle #" + colors.yellow(bottleNum));
}

// Waiting for new drinks sitting in queue and pours them
var queueRef = new Firebase('https://boozebot.firebaseio.com/drinkQueue');
var queue = new Queue(queueRef, function(data, progress, resolve, reject) {
    console.log(colors.green("BoozeBot making drink: ") + colors.green.underline(data["recipeUsed"]));
    for (var attributeName in data) {
        if (attributeName.substring(0, 10) == "ingredient") {
            pourDrink(data[attributeName]["liquorBottleNum"], data[attributeName]["amountUsed"]);
        }
    }
    resolve();
});
