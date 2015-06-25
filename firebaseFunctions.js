var ref         = new Firebase('https://boozebot.firebaseio.com');
var usersRef    = ref.child("Users");
var recipesRef  = ref.child("Recipes");
var bottlesRef  = ref.child("Bottles");

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
  var recipeNameField = $('#recipeName');

  recipesRef.push({
    name: recipeNameField.val()
  })

  recipeNameField.val('');
}

function addBottle() {
  var typeField = $('#liquorType');
  var nameField = $('#liquorName');
  var proofField = $('#liquorProof');
  var priceField = $('#liquorPrice');
  var sizeField = $('#bottleSize');
  var locField = $('#bottleLoc');

  bottlesRef.push({
    type: typeField.val(),
    name: nameField.val(),
    proof: proofField.val(),
    price: priceField.val(),
    amountRemaining: sizeField.val(),
    bottleLoc: locField.val()
  });

  typeField.val('');
  nameField.val('');
  proofField.val('');
  priceField.val('');
  sizeField.val('');
  locField.val('');
}