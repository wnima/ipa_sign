package com.bootdo.common.utils.png;

public class PNGIHDRTrunk extends PNGTrunk
{
    public int m_nWidth;
    public int m_nHeight;
    
    public PNGIHDRTrunk(final int nSize, final String szName, final byte[] nData, final byte[] nCRC) {
        super(nSize, szName, nData, nCRC);
        this.m_nWidth = PNGTrunk.readInt(nData, 0);
        this.m_nHeight = PNGTrunk.readInt(nData, 4);
    }
}
