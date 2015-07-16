var ref             = new Firebase('https://boozebot.firebaseio.com');
var usersRef        = ref.child("Users");
var recipesRef      = ref.child("Recipes");
var bottlesRef      = ref.child("Bottles");
var transactionsRef = ref.child("Transactions")

var conversionRatio = 29.5735;

function addUser() {
  var usernameField = $('#username');
  var fullnameField  = $('#fullname');

  // Check to make sure user name doesn't exist already
  usersRef.child(usernameField.val()).once("value", function(userSnapshot) {
    var exists = (userSnapshot.val() !== null);     
    if (exists) {
      usernameField.val("Username already exists");
      return;
    } 
  });

  // Add user to Firebase
  userToAdd = {};
  userData = { "name": fullnameField.val() };
  userToAdd[usernameField.val()] = userData;
  usersRef.update(userToAdd);

  // Clear HTML input boxes
  usernameField.val('');
  fullnameField.val('');
}

function addRecipe() {
  var recipeNameField  = $('#recipeName');
  var ingredients = [$('#ingredient1'), $('#ingredient2'), $('#ingredient3'), $('#ingredient4'), $('#ingredient5'), $('#ingredient6'), $('#ingredient7')];
  var amounts = [$('#amount1'), $('#amount2'), $('#amount3'), $('#amount4'), $('#amount5'), $('#amount6'), $('#amount7')];

  // Add entered ingredients to recipe and push recipe to Firebase
  recipeToAdd = {};
  recipeData = {};
  for (var i = 1; i < ingredients.length + 1; i++) {
    if ((ingredients[i - 1].val() != '') && (amounts[i - 1].val() != '')) {   // Only add number of ingredients entered
      var floatAmount = parseFloat(amounts[i - 1].val(), 10);
      if (!isNaN(floatAmount)) {                                              // Make sure amount is a float (or int)
        recipeData["ingredient" + i] = {
          "type": ingredients[i - 1].val(),
          "amount": amounts[i - 1].val()
        }
      } else {
        recipeNameField.val("All amounts must be of type int/float");
        return;
      }
    }
  }
  recipeToAdd[recipeNameField.val()] = recipeData;
  recipesRef.update(recipeToAdd);

  // Clear HTML input boxes
  recipeNameField.val('');
  for (var i = 0; i < ingredients.length; i++) {
    ingredients[i].val('');
    amounts[i].val('');
  }
}

function addBottle() {
  var typeField = $('#liquorType');
  var nameField = $('#liquorName');
  var proofField = $('#liquorProof');
  var priceField = $('#liquorPrice');
  var sizeField = $('#bottleSize');
  var locField = $('#bottleLoc');

  // Grab bottle snapshot
  bottlesRef.child(typeField.val()).once("value", function(bottleSnapshot) {
    // Check to make sure there isn't a bottle of the same type name already
    var exists = (bottleSnapshot.val() !== null);     
    if (exists) {
      typeField.val("Bottle of type " + typeField.val() + " already exists");
      return;
    } 

    // Add bottle to Firebase
    bottleToAdd = {};
    bottleData = {
      name: nameField.val(),
      proof: proofField.val(),
      price: priceField.val(),
      amountRemaining: sizeField.val(),
      bottleLoc: locField.val(),
      costPerFlOz: (conversionRatio / sizeField.val()) * priceField.val()
    };
    bottleToAdd[typeField.val()] = bottleData;
    bottlesRef.update(bottleToAdd);

    // Clear HTML input boxes
    typeField.val('');
    nameField.val('');
    proofField.val('');
    priceField.val('');
    sizeField.val('');
    locField.val('');
  });

  
}

function pourDrink() {
  var userPouringDrinkField = $('#userPouringDrink');
  var drinkToPourField      = $('#drinkToPour');

  var drinkCounter = 0;
  var ingredients = [];
  var ingredientCounter = 0;

  // Get drink recipe
  drinkToPourRef = recipesRef.child(drinkToPourField.val());
  drinkToPourRef.once("value", function(recipeSnapshot) {
    // Create transaction and store recipe used in transaction
    var curTransaction = {
        recipeUsed: recipeSnapshot.key(),
        totalCost: 0,
        numStandardDrinks: 0
    };

    // Get list of ingredients from recipe
    recipeSnapshot.forEach(function(ingredientSnapshot) {
      ingredients.push(ingredientSnapshot.val()); 

      // Get prices for ingredients based on current bottle prices and amount
      bottlesRef.child(ingredientSnapshot.val().type).once("value", function(bottleSnapshot) {
        // Check that liquor exists in Bottles
        var exists = (bottleSnapshot.val() !== null);     
        if (!exists) {
          drinkToPourField.val("No bottles of type " + ingredientSnapshot.val().type);
          return;
        } 

        // Check there is enough of the liquor left in the bottle
        if (parseFloat(bottleSnapshot.val().amountRemaining) < (parseFloat(ingredientSnapshot.val().amount) * conversionRatio)) {
          drinkToPourField.val("Not enough " + ingredientSnapshot.val().type + " left to make drink");
          return;
        }

        // Add line item to transaction
        var ingredientNum = ingredientCounter + 1;
        curTransaction["ingredient" + ingredientNum] = {
          "amountUsed": parseFloat(ingredientSnapshot.val().amount),
          "lineItemPrice": parseFloat(bottleSnapshot.val().costPerFlOz) * parseFloat(ingredientSnapshot.val().amount),
          "liquorName": bottleSnapshot.val().name
        };

        // Increment standard drink count, ingredient count, and total cost
        curTransaction.totalCost += ((parseFloat(ingredientSnapshot.val().amount) * (parseFloat(bottleSnapshot.val().proof) / 200)) * 2);
        ingredientCounter = ingredientCounter + 1;
        curTransaction.numStandardDrinks += (parseFloat(bottleSnapshot.val().costPerFlOz) * parseFloat(ingredientSnapshot.val().amount));
      });
    });

    usersRef.child(userPouringDrinkField.val()).once("value", function(userSnapshot) {
      // Check if username exists
      var exists = (userSnapshot.val() !== null);     
      if (!exists) {
        userPouringDrinkField.val("Username does not exist");
        return;
      } 

      // Add transaction to user
      transactionToAdd = {};
      transactionToAdd["Transactions"] = curTransaction;
      usersRef.child(userPouringDrinkField.val()).child("Transactions").push(curTransaction);

      // Clear HTML input boxes
      userPouringDrinkField.val('');
      drinkToPourField.val('');
    });
  });

  // ****** Instruct Arduino to pour drink? ******

}

