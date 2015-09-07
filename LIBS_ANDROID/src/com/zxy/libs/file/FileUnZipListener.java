package com.zxy.libs.file;

public interface FileUnZipListener {

    /**
     * ��ѹ��ʼ
     */
    public void onUnZipStart(String fileName);

    /**
     * ��ѹʧ��
     */
    public void onUnzipError(String fileName);

    /**
     * ��ѹ����
     * 
     * @param listener
     */
    public void onUnzipSchedule(String fileName, float complete); // zip�Ľ���

    /**
     * ��ѹ�ɹ�
     */
    public void onUnzipFinish(String fileName);

    /**
     * ȡ����ѹ
     */
    public void onUnzipCancel();
}
