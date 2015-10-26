package com.fastspider.fastcat.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.fastspider.fastcat.Conf;
import com.fastspider.fastcat.R;
import com.fastspider.fastcat.bean.HomeBean;
import com.fastspider.fastcat.lib.FlipLayout;

public class HomeAdapter extends BaseAdapter {
	private List<HomeBean> homeList = new ArrayList<HomeBean>();
	private Context context;
	private HomeBean homeBean;
	ListView listView;

	public HomeAdapter(Context context, ListView listView) {
		this.context = context;
		this.listView = listView;
	}

	public void resetData(List<HomeBean> list) {
		this.homeList.clear();
		this.homeList.addAll(list);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return homeList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return homeList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_one, null);
			viewHolder = new ViewHolder();
			viewHolder.item_vickeytalk_tv = (TextView) convertView
					.findViewById(R.id.item_vickeytalk_tv);
			viewHolder.item_vickeytalk_iv = (ImageView) convertView
					.findViewById(R.id.item_vickeytalk_iv);
			
//			viewHolder.flipLayout= (FlipLayout) convertView.findViewById(R.id.flipLayout);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		homeBean = homeList.get(position);
		viewHolder.item_vickeytalk_tv.setText(homeBean.content);
		viewHolder.item_vickeytalk_tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				viewHolder.item_vickeytalk_iv.setVisibility(View.VISIBLE);
				viewHolder.item_vickeytalk_tv.setVisibility(View.GONE);
				
			}
		});
		viewHolder.item_vickeytalk_iv.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				int a = homeList.get(position).imgid;
				Log.e("",""+a);
				downImg(a);
				return false;
			}
		});
		Ion.with(context, homeBean.img).withBitmap()
				.intoImageView(viewHolder.item_vickeytalk_iv);

 
		return convertView;
	}

	private class ViewHolder {
		TextView item_vickeytalk_tv;
		ImageView item_vickeytalk_iv;
	    FlipLayout flipLayout;
	}
	private void downImg(final int id) {
		HttpUtils http = new HttpUtils();
		HttpHandler handler = http.download(Conf.APP_IMG+id+".jpg",
		    "/sdcard/zhidu/+"+id+".jpg",
		    true, // ���Ŀ���ļ����ڣ�����δ��ɵĲ��ּ������ء���������֧��RANGEʱ���������ء�
		    true, // �������󷵻���Ϣ�л�ȡ���ļ���������ɺ��Զ�������
		    new RequestCallBack<File>() {

		        @Override
		        public void onStart() {
		        	Log.e("onStart","........start......");
		        }

		        @Override
		        public void onLoading(long total, long current, boolean isUploading) {
		        	Log.e("onLoading",total+"|"+current);
		        }

		        @Override
		        public void onSuccess(ResponseInfo<File> responseInfo) {
		        	Log.e("onSuccess", responseInfo.toString());
		        	Toast.makeText(context, "ͼƬ�ѱ��浽 /sdcard/zhidu/"+id+".jpg", 1).show();
		        	
		        }


		        @Override
		        public void onFailure(HttpException error, String msg) {
		        	Log.e("onFailure","........msg......"+msg);
		        }
		});

	}
}
