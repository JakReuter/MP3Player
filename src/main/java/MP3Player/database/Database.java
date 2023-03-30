package MP3Player.database;

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
    public static void createNewPlaylist(String name, String description) {
        String sql = "INSERT INTO Playlist VALUES(?,0, current_date, 0,?,0)";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,name);
            stmt.setString(2,description);
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
    public static void addNewSong(String name, String path, String author, String album, int duration) {

        String sql = "INSERT INTO Song VALUES(?,?,?,?, current_date, ?)";
        try {
            //Statement stmt = conn.createStatement();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,name);
            stmt.setString(2,path);
            stmt.setString(3,author);
            stmt.setString(4,album);
            stmt.setInt(5,duration);
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
        String sql1 = "UPDATE Songs_In_Playlist SET position = (position + 1)*-1 WHERE playlist_name = ? and position >= ?";
        String sql = "INSERT INTO Songs_In_Playlist VALUES(?,?,?)";
        String sqlflip = "UPDATE Songs_In_Playlist SET position = (position)*-1 WHERE position < 0";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql1);
            stmt.setString(1,playlistName);
            stmt.setInt(2,position);
            stmt.executeUpdate();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1,playlistName);
            stmt.setString(2,songName);
            stmt.setInt(3,position);
            stmt.executeUpdate();
            stmt = conn.prepareStatement(sqlflip);
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
        String sqlstart = "SELECT position FROM Songs_In_Playlist WHERE playlist_name = ? AND song_name = ?";
        String sqlpreset = "UPDATE Songs_In_Playlist SET position = ? WHERE playlist_name = ? AND song_name = ?";
        String sqlorig = "UPDATE Songs_In_Playlist SET position = (position + -1)*-1 WHERE playlist_name = ? AND position > ? AND position <= ?";
        String sqlnew = "UPDATE Songs_In_Playlist SET position = (position + 1)*-1 WHERE playlist_name = ? AND position < ? AND position >= ?";
        String sqlset = "UPDATE Songs_In_Playlist SET position = ? WHERE playlist_name = ? AND song_name = ?";
        String sqlflip = "UPDATE Songs_In_Playlist SET position = (position)*-1 WHERE position < 0";
        try {
            PreparedStatement stmt = conn.prepareStatement(sqlstart);
            stmt.setString(1,playlistName);
            stmt.setString(2,songName);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int originalpos = (int) rs.getObject("position");
            stmt = conn.prepareStatement(sqlpreset);
            stmt.setInt(1,Integer.MIN_VALUE);
            stmt.setString(2,playlistName);
            stmt.setString(3,songName);
            stmt.executeUpdate();
            if (originalpos<position){ //original position is before the new position
                stmt = conn.prepareStatement(sqlorig);
                stmt.setString(1,playlistName);
                stmt.setInt(2,originalpos);
                stmt.setInt(3,position);
                System.out.println("hit2");
                stmt.executeUpdate();
            }else {
                stmt = conn.prepareStatement(sqlnew);
                stmt.setString(1,playlistName);
                stmt.setInt(2,originalpos);
                stmt.setInt(3,position);
                System.out.println("hit3");
                stmt.executeUpdate();
            }
            stmt = conn.prepareStatement(sqlset);
            stmt.setInt(1,position);
            stmt.setString(2,playlistName);
            stmt.setString(3,songName);
            stmt.executeUpdate();
            stmt = conn.prepareStatement(sqlflip);
            stmt.executeUpdate();
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
        String sqlstart = "SELECT position FROM Songs_In_Playlist WHERE playlist_name = ? AND song_name = ?";
        String sqldelete = "DELETE FROM Songs_In_Playlist WHERE playlist_name = ? AND song_name = ?";
        String sqlupdate = "UPDATE Songs_In_Playlist SET position = (position - 1)*-1 WHERE playlist_name = ? and position > ?";
        String sqlflip = "UPDATE Songs_In_Playlist SET position = (position)*-1 WHERE position < 0";

        try {
            PreparedStatement stmt = conn.prepareStatement(sqlstart);
            stmt.setString(1,playlistName);
            stmt.setString(2,songName);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int originalpos = (int) rs.getObject("position");
            stmt = conn.prepareStatement(sqldelete);
            stmt.setString(1,playlistName);
            stmt.setString(2,songName);
            stmt.executeUpdate();
            stmt = conn.prepareStatement(sqlupdate);
            stmt.setString(1,playlistName);
            stmt.setInt(2,originalpos);
            stmt.executeUpdate();
            stmt = conn.prepareStatement(sqldelete);
            stmt.setString(1,playlistName);
            stmt.setString(2,songName);
            stmt.executeUpdate();
            stmt = conn.prepareStatement(sqlflip);
            stmt.executeUpdate();
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
        String sql = "SELECT * FROM Songs_In_Playlist JOIN Song S on S.name = Songs_In_Playlist.song_name WHERE playlist_name = ?";
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

    public static ResultSet selectAllSongsInPlaylists() {
        String sql = "SELECT * FROM Songs_In_Playlist SP JOIN Song S on S.name = SP.song_name UNION SELECT playlist_name, song_name, position, 0,0,0,0,0,0 FROM Songs_In_Playlist";
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

    public static void editSong(String name, String path, String author, String album){
        String sql = "UPDATE Song SET name = ?, filepath = ?, author = ?, album = ? WHERE name = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,name);
            stmt.setString(2,path);
            stmt.setString(3,author);
            stmt.setString(4,album);
            stmt.setString(5,name);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void editPlaylist(String name, String description){
        String sql = "UPDATE Playlist SET name = ?, description = ? WHERE name = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,name);
            stmt.setString(2,description);
            stmt.setString(3,name);

        } catch (SQLException ex) {
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
                                        "Position: " + rs.getObject("position") + "\t" +
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
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        connect();
        testing("Song");
        System.out.println();
        //addSongToPlaylist("testplaylist", "testsong");
        //removeSongFromPlaylist("testplaylist", "testsong");
        //addSongToPlaylist("testplaylist", "testfruit", 0);
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
        }*/
        testing("Both");
        System.out.println();
        testing("Playlist");
        close();
    }
}
