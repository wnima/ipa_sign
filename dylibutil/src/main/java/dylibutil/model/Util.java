package dylibutil.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Util {
	public static void readArm64File(String filePath, String newfilePath) throws IOException {
		DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)));
		HeaderARM64 header = readHeader(in);
		List<LoadCommand> commands = readerLoadCommands(in, header);
		int lastIndex = 0;
		for (int i = 0; i < commands.size(); i++) {
			if (commands.get(i).getCmd() == LC_LOAD_DYLIB.CMD) {
				lastIndex = i;
			}
		}
		LC_LOAD_DYLIB diy = new LC_LOAD_DYLIB();
		diy.setCmd(LC_LOAD_DYLIB.CMD);
		diy.setCurrentVersion(1000);
		diy.setCompatibVer(1000);
		diy.setPath("@executable_path/Frameworks/FF.framework/FF");
		diy.setStrOffset(24);
		diy.setTimeStamp((int) (System.currentTimeMillis() / 1000));
		commands.add(lastIndex + 1, diy);

		// 计算commands总大小
		List<byte[]> commandsByteList = new ArrayList<byte[]>();
		int commandSize = 0;
		for (LoadCommand cmd : commands) {
			byte[] bs = cmd.toByteArray();
			commandsByteList.add(bs);
			commandSize += bs.length;
		}
		// 更新header
		int oldSize = header.getSizeofcmds();
		header.setNcmds(commandsByteList.size());
		header.setSizeofcmds(commandSize);

		// 写入新文件
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(newfilePath)));
		// header
		byte[] headerB = header.toByteArray();
		out.write(headerB);
		// 写入loadcommands
		for (byte[] cmdByte : commandsByteList) {
			out.write(cmdByte);
		}
		// 写入 剩余部分
		// 跳过（commandsByteList.size() - oldSize）个长度，剩下的与原来的保持一致
		System.out.println("原大小:"+oldSize);
		System.out.println("新大小:"+commandSize);
		in.skip(commandSize - oldSize);
		int len = 0;
		byte[] temp = new byte[2048];
		int b = 0;
		while ((b = in.read()) != -1) {
			out.write(b);
		}
		out.close();
		in.close();
	}

	public static List<LoadCommand> readerLoadCommands(DataInputStream in, HeaderARM64 header) throws IOException {
		int cmdSize = header.getSizeofcmds();
		List<LoadCommand> list = new ArrayList<LoadCommand>();
		while (cmdSize > 0) {
			int cmd = DataUtil.readInt(in);
			int size = DataUtil.readInt(in);
			System.out.println(Integer.toHexString(cmd) + ":" + size);
			LoadCommand loadcmd = null;
			if (cmd == 12) {
				int strOffset = DataUtil.readInt(in);
				int timeStamp = DataUtil.readInt(in);
				int currVersion = DataUtil.readInt(in);
				int compatibVer = DataUtil.readInt(in);
				byte[] pathb = new byte[size - 4 - 4 - 4 - 4 - 4 - 4];
				in.read(pathb);
				String path = new String(pathb);
				LC_LOAD_DYLIB dylib = new LC_LOAD_DYLIB();
				dylib.setCmd(cmd);
				dylib.setSize(size);
				//
				dylib.setStrOffset(strOffset);
				dylib.setTimeStamp(timeStamp);
				dylib.setCurrentVersion(currVersion);
				dylib.setCompatibVer(compatibVer);
				dylib.setPath(path);
				loadcmd = dylib;
			} else {
				loadcmd = new LoadCommand();
				loadcmd.setCmd(cmd);
				loadcmd.setSize(size);
				if (size != 0) {
					byte[] data = new byte[size - 4 - 4];
					in.read(data);
					loadcmd.setData(data);
				} else {
					size = 8;
				}
			}
			cmdSize -= size;
			list.add(loadcmd);
		}
		return list;
	}

	public static HeaderARM64 readHeader(DataInputStream in) throws IOException {
		int magic = DataUtil.readInt(in);
		if (magic == HeaderARM64.MH_MAGIC_64) {
			int cputype = DataUtil.readInt(in); /* cpu specifier */
			int cpusubtype = DataUtil.readInt(in); /* machine specifier */
			int filetype = DataUtil.readInt(in); /* type of file */
			int ncmds = DataUtil.readInt(in); /* number of load commands */
			int sizeofcmds = DataUtil.readInt(in); /* the size of all the load commands */
			int flags = DataUtil.readInt(in); /* flags */
			int reserved = DataUtil.readInt(in); /* reserved */
			HeaderARM64 headerARM64 = new HeaderARM64(magic, cputype, cpusubtype, filetype, ncmds, sizeofcmds, flags,
					reserved);
			return headerARM64;
		}
		return null;
	}

	public static void main(String[] args) throws IOException {
		readArm64File("D:/ori", "D:/new");
	}
}
