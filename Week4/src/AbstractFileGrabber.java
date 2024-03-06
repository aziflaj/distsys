package com.aziflaj.uptdistsys;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

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

  protected boolean isPartialContentSupported() throws IOException {
    HttpURLConnection connection = (HttpURLConnection) source.openConnection();
    Map<String, List<String>> headers = connection.getHeaderFields();
    connection.disconnect();

    return headers.containsKey("Accept-Ranges");
  }

  protected long getFileSize() throws IOException {
    HttpURLConnection connection = (HttpURLConnection) source.openConnection();
    long fileSize = connection.getContentLengthLong();
    connection.disconnect();

    if (fileSize > 0) {
      return fileSize;
    }

    // read the content length from the Content-Length header
    Map<String, List<String>> headers = connection.getHeaderFields();
    List<String> contentLength = headers.get("Content-Length");
    if (contentLength != null) {
      return Long.parseLong(contentLength.get(0));
    }

    throw new IOException("Could not determine file size");
  }

  protected long getChunkSize() throws IOException {
    return getFileSize() / parallelismCount;
  }

  protected long getRemainingBytes() throws IOException {
    return getFileSize() % parallelismCount;
  }
}
