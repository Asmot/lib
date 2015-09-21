package com.zxy.libs.file;

import java.io.File;
import java.util.List;


public class Test {

	public static void main(String[] args) {
//		FileUtils.readFileByLine();
//		FileUtils.readFileByByte();
		
//		testClasses();
		
		testDelete();
	}

	/**
	 * 
	 */
	private static void testClasses() {
		List<Class<?>> classes = ClassUtils.getClasses("com.zxy.libs.file");
		
		for(Class<?> c : classes)  {
			System.out.println(c.getName());
		}
	}

	public static void testDelete() {
		long start = 0L;
		long end = 0L;
		
		String tar = "D:/tmp";
		System.err.println(FileUtils.getTotalFileCount(new File(tar)));
		
		System.err.println(FileUtils.getTotalFileAndDirCount(new File(tar)));
		
		start = System.currentTimeMillis();
		FileUtils.deleteFileWithCallBack(new File(tar), fileDeleteListener);
		end = System.currentTimeMillis();
		System.out.println("offset: " + (end - start));
	}
	
	public static void testUnzip() {
		long start = 0L;
		long end = 0L;
		
		String src = "tmp/China_largescale (2).zip";
		String tar = "D:/tmp";
		
//		System.err.println(FileUtils.getZipFileSize(src));
		
		start = System.currentTimeMillis();
		FileUtils.UnZipFileWithCallback(src, tar, fileUnZipListener);
		end = System.currentTimeMillis();
		System.out.println("offset: " + (end - start));
	}
	
	public static void testCopy() {
		long start = 0L;
		long end = 0L;
		
		String src = "data";
		String tar = "D:/tmp";
		
		start = System.currentTimeMillis();
		long fileLength = FileUtils.getFileSize(new File(src));
		end = System.currentTimeMillis();
		System.out.println("offset: " + (end - start));
		
		start = System.currentTimeMillis();
		FileUtils.copyDirectoryWithCallback(new File(src), new File(tar), 0, fileLength, fileCopyListener);
		end = System.currentTimeMillis();
		System.out.println("offset: " + (end - start));
	}
	
	
	public static IDeleteFileListener fileDeleteListener = new IDeleteFileListener() {
		
		@Override
		public void onDeleteStart(String fileName) {
			System.out.println("onDeleteStart " + fileName);
		}
		
		@Override
		public void onDeleteSchedule(String fileName, float complete) {
			if((int)complete % 40 == 0)
				System.out.println(fileName + " " + complete + "%");
		}
		
		@Override
		public void onDeleteFinish(String fileName) {
			System.out.println("onDeleteFinish " + fileName);
		}
		
		@Override
		public void onDeleteError(String fileName) {
			System.out.println("onDeleteError " + fileName);
		}
		
		@Override
		public void onDeleteCancel() {
			
		}
	};
	
	public static IUnZipListener fileUnZipListener = new IUnZipListener() {
		
		@Override
		public void onUnzipSchedule(String fileName, float complete) {
			if((int)complete % 40 == 0)
				System.out.println(fileName + " " + complete + "%");
		}
		
		@Override
		public void onUnzipFinish(String fileName) {
			System.out.println("onUnzipFinish " + fileName);
			
		}
		
		@Override
		public void onUnzipError(String fileName) {
			System.out.println("onUnzipError " + fileName);
			
		}
		
		@Override
		public void onUnzipCancel() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onUnZipStart(String fileName) {
			System.out.println("onUnZipStart " + fileName);
		}
	};
	
	public static ICopyFileListener fileCopyListener = new ICopyFileListener() {
		
		@Override
		public void onProgress(String inName, String outName, float completeCode) {
			if((int)completeCode % 10 == 0)
				System.out.println(inName + " to " + outName + " " + completeCode + "%");
		}
		
		@Override
		public void onCopyStart(String inName, String outName) {
			System.out.println("onCopyStart " + inName + " to " + outName + " ");
		}
		
		@Override
		public void onCopyFinish(String inName, String outName) {
			System.out.println("onCopyFinish " + inName + " to " + outName + " ");
		}
		
		@Override
		public void onCopyError(String inName, String outName, int error) {
			
		}
	};
}
