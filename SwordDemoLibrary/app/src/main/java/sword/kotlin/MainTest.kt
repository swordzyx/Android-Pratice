package sword.kotlin

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import kotlin.random.Random
import kotlin.system.measureTimeMillis


fun main() {
    //flowSample4()
    //selectSample2()
    //synchronizedSample5()

    println(String.format("Unexpected char %#x at %d in %s value", 120, 1, "name"))
    
}

fun synchronizedSample5() = runBlocking { 
    var i = 0
    val jobs = mutableListOf<Job>()
    repeat(10) {
        val job = launch { 
            repeat(1000) {
                i++
                logx("i++, i = $i")
            }
        }
        jobs.add(job)
    }
    jobs.joinAll()
    logx("i = $i")
}

fun synchronizedSample4() = runBlocking {
    val deferreds = (1..10).map { 
        async { 
            var count = 0
            repeat(1000) {
                count ++
            }
            return@async count
        }
    }
    
    var result = 0
    deferreds.forEach { deferred -> 
        result += deferred.await() 
    }
    logx("result: $result")
}

sealed class Msg
object AddMsg: Msg()

class ResultMsg(val result: CompletableDeferred<Int>): Msg()
@OptIn(ObsoleteCoroutinesApi::class)
fun synchronizedSample3() = runBlocking { 
    val actor = actor<Msg> { 
        var count = 0
        for (msg in channel) {
            when (msg) {
                is AddMsg -> count++
                is ResultMsg -> msg.result.complete(count)
            }
        }
    }
    
    val jobs = mutableListOf<Job>()
    repeat(10) {
        val job = launch(Dispatchers.Default) {
            repeat(1000) {
                actor.send(AddMsg)
                logx("send AddMsg")
            }
        }
        jobs.add(job)
    }
    jobs.joinAll()
    
    val deferred = CompletableDeferred<Int>()
    actor.send(ResultMsg(deferred))
    logx("send ResultMsg")
    val result = deferred.await()
    actor.close()
    
    println("result: $result")
    
}

fun synchronizedSample2() = runBlocking {
    val mutex = Mutex()
    
    var i = 0
    val jobs = mutableListOf<Job>()
    
    repeat(10) {
        val job = launch(Dispatchers.Default) { 
            repeat(1000) {
                mutex.withLock {
                    i++
                }
            }
        }
        jobs.add(job)
    }
    println("i = $i")
}

fun synchronizedSample() = runBlocking { 
    
    var i = 0
    val lock = Any()
    
    val jobs = mutableListOf<Job>()
    
    repeat(10) {
        val job = launch(Dispatchers.Default) { 
            repeat(1000) {
                synchronized(lock) {
                    i++
                }
            }
        }
        jobs.add(job)
    }
    
    jobs.joinAll()
    println("i = $i")
}

@OptIn(ExperimentalCoroutinesApi::class)
fun selectSample2() = runBlocking { 
    val startTime = System.currentTimeMillis()
    val channel1 = produce {
        send("1")
        delay(200L)
        send("2")
        delay(200L)
        send("3")
        delay(150L)
    }
    
    val channel2 = produce { 
        delay(100L)
        send("a")
        delay(200L)
        send("b")
        delay(200L)
        send("c")
    }
    
    suspend fun selectChannel(channel1: ReceiveChannel<String>, channel2: ReceiveChannel<String>) = select {
        channel1.onReceive {
            logx("receive channel1: $it")
        }
        channel2.onReceive {
            logx("receive channel2: $it")
        }
    }
    
    repeat(6) {
        selectChannel(channel1, channel2)
    }
    channel1.cancel()
    channel2.cancel()
    
    println("Time cost: ${System.currentTimeMillis() - startTime}")
}

fun selectSample1() = runBlocking {
    suspend fun getCacheInfo(productId: String): Product? { 
        delay(200L)
        return Product(productId, 9.9)
    }
    
    suspend fun getNetworkInfo(productId: String): Product? {
        delay(100L)
        return Product(productId, 9.8)
    }
    
    fun updateUI(product: Product) {
        logx("${product.productId}==${product.price}")
    }
    
    val startTime = System.currentTimeMillis()
    val productId = "product_xxxxx"
    val product = select {
        async { 
            getCacheInfo(productId)
        }.onAwait {
            it
        }
        
        async {
            getNetworkInfo(productId)
        }.onAwait {
            it
        }
    }
    
    product?.let { 
        updateUI(it)
        logx("Time code: ${System.currentTimeMillis() - startTime}")
    }
}

data class Product(val productId: String, val price: Double)

fun flowSample4() = runBlocking { 
    flow {
        withContext(Dispatchers.IO) {
            emit(1)
        }
    }.map { it * 2 }.collect()
}

fun flowSample3() = runBlocking { 
    fun loadData () =  flow {
        repeat(3) {
            delay(1000)
            emit(it)
            logx("emit: $it")
        }
    }
    
    fun updateUI(newData: Int) { logx("update UI: $newData") }
    fun showLoading() { logx("showLoading") }
    fun hideLoading() { logx("hideLoading") }
    
    val uiScope = CoroutineScope(mySingleDispatcher)
    
    loadData()
        .onStart { showLoading() }
        .map { it * 2 }
        .flowOn(Dispatchers.IO)
        .catch { throwable -> 
            println(throwable)
            hideLoading()
            emit(-1)
        }
        .onEach { updateUI(it) }
        .onCompletion { hideLoading() }
        .launchIn(uiScope)
}

