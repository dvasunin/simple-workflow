package net.catenax.simplewfms.test.steps;

import net.catenax.simplewfms.Task;
import net.catenax.simplewfms.test.TaskTools;

public class Process2Step extends Task<Process2Step> {

    public Process2Step() {
        name = "Process2";
        registerExternalParameter("Start", "param0", "sparam");
    }
    @Override
    public void runThrows() throws InterruptedException {
        String param = (String) getParameter("sparam");
        setOutput("param1", param + ":t2");
        Thread.sleep(5000);
        TaskTools.printStatus(this);
    }
}