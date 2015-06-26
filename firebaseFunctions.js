var ref             = new Firebase('https://boozebot.firebaseio.com');
var usersRef        = ref.child("Users");
var recipesRef      = ref.child("Recipes");
var bottlesRef      = ref.child("Bottles");
var transactionsRef = ref.child("Transactions")

function addUser() {
  var firstnameField = $('#firstname');
  var lastnameField  = $('#lastname');

  usersRef.push({
    name: firstnameField.val() + " " + lastnameField.val()
  });

  firstnameField.val('');
  lastnameField.val('');
}

function addRecipe() {
  var recipeNameField  = $('#recipeName');
  var ingredients = [$('#ingredient1'), $('#ingredient2'), $('#ingredient3'), $('#ingredient4'), $('#ingredient5'), $('#ingredient6'), $('#ingredient7')];
  var amounts = [$('#amount1'), $('#amount2'), $('#amount3'), $('#amount4'), $('#amount5'), $('#amount6'), $('#amount7')];

  recipeToAdd = {};
  recipeData = {
    ingredient1: {"type": ingredients[0].val(), "amount": amounts[0].val()},
    ingredient2: {"type": ingredients[1].val(), "amount": amounts[1].val()},
    ingredient3: {"type": ingredients[2].val(), "amount": amounts[2].val()},
    ingredient4: {"type": ingredients[3].val(), "amount": amounts[3].val()},
    ingredient5: {"type": ingredients[4].val(), "amount": amounts[4].val()},
    ingredient6: {"type": ingredients[5].val(), "amount": amounts[5].val()},
    ingredient7: {"type": ingredients[6].val(), "amount": amounts[6].val()},
  };
  recipeToAdd[recipeNameField.val()] = recipeData;

  recipesRef.update(recipeToAdd);

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



  drinkToPourField.val('')
}

