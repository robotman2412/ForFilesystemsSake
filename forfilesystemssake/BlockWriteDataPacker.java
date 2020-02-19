package forfilesystemssake;

import jutils.JUtils;
import jutils.database.DataPacker;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * You don't need to use this class to use FFS.
 */
public class BlockWriteDataPacker extends DataPacker {
	
	protected FFSBase fs;
	protected Block block;
	protected boolean closed;
	
	public BlockWriteDataPacker(FFSBase fs) throws IOException {
		this.fs = fs;
		block = fs.getFreeBlock();
		block.nextBlockId = block.id;
		closed = false;
	}
	
	public BlockWriteDataPacker(FFSBase fs, long startingBlock) throws IOException {
		this.fs = fs;
		block = fs.getBlock(startingBlock);
		closed = false;
	}
	
	/** Used to add an existing byte array on to the buffer.
	 *  If lenBytes is under 1, the length will not be packed */
	public void addArr(int lenBytes, byte[] mData) {
		byte[] len = JUtils.intToBytes(mData.length);
		byte[] lenOut = new byte[lenBytes];
		System.arraycopy(len, 0, lenOut, 0, len.length);
		addArrRaw(lenOut);
		addArrRaw(mData);
	}
	
	/** Used to add an existing byte array on to the buffer.
	 *  Note that the length will always be stored in 4 bytes. */
	public void addArr(byte[] mData) {
		addInt(mData.length);
		addArrRaw(mData);
	}
	
	/** Used to add an existing byte array on to the buffer.
	 *  Note that this will not include the length. */
	public void addArrRaw(byte[] mData) {
		for (byte b : mData) {
			addByte(b);
		}
	}
	
	/** Used to convert an integer to bytes and append it to the buffer. */
	public void addInt(int mData) {
		addArrRaw(JUtils.intToBytes(mData));
	}
	
	/** Used to convert a short to bytes and append it to the buffer. */
	public void addShort(int mData) {
		addArrRaw(JUtils.shortToBytes(mData));
	}
	
	/** Used to convert a long to bytes and append it to the buffer. */
	public void addLong(long mData) {
		addArrRaw(JUtils.longToBytes(mData));
	}
	
	/** Used to append a byte to the buffer. */
	public void addByte(int mData) {
		block.data[index] = (byte) mData;
		index ++;
		if (index >= block.data.length) {
			try {
				if (block.nextBlockId == block.id) {
					fs.setBlock(block.id, block);
					block = fs.getBlock(block.nextBlockId);
				}
				else
				{
					Block nextBlock = fs.getFreeBlock();
					block.nextBlockId = nextBlock.id;
					fs.setBlock(block.id, block);
					block = nextBlock;
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/** Used to convert a float to bytes and append it to the buffer. */
	public void addFloat(float mData) {
		addArrRaw(JUtils.floatToBytes(mData));
	}
	
	/** Used to convert a double to bytes and append it to the buffer. */
	public void addDouble(double mData) {
		addArrRaw(JUtils.doubleToBytes(mData));
	}
	
	/** Used to convert a string to bytes and append it to the buffer. */
	public void addString(String mData) {
		addArr(mData.getBytes(StandardCharsets.US_ASCII));
	}
	
	public void close() throws IOException {
		if (closed) {
			return;
		}
		if (block.nextBlockId != block.id) {
			fs.deleteBlocks(fs.getBlock(block.nextBlockId));
		}
		fs.setBlock(block.id, block);
		closed = true;
	}
	
	@Override
	protected void finalize() throws Throwable {
		close();
	}
	
	//region illegal
	
	/**
	 * Used to load a byte array from the buffer.<br>
	 * lenBytes must be between 1 and 4 (inclusive) for the array to load correctly.
	 */
	public byte[] readArr(int lenBytes) {
		throw new RuntimeException("This datapacker cannot read!");
	}
	
	/**
	 * Used to load a byte array from the buffer.
	 * Note that this assumes there is 4 length bytes.
	 */
	public byte[] readArr() {
		throw new RuntimeException("This datapacker cannot read!");
	}
	
	/**
	 * Used to load a byte array from the buffer.
	 * Note that this assumes there is no length encoded and thus requires a length argument.
	 */
	public byte[] readArrRaw(int len) {
		throw new RuntimeException("This datapacker cannot read!");
	}
	
	/** Used to load an integer from the buffer. */
	public int readInt() {
		throw new RuntimeException("This datapacker cannot read!");
	}
	
	/** Used to load a short from the buffer. */
	public int readShort() {
		throw new RuntimeException("This datapacker cannot read!");
	}
	
	/** Used to load a long from the buffer. */
	public long readLong() {
		throw new RuntimeException("This datapacker cannot read!");
	}
	
	/** Used to load a byte from the buffer. */
	public byte readByte() {
		throw new RuntimeException("This datapacker cannot read!");
	}
	
	/** Used to load a float from the buffer. */
	public float readFloat() {
		throw new RuntimeException("This datapacker cannot read!");
	}
	
	/** Used to load a double from the buffer. */
	public double readDouble() {
		throw new RuntimeException("This datapacker cannot read!");
	}
	
	/** Used to load a string from the buffer. */
	public String readString() {
		throw new RuntimeException("This datapacker cannot read!");
	}
	
	//endregion illegal
	
}
