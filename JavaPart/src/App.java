import Logic.Benchmarking.Benchmark;
import Logic.Benchmarking.BenchmarkRunner;

public class App {
    
    public static void main(String[] args) throws Exception {
        Benchmark benchmark = new Benchmark();
        int numberOfRuns = 5;
        BenchmarkRunner runner = new BenchmarkRunner(benchmark, numberOfRuns);
        runner.runBenchmarks();
        runner.printSpeedupSummary();
        runner.exportResultsToCsvWithChooser();
    }

}
