#ifndef _SINGLETON_H_
#define _SINGLETON_H_


class Singleton{
public:
	static Singleton* getInstance();

private:
	Singleton();
	//把复制构造函数和=操作符也设为私有,防止被复制
	Singleton(const Singleton&);
	Singleton& operator=(const Singleton&);

	static Singleton* instance;
};

#endif
 

#include "Singleton.h"


Singleton::Singleton(){

}


Singleton::Singleton(const Singleton&){

}


Singleton& Singleton::operator=(const Singleton&){

}


//在此处初始化
Singleton* Singleton::instance = new Singleton();
Singleton* Singleton::getInstance(){
	return instance;
}