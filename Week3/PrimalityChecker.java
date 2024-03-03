package com.aziflaj.uptdistsys;

class PrimalityChecker {
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

  // Checks whether any of the CLI Args is a prime number
  // Usage: java PrimalityChecker.java 123 456 789
  public static void main(String[] args) {
    // System.out.println("Checking primality of the following numbers:");
    // System.out.println(String.join(", ", args));

    for (String arg : args) {
      Long num = Long.parseLong(arg);

      System.out.println(num + " is " + (isPrime(num) ? "prime" : "not prime"));
    }
  }
}
