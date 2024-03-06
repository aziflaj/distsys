package com.aziflaj.uptdistsys;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class AbstractFileGrabber {
  protected final URL source;
  protected final String destination;
  protected final int parallelismCount;

  public AbstractFileGrabber(URL source, String destination, int parallelismCount) {
    this.source = source;
    this.destination = destination;
    this.parallelismCount = parallelismCount;
  }

  public abstract void download() throws Exception;

  protected int getFileSize() throws IOException {
    HttpURLConnection connection = (HttpURLConnection) source.openConnection();
    int fileSize = connection.getContentLength();
    connection.disconnect();

    return fileSize;
  }

  protected int getChunkSize() throws IOException {
    return getFileSize() / parallelismCount;
  }

  protected int getRemainingBytes() throws IOException {
    return getFileSize() % parallelismCount;
  }
}
