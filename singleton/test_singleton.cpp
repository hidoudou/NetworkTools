#include "singleton.h"
#include <stdio.h>


int main(){
	Singleton* singleton1 = Singleton::getInstance();
	Singleton* singleton2 = Singleton::getInstance();

	if (singleton1 == singleton2)
		fprintf(stderr,"singleton1 = singleton2\n");

	return 0;
}