fun flowSample2() = runBlocking {
    val scope = CoroutineScope(mySingleDispatcher)
    flow {
        emit(1)
        logx("emit 1")
        emit(2)
        logx("emit 2")
        emit(3)
        logx("emit 3")
        emit(4)
        logx("emit 4")
    }.flowOn(Dispatchers.IO)
        .filter {
            logx("filter $it")
            it > 2
        }
        .onEach {
            logx("onEach $it")
        }
        .launchIn(scope)
    
    //flow.launchIn(scope) 等价于 scope.launch{ flow.collect() }
}

fun flowSample() = runBlocking {

    flow {
        emit(1)
        logx("emit 1")
        emit(2)
        logx("emit 2")
        emit(3)
        logx("emit 3")
        //emit(4)
        //throw IllegalStateException()
        //emit(5)
    }
        .filter {
            logx("filter $it")
            it > 2
        }
        .flowOn(Dispatchers.IO)
        .map {
            logx("map $it")
            it * 2
        }
        .catch { //捕获 catch 前面发生的异常
            logx("catch exception: $it")
        }
        .take(2)
        .onStart { //上游开始发数据前回调
            logx("Flow Start")
        }
        .onCompletion { //下游接收完所有的数据之后回调
            logx("Flow Completion, $it")
        }
        .collect {
            logx("collect: $it")
        }
    /*flowOf(1, 2, 3, 4, 5)
      .filter { it > 2 }
      .map {
        it * 2
      }
      .take(2)
      .onStart {
        logx("Flow Start")
      }
      .collect {
        logx("collect: $it")
      }*/
}

fun channelSample() = runBlocking {
    val channel = Channel<Int>(
        capacity = Channel.CONFLATED,
        onUndeliveredElement = {
            logx("onUndeliveredElement: $it")
        })

    val channel1 = produce {
        (1..3).forEach {
            send(it)
            logx("Send: $it")
        }
    }

    channel1.consumeEach {
        logx("Receive: $it")
    }

    logx("end")
}

val mySingleDispatcher = Executors.newSingleThreadExecutor {
    Thread(it, "MySingleThread").apply { isDaemon = true }
}.asCoroutineDispatcher()

@OptIn(ExperimentalStdlibApi::class)
fun coroutineContextSample() = runBlocking(mySingleDispatcher) {
    /*val user = getUserInfo(this)
    logx(user)*/

    val scope = CoroutineScope(Job() + mySingleDispatcher)
    scope.launch(CoroutineName("FirstCoroutine")) {
        logx(coroutineContext[CoroutineDispatcher] == mySingleDispatcher)
        delay(1000)
        logx("First end")
    }

    delay(500L)
    scope.cancel()
    delay(1000L)
}

suspend fun getUserInfo(coroutineScope: CoroutineScope): String {
    logx("Before IO Context")
    val result = withContext(Dispatchers.IO) {
        logx("In IO Context")
        delay(1000L)
        logx("after delay")
    }
    logx("After IO Context, Context result: $result")
    return "BoyCoder"
}

suspend fun coroutineByAsync(coroutineScope: CoroutineScope) {
    println("In runBlocking: ${Thread.currentThread().name}")

    val deferred: Deferred<String> = coroutineScope.async {
        println("In async: ${Thread.currentThread().name}")
        delay(1000)
        println("after delay")
        return@async "Async Task completed"
    }
    println("After async: ${Thread.currentThread().name}")
    //println("async result: ${deferred.await()}")
    delay(2000)
}

fun Job.log() {
    logx(
        """
  isActive=$isActive
  isCancelled=$isCancelled
  isCompleted=$isCompleted
  """.trimIndent()
    )
}

fun logx(any: Any?) {
    println("$any Thread: ${Thread.currentThread().name}")
}

private suspend fun coroutineAwaitSample(coroutineScope: CoroutineScope) {
    suspend fun getResult1(): String {
        delay(1000L)
        return "Result1"
    }

    suspend fun getResult2(): String {
        delay(1000L)
        return "Result2"
    }

    suspend fun getResult3(): String {
        delay(1000L)
        return "Result3"
    }

    var results: List<String>
    val time = measureTimeMillis {
        val result1 = coroutineScope.async { getResult1() }
        val result2 = coroutineScope.async { getResult2() }
        val result3 = coroutineScope.async { getResult3() }

        results = listOf(result1.await(), result2.await(), result3.await())
    }
    println("Time: $time")
    println("results: $results")
}

private suspend fun joinApiSample(coroutineScope: CoroutineScope) {
    suspend fun download() {
        val time = (Random.nextDouble() * 1000).toLong()
        logx("Delay time: $time")
        delay(time)
    }

    val job = coroutineScope.launch(start = CoroutineStart.LAZY) {
        logx("Coroutine start")
        download()
        logx("Coroutine End")
    }
    delay(500L)
    job.log()
    job.start()
    job.log()
    job.invokeOnCompletion {
        job.log()
    }
    job.join()
    logx("Process end")
}

fun coroutineByLaunch() {
    GlobalScope.launch(Dispatchers.IO) {
        println("Coroutine started: ${Thread.currentThread().name}")
    }
}
