import kotlin.time.measureTimedValue

fun <T> printTimedResult(expectedValue: T? = null, operation: () -> T) {
    val result = measureTimedValue(operation)

    println("time taken: \n\t${result.duration}")
    println("result: \n\t${result.value}")
    if(expectedValue != null) {
        println("passes: \n\t${expectedValue == result.value}")
    }
}