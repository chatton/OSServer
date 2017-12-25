package ie.gmit.sw.command.impl;

import ie.gmit.sw.command.Command;
import ie.gmit.sw.server.UserHandler;

public class ExitCommand implements Command {

    private final UserHandler handler;

    public ExitCommand(final UserHandler handler) {
        this.handler = handler;
    }

    @Override
    public void execute() {
        handler.stop();
    }
}
