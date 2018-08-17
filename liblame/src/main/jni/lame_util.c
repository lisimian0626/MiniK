#include "lame_3.99.5_libmp3lame/lame.h"
#include "com_czt_mp3recorder_util_LameUtil.h"
#include <stdio.h>
#include<android/log.h>
#include <jni.h>
#define LOG_TAG "lamelog"
#define LOGI(fmt, args...)
//__android_log_print(ANDROID_LOG_INFO, LOG_TAG, fmt, ##args)
#define         MAX_U_32_NUM            0xFFFFFFFF
typedef long double ieee854_float80_t;
typedef double      ieee754_float64_t;
typedef float       ieee754_float32_t;
#define INT_MIN     (-2147483647 - 1) /* minimum (signed) int value */
#define INT_MAX       2147483647    /* maximum (signed) int value */
static lame_global_flags *lame = NULL;

struct PcmBuffer {
	void   *ch[2];           /* buffer for each channel */
	int     w;               /* sample width */
	int     n;               /* number samples allocated */
	int     u;               /* number samples used */
	int     skip_start;      /* number samples to ignore at the beginning */
	int     skip_end;        /* number samples to ignore at the end */
};

typedef struct PcmBuffer PcmBuffer;
typedef enum ByteOrder { ByteOrderLittleEndian, ByteOrderBigEndian } ByteOrder;

typedef struct RawPCMConfig
{
	int     in_bitwidth;
	int     in_signed;
	ByteOrder in_endian;
} RawPCMConfig;
RawPCMConfig global_raw_pcm =
		{ /* in_bitwidth */ 16
				, /* in_signed   */ -1
				, /* in_endian   */ ByteOrderLittleEndian
		};
static void
initPcmBuffer(PcmBuffer * b, int w)
{
	b->ch[0] = 0;
	b->ch[1] = 0;
	b->w = w;
	b->n = 0;
	b->u = 0;
	b->skip_start = 0;
	b->skip_end = 0;
}

static void
freePcmBuffer(PcmBuffer * b)
{
	if (b != 0) {
		free(b->ch[0]);
		free(b->ch[1]);
		b->ch[0] = 0;
		b->ch[1] = 0;
		b->n = 0;
		b->u = 0;
	}
}

static int
addPcmBuffer(PcmBuffer * b, void *a0, void *a1, int read)
{
	int     a_n;

	if (b == 0) {
		return 0;
	}
	if (read < 0) {
		return b->u - b->skip_end;
	}
	if (b->skip_start >= read) {
		b->skip_start -= read;
		return b->u - b->skip_end;
	}
	a_n = read - b->skip_start;

	if (b != 0 && a_n > 0) {
		int const a_skip = b->w * b->skip_start;
		int const a_want = b->w * a_n;
		int const b_used = b->w * b->u;
		int const b_have = b->w * b->n;
		int const b_need = b->w * (b->u + a_n);
		if (b_have < b_need) {
			b->n = b->u + a_n;
			b->ch[0] = realloc(b->ch[0], b_need);
			b->ch[1] = realloc(b->ch[1], b_need);
		}
		b->u += a_n;
		if (b->ch[0] != 0 && a0 != 0) {
			char   *src = a0;
			char   *dst = b->ch[0];
			memcpy(dst + b_used, src + a_skip, a_want);
		}
		if (b->ch[1] != 0 && a1 != 0) {
			char   *src = a1;
			char   *dst = b->ch[1];
			memcpy(dst + b_used, src + a_skip, a_want);
		}
	}
	b->skip_start = 0;
	return b->u - b->skip_end;
}

