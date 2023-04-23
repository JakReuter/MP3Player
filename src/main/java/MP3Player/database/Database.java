package MP3Player.database;

import MP3Player.Popup.PopupWindow;
import org.sqlite.SQLiteConfig;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;

//TODO: Fully Test protection against unresonable positions in AddSongToPlaylist and MoveSongInPlaylist
//TODO: Design potential error returning
//TODO: Add position changing in Playlist table
//TODO: Implement Playlist integration with invalid filepaths

public class Database {
    static Connection conn = null;

    public static final String DB_URL = "jdbc:sqlite:mp3db.db";
    public static final String DRIVER = "org.sqlite.JDBC";

    public static Connection getConnection() throws ClassNotFoundException {
        Class.forName(DRIVER);
        Connection connection = null;
        try {
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            connection = DriverManager.getConnection(DB_URL, config.toProperties());
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return connection;
    }

    /**
     * Connect to the database
     */
    public static void connect() {

        try {
            // db parameters
            //String url = "jdbc:sqlite:mp3db.db";
            conn = getConnection();
            // create a connection to the database
            //conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Closes the connection to the database
     */
    public static void close() {
        try {
            if (conn != null) {
                System.out.println("Connection to SQLite has been closed");
                conn.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Creates a new playlist
     *
     * @param name name of the new Playlist
     */
    public static void createNewPlaylist(String name, String description) {
        String sql = "INSERT INTO Playlist VALUES(?,0, current_date, 0,?,0)";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            PopupWindow popupWindow = new PopupWindow();
            popupWindow.SQLError("There has been an error creating the playlist. Please close and reopen the application");
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Adds a new song to the master song table
     *
     * @param name   name of the new Song
     * @param path   filepath to the source file
     * @param author name of the author
     * @param album  name of the album
     */
    public static void addNewSong(String name, String path, String author, String album, int duration) {

        String sql = "INSERT INTO Song VALUES(?,?,?,?, current_date, ?, 1)";
        try {
            //Statement stmt = conn.createStatement();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, path);
            stmt.setString(3, author);
            stmt.setString(4, album);
            stmt.setInt(5, duration);
            stmt.executeUpdate();
            //ResultSet rs = stmt.executeQuery(sql);
        } catch (SQLException ex) {
            PopupWindow popupWindow = new PopupWindow();
            popupWindow.SQLError("There has been an error adding the song. Please close and reopen the application");
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Adds a new song to the master song table through parsing the mp3 file
     * This method takes in a mp3 file and then parses it within the method then adds it to the database
     *
     * @param mp3 File of the song to be added
     */
    public static void addNewSongMP3(File mp3) {
        String sql = "";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Adds a song to a playlist, inserts into the Songs_In_Playlist table
     *
     * @param songName     name of the Song to be added
     * @param playlistName name of the playlist the song is to be added to
     * @param position     position in the playlist for the song to be added to
     */
    public static void addSongToPlaylist(String playlistName, String songName, int position) {
        int count = checkPosition(playlistName,position);
        if (0 == count){
            throw new IndexOutOfBoundsException("This position is not valid");
        }
        String sql1 = "UPDATE Songs_In_Playlist SET position = (position + 1)*-1 WHERE playlist_name = ? and position >= ?";
        String sql = "INSERT INTO Songs_In_Playlist VALUES(?,?,?)";
        String sqlflip = "UPDATE Songs_In_Playlist SET position = (position)*-1 WHERE position < 0";
        try {
            int i = operationCheck("Both");
            PreparedStatement stmt = conn.prepareStatement(sql1);
            stmt.setString(1, playlistName);
            stmt.setInt(2, position);
            stmt.executeUpdate();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, playlistName);
            stmt.setString(2, songName);
            stmt.setInt(3, position);
            stmt.executeUpdate();
            stmt = conn.prepareStatement(sqlflip);
            stmt.executeUpdate();
            int j = operationCheck("Both");
            if (i != j) {
                updatePlaylist(playlistName, songName, 1);
            }
        } catch (SQLException ex) {
            PopupWindow popupWindow = new PopupWindow();
            popupWindow.SQLError("There has been an error adding the song to the playlist. Please close and reopen the application");
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Adds a song to a playlist, inserts into the Songs_In_Playlist table
     * Adds the song to the end of the playlist, useful for quick-adding songs
     *
     * @param songName     name of the Song to be added
     * @param playlistName name of the playlist the song is to be added to
     */
    public static void addSongToPlaylist(String playlistName, String songName) {
        String sql = "INSERT INTO Songs_In_Playlist VALUES(?,?,(SELECT ifnull(max(position),-1) FROM Songs_In_Playlist WHERE playlist_name = ?)+1)";
        try {
            int i = operationCheck("Both");
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, playlistName);
            stmt.setString(2, songName);
            stmt.setString(3, playlistName);
            stmt.executeUpdate();
            int j = operationCheck("Both");
            if (i != j) {
                updatePlaylist(playlistName, songName, 1);
            }
        } catch (SQLException ex) {
            PopupWindow popupWindow = new PopupWindow();
            popupWindow.SQLError("There has been an error adding the song to the playlist. Please close and reopen the application");
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Moves a songs position in a playlist in the Songs_In_Playlist table
     * Does this by incrementing every song after the given position, then reassigning the position attribute
     *
     * @param songName     name of the Song to be moved
     * @param playlistName name of the playlist the song is in
     * @param newPosition  position in the playlist for the song to be moved to
     */
    public static void moveSongInPlaylist(String playlistName, String songName, int newPosition) {
        int count = checkPosition(playlistName,newPosition);
        if (0 == count){
            PopupWindow popupWindow = new PopupWindow();
            popupWindow.SQLError("This position is not valid");
            //throw new IndexOutOfBoundsException("This position is not valid");
        }
        String sqlstart = "SELECT position FROM Songs_In_Playlist WHERE playlist_name = ? AND song_name = ?";
        String sqlpreset = "UPDATE Songs_In_Playlist SET position = ? WHERE playlist_name = ? AND song_name = ?";
        String sqlorig = "UPDATE Songs_In_Playlist SET position = (position + -1)*-1 WHERE playlist_name = ? AND position > ? AND position <= ?";
        String sqlnew = "UPDATE Songs_In_Playlist SET position = (position + 1)*-1 WHERE playlist_name = ? AND position < ? AND position >= ?";
        String sqlset = "UPDATE Songs_In_Playlist SET position = ? WHERE playlist_name = ? AND song_name = ?";
        String sqlflip = "UPDATE Songs_In_Playlist SET position = (position)*-1 WHERE position < 0";
        try {
            PreparedStatement stmt = conn.prepareStatement(sqlstart);
            stmt.setString(1, playlistName);
            stmt.setString(2, songName);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int originalpos = (int) rs.getObject("position");
            stmt = conn.prepareStatement(sqlpreset);
            stmt.setInt(1, Integer.MIN_VALUE);
            stmt.setString(2, playlistName);
            stmt.setString(3, songName);
            stmt.executeUpdate();
            if (originalpos < newPosition) { //original position is before the new position
                stmt = conn.prepareStatement(sqlorig);
                stmt.setString(1, playlistName);
                stmt.setInt(2, originalpos);
                stmt.setInt(3, newPosition);
                stmt.executeUpdate();
            } else {
                stmt = conn.prepareStatement(sqlnew);
                stmt.setString(1, playlistName);
                stmt.setInt(2, originalpos);
                stmt.setInt(3, newPosition);
                stmt.executeUpdate();
            }
            stmt = conn.prepareStatement(sqlset);
            stmt.setInt(1, newPosition);
            stmt.setString(2, playlistName);
            stmt.setString(3, songName);
            stmt.executeUpdate();
            stmt = conn.prepareStatement(sqlflip);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            PopupWindow popupWindow = new PopupWindow();
            popupWindow.SQLError("There has been an error moving the song. Please close and reopen the application");
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Removes a song from a playlist, removes from the Songs_In_Playlist table
     *
     * @param songName     name of the Song to be removed
     * @param playlistName name of the playlist the song is to be removed from
     */
    public static void removeSongFromPlaylist(String playlistName, String songName) {
        String sqlstart = "SELECT position FROM Songs_In_Playlist WHERE playlist_name = ? AND song_name = ?";
        String sqldelete = "DELETE FROM Songs_In_Playlist WHERE playlist_name = ? AND song_name = ?";
        String sqlupdate = "UPDATE Songs_In_Playlist SET position = (position - 1)*-1 WHERE playlist_name = ? and position > ?";
        String sqlflip = "UPDATE Songs_In_Playlist SET position = (position)*-1 WHERE position < 0";

        try {
            int i = operationCheck("Both");
            PreparedStatement stmt = conn.prepareStatement(sqlstart);
            stmt.setString(1, playlistName);
            stmt.setString(2, songName);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int originalpos = (int) rs.getObject("position");
            stmt = conn.prepareStatement(sqldelete);
            stmt.setString(1, playlistName);
            stmt.setString(2, songName);
            stmt.executeUpdate();
            stmt = conn.prepareStatement(sqlupdate);
            stmt.setString(1, playlistName);
            stmt.setInt(2, originalpos);
            stmt.executeUpdate();
            stmt = conn.prepareStatement(sqldelete);
            stmt.setString(1, playlistName);
            stmt.setString(2, songName);
            stmt.executeUpdate();
            stmt = conn.prepareStatement(sqlflip);
            stmt.executeUpdate();
            int j = operationCheck("Both");
            if (i > j) {
                updatePlaylist(playlistName, songName, -1);
            }
        } catch (SQLException ex) {
            PopupWindow popupWindow = new PopupWindow();
            popupWindow.SQLError("There has been an error removing the song from the playlist. Please close and reopen the application");
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Removes a song from the master song list
     * This will also affect the Songs_In_Playlist table
     *
     * @param name name of the Song to be removed
     */
    public static void removeSong(String name) {
        String sql = "DELETE FROM Song WHERE name = ?";
        try {
            //Statement stmt = conn.createStatement();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            PopupWindow popupWindow = new PopupWindow();
            popupWindow.SQLError("There has been an error removing the song. Please close and reopen the application");
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Removes a playlist from the Playlist table
     * This will also affect the Songs_In_Playlist table
     *
     * @param name name of the Playlist to be removed
     */
    public static void removePlaylist(String name) {
        String sql = "DELETE FROM Playlist WHERE name = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            PopupWindow popupWindow = new PopupWindow();
            popupWindow.SQLError("There has been an error removing the playlist. Please close and reopen the application");
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Returns all songs from the master song list
     *
     * @return ResultSet This ResultSet contains every song in the master song list
     */
    public static ResultSet selectAllSongs() {
        String sql = "SELECT * FROM Song";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
        } catch (SQLException ex) {
            PopupWindow popupWindow = new PopupWindow();
            popupWindow.SQLError("There has been an error. Please close and reopen the application");
            System.out.println(ex.getMessage());
            return null;
        }
    }

    /**
     * Returns all songs from a given playlist
     *
     * @param name the playlist for songs to be selected from
     * @return ResultSet This ResultSet contains every song from the given playlist
     */
    public static ResultSet selectSongsFromPlaylist(String name) {
        String sql = "SELECT * FROM Songs_In_Playlist JOIN Song S on S.name = Songs_In_Playlist.song_name WHERE playlist_name = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            return rs;
        } catch (SQLException ex) {
            PopupWindow popupWindow = new PopupWindow();
            popupWindow.SQLError("There has been an error. Please close and reopen the application");
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public static ResultSet selectAllSongsInPlaylists() {
        String sql = "SELECT * FROM Songs_In_Playlist SP JOIN Song S on S.name = SP.song_name";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            return rs;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    /**
     * Returns all playlists
     *
     * @return ResultSet This ResultSet contains every playlist
     */
    public static ResultSet selectAllPlaylists() {
        String sql = "SELECT * FROM Playlist";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
        } catch (SQLException ex) {
            PopupWindow popupWindow = new PopupWindow();
            popupWindow.SQLError("There has been an error. Please close and reopen the application");
            System.out.println(ex.getMessage());
            return null;
        }
    }

    /**
     * Returns all attributes for a given song
     *
     * @param name the song for which the info is gotten
     * @return ResultSet This ResultSet contains a single row with the stored attributes for the given song
     */
    public static ResultSet getSongInfo(String name) {
        String sql = "SELECT * FROM Song WHERE name = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            return rs;
        } catch (SQLException ex) {
            PopupWindow popupWindow = new PopupWindow();
            popupWindow.SQLError("There has been an error getting your Song information. Most likely, the song you are trying to edit does not exist. Please try again.");
            System.out.println(ex.getMessage());
            return null;
        }
    }

    /**
     * Returns all attributes for a given playlist
     *
     * @param name the playlist for which the info is gotten
     * @return ResultSet This ResultSet contains a single row with the stored attributes for the given playlist
     */
    public static ResultSet getPlaylistInfo(String name) {
        String sql = "SELECT * FROM Playlist WHERE name = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            return rs;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public static void editSong(String name, String path, String author, String album) {
        String sql = "UPDATE Song SET filepath = ?, author = ?, album = ? WHERE name = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, path);
            stmt.setString(2, author);
            stmt.setString(3, album);
            stmt.setString(4, name);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            PopupWindow popupWindow = new PopupWindow();
            popupWindow.SQLError("There has been an error changing your Song information. Most likely, the song you are trying to edit does not exist. Please try again.");
            //System.out.println("editSong failed");
            System.out.println(ex.getMessage());
        }
    }

    public static void editSongName(String oldName, String newName) {
        String sql = "UPDATE Song SET name = ? WHERE name = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newName);
            stmt.setString(2, oldName);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            PopupWindow popupWindow = new PopupWindow();
            popupWindow.SQLError("There has been an error changing the Song Name. Please try again.");
            //System.out.println("editSongName failed");
            System.out.println(ex.getMessage());
        }
    }

    public static void editPlaylist(String name, String description) {
        String sql = "UPDATE Playlist SET name = ?, description = ? WHERE name = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setString(3, name);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            PopupWindow popupWindow = new PopupWindow();
            popupWindow.SQLError("The playlist you tried to edit does not appear to exist. Please try again.");
            System.out.println("editPlaylist failed");
            System.out.println(ex.getMessage());
        }
    }

    public static void editPlaylistName(String oldName, String newName) {
        String sql = "UPDATE Playlist SET name = ? WHERE name = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newName);
            stmt.setString(2, oldName);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            PopupWindow popupWindow = new PopupWindow();
            popupWindow.SQLError("There has been an error changing the Playlist Name. Please try again.");
            //System.out.println("editSongName failed");
            System.out.println(ex.getMessage());
        }
    }

    private static void updatePlaylist(String playlistName, String songName, int direction) {
        String sql = "UPDATE Playlist SET duration = (duration + (SELECT duration FROM Song WHERE Song.name = ?))*?, num_songs = num_songs + ? WHERE name = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, songName);
            stmt.setInt(2, direction);
            stmt.setInt(3, direction);
            stmt.setString(4, playlistName);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            PopupWindow popupWindow = new PopupWindow();
            popupWindow.SQLError("There has been an error. Please close the application");
            System.out.println("updatePlaylist failed");
            System.out.println(ex.getMessage());
        }
    }

    private static int operationCheck(String type) {
        ResultSet rs;
        if (type.equals("Song")) {
            rs = selectAllSongs();
        } else if (type.equals("Playlist")) {
            rs = selectAllPlaylists();
        } else if (type.equals("Both")) {
            rs = selectAllSongsInPlaylists();
        } else {
            return -1;
        }

        int i = 0;
        try {
            while (rs.next()) {
                i++;
            }
        } catch (Exception ex) {
            PopupWindow popupWindow = new PopupWindow();
            popupWindow.SQLError("There has been an error. Please close the application");
            System.out.println("operationCheck failed");
            System.out.println(ex.getMessage());
        }
        return i;
    }

    public static void changeSongActiveStatus(String name, int active) {
        String sql = "UPDATE Song SET active = ? WHERE name = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, active);
            stmt.setString(2, name);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            PopupWindow popupWindow = new PopupWindow();
            popupWindow.SQLError("There has been an error. Please close the application");
            System.out.println("changeSongActiveStatus failed");
            System.out.println(ex.getMessage());
        }
    }

    private static int checkPosition(String playlistName, int position){
        String sql = "SELECT count(position) as num FROM Songs_In_Playlist WHERE playlist_name = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, playlistName);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt("num");
            if (position<0 || position>count){
                return 0;
            }
            return 1;
        } catch (SQLException ex) {
            PopupWindow popupWindow = new PopupWindow();
            popupWindow.SQLError("There has been an error. Please close the application");
            System.out.println("checkPosition failed");
            System.out.println(ex.getMessage());
        }
        return 0;
    }

    public static void verifyAllSongs(){
        ResultSet rs = selectAllSongs();
        try {
            while(rs.next()){
                String path = (String) rs.getObject("filepath");
                String name = (String) rs.getObject("name");
                File file = new File(path);

                if(file.canExecute()){
                    changeSongActiveStatus(name, 1);
                }else{
                    changeSongActiveStatus(name, 0);
                }
            }
        } catch (SQLException ex) {
            PopupWindow popupWindow = new PopupWindow();
            popupWindow.SQLError("There has been an error. Please close the application");
            System.out.println("verifyAllSongs failed");
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Returns all attributes for all tables in the database
     *
     * @return ResultSet This ResultSet contains all playlists in the database
     */
    public static ResultSet getTables() {
        String sql = "SELECT * FROM sqlite_master WHERE type='table'";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
        } catch (SQLException ex) {
            PopupWindow popupWindow = new PopupWindow();
            popupWindow.SQLError("There has been an error. Please close the application");
            System.out.println("getTables failed");
            System.out.println(ex.getMessage());
            return null;
        }
    }

    //These two methods should never be run, unless we need to reset our tables
    /*private static void createTable(){
        String sql = "";
        try {
            Statement stmt  = conn.createStatement();
            stmt.execute(sql);
            System.out.println("Table: Song successfully created");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void dropTable(String name){
        String sql = "DROP TABLE " + name;
        try {
            Statement stmt  = conn.createStatement();
            stmt.execute(sql);
            System.out.println("Table: " + name + " successfully dropped");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }*/
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
                                        "date: " + rs.getObject("date_added") + "\t" +
                                        "active: " + rs.getObject("active") + "\t" +
                                        "duration: " + rs.getObject("duration") + "\t");
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
                                        "date: " + rs.getObject("date_created") + "\t" +
                                        "duration: " + rs.getObject("duration") + "\t" +
                                        "description: " + rs.getObject("description") + "\t" +
                                        "num_songs: " + rs.getObject("num_songs") + "\t");
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
                                        "Position: " + rs.getObject("position") + "\t" +
                                        "path: " + rs.getObject("filepath") + "\t" +
                                        "author: " + rs.getObject("author") + "\t" +
                                        "album: " + rs.getObject("album") + "\t" +
                                        "active: " + rs.getObject("active") + "\t" +
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
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        connect();
        //addNewSong("testsong", "testpath", "testauthor", "testalbum", 15);
        //removePlaylist("testplaylist");
        //createNewPlaylist("testplaylist", "test description");
        //addSongToPlaylist("testplaylist", "testapple");
        //removeSongFromPlaylist("testplaylist", "testsong");
        //addSongToPlaylist("testplaylist", "testsong");
        //moveSongInPlaylist("testplaylist", "testfruit", 5);
        /*String sql1 = "UPDATE Songs_In_Playlist SET position = ? WHERE playlist_name = ? AND song_name = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql1);
            stmt.setInt(1,3);
            stmt.setString(2,"testplaylist");
            stmt.setString(3,"testcoconut");
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }*//*
        addNewSong("4th Mvmt", "C:\\Users\\21shu\\IdeaProjects\\MP3Player\\src\\main\\resources\\song\\4th Mvmt.mp3", "testauthor", "testalbum", 20);
        addNewSong("Alvin", "C:\\Users\\21shu\\IdeaProjects\\MP3Player\\src\\main\\resources\\song\\Iko_Iko_From__Alvin_And_The_Chipmunks__The_Road_Chip__Soundtrack.mp3", "testauthor", "testalbum", 20);
        addNewSong("MeAndYourMama", "C:\\Users\\21shu\\IdeaProjects\\MP3Player\\src\\main\\resources\\song\\MeAndYourMama.mp3", "testauthor", "testalbum", 20);
        changeSongActiveStatus("Alvin", 0);
        changeSongActiveStatus("MeAndYourMama", 0);
        verifyAllSongs();

        File file = new File("C:\\Users\\21shu\\IdeaProjects\\MP3Player\\src\\main\\resources\\song", "4th Mvmt.mp3");
        System.out.println("CanExecute: " + file.canExecute());
        removeSong("4th Mvmt");
        removeSong("Alvin");
        removeSong("MeAndYourMama");
*/
        //addNewSong("test_song_name", "", "", "", 0);
        //editSong("test_song_name", null, "", "");
        //removeSong("Iko_Iko_From__Alvin_And_The_Chipmunks__The_Road_Chip__Soundtrack.mp3");
        testing("Song");
        System.out.println();
        testing("Both");
        System.out.println();
        testing("Playlist");
        close();
    }
}
