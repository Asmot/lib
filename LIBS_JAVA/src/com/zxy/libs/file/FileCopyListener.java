package com.zxy.libs.file;

public interface FileCopyListener {
	public void onCopyStart(String inName, String outName);
	public void onProgress(String inName, String outName,float completeCode);
	public void onCopyFinish(String inName, String outName);
	public void onCopyError(String inName, String outName, int error);
}