static int
takePcmBuffer(PcmBuffer * b, void *a0, void *a1, int a_n, int mm)
{
	if (a_n > mm) {
		a_n = mm;
	}
	if (b != 0 && a_n > 0) {
		int const a_take = b->w * a_n;
		if (a0 != 0 && b->ch[0] != 0) {
			memcpy(a0, b->ch[0], a_take);
		}
		if (a1 != 0 && b->ch[1] != 0) {
			memcpy(a1, b->ch[1], a_take);
		}
		b->u -= a_n;
		if (b->u < 0) {
			b->u = 0;
			return a_n;
		}
		if (b->ch[0] != 0) {
			memmove(b->ch[0], (char *) b->ch[0] + a_take, b->w * b->u);
		}
		if (b->ch[1] != 0) {
			memmove(b->ch[1], (char *) b->ch[1] + a_take, b->w * b->u);
		}
	}
	return a_n;
}
typedef struct get_audio_global_data_struct {
	int     count_samples_carefully;
	int     pcmbitwidth;
	int     pcmswapbytes;
	int     pcm_is_unsigned_8bit;
	int     pcm_is_ieee_float;
	unsigned int num_samples_read;
	//FILE   *music_in;
	//SNDFILE *snd_file;
	hip_t   hip;
	PcmBuffer pcm32;
	PcmBuffer pcm16;
	size_t  in_id3v2_size;
	unsigned char* in_id3v2_tag;
	int buf_readed;
} get_audio_global_data;

static get_audio_global_data global;

void init_global()
{
	global. count_samples_carefully = 0;
	global. num_samples_read = 0;
	global. pcmbitwidth =16;// global_raw_pcm.in_bitwidth;
	global. pcmswapbytes =0;// global_reader.swapbytes;
	global. pcm_is_unsigned_8bit = global_raw_pcm.in_signed == 1 ? 0 : 1;
	global. pcm_is_ieee_float = 0;
	global. hip = 0;
	//global. music_in = 0;
	//global. snd_file = 0;
	global. in_id3v2_size = 0;
	global. in_id3v2_tag = 0;
}
JNIEXPORT void JNICALL Java_com_czt_mp3recorder_util_LameUtil_init(
		JNIEnv *env, jclass cls, jint inSamplerate, jint inChannel, jint outSamplerate, jint outBitrate, jint quality) {
	if (lame != NULL) {
		lame_close(lame);
		lame = NULL;
	}
	lame = lame_init();
	lame_set_in_samplerate(lame, inSamplerate);
	lame_set_num_channels(lame, inChannel);//输入流的声道
	lame_set_out_samplerate(lame, outSamplerate);
	lame_set_brate(lame, outBitrate);
	lame_set_quality(lame, quality);

id3tag_init(lame);
lame_set_findReplayGain(lame, 1);
lame_set_num_samples(lame, 0);
lame_set_mode(lame, NOT_SET);
//
init_global();
initPcmBuffer(&global.pcm32, sizeof(int));
initPcmBuffer(&global.pcm16, sizeof(short));
global. pcm16.skip_start = global.pcm32.skip_start = 0;
global. pcm16.skip_end = global.pcm32.skip_end = 0;
lame_set_write_id3tag_automatic(lame, 0);
	lame_init_params(lame);


}

void get_left_right(const void * buffer, int len,void* outBuffer, int channelmode) {

	//int *PpcmData = (int*)buffer;
	//int *buffer_end = (int*)buffer + (len/4);
	short *PpcmData = (short*)buffer;
	short *outPcmData = (short*)outBuffer;
	short *buffer_end = (short*)buffer + (len/2);
	switch(channelmode) {
		case 1://left sound
			while(PpcmData<buffer_end) {
				*outPcmData= *PpcmData;
				*(outPcmData+1) = *PpcmData;
				PpcmData = PpcmData+2;
				outPcmData = outPcmData+2;
			}

			break;

		case 2://right sound
			while(PpcmData<buffer_end) {
				*outPcmData= *(PpcmData+1);
				*(outPcmData+1)= *(PpcmData+1);
				PpcmData = PpcmData+2;
				outPcmData = outPcmData+2;
			}

			break;


	}
}

