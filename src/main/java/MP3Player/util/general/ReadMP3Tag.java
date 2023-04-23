package MP3Player.util.general;
import java.io.IOException;
import MP3Player.database.Database;
import org.sqlite.SQLiteConfig;
import java.util.regex.Pattern;
import java.sql.*;
import java.io.*;
import java.util.*;
import java.nio.*;

public class ReadMP3Tag{
	String footerType ;
	String title = "";
	String artist = "" ;
	String album = "" ;
	String year = "";
	String comment = "";
    public void getMetadata(String args) {
		//parse file
		File file = new File(args);
		byte[] bytes = new byte[(int) file.length()];
		try(FileInputStream fis = new FileInputStream(file)) {
			//try {
			fis.read(bytes);

			String test = new String(Arrays.copyOfRange(bytes,0,3));
			if (test.compareTo("ID3") == 0){
				Scanner fileScanner = new Scanner(String.valueOf(bytes));
				String Line = fileScanner.nextLine();
				album = Line.split("TIT2")[1].split("TPE2")[0];
				artist = Line.split("TIT2")[1].split("TPE2")[1];
				String cleaned_album = album.replaceAll("[^\\x20-\\x7E]+", "");
				String cleaned_artist = artist.replaceAll("[^\\x20-\\x7E]+", "");
				album = cleaned_album;
				artist = cleaned_artist;
			}else{
				RandomAccessFile raf = new RandomAccessFile(args, "r");
				long length = raf.length();
				raf.seek(length - 128);

				// Read the last 128 bytes of the file
				byte[] buffer = new byte[128];
				raf.read(buffer);

				String tag = new String(buffer);
				if(tag.compareTo("ID3") ==0 ){

					/* structure of footer
					 * first 3 char = Tag
					 * next 30 char = title
					 * next 30 char = artist
					 * next 30 char = album
					 * next 4 char = year
					 * next 30 char = comment
					 * last char = genre (genre is based on number represented by last byte)
					 */
					String footerType = new String(Arrays.copyOfRange(buffer,0,3));
					String title = new String(Arrays.copyOfRange(buffer,3,33));
					artist = new String(Arrays.copyOfRange(buffer,33,63));
					album = new String(Arrays.copyOfRange(buffer,63,93));
					String year = new String(Arrays.copyOfRange(buffer,93,97));
					String comment = new String(Arrays.copyOfRange(buffer,97,126));
					//if we want the genre would need to convert the number to the associated genre type with a table, map or etc
					int genre = buffer[127];
					//System.out.println("artist: " + artist);
					//System.out.println("album: " + album);

					raf.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//upload results to database

			Database mp3db = new Database();
	        	mp3db.connect();
			mp3db.addNewSong(title, args, artist,album, 0);


	}

	public String getTitle() {
		return title;
	}

	public String getcomment() {
		return comment;
	}
	public String getyear() {
		return year;
	}
	public String getalbum() {
		return album;
	}
	public String getartist() {
		return artist;
	}
	public String getfooterType() {
		return footerType;
	}

}
