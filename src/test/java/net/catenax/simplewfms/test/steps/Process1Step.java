package net.catenax.simplewfms.test.steps;

import net.catenax.simplewfms.Task;
import net.catenax.simplewfms.test.TaskTools;

public class Process1Step extends Task<Process1Step> {

    private final String testString = "Hello from Step!";

    public Process1Step() {
        name = "Process1";
        registerExternalParameter("Start", "param0", "sparam");
    }
    @Override
    public void runThrows() throws Exception {
        Thread.sleep(5000);
        System.out.println(testString);
        TaskTools.printStatus(this);
        String param = (String) getParameter("sparam");
        setOutput("param1", param + ":t1");
    }
}
