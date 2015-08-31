package com.zxy.libs.file;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.stream.FileImageInputStream;

/**
 * 文件相关操作
 * 读写文件
 * 删除文件（夹）
 * 复制文件（夹）
 *
 */
public class FileUtils {
	public final static String DIR_PATH = "data/file/";
	public final static String FILE_PATH = "test.txt";
	
	/**
	 * 以inputStream的方式读取文件内容
	 */
	public static void readFileByByte() {
		StringBuilder builder = new StringBuilder();
		
		String path = DIR_PATH + FILE_PATH;
		File file = new File(path);
		
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			byte[] b = new byte[1024];
			int len = 0;
			while((len = inputStream.read(b)) > 0) {
				builder.append(new String(b,0,len));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println(builder.toString());
		
	}
	
	/**
	 * 以DataInputStream的方式读取文件内容,
	 * 它一般对应着自己的写入
	 * 它可以读取boolean，int 等等数据
	 */
	public static void readFileByDataInputStream() {
		StringBuilder builder = new StringBuilder();
		
		String path = DIR_PATH + FILE_PATH;
		File file = new File(path);
		
		DataInputStream inputStream = null;
		try {
			inputStream = new DataInputStream(new FileInputStream(file));
			char c ;
			// 结束表示，读到结尾会跑出 eof异常
			while(inputStream.available() > 0) {
				c = inputStream.readChar();
				builder.append(c);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println(builder.toString());
		
	}
	
	
	/**
	 * 以BufferReader的方式读取文件内容 ， 读取一行
	 */
	public static void readFileByLine() {
		StringBuilder builder = new StringBuilder();
		
		String path = DIR_PATH + FILE_PATH;
		File file = new File(path);
		
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			
			while(true) {
				String str = bufferedReader.readLine();
				if(str == null) {
					break;
				}
				builder.append(str);
				// add enter
				builder.append("\n");
			}
			//delete the last enter /n cost per length
			builder.deleteCharAt(builder.length() - 1);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println(builder.toString());
		
	}
}
