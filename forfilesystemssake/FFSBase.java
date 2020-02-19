package forfilesystemssake;

import jutils.database.DataPacker;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * A java variant of ForFilesystemsSake so that i can interact with the FS i use with my breadboard CPU.<br>
 * See {@link Header} for the header format.<br>
 * A folder structure directly follows the header as the root folder.<br>
 * Block format is only the last 4 bytes being reserved for the pointer to the next block.<br>
 * Note: Block pointer of null means unused block.<br>
 * Note: Block pointer matching block id means last block in chain.<br>
 */
public abstract class FFSBase {
	
	protected boolean isInitialised;
	protected Header header;
	
	public byte[] getOSData() {
		return header.osData;
	}
	
	public byte[] setOSData() {
		return header.osData;
	}
	
	public long getNumBlocks() {
		return header.numBlocks;
	}
	
	public double getBlocksUsed() {
		return header.blocksUsed;
	}
	
	public double getPartUsed() {
		return header.numBlocks / (double) header.blocksUsed;
	}
	
	/**
	 * For subclasses: this method must be overridden and then a super call at the end.
	 * For subclasses: you may want to ignore a call to this method if already initialised, as the base does too.
	 */
	public void initialise() throws IOException {
		if (isInitialised) {
			return;
		}
		header = new Header(new BlockReadDataPacker(this, 0));
		isInitialised = true;
	}
	
	public void close() throws IOException {
		if (!isInitialised) {
			return;
		}
		BlockWriteDataPacker packer = new BlockWriteDataPacker(this, 0);
		header.getData(packer);
		packer.close();
		isInitialised = false;
	}
	
	@Override
	protected void finalize() throws Throwable {
		close();
	}
	
	protected void deleteBlocks(Block startingBlock) throws IOException {
		List<Block> blocks = new ArrayList<>();
		Block lastBlock;
		blocks.add(lastBlock = startingBlock);
		while (lastBlock.nextBlockId != lastBlock.id && lastBlock.id != 0) {
			blocks.add(lastBlock = getBlock(lastBlock.nextBlockId));
		}
		for (Block block : blocks) {
			Block newBlock = new Block(block.id, header.blockLength);
			newBlock.nextBlockId = 0;
			setBlock(block.id, newBlock);
		}
	}
	
	protected Block[] getBlocks(long startingId) throws IOException {
		List<Block> blocks = new ArrayList<>();
		Block lastBlock;
		blocks.add(lastBlock = getBlock(startingId));
		while (lastBlock.nextBlockId != lastBlock.id) {
			if (lastBlock.id == 0) {
				throw new IOException(String.format("Block %08x points at block %08x, but block %08x is marked unused!", lastBlock.id, lastBlock.nextBlockId, lastBlock.nextBlockId));
			}
			blocks.add(lastBlock = getBlock(lastBlock.nextBlockId));
		}
		return blocks.toArray(new Block[0]);
	}
	
	protected abstract Block getBlock(long id) throws IOException;
	
	protected abstract void setBlock(long id, Block block) throws IOException;
	
	/**
	 * For subclasses: overriding is very much recommended.
	 */
	protected Block getFreeBlock() throws IOException {
		for (long i = 0; i < header.numBlocks; i++) {
			Block block = getBlock(i);
			if (block.nextBlockId == 0) {
				header.blocksUsed ++;
				return block;
			}
		}
		throw new IOException("No more free blocks!");
	}
	
	/**
	 * Format:<br>
	 * block length (2 bytes, at least 5, in bytes)<br>
	 * number of blocks (4 bytes, more than 8 blocks is recommended)<br>
	 * number of blocks used (4 bytes)
	 * FS name length (2 bytes, no minimum)<br>
	 * FS name (ASCII is recommended)<br>
	 * OS data length (2 bytes, no minimum)<br>
	 * OS data (anything)<br><br>
	 *
	 * Note: last 4 bytes of block are used by FFS to indicate the next block, or null for none
	 */
	public static class Header {
		
		public int blockLength;
		public long numBlocks;
		public long blocksUsed;
		public String name;
		public byte[] osData;
		
		public Header() {
		
		}
		
		public Header(DataPacker data) {
			blockLength = data.readShort();
			numBlocks = data.readInt() & (long) 0xFFFFFFFF;
			blocksUsed = data.readInt() & (long) 0xFFFFFFFF;
			name = new String(data.readArrRaw(data.readShort() & 0xFFFF));
			osData = data.readArr(data.readShort() & 0xFFFF);
		}
		
		public void getData(DataPacker data) {
			data.addShort(blockLength);
			data.addInt((int) numBlocks);
			data.addInt((int) blocksUsed);
			data.addShort(name.getBytes(StandardCharsets.US_ASCII).length);
			data.addArrRaw(name.getBytes(StandardCharsets.US_ASCII));
			data.addShort(osData.length);
			data.addArrRaw(osData);
		}
		
	}
	
}
