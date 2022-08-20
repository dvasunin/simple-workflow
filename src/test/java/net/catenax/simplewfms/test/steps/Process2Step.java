package net.catenax.simplewfms.test.steps;

import net.catenax.simplewfms.Task;
import net.catenax.simplewfms.test.TaskTools;

import java.util.function.Supplier;

public class Process2Step extends Task<Process2Step> {

    Supplier<String> param0 = registerExternalParameter("Start", "param0");

    public Process2Step() {
        name = "Process2";
    }
    @Override
    public void runThrows() throws InterruptedException {
        String param = param0.get();
        setOutput("param1", param + ":t2");
        Thread.sleep(5000);
        TaskTools.printStatus(this);
    }
}