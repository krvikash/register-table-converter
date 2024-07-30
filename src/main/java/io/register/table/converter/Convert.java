package io.register.table.converter;

import java.io.File;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

public class Convert
{
    private static final RegisterTableConverter converter = new RegisterTableConverter();

    public static void main(String[] args)
    {
        checkArgument(args.length == 1, "Provide only one argument (directory/file).");
        System.out.println("Logging at convert.log file");
        convert(new File(args[0]));
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
        converter.convert(filePath.getAbsolutePath());
    }
}
