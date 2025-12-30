package com.thomaskioko.tvmaniac.benchmark.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.thomaskioko.tvmaniac.benchmark.DEFAULT_ITERATIONS
import com.thomaskioko.tvmaniac.benchmark.TARGET_PACKAGE
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class StartupBenchmarks {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startupCompilationModePartial() = startup(CompilationMode.Partial())

    @Test
    fun startupCompilationModeNone() = startup(CompilationMode.None())

    private fun startup(compilationMode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(StartupTimingMetric()),
        iterations = DEFAULT_ITERATIONS,
        compilationMode = compilationMode,
        startupMode = StartupMode.COLD,
    ) {
        pressHome()
        startActivityAndWait()

        // TODO:: Add core app interactions
    }
}
