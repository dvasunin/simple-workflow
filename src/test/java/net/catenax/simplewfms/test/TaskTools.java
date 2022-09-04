package net.catenax.simplewfms.test;

import net.catenax.simplewfms.Task;

import java.time.Instant;

public class TaskTools {
    public static void printStatus(Task<?> task) {
        System.out.println("Task: " + task.getName() + "; Thread: " + Thread.currentThread().getId() + "; timestamp: " + Instant.now());
    }
}
