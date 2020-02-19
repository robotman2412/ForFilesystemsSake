package forfilesystemssake;

import jutils.JUtils;

import java.io.IOException;

public class FileWriteDataPacker extends BlockWriteDataPacker {
	
	Block startingBlock;
	int written;
	
	public FileWriteDataPacker(FFSBase fs, long startingBlock) throws IOException {
		super(fs, startingBlock);
		index = 12;
		this.startingBlock = block;
	}
	
	/** Used to append a byte to the buffer. */
	@Override
	public void addByte(int mData) {
		block.data[index] = (byte) mData;
		index ++;
		written ++;
		if (index >= block.data.length) {
			try {
				if (block.nextBlockId == block.id) {
					fs.setBlock(block.id, block);
					block = fs.getBlock(block.nextBlockId);
					index = 0;
				}
				else
				{
					Block nextBlock = fs.getFreeBlock();
					block.nextBlockId = nextBlock.id;
					fs.setBlock(block.id, block);
					block = nextBlock;
					index = 0;
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	@Override
	public void close() throws IOException {
		if (closed) {
			return;
		}
		byte[] len = JUtils.intToBytes(written);
		System.arraycopy(len, 0, startingBlock.data, 0, 4);
		fs.setBlock(startingBlock.id, startingBlock);
		if (block.nextBlockId != block.id) {
			fs.deleteBlocks(fs.getBlock(block.nextBlockId));
		}
		fs.setBlock(block.id, block);
		closed = true;
	}
	
}
