package net.catenax.simplewfms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Workflow implements Runnable{

    final Map<String, Task<?>> tasks = new HashMap<>();
    ExecutorService executor;

    public Workflow(int concurrency) {
        executor = Executors.newFixedThreadPool(concurrency);
    }


    @Override
    public void run() {
        try {
            CompletionService<Task<?>> cs = new ExecutorCompletionService<>(executor);
            prepare();
            List<Task<?>> ready = tasks.values().stream().filter(Task::isReadyToRun).toList();
            int activeThreads = 0;
            while (!ready.isEmpty() || activeThreads > 0) {
                ready.stream().peek(task -> task.status = Status.ACTIVE).forEach(task -> cs.submit(task.processes, task));
                activeThreads += ready.size();
                var finishedTask = cs.take().get();
                activeThreads--;
                finishedTask.status = Status.DONE;
                ready = finishedTask.dependedOn.stream()
                        .filter(Task::isReadyToRun)
                        .toList();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void prepare() {
        for (Task<?> task : tasks.values()) {
            for (String name : task.dependsOnName) {
                var dependsOn = tasks.get(name);
                task.dependsOn.add(dependsOn);
                dependsOn.dependedOn.add(task);
            }
        }
    }

    public <T extends Task<T>> T registerTask(T task, String name) {
        task.self = task;
        task.workflow = this;
        task.name = name;
        tasks.put(name, task);
        return task;
    }

    public void shutdown() {
        executor.shutdown();
    }
}
