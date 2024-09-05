package io.register.table.converter;

import picocli.CommandLine;

import java.util.Optional;

public class ConverterOptions
{
    @CommandLine.Option(names = "--source", paramLabel = "<source>", description = "Path of file/directory containing CREATE TABLE ... WITH ... LOCATION statement")
    public Optional<String> source;
}
