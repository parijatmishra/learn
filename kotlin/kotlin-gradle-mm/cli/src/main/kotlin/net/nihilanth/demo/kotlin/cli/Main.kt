package net.nihilanth.demo.kotlin.cli

import net.nihilanth.demo.kotlin.api.Framework
import net.nihilanth.demo.kotlin.api.IRecordProcessor
import net.nihilanth.demo.kotlin.api.Record

class RecordProcessorImpl: IRecordProcessor {
    override fun init() {
        println("RecordProcessorImpl.init()")
    }

    override fun processRecords(records: Array<Record>) {
        println("RecordProcessorImpl.processRecords: ${records.size} records")
    }

    override fun close() {
        println("RecordProcessorImpl.close()")
    }
}

fun main(args: Array<String>) {
    val p = RecordProcessorImpl()
    val f = Framework(p)

    f.init()
    f.run()
    f.stop()
}