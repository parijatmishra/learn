package net.nihilanth.demo.kotlin.api

public data class Record(val id: Int, val data: String)

public interface IRecordProcessor {
    fun init()
    fun processRecords(records: Array<Record>)
    fun close()
}

public class Framework(private val processor: IRecordProcessor) {
    fun init() = processor.init()
    fun stop() = processor.close()

    fun run() {
        for (i in 1..10) {
            val ar = Array<Record>(10, { Record(it, "${i}-${it}") })
            processor.processRecords(ar)
        }
    }
}