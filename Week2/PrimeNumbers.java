package com.aziflaj.uptdistsys;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class PrimeNumbers {
  public static boolean isPrime(Long n) {
    if (n <= 1) {
      return false;
    }

    for (int i = 2; i <= Math.sqrt(n); i++) {
      if (n % i == 0) {
        return false;
      }
    }

    return true;
  }

  public static void logPrimality(String tag, Long n, boolean isPrime) {
    String primality = isPrime ? "prime" : "not prime";

    System.out.printf("[%s] \t %d is %s\n", tag, n, primality);
  }

  public static void main(String[] args) {
    long start, end;
    Long[] numbers = {
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

    // single threaded version first
    //*
    start = System.currentTimeMillis();
    checkPrimality(numbers);
    end = System.currentTimeMillis();
    System.out.println("*** Single threaded version took " + (end - start) + "ms ***");
    //*/

    // multi-threaded version
    //*
    start = System.currentTimeMillis();
    checkPrimalityMultiThreaded(numbers);
    end = System.currentTimeMillis();
    System.out.println("*** Multi-threaded version took " + (end - start) + "ms ***");
    //*/

    // thread-pool version
    //*
    start = System.currentTimeMillis();
    checkPrimalityThreadPool(numbers);
    end = System.currentTimeMillis();
    System.out.println("*** Thread-pool version took " + (end - start) + "ms ***");
    //*/

    // fixed pool size version
    //*
    start = System.currentTimeMillis();
    checkPrimalityFixedSizePool(numbers);
    end = System.currentTimeMillis();
    System.out.println("*** 4-pool version took " + (end - start) + "ms ***");
    //*/
  }

  public static void checkPrimality(Long... numbers) {
    for (Long n : numbers) {
      logPrimality("Serial", n, isPrime(n));
    }
  }


  public static void checkPrimalityMultiThreaded(Long... numbers) {
    Thread[] threads = new Thread[numbers.length];

    for (int i = 0; i < numbers.length; i++) {
      Long n = numbers[i];

      threads[i] = new Thread(() -> {
        logPrimality("MultiT", n, isPrime(n));
      });
    }

    // start all threads
    for (Thread t : threads) {
      t.start();
    }

    // wait for threads to finish
    for (Thread t : threads) {
      try {
        t.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public static void checkPrimalityThreadPool(Long... numbers) {
    ExecutorService executor = Executors.newCachedThreadPool();

    for (Long n : numbers) {
      executor.submit(() -> {
        logPrimality("CachedPool", n, isPrime(n));
      });
    }

    try {
      executor.shutdown();
      while (!executor.awaitTermination(1000, java.util.concurrent.TimeUnit.MILLISECONDS)) {
        // System.out.println("Waiting for tasks to finish...");
        // noop
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static void checkPrimalityFixedSizePool(Long... numbers) {
    ExecutorService executor = Executors.newFixedThreadPool(2);

    for (Long n : numbers) {
      executor.submit(() -> {
        logPrimality("4Fixed", n, isPrime(n));
      });
    }

    try {
      executor.shutdown();
      while (!executor.awaitTermination(1000, java.util.concurrent.TimeUnit.MILLISECONDS)) {
        // System.out.println("Waiting for tasks to finish...");
        // noop
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
