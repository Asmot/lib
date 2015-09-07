package com.zxy.libs.file;

public interface FileDeleteListener {
	 /**
     * ɾ����ʼ
     */
    public void onDeleteStart(String fileName);

    /**
     * ɾ��ʧ��
     */
    public void onDeleteError(String fileName);

    /**
     * ɾ������
     * 
     * @param listener
     */
    public void onDeleteSchedule(String fileName, float complete); // zip�Ľ���

    /**
     * ɾ���ɹ�
     */
    public void onDeleteFinish(String fileName);

    /**
     * ȡ��ɾ��
     */
    public void onDeleteCancel();
}
