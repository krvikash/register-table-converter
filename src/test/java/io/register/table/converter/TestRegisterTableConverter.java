package io.register.table.converter;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;

import static com.google.common.io.Resources.getResource;

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
        Map<String, String> convertedToRegisterTable = converter.convert(path);
        print(convertedToRegisterTable);
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
                .add("failed-10.sql")

                .add("mixed-1.sql")
                .build();
    }

    private static void print(Map<String, String> convertedToRegisterTable)
    {
        for (Map.Entry<String, String>  entry : convertedToRegisterTable.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println(entry.getValue());
            System.out.println();
            System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            System.out.println();
        }
    }
}
