package UnitTests;

import MP3Player.database.Database;
import org.junit.*;
import org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.ResultSet;


public class DatabaseTests {
    public static void testing(String type) {
        ResultSet rs;
        if (type.equals("Song")) {
            rs = Database.selectAllSongs();
        } else if (type.equals("Playlist")) {
            rs = Database.selectAllPlaylists();
        } else {
            rs = Database.selectAllSongsInPlaylists();
        }
        if (rs == null) {
            System.out.println("no rows found");
        } else {
            if (type.equals("Song")) {
                try {
                    int i = 0;
                    while (rs.next()) {
                        System.out.println(
                                "name: " + rs.getObject("name") + "\t" +
                                        "path: " + rs.getObject("filepath") + "\t" +
                                        "author: " + rs.getObject("author") + "\t" +
                                        "album: " + rs.getObject("album") + "\t" +
                                        "date: " + rs.getObject("date_added") + "\t");
                        i++;
                    }
                    if (i == 0) {
                        System.out.println("No output");
                    }
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            } else if (type.equals("Playlist")) {
                try {
                    int i = 0;
                    while (rs.next()) {
                        System.out.println(
                                "name: " + rs.getObject("name") + "\t" +
                                        "position: " + rs.getObject("position") + "\t" +
                                        "date: " + rs.getObject("date_created") + "\t");
                        i++;
                    }
                    if (i == 0) {
                        System.out.println("No output");
                    }
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            } else {
                try {
                    int i = 0;
                    while (rs.next()) {
                        System.out.println(
                                "PlaylistName: " + rs.getObject("playlist_name") + "\t" +
                                        "SongName: " + rs.getObject("song_name") + "\t" +
                                        "Position: " + rs.getObject("position") + "\t");
                        i++;
                    }
                    if (i == 0) {
                        System.out.println("No output");
                    }
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }

    public static void addRemoveTests() {
        System.out.println("Add/Remove Song Test");
        testing("Song");
        Database.addNewSong("testname", "testpath", "testauthor", "testalbum", 0);
        testing("Song");
        Database.removeSong("testname");
        testing("Song");
        System.out.println("Add/Remove Playlist Test");
        testing("Playlist");
        Database.createNewPlaylist("testplaylist", "test description");
        testing("Playlist");
        Database.removePlaylist("testplaylist");
        testing("Playlist");
    }

    public static void getSongInfoTest() {
        testing("Song");
        Database.addNewSong("testname", "testpath", "testauthor", "testalbum", 0);
        Database.addNewSong("testname2", "testpath", "testauthor", "testalbum", 0);
        ResultSet rs = Database.getSongInfo("testname");
        if (rs == null) {
            System.out.println("no rows found");
        } else {
            try {
                int i = 0;
                while (rs.next()) {
                    System.out.println(
                            "name: " + rs.getObject("name") + "\t" +
                                    "path: " + rs.getObject("filepath") + "\t" +
                                    "author: " + rs.getObject("author") + "\t" +
                                    "album: " + rs.getObject("album") + "\t" +
                                    "date: " + rs.getObject("date_added") + "\t");
                    i++;
                }
                if (i == 0) {
                    System.out.println("No output");
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        testing("Song");
        Database.removeSong("testname2");
        Database.removeSong("testname");
        testing("Song");
    }

    public static void getPlaylistInfoTest() {
        testing("Playlist");
        Database.createNewPlaylist("testplaylist", "test description");
        Database.createNewPlaylist("testplaylist2", "test description");
        ResultSet rs = Database.getPlaylistInfo("testplaylist");
        if (rs == null) {
            System.out.println("no rows found");
        } else {
            try {
                int i = 0;
                while (rs.next()) {
                    System.out.println(
                            "name: " + rs.getObject("name") + "\t" +
                                    "position: " + rs.getObject("position") + "\t" +
                                    "date: " + rs.getObject("date_created") + "\t");
                    i++;
                }
                if (i == 0) {
                    System.out.println("No output");
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        testing("Playlist");
        Database.removePlaylist("testplaylist");
        Database.removePlaylist("testplaylist2");
        testing("Playlist");
    }

    @Test
    public void addSongTest1() throws Exception {
        //arrange
        Database.connect();
        //act
        Database.addNewSong("database_test_1", "testpath", "testauthor", "testalbum", 0);
        ResultSet rs = Database.getSongInfo("database_test_1");
        rs.next();
        //assert
        Assert.assertEquals("database_test_1", rs.getObject("name"));
        Assert.assertEquals("testpath", rs.getObject("filepath"));
        Assert.assertEquals("testauthor", rs.getObject("author"));
        Assert.assertEquals("testalbum", rs.getObject("album"));
        Assert.assertEquals(0, rs.getObject("duration"));
        //cleanup
        Database.removeSong("database_test_1");
        Database.close();
    }

    @Test
    public void addSongTest2() throws Exception {
        //arrange
        Database.connect();
        //act
        Database.addNewSong("database_test_1", "testpath", "testauthor", "testalbum", 0);
        ResultSet rs = Database.getSongInfo("database_test_2");
        rs.next();
        //assert
        Assert.assertNull(rs.getObject("name"));
        //cleanup
        Database.removeSong("database_test_1");
        Database.close();
    }



    /*@Before
    public void setup() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }*/

    @Test
    public void addSongTest3() {
        //arrange
        Database.connect();
        Database.addNewSong("database_test_1", "testpath", "testauthor", "testalbum", 0);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream stdout = System.out;
        System.setOut(new PrintStream(outContent));
        String error = "[SQLITE_CONSTRAINT_PRIMARYKEY] A PRIMARY KEY constraint failed (UNIQUE constraint failed: Song.name)\r\n";
        //act
        Database.addNewSong("database_test_1", "testpath", "testauthor", "testalbum", 0);
        //assert
        Assert.assertEquals(error,outContent.toString());
        //cleanup
        System.setOut(stdout);
        Database.removeSong("database_test_1");
        Database.close();
    }

    @Test
    public void addSongTest4() {
        //arrange
        Database.connect();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream stdout = System.out;
        System.setOut(new PrintStream(outContent));
        String error = "[SQLITE_CONSTRAINT_NOTNULL] A NOT NULL constraint failed (NOT NULL constraint failed: Song.filepath)\r\n";
        //act
        Database.addNewSong("database_test_1", null, "testauthor", "testalbum", 0);
        //assert
        Assert.assertEquals(error,outContent.toString());
        //cleanup
        System.setOut(stdout);
        Database.removeSong("database_test_1");
        Database.close();
    }

    @Test
    public void addSongTest5() throws Exception {
        //arrange
        Database.connect();
        //act
        Database.addNewSong("database_test_1", "testpath", "testauthor", "testalbum", 0);
        Database.removeSong("database_test_1");
        ResultSet rs = Database.getSongInfo("database_test_1");
        rs.next();
        //assert
        Assert.assertNull(rs.getObject("name"));
        //cleanup
        Database.close();
    }

    @Test
    public void addPlaylistTest1() throws Exception {
        //arrange
        Database.connect();
        //act
        Database.createNewPlaylist("database_test_1", "test description");
        ResultSet rs = Database.getPlaylistInfo("database_test_1");
        rs.next();
        //assert
        Assert.assertEquals("database_test_1", rs.getObject("name"));
        Assert.assertEquals("test description", rs.getObject("description"));
        Assert.assertEquals(0, rs.getObject("num_songs"));
        Assert.assertEquals(0, rs.getObject("duration"));
        //cleanup
        Database.removePlaylist("database_test_1");
        Database.close();
    }

    @Test
    public void addPlaylistTest2() throws Exception {
        //arrange
        Database.connect();
        //act
        Database.createNewPlaylist("database_test_1", "test description");
        ResultSet rs = Database.getPlaylistInfo("database_test_2");
        rs.next();
        //assert
        Assert.assertNull(rs.getObject("name"));
        //cleanup
        Database.removePlaylist("database_test_1");
        Database.close();
    }

    @Test
    public void addPlaylistTest3() {
        //arrange
        Database.connect();
        Database.createNewPlaylist("database_test_1", "test description");
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream stdout = System.out;
        System.setOut(new PrintStream(outContent));
        String error = "[SQLITE_CONSTRAINT_PRIMARYKEY] A PRIMARY KEY constraint failed (UNIQUE constraint failed: Playlist.name)\r\n";
        //act
        Database.createNewPlaylist("database_test_1", "test description");
        //assert
        Assert.assertEquals(error,outContent.toString());
        //cleanup
        System.setOut(stdout);
        Database.removePlaylist("database_test_1");
        Database.close();
    }

    @Test
    public void addPlaylistTest4() {
        //arrange
        Database.connect();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream stdout = System.out;
        System.setOut(new PrintStream(outContent));
        String error = "[SQLITE_CONSTRAINT_NOTNULL] A NOT NULL constraint failed (NOT NULL constraint failed: Playlist.name)\r\n";
        //act
        Database.createNewPlaylist(null, "test description");
        //assert
        Assert.assertEquals(error,outContent.toString());
        //cleanup
        System.setOut(stdout);
        //Database.removePlaylist("database_test_1");
        Database.close();
    }

    @Test
    public void addPlaylistTest5() throws Exception {
        //arrange
        Database.connect();
        //act
        Database.createNewPlaylist("database_test_1", "test description");
        Database.removePlaylist("database_test_1");
        ResultSet rs = Database.getPlaylistInfo("database_test_1");
        rs.next();
        //assert
        Assert.assertNull(rs.getObject("name"));
        //cleanup
        Database.close();
    }

    public static void main(String[] args) {
        Database.connect();
        testing("Both");
        Database.close();
    }

}
