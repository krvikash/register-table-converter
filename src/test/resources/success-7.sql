-- Multiple type statements

Create Table test_delta.test.my_table (
   a bigint NOT NULL COMMENT 'Primary Key',
   b bigint
)
with (
   LOCATION = 'abfss://starburstdata@container.dfs.core.windows.net/test/my_table'
);

INSERT INTO test_delta.test.my_table values (1, 2);

CREATE TABLE another_catalog.another_schema.another_table (
   a bigint NOT NULL COMMENT 'Primary Key',
   b bigint
)
WITH (
   location = 'abfss://starburstdata@container.dfs.core.windows.net/another_schema/another_table'
);

INSERT INTO another_catalog.another_schema.another_table values (1, 2);
