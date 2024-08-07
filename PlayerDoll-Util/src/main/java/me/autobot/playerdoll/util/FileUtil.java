package me.autobot.playerdoll.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class FileUtil {
    public static FileUtil INSTANCE;
    private final Path pluginPath;
    private final Path playerDataDir = Bukkit.getServer().getWorldContainer().toPath().resolve("world").resolve("playerdata");
    private Path dollDir;
    private Path languageDir;
    private Path backupDir;
    public FileUtil(Plugin plugin) {
        INSTANCE = this;
        pluginPath = plugin.getDataFolder().toPath();
        setupDirectories();
    }

    private void setupDirectories() {
        dollDir = pluginPath.resolve("doll");
        languageDir = pluginPath.resolve("language");
        backupDir = pluginPath.resolve("backup");

        checkExist(dollDir);
        checkExist(languageDir);
        checkExist(backupDir);
    }

    private void checkExist(Path path) {
        File file = path.toFile();
        if (!file.exists()) {
            file.mkdirs();
        }
    }
    public File getOrCreateFile(Path path, File fileName) {
        return getOrCreateFile(path, fileName.getName());
    }
    public File getOrCreateFile(Path path, String fileName) {
        File file = path.resolve(fileName).toFile();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return file;
    }

    public File getFile(Path path, String fileName) {
        return path.resolve(fileName).toFile();
    }

    public Path getPluginPath() {
        return pluginPath;
    }

    public Path getDollDir() {
        return dollDir;
    }

    public Path getLanguageDir() {
        return languageDir;
    }

    public Path getBackupDir() {
        return backupDir;
    }

    public Path getPlayerDataDir() {
        return playerDataDir;
    }
}
