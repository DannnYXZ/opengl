package com.dannnyxz.util;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileWatcher {

  private final String monitorDirectoryPath;
  private final AtomicBoolean changed;

  public FileWatcher(final String monitorDirectoryPath) {
    this.monitorDirectoryPath = monitorDirectoryPath;
    this.changed = new AtomicBoolean(false);
  }

  public void start() {
    new Thread(() -> {
      final WatchService watchService;
      try {
        watchService = FileSystems.getDefault().newWatchService();
        Path path = Paths.get(monitorDirectoryPath);
        path.register(
            watchService,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_DELETE,
            StandardWatchEventKinds.ENTRY_MODIFY
        );
        WatchKey key;
        while ((key = watchService.take()) != null) {
          for (WatchEvent<?> event : key.pollEvents()) {
            System.out.println("Event kind:" + event.kind() + ". "
                + "File affected: " + event.context() + ".");
          }
          changed.set(true);
          key.reset();
        }
      } catch (IOException | InterruptedException e) {
        e.printStackTrace();
      }
    }).start();
  }

  public boolean changed() {
    return changed.compareAndSet(true, false);
  }
}
