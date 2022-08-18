package net.catenax;

import net.catenax.simplewfms.SimpleTask;
import net.catenax.simplewfms.Task;
import net.catenax.simplewfms.Workflow;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Workflow workflow = new Workflow(executorService);

        workflow.registerTask(new SimpleTask(), "Start")
                .setOutput("param0", "param0 value")
                .addStep(Main::printStatus);

        class Process1 extends Task<Process1> {
            final String testString = "Hello from Step!";
            public void runThrows() throws InterruptedException {
                String param = (String) getParameter("sparam");
                setOutput("param1", param + ":t1");
                Thread.sleep(5000);
            }
        }
        workflow.registerTask(new Process1(), "Process1")
                .registerExternalParameter("Start", "param0", "sparam")
                .addStep(t -> System.out.println(t.testString))
                .addStep(Main::printStatus);

        workflow.registerTask(new SimpleTask(), "Process11")
                .addDependency("Process1")
                .addStep(t -> Thread.sleep(3000))
                .addStep(Main::printStatus);

        class Process2 extends Task<Process2> {
            @Override
            public void runThrows() throws InterruptedException {
                String param = (String) getParameter("sparam");
                setOutput("param1", param + ":t2");
                Thread.sleep(5000);
            }
        }
        workflow.registerTask(new Process2(), "Process2")
                .registerExternalParameter("Start", "param0", "sparam")
                .addStep(Main::printStatus);

        class Process3 extends Task<Process3> {
            public void run() {
                System.out.println(getParameter("p1param"));
                System.out.println(getParameter("p2param"));
            }
            @Override
            public void runThrows() {}
        }
        workflow.registerTask(new Process3(), "Process3")
                .registerExternalParameter("Process1", "param1", "p1param")
                .registerExternalParameter("Process2", "param1", "p2param")
                .addDependency("Process11")
                .addStep(Main::printStatus);

        workflow.run();
        executorService.shutdown();
    }

    static void printStatus(Task<?> task) {
        System.out.println("Task: " + task.getName() + "; Thread: " + Thread.currentThread().getId() + "; timestamp: " + Instant.now());
    }
}