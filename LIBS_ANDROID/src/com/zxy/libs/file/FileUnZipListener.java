package com.zxy.libs.file;

public interface FileUnZipListener {

    /**
     * 解压开始
     */
    public void onUnZipStart(String fileName);

    /**
     * 解压失败
     */
    public void onUnzipError(String fileName);

    /**
     * 解压进度
     * 
     * @param listener
     */
    public void onUnzipSchedule(String fileName, float complete); // zip的进度

    /**
     * 解压成功
     */
    public void onUnzipFinish(String fileName);

    /**
     * 取消解压
     */
    public void onUnzipCancel();
}
