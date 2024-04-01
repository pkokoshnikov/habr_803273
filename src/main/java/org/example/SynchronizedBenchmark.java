package org.example;

import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@State(Scope.Benchmark)
public class SynchronizedBenchmark {

    private ExecutorService ftp;
    private Map<String, String> sharedMap;

    @Setup
    public void setup() {
        sharedMap = new HashMap<>();
        ftp = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
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
     * # VM options: -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=60657:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
     * # Warmup: 5 iterations, 10 s each
     * # Measurement: 5 iterations, 10 s each
     * # Timeout: 10 min per iteration
     * # Threads: 1 thread, will synchronize iterations
     * # Benchmark mode: Average time, time/op
     * # Benchmark: org.example.SynchronizedBenchmark.fastTask
     * <p>
     * # Run progress: 0.00% complete, ETA 00:03:20
     * # Fork: 1 of 1
     * # Warmup Iteration   1: 47.480 ms/op
     * # Warmup Iteration   2: 46.987 ms/op
     * # Warmup Iteration   3: 46.928 ms/op
     * # Warmup Iteration   4: 46.216 ms/op
     * # Warmup Iteration   5: 46.159 ms/op
     * Iteration   1: 46.355 ms/op
     * Iteration   2: 46.239 ms/op
     * Iteration   3: 45.634 ms/op
     * Iteration   4: 45.990 ms/op
     * Iteration   5: 45.893 ms/op
     * <p>
     * <p>
     * Result "org.example.SynchronizedBenchmark.fastTask":
     * 46.022 ±(99.9%) 1.099 ms/op [Average]
     * (min, avg, max) = (45.634, 46.022, 46.355), stdev = 0.286
     * CI (99.9%): [44.923, 47.122] (assumes normal distribution)
     * Benchmark                       Mode  Cnt      Score      Error  Units
     * SynchronizedBenchmark.fastTask  avgt    5     46.022 ±    1.099  ms/op
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void fastTask() throws ExecutionException, InterruptedException {
        CompletableFuture.allOf(IntStream.range(0, 100_000).mapToObj(x -> CompletableFuture.runAsync(() -> {
            synchronized (sharedMap) {
                sharedMap.put(String.valueOf(x % 1000), RandomStringUtils.randomAlphabetic(10));
            }
        }, ftp)).toArray(CompletableFuture[]::new)).get();
    }

    /**
     * # JMH version: 1.23
     * # VM version: JDK 17.0.8, Java HotSpot(TM) 64-Bit Server VM, 17.0.8+9-LTS-211
     * # VM invoker: /Users/p.l.kokoshnikov/.sdkman/candidates/java/17.0.8-oracle/bin/java
     * # VM options: -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=60657:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
     * # Warmup: 5 iterations, 10 s each
     * # Measurement: 5 iterations, 10 s each
     * # Timeout: 10 min per iteration
     * # Threads: 1 thread, will synchronize iterations
     * # Benchmark mode: Average time, time/op
     * # Benchmark: org.example.SynchronizedBenchmark.longTask
     * <p>
     * # Run progress: 50.00% complete, ETA 00:01:41
     * # Fork: 1 of 1
     * # Warmup Iteration   1: 13327.754 ms/op
     * # Warmup Iteration   2: 12897.827 ms/op
     * # Warmup Iteration   3: 13875.043 ms/op
     * # Warmup Iteration   4: 13885.137 ms/op
     * # Warmup Iteration   5: 14400.243 ms/op
     * Iteration   1: 14451.678 ms/op
     * Iteration   2: 13578.904 ms/op
     * Iteration   3: 14542.732 ms/op
     * Iteration   4: 14481.361 ms/op
     * Iteration   5: 14769.419 ms/op
     * <p>
     * <p>
     * Result "org.example.SynchronizedBenchmark.longTask":
     * 14364.819 ±(99.9%) 1758.425 ms/op [Average]
     * (min, avg, max) = (13578.904, 14364.819, 14769.419), stdev = 456.657
     * CI (99.9%): [12606.394, 16123.244] (assumes normal distribution)
     * <p>
     * <p>
     * # Run complete. Total time: 00:04:02
     * <p>
     * REMEMBER: The numbers below are just data. To gain reusable insights, you need to follow up on
     * why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
     * experiments, perform baseline and negative tests that provide experimental control, make sure
     * the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
     * Do not assume the numbers tell you what you want them to tell.
     * <p>
     * Benchmark                       Mode  Cnt      Score      Error  Units
     * SynchronizedBenchmark.longTask  avgt    5  14364.819 ± 1758.425  ms/op
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void longTask() throws ExecutionException, InterruptedException {
        CompletableFuture.allOf(IntStream.range(0, 10_000).mapToObj(x -> CompletableFuture.runAsync(() -> {
            synchronized (sharedMap) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, ftp)).toArray(CompletableFuture[]::new)).get();
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(SynchronizedBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
