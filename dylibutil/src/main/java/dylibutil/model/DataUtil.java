package dylibutil.model;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class DataUtil {
	
	/// read
	public static final int readShort(DataInputStream in) throws IOException {
		int ch1 = in.readByte() & 0x0ff;
		int ch2 = in.readByte() & 0x0ff;
		return ((ch2 << 8) + (ch1 << 0));
	}

	public static final int readInt(DataInputStream in) throws IOException {
		int ch1 = in.readByte() & 0x0ff;
		int ch2 = in.readByte() & 0x0ff;
		int ch3 = in.readByte() & 0x0ff;
		int ch4 = in.readByte() & 0x0ff;
		return ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0));
	}

	public static final int readLong(DataInputStream in) throws IOException {
		int ch1 = in.readByte() & 0x0ff;
		int ch2 = in.readByte() & 0x0ff;
		int ch3 = in.readByte() & 0x0ff;
		int ch4 = in.readByte() & 0x0ff;
		int ch5 = in.readByte() & 0x0ff;
		int ch6 = in.readByte() & 0x0ff;
		int ch7 = in.readByte() & 0x0ff;
		int ch8 = in.readByte() & 0x0ff;
		return ((ch8 << 56) + (ch7 << 48) + (ch6 << 40) + (ch5 << 32) + (ch4 << 24) + (ch3 << 16) + (ch2 << 8)
				+ (ch1 << 0));
	}
	
	
	// write
	public static final void writeShort(short data,DataOutputStream out) throws IOException {
		int ch1 = data << 8 >> 8;
		int ch2 = data >> 8;
		out.writeByte(ch1);
		out.writeByte(ch2);
	}

	public static final void writeInt(int data,DataOutputStream out) throws IOException {
		int ch1 = data << 24 >> 24;
		int ch2 = data << 16 >> 16 >> 8;
		int ch3 = data << 8 >> 8 >> 16;
		int ch4 = data >> 24;
		out.writeByte(ch1);
		out.writeByte(ch2);
		out.writeByte(ch3);
		out.writeByte(ch4);
	}

	public static final void writeLong(long data,DataOutputStream out) throws IOException {
		int ch1 = (int) (data << 56 >> 56);
		int ch2 = (int) (data << 48 >> 48 >> 8);
		int ch3 = (int) (data << 40 >> 40 >> 16);
		int ch4 = (int) (data << 32 >> 32 >> 24);
		int ch5 = (int) (data << 24 >> 24 >> 32);
		int ch6 = (int) (data << 16 >> 16 >> 40);
		int ch7 = (int) (data << 8 >> 8 >> 48);
		int ch8 = (int) (data >> 56);
		out.writeByte(ch1);
		out.writeByte(ch2);
		out.writeByte(ch3);
		out.writeByte(ch4);
		out.writeByte(ch5);
		out.writeByte(ch6);
		out.writeByte(ch7);
		out.writeByte(ch8);
	}
	
	public static void main(String[] args) throws IOException {
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(o);
		writeInt(712, out);
		out.writeInt(712);
		System.out.println(Arrays.toString(o.toByteArray()));
		out.close();
	}
	
}
