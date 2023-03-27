
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.util.Objects;


public class Database {
    static Connection conn = null;

    /**
     * Connect to the database
     */
    public static void connect() {

        try {
            // db parameters
            String url = "jdbc:sqlite:mp3db.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
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
    public static void createNewPlaylist(String name) {
        String sql = "INSERT INTO Playlist VALUES(?,0, current_date)";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,name);
            stmt.executeUpdate();
        } catch (SQLException ex) {
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
    public static void addNewSong(String name, String path, String author, String album) {

        String sql = "INSERT INTO Song VALUES(?,?,?,?, current_date)";
        try {
            //Statement stmt = conn.createStatement();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,name);
            stmt.setString(2,path);
            stmt.setString(3,author);
            stmt.setString(4,album);
            stmt.executeUpdate();
            //ResultSet rs = stmt.executeQuery(sql);
        } catch (SQLException ex) {
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
        String sql = "INSERT INTO Songs_In_Playlist VALUES(?,?,?)";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,songName);
            stmt.setString(2,playlistName);
            stmt.setInt(3,position);
            stmt.executeUpdate();
        } catch (SQLException ex) {
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
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,playlistName);
            stmt.setString(2,songName);
            stmt.setString(3,playlistName);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Moves a songs position in a playlist in the Songs_In_Playlist table
     * Does this by incrementing every song after the given position, then reassigning the position attribute
     *
     * @param songName     name of the Song to be moved
     * @param playlistName name of the playlist the song is in
     * @param position     position in the playlist for the song to be moved to
     */
    public static void moveSongInPlaylist(String playlistName, String songName, int position) {
        String sql = "";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
        } catch (SQLException ex) {
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
        String sql = "";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
        } catch (SQLException ex) {
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
            stmt.setString(1,name);
            stmt.executeUpdate();
        } catch (SQLException ex) {
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
            stmt.setString(1,name);
            stmt.executeUpdate();
        } catch (SQLException ex) {
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
        String sql = "SELECT * FROM Songs_In_Playlist WHERE playlist_name = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,name);
            ResultSet rs = stmt.executeQuery();
            return rs;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public static ResultSet selectAllSongsInPlaylist() {
        String sql = "SELECT * FROM Songs_In_Playlist";
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
            stmt.setString(1,name);
            ResultSet rs = stmt.executeQuery();
            return rs;
        } catch (SQLException ex) {
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
            stmt.setString(1,name);
            ResultSet rs = stmt.executeQuery();
            return rs;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
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
        if (type.equals("Song") ){
            rs = selectAllSongs();
        }else if (type.equals("Playlist")){
            rs = selectAllPlaylists();
        } else{
            rs = selectAllSongsInPlaylist();
        }
        if (rs == null) {
            System.out.println("no rows found");
        } else {
            if (type.equals("Song")){
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
            else if(type.equals("Playlist")){
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
            else {
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

    public static void addRemoveTests(){
        System.out.println("Add/Remove Song Test");
        testing("Song");
        addNewSong("testname", "testpath", "testauthor", "testalbum");
        testing("Song");
        removeSong("testname");
        testing("Song");
        System.out.println("Add/Remove Playlist Test");
        testing("Playlist");
        createNewPlaylist("testplaylist");
        testing("Playlist");
        removePlaylist("testplaylist");
        testing("Playlist");
    }

    public static void getSongInfoTest(){
        testing("Song");
        addNewSong("testname", "testpath", "testauthor", "testalbum");
        addNewSong("testname2", "testpath", "testauthor", "testalbum");
        ResultSet rs = getSongInfo("testname");
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
        removeSong("testname2");
        removeSong("testname");
        testing("Song");
    }

    public static void getPlaylistInfoTest(){
        testing("Playlist");
        createNewPlaylist("testplaylist");
        createNewPlaylist("testplaylist2");
        ResultSet rs = getPlaylistInfo("testplaylist");
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
        removePlaylist("testplaylist");
        removePlaylist("testplaylist2");
        testing("Playlist");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        connect();
        testing("Song");
        testing("Playlist");
        removePlaylist("testname");
        addSongToPlaylist("testplaylist","testname");
        testing("Both");
        close();
    }
}
