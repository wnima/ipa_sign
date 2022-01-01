package dylibutil.model;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class LoadCommand {
	private int cmd;
	private int size;
	private byte[] data;
	public int getCmd() {
		return cmd;
	}
	public void setCmd(int cmd) {
		this.cmd = cmd;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "LoadCommand [cmd=" + cmd + ", size=" + size + ", data=" + Arrays.toString(data) + "]";
	}
	
	public byte[] toByteArray() throws IOException{
		int len = Integer.BYTES + Integer.BYTES + data.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream target = new DataOutputStream(out);
		DataUtil.writeInt(cmd, target);
		DataUtil.writeInt(len, target);
		target.write(data);
		byte[] result = out.toByteArray();
		System.out.println("toByteArray:"+len +"="+result.length);
		return result;
	}
}
