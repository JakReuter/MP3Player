package MP3Player.util.general;

import com.example.demo2.ReadMP3Tag;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import org.apache.tika.metadata.Metadata;

import java.io.*;
import java.util.*;
import java.nio.*;

public class ReadMP3End {
    public static void main(String[] args) {
		com.example.demo2.ReadMP3Tag tag = new ReadMP3Tag();
		tag.getMetadata("C:\\Users\\Brendan Reuter\\Music\\audio.mp3");
        /**try {
            // Open the MP3 file for reading
            RandomAccessFile raf = new RandomAccessFile("C:\\Users\\Brendan Reuter\\Music\\audio.mp3", "r");
			System.out.println(raf.length());
            // Seek to the end of the file and move back 128 bytes
            long length = raf.length();
            raf.seek(0);
            // Read the last 128 bytes of the file
            byte[] buffer = new byte[128];
            raf.read(buffer);
            
            String tag = new String(buffer); //entire footer
            /* structure of footer
	     * first 3 char = Tag
	     * next 30 char = title 
	     * next 30 char = artist
	     * next 30 char = album 
	     * next 4 char = year 
	     * next 30 char = comment
	     * last char = genre (genre is based on number represented by last byte)
	    String footerType = new String(Arrays.copyOfRange(buffer,0,3));
	    String title = new String(Arrays.copyOfRange(buffer,3,33));
	    String artist = new String(Arrays.copyOfRange(buffer,33,63));
	    String album = new String(Arrays.copyOfRange(buffer,63,93));
	    String year = new String(Arrays.copyOfRange(buffer,93,97));
	    String comment = new String(Arrays.copyOfRange(buffer,97,126));
	    //if we want the genre would need to convert the number to the associated genre type with a table, map or etc
	    int genre = buffer[127];

	    System.out.println("title: " + title);
	    System.out.println("artist: " + artist);
	    System.out.println("album: " + album);
	    System.out.println("year: " + year);
	    System.out.println("comment: " + comment);
            System.out.println(genre);

            
            // Close the file
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        } **/
    }
}
