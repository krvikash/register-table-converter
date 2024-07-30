-- Case sensitive

Create Table test_delta.test.my_table (
   a bigint NOT NULL COMMENT 'Primary Key',
   b bigint
)
with (
   LOCATION = 'abfss://starburstdata@container.dfs.core.windows.net/test/my_table'
);
