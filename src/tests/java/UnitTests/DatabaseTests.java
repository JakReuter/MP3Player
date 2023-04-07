package UnitTests;

import MP3Player.database.Database;
import org.junit.*;
import org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;


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

    public static void main(String[] args) {
        Database.connect();
        testing("Both");
        Database.close();
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



    /*@Before
    public void setup() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }*/

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
        Assert.assertEquals(error, outContent.toString());
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
        Assert.assertEquals(error, outContent.toString());
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
    public void addSongTestException1() {
        //arrange
        Database.connect();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream stdout = System.out;
        System.setOut(new PrintStream(outContent));
        String error = "[SQLITE_CONSTRAINT_PRIMARYKEY] A PRIMARY KEY constraint failed (UNIQUE constraint failed: Song.name)\r\n";
        //act
        Database.addNewSong("database_test_1", "testpath", "testauthor", "testalbum", 0);
        Database.addNewSong("database_test_1", "testpath", "testauthor", "testalbum", 0);
        Database.removeSong("database_test_1");
        //assert
        Assert.assertEquals(error, outContent.toString());
        //cleanup
        System.setOut(stdout);

        Database.close();
    }

    @Test
    public void addSongTestException2() {
        //arrange
        Database.connect();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream stdout = System.out;
        System.setOut(new PrintStream(outContent));
        String error = "[SQLITE_CONSTRAINT_NOTNULL] A NOT NULL constraint failed (NOT NULL constraint failed: Song.name)\r\n";
        //act
        Database.addNewSong(null, "testpath", "testauthor", "testalbum", 0);
        //assert
        Assert.assertEquals(error, outContent.toString());
        //cleanup
        System.setOut(stdout);
        Database.close();
    }

    @Test
    public void addSongTestException3() {
        //arrange
        Database.connect();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream stdout = System.out;
        System.setOut(new PrintStream(outContent));
        String error = "[SQLITE_CONSTRAINT_NOTNULL] A NOT NULL constraint failed (NOT NULL constraint failed: Song.filepath)\r\n";
        //act
        Database.addNewSong("database_test_2", null,null,null,0);
        Database.removeSong("database_test_2");
        //assert
        Assert.assertEquals(error, outContent.toString());
        //cleanup
        System.setOut(stdout);

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
    public void addPlaylistTestException1() {
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
        Assert.assertEquals(error, outContent.toString());
        //cleanup
        System.setOut(stdout);
        Database.removePlaylist("database_test_1");
        Database.close();
    }

    @Test
    public void addPlaylistTestException2() {
        //arrange
        Database.connect();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream stdout = System.out;
        System.setOut(new PrintStream(outContent));
        String error = "[SQLITE_CONSTRAINT_NOTNULL] A NOT NULL constraint failed (NOT NULL constraint failed: Playlist.name)\r\n";
        //act
        Database.createNewPlaylist(null, "test description");
        //assert
        Assert.assertEquals(error, outContent.toString());
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

    @Test
    public void getSongInfoTest1() throws SQLException {
        //arrange
        Database.connect();
        Database.addNewSong("database_test_1", "testpath", "testauthor", "testalbum", 0);
        Database.addNewSong("database_test_2", "testpath", "testauthor", "testalbum", 0);
        //act
        ResultSet rs = Database.getSongInfo("database_test_1");
        rs.next();
        //assert
        Assert.assertEquals("database_test_1", rs.getObject("name"));
        Assert.assertEquals("testpath", rs.getObject("filepath"));
        Assert.assertEquals("testauthor", rs.getObject("author"));
        Assert.assertEquals("testalbum", rs.getObject("album"));
        Assert.assertEquals(0, rs.getObject("duration"));
        //cleanup
        Database.removeSong("database_test_2");
        Database.removeSong("database_test_1");
        Database.close();
    }

    @Test
    public void getPlaylistInfoTest1() throws SQLException {
        //arrange
        Database.connect();
        Database.createNewPlaylist("database_test_1", "test description");
        Database.createNewPlaylist("database_test_2", "test description");
        //act
        ResultSet rs = Database.getPlaylistInfo("database_test_1");
        rs.next();
        //assert
        Assert.assertEquals("database_test_1", rs.getObject("name"));
        Assert.assertEquals("test description", rs.getObject("description"));
        Assert.assertEquals(0, rs.getObject("duration"));
        Assert.assertEquals(0, rs.getObject("num_songs"));
        Assert.assertEquals(0, rs.getObject("duration"));
        //cleanup
        Database.removePlaylist("database_test_1");
        Database.removePlaylist("database_test_2");
        Database.close();
    }

    @Test
    public void editSongTest1() throws SQLException {
        //arrange
        Database.connect();
        Database.addNewSong("database_test_1", "testpath", "testauthor", "testalbum", 0);
        //act
        Database.editSong("database_test_1", "pathtest", "authortest", "albumtest");
        ResultSet rs = Database.getSongInfo("database_test_1");
        rs.next();
        //assert
        Assert.assertEquals("database_test_1", rs.getObject("name"));
        Assert.assertEquals("pathtest", rs.getObject("filepath"));
        Assert.assertEquals("authortest", rs.getObject("author"));
        Assert.assertEquals("albumtest", rs.getObject("album"));
        Assert.assertEquals(0, rs.getObject("duration"));
        //cleanup
        Database.removeSong("database_test_1");
        Database.close();
    }

    @Test
    public void editPlaylistTest1() throws SQLException {
        //arrange
        Database.connect();
        Database.createNewPlaylist("database_test_1", "test description");
        //act
        Database.editPlaylist("database_test_1", "new description");
        ResultSet rs = Database.getPlaylistInfo("database_test_1");
        rs.next();
        //assert
        Assert.assertEquals("database_test_1", rs.getObject("name"));
        Assert.assertEquals("new description", rs.getObject("description"));
        Assert.assertEquals(0, rs.getObject("duration"));
        Assert.assertEquals(0, rs.getObject("num_songs"));
        Assert.assertEquals(0, rs.getObject("duration"));
        //cleanup
        Database.removePlaylist("database_test_1");
        Database.close();
    }

    @Test
    public void getSongInfoException1() {
        //arrange
        Database.connect();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream stdout = System.out;
        System.setOut(new PrintStream(outContent));
        String error = "[SQLITE_CONSTRAINT_NOTNULL] A NOT NULL constraint failed (NOT NULL constraint failed: Song.filepath)\r\n";
        //act
        Database.addNewSong("database_test_1", "testpath", "testauthor", "testalbum", 0);
        Database.editSong("database_test_1", null, "testauthor", "testalbum");
        Database.removeSong("database_test_1");
        //assert
        Assert.assertEquals(error, outContent.toString());
        //cleanup
        System.setOut(stdout);
        Database.close();
    }

    @Test
    public void selectSongsFromPlaylistTest1() throws SQLException {
        //arrange
        Database.connect();
        Database.createNewPlaylist("database_test_1", "test description");
        //act
        Database.editPlaylist("database_test_1", "new description");
        ResultSet rs = Database.getPlaylistInfo("database_test_1");
        rs.next();
        //assert
        Assert.assertEquals("database_test_1", rs.getObject("name"));
        Assert.assertEquals("new description", rs.getObject("description"));
        Assert.assertEquals(0, rs.getObject("duration"));
        Assert.assertEquals(0, rs.getObject("num_songs"));
        Assert.assertEquals(0, rs.getObject("duration"));
        //cleanup
        Database.removePlaylist("database_test_1");
        Database.close();
    }

    @Test
    public void addSongToPlaylistTest1() throws SQLException {
        //arrange
        Database.connect();
        Database.createNewPlaylist("database_test_playlist_1", "test description");
        Database.addNewSong("database_test_song_1", "testpath", "testauthor", "testalbum", 0);
        //act
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_1");
        ResultSet rs = Database.selectSongsFromPlaylist("database_test_playlist_1");
        rs.next();
        //assert
        Assert.assertEquals("database_test_song_1", rs.getObject("name"));
        Assert.assertEquals("testpath", rs.getObject("filepath"));
        Assert.assertEquals("testauthor", rs.getObject("author"));
        Assert.assertEquals(0, rs.getObject("duration"));
        Assert.assertEquals(0, rs.getObject("position"));
        //cleanup
        Database.removeSongFromPlaylist("database_test_playlist_1", "database_test_song_1");
        Database.removePlaylist("database_test_playlist_1");
        Database.removeSong("database_test_song_1");
        Database.close();
    }

    @Test
    public void addSongToPlaylistTest2() throws SQLException {
        //arrange
        Database.connect();
        Database.createNewPlaylist("database_test_playlist_1", "test description");
        Database.addNewSong("database_test_song_1", "testpath", "testauthor", "testalbum", 0);
        //act
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_1", 0);
        ResultSet rs = Database.selectSongsFromPlaylist("database_test_playlist_1");
        rs.next();
        //assert
        Assert.assertEquals("database_test_song_1", rs.getObject("name"));
        Assert.assertEquals("testpath", rs.getObject("filepath"));
        Assert.assertEquals("testauthor", rs.getObject("author"));
        Assert.assertEquals(0, rs.getObject("duration"));
        Assert.assertEquals(0, rs.getObject("position"));
        //cleanup
        Database.removeSongFromPlaylist("database_test_playlist_1", "database_test_song_1");
        Database.removePlaylist("database_test_playlist_1");
        Database.removeSong("database_test_song_1");
        Database.close();
    }

    @Test
    public void addSongToPlaylistTest3() throws SQLException {
        //arrange
        Database.connect();
        Database.createNewPlaylist("database_test_playlist_1", "test description");
        Database.addNewSong("database_test_song_1", "testpath", "testauthor", "testalbum", 0);
        Database.addNewSong("database_test_song_2", "pathtest", "authortest", "albumtest", 150);
        //act
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_1");
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_2", 1);
        ResultSet rs = Database.selectSongsFromPlaylist("database_test_playlist_1");
        rs.next();
        //assert
        Assert.assertEquals("database_test_song_1", rs.getObject("name"));
        Assert.assertEquals(0, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_2", rs.getObject("name"));
        Assert.assertEquals(1, rs.getObject("position"));
        //cleanup
        Database.removeSongFromPlaylist("database_test_playlist_1", "database_test_song_1");
        Database.removePlaylist("database_test_playlist_1");
        Database.removeSong("database_test_song_1");
        Database.removeSong("database_test_song_2");
        Database.close();
    }

    @Test
    public void addSongToPlaylistTest4() throws SQLException {
        //arrange
        Database.connect();
        Database.createNewPlaylist("database_test_playlist_1", "test description");
        Database.addNewSong("database_test_song_1", "testpath", "testauthor", "testalbum", 0);
        Database.addNewSong("database_test_song_2", "pathtest", "authortest", "albumtest", 150);
        //act
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_1");
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_2", 0);
        ResultSet rs = Database.selectSongsFromPlaylist("database_test_playlist_1");
        rs.next();
        //assert
        Assert.assertEquals("database_test_song_2", rs.getObject("name"));
        Assert.assertEquals(0, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_1", rs.getObject("name"));
        Assert.assertEquals(1, rs.getObject("position"));
        //cleanup
        Database.removeSongFromPlaylist("database_test_playlist_1", "database_test_song_1");
        Database.removePlaylist("database_test_playlist_1");
        Database.removeSong("database_test_song_1");
        Database.removeSong("database_test_song_2");
        Database.close();
    }

    @Test
    public void addSongToPlaylistTest5() throws SQLException {
        //arrange
        Database.connect();
        Database.createNewPlaylist("database_test_playlist_1", "test description");
        Database.addNewSong("database_test_song_1", "testpath", "testauthor", "testalbum", 0);
        Database.addNewSong("database_test_song_2", "pathtest", "authortest", "albumtest", 150);
        Database.addNewSong("database_test_song_3", "path test", "author test", "album test", 20);
        //act
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_1");
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_2", 1);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_3", 0);
        ResultSet rs = Database.selectSongsFromPlaylist("database_test_playlist_1");
        rs.next();
        //assert
        Assert.assertEquals("database_test_song_3", rs.getObject("name"));
        Assert.assertEquals(0, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_1", rs.getObject("name"));
        Assert.assertEquals(1, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_2", rs.getObject("name"));
        Assert.assertEquals(2, rs.getObject("position"));
        //cleanup
        Database.removeSongFromPlaylist("database_test_playlist_1", "database_test_song_1");
        Database.removePlaylist("database_test_playlist_1");
        Database.removeSong("database_test_song_1");
        Database.removeSong("database_test_song_2");
        Database.removeSong("database_test_song_3");
        Database.close();
    }

    @Test
    public void addSongToPlaylistTest6() throws SQLException {
        //arrange
        Database.connect();
        Database.createNewPlaylist("database_test_playlist_1", "test description");
        Database.addNewSong("database_test_song_1", "testpath", "testauthor", "testalbum", 0);
        Database.addNewSong("database_test_song_2", "pathtest", "authortest", "albumtest", 150);
        Database.addNewSong("database_test_song_3", "path test", "author test", "album test", 20);
        Database.addNewSong("database_test_song_4", "testingpath", "testingauthor", "testingalbum", 70);
        //act
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_1");
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_2", 1);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_3", 0);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_4", 3);
        ResultSet rs = Database.selectSongsFromPlaylist("database_test_playlist_1");
        rs.next();
        //assert
        Assert.assertEquals("database_test_song_3", rs.getObject("name"));
        Assert.assertEquals(0, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_1", rs.getObject("name"));
        Assert.assertEquals(1, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_2", rs.getObject("name"));
        Assert.assertEquals(2, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_4", rs.getObject("name"));
        Assert.assertEquals(3, rs.getObject("position"));
        //cleanup
        Database.removeSongFromPlaylist("database_test_playlist_1", "database_test_song_1");
        Database.removePlaylist("database_test_playlist_1");
        Database.removeSong("database_test_song_1");
        Database.removeSong("database_test_song_2");
        Database.removeSong("database_test_song_3");
        Database.removeSong("database_test_song_4");
        Database.close();
    }

    @Test
    public void addSongToPlaylistTest7() throws SQLException {
        //arrange
        Database.connect();
        Database.createNewPlaylist("database_test_playlist_1", "test description");
        Database.addNewSong("database_test_song_1", "testpath", "testauthor", "testalbum", 0);
        Database.addNewSong("database_test_song_2", "pathtest", "authortest", "albumtest", 150);
        Database.addNewSong("database_test_song_3", "path test", "author test", "album test", 20);
        Database.addNewSong("database_test_song_4", "testingpath", "testingauthor", "testingalbum", 70);
        //act
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_1", 0);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_2", 1);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_3", 2);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_4", 3);
        Database.moveSongInPlaylist("database_test_playlist_1", "database_test_song_3", 1);
        ResultSet rs = Database.selectSongsFromPlaylist("database_test_playlist_1");
        rs.next();
        //assert
        Assert.assertEquals("database_test_song_1", rs.getObject("name"));
        Assert.assertEquals(0, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_3", rs.getObject("name"));
        Assert.assertEquals(1, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_2", rs.getObject("name"));
        Assert.assertEquals(2, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_4", rs.getObject("name"));
        Assert.assertEquals(3, rs.getObject("position"));
        //cleanup
        Database.removePlaylist("database_test_playlist_1");
        Database.removeSong("database_test_song_1");
        Database.removeSong("database_test_song_2");
        Database.removeSong("database_test_song_3");
        Database.removeSong("database_test_song_4");
        Database.close();
    }

    @Test
    public void addSongToPlaylistTest8() throws SQLException {
        //arrange
        Database.connect();
        Database.createNewPlaylist("database_test_playlist_1", "test description");
        Database.addNewSong("database_test_song_1", "testpath", "testauthor", "testalbum", 0);
        Database.addNewSong("database_test_song_2", "pathtest", "authortest", "albumtest", 150);
        Database.addNewSong("database_test_song_3", "path test", "author test", "album test", 20);
        Database.addNewSong("database_test_song_4", "testingpath", "testingauthor", "testingalbum", 70);
        //act
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_1", 0);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_2", 1);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_3", 2);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_4", 3);
        Database.moveSongInPlaylist("database_test_playlist_1", "database_test_song_3", 0);
        ResultSet rs = Database.selectSongsFromPlaylist("database_test_playlist_1");
        rs.next();
        //assert
        Assert.assertEquals("database_test_song_3", rs.getObject("name"));
        Assert.assertEquals(0, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_1", rs.getObject("name"));
        Assert.assertEquals(1, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_2", rs.getObject("name"));
        Assert.assertEquals(2, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_4", rs.getObject("name"));
        Assert.assertEquals(3, rs.getObject("position"));
        //cleanup
        Database.removePlaylist("database_test_playlist_1");
        Database.removeSong("database_test_song_1");
        Database.removeSong("database_test_song_2");
        Database.removeSong("database_test_song_3");
        Database.removeSong("database_test_song_4");
        Database.close();
    }

    @Test
    public void addSongToPlaylistException1() {
        //arrange
        Database.connect();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream stdout = System.out;
        System.setOut(new PrintStream(outContent));
        String error = "[SQLITE_CONSTRAINT_FOREIGNKEY] A foreign key constraint failed (FOREIGN KEY constraint failed)\r\n";
        //act
        Database.createNewPlaylist("database_test_playlist_1", "test description");
        //Database.addNewSong("database_test_song_1", "testpath", "testauthor", "testalbum", 0);
        Database.addSongToPlaylist("database_test_playlist_1","database_test_song_2" );
        Database.removePlaylist("database_test_playlist_1");
        //Database.removeSong("database_test_song_1");
        //assert
        Assert.assertEquals(error, outContent.toString());
        //cleanup

        System.setOut(stdout);
        Database.close();
    }

    @Test
    public void addSongToPlaylistException2() {
        //arrange
        Database.connect();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream stdout = System.out;
        System.setOut(new PrintStream(outContent));
        String error = "[SQLITE_CONSTRAINT_FOREIGNKEY] A foreign key constraint failed (FOREIGN KEY constraint failed)\r\n";
        //act
        //Database.createNewPlaylist("database_test_playlist_1", "test description");
        Database.addNewSong("database_test_song_1", "testpath", "testauthor", "testalbum", 0);
        Database.addSongToPlaylist("database_test_playlist_1","database_test_song_2" );
        //Database.removePlaylist("database_test_playlist_1");
        Database.removeSong("database_test_song_1");
        //assert
        Assert.assertEquals(error, outContent.toString());
        //cleanup

        System.setOut(stdout);
        Database.close();
    }

    @Test
    public void moveSongInPlaylist1() throws SQLException {
        //arrange
        Database.connect();
        Database.createNewPlaylist("database_test_playlist_1", "test description");
        Database.addNewSong("database_test_song_1", "testpath", "testauthor", "testalbum", 0);
        Database.addNewSong("database_test_song_2", "pathtest", "authortest", "albumtest", 150);
        Database.addNewSong("database_test_song_3", "path test", "author test", "album test", 20);
        Database.addNewSong("database_test_song_4", "testingpath", "testingauthor", "testingalbum", 70);
        //act
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_1", 0);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_2", 1);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_3", 2);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_4", 3);
        Database.moveSongInPlaylist("database_test_playlist_1", "database_test_song_1", 3);
        ResultSet rs = Database.selectSongsFromPlaylist("database_test_playlist_1");
        rs.next();
        //assert
        Assert.assertEquals("database_test_song_2", rs.getObject("name"));
        Assert.assertEquals(0, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_3", rs.getObject("name"));
        Assert.assertEquals(1, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_4", rs.getObject("name"));
        Assert.assertEquals(2, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_1", rs.getObject("name"));
        Assert.assertEquals(3, rs.getObject("position"));
        //cleanup
        Database.removePlaylist("database_test_playlist_1");
        Database.removeSong("database_test_song_1");
        Database.removeSong("database_test_song_2");
        Database.removeSong("database_test_song_3");
        Database.removeSong("database_test_song_4");
        Database.close();
    }

    @Test
    public void moveSongInPlaylist2() throws SQLException {
        //arrange
        Database.connect();
        Database.createNewPlaylist("database_test_playlist_1", "test description");
        Database.addNewSong("database_test_song_1", "testpath", "testauthor", "testalbum", 0);
        Database.addNewSong("database_test_song_2", "pathtest", "authortest", "albumtest", 150);
        Database.addNewSong("database_test_song_3", "path test", "author test", "album test", 20);
        Database.addNewSong("database_test_song_4", "testingpath", "testingauthor", "testingalbum", 70);
        //act
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_1", 0);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_2", 1);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_3", 2);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_4", 3);
        Database.moveSongInPlaylist("database_test_playlist_1", "database_test_song_1", 3);
        Database.moveSongInPlaylist("database_test_playlist_1", "database_test_song_1", 0);
        ResultSet rs = Database.selectSongsFromPlaylist("database_test_playlist_1");
        rs.next();
        //assert
        Assert.assertEquals("database_test_song_1", rs.getObject("name"));
        Assert.assertEquals(0, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_2", rs.getObject("name"));
        Assert.assertEquals(1, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_3", rs.getObject("name"));
        Assert.assertEquals(2, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_4", rs.getObject("name"));
        Assert.assertEquals(3, rs.getObject("position"));
        //cleanup
        Database.removePlaylist("database_test_playlist_1");
        Database.removeSong("database_test_song_1");
        Database.removeSong("database_test_song_2");
        Database.removeSong("database_test_song_3");
        Database.removeSong("database_test_song_4");
        Database.close();
    }

    @Test
    public void moveSongInPlaylist3() throws SQLException {
        //arrange
        Database.connect();
        Database.createNewPlaylist("database_test_playlist_1", "test description");
        Database.addNewSong("database_test_song_1", "testpath", "testauthor", "testalbum", 0);
        Database.addNewSong("database_test_song_2", "pathtest", "authortest", "albumtest", 150);
        Database.addNewSong("database_test_song_3", "path test", "author test", "album test", 20);
        Database.addNewSong("database_test_song_4", "testingpath", "testingauthor", "testingalbum", 70);
        //act
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_1", 0);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_2", 1);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_3", 2);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_4", 3);
        Database.moveSongInPlaylist("database_test_playlist_1", "database_test_song_1", 0);
        ResultSet rs = Database.selectSongsFromPlaylist("database_test_playlist_1");
        rs.next();
        //assert
        Assert.assertEquals("database_test_song_1", rs.getObject("name"));
        Assert.assertEquals(0, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_2", rs.getObject("name"));
        Assert.assertEquals(1, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_3", rs.getObject("name"));
        Assert.assertEquals(2, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_4", rs.getObject("name"));
        Assert.assertEquals(3, rs.getObject("position"));
        //cleanup
        Database.removePlaylist("database_test_playlist_1");
        Database.removeSong("database_test_song_1");
        Database.removeSong("database_test_song_2");
        Database.removeSong("database_test_song_3");
        Database.removeSong("database_test_song_4");
        Database.close();
    }

    @Test
    public void moveSongInPlaylist4() throws SQLException {
        //arrange
        Database.connect();
        Database.createNewPlaylist("database_test_playlist_1", "test description");
        Database.addNewSong("database_test_song_1", "testpath", "testauthor", "testalbum", 0);
        Database.addNewSong("database_test_song_2", "pathtest", "authortest", "albumtest", 150);
        Database.addNewSong("database_test_song_3", "path test", "author test", "album test", 20);
        Database.addNewSong("database_test_song_4", "testingpath", "testingauthor", "testingalbum", 70);
        //act
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_1", 0);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_2", 1);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_3", 2);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_4", 3);
        Database.moveSongInPlaylist("database_test_playlist_1", "database_test_song_4", 3);
        ResultSet rs = Database.selectSongsFromPlaylist("database_test_playlist_1");
        rs.next();
        //assert
        Assert.assertEquals("database_test_song_1", rs.getObject("name"));
        Assert.assertEquals(0, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_2", rs.getObject("name"));
        Assert.assertEquals(1, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_3", rs.getObject("name"));
        Assert.assertEquals(2, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_4", rs.getObject("name"));
        Assert.assertEquals(3, rs.getObject("position"));
        //cleanup
        Database.removePlaylist("database_test_playlist_1");
        Database.removeSong("database_test_song_1");
        Database.removeSong("database_test_song_2");
        Database.removeSong("database_test_song_3");
        Database.removeSong("database_test_song_4");
        Database.close();
    }

    @Test
    public void removeSongInPlaylist1() throws SQLException {
        //arrange
        Database.connect();
        Database.createNewPlaylist("database_test_playlist_1", "test description");
        Database.addNewSong("database_test_song_1", "testpath", "testauthor", "testalbum", 0);
        Database.addNewSong("database_test_song_2", "pathtest", "authortest", "albumtest", 150);
        Database.addNewSong("database_test_song_3", "path test", "author test", "album test", 20);
        Database.addNewSong("database_test_song_4", "testingpath", "testingauthor", "testingalbum", 70);
        //act
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_1", 0);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_2", 1);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_3", 2);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_4", 3);
        Database.removeSongFromPlaylist("database_test_playlist_1", "database_test_song_4");
        ResultSet rs = Database.selectSongsFromPlaylist("database_test_playlist_1");
        rs.next();
        //assert
        Assert.assertEquals("database_test_song_1", rs.getObject("name"));
        Assert.assertEquals(0, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_2", rs.getObject("name"));
        Assert.assertEquals(1, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_3", rs.getObject("name"));
        Assert.assertEquals(2, rs.getObject("position"));
        rs.next();
        Assert.assertNull(rs.getObject("name"));
        Assert.assertNull(rs.getObject("position"));
        //cleanup
        Database.removePlaylist("database_test_playlist_1");
        Database.removeSong("database_test_song_1");
        Database.removeSong("database_test_song_2");
        Database.removeSong("database_test_song_3");
        Database.removeSong("database_test_song_4");
        Database.close();
    }

    @Test
    public void removeSongInPlaylist2() throws SQLException {
        //arrange
        Database.connect();
        Database.createNewPlaylist("database_test_playlist_1", "test description");
        Database.addNewSong("database_test_song_1", "testpath", "testauthor", "testalbum", 0);
        Database.addNewSong("database_test_song_2", "pathtest", "authortest", "albumtest", 150);
        Database.addNewSong("database_test_song_3", "path test", "author test", "album test", 20);
        Database.addNewSong("database_test_song_4", "testingpath", "testingauthor", "testingalbum", 70);
        //act
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_1", 0);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_2", 1);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_3", 2);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_4", 3);
        Database.removeSongFromPlaylist("database_test_playlist_1", "database_test_song_1");
        ResultSet rs = Database.selectSongsFromPlaylist("database_test_playlist_1");
        rs.next();
        //assert
        Assert.assertEquals("database_test_song_2", rs.getObject("name"));
        Assert.assertEquals(0, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_3", rs.getObject("name"));
        Assert.assertEquals(1, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_4", rs.getObject("name"));
        Assert.assertEquals(2, rs.getObject("position"));
        rs.next();
        Assert.assertNull(rs.getObject("name"));
        Assert.assertNull(rs.getObject("position"));
        //cleanup
        Database.removePlaylist("database_test_playlist_1");
        Database.removeSong("database_test_song_1");
        Database.removeSong("database_test_song_2");
        Database.removeSong("database_test_song_3");
        Database.removeSong("database_test_song_4");
        Database.close();
    }

    @Test
    public void removeSongInPlaylist3() throws SQLException {
        //arrange
        Database.connect();
        Database.createNewPlaylist("database_test_playlist_1", "test description");
        Database.addNewSong("database_test_song_1", "testpath", "testauthor", "testalbum", 0);
        Database.addNewSong("database_test_song_2", "pathtest", "authortest", "albumtest", 150);
        Database.addNewSong("database_test_song_3", "path test", "author test", "album test", 20);
        Database.addNewSong("database_test_song_4", "testingpath", "testingauthor", "testingalbum", 70);
        //act
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_1", 0);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_2", 1);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_3", 2);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_4", 3);
        Database.removeSongFromPlaylist("database_test_playlist_1", "database_test_song_1");
        Database.removeSongFromPlaylist("database_test_playlist_1", "database_test_song_3");
        ResultSet rs = Database.selectSongsFromPlaylist("database_test_playlist_1");
        rs.next();
        //assert
        Assert.assertEquals("database_test_song_2", rs.getObject("name"));
        Assert.assertEquals(0, rs.getObject("position"));
        rs.next();
        Assert.assertEquals("database_test_song_4", rs.getObject("name"));
        Assert.assertEquals(1, rs.getObject("position"));
        rs.next();
        Assert.assertNull(rs.getObject("name"));
        Assert.assertNull(rs.getObject("position"));
        rs.next();
        Assert.assertNull(rs.getObject("name"));
        Assert.assertNull(rs.getObject("position"));
        //cleanup
        Database.removePlaylist("database_test_playlist_1");
        Database.removeSong("database_test_song_1");
        Database.removeSong("database_test_song_2");
        Database.removeSong("database_test_song_3");
        Database.removeSong("database_test_song_4");
        Database.close();
    }

    @Test
    public void playlistUpdateTest1() throws SQLException {
        //arrange
        Database.connect();
        Database.createNewPlaylist("database_test_playlist_1", "test description");
        Database.addNewSong("database_test_song_1", "testpath", "testauthor", "testalbum", 0);
        Database.addNewSong("database_test_song_2", "pathtest", "authortest", "albumtest", 150);
        Database.addNewSong("database_test_song_3", "path test", "author test", "album test", 20);
        Database.addNewSong("database_test_song_4", "testingpath", "testingauthor", "testingalbum", 70);
        //act
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_1", 0);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_2", 1);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_3", 2);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_4", 3);
        Database.removeSongFromPlaylist("database_test_playlist_1", "database_test_song_1");
        Database.removeSongFromPlaylist("database_test_playlist_1", "database_test_song_3");
        ResultSet rs = Database.getPlaylistInfo("database_test_playlist_1");
        rs.next();
        //assert
        Assert.assertEquals(2, rs.getObject("num_songs"));
        Assert.assertEquals(220, rs.getObject("duration"));
        //cleanup
        Database.removePlaylist("database_test_playlist_1");
        Database.removeSong("database_test_song_1");
        Database.removeSong("database_test_song_2");
        Database.removeSong("database_test_song_3");
        Database.removeSong("database_test_song_4");
        Database.close();
    }

    @Test
    public void playlistUpdateTest2() throws SQLException {
        //arrange
        Database.connect();
        Database.createNewPlaylist("database_test_playlist_1", "test description");
        Database.addNewSong("database_test_song_1", "testpath", "testauthor", "testalbum", 0);
        Database.addNewSong("database_test_song_2", "pathtest", "authortest", "albumtest", 150);
        Database.addNewSong("database_test_song_3", "path test", "author test", "album test", 20);
        Database.addNewSong("database_test_song_4", "testingpath", "testingauthor", "testingalbum", 70);
        //act
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_1", 0);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_2", 1);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_3", 2);
        Database.addSongToPlaylist("database_test_playlist_1", "database_test_song_4", 3);
        ResultSet rs = Database.getPlaylistInfo("database_test_playlist_1");
        rs.next();
        //assert
        Assert.assertEquals(4, rs.getObject("num_songs"));
        Assert.assertEquals(240, rs.getObject("duration"));
        //cleanup
        //Database.removeSongFromPlaylist("database_test_playlist_1", "database_test_song_1");
        Database.removePlaylist("database_test_playlist_1");
        Database.removeSong("database_test_song_1");
        Database.removeSong("database_test_song_2");
        Database.removeSong("database_test_song_3");
        Database.removeSong("database_test_song_4");
        Database.close();
    }
}