void cov_left_right(const void * buffer, int len,void* leftBuffer, void* rigthBuffer) {

	//int *PpcmData = (int*)buffer;
	//int *buffer_end = (int*)buffer + (len/4);
	short *PpcmData = (short*)buffer;
	short *leftData = (short*)leftBuffer;
	short *rigthData = (short*)rigthBuffer;
	short *buffer_end = (short*)buffer + (len/2);

			while(PpcmData<buffer_end) {
				*leftData= *PpcmData;
				*(leftData+1)=  *PpcmData;

				*rigthData=  *(PpcmData+1);
				*(rigthData+1)=  *(PpcmData+1);

				rigthData =rigthData+2;
				leftData = leftData+2;
				PpcmData = PpcmData+2;
			}

}
static int
unpack_read_samples(const int samples_to_read, const int bytes_per_sample,
					const int swap_order, int *sample_buffer, const short* srcbuffer,int bread, const int samples)
{
	size_t  samples_read;
	int     i;
	int    *op;              /* output pointer */
	unsigned char *ip = (unsigned char *) sample_buffer; /* input pointer */
	const int b = sizeof(int) * 8;
	const char* sbuffer = (const char*)srcbuffer;

#define GA_URS_IFLOOP( ga_urs_bps ) \
    if( bytes_per_sample == ga_urs_bps ) \
      for( i = samples_read * bytes_per_sample; (i -= bytes_per_sample) >=0;)
	//bytes_per_sample*
	samples_read = samples_to_read;//fread(sample_buffer, bytes_per_sample, samples_to_read, pcm_in);
	if(samples_read+bread >samples )
		samples_read = samples-bread;
	memcpy(sample_buffer,sbuffer+(bytes_per_sample*bread),bytes_per_sample*samples_read);
	///
	op = sample_buffer + samples_read;

	if (swap_order == 0) {
		GA_URS_IFLOOP(1)
				* --op = ip[i] << (b - 8);
		GA_URS_IFLOOP(2)
				* --op = ip[i] << (b - 16) | ip[i + 1] << (b - 8);
		GA_URS_IFLOOP(3)
				* --op = ip[i] << (b - 24) | ip[i + 1] << (b - 16) | ip[i + 2] << (b - 8);
		GA_URS_IFLOOP(4)
				* --op =
						ip[i] << (b - 32) | ip[i + 1] << (b - 24) | ip[i + 2] << (b - 16) | ip[i + 3] << (b -
																										  8);
	}
	else {
		GA_URS_IFLOOP(1)
				* --op = (ip[i] ^ 0x80) << (b - 8) | 0x7f << (b - 16); /* convert from unsigned */
		GA_URS_IFLOOP(2)
				* --op = ip[i] << (b - 8) | ip[i + 1] << (b - 16);
		GA_URS_IFLOOP(3)
				* --op = ip[i] << (b - 8) | ip[i + 1] << (b - 16) | ip[i + 2] << (b - 24);
		GA_URS_IFLOOP(4)
				* --op =
						ip[i] << (b - 8) | ip[i + 1] << (b - 16) | ip[i + 2] << (b - 24) | ip[i + 3] << (b -
																										 32);
	}
#undef GA_URS_IFLOOP
	if (global.pcm_is_ieee_float) {
		ieee754_float32_t const m_max = INT_MAX;
		ieee754_float32_t const m_min = -(ieee754_float32_t) INT_MIN;
		ieee754_float32_t *x = (ieee754_float32_t *) sample_buffer;
//		assert(sizeof(ieee754_float32_t) == sizeof(int));
		for (i = 0; i < samples_to_read; ++i) {
			ieee754_float32_t const u = x[i];
			int     v;
			if (u >= 1) {
				v = INT_MAX;
			}
			else if (u <= -1) {
				v = INT_MIN;
			}
			else if (u >= 0) {
				v = (int) (u * m_max + 0.5f);
			}
			else {
				v = (int) (u * m_min - 0.5f);
			}
			sample_buffer[i] = v;
		}
	}
	return (samples_read);
}



/************************************************************************
*
* read_samples()
*
* PURPOSE:  reads the PCM samples from a file to the buffer
*
*  SEMANTICS:
* Reads #samples_read# number of shorts from #musicin# filepointer
* into #sample_buffer[]#.  Returns the number of samples read.
*
************************************************************************/

