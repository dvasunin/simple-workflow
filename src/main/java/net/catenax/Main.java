package net.catenax;

import net.catenax.simplewfms.SimpleTask;
import net.catenax.simplewfms.Task;
import net.catenax.simplewfms.Workflow;

import java.time.Instant;

public class Main {
    public static void main(String[] args) {
        Workflow workflow = new Workflow(10);

        workflow.registerTask(new SimpleTask("Start"))
                .setOutput("param0", "param0 value");


        class Process1 extends Task<Process1> {
            final String testString = "Hello from Step!";
            public Process1(){
                super("Process1");
                addStep(t -> Thread.sleep(5000));
            }
            public void run() {
                String param = (String) getParameter("sparam");
                setOutput("param1", param + ":t1");
            }
        };
        workflow.registerTask(new Process1())
                .registerExternalParameter("Start", "param0", "sparam")
                .addStep(t -> System.out.println(t.testString));

        workflow.registerTask(new SimpleTask("Process11"))
                .addDependency("Process1")
                .addStep(t -> Thread.sleep(3000));


        class Process2 extends Task<Process2> {
            public Process2(){
                super("Process2");
            }
            public void run() {
                String param = (String) getParameter("sparam");
                setOutput("param1", param + ":t2");
            }
        };
        workflow.registerTask(new Process2())
                .addStep(t -> Thread.sleep(5000))
                .registerExternalParameter("Start", "param0", "sparam");


        class Process3 extends Task<Process3> {
            public Process3(){
                super("Process3");
            }
            public void run() {
                System.out.println(getParameter("p1param"));
                System.out.println(getParameter("p2param"));
                System.out.println(Instant.now());
            }
        }
        workflow.registerTask(new Process3())
                .registerExternalParameter("Process1", "param1", "p1param")
                .registerExternalParameter("Process2", "param1", "p2param")
                .addDependency("Process11");

        System.out.println(Instant.now());
        workflow.run();
        workflow.shutdown();
    }
}