package com.aziflaj.updistsys;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

class PrimeProcesses {
  static final Long[] NUMBERS = {
    5796176390311246061L,
    7012999129871695678L,
    5171198685506202337L,
    1987190767568698867L,
    9083023940129848844L,
    7635892304987129698L,
    9128254550582789571L,
    2335251515868683803L,
    2908232862892820101L,
    8949602430458993761L,
    4963324134666664519L,
    1405394748903985313L,
    4955604963249413107L,
    6605717036690347269L,
    7605289652550058667L,
    2735948181247105351L,
    7684182252467441719L,
    7056341142757723231L,
    4352099975771053439L,
  };


  public static void main(String[] args) {
    long start, end;

    // single process
    //*
    start = System.currentTimeMillis();
    checkPrimality();
    end = System.currentTimeMillis();
    System.out.println("*** Single Process version took " + (end - start) + "ms ***");
    //*/

    // multiple processes
    //*
    start = System.currentTimeMillis();
    checkPrimalityMultiProcess();
    end = System.currentTimeMillis();
    System.out.println("*** Multi Process version took " + (end - start) + "ms ***");
    //*/
  }

  public static void checkPrimality() {
    String[] command = new String[2 + NUMBERS.length];
    command[0] = "java";
    command[1] = "PrimalityChecker.java";
    int i = 2;

    for (Long n : NUMBERS) {
      command[i++] = n.toString();
    }

    try {
      ProcessBuilder pb = new ProcessBuilder(command);
      Process process = pb.start();

      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      reader.lines().forEach(System.out::println);

      int exitCode = process.waitFor();
      System.out.println("Process exited with code " + exitCode);
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static void checkPrimalityMultiProcess() {
    List<Process> processes = new ArrayList<>();

    for (Long n : NUMBERS) {
      String[] command = {
        "java",
        "PrimalityChecker.java",
        n.toString()
      };

      try {
        ProcessBuilder pb = new ProcessBuilder(command);
        Process process = pb.start();
        processes.add(process);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    for (Process process : processes) {
      try {
        process.waitFor();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    for (Process process : processes) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      reader.lines().forEach(System.out::println);
    }
  }
}

