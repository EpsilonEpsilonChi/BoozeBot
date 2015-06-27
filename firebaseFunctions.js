var ref             = new Firebase('https://boozebot.firebaseio.com');
var usersRef        = ref.child("Users");
var recipesRef      = ref.child("Recipes");
var bottlesRef      = ref.child("Bottles");
var transactionsRef = ref.child("Transactions")

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
    if ((ingredients[i - 1].val() != '') && (amounts[i - 1].val() != '')) {
      var floatAmount = parseFloat(amounts[i - 1].val(), 10);
      if (!isNaN(floatAmount)) {                              // Make sure amount is a float (or int)
        recipeData["ingredient" + i] = {
          "type": ingredients[i - 1].val(),
          "amount": amounts[i - 1].val()
        }
      } else {
        recipeNameField.val("All amounts must be of type int/float");
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

  var ingredients = []
  drinkToPourRef = recipesRef.child(drinkToPourField.val());
  drinkToPourRef.once("value", function(snapshot) {
    ingredients = [
      snapshot.val().ingredient1,
      snapshot.val().ingredient2,
      snapshot.val().ingredient3,
      snapshot.val().ingredient4,
      snapshot.val().ingredient5,
      snapshot.val().ingredient6,
      snapshot.val().ingredient7
    ];
  });

  var ingredientRefs = [
    bottlesRef.child(ingredients[0]),
    bottlesRef.child(ingredients[1]),
    bottlesRef.child(ingredients[2]),
    bottlesRef.child(ingredients[3]),
    bottlesRef.child(ingredients[4]),
    bottlesRef.child(ingredients[5]),
    bottlesRef.child(ingredients[6]),
  ];


  // Clear HTML input boxes
  drinkToPourField.val('')
}

