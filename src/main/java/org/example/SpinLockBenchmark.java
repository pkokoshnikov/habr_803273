package org.example;

import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

@State(Scope.Benchmark)
public class SpinLockBenchmark {

    private ExecutorService ftp;
    private Map<String, String> sharedMap;
    private ReentrantLock reentrantLock;

    @Setup
    public void setup() {
        sharedMap = new HashMap<>();
        ftp = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        reentrantLock = new ReentrantLock();
    }

    @TearDown
    public void tearDown() {
        sharedMap.clear();
        ftp.shutdown();
    }

    /**
     * # JMH version: 1.23
     * # VM version: JDK 17.0.8, Java HotSpot(TM) 64-Bit Server VM, 17.0.8+9-LTS-211
     * # VM invoker: /Users/p.l.kokoshnikov/.sdkman/candidates/java/17.0.8-oracle/bin/java
     * # VM options: -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=60620:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
     * # Warmup: 5 iterations, 10 s each
     * # Measurement: 5 iterations, 10 s each
     * # Timeout: 10 min per iteration
     * # Threads: 1 thread, will synchronize iterations
     * # Benchmark mode: Average time, time/op
     * # Benchmark: org.example.SpinLockBenchmark.fastTask
     * <p>
     * # Run progress: 0.00% complete, ETA 00:01:40
     * # Fork: 1 of 1
     * # Warmup Iteration   1: 43.396 ms/op
     * # Warmup Iteration   2: 42.307 ms/op
     * # Warmup Iteration   3: 42.056 ms/op
     * # Warmup Iteration   4: 42.405 ms/op
     * # Warmup Iteration   5: 42.739 ms/op
     * Iteration   1: 41.536 ms/op
     * Iteration   2: 41.339 ms/op
     * Iteration   3: 41.773 ms/op
     * Iteration   4: 41.512 ms/op
     * Iteration   5: 42.320 ms/op
     * <p>
     * <p>
     * Result "org.example.SpinLockBenchmark.fastTask":
     * 41.696 ±(99.9%) 1.469 ms/op [Average]
     * (min, avg, max) = (41.339, 41.696, 42.320), stdev = 0.382
     * CI (99.9%): [40.227, 43.165] (assumes normal distribution)
     * <p>
     * <p>
     * # Run complete. Total time: 00:01:41
     * <p>
     * REMEMBER: The numbers below are just data. To gain reusable insights, you need to follow up on
     * why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
     * experiments, perform baseline and negative tests that provide experimental control, make sure
     * the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
     * Do not assume the numbers tell you what you want them to tell.
     * <p>
     * Benchmark                   Mode  Cnt   Score   Error  Units
     * SpinLockBenchmark.fastTask  avgt    5  41.696 ± 1.469  ms/op
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void fastTask() throws ExecutionException, InterruptedException {
        CompletableFuture.allOf(IntStream.range(0, 100_000).mapToObj(x -> CompletableFuture.runAsync(() -> {
            reentrantLock.lock();
            try {
                sharedMap.put(String.valueOf(x % 1000), RandomStringUtils.randomAlphabetic(10));
            } finally {
                reentrantLock.unlock();
            }
        }, ftp)).toArray(CompletableFuture[]::new)).get();
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(SpinLockBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
