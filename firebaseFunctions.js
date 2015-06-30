var ref             = new Firebase('https://boozebot.firebaseio.com');
var usersRef        = ref.child("Users");
var recipesRef      = ref.child("Recipes");
var bottlesRef      = ref.child("Bottles");
var transactionsRef = ref.child("Transactions")

var conversionRatio = 29.5735;

function addUser() {
  var firstnameField = $('#firstname');
  var lastnameField  = $('#lastname');

  // Add user to Firebase
  usersRef.push({
    name: firstnameField.val() + " " + lastnameField.val()
  });

  // Clear HTML input boxes
  firstnameField.val('');
  lastnameField.val('');
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

  // Add bottle to Firebase
  bottleToAdd = {};
  bottleData = {
    name: nameField.val(),
    proof: proofField.val(),
    price: priceField.val(),
    amountRemaining: sizeField.val(),
    bottleLoc: locField.val()
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
}

function pourDrink() {
  var userPouringDrinkField = $('#userPouringDrink');
  var drinkToPourField      = $('#drinkToPour');

  // Get drink recipe
  drinkToPourRef = recipesRef.child(drinkToPourField.val());
  drinkToPourRef.once("value", function(snapshot) {
    // Create transaction and store recipe used in transaction
    var curTransaction = {
      "recipeUsed": snapshot.key()
    };

    // Get list of ingredients from recipe
    var totalCost = 0;
    var drinkCounter = 0;
    var ingredients = [];
    var ingredientCounter = 0;
    snapshot.forEach(function(childSnapshot) {
      ingredients.push(childSnapshot.val()); 

      // Get prices for ingredients based on current bottle prices and amount
      bottlesRef.child(childSnapshot.val().type).once("value", function(bottleSnapshot) {
        var exists = (bottleSnapshot.val() !== null);     // Check that liquor exists in Bottles
        if (!exists) {
          drinkToPourField.val("No bottles of type " + childSnapshot.val().type);
          return;
        } else {
          // Check there is enough of the liquor left in the bottle

          // Calculate cost of line item and add to transaction
          var costOfItem = (bottleSnapshot.val().amountRemaining / conversionRatio) * childSnapshot.val().amount;   // Fix, calculate cost of amount
          curTransaction["ingredient" + ingredientCounter] = {
            "amountUsed": childSnapshot.val().amount,
            "lineItemPrice": costOfItem,
            "liquorName": bottleSnapshot.val().name
          };

          drinkCounter = drinkCounter + (bottleSnapshot.val().proof// Increment standard drink count



          ingredientCounter = ingredientCounter + 1;      // Increment ingredient count
        }
      });
    });
  });

  // ****** Instruct Arduino to pour drink? ******
}

