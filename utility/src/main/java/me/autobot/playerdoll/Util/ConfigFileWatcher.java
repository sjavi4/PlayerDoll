package me.autobot.playerdoll.Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConfigFileWatcher {
    private final WatchService watcher;
    private final Map<WatchKey, List<Path>> keys;
    private CompletableFuture<Void> future;
    public ConfigFileWatcher(Path... dirs) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();

        for (Path dir : dirs) {
            List<Path> list;
            WatchKey key = dir.getParent().register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            if (!keys.containsKey(key)) {
                list = new ArrayList<>();
                list.add(dir);
                keys.put(key, list);
            } else {
                list = keys.get(key);
                list.add(dir);
                keys.put(key,list);
            }
        }

        this.future = CompletableFuture.runAsync(this::processEvents);
    }

    private void reload(String name) {
        this.future.thenRun(() -> ConfigManager.reloadConfig(name));
    }
    private void restart() {
        if (!this.future.isCancelled()) {
            this.future.thenRunAsync(() -> this.future = CompletableFuture.runAsync(ConfigFileWatcher.this::processEvents));
        }
    }

    private void processEvents() {
        while (!Thread.currentThread().isInterrupted() && !future.isDone()) {
            WatchKey key;
            try {
                key = watcher.take();
                Thread.sleep(500);
            } catch (InterruptedException x) {
                return;
            }
            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                    key.reset();
                    List<Path> dirs = keys.get(key);
                    dirs.forEach(path -> {
                        Path changed = Paths.get(path.getParent().toUri()).resolve(((Path) event.context()));
                        try {
                            if (Files.isSameFile(changed, path)) {
                                String filename = changed.getFileName().toString();
                                System.out.printf("Config %s has been modified. Reloading\n",filename);
                                this.future.complete(null);
                                reload(filename.substring(0,filename.length()-4));
                                restart();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            //throw new RuntimeException(e);
                        }
                    });
                }
            }
        }
        try {
            watcher.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void stop() {
        future.cancel(true);
    }
}