static int
read_samples_pcm(  int sample_buffer[2304], int samples_to_read,const short* srcbuffer,const int bread, const int samples)
{
	int     samples_read;
	int     bytes_per_sample = global.pcmbitwidth / 8;
	int     swap_byte_order; /* byte order of input stream */

	switch (global.pcmbitwidth) {
		case 32:
		case 24:
		case 16:
			if (global_raw_pcm.in_signed == 0) {
				//if (global_ui_config.silent < 10) {
				//	error_printf("Unsigned input only supported with bitwidth 8\n");
			//	}
				return -1;
			}
			swap_byte_order = (global_raw_pcm.in_endian != ByteOrderLittleEndian) ? 1 : 0;
			if (global.pcmswapbytes) {
				swap_byte_order = !swap_byte_order;
			}
			break;

		case 8:
			swap_byte_order = global.pcm_is_unsigned_8bit;
			break;

		default:
//			if (global_ui_config.silent < 10) {
//				error_printf("Only 8, 16, 24 and 32 bit input files supported \n");
//			}
			return -1;
	}
	samples_read = unpack_read_samples(samples_to_read, bytes_per_sample, swap_byte_order,
									   sample_buffer, srcbuffer,bread,samples);
	/*if (ferror(musicin)) {
		if (global_ui_config.silent < 10) {
			error_printf("Error reading input file\n");
		}
		return -1;
	}*/

	return samples_read;
}

