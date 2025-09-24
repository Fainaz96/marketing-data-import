package com.flaregames.imports.tableslice;

import com.google.inject.Inject;

import com.flaregames.imports.importlog.ImportLogEntry;
import com.flaregames.imports.importlog.ImportLogServiceDefault;
import com.flaregames.imports.integration.AbstractIntegrationTest;
import com.flaregames.imports.integration.TestDao;
import com.flaregames.imports.schema.ClassToTableMapper;
import com.flaregames.imports.schema.DBSchemaName;
import com.flaregames.imports.schema.SchemaDao;
import com.flaregames.imports.testdata.TestDataRecord;

import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class ImportIntegrationTest extends AbstractIntegrationTest {

    @Inject
    private TableSliceProcessor sliceProcessor;

    @Inject
    private ImportLogServiceDefault importLogService;

    @Inject
    private ClassToTableMapper tableMapper;

    @Inject
    private TableSliceFactory tableSliceFactory;

    @Inject
    private TestDao testDao;

    @Inject
    private SchemaDao schemaDao;

    @Inject
    private TableSliceDao tableSliceDao;

    @Inject
    private DBSchemaName schemaName;

    private List<TestDataRecord> importData(Instant day) {

        String[] dates = new String[]{"2019-12-05T01:02:03.456Z", "2020-01-05T01:02:03.456Z", "2019-12-31T19:20:21Z",
                "2020-01-10T00:00" + ":00.123Z"};
        return IntStream.range(0, dates.length)
                .mapToObj(index -> new TestDataRecord(index, Instant.parse(dates[index]), String.format("entry %d", index)))
                .collect(Collectors.toList());
    }

    @Before
    public void before() {
        testDao.dropView(schemaName.toString(), "some_table");
        testDao.dropView(schemaName.toString(), "some_view");
        testDao.dropTable(schemaName.toString(), "some_table_m201912");
        testDao.dropTable(schemaName.toString(), "some_table_m202001");
        testDao.dropTable(schemaName.toString(), "import_log");
        schemaDao.createTable(schemaName.toString(), "import_log", tableMapper.getTableDefinition(ImportLogEntry.class));
    }

    private void uploadAndCreateFromMeta(TableSliceMeta meta) {

        Instant day = Instant.parse("2020-05-05T00:00:00Z");
        TableSliceSet set = new TableSliceSet(meta, tableSliceFactory);

        importLogService.checkForDailyJob("ref_import", "ref_log", day).execute(() -> {
            importData(day).stream().forEach(d -> set.addObject(d.stamp, d));
            set.forEach(sliceProcessor::uploadToS3);
            set.forEach(sliceProcessor::upsertFromSlice);
            sliceProcessor.createView(meta);
        });
    }

    @Test
    public void uploadAndCreate() {

        TableSliceMeta meta = TableSliceMeta.builder()
                .withBaseTable("some_table")
                .withS3Path("ref_import")
                .withCreateSQL(tableMapper.getTableDefinition(TestDataRecord.class))
                .withUpsertColumns("id")
                .build();

        uploadAndCreateFromMeta(meta);

        List<Tuple> list_m201912 = testDao.queryTuples("stamp::VARCHAR, name, id", schemaName.toString(), "some_table_m201912");
        assertThat(list_m201912).containsExactlyInAnyOrder(tuple("2019-12-05 01:02:03.456", "entry 0", 0),
                tuple("2019-12-31 19:20:21", "entry 2", 2));

        List<Tuple> list_m202001 = testDao.queryTuples("stamp::VARCHAR, name, id", schemaName.toString(), "some_table_m202001");
        assertThat(list_m202001).containsExactlyInAnyOrder(tuple("2020-01-05 01:02:03.456", "entry 1", 1),
                tuple("2020-01-10 00:00:00.123", "entry 3", 3));

        List<Tuple> list_view = testDao.queryTuples("stamp::VARCHAR, name, id", schemaName.toString(), "some_table");
        assertThat(list_view).containsExactlyInAnyOrder(tuple("2019-12-05 01:02:03.456", "entry 0", 0),
                tuple("2019-12-31 19:20:21", "entry 2", 2), tuple("2020-01-05 01:02:03.456", "entry 1", 1),
                tuple("2020-01-10 00:00:00.123", "entry 3", 3));

    }

    @Test
    public void uploadAnCreateWithAddTable() {

        tableSliceDao.createOrReplaceView(schemaName.toString(), "some_view",
                "SELECT 1::integer AS id, '2021-01-01'::timestamp AS stamp, 'hello'::varchar(10) as name, '2021-01-01'::timestamp AS " +
                        "formatted");

        TableSliceMeta meta = TableSliceMeta.builder()
                .withBaseTable("some_table")
                .withS3Path("ref_import")
                .withCreateSQL(tableMapper.getTableDefinition(TestDataRecord.class))
                .withUpsertColumns("id")
                .withAddViewTables("some_view")
                .build();

        uploadAndCreateFromMeta(meta);

        List<Tuple> list_view = testDao.queryTuples("stamp::VARCHAR, name, id", schemaName.toString(), "some_table");
        assertThat(list_view).containsExactlyInAnyOrder(tuple("2019-12-05 01:02:03.456", "entry 0", 0),
                tuple("2019-12-31 19:20:21", "entry 2", 2), tuple("2020-01-05 01:02:03.456", "entry 1", 1),
                tuple("2020-01-10 00:00:00.123", "entry 3", 3), tuple("2021-01-01 00:00:00", "hello", 1));

    }

}
