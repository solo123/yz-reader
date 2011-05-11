package com.yazo.util;


public class MemoryStream {

	private byte[] buffer;

	private int capacity;
	
	private int length;

	private int position;

	public MemoryStream() {
		this(256);
	}
	
	public MemoryStream(byte[] bufferArray) {
		if (bufferArray == null) {
			return;
		}

		byte[] buffer = new byte[bufferArray.length];
		this.position = 0;
		System.arraycopy(bufferArray, 0, buffer, 0, bufferArray.length);
		this.buffer = buffer;
		this.length = buffer.length;
		this.capacity = buffer.length * 2;
	}

	public MemoryStream(int capacity) {
		this.position = 0;
		this.capacity = capacity;
		this.length = 0;

		this.buffer = new byte[this.capacity];
	}

	public void clear() {

		this.buffer = null;

		System.gc();
	}

	public int getCapacity() {
		return this.capacity;
	}

	public long getLength() {
		return this.length;
	}

	public int getPosition() {
		return this.position;
	}

	
	public int read(byte[] buffer, int offset, int count) {
		if (buffer == null) {
			throw new NullPointerException("buffer");
		}
		else if ( offset < 0 || count < 0 || offset > buffer.length - 1) {
			throw new IndexOutOfBoundsException("offset ï¿½ï¿½ countï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Öµï¿½ï¿½Ð?ï¿?");
		}
		else {
			int len = this.buffer.length - this.position;
			if (len > count) {
				len = count;
			}
			
			for (int i = 0; i < len; i++) {
				buffer[ offset + i ] = (byte) this.readByte();
			}
			
			return len;
		}		
	}

	public int readByte() {
		int value = (this.position >= this.buffer.length) ? -1 : this.buffer[this.position];
		this.position++;
	/*	if(value==-1)
			DataResources.buffer.append("find error"+position);*/
		return value;
	}

	public void setCapacity(int capacity) {

	}

	public void setLength(int value) {
		this.length = (value > this.length) ? this.length : value;
		this.length = (value < 0) ? 0 : value;
		this.position = this.length;
	}

	
	public void setPosition(int position) {
		if (position > this.length || position < 0) {
			throw new IllegalArgumentException("positionï¿½ï¿½Öµï¿½ï¿½Ð§");
		}
		else {
			this.position = position;
		}
	}

	public byte[] toArray() {
		byte[] buffer = new byte[this.length];
		System.arraycopy(this.buffer, 0, buffer, 0, buffer.length);
		return buffer;
	}

	public void write(byte[] buffer, int offset, int count) {
		if (buffer == null) {
			throw new NullPointerException("");
		}
		else if (offset < 0 || offset > buffer.length) {
			throw new IllegalArgumentException("");
		}
		else if (count < 0 || (offset + count) > buffer.length) {
			throw new IllegalArgumentException("");
		}
		else {
			for (int i = 0; i < count; i++) {
				this.writeByte(buffer[offset + i ]);
			}
		}
	}

	public void writeByte(byte value) {
		if (this.position >= this.buffer.length - 1) {
			this.capacity = this.buffer.length * 2;
			byte[] buffertemp = new byte[this.capacity];
			System.arraycopy(this.buffer, 0, buffertemp, 0, this.buffer.length);
			this.buffer = buffertemp;
		}

		this.buffer[this.position++] = value;
		this.length++;
	}
}