static int
get_audio_common(lame_t gfp, int buffer[2][1152], short buffer16[2][1152],const short* srcbuffer, const int samples)
{
	int     num_channels = lame_get_num_channels(gfp);
	int     insamp[2 * 1152];
	short   buf_tmp16[2][1152];
	int     samples_read;
	int     framesize;
	int     samples_to_read;
	unsigned int remaining, tmp_num_samples;
	int     i;
	int    *p;

	/*
     * NOTE: LAME can now handle arbritray size input data packets,
     * so there is no reason to read the input data in chuncks of
     * size "framesize".  EXCEPT:  the LAME graphical frame analyzer
     * will get out of sync if we read more than framesize worth of data.
     */

	samples_to_read = framesize = lame_get_framesize(gfp);
	//assert(framesize <= 1152);
LOGI("samples_to_read %d ",samples_to_read);
	/* get num_samples */
//	if (is_mpeg_file_format(global_reader.input_format)) {
//		tmp_num_samples = global_decoder.mp3input_data.nsamp;
//	}
//	else {
		tmp_num_samples = lame_get_num_samples(gfp);
//	}

	/* if this flag has been set, then we are carefull to read
     * exactly num_samples and no more.  This is useful for .wav and .aiff
     * files which have id3 or other tags at the end.  Note that if you
     * are using LIBSNDFILE, this is not necessary
     */
	if (global.count_samples_carefully) {
		if (global.num_samples_read < tmp_num_samples) {
			remaining = tmp_num_samples - global.num_samples_read;
		}
		else {
			remaining = 0;
		}
		if (remaining < (unsigned int) framesize && 0 != tmp_num_samples)
			/* in case the input is a FIFO (at least it's reproducible with
               a FIFO) tmp_num_samples may be 0 and therefore remaining
               would be 0, but we need to read some samples, so don't
               change samples_to_read to the wrong value in this case */
			samples_to_read = remaining;
	}
	LOGI("tmp_num_samples %d samples_to_read %d ",tmp_num_samples, samples_to_read);
		samples_read =
				read_samples_pcm(insamp, num_channels * samples_to_read,srcbuffer,global.buf_readed,samples);
LOGI("read pcm %d  readed %d ",samples_read, global.buf_readed);
	if (samples_read < 0) {
		return samples_read;
	}
	global.buf_readed +=samples_read;
	p = insamp + samples_read;
	samples_read /= num_channels;
	if (buffer != NULL) { /* output to int buffer */
		if (num_channels == 2) {
			for (i = samples_read; --i >= 0;) {
				buffer[1][i] = *--p;
				buffer[0][i] = *--p;
			}
		}
		else if (num_channels == 1) {
			memset(buffer[1], 0, samples_read * sizeof(int));
			for (i = samples_read; --i >= 0;) {
				buffer[0][i] = *--p;
			}
		}
//		else
//			assert(0);
	}
	else {          /* convert from int; output to 16-bit buffer */
		if (num_channels == 2) {
			for (i = samples_read; --i >= 0;) {
				buffer16[1][i] = *--p >> (8 * sizeof(int) - 16);
				buffer16[0][i] = *--p >> (8 * sizeof(int) - 16);
			}
		}
		else if (num_channels == 1) {
			memset(buffer16[1], 0, samples_read * sizeof(short));
			for (i = samples_read; --i >= 0;) {
				buffer16[0][i] = *--p >> (8 * sizeof(int) - 16);
			}
		}
//		else
//			assert(0);
	}

	/* if num_samples = MAX_U_32_NUM, then it is considered infinitely long.
       Don't count the samples */
	if (tmp_num_samples != MAX_U_32_NUM)
		global. num_samples_read += samples_read;

	return samples_read;
}
int
get_audio(lame_t gfp, int buffer[2][1152], const short* srcbuffer, const int samples)
{
	int     used = 0, read = 0;
	do {
		read = get_audio_common(gfp, buffer, NULL,srcbuffer,samples);
		used = addPcmBuffer(&global.pcm32, buffer[0], buffer[1], read);
	} while (used <= 0 && read > 0);
	if (read < 0) {
		return read;
	}
	//if (global_reader.swap_channel == 0)
	//	return takePcmBuffer(&global.pcm32, buffer[0], buffer[1], used, 1152);
	//else
		return takePcmBuffer(&global.pcm32, buffer[1], buffer[0], used, 1152);
}
int lame_encode_leftright(const short* srcbuffer,const int samples,void* mp3buf, const int buflen)
{
	int ret=0;
	unsigned char mp3buffer[LAME_MAXMP3BUFFER];
	int     Buffer[2][1152];
	int     iread, imp3, owrite=0,bread=0;
	global.buf_readed=0;
	do {
		/* read in 'iread' samples */
		iread = get_audio(lame, Buffer,srcbuffer,samples);
LOGI("iread %d ==global.buf_readed %d ==%d  ",iread,global.buf_readed,samples);
		if (iread >= 0) {

			/* encode */
			imp3 = lame_encode_buffer_int(lame, Buffer[0], Buffer[1], iread,
										  mp3buffer, sizeof(mp3buffer));

			/* was our output buffer big enough? */
			if (imp3 < 0) {
				//if (imp3 == -1)
				//	error_printf("mp3 buffer is not big enough... \n");
			//	else
				//	error_printf("mp3 internal error:  error code=%i\n", imp3);
			//	return 1;
				break;
			}
			//owrite = (int) fwrite(mp3buffer, 1, imp3, outf);
			if(owrite+imp3>buflen)
				imp3 = buflen-owrite;
			if(imp3>0)
			{
				memcpy(mp3buf,mp3buffer,imp3);
				owrite += imp3;
				mp3buf = mp3buf+imp3;
			}

			/*if (owrite != imp3) {
				error_printf("Error writing mp3 output \n");
				return 1;
			}*/
		}

	} while (iread > 0);
ret = owrite;
	return ret;
}
JNIEXPORT jint JNICALL Java_com_czt_mp3recorder_util_LameUtil_encode(
		JNIEnv *env, jclass cls, jshortArray buffer_l, jshortArray buffer_r,
		jint samples, jbyteArray mp3buf ,jint flag) {
	jshort* j_buffer_l = (*env)->GetShortArrayElements(env, buffer_l, NULL);

	jshort* j_buffer_r = (*env)->GetShortArrayElements(env, buffer_r, NULL);

	const jsize mp3buf_size = (*env)->GetArrayLength(env, mp3buf);
	jbyte* j_mp3buf = (*env)->GetByteArrayElements(env, mp3buf, NULL);

	jshort* j_leftrigthpcmbuf;

	int result;// = lame_encode_buffer(lame, j_buffer_l, j_buffer_r,
//			samples, j_mp3buf, mp3buf_size);


	LOGI("==========encode==========");


	if(flag==2)
	{
		result=lame_encode_leftright(j_buffer_l,samples,j_mp3buf,mp3buf_size);
		//leftoutlen = samples;
		//j_leftrigthpcmbuf = (*env)->GetShortArrayElements(env, leftrigthpcmbuf, NULL);
		//get_left_right(j_buffer_l,samples,j_leftrigthpcmbuf,flag);

		//(*env)->ReleaseShortArrayElements(env, leftrigthpcmbuf, j_leftrigthpcmbuf, 0);
	}
	else
	{
		result = lame_encode_buffer(lame, j_buffer_l, j_buffer_r,
			samples, j_mp3buf, mp3buf_size);
	}
	(*env)->ReleaseShortArrayElements(env, buffer_l, j_buffer_l, 0);
	(*env)->ReleaseShortArrayElements(env, buffer_r, j_buffer_r, 0);
	(*env)->ReleaseByteArrayElements(env, mp3buf, j_mp3buf, 0);
	LOGI("==========encode==end===%d=====",result);
	return result;
}

