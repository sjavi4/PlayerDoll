package me.autobot.playerdoll.loader;

import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.api.PlayerDollAPI;

import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class AddonClassLoader extends URLClassLoader {
    private final List<JarURLConnection> cachedJarFiles = new ArrayList<>();
    public AddonClassLoader() {
        super(new URL[]{}, findParentClassLoader());
    }
    public void addURLFile(URL file) {
        try {
            URLConnection uc = file.openConnection();
            if (uc instanceof JarURLConnection ju) {
                uc.setUseCaches(true);
                ju.getManifest();
                cachedJarFiles.add(ju);
            }
        } catch (Exception e) {
            PlayerDollAPI.getLogger().warning("Failed to cache plugin JAR file: " + file.toExternalForm());
        }
        addURL(file);
    }

    public void unloadJarFiles() {
        for (JarURLConnection url : cachedJarFiles) {
            try {
                PlayerDollAPI.getLogger().warning("Unloading plugin JAR file " + url.getJarFile().getName());
                url.getJarFile().close();
                url=null;
            } catch (Exception e) {
                PlayerDollAPI.getLogger().warning("Failed to unload JAR file\n"+e);
            }
        }
    }
    private static ClassLoader findParentClassLoader() {
        ClassLoader parent = PlayerDoll.class.getClassLoader();
        if (parent == null) {
            parent = AddonClassLoader.class.getClassLoader();
        }
        if (parent == null) {
            parent = ClassLoader.getSystemClassLoader();
        }
        return parent;
    }
}
