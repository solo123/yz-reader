package com.yazo.network;

//import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ����ֵ������byte��������໥ת�ￄ1�7   
 * @author minco
 */
public class BitConverter {
	public static byte[] getBytes(boolean value) {
		return new byte[] { (value ? ((byte) 1) : ((byte) 0)) };
	}

	public static byte[] getBytes(char value) {
		return getBytes((short) value);
	}

	public static byte[] getBytes(double value) {
		byte[] b = new byte[8];
		long l = Double.doubleToLongBits(value);
		
		for (int i = 0; i < b.length; i++) {
			//not sure correct!!!!!!!!!!!;
			b[i] = new Double(new Long(l).doubleValue()).byteValue();
			l = l >> 8;

		}
		return b;
	}

	public static byte[] getBytes(float value) {
		int temp = Float.floatToIntBits(value);
		byte[] data = new byte[4];
		data[3] = (byte) ((temp >> 24) & 0xff);
		data[2] = (byte) ((temp >> 16) & 0xff);
		data[1] = (byte) ((temp >> 8) & 0xff);
		data[0] = (byte) (temp & 0xff);
		return data;
	}

	public static byte[] getBytes(int value) {
		byte[] data = new byte[4];
		data[3] = (byte) ((value >> 24) & 0xff);
		data[2] = (byte) ((value >> 16) & 0xff);
		data[1] = (byte) ((value >> 8) & 0xff);
		data[0] = (byte) (value & 0xff);
		return data;
	}

	public static byte[] getBytes(long value) {
		byte[] data = new byte[8];
		data[7] = (byte) ((value >> 56) & 0xff);
		data[6] = (byte) ((value >> 48) & 0xff);
		data[5] = (byte) ((value >> 40) & 0xff);
		data[4] = (byte) ((value >> 32) & 0xff);
		data[3] = (byte) ((value >> 24) & 0xff);
		data[2] = (byte) ((value >> 16) & 0xff);
		data[1] = (byte) ((value >> 8) & 0xff);
		data[0] = (byte) (value & 0xff);
		return data;
	}

	public static byte[] getBytes(short value) {
		byte[] data = new byte[2];
		data[1] = (byte) ((value >> 8) & 0xff);
		data[0] = (byte) (value & 0xff);
		return data;
	}

	public static boolean toBoolean(byte value) {
		return value == 1;
	}

	public static char toChar(byte[] bufer) {

		return (char) (((bufer[1] << 8) | bufer[0] & 0xff));

	}

	public static double toDouble(byte[] buffer) {
		long value;

		value = buffer[0];
		value &= 0xff;
		value |= ((long) buffer[1] << 8);
		value &= 0xffff;
		value |= ((long) buffer[2] << 16);
		value &= 0xffffff;
		value |= ((long) buffer[3] << 24);
		value &= 0xffffffffl;
		value |= ((long) buffer[4] << 32);
		value &= 0xffffffffffl;
		value |= ((long) buffer[5] << 40);
		value &= 0xffffffffffffl;
		value |= ((long) buffer[6] << 48);
		value |= ((long) buffer[7] << 56);

		return Double.longBitsToDouble(value);
	}

	public static float toFloat(byte[] bufer) {
		return Float.intBitsToFloat(toInt32(bufer));
	}

	public static short toInt16(byte[] bufer) {

		return (short) (((bufer[1] << 8) | bufer[0] & 0xff));

	}

	public static int toInt32(byte[] bufer) {
		return (((bufer[3] & 0xff) << 24) | ((bufer[2] & 0xff) << 16) | ((bufer[1] & 0xff) << 8) | ((bufer[0] & 0xff) << 0));

	}

	public static long toInt64(byte[] bufer) {
		return ((((long) bufer[7] & 0xff) << 56) | (((long) bufer[6] & 0xff) << 48) | (((long) bufer[5] & 0xff) << 40) | (((long) bufer[4] & 0xff) << 32)
				| (((long) bufer[3] & 0xff) << 24) | (((long) bufer[2] & 0xff) << 16) | (((long) bufer[1] & 0xff) << 8) | (((long) bufer[0] & 0xff) << 0));
	}

	public static float toSingle(byte[] bufer) {
		return Float.intBitsToFloat(toInt32(bufer));
	}

    public static Object transToObject(boolean bool)
    {
    	return new Boolean(bool);
    }
    public static Object transToObject(int i)
    {
    	return new Integer(i);
    }
    public static Object transToObject(long l)
    {
    	return new Long(l);
    }
    public static Object transToObject(short s)
    {
    	return new Short(s);
    }
    public static Object transToObject(byte b)
    {
    	return new Byte(b);
    }
}
