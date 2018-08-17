#include "Score.h"

#include <math.h>
#include "util.h"
#include "musicalscale.h"

#include <android/log.h>

#define LOG_TAG    "jnilog" // 这个是自定义的LOG的标识，可用来定位
#undef LOG // 取消默认的LOG
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__) // 定义LOG类型
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__) // 定义LOG类型
#define LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__) // 定义LOG类型
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__) // 定义LOG类型
#define LOGF(...)  __android_log_print(ANDROID_LOG_FATAL,LOG_TAG,__VA_ARGS__) // 定义LOG类型

Score::Score(string filename)
:m_filename(filename)
,m_score(0)
{
	m_pAnalyzer = new Analyzer(44100, filename);
	load_notes();
}

Score::Score()
:m_score(0)
{
	m_pAnalyzer = new Analyzer(44100, "default");
}

Score::~Score()
{
	delete m_pAnalyzer;
	m_pAnalyzer = NULL;
}

void Score::set_notes(vector <Note> notes)
{
	m_notes_freq = notes;
	m_notes.clear();
	MusicalScale scale;
	// LOGI("set_notes size: %d", notes.size());
	for (size_t i=0;i<m_notes_freq.size();i++)
	{
		// LOGI("set_notes handle %d:", i);
		Note note;
		note.pos = m_notes_freq[i].pos;
		note.len = m_notes_freq[i].len;
		note.head = m_notes_freq[i].head;
		note.tail = m_notes_freq[i].tail;
		note.key = scale.setFreq(m_notes_freq[i].key).getNote(); // 频率到key;
		m_notes.push_back(note);
	}

    m_score = 0;
	m_time_score_map.clear();
}

bool Score::load_notes()
{
	FILE* fp;
	fp = fopen(m_filename.c_str(),"r");
	if(fp == NULL)
		return false;

	while(true)
	{
		Note note;
		fscanf(fp, "%f,%f,%d;\n", &note.pos, &note.len, &note.key);

		note.head = note.pos;
		note.tail = note.pos + note.len;

		if(note.pos == 0 && note.len == 0 && note.key == 0)
			break;
		else
			m_notes_freq.push_back(note);
	}

	fclose(fp);

	MusicalScale scale;
	for (size_t i=0;i<m_notes_freq.size();i++)
	{
		Note note;
		note.pos = m_notes_freq[i].pos;
		note.len = m_notes_freq[i].len;
		note.head = m_notes_freq[i].head;
		note.tail = m_notes_freq[i].tail;
		note.key = scale.setFreq(m_notes_freq[i].key).getNote(); // 频率到key;
		m_notes.push_back(note);
	}

	return true;
}

bool Score::clear_visual_data(void)
{
	m_red_time.clear();
	m_red_keys.clear();
	return true;
}

bool Score::get_visual_data(vector<double> &red_time, vector<double> &red_keys)
{
	red_time = m_red_time;
	red_keys = m_red_keys;
	return true;
}

bool Score::input_frames(const double* pData, const int from_pos, const int to_pos, const float from_sec, const float to_sec)
{
	map<float, int>::iterator it = m_time_score_map.find(from_sec);
	if (it != m_time_score_map.end()){
		return false;
	}

	// LOGI("from_pos %d to_pos %d from_sec %.4f to_sec %.4f err %.4f", from_pos, to_pos, from_sec, to_sec, error);
	m_pAnalyzer->input(pData + from_pos, pData + to_pos); // 4096 / 8 = 512
	m_pAnalyzer->process();
	Tone const* t = m_pAnalyzer->findTone();

	MusicalScale scale;
	// if (t && t->db > -getInf() && t->freq != 0) {
	if (t && t->db > -50.0 && t->freq >= 60 && t->freq <= 800) { // 音量限制，必须大于-51db, 频率限制，必须在60~800hz之间
		// 搜索这个tone所在时间范围内的note
		vector <Note> notes = this->get_notes(from_sec, to_sec);
		if (notes.size() == 0) {
//			LOGI("No note for analyzing");
			m_time_score_map.insert(make_pair(from_sec, m_score));
			return false; // 没有待分析的数据...跑
		}

//		LOGI("Got %d notes", notes.size());

		double noteVal = 0.0;
		if (notes.size() >= 2)
			noteVal = (notes[notes.size()-2].key + notes[notes.size()-1].key) * 0.5;
		else if(notes.size() == 1)
			noteVal = notes[0].key;
		else // 没有待分析的数据
		{
			m_time_score_map.insert(make_pair(from_sec, m_score));
			return false;
		}

		float toneKey = scale.setFreq(t->freq).getNote(); // 基音音调
		// 基音得分
		double get_score = notes[0].calc_score(toneKey, from_sec, to_sec);
		if (get_score > 0)
		{
			m_score += get_score;
			m_red_keys.push_back(toneKey); // 基音绘图
			m_red_time.push_back(from_sec); // 基音绘图
		}
	}else{
//		LOGI("No tone for analyzing");
	}

	m_time_score_map.insert(make_pair(from_sec, m_score));

	return true;
}

