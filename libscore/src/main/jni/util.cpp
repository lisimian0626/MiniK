#include "util.h"
#include <math.h>

#include <string>
#include <iostream>

//double remainder(double x, double y)
//{
//	int z = int(x/y);
//	if ((x - y * z) > y / 2){
//		return (x - y * z - y);
//	}
//	else{
//		return (x - y * z);
//	}
//}
//
//double round(double val)
//{
//	return (val> 0.0) ? floor(val+ 0.5) : ceil(val- 0.5);
//}

int LOrB(){
	union {
		unsigned short us;
		unsigned char uc[2];
	}un;
	un.us=0x0001;
	if(un.uc[0]==1)
		return 1;
	else if(un.uc[0]==0)
		return 0;
	else return -1;
}
//
//double framePosToTimePos(int framePos, int sample_frequency)
//{
//	//Ĭ��44100Hz����������16bit���������ĳ��Զ��Ѿ��ڼ���ʱ����
//	return double(framePos)/(sample_frequency*16.0/8.0*2);
//}