package com.xyz.websocket;

/**
 * $Id$
 *
 * @author precuay
 * @date 01/04/15
 */

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class MainWatch {

    Path path;

    //FileSystem fs;
    WatchService service;

    public MainWatch(Path pathParam) throws IOException {

        path = pathParam;

        // Sanity check - Check if path is a folder
        try {
            Boolean isFolder = (Boolean) Files.getAttribute(path,
                    "basic:isDirectory", NOFOLLOW_LINKS);
            if (!isFolder) {
                throw new IllegalArgumentException("Path: " + path + " is not a folder");
            }
        } catch (IOException ioe) {
            // Folder does not exists
            ioe.printStackTrace();
        }

        System.out.println("Watching path: " + path);

        // We obtain the file system of the Path
        FileSystem fs = path.getFileSystem();

        // We create the new WatchService using the new try() block
        service = fs.newWatchService();

        // We register the path to the service
        // We watch for creation events
        path.register(service, ENTRY_CREATE);

    }

    public void watchLoop() {

        try {

            // Start the infinite polling loop
            WatchKey key = null;
            while (true) {
                key = service.take();

                // Dequeueing events
                Kind<?> kind = null;
                for (WatchEvent<?> watchEvent : key.pollEvents()) {
                    // Get the type of the event
                    kind = watchEvent.kind();
                    if (OVERFLOW == kind) {
                        continue; //loop
                    } else if (ENTRY_CREATE == kind) {
                        // A new Path was created
                        Path newPath = ((WatchEvent<Path>) watchEvent).context();
                        // Output
                        System.out.println("New path created: " + newPath);
                    }
                }

                if (!key.reset()) {
                    break; //lotuhombr
                }
            }

        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

    }

    public String watchOnce() {

        String result = null;

        try {
            System.out.println("A");
            // Start the infinite polling loop
            WatchKey key = null;
            System.out.println("B");
            key = service.take();
            System.out.println("C");
            // Dequeueing events
            Kind<?> kind = null;
            System.out.println("D");
            for (WatchEvent<?> watchEvent : key.pollEvents()) {
                System.out.println("E");
                // Get the type of the event
                kind = watchEvent.kind();
                System.out.println("F");
                if (OVERFLOW == kind) {
                    System.out.println("G");
                    continue; //loop
                } else if (ENTRY_CREATE == kind) {
                    System.out.println("H");
                    // A new Path was created
                    Path newPath = ((WatchEvent<Path>) watchEvent).context();
                    // Output
                    System.out.println("New path created: " + newPath);
                    result = newPath.toString();
                }
            }
            key.reset();

        } catch (ClosedWatchServiceException ie) {
            System.out.println("ERROR ClosedWatchServiceException");
            ie.printStackTrace();
        } catch (InterruptedException ie) {
            System.out.println("ERROR InterruptedException");
            ie.printStackTrace();
        }catch (Throwable ie) {
            System.out.println("ERROR Throwable");
            ie.printStackTrace();
        }
        System.out.println("I");
        return result;
    }

    public static void main(String[] args) throws IOException,
            InterruptedException {

        Path folder = Paths.get(System.getProperty("user.home"));

        MainWatch mainWatch = new MainWatch(folder);
        mainWatch.watchOnce();
    }
}
