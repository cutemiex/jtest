package com.tiantiandou.file;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/****
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月19日
 */
public final class FileObserver {
    private static final Logger LOGGER = LoggerFactory.getLogger("FileObserver");
    
    private FileObserver() {

    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        FileSystem fs = FileSystems.getDefault();
        WatchService watcher = fs.newWatchService();
        Path dir = fs.getPath("d:/");
        dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE);
        WatchKey key = null;
        while ((key = watcher.take()) != null) {
            List<WatchEvent<?>> events = key.pollEvents();
            for (WatchEvent<?> e : events) {
                Kind<?> kind = e.kind();
                if (kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                    WatchEvent<Path> pe = (WatchEvent<Path>) e;
                    Path fileName = pe.context();
                    LOGGER.debug("File modify name is : " + fileName);
                }
                if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                    WatchEvent<Path> pe = (WatchEvent<Path>) e;
                    Path fileName = pe.context();
                    LOGGER.debug("File create name is : " + fileName);
                }
                if (kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                    WatchEvent<Path> pe = (WatchEvent<Path>) e;
                    Path fileName = pe.context();
                    LOGGER.debug("File delete name is : " + fileName);
                }
            }
        }
    }
}