bool Score::input_frames_easy(const double* pData, const int from_pos, const int to_pos, const float from_sec, const float to_sec)
{
	map<float, int>::iterator it = m_time_score_map.find(from_sec);
	if (it != m_time_score_map.end()){
		return false;
	}

	// LOGI("from_pos %d to_pos %d from_sec %.4f to_sec %.4f err %.4f", from_pos, to_pos, from_sec, to_sec, error);
	m_pAnalyzer->input(pData + from_pos, pData + to_pos); // 4096 / 8 = 512
	m_pAnalyzer->process();
	Tone const* t = m_pAnalyzer->findTone();

	MusicalScale scale;
	// if (t && t->db > -getInf() && t->freq != 0) {
	if (t && t->db > -51.0 && t->freq >= 60 && t->freq <= 800) { // 音量限制，必须大于-51db, 频率限制，必须在60~800hz之间
		// 搜索这个tone所在时间范围内的note
		vector <Note> notes = this->get_notes(from_sec, to_sec);
		if (notes.size() == 0) {
//			LOGI("No note for analyzing");
			m_time_score_map.insert(make_pair(from_sec, m_score));
			return false; // 没有待分析的数据...跑
		}

//		LOGI("Got %d notes", notes.size());

		double noteVal = 0.0;
		if (notes.size() >= 2)
			noteVal = (notes[notes.size()-2].key + notes[notes.size()-1].key) * 0.5;
		else if(notes.size() == 1)
			noteVal = notes[0].key;
		else // 没有待分析的数据
		{
			m_time_score_map.insert(make_pair(from_sec, m_score));
			return false;
		}

		float toneKey = scale.setFreq(t->freq).getNote(); // 基音音调
		// 基音得分
		double get_score = notes[0].calc_score(toneKey, from_sec, to_sec);
		if (get_score > 0)
		{
			m_score += get_score;
			m_red_keys.push_back(toneKey); // 基音绘图
			m_red_time.push_back(from_sec); // 基音绘图
		}
	}else{
//		LOGI("No tone for analyzing");
	}

	m_time_score_map.insert(make_pair(from_sec, m_score));

	return true;
}

vector <Note> Score::get_notes(float from_sec, float to_sec)
{
	vector<Note> ret_notes;
	for (size_t i=0;i<m_notes.size();i++)
	{
		Note nti = m_notes[i];
		float from_cross = (from_sec - nti.pos) * (from_sec - (nti.pos + nti.len));
		float to_cross = (to_sec - nti.pos) * (to_sec - (nti.pos + nti.len));

		float head_cross = (from_sec - nti.head) * (to_sec - nti.head);
		float tail_cross = (from_sec - nti.tail) * (to_sec - nti.tail);

		if (from_cross < 0 || to_cross < 0 || (from_sec <= nti.head && nti.tail <= to_sec) || head_cross < 0 || tail_cross < 0 || (nti.head <= from_sec && to_sec <= nti.tail))
		{
			ret_notes.push_back(nti);
		}
	}

	return ret_notes;
}

int Score::frequency_to_key(double freq)
{
	return int(12 * log(freq/440.0) / log(double(2.0)) + 49);
}
