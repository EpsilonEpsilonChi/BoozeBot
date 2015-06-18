// Main code running on BoozeBot Arduino

// ******** Will be changed to a .ino file shortly *********

/* 
Arduino Outputs:
  Analog:
    0  --> Bottle 1 Solenoid
    1  --> Bottle 2 Solenoid
    2  --> Bottle 3 Solenoid
    3  --> Bottle 4 Solenoid
    4  --> Bottle 5 Solenoid
    5  --> Bottle 6 Solenoid
  Digital:
    0  --> 
    1  --> 
    2  --> 
    3  --> TLC5940 GSCLK
    4  --> Bottle 7 Solenoid
    5  --> Bottle 8 Solenoid
    6  --> 
    7  --> TLC5940 SIN
    8  --> 
    9  --> TLC5940 XLAT
    10 --> TLC5940 BLANK
    11 --> TLC5940 SIN
    12 --> 
    13 --> TLC5940 SCLK
*/

#include "Tlc5940.h"
#include <unordered_map>

#define BOTTLE1 14
#define BOTTLE2 15
#define BOTTLE3 16
#define BOTTLE4 17
#define BOTTLE5 18
#define BOTTLE6 19
#define BOTTLE7 4
#define BOTTLE8 5

// Variables
unordered_map<string, int> liquors;      // Mapping of liquor names to bottle numbers
int timeForOneShot =  1200;              // Amount of time it takes to pour one shot with solenoid open in ms
bool solOpen       = HIGH;               // Sets default setting of solenoid
bool solClosed     = LOW;

void longIsland();
    
void setup() {
    pinMode(BOTTLE1, OUTPUT);
    pinMode(BOTTLE2, OUTPUT);
    pinMode(BOTTLE3, OUTPUT);
    pinMode(BOTTLE4, OUTPUT);
    pinMode(BOTTLE5, OUTPUT);
    pinMode(BOTTLE6, OUTPUT);
    pinMode(BOTTLE7, OUTPUT);
    pinMode(BOTTLE9, OUTPUT);

    Tlc.init();
    Serial.begin(9600);
}

void loop() {
  
    delay(25);
}

void longIsland() {
    digitalWrite(liquors["vodka"], solOpen);
    digitalWrite(liquors["gin"], solOpen);
    digitalWrite(liquors["tequila"], solOpen);
    digitalWrite(liquors["white_rum"], solOpen);
    digitalWrite(liquors["triple_sec"], solOpen);
    delay(timeForOneShot);
    digitalWrite(liquors["vodka"], solClosed);
    digitalWrite(liquors["gin"], solClosed);
    digitalWrite(liquors["tequila"], solClosed);
    digitalWrite(liquors["white_rum"], solClosed);
    digitalWrite(liquors["triple_sec"], solClosed);
}




