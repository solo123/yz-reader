package com.yazo.network;

public interface IEncryption
{
	/**
     * �����ֽ�����
     * @param input
     * @return
     */
    byte[] Decrypt ( byte[] input );

    /**
     * �����ֽ�����
     * @param input
     * @return
     */
    byte[] Encrypt( byte[] input );

    /**
     * ����ֽ������Ƿ񱻼��ܹ����ʵ�ֵļ����㷨���м�⣩
     * @param input
     * @return
     */
    boolean IsEncrypted( byte[] input );
}
