package forfilesystemssake;

public class FFSFile {
	
	/** Underlying folders, index 0 is folder in root folder. */
	protected String[] folders;
	protected String name;
	protected byte[] data;
	protected FFSBase fs;
	protected long startingBlock;
	
	public FFSFile(long startingBlock) {
		this.startingBlock = startingBlock;
	}
	
}
