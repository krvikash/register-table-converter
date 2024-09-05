package io.register.table.converter;

import picocli.CommandLine;

import java.io.File;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "register-table-converter",
        header = "Register table converter",
        synopsisHeading = "%nUSAGE:%n%n",
        optionListHeading = "%nOPTIONS:%n",
        usageHelpAutoWidth = true)
public class Console
        implements Callable<Integer>
{
    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "Show this help message and exit")
    public boolean usageHelpRequested;

    @CommandLine.Mixin
    public ConverterOptions converterOptions;

    @Override
    public Integer call()
    {
        return run() ? 0 : 1;
    }

    public boolean run()
    {
        Optional<String> sourcePath = converterOptions.source;
        if (sourcePath.isEmpty()) {
            System.err.println("Please provide --source option");
            return false;
        }
        convert(new File(sourcePath.get()));
        return true;
    }

    private static void convert(File path)
    {
        if (path.isDirectory()) {
            for (File innerPath : Objects.requireNonNull(path.listFiles())) {
                convert(innerPath);
            }
        }
        else {
            convertFile(path);
        }
    }

    private static void convertFile(File filePath)
    {
        new RegisterTableConverter().convert(filePath.getAbsolutePath());
    }
}
