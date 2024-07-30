## Overview
This project aims to convert following type statement with `register_table` statement
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

Other format of `CREATE TABLE` statement will not be converted.

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

## Usage

Using file
```
java -cp register-table-converter-1.0-jar-with-dependencies.jar /path/to/file/
```

Using directory
```
java -cp register-table-converter-1.0-jar-with-dependencies.jar /path/to/directory/
```

If there is any match then in the same location as of source file there will be a new file created with `_register_table` suffix
which will contain the `register_table` statement instead `create table` statement.
