import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.URL;

public class Random
{
	private final int NUM_BYTES = 1250;
	private File file = new File("random.dat");
	private RandomAccessFile ram = new RandomAccessFile(file, "rw");
	private long readpt, writept;
	
	public Random() throws IOException {
		if (ram.length() == 0) {
			ram.writeLong(16);
			ram.writeLong(0);
			writept = 16;
			newBytes();
		}
		ram.seek(0); 
		readpt = ram.readLong();		// pointer for reading bytes
		writept = ram.readLong();		// pointer for writing bytes
		ram.seek(readpt);				// resume reading file
	}
	
	public int nextInt(int l, int h) throws IOException {
		int m = (h - l) + 1;			// number of ranges needed
		int r = 256 / m;				// size of byte range
		int x = (r * m) - 1;			// maximum allowable byte value
		int b;
		do {
			try {						// get random byte from file
				b = ram.readUnsignedByte();
			} catch (EOFException e) {	// catch EOF, reset pointer
				b = 256; ram.seek(16);	// and set b to maximum value
			}							// so test will fail.
		} while(b > x);					// if byte is greater than
		readpt = ram.getFilePointer();	// allowable value, loop.
		return (b / r) + l;				// return random integer
	}									// within requested range
	
	public void close() throws IOException {
		ram.seek(0);					// move pointer to start of file
		ram.writeLong(readpt);			// save the read and write
		ram.writeLong(writept);			// pointer and close the
		ram.close();					// file
	}
	
	public void newBytes() throws IOException {
		URL url = new URL("http://www.random.org/integers/?num=" + (NUM_BYTES * 8) + "&min=0&max=1&col=8&base=2&format=plain&rnd=new");
		BufferedReader buffer = new BufferedReader(new InputStreamReader(url.openStream()));
		byte[] bytes = new byte[NUM_BYTES];		
		
		try {
			String line; int i = 0;
			while((line = buffer.readLine()) != null) {
				bytes[i++] = (byte) Integer.parseInt(line.replaceAll("\\s+", ""), 2);
			}
			if (writept >= 25016) writept = 16;
			ram.seek(writept);
			ram.write(bytes);
		} catch (Exception e) {
		} finally {
			writept = ram.getFilePointer(); buffer.close();
		}
	}
}