package it.uniroma3.siw.festivalprof.controller.api;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.uniroma3.siw.festivalprof.service.FetchBenchmarkService;
import it.uniroma3.siw.festivalprof.service.FetchBenchmarkService.BenchmarkResult;

@RestController
@RequestMapping("/api/benchmark")
public class FetchBenchmarkRestController {

    private final FetchBenchmarkService fetchBenchmarkService;

    public FetchBenchmarkRestController(FetchBenchmarkService fetchBenchmarkService) {
        this.fetchBenchmarkService = fetchBenchmarkService;
    }

    @GetMapping
    public BenchmarkResult getBenchmarkResults(@RequestParam(value = "count", defaultValue = "50") int count) {
        return fetchBenchmarkService.runBenchmark(count);
    }

    @GetMapping("/curve")
    public List<BenchmarkResult> getCurveBenchmarkResults(
            @RequestParam(value = "steps", defaultValue = "20,40,60,80,100,150,200,300,400,500,750,1000,1500,2000") List<Integer> steps) {
        return fetchBenchmarkService.runCurveBenchmark(steps);
    }
}
