package io.register.table.converter;

import picocli.CommandLine;

public class Converter
{
    public static void main(String[] args)
    {
        System.exit(createCommandLine(new Console()).execute(args));
    }

    public static CommandLine createCommandLine(Object command)
    {
        return new CommandLine(command);
    }
}
