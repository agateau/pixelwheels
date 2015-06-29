package com.greenyetilab.tinywheels;

/**
 * Orchestrate changes between screens
 */
public interface Maestro {
    void actionTriggered(String action);
    void start();
}
