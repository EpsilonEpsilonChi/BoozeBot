// Main code running on BoozeBot Arduino

/* 
Arduino Outputs:
  Analog:
    0  --> Bottle 1
    1  --> Bottle 2
    2  --> Bottle 3
    3  --> Bottle 4
    4  --> Bottle 5
    5  --> Bottle 6
  Digital:
    0  --> Bottle 7
    1  --> Bottle 8
    2  --> Bottle 9
    3  --> TLC5940 GSCLK
    4  --> Bottle 10
    5  --> Bottle 11
    6  --> Bottle 12
    7  --> TLC5940 SIN
    8  --> Bottle 13
    9  --> TLC5940 XLAT
    10 --> TLC5940 BLANK
    11 --> TLC5940 SIN
    12 --> Bottle 14
    13 --> TLC5940 SCLK
*/

#include "Tlc5940.h"
#include <String.h>
#include <vector>
#include <unordered_map>

using namespace std;

// Bottle pin definitions
#define BOTTLE1 14
#define BOTTLE2 15
#define BOTTLE3 16
#define BOTTLE4 17
#define BOTTLE5 18
#define BOTTLE6 19
#define BOTTLE7 0
#define BOTTLE8 1
#define BOTTLE9 2
#define BOTTLE10 4
#define BOTTLE11 5
#define BOTTLE12 6
#define BOTTLE13 8
#define BOTTLE14 12

// Constants
const int timeForOneFlOz = 1000;               // Amount of time it takes to pour one shot with solenoid open in ms
const bool pumpOn       = HIGH;                // Sets default setting of relay
const bool pumpOff      = LOW;

void decodeSerial(int input);
void dispenseShot(int bottle, int numberOfOunces);
void sendResponse();
    
void setup() {
  pinMode(BOTTLE1, OUTPUT); 
  pinMode(BOTTLE2, OUTPUT); 
  pinMode(BOTTLE3, OUTPUT); 
  pinMode(BOTTLE4, OUTPUT); 
  pinMode(BOTTLE5, OUTPUT); 
  pinMode(BOTTLE6, OUTPUT); 
  pinMode(BOTTLE7, OUTPUT); 
  pinMode(BOTTLE8, OUTPUT); 
  pinMode(BOTTLE9, OUTPUT); 
  pinMode(BOTTLE10, OUTPUT);
  pinMode(BOTTLE11, OUTPUT); 
  pinMode(BOTTLE12, OUTPUT);
  pinMode(BOTTLE13, OUTPUT); 
  pinMode(BOTTLE14, OUTPUT);   
  
  Tlc.init();
  Serial.begin(9600);
}

void loop() {
  if (Serial.available()) {
     decodeSerial(Serial.read() - '0');
  }
  
  delay(10);
}

void decodeSerial(int input) {
   
}

void dispenseShot(int bottle, int numberOfOunces) {
  digitalWrite(bottle, pumpOn);
  delay(numberOfOunces * timeForOneFlOz);
  digitalWrite(bottle, pumpOff);
}

void sendResponse() {
  
}


