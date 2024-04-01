package org.example;

import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@State(Scope.Benchmark)
public class SingleThreadExecutorMonitorBenchmark {

    private ExecutorService ftp;
    private ExecutorService monitor;
    private Map<String, String> sharedMap;

    @Setup
    public void setup() {
        sharedMap = new HashMap<>();
        monitor = Executors.newSingleThreadExecutor();
        ftp = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @TearDown
    public void tearDown() {
        sharedMap.clear();
        ftp.shutdown();
        monitor.shutdown();
    }

    /**
     * # JMH version: 1.23
     * # VM version: JDK 17.0.8, Java HotSpot(TM) 64-Bit Server VM, 17.0.8+9-LTS-211
     * # VM invoker: /Users/p.l.kokoshnikov/.sdkman/candidates/java/17.0.8-oracle/bin/java
     * # VM options: -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=60360:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
     * # Warmup: 5 iterations, 10 s each
     * # Measurement: 5 iterations, 10 s each
     * # Timeout: 10 min per iteration
     * # Threads: 1 thread, will synchronize iterations
     * # Benchmark mode: Average time, time/op
     * # Benchmark: org.example.SingleThreadExecutorMonitorBenchmark.fastTask
     * <p>
     * # Run progress: 0.00% complete, ETA 00:03:20
     * # Fork: 1 of 1
     * # Warmup Iteration   1: 2187.815 ms/op
     * # Warmup Iteration   2: 2151.379 ms/op
     * # Warmup Iteration   3: 2149.262 ms/op
     * # Warmup Iteration   4: 2196.040 ms/op
     * # Warmup Iteration   5: 2155.831 ms/op
     * Iteration   1: 2179.545 ms/op
     * Iteration   2: 2195.192 ms/op
     * Iteration   3: 2142.782 ms/op
     * Iteration   4: 2168.577 ms/op
     * Iteration   5: 2310.353 ms/op
     * <p>
     * <p>
     * Result "org.example.SingleThreadExecutorMonitorBenchmark.fastTask":
     * 2199.290 ±(99.9%) 250.130 ms/op [Average]
     * (min, avg, max) = (2142.782, 2199.290, 2310.353), stdev = 64.958
     * CI (99.9%): [1949.160, 2449.420] (assumes normal distribution)
     * Benchmark                                      Mode  Cnt      Score      Error  Units
     * SingleThreadExecutorMonitorBenchmark.fastTask  avgt    5    223.419 ±   28.743  ms/op
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void fastTask() throws ExecutionException, InterruptedException {
        CompletableFuture.allOf(IntStream.range(0, 100_000).mapToObj(x -> CompletableFuture.runAsync(() -> {
            try {
                monitor.submit(() -> {
                    sharedMap.put(String.valueOf(x % 1000), RandomStringUtils.randomAlphabetic(10));
                }).get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, ftp)).toArray(CompletableFuture[]::new)).get();
    }


    /**
     * # JMH version: 1.23
     * # VM version: JDK 17.0.8, Java HotSpot(TM) 64-Bit Server VM, 17.0.8+9-LTS-211
     * # VM invoker: /Users/p.l.kokoshnikov/.sdkman/candidates/java/17.0.8-oracle/bin/java
     * # VM options: -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=60551:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
     * # Warmup: 5 iterations, 10 s each
     * # Measurement: 5 iterations, 10 s each
     * # Timeout: 10 min per iteration
     * # Threads: 1 thread, will synchronize iterations
     * # Benchmark mode: Average time, time/op
     * # Benchmark: org.example.SingleThreadExecutorMonitorBenchmark.longTask
     * <p>
     * # Run progress: 50.00% complete, ETA 00:01:42
     * # Fork: 1 of 1
     * # Warmup Iteration   1: 13541.004 ms/op
     * # Warmup Iteration   2: 14739.143 ms/op
     * # Warmup Iteration   3: 14710.052 ms/op
     * # Warmup Iteration   4: 14633.911 ms/op
     * # Warmup Iteration   5: 13367.187 ms/op
     * Iteration   1: 13009.014 ms/op
     * Iteration   2: 14539.586 ms/op
     * Iteration   3: 14277.045 ms/op
     * Iteration   4: 14682.038 ms/op
     * Iteration   5: 14981.292 ms/op
     * <p>
     * <p>
     * Result "org.example.SingleThreadExecutorMonitorBenchmark.longTask":
     * 14297.795 ±(99.9%) 2941.815 ms/op [Average]
     * (min, avg, max) = (13009.014, 14297.795, 14981.292), stdev = 763.980
     * CI (99.9%): [11355.980, 17239.610] (assumes normal distribution)
     * <p>
     * <p>
     * # Run complete. Total time: 00:04:05
     * <p>
     * REMEMBER: The numbers below are just data. To gain reusable insights, you need to follow up on
     * why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
     * experiments, perform baseline and negative tests that provide experimental control, make sure
     * the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
     * Do not assume the numbers tell you what you want them to tell.
     * <p>
     * Benchmark                                      Mode  Cnt      Score      Error  Units
     * SingleThreadExecutorMonitorBenchmark.longTask  avgt    5  14297.795 ± 2941.815  ms/op
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void longTask() throws ExecutionException, InterruptedException {
        CompletableFuture.allOf(IntStream.range(0, 10_000).mapToObj(x -> CompletableFuture.runAsync(() -> {
            try {
                monitor.submit(() -> {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, ftp)).toArray(CompletableFuture[]::new)).get();
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(SingleThreadExecutorMonitorBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
