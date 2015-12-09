#include <aJSON.h>
#include <tlc594x.h>

void testSequence() {
	for (int i = 0; i < 3; ++i) {
		for (int j = 0; j < 12; ++j) {
			Tlc.set(j + i, 4096);
		}
		for (int j = 12; j > 0; j--) {
			Tlc.set(j + i, 0);
		}
	}
}


void setup() {
	Tlc.init();
	Serial.begin(9600);
}

void loop() {
	testSequence();
}
