package com.demo.houchao.soundrecording.Utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {

	private String SDPATH = null;

	public String getSDPATH() {
		return SDPATH;
	}

	public FileUtil() {
		// 得到当前外部存储设备的目录
		// SDCARD
		SDPATH = Environment.getExternalStorageDirectory() + "/";
		Log.d("SDPATH=" ,SDPATH);
	}

	public String getStorePath(){
		SDPATH = Environment.getExternalStorageDirectory() + "/";
		return SDPATH;
	}

	public File CreatSDFile(String fileNmae){
		File file =new File(SDPATH+fileNmae);
		try {
		file.createNewFile();
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		return file;
		}
		/*
		* 在SD卡上创建目录
		*/
		public File creatSDDir(String dirName){
		File dir=new File(dirName);
		if(!dir.exists())
		{
		dir.mkdirs();
		}
		return dir;
		}
		/*
		*判断SD卡上的文件夹是否存在
		*/
		public boolean isFileExist(String fileName){
		File file =new File(SDPATH+fileName);
		return file.exists();
		}
		/*
		*将一个InputSteam里面的数据写入到SD卡中 
		*/
		public File write2SDFromInput(String path,String fileName,InputStream input){
		System.out.println("path="+path+";fileName="+fileName+";");
		File file =null;
		File folder=null;
		OutputStream output=null;
		try {
		folder=creatSDDir(path);
		System.out.println("folder="+folder);
		file=CreatSDFile(path+fileName);
		System.out.println("file="+file);
		output=new FileOutputStream(file);
		byte buffer[]=new byte[4*1024];
		
		int len=-1;
		while((len=input.read(buffer))!=-1){
			output.write(buffer, 0, len);
		}
		
			// while((input.read())!=-1){
			// output.write(buffer);
			// }
		
		output.flush();
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}finally{
		try{
		output.close();
		input.close();
		}catch(Exception e){
		e.printStackTrace();
		}
		}
		return file;
		}
		    
		 

}
