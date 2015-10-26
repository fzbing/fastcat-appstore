package com.fastspider.fastcat.fragment;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.fastspider.fastcat.R;
import com.fastspider.fastcat.cache.ACache;
import com.fastspider.fastcat.dialog.SweetAlertDialog;
import com.fastspider.fastcat.lib.NetWorkUtil;

public class EveryDayEnglishFragment extends Fragment implements
		OnClickListener,
		android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener {
	TextView tv_english, tv_china;
	ImageView iv, iv_play;
	private MediaPlayer mMediaPlayer = new MediaPlayer();// ������Ƶ��
	int play_state;
	private boolean mPlayState; // ����״̬
	String dateline;
	String tts;
	private ProgressBar mDisplayVoiceProgressBar;
	private String strDate = "";
	private int year;
	private int month;
	private int day;
	SwipeRefreshLayout swipe;
	TextView voice_display_voice_time;
	LinearLayout voice_display_voice_layout;
	ACache mCache;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);// Ϊ����Fragment����ʾ���Ͻǵ�menu

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_everydayenglish,
				container, false);
		initView(view);
		
		
		return view;
	}

	private void initView(View view) {
		mCache = ACache.get(getActivity());
		try {
			ViewConfiguration mconfig = ViewConfiguration.get(getActivity());
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);

				menuKeyField.setBoolean(mconfig, false);
			}
		} catch (Exception ex) {
		}
		// numbercircleprogress_bar = (RoundProgressBar) view.
		// findViewById(R.id.numbercircleprogress_bar);
		swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
		swipe.setOnRefreshListener(this);
		// ����ˢ�µ���ʽ
		swipe.setColorSchemeResources(android.R.color.holo_red_light,
				android.R.color.holo_green_light,
				android.R.color.holo_blue_bright,
				android.R.color.holo_orange_light);
		mDisplayVoiceProgressBar = (ProgressBar) view
				.findViewById(R.id.voice_display_voice_progressbar);
		voice_display_voice_time = (TextView) view
				.findViewById(R.id.voice_display_voice_time);
		voice_display_voice_layout = (LinearLayout) view
				.findViewById(R.id.voice_display_voice_layout);
		voice_display_voice_layout.setOnClickListener(this);
		tv_english = (TextView) view.findViewById(R.id.tv_english);
		tv_china = (TextView) view.findViewById(R.id.tv_china);
		iv = (ImageView) view.findViewById(R.id.iv);
		iv.setOnClickListener(this);
		iv_play = (ImageView) view.findViewById(R.id.voice_display_voice_play);
		iv_play.setImageResource(R.drawable.globle_player_btn_play);
		// iv_play.setOnClickListener(this);

		// http://open.iciba.com/dsapi/?date=

		if (NetWorkUtil.networkCanUse(getActivity())) {
			getData("http://open.iciba.com/dsapi/?date=" + strDate);
		} else {
			String content = mCache.getAsString("content");
			tv_english.setText(content);
			String note = mCache.getAsString("note");
			tv_china.setText(note);
			String picture2 = mCache.getAsString("picture2");
			String picture = mCache.getAsString("picture");
			if (picture2.equals("http://cdn.iciba.com/news/word/")) {
				Ion.with(getActivity(), picture).withBitmap().intoImageView(iv);
			} else {
				Ion.with(getActivity(), picture2).withBitmap()
						.intoImageView(iv);
			}
		}

	}

	private void getData(String jsonurl) {
		if (isAdded() == true) {

			Ion.with(getActivity(), jsonurl).asJsonObject()
					.setCallback(new FutureCallback<JsonObject>() {

						@Override
						public void onCompleted(Exception e, JsonObject result) {
							if (e != null) {
								return;
							}

							String content = result.get("content")
									.getAsString();
							mCache.put("content", content);//
							tv_english.setText(content);
							String note = result.get("note").getAsString();
							mCache.put("note", note);//
							tv_china.setText(note);
							String picture2 = result.get("picture2")
									.getAsString();
							mCache.put("picture2", picture2);//
							String picture = result.get("picture")
									.getAsString();
							mCache.put("picture", picture);//
							if (picture2
									.equals("http://cdn.iciba.com/news/word/")) {
								Ion.with(getActivity(), picture).withBitmap()
										.intoImageView(iv);
								Log.e("picture", picture);
							} else {
								Ion.with(getActivity(), picture2).withBitmap()
										.intoImageView(iv);
							}

							dateline = result.get("dateline").getAsString();
							tts = result.get("tts").getAsString();

						}
					});
		}
	}

	@SuppressLint("SdCardPath")
	private void aa() {
		if (!NetWorkUtil.networkCanUse(getActivity())) {
			new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
					.setTitleText("��������ʧ��...").setContentText("����������������Ƿ���")
					.show();
			return;
		}
		HttpUtils http = new HttpUtils();

		try {
			if (tts.equals("") || tts == null) {
				getData("http://open.iciba.com/dsapi/?date=" + strDate);
			}
			@SuppressWarnings({ "rawtypes", "unused" })
			HttpHandler handler = http.download(tts, "/sdcard/zhidu/" + strDate
					+ ".mp3", true, // ���Ŀ���ļ����ڣ�����δ��ɵĲ��ּ������ء���������֧��RANGEʱ���������ء�
					false, // �������󷵻���Ϣ�л�ȡ���ļ���������ɺ��Զ�������
					new RequestCallBack<File>() {

						@Override
						public void onStart() {
							Log.e("onStart", "........start......");
						}

						@Override
						public void onLoading(long total, long current,
								boolean isUploading) {
							Log.e("onLoading", total + "=|=" + current);
							// pb.setMax((int)total);
							// pb.setProgress((int)current);
							Log.e("(int)current------------>", (int) current
									+ "");
						}

						@Override
						public void onSuccess(ResponseInfo<File> responseInfo) {
							Log.e("onSuccess", responseInfo.toString());
							Toast.makeText(getActivity(), "���ڻ�ȡ���緢��..", 1).show();

							play();
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							Log.e("onFailure", "........msg......" + msg);
						}
					});
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public String getStandardTime(long timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("ss", Locale.getDefault());
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
		Date date = new Date(timestamp * 1000);
		sdf.format(date);
		return sdf.format(date);
	}

	/**
	 * ����ת�� mm��ss��ʽ����
	 * 
	 * @param max
	 * @return
	 */
	public String converLongTimeToStr(long time) {
		int ss = 1000;
		int mi = ss * 60;
		int hh = mi * 60;

		long hour = (time) / hh;
		long minute = (time - hour * hh) / mi;
		long second = (time - hour * hh - minute * mi) / ss;

		String strHour = hour < 10 ? "0" + hour : "" + hour;
		String strMinute = minute < 10 ? "0" + minute : "" + minute;
		String strSecond = second < 10 ? "0" + second : "" + second;
		if (hour > 0) {
			return strHour + ":" + strMinute + ":" + strSecond;
		} else {
			return strMinute + ":" + strSecond;
		}
	}

	@SuppressLint("SdCardPath")
	private void play() {
		Log.e("dateline:", "" + dateline);
		// ����¼��
		if (!mPlayState) {
			mMediaPlayer = new MediaPlayer();
			try {
				// ���¼����·��
				mMediaPlayer.setDataSource("/sdcard/zhidu/" + strDate + ".mp3");

				// ׼��
				mMediaPlayer.prepare();
				// ����
				mMediaPlayer.start();
				voice_display_voice_time
						.setText(converLongTimeToStr(mMediaPlayer.getDuration())
								+ "��");
				// ���ʱ���޸Ľ���
				new Thread(new Runnable() {

					public void run() {
						mDisplayVoiceProgressBar.setMax(mMediaPlayer
								.getDuration());

						while (mMediaPlayer.isPlaying()) {

							mDisplayVoiceProgressBar.setProgress(mMediaPlayer
									.getCurrentPosition());
						}
					}
				}).start();
				// �޸Ĳ���״̬
				mPlayState = true;
				// �޸Ĳ���ͼ��
				// mDisplayVoicePlay
				// .setImageResource(R.drawable.globle_player_btn_stop);

				iv_play.setImageResource(R.drawable.globle_player_btn_stop);
				mMediaPlayer
						.setOnCompletionListener(new OnCompletionListener() {
							// ���Ž�������
							public void onCompletion(MediaPlayer mp) {
								// ֹͣ����
								mMediaPlayer.stop();
								// �޸Ĳ���״̬
								mPlayState = false;
								// �޸Ĳ���ͼ��
								iv_play.setImageResource(R.drawable.globle_player_btn_play);
								mDisplayVoiceProgressBar.setProgress(0);

							}
						});

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			if (mMediaPlayer != null) {
				// ��ݲ���״̬�޸���ʾ����
				if (mMediaPlayer.isPlaying()) {
					mPlayState = false;
					mMediaPlayer.stop();
					mDisplayVoiceProgressBar.setProgress(0);
					iv_play.setImageResource(R.drawable.globle_player_btn_play);
				} else {
					mPlayState = false;
					iv_play.setImageResource(R.drawable.globle_player_btn_play);
					mDisplayVoiceProgressBar.setProgress(0);
				}
			}
		}

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getData("http://open.iciba.com/dsapi/?date=" + strDate);
	}

	@SuppressLint("SdCardPath")
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		switch (arg0.getId()) {
		case R.id.voice_display_voice_layout:
			File file = new File("/sdcard/zhidu/" + strDate + ".mp3");
			if (file.exists()) {
				play();
			} else {
				aa();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {

			return true;
		} else {
			
			Calendar cal = Calendar.getInstance();
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH);
			day = cal.get(Calendar.DAY_OF_MONTH);
			DatePickerDialog dpd = new DatePickerDialog(getActivity(),
					Datelistener, year, month, day);

			dpd.show();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getActivity().getMenuInflater().inflate(R.menu.time, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	private DatePickerDialog.OnDateSetListener Datelistener = new DatePickerDialog.OnDateSetListener() {
		/**
		 * params��view�����¼���������� params��myyear����ǰѡ����� params��monthOfYear����ǰѡ�����
		 * params��dayOfMonth����ǰѡ�����
		 */
		@Override
		public void onDateSet(DatePicker view, int myyear, int monthOfYear,
				int dayOfMonth) {

			// �޸�year��month��day�ı���ֵ���Ա��Ժ󵥻���ťʱ��DatePickerDialog����ʾ��һ���޸ĺ��ֵ
			year = myyear;
			month = monthOfYear;
			day = dayOfMonth;
			// ��������
			updateDate();

		}

		// ��DatePickerDialog�ر�ʱ������������ʾ
		private void updateDate() {
			// ��TextView����ʾ����
			int mm = month + 1;
			// showdate.setText("��ǰ���ڣ�"+year+"-"+(month+1)+"-"+day);
			strDate = year + "-" + mm + "-" + day;
			Calendar cal = Calendar.getInstance();
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH)+1;
			int d = cal.get(Calendar.DAY_OF_MONTH);
			String nowTime = y+"-"+m+"-"+d;
			if (year > y||year<=2013){
				new SweetAlertDialog(getActivity()).setTitleText("��ѯ��Χ:2014-1-1��"+nowTime)
				.show();
				return;
			}

			String da = "http://open.iciba.com/dsapi/?date=" + strDate;
			getData(da);
			voice_display_voice_time.setText("");
		}
	};

	@Override
	public void onRefresh() {

		new Handler().postDelayed(new Runnable() {
			public void run() {

				if (NetWorkUtil.networkCanUse(getActivity())) {
					Calendar cal = Calendar.getInstance();
					year = cal.get(Calendar.YEAR);
					month = cal.get(Calendar.MONTH);
					day = cal.get(Calendar.DAY_OF_MONTH);
					strDate = year + "-" + (month + 1) + "-" + day;
					getData("http://open.iciba.com/dsapi/?date=" + strDate);
					voice_display_voice_time.setText("");


				} else {
					Toast.makeText(getActivity(), "��������ʧ��..", 1).show();

				}

				swipe.setRefreshing(false);

			}
		}, 1500);
	}
}
