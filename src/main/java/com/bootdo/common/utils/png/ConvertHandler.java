package com.bootdo.common.utils.png;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.CRC32;

import com.jcraft.jzlib.ZStream;

public class ConvertHandler {
	protected boolean isPNGFile(final File file) {
		final String szFileName = file.getName();
		return szFileName.length() >= 5 && szFileName.substring(szFileName.length() - 4).equalsIgnoreCase(".png");
	}

	protected PNGTrunk getTrunk(ArrayList<PNGTrunk> trunks,final String szName) {
		if (trunks == null) {
			return null;
		}
		PNGTrunk returnTrunk = null;
		for (int n = 0; n < trunks.size(); ++n) {
			final PNGTrunk trunk = trunks.get(n);
			if (trunk.getName().equalsIgnoreCase(szName)) {
				if (returnTrunk == null) {
					returnTrunk = trunks.get(n);
				} else {
					returnTrunk.appendTrunk(trunks.get(n));
				}
			}
		}
		return returnTrunk;
	}

	public boolean convertPNGFile(final File pngFile, File newFile) {
		boolean isSuccess = false;
		ArrayList<PNGTrunk> trunks;
		try {
			final DataInputStream file = new DataInputStream(new FileInputStream(pngFile));
			final byte[] nPNGHeader = new byte[8];
			file.read(nPNGHeader);
			boolean bWithCgBI = false;
			trunks = new ArrayList<PNGTrunk>();
			if (nPNGHeader[0] == -119 && nPNGHeader[1] == 80 && nPNGHeader[2] == 78 && nPNGHeader[3] == 71
					&& nPNGHeader[4] == 13 && nPNGHeader[5] == 10 && nPNGHeader[6] == 26 && nPNGHeader[7] == 10) {
				PNGTrunk trunk;
				do {
					trunk = PNGTrunk.generateTrunk(file);
					trunks.add(trunk);
					if (trunk.getName().equalsIgnoreCase("CgBI")) {
						bWithCgBI = true;
					}
				} while (!trunk.getName().equalsIgnoreCase("IEND"));
			}
			file.close();
			if (getTrunk(trunks,"CgBI") != null) {
				System.out.println("Dir:" + pngFile.getAbsolutePath() + "--->" + newFile.getPath());
				final PNGTrunk dataTrunk = getTrunk(trunks,"IDAT");
				System.out.println("dataTrunk size = " + dataTrunk.getSize());
				final PNGIHDRTrunk ihdrTrunk = (PNGIHDRTrunk) getTrunk(trunks,"IHDR");
				System.out.println("Width:" + ihdrTrunk.m_nWidth + "  Height:" + ihdrTrunk.m_nHeight);
				final int nMaxInflateBuffer = 4 * (ihdrTrunk.m_nWidth + 1) * ihdrTrunk.m_nHeight;
				final byte[] outputBuffer = new byte[nMaxInflateBuffer];
				final ZStream inStream = new ZStream();
				inStream.avail_in = dataTrunk.getSize();
				inStream.next_in_index = 0;
				inStream.next_in = dataTrunk.getData();
				inStream.next_out_index = 0;
				inStream.next_out = outputBuffer;
				inStream.avail_out = outputBuffer.length;
				if (inStream.inflateInit(-15) != 0) {
					System.out.println("PNGCONV_ERR_ZLIB");
					return isSuccess;
				}
				int nResult = inStream.inflate(0);
				switch (nResult) {
				case 2: {
					nResult = -3;
				}
				case -4:
				case -3: {
					inStream.inflateEnd();
					System.out.println("PNGCONV_ERR_ZLIB");
					return isSuccess;
				}
				default: {
					nResult = inStream.inflateEnd();
					if (inStream.total_out > nMaxInflateBuffer) {
						System.out.println("PNGCONV_ERR_INFLATED_OVER");
					}
					int nIndex = 0;
					for (int y = 0; y < ihdrTrunk.m_nHeight; ++y) {
						++nIndex;
						for (int x = 0; x < ihdrTrunk.m_nWidth; ++x) {
							final byte nTemp = outputBuffer[nIndex];
							outputBuffer[nIndex] = outputBuffer[nIndex + 2];
							outputBuffer[nIndex + 2] = nTemp;
							nIndex += 4;
						}
					}
					final ZStream deStream = new ZStream();
					final int nMaxDeflateBuffer = nMaxInflateBuffer + 1024;
					final byte[] deBuffer = new byte[nMaxDeflateBuffer];
					deStream.avail_in = outputBuffer.length;
					deStream.next_in_index = 0;
					deStream.next_in = outputBuffer;
					deStream.next_out_index = 0;
					deStream.next_out = deBuffer;
					deStream.avail_out = deBuffer.length;
					deStream.deflateInit(9);
					nResult = deStream.deflate(4);
					if (deStream.total_out > nMaxDeflateBuffer) {
						System.out.println("PNGCONV_ERR_DEFLATED_OVER");
					}
					final byte[] newDeBuffer = new byte[(int) deStream.total_out];
					for (int n = 0; n < deStream.total_out; ++n) {
						newDeBuffer[n] = deBuffer[n];
					}
					final CRC32 crc32 = new CRC32();
					crc32.update(dataTrunk.getName().getBytes());
					crc32.update(newDeBuffer);
					final long lCRCValue = crc32.getValue();
					dataTrunk.m_nData = newDeBuffer;
					dataTrunk.m_nCRC[0] = (byte) ((lCRCValue & 0xFFFFFFFFFF000000L) >> 24);
					dataTrunk.m_nCRC[1] = (byte) ((lCRCValue & 0xFF0000L) >> 16);
					dataTrunk.m_nCRC[2] = (byte) ((lCRCValue & 0xFF00L) >> 8);
					dataTrunk.m_nCRC[3] = (byte) (lCRCValue & 0xFFL);
					dataTrunk.m_nSize = newDeBuffer.length;
					final FileOutputStream outStream = new FileOutputStream(newFile);
					final byte[] pngHeader = { -119, 80, 78, 71, 13, 10, 26, 10 };
					outStream.write(pngHeader);
					for (int n2 = 0; n2 < trunks.size(); ++n2) {
						final PNGTrunk trunk = trunks.get(n2);
						if (!trunk.getName().equalsIgnoreCase("CgBI")) {
							trunk.writeToStream(outStream);
						}
					}
					outStream.close();
					isSuccess = true;
					break;
				}
				}
			}
		} catch (IOException e) {
			System.out.println("Error --" + e.toString());
		}
		return isSuccess;
	}

	public void convertDirectory(final File dir) {
		final File[] files = dir.listFiles();
		for (int n = 0; n < files.length; ++n) {
			if (files[n].isDirectory()) {
				this.convertDirectory(files[n]);
			} else if (this.isPNGFile(files[n])) {
				File newFile = new File(
						files[n].getName().substring(0, files[n].getName().lastIndexOf(".")) + "-new.png");
				this.convertPNGFile(files[n], newFile);
			}
		}
	}
	
	public static void main(String[] args) {
		new ConvertHandler().convertPNGFile(new File("E:/1.png"), new File("E:/2.png"));
	}
}
