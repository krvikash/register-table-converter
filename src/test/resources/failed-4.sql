-- Not convertible

CREATE TABLE test_delta.test.my_table (
   a bigint NOT NULL COMMENT 'Primary Key',
   b bigint
)
COMMENT 'This is not convertible'
WITH (
   location = 'abfss://starburstdata@container.dfs.core.windows.net/test/my_table'
);
