#include "Tone.h"
#include "util.h"
#include <iomanip>

Tone::Tone() :
freq(0.0),
db(-getInf()),
stabledb(-getInf()),
age()
{
	// for (auto& h : harmonics) h = -getInf();
	for (int i=0;i<MAXHARM;i++)
	{
		harmonics[MAXHARM] = -getInf(); ///< Harmonics' levels
	}

}

void Tone::print() const {
	if (age < Tone::MINAGE) return;
	std::cout << fixed << setprecision(1) << freq << " Hz, age " << age << ", " << db << " dB:";
	for (size_t i = 0; i < 8; ++i) cout << " " << harmonics[i];
	cout << endl;
}

bool Tone::operator==(double f) const {
	return std::abs(freq / f - 1.0) < 0.05;
}
