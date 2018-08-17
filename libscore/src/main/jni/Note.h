#pragma once

#include <vector>
#include "util.h"

using namespace std;

class Note{
public:
	Note();
	~Note();
	static double diff(double note, double n) { return remainder(n - note, 12.0); }
	double diff_in(double note) const { return remainder(key - note, 12.0); }

	double clampDuration(double b, double e) const;
	double scoreMultiplier() const;
	double powerFactor(double note) const;
	double powerFactorEasy(double note) const;
	double calc_score(double n, double b, double e) const;
	double calc_score_easy(double n, double b, double e) const;

	float pos;
	float len;
	double head;
	double tail;
	int key;
	int freq;
	int score;

	double power;//打分的淡入淡出乘数

	enum Type { FREESTYLE = 'F', NORMAL = ':', GOLDEN = '*', SLIDE = '+', SLEEP = '-',
		TAP = '1', HOLDBEGIN = '2', HOLDEND = '3', ROLL = '4', MINE = 'M', LIFT = 'L'} type;
};