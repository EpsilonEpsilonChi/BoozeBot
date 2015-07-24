#include <aJSON.h>

// Work on concurrent pouring later

// Bottle relay pin definitions
#define BOTTLE1 2
#define BOTTLE2 3
#define BOTTLE3 4
#define BOTTLE4 5
#define BOTTLE5 6
#define BOTTLE6 7
#define BOTTLE7 8
#define BOTTLE8 9
#define BOTTLE9 10
#define BOTTLE10 11
#define BOTTLE11 12
#define BOTTLE12 13
#define BOTTLE13 14
#define BOTTLE14 15
#define BOTTLE15 16

// To allow relay to be NC or NO
#define RELAYOPEN HIGH
#define RELAYCLOSED LOW

int msToPourFlOz = 2200		// Time it takes to pour 1 fl oz (in ms)

aJsonStream serial_stream(&Serial);

void setup() {
	Serial.begin(9600);

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
	pinMode(BOTTLE15, OUTPUT);
}

int bottleNumToPin(int bottleNum) {
	if (bottleNum == 1) { return BOTTLE1;
	} else if (bottleNum == 2) { return BOTTLE2;
	} else if (bottleNum == 3) { return BOTTLE3; 
	} else if (bottleNum == 4) { return BOTTLE4; 
	} else if (bottleNum == 5) { return BOTTLE5; 
	} else if (bottleNum == 6) { return BOTTLE6; 
	} else if (bottleNum == 7) { return BOTTLE7;
	} else if (bottleNum == 8) { return BOTTLE8; 
	} else if (bottleNum == 9) { return BOTTLE9; 
	} else if (bottleNum == 10) { return BOTTLE10; 
	} else if (bottleNum == 11) { return BOTTLE11;
	} else if (bottleNum == 12) { return BOTTLE12;
	} else if (bottleNum == 13) { return BOTTLE13; 
	} else if (bottleNum == 14) { return BOTTLE14; 
	} else { return BOTTLE15; }
}

void pourLiquor(int bottleNum, float amount) {
	digitalWrite(bottleNumToPin(bottleNum), RELAYOPEN);
	delay(amount);
	digitalWrite(bottleNumToPin(bottleNum), RELAYCLOSED);
}

void processMessage(aJsonObject *msg) {

}

void loop() {

	// Skip any accidental whitespace
	if (serial_stream.available()) {
		serial_stream.skip();
	}

	// Read input
	if (serial_stream.available()) {
		aJsonObject *msg = aJson.parse(&serial_stream);
		processMessage(msg);
		aJson.deleteItem(msg);
	}
}