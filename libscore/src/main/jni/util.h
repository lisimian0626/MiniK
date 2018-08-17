#pragma once

//#include <cstdint>
#include <limits>
#include <vector>
#include <stdexcept>

#ifndef UTIL
#define UTIL

// static inline double remainder(double x, double y);
// static inline double round(double val);
static inline int LOrB();
static inline double framePosToTimePos(int framePos, int sample_frequency=44100) {return double(framePos)/(sample_frequency*16.0/8.0);};

/** Limit val to range [min, max] **/
template <typename T> T clamp(T val, T min = 0, T max = 1) {
	if (min > max) throw ("min > max");
	if (val < min) return min;
	if (val > max) return max;
	return val;
}

template <typename Numeric> struct MinMax {
	Numeric min, max;
	explicit MinMax(Numeric min = std::numeric_limits<Numeric>::min(), Numeric max = std::numeric_limits<Numeric>::max()): min(min), max(max) {}
	bool matches(Numeric val) const { return val >= min && val <= max; }
};

/** A convenient way for getting NaNs **/
static inline double getNaN() { return std::numeric_limits<double>::quiet_NaN(); }

/** A convenient way for getting infs **/
static inline double getInf() { return std::numeric_limits<double>::infinity(); }

static inline bool isPow2(unsigned int val) {
	if (val == 0) return false;
	if ((val & (val-1)) == 0) return true; // From Wikipedia: Power_of_two
	return false;
}

static inline unsigned int nextPow2(unsigned int val) {
	unsigned int ret = 1;
	while (ret < val) ret *= 2;
	return ret;
}

static inline unsigned int prevPow2(unsigned int val) {
	unsigned int ret = 1;
	while ((ret*2) < val) ret *= 2;
	return ret;
}

#endif
