package autochoosecourse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.apache.commons.io.FilenameUtils;

public class MusicInfo extends Thread {

    private Player player;
    public static String filepath;
    public static FileInputStream fileInputStream;
    private volatile boolean running = false;
    
    public MusicInfo() {

    }

    public void setRunning() {
        running = true;
    }

    public void terminate() {
        this.stop();
    }

    public static boolean mp3Existed() {
        try {
            File dir = new File(new File(Login.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath());
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                for (File child : directoryListing) {
                    
                    if (FilenameUtils.getExtension(child.getName()).toLowerCase().equals("mp3")) {
                        fileInputStream = new FileInputStream(dir + "/" + child.getName());
                        filepath = dir + "/" + child.getName();
                        return true;
                    }
                }
            }
        } catch (URISyntaxException ex) {
            Logger.getLogger(MusicInfo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MusicInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public void run() {
        do {
            try (FileInputStream fs = new FileInputStream(filepath)){
                player = new Player(fs);
                player.play();
            } catch (JavaLayerException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MusicInfo.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MusicInfo.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while (running);
    }
}
