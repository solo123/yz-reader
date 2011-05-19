package com.yazo.network;

public interface ICompression {
	
	/**
	 * ��ѹ���ֽ�����
	 * @param input
	 * @return
	 */
    byte[] Decompress(byte[] input);

    /**
     * ѹ���ֽ�����
     * @param input
     * @return
     */
    byte[] Compress(byte[] input);

    /**
     * ����ֽ������Ƿ�ѹ�������ʵ�ֵ�ѹ���㷨���м�⣩
     * @param input
     * @return
     */
    boolean IsCompressed(byte[] input);

    /**
     * ����������ύ���ֽ������ȴ��ڴ˳���ʱ��ִ�и�ʵ���ṩ��ѹ������
     * @return
     */
    int getCompressWhenGreaterThan();
}
