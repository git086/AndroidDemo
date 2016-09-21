package com.demo.houchao.soundrecording.Utils;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolder {

	private SparseArray<View> mViews;
	private int mPosition;
	private View mconvertView;
	
	//构造函数
	public ViewHolder(Context context,ViewGroup parent,int layoutId,int position) {
		this.mPosition=position;
		this.mViews=new SparseArray<View>();
		mconvertView=LayoutInflater.from(context).inflate(layoutId,parent,false);
		mconvertView.setTag(this);
	}
	
	//入口方法
	public static ViewHolder get(Context context,View convertView,ViewGroup parent,int layoutId,int position){
		if(convertView==null){
			return new ViewHolder(context, parent, layoutId, position);
		}
		else{
			ViewHolder holder=(ViewHolder) convertView.getTag();
			holder.mPosition=position;
			return holder;
		}
	}
	
	//通过传入viewId,拿到View的控件
	public <T extends View> T getView(int viewId) {
		View view = mViews.get(viewId);
		if(view == null) {
			view = mconvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T)view;
	}
	//返回convertView函数
	public View getconvertView() {
		return mconvertView;
	}
	//为TextView设置text的方法
	public ViewHolder setText(int viewId,String text){
		TextView tv=getView(viewId);
		tv.setText(text);
		return this;
	}
	
	
	//为TextView设置颜色的方法
		public ViewHolder setTextColor(int viewId,int color){
			TextView tv=getView(viewId);
			tv.setTextColor(color);
			return this;
		}
	
	
	//为ImageView设置Image方法
	public ViewHolder setImage(int viewId,int ImageId){
		ImageView image=getView(viewId);
		image.setImageResource(ImageId);
		return this;
	}
	
	
	
}
