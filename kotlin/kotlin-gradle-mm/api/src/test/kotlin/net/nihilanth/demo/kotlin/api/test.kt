package net.nihilanth.demo.kotlin.api

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

class TestRecordProcessor: IRecordProcessor {
    var initCalled = 0
    var closeCalled = 0
    var processCalled = 0
    var recordsPassed = 0

    override fun init() {
        this.initCalled += 1
    }

    override fun close() {
        this.closeCalled += 1
    }

    override fun processRecords(records: Array<Record>) {
        this.processCalled += 1
        this.recordsPassed += records.size
    }
}
class LibTest {

    @Test fun whenFrameworkInit_thenRecordProcessorInit() {
        val recordProcessor = TestRecordProcessor()
        val framework = Framework(recordProcessor)
        framework.init()
        assertEquals("init() should be called once", 1, recordProcessor.initCalled)
        assertEquals("close() should be called 0 times", 0, recordProcessor.closeCalled)
        assertEquals("processRecords() should be called 0 times", 0, recordProcessor.processCalled)
    }

    @Test fun whenFrameworkStop_thenRecordClose() {
        val recordProcessor = TestRecordProcessor()
        val framework = Framework(recordProcessor)
        framework.stop()
        assertEquals("init() should be called 0 times", 0, recordProcessor.initCalled)
        assertEquals("close() should be called once", 1, recordProcessor.closeCalled)
        assertEquals("processRecords() should be called 0 times", 0, recordProcessor.processCalled)
    }

    @Test fun whenFrameworkRun_thenRecordProcessorProcess() {
        val recordProcessor = TestRecordProcessor()
        val framework = Framework(recordProcessor)
        framework.run()
        assertEquals("init() should be called 0 times", 0, recordProcessor.initCalled)
        assertEquals("close() should be called 0 times", 0, recordProcessor.closeCalled)
        assertEquals("processRecords() should be called 10 times", 10, recordProcessor.processCalled)
        assertEquals("100 records should be passed", 100, recordProcessor.recordsPassed)
    }
}