package dylibutil.model;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LC_LOAD_DYLIB extends LoadCommand {
	public static final int CMD = 12;
	private int strOffset;
	private int timeStamp;
	private int currentVersion;
	private int compatibVer;
	private String path;

	public int getStrOffset() {
		return strOffset;
	}

	public void setStrOffset(int strOffset) {
		this.strOffset = strOffset;
	}

	public int getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(int timeStamp) {
		this.timeStamp = timeStamp;
	}

	public int getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(int currentVersion) {
		this.currentVersion = currentVersion;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getCompatibVer() {
		return compatibVer;
	}

	public void setCompatibVer(int compatibVer) {
		this.compatibVer = compatibVer;
	}

	public byte[] toByteArray() throws IOException {
		int len = Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES
				+ path.getBytes().length;
		int mod = (len % 8);
		if (mod != 0) {
			len += 8 - mod;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream target = new DataOutputStream(out);
		DataUtil.writeInt(getCmd(), target);
		DataUtil.writeInt(len, target);
		DataUtil.writeInt(strOffset, target);
		DataUtil.writeInt(timeStamp, target);
		DataUtil.writeInt(currentVersion, target);
		DataUtil.writeInt(compatibVer, target);
		target.write(path.getBytes());
		if (mod != 0) {
			target.write(new byte[8 - mod]);
		}
		byte[] result = out.toByteArray();
		System.out.println("toByteArray:" + len + "=" + result.length);
		target.close();
		return result;
	}
}
