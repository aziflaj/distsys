package com.aziflaj.uptdistsys;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.HttpURLConnection;
import java.nio.channels.FileLock;

public class FileGrabberPro {
  public static void main(String[] args) throws Exception {
    if (args.length != 4) {
      System.out.println("Usage: java FileGrabberPro <source> <destination> <startByte> <endByte>");
      System.exit(1);
    }

    String sourceStr = args[0];
    URL source = new URI(sourceStr).toURL();

    String destination = args[1];
    int startByte = Integer.parseInt(args[2]);
    int endByte = Integer.parseInt(args[3]);

    // download the chunk
    HttpURLConnection connection = (HttpURLConnection) source.openConnection();
    connection.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);

    try (InputStream in = connection.getInputStream()) {
      byte[] buffer = new byte[1024];
      int bytes;

      try (RandomAccessFile outFile = new RandomAccessFile(destination, "rw")) {
        outFile.seek(startByte);
        while ((bytes = in.read(buffer)) != -1) {
          FileLock lock = outFile.getChannel().lock();
          outFile.write(buffer, 0, bytes);
          lock.release();
        }
      }
    }
  }
}
