# Node server that simplifies communication with BoozeBot. 

## Routes: 
POST /queue_drink {user: "user_name", drink: "drink_name"}
* Adds the specified drink to BoozeBot's drink queue for the specified user.  
* Adds the drink transaction to the user's transaction list.
* Checks that the recipe exists, that all ingredients in the recipe have enough
  remaining for the recipe, and that the user exists.

## To Deploy: 
1) cd into 'deployed' directory 
2) commit any changes to the openshift git repo in the 'deployed' dir
3) push changes -- ourput should indicate that the app was automatically
deployed.

## SSH:
ssh 56874b262d5271956d00015e@boozebot-boozebotapi.rhcloud.com
