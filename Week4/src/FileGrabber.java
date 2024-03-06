package com.aziflaj.uptdistsys;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.CountDownLatch;

public class FileGrabber extends AbstractFileGrabber {
  private final Lock lock = new ReentrantLock();

  public FileGrabber(URL urlSource, String destination) {
    super(urlSource, destination, 4);
  }

  public FileGrabber(URL urlSource, String destination, int parallelismCount) {
    super(urlSource, destination, parallelismCount);
  }

  public void download() throws IOException, InterruptedException {
    int startByte = 0;

    ExecutorService executor = Executors.newFixedThreadPool(parallelismCount);
    CountDownLatch latch = new CountDownLatch(parallelismCount);

    for (int i = 0; i < parallelismCount; i++) {
      int offset = (i == parallelismCount - 1 ? getRemainingBytes() : 0);
      int endByte = startByte + getChunkSize() - 1 + offset;

      executor.execute(new DownloadChunk(startByte, endByte, latch, i));
      startByte = endByte + 1;
    }

    latch.await();
    executor.shutdown();
  }

  private class DownloadChunk implements Runnable {
    private final int startByte, endByte;
    private final CountDownLatch latch;
    private final int threadIdx;

    public DownloadChunk(int startByte, int endByte, CountDownLatch latch, int idx) {
      this.startByte = startByte;
      this.endByte = endByte;
      this.latch = latch;
      this.threadIdx = idx;
    }

    @Override
    public void run() {
      try {
        HttpURLConnection connection = (HttpURLConnection) source.openConnection();
        connection.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);

        try (InputStream in = connection.getInputStream()) {
          byte[] buffer = new byte[1024]; // 1kB at a time
          int bytes;

          try (RandomAccessFile outFile = new RandomAccessFile(destination, "rw")) {
            outFile.seek(startByte); // start writing at the right place
            while ((bytes = in.read(buffer)) != -1) {
              lock.lock();
              // witness();
              outFile.write(buffer, 0, bytes);
              lock.unlock();
            }
          }
        } finally {
          connection.disconnect();
          latch.countDown();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    private void witness() {
      for (int i = 0; i < threadIdx; i++) {
        System.out.print("\t");
      }
      System.out.println("Thread-" + threadIdx);
    }
  }
}
