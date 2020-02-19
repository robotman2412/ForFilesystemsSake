package forfilesystemssake;

import java.io.IOException;

public class FileReadDataPacker extends BlockReadDataPacker {
	
	public int maxRead;
	public int totalRead;
	
	protected FileReadDataPacker(FFSBase fs, long startingBlock) throws IOException {
		super(fs, startingBlock);
		index = 8;
		maxRead = readInt();
	}
	
	/** Used to load a byte from the buffer. */
	@Override
	public byte readByte() {
		totalRead ++;
		if (totalRead > maxRead) {
			throw new RuntimeException("No more data in file!");
		}
		byte ret = blocks[blockIndex].data[index];
		index ++;
		if (index >= blocks[blockIndex].data.length) {
			index = 0;
			blockIndex ++;
		}
		return ret;
	}
	
}
