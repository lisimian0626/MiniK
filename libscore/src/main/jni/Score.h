#pragma once

#include <string>
#include <vector>
#include <map>

#include "Note.h"
#include "Analyzer.h"

using namespace std;

class Score{
public:
	Score(string filename);
	Score();
	~Score();
    void set_notes(vector <Note> notes);
	vector <Note> get_notes(void ){ return m_notes;};
	vector <Note> get_notes(float from_sec, float to_sec );

	float get_score(void){return m_score;};
	
	bool input_frames(const double* pData, const int from_pos, const int to_pos, const float from_sec, const float to_sec);//������Ƶ����
	bool input_frames_easy(const double* pData, const int from_pos, const int to_pos, const float from_sec, const float to_sec);//������Ƶ����
	bool get_visual_data(vector<double> &red_time, vector<double> &red_keys);
	bool clear_visual_data(void);

private:
	int frequency_to_key(double freq);
	bool load_notes();//����һ��notes�ļ�

	map<float, int> m_time_score_map;
	vector <Note> m_notes;
	vector <Note> m_notes_freq;
	vector<double> m_red_time, m_red_keys;
	Analyzer* m_pAnalyzer;
	string m_filename;
	double m_score;
};