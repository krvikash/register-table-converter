package io.register.table.converter;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static com.google.common.io.Resources.getResource;
import static io.register.table.converter.RegisterTableConverter.readFile;

public class TestRegisterTableConverter
{
    private final RegisterTableConverter converter;

    public TestRegisterTableConverter()
    {
        converter = new RegisterTableConverter();
    }

    @ParameterizedTest
    @MethodSource("fileNameProvider")
    public void testSample(String fileName)
    {
        String path = getResource(fileName).getPath();
        String generatedFile = converter.convert(path);
        print(generatedFile);
    }

    public static List<Object> fileNameProvider()
    {
        return ImmutableList.builder()
                .add("success-1.sql")
                .add("success-2.sql")
                .add("success-3.sql")
                .add("success-4.sql")
                .add("success-5.sql")
                .add("success-6.sql")
                .add("success-7.sql")

                .add("failed-1.sql")
                .add("failed-2.sql")
                .add("failed-3.sql")
                .add("failed-4.sql")
                .add("failed-5.sql")

                .add("mixed-1.sql")
                .build();
    }

    private static void print(String generatedFile)
    {
        System.out.println(readFile(generatedFile));
    }
}
