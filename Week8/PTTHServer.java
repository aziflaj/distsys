package com.aziflaj.distsys;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;
import java.io.FileReader;

public class PTTHServer {
  public static void main(String[] args) throws Exception {
    ServerSocket server = new ServerSocket(8080);
    System.out.println("Server is running on port 8080");

    while (true) {
      Socket client = server.accept();

      BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
      PrintWriter out = new PrintWriter(client.getOutputStream(), true);

      handleRequest(in, out);

      client.close();
    }
  }

  private static void handleRequest(BufferedReader in, PrintWriter out) throws Exception {
    String request = in.readLine();
    String[] parts = request.split(" ");
    System.out.println(request);

    // Thread.sleep(10000);

    String method = parts[0];
    String path = parts[1];

    switch (method) {
      case "GET":
        handleGet(path, out);
        break;
        // TODO: Implement other methods
      default:
        out.println("HTTP/1.1 405 Method Not Allowed");
        out.println("Content-Type: text/html");
        out.println();
        out.println("<h1>405 Method Not Allowed</h1>");
    }

  }

  private static void handleGet(String path, PrintWriter out) {
    String fileName = path;
    if (path.equals("/")) {
      fileName = "index.html";
    }

    File file = new File("public/" + fileName);

    if (!file.exists()) {
      out.println("HTTP/1.1 404 Not Found");
      out.println("Content-Type: text/html");
      out.println();
      out.println("<h1>404 Not Found</h1>");
      return;
    }

    out.println("HTTP/1.1 200 OK");
    out.println("Content-Type: text/html");
    out.println();
    out.println(getFileContent(file));
  }

  private static String getFileContent(File file) {
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      StringBuilder content = new StringBuilder();
      String line;

      while ((line = reader.readLine()) != null) {
        content.append(line);
      }

      return content.toString();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return "";
  }
}