jint JNICALL Java_com_czt_mp3recorder_util_LameUtil_encode2
(JNIEnv *env, jclass cls, jbyteArray pcmbuf,  jint samples, jbyteArray  mp3buf,jint flag)
{
		jbyte* j_bufferbuf = (*env)->GetByteArrayElements(env, pcmbuf, NULL);
		const jsize mp3buf_size = (*env)->GetArrayLength(env, mp3buf);
		jbyte* j_mp3buf = (*env)->GetByteArrayElements(env, mp3buf, NULL);
		int bufflen = samples/2;

		int result =0;
			if(flag ==2)
			{
				result=result=lame_encode_leftright(j_bufferbuf,bufflen,j_mp3buf,mp3buf_size);
			}else
			{
				result=lame_encode_buffer(lame, (short*)j_bufferbuf, (short*)j_bufferbuf,
										  bufflen, j_mp3buf, mp3buf_size);
			}

		(*env)->ReleaseByteArrayElements(env, pcmbuf, j_bufferbuf, 0);
		(*env)->ReleaseByteArrayElements(env, mp3buf, j_mp3buf, 0);
	return result;
}
JNIEXPORT jint JNICALL Java_com_czt_mp3recorder_util_LameUtil_flush(
		JNIEnv *env, jclass cls, jbyteArray mp3buf) {
	const jsize mp3buf_size = (*env)->GetArrayLength(env, mp3buf);
	jbyte* j_mp3buf = (*env)->GetByteArrayElements(env, mp3buf, NULL);

	int result = lame_encode_flush(lame, j_mp3buf, mp3buf_size);

	(*env)->ReleaseByteArrayElements(env, mp3buf, j_mp3buf, 0);

	return result;
}

JNIEXPORT jint JNICALL Java_com_czt_mp3recorder_util_LameUtil_writeXingFrame(
		JNIEnv *env, jclass cls, jbyteArray mp3buf) {
	const jsize mp3buf_size = (*env)->GetArrayLength(env, mp3buf);
	jbyte* j_mp3buf = (*env)->GetByteArrayElements(env, mp3buf, NULL);

	int result = lame_get_lametag_frame(lame, j_mp3buf, mp3buf_size);

	(*env)->ReleaseByteArrayElements(env, mp3buf, j_mp3buf, 0);

	return result;
}

JNIEXPORT void JNICALL Java_com_czt_mp3recorder_util_LameUtil_close
(JNIEnv *env, jclass cls) {
	lame_close(lame);
	lame = NULL;
}
