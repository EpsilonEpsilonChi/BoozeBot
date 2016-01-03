# Node server that simplifies communication with BoozeBot. 

## Routes: 
POST /queue_drink {user: "user_name", drink: "drink_name"}
* Adds the spcified drink to BoozeBot's drink queue for the specified user.  
* Adds the drink transaction to the user's transaction list.
* Checks that the recipe exists, that all ingredients in the recipe have enough
  remianing for the recipe, and that the user exists.
