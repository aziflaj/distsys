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
    int startByte = 0;

    // run one process per chunk
    for (int i = 0; i < parallelismCount; i++) {
      int offset = (i == parallelismCount - 1 ? getRemainingBytes() : 0);
      int endByte = startByte + getChunkSize() - 1 + offset;

      ProcessBuilder pb = new ProcessBuilder(
          "java",
          "-cp",
          "out",
          "com.aziflaj.uptdistsys.FileGrabberPro",
          source.toString(),
          destination,
          Integer.toString(startByte),
          Integer.toString(endByte));

      processes.add(pb.start());

      startByte = endByte + 1;
    }

    // wait for all processes to finish
    for (Process p : processes) {
      p.waitFor();
    }
  }
}
