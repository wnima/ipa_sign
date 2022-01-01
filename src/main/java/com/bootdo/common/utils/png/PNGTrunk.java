package com.bootdo.common.utils.png;

import java.io.*;

public class PNGTrunk
{
    protected int m_nSize;
    protected String m_szName;
    protected byte[] m_nData;
    protected byte[] m_nCRC;
    public static int[] crc_table;
    
    static {
        PNGTrunk.crc_table = null;
    }
    
    public static PNGTrunk generateTrunk(final DataInputStream file) {
        try {
            final int nSize = file.readInt();
            final byte[] nData = new byte[4];
            file.read(nData);
            final String szName = new String(nData);
            final byte[] nDataBuffer = new byte[nSize];
            file.read(nDataBuffer);
            final byte[] nCRC = new byte[4];
            file.read(nCRC);
            if (szName.equalsIgnoreCase("IHDR")) {
                return new PNGIHDRTrunk(nSize, szName, nDataBuffer, nCRC);
            }
            return new PNGTrunk(nSize, szName, nDataBuffer, nCRC);
        }
        catch (IOException loc_0) {
            return null;
        }
    }
    
    public PNGTrunk appendTrunk(final PNGTrunk trunk) {
        this.m_nSize += trunk.m_nSize;
        final byte[] new_m_nData = new byte[this.m_nData.length + trunk.m_nData.length];
        System.arraycopy(this.m_nData, 0, new_m_nData, 0, this.m_nData.length);
        System.arraycopy(trunk.m_nData, 0, new_m_nData, this.m_nData.length, trunk.m_nData.length);
        this.m_nData = new_m_nData;
        return null;
    }
    
    protected PNGTrunk(final int nSize, final String szName, final byte[] nCRC) {
        super();
        this.m_nSize = nSize;
        this.m_szName = szName;
        this.m_nCRC = nCRC;
    }
    
    protected PNGTrunk(final int nSize, final String szName, final byte[] nData, final byte[] nCRC) {
        this(nSize, szName, nCRC);
        this.m_nData = nData;
    }
    
    public int getSize() {
        return this.m_nSize;
    }
    
    public String getName() {
        return this.m_szName;
    }
    
    public byte[] getData() {
        return this.m_nData;
    }
    
    public byte[] getCRC() {
        return this.m_nCRC;
    }
    
    public void writeToStream(final FileOutputStream outStream) throws IOException {
        final byte[] nSize = { (byte)((this.m_nSize & 0xFF000000) >> 24), (byte)((this.m_nSize & 0xFF0000) >> 16), (byte)((this.m_nSize & 0xFF00) >> 8), (byte)(this.m_nSize & 0xFF) };
        outStream.write(nSize);
        outStream.write(this.m_szName.getBytes());
        outStream.write(this.m_nData);
        outStream.write(this.m_nCRC);
    }
    
    public static void writeInt(final byte[] nDes, final int nPos, final int nVal) {
        nDes[nPos] = (byte)((nVal & 0xFF000000) >> 24);
        nDes[nPos + 1] = (byte)((nVal & 0xFF0000) >> 16);
        nDes[nPos + 2] = (byte)((nVal & 0xFF00) >> 8);
        nDes[nPos + 3] = (byte)(nVal & 0xFF);
    }
    
    public static int readInt(final byte[] nDest, final int nPos) {
        return (nDest[nPos] & 0xFF) << 24 | (nDest[nPos + 1] & 0xFF) << 16 | (nDest[nPos + 2] & 0xFF) << 8 | (nDest[nPos + 3] & 0xFF);
    }
    
    public static void writeCRC(final byte[] nData, final int nPos) {
        final int chunklen = readInt(nData, nPos);
        System.out.println(chunklen);
        final int sum = ~CRCChecksum(nData, nPos + 4, 4 + chunklen);
        System.out.println(sum);
        writeInt(nData, nPos + 8 + chunklen, sum);
    }
    
    public static int CRCChecksum(final byte[] nBuffer, final int nOffset, final int nLength) {
        int c = -1;
        if (PNGTrunk.crc_table == null) {
            PNGTrunk.crc_table = new int[256];
            for (int mkn = 0; mkn < 256; ++mkn) {
                int mkc = mkn;
                for (int mkk = 0; mkk < 8; ++mkk) {
                    if ((mkc & 0x1) == 0x1) {
                        mkc = (0xEDB88320 ^ mkc >>> 1);
                    }
                    else {
                        mkc >>>= 1;
                    }
                }
                PNGTrunk.crc_table[mkn] = mkc;
            }
        }
        for (int n = nOffset; n < nLength + nOffset; ++n) {
            c = (PNGTrunk.crc_table[(c ^ nBuffer[n]) & 0xFF] ^ c >>> 8);
        }
        return c;
    }
}
