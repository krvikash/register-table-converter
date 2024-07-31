package io.register.table.converter;

import io.trino.cli.lexer.StatementSplitter;
import io.trino.sql.parser.SqlParser;
import io.trino.sql.tree.CreateTable;
import io.trino.sql.tree.Property;
import io.trino.sql.tree.Statement;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class RegisterTableConverter
{
    // To match with relax CREATE TABLE <catalog>.<schema_name>.<table_name> (<column_definition>) WITH (... Location = '<table_location>' ...) ....
    private final static String CRATE_TABLE_WITH_LOCATION_RELAX = "CREATE\\s+TABLE\\s+(\\w+)\\.(\\w+)\\.(\\w+)\\s*\\(\\s*[^)]*\\s*\\)\\s*WITH\\s*\\([^)]*location\\s*=\\s*'([^']+)'([^)]+)\\)";
    // To match with strict CREATE TABLE <catalog>.<schema_name>.<table_name> (<column_defintion>) WITH (Location = '<table_location>')
    private final static String CRATE_TABLE_WITH_LOCATION_STRICT = "CREATE\\s+TABLE\\s+(\\w+)\\.(\\w+)\\.(\\w+)\\s*\\(\\s*[^)]*\\s*\\)\\s*WITH\\s*\\(\\s*LOCATION\\s*=\\s*'([^']+)'\\s*\\)(?:\\s*;)?\\s*\n";

    public String convert(String filePath)
    {
        if (!Files.exists(Path.of(filePath))) {
            throw new RuntimeException("%s does not exist".formatted(filePath));
        }
        System.out.println("----------- Started Converting %s -----------".formatted(filePath));
        String fileContent = readFile(filePath);
        String newFileContent = fileContent;

        StatementSplitter statementSplitter = new StatementSplitter(fileContent);
        List<StatementSplitter.Statement> statementList = statementSplitter.getCompleteStatements();
        boolean atLeastOneConversion = false;

        for (StatementSplitter.Statement statement : statementList) {
            SqlParser sqlParser = new SqlParser();
            String sqlQuery = statement.statement();
            Statement sqlStatement = sqlParser.createStatement(statement.statement());
            if (sqlStatement instanceof CreateTable createTableStatement) {
                boolean validForConversion = false;
                List<String> nameParts = createTableStatement.getName().getParts();
                // Only when table name is full qualified
                if (nameParts.size() == 3) {
                    String catalog =nameParts.get(0);
                    String schemaName =nameParts.get(1);
                    String tableName =nameParts.get(2);
                    List<Property> properties = createTableStatement.getProperties();
                    // Only when there is only table location and no comment
                    if (properties.size() == 1 && createTableStatement.getComment().isEmpty()) {
                        Property property = properties.getLast();
                        String propertyName = property.getName().getValue();
                        String propertyValue = property.getNonDefaultValue().toString();
                        if (propertyName.equalsIgnoreCase("Location")) {
                            validForConversion = true;
                            atLeastOneConversion = true;
                            String tableLocation = propertyValue;
                            if (propertyValue.startsWith("'")) {
                                tableLocation = tableLocation.substring(1);
                            }
                            if (propertyValue.endsWith("'")) {
                                tableLocation = tableLocation.substring(0, tableLocation.length() - 1);
                            }
                            String registerTableStatement = createRegisterTableStatement(catalog, schemaName, tableName, tableLocation);
                            // This will replace the sql comment as well if there are any in the source file
                            newFileContent = newFileContent.replace(sqlQuery, registerTableStatement);
                        }
                    }
                }
                if (!validForConversion) {
                    System.out.println();
                    System.out.println("WARNING: \"\n%s\n\"\ncould not be converted to register_table statement. Please check %s".formatted(sqlQuery, filePath));
                    System.out.println();
                }
            }
        }

        if (atLeastOneConversion) {
            return writeFile(newFileContent, filePath);
        }
        System.out.println("----------- Completed Converting %s -----------".formatted(filePath));
        System.out.println();
        return filePath;
    }

    private String createRegisterTableStatement(String catalog, String schemaName, String tableName, String tableLocation)
    {
        return "CALL %s.system.register_table(schema_name => '%s', table_name => '%s', table_location => '%s')"
                .formatted(catalog, schemaName, tableName, tableLocation);
    }

    public static String readFile(String filePath)
    {
        try {
            return Files.readString(Path.of(filePath));
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String writeFile(String replacedContent, String inputFilePath)
    {
        String outputFilePath = inputFilePath + "_register_table";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            writer.write(replacedContent);
            System.out.println("Source path: %s, Replacement path: %s".formatted(inputFilePath, outputFilePath));
            return outputFilePath;
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
