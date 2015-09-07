package com.zxy.libs.file;

public interface FileDeleteListener {
	 /**
     * 删除开始
     */
    public void onDeleteStart(String fileName);

    /**
     * 删除失败
     */
    public void onDeleteError(String fileName);

    /**
     * 删除进度
     * 
     * @param listener
     */
    public void onDeleteSchedule(String fileName, float complete); // zip的进度

    /**
     * 删除成功
     */
    public void onDeleteFinish(String fileName);

    /**
     * 取消删除
     */
    public void onDeleteCancel();
}
