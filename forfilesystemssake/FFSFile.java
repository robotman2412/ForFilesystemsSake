package forfilesystemssake;

import jutils.database.DataPacker;

import java.io.IOException;

/**
 * Format (in block data):
 * number of blocks used (4 bytes, noncritical, at least 1)
 * parent folder starting block (4 bytes, null if in root folder)
 * file size (4 bytes, length of remaining content)
 * file content (anything)
 */
public class FFSFile {
	
	/** Underlying folders, index 0 is folder in root folder. */
	protected String[] folders;
	protected String name;
	protected FFSBase fs;
	protected long parentFolderStartingBlock;
	protected long startingBlock;
	protected int properties;
	protected int numBlocksUsed;
	
	protected FFSFile(FFSBase fs, String[] folders, String name, long startingBlock) throws IOException {
		this.fs = fs;
		this.startingBlock = startingBlock;
		this.name = name;
		this.folders = folders;
		Block block = fs.getBlock(startingBlock);
		DataPacker data = new DataPacker(block.data);
		numBlocksUsed = data.readInt();
		parentFolderStartingBlock = data.readInt() & (long) 0xFFFFFFFF;
	}
	
	public BlockReadDataPacker read() throws IOException {
		if (isDirectory.get()) {
			throw new RuntimeException("Reading the content of a directory is not allowed!");
		}
		BlockReadDataPacker data = new BlockReadDataPacker(fs, startingBlock);
		data.index = 8;
		return data;
	}
	
	public BlockWriteDataPacker write() throws IOException {
		if (isDirectory.get()) {
			throw new RuntimeException("Reading the content of a directory is not allowed!");
		}
		BlockWriteDataPacker data = new BlockWriteDataPacker(fs, startingBlock);
		data.index = 8;
		return data;
	}
	
	public String getName() {
		return name;
	}
	
	public void rename(String newName) throws IOException {
		if (parentFolderStartingBlock == 0) {
			throw new RuntimeException("Root folder cannot be renamed!");
		}
		FFSFileFolder parent = new FFSFileFolder(fs, parentFolderStartingBlock);
		
	}
	
	//region properies
	
	public Property isDirectory = new Property(Properties.DIRECTORY, true);
	public Property isSystem = new Property(Properties.SYSTEM);
	public Property isHidden = new Property(Properties.HIDDEN);
	public Property reservedProperty0 = new Property(Properties.RESERVED0, true);
	public Property reservedProperty1 = new Property(Properties.RESERVED1, true);
	public Property reservedProperty2 = new Property(Properties.RESERVED2, true);
	public Property reservedProperty3 = new Property(Properties.RESERVED3, true);
	public Property reservedProperty4 = new Property(Properties.RESERVED4, true);
	
	protected class Property {
		
		int mask;
		boolean readOnly;
		
		public Property(int mask, boolean readOnly) {
			this.mask = mask;
			this.readOnly = readOnly;
		}
		
		public Property(int mask) {
			this.mask = mask;
			readOnly = false;
		}
		
		public boolean get() {
			return (properties & mask) > 0;
		}
		
		public void set(boolean value) {
			if (readOnly) {
				System.err.println("Attempted modification of read-only property!");
				Thread.dumpStack();
				return;
			}
			properties = (properties & ~mask) | (value ? mask : 0);
		}
		
		public void on() {
			if (readOnly) {
				System.err.println("Attempted modification of read-only property!");
				Thread.dumpStack();
				return;
			}
			properties |= mask;
		}
		
		public void off() {
			if (readOnly) {
				System.err.println("Attempted modification of read-only property!");
				Thread.dumpStack();
				return;
			}
			properties &= ~mask;
		}
		
	}
	
	public static class Properties {
		
		public static final int DIRECTORY = 0x00000001;
		public static final int SYSTEM = 0x00000002;
		public static final int HIDDEN = 0x00000004;
		public static final int RESERVED0 = 0x00000008;
		public static final int RESERVED1 = 0x00000010;
		public static final int RESERVED2 = 0x00000020;
		public static final int RESERVED3 = 0x00000040;
		public static final int RESERVED4 = 0x00000080;
		
	}
	
	//endregion properties
	
}
