## Overview
This project aims to convert following type `CREATE TABLE` statement to `register_table` statement.
```sql
CREATE TABLE <catalog>.<schema_name>.<table_name> (<column_defintion>) WITH (Location = '<table_location>')
```

## Example

Following statement will be converted
```sql
CREATE TABLE test_delta.test.my_table (
   a bigint NOT NULL COMMENT 'Primary Key',
   b bigint
)
WITH (
   location = 'abfss://starburstdata@container.dfs.core.windows.net/test/my_table'
)
```

to

```dtd
CALL test_delta.system.register_table(schema_name => 'test', table_name => 'my_table', table_location => 'abfss://starburstdata@container.dfs.core.windows.net/test/my_table')
```

Other format of `CREATE TABLE` statement will not be converted. Those statements will be skipped.

There should not be any other property than `property`

```
CREATE TABLE test_delta.test.my_table (
   a bigint NOT NULL COMMENT 'Primary Key',
   b bigint
)
WITH (
   prop1 = '',
   location = 'abfss://starburstdata@container.dfs.core.windows.net/test/my_table'
);
```

There should not be any `COMMENT`
```
CREATE TABLE test_delta.test.my_table (
   a bigint NOT NULL COMMENT 'Primary Key',
   b bigint
)
WITH (
   location = 'abfss://starburstdata@container.dfs.core.windows.net/test/my_table'
) COMMENT 'This is not convertible';
```

## How to build

Use maven to build the project

Build and compile
```
mvn clean install
```
Build jar with dependencies. This will generate `register-table-converter-1.0-jar-with-dependencies` jar in `target` directory.
```
mvn clean compile assembly:single
```

## Usage

Using file:
```
java -cp lib/register-table-converter-1.0-jar-with-dependencies.jar io.register.table.converter.Convert /path/to/file/
```

Using directory:

This approach will use files recursively.
```
java -cp lib/register-table-converter-1.0-jar-with-dependencies.jar io.register.table.converter.Convert /path/to/directory/
```

If there is any match then in the same location as of source file there will be a new file created with `_register_table` suffix
which will contain the `register_table` statement instead of `create table` statement.
