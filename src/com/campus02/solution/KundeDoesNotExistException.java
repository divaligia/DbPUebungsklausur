package com.campus02.solution;

public class KundeDoesNotExistException extends Exception {
    public KundeDoesNotExistException() {
        System.out.println("This Kunde does not exist!");
    }
}
