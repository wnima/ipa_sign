package dylibutil.model;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class HeaderARM64 {
	public static final int MH_MAGIC_64 = 0xfeedfacf;
	private int magic; /* mach magic number identifier */
	private int cputype; /* cpu specifier */
	private int cpusubtype; /* machine specifier */
	private int filetype; /* type of file */
	private int ncmds; /* number of load commands */
	private int sizeofcmds; /* the size of all the load commands */
	private int flags; /* flags */
	private int reserved; /* reserved */

	public int getMagic() {
		return magic;
	}

	public void setMagic(int magic) {
		this.magic = magic;
	}

	public int getCputype() {
		return cputype;
	}

	public void setCputype(int cputype) {
		this.cputype = cputype;
	}

	public int getCpusubtype() {
		return cpusubtype;
	}

	public void setCpusubtype(int cpusubtype) {
		this.cpusubtype = cpusubtype;
	}

	public int getFiletype() {
		return filetype;
	}

	public void setFiletype(int filetype) {
		this.filetype = filetype;
	}

	public int getNcmds() {
		return ncmds;
	}

	public void setNcmds(int ncmds) {
		this.ncmds = ncmds;
	}

	public int getSizeofcmds() {
		return sizeofcmds;
	}

	public void setSizeofcmds(int sizeofcmds) {
		this.sizeofcmds = sizeofcmds;
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public int getReserved() {
		return reserved;
	}

	public void setReserved(int reserved) {
		this.reserved = reserved;
	}

	@Override
	public String toString() {
		return "Arm64Header [magic=" + magic + ", cputype=" + cputype + ", cpusubtype=" + cpusubtype + ", filetype="
				+ filetype + ", ncmds=" + ncmds + ", sizeofcmds=" + sizeofcmds + ", flags=" + flags + ", reserved="
				+ reserved + "]";
	}

	public HeaderARM64(int magic, int cputype, int cpusubtype, int filetype, int ncmds, int sizeofcmds, int flags,
			int reserved) {
		super();
		this.magic = magic;
		this.cputype = cputype;
		this.cpusubtype = cpusubtype;
		this.filetype = filetype;
		this.ncmds = ncmds;
		this.sizeofcmds = sizeofcmds;
		this.flags = flags;
		this.reserved = reserved;
	}
	
	public byte[] toByteArray() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream target = new DataOutputStream(out);
		DataUtil.writeInt(magic, target);
		DataUtil.writeInt(cputype, target);
		DataUtil.writeInt(cpusubtype, target);
		DataUtil.writeInt(filetype, target);
		DataUtil.writeInt(ncmds, target);
		DataUtil.writeInt(sizeofcmds, target);
		DataUtil.writeInt(flags, target);
		DataUtil.writeInt(reserved, target);
		byte[] result = out.toByteArray();
		target.close();
		return result;
	}
}
