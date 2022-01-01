package dylibutil.model;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Arm64 {
	public static int MH_MAGIC_64 = 0xfeedfacf;
	private int magic; /* mach magic number identifier */
	private int cputype; /* cpu specifier */
	private int cpusubtype; /* machine specifier */
	private int filetype; /* type of file */
	private int ncmds; /* number of load commands */
	private int sizeofcmds; /* the size of all the load commands */
	private int flags; /* flags */
	private int reserved; /* reserved */
	private List<Map<String,Object>> loadcommands = new ArrayList<>(); 

	public Arm64(String filepath) throws IOException {
		super();
		DataInputStream is = new DataInputStream(new BufferedInputStream(new FileInputStream(filepath)));
		this.magic = readInt(is);
		this.cputype = readInt(is);
		this.cpusubtype = readInt(is);
		this.filetype = readInt(is);
		this.ncmds = readInt(is);
		this.sizeofcmds = readInt(is);
		this.flags = readInt(is);
		this.reserved = readInt(is);
		while(sizeofcmds > 0) {
			int cmd = readInt(is);
			int size = readInt(is);
			System.out.println(Integer.toHexString(cmd)+":"+size);
			if(cmd == 12) {
				long strOffset = readLong(is);
				int timeStamp = readInt(is);
				int mmV = is.read();
				int mV = is.read();
				int ver = readShort(is);
				String currVersion = ver+"."+mV+"."+mmV;
				byte[] pathb = new byte[size - 4 - 4 - 8 - 4 - 4];
				is.read(pathb);
				String path = new String(pathb);
				HashMap<String,Object> dylibMap = new HashMap<String, Object>();
				dylibMap.put("strOffset", strOffset);
				dylibMap.put("timeStamp", timeStamp);
				dylibMap.put("currVersion", currVersion);
				dylibMap.put("path", path);
				loadcommands.add(dylibMap);
			}else {
				if(size!=0) {
					byte[] data = new byte[size-4-4];
					is.read(data);
				}else {
					size = 8;
				}
			}
			sizeofcmds -= size;
		}

		is.close();
		System.out.println(loadcommands);
	}

	public static final int readShort(DataInputStream in) throws IOException {
		   int ch1 = in.readByte() & 0x0ff;
		   int ch2 = in.readByte() & 0x0ff;
//		   System.out.println(Integer.toHexString(ch1));
//		   System.out.println(Integer.toHexString(ch2));
		   return ((ch2 << 8) + (ch1 << 0));
		}
	
	public static final int readInt(DataInputStream in) throws IOException {
	   int ch1 = in.readByte() & 0x0ff;
	   int ch2 = in.readByte() & 0x0ff;
	   int ch3 = in.readByte() & 0x0ff;
	   int ch4 = in.readByte() & 0x0ff;
//	   System.out.println(Integer.toHexString(ch1));
//	   System.out.println(Integer.toHexString(ch2));
//	   System.out.println(Integer.toHexString(ch3));
//	   System.out.println(Integer.toHexString(ch4));
	   return ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0));
	}
	
	public static final int readLong(DataInputStream in) throws IOException {
		   byte ch1 = in.readByte();
		   byte ch2 = in.readByte();
		   byte ch3 = in.readByte();
		   byte ch4 = in.readByte();
		   byte ch5 = in.readByte();
		   byte ch6 = in.readByte();
		   byte ch7 = in.readByte();
		   byte ch8 = in.readByte();
//		   System.out.println(Integer.toHexString(ch1));
//		   System.out.println(Integer.toHexString(ch2));
//		   System.out.println(Integer.toHexString(ch3));
//		   System.out.println(Integer.toHexString(ch4));
		   return ((ch8 << 56) + (ch7 << 48) + (ch6 << 40) + (ch5 << 32) + (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0));
		}
	
	@Override
	public String toString() {
		return "Arm64 [magic=" + Integer.toHexString(magic) + ", cputype=" + Integer.toHexString(cputype) + ", cpusubtype=" + Integer.toHexString(cpusubtype) + ", filetype="
				+ Integer.toHexString(filetype) + ", ncmds=" + Integer.toHexString(ncmds) + ", sizeofcmds=" + Integer.toHexString(sizeofcmds) + ", flags=" + Integer.toHexString(flags) + ", reserved="
				+ Integer.toHexString(reserved) + "]";
	}

}
