-- The test coverage for extra comment
-- Create Table for test_delta.test.my_table

CREATE TABLE test_delta.test.my_table (
   a bigint NOT NULL COMMENT 'Primary Key',
   b bigint
)
WITH (
   location = 'abfss://starburstdata@container.dfs.core.windows.net/test/my_table'
);

-- Create Table for another_catalog.another_schema.another_table

CREATE TABLE another_catalog.another_schema.another_table (
   a bigint NOT NULL COMMENT 'Primary Key',
   b bigint
)
WITH (
   location = 'abfss://starburstdata@container.dfs.core.windows.net/another_schema/another_table'
);
