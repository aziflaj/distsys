package com.aziflaj.uptdistsys;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
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

    downloadFile(sourceStr, destination, startByte, endByte);

    // download the chunk
    // downloadFileV2(source, destination, startByte, endByte);
  }


  // NOTE: Method signature updated to accept filename, startByte and endByte
  private static void downloadFile(
      String fileUrl,
      String fileName,
      int startByte,
      int endByte
  ) {
    try {
      // URL url = new URL(fileUrl); // NOTE: Deprecated.
      URL url = new URI(fileUrl).toURL();

      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
      // NOTE: Set the range of bytes to download
      httpConn.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);
      int responseCode = httpConn.getResponseCode();

      // Check if response is successful (200)
      if (responseCode == HttpURLConnection.HTTP_OK) {
        // NOTE: Already have the file name
        // String fileName = "";
        // String disposition = httpConn.getHeaderField("Content-Disposition");

        // Extract file name from content-disposition header field
        // if (disposition != null) {
        //   int index = disposition.indexOf("filename=");
        //   if (index > 0) {
        //     fileName = disposition.substring(index + 10, disposition.length() - 1);
        //   }
        // } else {
        //   fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1, fileUrl.length());
        // }

        // Open input and output streams
        InputStream inputStream = httpConn.getInputStream();
        // FileOutputStream outputStream = new FileOutputStream(fileName);
        // NOTE: Use RandomAccessFile instead of FileOutputStream
        // to write to a specific position in the file
        RandomAccessFile outputStream = new RandomAccessFile(fileName, "rw");

        // Read from input stream and write to output stream
        int bytesRead;
        byte[] buffer = new byte[4096]; // NOTE: Why 4kB buffer?
        outputStream.seek(startByte); // NOTE: Seek to the startByte
        while ((bytesRead = inputStream.read(buffer)) != -1) {
          FileLock lock = outputStream.getChannel().lock(); // NOTE: Lock
          outputStream.write(buffer, 0, bytesRead);
          lock.release(); // NOTE: Unlock
        }

        outputStream.close();
        inputStream.close();

        System.out.println("File downloaded successfully.");
      } else {
        System.out.println("Failed to download file. Response Code: " + responseCode);
      }
      httpConn.disconnect();
    } catch (IOException | URISyntaxException e) {
      e.printStackTrace();
    }
  }

  private static void downloadFileV2(URL source, String destination, int startByte, int endByte) throws IOException {
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
    } finally {
      connection.disconnect();
    }
  }
}
