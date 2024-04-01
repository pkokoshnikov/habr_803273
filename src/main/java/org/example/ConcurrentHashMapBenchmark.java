package org.example;

import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@State(Scope.Benchmark)
public class ConcurrentHashMapBenchmark {

    private ExecutorService ftp;
    private Map<String, String> sharedMap;

    @Setup
    public void setup() {
        sharedMap = new ConcurrentHashMap<>();
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
     * # VM options: -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=60514:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
     * # Warmup: 5 iterations, 10 s each
     * # Measurement: 5 iterations, 10 s each
     * # Timeout: 10 min per iteration
     * # Threads: 1 thread, will synchronize iterations
     * # Benchmark mode: Average time, time/op
     * # Benchmark: org.example.ConcurrentHashMapBenchmark.fastTask
     * <p>
     * # Run progress: 0.00% complete, ETA 00:01:40
     * # Fork: 1 of 1
     * # Warmup Iteration   1: 35.386 ms/op
     * # Warmup Iteration   2: 35.205 ms/op
     * # Warmup Iteration   3: 36.344 ms/op
     * # Warmup Iteration   4: 35.972 ms/op
     * # Warmup Iteration   5: 34.384 ms/op
     * Iteration   1: 34.292 ms/op
     * Iteration   2: 33.044 ms/op
     * Iteration   3: 32.763 ms/op
     * Iteration   4: 34.196 ms/op
     * Iteration   5: 35.029 ms/op
     * <p>
     * <p>
     * Result "org.example.ConcurrentHashMapBenchmark.fastTask":
     * 33.865 ±(99.9%) 3.619 ms/op [Average]
     * (min, avg, max) = (32.763, 33.865, 35.029), stdev = 0.940
     * CI (99.9%): [30.246, 37.484] (assumes normal distribution)
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
     * Benchmark                            Mode  Cnt   Score   Error  Units
     * ConcurrentHashMapBenchmark.fastTask  avgt    5  33.865 ± 3.619  ms/op
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void fastTask() throws ExecutionException, InterruptedException {
        CompletableFuture.allOf(IntStream.range(0, 100_000).mapToObj(x -> CompletableFuture.runAsync(() ->
                        sharedMap.put(String.valueOf(x % 1000), RandomStringUtils.randomAlphabetic(10)),
                ftp)).toArray(CompletableFuture[]::new)).get();
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(ConcurrentHashMapBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
