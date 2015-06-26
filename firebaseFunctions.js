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
  var ingredient1Field = $('#ingredient1');
  var ingredient2Field = $('#ingredient2');
  var ingredient3Field = $('#ingredient3');
  var ingredient4Field = $('#ingredient4');
  var ingredient5Field = $('#ingredient5');
  var ingredient6Field = $('#ingredient6');
  var ingredient7Field = $('#ingredient7');

  recipeToAdd = {};
  recipeData = {
    ingredient1: ingredient1Field.val(),
    ingredient2: ingredient2Field.val(),
    ingredient3: ingredient3Field.val(),
    ingredient4: ingredient4Field.val(),
    ingredient5: ingredient5Field.val(),
    ingredient6: ingredient6Field.val(),
    ingredient7: ingredient7Field.val()
  };
  recipeToAdd[recipeNameField.val()] = recipeData;

  recipesRef.update(recipeToAdd);

  recipeNameField.val('');
  ingredient1Field.val('');
  ingredient2Field.val('');
  ingredient3Field.val('');
  ingredient4Field.val('');
  ingredient5Field.val('');
  ingredient6Field.val('');
  ingredient7Field.val('');
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

// function pourDrink() {
//   var userPouringDrinkField = $('#userPouringDrink');
//   var drinkToPourField      = $('#drinkToPourField');

  
// }

