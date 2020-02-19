package forfilesystemssake;

import jutils.database.DataPacker;

public class Block {
	
	public byte[] data;
	public final long id;
	public long nextBlockId;
	
	public Block(long id, int blockSize) {
		this.id = id;
		data = new byte[blockSize - 4];
		nextBlockId = id;
	}
	
	public Block(long id, byte[] raw) {
		DataPacker data = new DataPacker(raw);
		this.data = data.readArr(raw.length - 4);
		//DO NOT SIMPLIFY!
		this.nextBlockId = ((long) data.readInt()) & (long) 0xFFFFFFFF;
		this.id = id;
	}
	
	/** Note: the block id is not included. */
	public byte[] getRaw() {
		DataPacker data = new DataPacker();
		data.addArrRaw(this.data);
		data.addInt((int) nextBlockId);
		return data.data;
	}
	
}