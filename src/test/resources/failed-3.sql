-- Not convertible

CREATE TABLE test_delta.test.my_table (
   a bigint NOT NULL COMMENT 'Primary Key',
   b bigint
)
WITH (
   prop1 = 'value',
   location = 'abfss://starburstdata@container.dfs.core.windows.net/test/my_table',
   prop2 = 'value'
);
