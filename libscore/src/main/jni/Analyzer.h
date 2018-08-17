/// analyzer class
/** class to analyze input audio and transform it into useable data
*/

#pragma once

#include <vector>
#include <complex>
#include <list>
#include <algorithm>

#include "Tone.h"
#include "RingBuffer.h"


static const unsigned FFT_P = 10;
static const std::size_t FFT_N = 1 << FFT_P;
static const std::size_t BUF_N = 2 * FFT_N;

using namespace std;
class Analyzer {
public:
	/// fast fourier transform vector
	typedef std::vector<std::complex<float> > fft_t;
	/// list of tones
	typedef std::list<Tone> tones_t;
	/// constructor
	Analyzer(double rate, std::string id, std::size_t step = 200);
	/** Add input data to buffer. This is thread-safe (against other functions). **/
	template <typename InIt> void input(InIt begin, InIt end) {
		size_t r = m_bufRead;  // The read position
		size_t w = m_bufWrite;  // The write position
		bool overflow = false;
		while (begin != end) {
			float s = *begin++;  // Read input sample
			// Peak level calculation
			float p = s * s;
			if (p > m_peak) m_peak = p; else m_peak *= 0.999;
			m_buf[w] = s;
			// Cursor updates
			w = (w + 1) % BUF_N;
			if (w == r) overflow = true;
		}
		m_bufWrite = w;
		if (overflow) m_bufRead = (w + 1) % BUF_N;  // Reset read pointer on overflow
	}
	/** Call this to process all data input so far. **/
	void process();
	/** Get the raw FFT. **/
	fft_t const& getFFT() const { return m_fft; }
	/** Get the peak level in dB (negative value, 0.0 = clipping). **/
	double getPeak() const { return 10.0 * log10(m_peak); }
	/** Get a list of all tones detected. **/
	tones_t const& getTones() const { return m_tones; }
	/** Find a tone within the singing range; prefers strong tones around 200-400 Hz. **/
	Tone const* findTone(double minfreq = 65.0, double maxfreq = 1000.0) const {
		if (m_tones.empty()) { m_oldfreq = 0.0; return NULL; }
		double db = std::max_element(m_tones.begin(), m_tones.end(), Tone::dbCompare)->db;
		Tone const* best = NULL;
		double bestscore = 0;
		for (tones_t::const_iterator it = m_tones.begin(); it != m_tones.end(); ++it) {
			if (it->db < db - 20.0 || it->freq < minfreq || it->age < Tone::MINAGE) continue;
			if (it->freq > maxfreq) break;
			double score = it->db - max(180.0, abs(it->freq - 300.0)) / 10.0;
			if (m_oldfreq != 0.0 && fabs(it->freq/m_oldfreq - 1.0) < 0.05) score += 10.0;
			if (best && bestscore > score) break;
			best = &*it;
			bestscore = score;
		}
		m_oldfreq = (best ? best->freq : 0.0);
		return best;
	}
	std::string const& getId() const { return m_id; }

private:
	std::size_t m_step;
	double m_rate;
	std::string m_id;
	std::vector<float> m_window;
	float m_buf[2 * BUF_N];
	volatile size_t m_bufRead, m_bufWrite;
	fft_t m_fft;
	std::vector<float> m_fftLastPhase;
	double m_peak;
	tones_t m_tones;
	mutable double m_oldfreq;
	bool calcFFT();
	void calcTones();
	void mergeWithOld(tones_t& tones) const;
};