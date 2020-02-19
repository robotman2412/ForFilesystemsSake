package forfilesystemssake;

import jutils.database.DataPacker;

import java.io.IOException;

/**
 * Format (in file data):
 * number of file indicies (4 bytes)
 * file indicies
 *
 * Format (in file index):
 * file properties (1 byte)
 * file name length (1 byte, at least 1)
 * file name
 * file starting block (4 bytes)
 * file properties (1 byte)
 */
public class FFSFileFolder extends FFSFile {
	
	protected FFSFileFolder(FFSBase fs, String[] folders, String name, long startingBlock) throws IOException {
		super(fs, folders, name, startingBlock);
		loadFiles();
	}
	
	protected FFSFileFolder(FFSBase fs, long startingBlock) throws IOException {
		super(fs, new String[0], "", startingBlock);
		loadFiles();
	}
	
	protected void loadFiles() throws IOException {
		DataPacker data = read();
	}
	
}
