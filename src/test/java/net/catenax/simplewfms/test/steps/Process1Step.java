package net.catenax.simplewfms.test.steps;

import net.catenax.simplewfms.Task;
import net.catenax.simplewfms.test.TaskTools;

import java.util.function.Supplier;

public class Process1Step extends Task<Process1Step> {

    private final String testString = "Hello from Step!";
    private Supplier<String> param0 = registerExternalParameter("Start", "param0", String.class);

    public Process1Step() {
        name = "Process1";
    }
    @Override
    public void runThrows() throws Exception {
        Thread.sleep(5000);
        System.out.println(testString);
        TaskTools.printStatus(this);
        String param = param0.get();
        setOutput("param1", param + ":t1");
    }
}
