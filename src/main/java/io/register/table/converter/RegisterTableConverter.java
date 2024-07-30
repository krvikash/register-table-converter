package io.register.table.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.logging.log4j.Level.INFO;
import static org.apache.logging.log4j.Level.WARN;

public class RegisterTableConverter
{
    private static final Logger LOGGER = LogManager.getLogger(RegisterTableConverter.class);
    // To match with relax CREATE TABLE <catalog>.<schema_name>.<table_name> (<column_definition>) WITH (... Location = '<table_location>' ...) ....
    private final static String CRATE_TABLE_WITH_LOCATION_RELAX = "CREATE\\s+TABLE\\s+(\\w+)\\.(\\w+)\\.(\\w+)\\s*\\(\\s*[^)]*\\s*\\)\\s*WITH\\s*\\([^)]*location\\s*=\\s*'([^']+)'([^)]+)\\)";
    // To match with strict CREATE TABLE <catalog>.<schema_name>.<table_name> (<column_defintion>) WITH (Location = '<table_location>')
    private final static String CRATE_TABLE_WITH_LOCATION_STRICT = "CREATE\\s+TABLE\\s+(\\w+)\\.(\\w+)\\.(\\w+)\\s*\\(\\s*[^)]*\\s*\\)\\s*WITH\\s*\\(\\s*LOCATION\\s*=\\s*'([^']+)'\\s*\\)(?:\\s*;)?\\s*\n";

    public Map<String, String> convert(String filePath)
    {
        String fileContent = readFile(filePath);
        String newFileContent = fileContent;
        Pattern pattern = Pattern.compile(CRATE_TABLE_WITH_LOCATION_STRICT, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(fileContent);

        Map<String, String> createTableWithLocationToRegisterTableMap = new HashMap<>();
        int totalMatchCount = 0;

        // Loop through all matches found
        while (matcher.find()) {
            String createTableStatement = matcher.group(0).trim();
            String registerTableStatement = createRegisterTableStatement(matcher);
            registerTableStatement = createTableStatement.endsWith(";") ? registerTableStatement + ";" : registerTableStatement;

            newFileContent = newFileContent.replace(createTableStatement, registerTableStatement);
            createTableWithLocationToRegisterTableMap.put(createTableStatement, registerTableStatement);
            totalMatchCount++;
        }

        if (totalMatchCount > 0) {
            writeFile(newFileContent, filePath);
        }

        int relaxCreateTableMatchCount = relaxCreateTableMatch(fileContent);
        if (relaxCreateTableMatchCount > totalMatchCount) {
            LOGGER.log(WARN, "%s extra CREATE TABLE ... WITH ... LOCATION ... statement(s) found which are not replaced with register_table. Please check %s".formatted(relaxCreateTableMatchCount- totalMatchCount, filePath));
        }
        return createTableWithLocationToRegisterTableMap;
    }

    private String createRegisterTableStatement(Matcher matcher)
    {
        String catalog = matcher.group(1);
        String schemaName = matcher.group(2);
        String tableName = matcher.group(3);
        String tableLocation = matcher.group(4);

        return "CALL %s.system.register_table(schema_name => '%s', table_name => '%s', table_location => '%s')"
                .formatted(catalog, schemaName, tableName, tableLocation);
    }

    private static int relaxCreateTableMatch(String fileContent)
    {
        Pattern pattern = Pattern.compile(CRATE_TABLE_WITH_LOCATION_RELAX, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(fileContent);
        int totalMatchCount = 0;

        // Loop through all matches found
        while (matcher.find()) {
            totalMatchCount++;
        }
        return totalMatchCount;
    }

    private String readFile(String filePath)
    {
        try {
            return Files.readString(Path.of(filePath));
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void writeFile(String replacedContent, String inputFilePath)
    {
        String outputFilePath = inputFilePath + "_register_table";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            writer.write(replacedContent);
            LOGGER.log(INFO, "Source path: %s, Replacement path: %s".formatted(inputFilePath, outputFilePath));
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
