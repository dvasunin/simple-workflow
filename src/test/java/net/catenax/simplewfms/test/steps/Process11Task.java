package net.catenax.simplewfms.test.steps;

import net.catenax.simplewfms.Task;
import net.catenax.simplewfms.test.TaskTools;

public class Process11Task extends Task<Process11Task> {

    public Process11Task() {
        name ="Process11";
        addDependency("Process1");
    }
    @Override
    public void runThrows() throws Exception {
        Thread.sleep(3000);
        TaskTools.printStatus(this);
    }
}
