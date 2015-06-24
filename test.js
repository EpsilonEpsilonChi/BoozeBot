var Firebase = require('firebase')

var ref 		= new Firebase('https://shining-torch-4136.firebaseio.com');
var usersRef 	= ref.child("Users")
var recipesRef	= ref.child("Recipes")
var bottlesRef  = ref.child("Bottles")

// usersRef.push({
// 	name: "Connor Mason",
// });

// bottlesRef.push({
// 	bottleLoc: 1,
// 	type: "vodka",
// 	name: "Smirnoff",
// 	proof: 80,
// 	amountRemaining: 750,
// 	price: 12.99
// })

bottlesRef.push({
	bottleLoc: 2,
	type: "lemon lime",
	name: "Sprite",
	proof: 0,
	amountRemaining: 2000,
	price: 1.19
})

recipesRef.push({
	name: "Vodka & Sprite",
	ingredients: {
		vodka: 3,
		sprite: 9
	}
})