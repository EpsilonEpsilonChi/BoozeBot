"use strict";

var express = require('express');
var bodyParser = require('body-parser');
var Firebase = require("firebase");

var ref = new Firebase('https://boozebot.firebaseio.com');
var recipesRef = ref.child("Recipes");
var usersRef        = ref.child("Users");
var bottlesRef      = ref.child("Bottles");
var queueRef        = new Firebase('https://boozebot.firebaseio.com/drinkQueue/tasks');

var conversionRatio = 29.5735;

var app = express();
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

function generateTimestamp() {
  var today = new Date();
  var dd = today.getDate();
  var mm = today.getMonth() + 1;
  var yyyy = today.getFullYear();
  var hh = today.getHours();
  var minmin = today.getMinutes();
  var ss = today.getSeconds();

  if (dd < 10) { dd = '0' + dd; }
  if (mm < 10) { mm = '0' + mm; }
  if (hh < 10) { hh = '0' + hh; }
  if (minmin < 10) { minmin = '0' + minmin; }
  if (ss < 10) { ss = '0' + ss; }

  return dd + "-" + mm + "-" + yyyy + " " + hh + ":" + minmin + ":" + ss;
}

// respond with "hello world" when a GET request is made to the homepage
app.get('/', function(req, res) {
  res.send('hello world');
});

app.post('/queue_drink', function(req, res) {
  var userName = req.body.user;
  var drinkName = req.body.drink;
  var errors = []; 

  if (!userName || !drinkName) {
    errors.push("Missing user or drink name!");
  } 
  console.log("Pouring " + drinkName + " for " + userName);

  // Get recipe
  recipesRef.child(drinkName).once("value", function(recipeSnapshot) {
    if (recipeSnapshot.val() === null) { 
      res.status(400).jsonp({"error":"Drink does not exist!"}); 
      return;
    }

    // Get transaction and store recipe used in transaction
    var curTransaction = {
      recipeUsed: drinkName,
      totalCost: 0, 
      numStandardDrinks: 0,
      username: userName
    };

    // Get list of ingredients from recipe
    var ingredients = [];
    var ingredientCounter = 1;
    recipeSnapshot.forEach(function(ingredientSnapshot) {
      var ingredient = ingredientSnapshot.val();
      var amount = parseFloat(ingredient.amount);

      ingredients.push(ingredient); 

      // Get price for ingredient from bottle.
      bottlesRef.child(ingredient.type).once("value", function(bottleSnapshot) {
        var bottle = bottleSnapshot.val();
        var costPerFlOz = parseFloat(bottle.costPerFlOz);

        if (bottle == null) { 
          res.status(400).jsonp({"error":"No more ${ingredient}!"}); 
          return;
        }

        // If there's not enough liquid in the bottle:
        if (parseFloat(bottle.amountRemaining) < amount * conversionRatio) {
          res.status(400).jsonp({"error":"Not enough ${ingredient} remaining"});
          return;
        }

        // Add line item to transaction
        curTransaction["ingredient" + ingredientCounter] = {
          "amountUsed": amount,
          "lineItemPrice": costPerFlOz * amount,
          "liquorName": bottle.name,
          "liquorBottleNum": bottle.bottleLoc
        };

        // *****DECREMENT AMOUNT LEFT IN BOTTLE***** 

        // Increment standard drink count, ingredient count, and total cost
        curTransaction.numStandardDrinks += (2 * amount * parseFloat(bottle.proof)) / 200;
        curTransaction.totalCost += costPerFlOz * amount;
        ingredientCounter += 1;
      });
    });

    usersRef.child(userName).once("value", function(userSnapshot) {
      var user = userSnapshot.val();
      // Check if username exists
      if (user === null) { 
        res.status(400).jsonp({"error": "Invalid User ${userName}"});
        return;
      }

      curTransaction.ingredientCount = ingredientCounter - 1; // account for naming scheme offset (ingredientCounter = 1 not 0 at init)
      curTransaction.timestamp = generateTimestamp();

      // Add transaction to user's transaction list
      var id = usersRef.child(userName).child("Transactions").push(curTransaction).name();
      // Add transaction to user's pending transaction list
      usersRef.child(userName).child("pendingTransactions").child(id).set(curTransaction);
      // Add drink transaction to queue
      console.log(curTransaction);
      queueRef.push(curTransaction);

      // This has to be in here because javascript is a fuck.
      console.log("Success!");
      res.status(200).send("success!");
    });
  });
});

var server_port = process.env.OPENSHIFT_NODEJS_PORT || 8080
var server_ip_address = process.env.OPENSHIFT_NODEJS_IP || '127.0.0.1'

app.set('port', server_port);
app.set('ip', server_ip_address);

app.listen(app.get('port'), app.get('ip'), function () {
  console.log( "Listening on " + server_ip_address + ", server_port " + server_port )
});
