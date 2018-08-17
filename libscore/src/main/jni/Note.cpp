#include "Note.h"
#include "util.h"

using namespace std;
Note::Note()
		:pos(0.0)
		,len(0.0)
		,key(0)
		,type(LIFT)
{

}

Note::~Note()
{

}


double Note::clampDuration(double b, double e) const {
	double len = max(e, tail) - min(b, head); // double len = min(b, head) - max(e, tail);
	return len > 0.0 ? len : 0.0;
}

double Note::calc_score(double n, double b, double e) const {
	return scoreMultiplier() * powerFactor(n) * clampDuration(b, e);
}

double Note::calc_score_easy(double n, double b, double e) const {
	return scoreMultiplier() * powerFactor(n) * clampDuration(b, e);
}

double Note::scoreMultiplier() const {
	switch(type) {
		case GOLDEN:
			return 2.0;
		case SLEEP:
			return 0.0;
		case FREESTYLE:
		case NORMAL:
		case SLIDE:
		case TAP:
		case HOLDBEGIN:
		case HOLDEND:
		case ROLL:
		case MINE:
		case LIFT:
			return 1.0;
	}
	return 0.0;
}

double Note::powerFactor(double note) const {
	if (type == FREESTYLE) return 1.0;
	double error = std::abs(diff_in(note));
	return clamp(1.5 - error, 0.0, 1.0);
}

double Note::powerFactorEasy(double note) const {
	if (type == FREESTYLE) return 1.0;
	double error = std::abs(diff_in(note));
	return clamp(1.6 - error, 0.0, 1.0);
}