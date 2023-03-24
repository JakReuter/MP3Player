import javafx.scene.image.ImageView;

import java.io.File;

public class Audio {
    private String path;

    private String name;
    private String artist;
    private ImageView albumCover;
    private File file;


    public Audio(String path){
        this.path = path;
        file = new File(path);
    }










    /* GETTER AND SETTER*/
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

}
