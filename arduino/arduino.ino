#include <aJSON.h>

// Pin definitions
#define BOTTLERELAY1 2
#define BOTTLERELAY2 3
#define BOTTLERELAY3 4
#define BOTTLERELAY4 5
#define BOTTLERELAY5 6
#define BOTTLERELAY6 7
#define BOTTLERELAY7 8
#define BOTTLERELAY8 9
#define BOTTLERELAY9 10
#define BOTTLERELAY10 11
#define BOTTLERELAY11 12
#define BOTTLERELAY12 14
#define BOTTLERELAY13 15
#define BOTTLERELAY14 16
#define BOTTLERELAY15 17
#define POWERRELAY 18
#define ACTIVITYLED 13

#define PUMPON 1
#define PUMPOFF 2

int maxIngredients       = 7; 			// The max number of ingredients passed from Raspberry Pi in JSON object
double timeToPourOneFlOz = 2200;    // Time it takes to pour 1 fl oz (in milliseconds)

aJsonStream serial_stream(&Serial);

// ****** EDIT TO DISPENSE ALL INGREDIENTS AT THE SAME TIME

void setup() {
  Serial.begin(9600);

  pinMode(BOTTLERELAY1, OUTPUT);
  pinMode(BOTTLERELAY2, OUTPUT);
  pinMode(BOTTLERELAY3, OUTPUT);
  pinMode(BOTTLERELAY4, OUTPUT);
  pinMode(BOTTLERELAY5, OUTPUT);
  pinMode(BOTTLERELAY6, OUTPUT);
  pinMode(BOTTLERELAY7, OUTPUT);
  pinMode(BOTTLERELAY8, OUTPUT);
  pinMode(BOTTLERELAY9, OUTPUT);
  pinMode(BOTTLERELAY10, OUTPUT);
  pinMode(BOTTLERELAY11, OUTPUT);
  pinMode(BOTTLERELAY12, OUTPUT);
  pinMode(BOTTLERELAY13, OUTPUT);
  pinMode(BOTTLERELAY14, OUTPUT);
  pinMode(BOTTLERELAY15, OUTPUT);

  pinMode(POWERRELAY, OUTPUT);
  pinMode(ACTIVITYLED, OUTPUT);
}

// Selects proper pin constant based on bottle number
int selectPin(int bottleNum) {
  switch(bottleNum) {
    case 1:
      return BOTTLERELAY1;
    case 2:
      return BOTTLERELAY2;
    case 3:
      return BOTTLERELAY3;
    case 4:
      return BOTTLERELAY4;
    case 5:
      return BOTTLERELAY5;
    case 6:
      return BOTTLERELAY6;
    case 7:
      return BOTTLERELAY7;
    case 8:
      return BOTTLERELAY8;
    case 9:
      return BOTTLERELAY9;
    case 10:
      return BOTTLERELAY10;
    case 11:
      return BOTTLERELAY11;
    case 12:
      return BOTTLERELAY12;
    case 13:
      return BOTTLERELAY13;
    case 14:
      return BOTTLERELAY14;
    case 15:
      return BOTTLERELAY15;
  }
}

// Dispenses a certain amount of liquor
void dispenseLiquor(int amount, int bottleNum) {
  int liquorPin = selectPin(bottleNum);

  digitalWrite(liquorPin, PUMPON);
  delay(timeToPourOneFlOz);
  digitalWrite(liquorPin, PUMPOFF);
}

// Processes recipe transaction JSON data passed in
void processRecipe(aJsonObject *recipe) {
	// Check to make sure recipe exists
	if (!recipe) {
		Serial.println("processRecipe() called with no recipe data");
		return;
	}

	// Read ingredients from JSON transaction object
  aJsonObject *recipeName = aJson.getObjectItem(recipe, "recipeUsed");
  Serial.println("Pouring drink: " + recipeName->valuestring)
	for (int i = 1; i <= maxIngredients; ++i) {
		String ingredientString = "ingredient" + i;
		aJsonObject *ingredient = aJson.getObjectItem(recipe, ingredientString);

		if (!ingredient) {
			// No ingredient of that index 
			continue;
		} else {
      aJsonObject *amount = aJson.getObjectItem(ingredient, "amountUsed");
      aJsonObject *bottleNum = aJson.getObjectItem(ingredient, "liquorBottleNum"); 

      // Convert string values
      double amountFloat = (amount->valuestring).tofloat();
      int bottleNumInt = (bottleNum->valuestring).toInt();

      // Check type validity on amount
      if (amountFloat == 0) { 
        Serial.println("amountUsed of " + ingredient->valuestring + " not valid float or zero");
        return;
      }

      // Check type validity on bottleNum
      if (bottleNumInt == 0) {
        Serial.println("liquorBottleNum of " + ingredient->valuestring + " not valid float or zero");
        return;
      }

      dispenseLiquor(amountFloat, bottleNumInt);
		}
	}
}

void loop() {
  // Skip any accidental whitespace like newlines
  if (serial_stream.available()) {
    serial_stream.skip();
  }

  // Read recipe transaction from Serial
  if (serial_stream.available()) {
    aJsonObject *recipe = aJson.parse(&serial_stream);
    processRecipe(recipe);
    aJson.deleteItem(recipe);
  }

  // ********* SEND RESPONSES IF SUCCESSFUL *********

  delay(100);
}