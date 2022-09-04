package net.catenax.simplewfms.test.steps;

import net.catenax.simplewfms.Task;
import net.catenax.simplewfms.test.TaskTools;

public class Process3Task extends Task<Process3Task> {

    public Process3Task() {
        name ="Process3";
        registerExternalParameter("Process1", "param1", "p1param")
                        .registerExternalParameter("Process2", "param1", "p2param")
                        .addDependency("Process11");
    }


    @Override
    public void run() {
        System.out.println(getParameter("p1param"));
        System.out.println(getParameter("p2param"));
        TaskTools.printStatus(this);
    }

    @Override
    public void runThrows() {}
}
