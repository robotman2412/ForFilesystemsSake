package forfilesystemssake;

import jutils.JUtils;
import jutils.database.DataPacker;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * You don't need to use this class to use FFS.
 */
public class BlockReadDataPacker extends DataPacker {
	
	protected FFSBase fs;
	protected Block[] blocks;
	
	public BlockReadDataPacker(FFSBase fs, long startingBlock) throws IOException {
		this.fs = fs;
		
		this.blocks = fs.getBlocks(startingBlock);
	}
	
	/** The block we're reading from. */
	public int blockIndex;
	
	//region illegal
	/** Used to add an existing byte array on to the buffer.
	 *  If lenBytes is under 1, the length will not be packed */
	public void addArr(int lenBytes, byte[] mData) {
		throw new RuntimeException("This datapacker cannot write!");
	}
	
	/** Used to add an existing byte array on to the buffer.
	 *  Note that the length will always be stored in 4 bytes. */
	public void addArr(byte[] mData) {
		throw new RuntimeException("This datapacker cannot write!");
	}
	
	/** Used to add an existing byte array on to the buffer.
	 *  Note that this will not include the length. */
	public void addArrRaw(byte[] mData) {
		throw new RuntimeException("This datapacker cannot write!");
	}
	
	/** Used to convert an integer to bytes and append it to the buffer. */
	public void addInt(int mData) {
		throw new RuntimeException("This datapacker cannot write!");
	}
	
	/** Used to convert a short to bytes and append it to the buffer. */
	public void addShort(int mData) {
		throw new RuntimeException("This datapacker cannot write!");
	}
	
	/** Used to convert a long to bytes and append it to the buffer. */
	public void addLong(long mData) {
		throw new RuntimeException("This datapacker cannot write!");
	}
	
	/** Used to append a byte to the buffer. */
	public void addByte(int mData) {
		throw new RuntimeException("This datapacker cannot write!");
	}
	
	/** Used to convert a float to bytes and append it to the buffer. */
	public void addFloat(float mData) {
		throw new RuntimeException("This datapacker cannot write!");
	}
	
	/** Used to convert a double to bytes and append it to the buffer. */
	public void addDouble(double mData) {
		throw new RuntimeException("This datapacker cannot write!");
	}
	
	/** Used to convert a string to bytes and append it to the buffer. */
	public void addString(String mData) {
		throw new RuntimeException("This datapacker cannot write!");
	}
	//endregion illegal
	
	/**
	 * Used to load a byte array from the buffer.<br>
	 * lenBytes must be between 1 and 4 (inclusive) for the array to load correctly.
	 */
	public byte[] readArr(int lenBytes) {
		int length = 0;
		for (int i = 0; i < lenBytes; i++) {
			lenBytes <<= 8;
			lenBytes |= readByte();
		}
		return readArrRaw(length);
	}
	
	/**
	 * Used to load a byte array from the buffer.
	 * Note that this assumes there is 4 length bytes.
	 */
	public byte[] readArr() {
		return readArrRaw(readInt());
	}
	
	/**
	 * Used to load a byte array from the buffer.
	 * Note that this assumes there is no length encoded and thus requires a length argument.
	 */
	public byte[] readArrRaw(int len) {
		byte[] arr = new byte[len];
		for (int i = 0; i < len; i++) {
			arr[i] = readByte();
		}
		return arr;
	}
	
	/** Used to load an integer from the buffer. */
	public int readInt() {
		int ret = JUtils.bytesToInt(readArrRaw(4));
		return ret;
	}
	
	/** Used to load a short from the buffer. */
	public int readShort() {
		int ret = JUtils.bytesToInt(readArrRaw(2));
		return ret;
	}
	
	/** Used to load a long from the buffer. */
	public long readLong() {
		long ret = JUtils.bytesToLong(readArrRaw(8));
		return ret;
	}
	
	/** Used to load a byte from the buffer. */
	public byte readByte() {
		byte ret = blocks[blockIndex].data[index];
		if (index >= blocks[blockIndex].data.length) {
			index = 0;
			blockIndex ++;
		}
		return ret;
	}
	
	/** Used to load a float from the buffer. */
	public float readFloat() {
		float ret = JUtils.bytesToFloat(readArrRaw(4));
		return ret;
	}
	
	/** Used to load a double from the buffer. */
	public double readDouble() {
		double ret = JUtils.bytesToDouble(readArrRaw(8));
		return ret;
	}
	
	/** Used to load a string from the buffer. */
	public String readString() {
		return new String(readArr(), StandardCharsets.US_ASCII);
	}
	
}
