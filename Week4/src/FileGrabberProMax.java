package com.aziflaj.uptdistsys;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

public class FileGrabberProMax extends AbstractFileGrabber {
  public FileGrabberProMax(URL source, String destination, int parallelismCount) {
    super(source, destination, parallelismCount);
  }

  @Override
  public void download() throws Exception {
    List<Process> processes = new ArrayList<>();
    long startByte = 0;

    if (!isPartialContentSupported()) {
      throw new IOException("Partial content not supported");
    } else {
      System.out.println("Partial content supported");
    }

    // run one process per chunk
    for (int i = 0; i < parallelismCount; i++) {
      long offset = (i == parallelismCount - 1 ? getRemainingBytes() : 0);
      long endByte = startByte + getChunkSize() - 1 + offset;

      System.out.println("File size: " + getFileSize());
      System.out.println("Chunk size: " + getChunkSize());
      System.out.println("Downloading chunk " + i + " from " + startByte + " to " + endByte);

      ProcessBuilder pb = new ProcessBuilder(
          "java",
          "-cp",
          "out",
          "com.aziflaj.uptdistsys.FileGrabberPro",
          source.toString(),
          destination,
          Long.toString(startByte),
          Long.toString(endByte));

      processes.add(pb.start());

      startByte = endByte + 1;
    }

    // wait for all processes to finish
    for (Process p : processes) {
      p.waitFor();
    }
  }
}
