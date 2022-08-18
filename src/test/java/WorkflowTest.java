import net.catenax.simplewfms.SimpleTask;
import net.catenax.simplewfms.Task;
import net.catenax.simplewfms.Workflow;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class WorkflowTest {

    static ExecutorService executorService = Executors.newFixedThreadPool(2);

    @AfterAll
    static void done() {
        executorService.shutdown();
    }


    @Test
    void runOkTest() {
        Workflow workflow = createTestWorkflow(executorService);
        workflow.run();
    }

    @Test
    void runExTest() {
        Workflow workflow = createTestWorkflow(executorService);
        var process3 = workflow.getTask("Process3");
        process3.addStep(t -> {throw new Exception("Test exception");});
        Exception e = assertThrows(
                Exception.class,
                workflow::run
        );
        assertEquals("Test exception", e.getMessage());
    }

    private Workflow createTestWorkflow(Executor executor) {
        Workflow workflow = new Workflow(executor);

        workflow.registerTask(new SimpleTask(), "Start")
                .setOutput("param0", "param0 value")
                .addStep(WorkflowTest::printStatus);

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
                .addStep(WorkflowTest::printStatus);

        workflow.registerTask(new SimpleTask(), "Process11")
                .addDependency("Process1")
                .addStep(t -> Thread.sleep(3000))
                .addStep(WorkflowTest::printStatus);

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
                .addStep(WorkflowTest::printStatus);

        class Process3 extends Task<Process3> {
            public void run() {
                System.out.println(getParameter("p1param"));
                System.out.println(getParameter("p2param"));
            }

            @Override
            public void runThrows() {
            }
        }
        workflow.registerTask(new Process3(), "Process3")
                .registerExternalParameter("Process1", "param1", "p1param")
                .registerExternalParameter("Process2", "param1", "p2param")
                .addDependency("Process11")
                .addStep(WorkflowTest::printStatus);

        return workflow;
    }

    static void printStatus(Task<?> task) {
        System.out.println("Task: " + task.getName() + "; Thread: " + Thread.currentThread().getId() + "; timestamp: " + Instant.now());
    }
}
