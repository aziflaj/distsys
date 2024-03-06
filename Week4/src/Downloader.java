package com.aziflaj.uptdistsys;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Downloader {
  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      System.out.println("Usage: java Downloader [tp] <file_url>");
      System.exit(1);
    }

    String mode = args[0];
    String fileUrl = args[1];
    String destPath = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);

    URL url = new URI(fileUrl).toURL();

    if (Objects.equals(mode, "t")) { // threaded version
      threadedDownload(url, destPath);
    } else if (Objects.equals(mode, "p")) { // process version
      processDownload(url, destPath);
    } else {
      System.out.println("Usage: java Downloader [tp] <file_url>");
      System.exit(1);
    }

    System.out.println("File downloaded successfully.");
  }

  private static void threadedDownload(URL url, String destPath) throws IOException, InterruptedException {
    FileGrabber grabber = new FileGrabber(url, destPath, 4);
    // FileGrabber grabber = new FileGrabber(url, destPath, 255);
    grabber.download();
  }

  private static void processDownload(URL url, String destPath) throws Exception {
    int cores = Runtime.getRuntime().availableProcessors();
    // FileGrabberProMax grabber = new FileGrabberProMax(url, destPath, 4);
    FileGrabberProMax grabber = new FileGrabberProMax(url, destPath, cores - 1);
    grabber.download();
  }
}
