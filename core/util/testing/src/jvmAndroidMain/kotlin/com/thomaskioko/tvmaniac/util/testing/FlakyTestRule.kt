package com.thomaskioko.tvmaniac.util.testing

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.MultipleFailureException
import org.junit.runners.model.Statement

public class FlakyTestRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                val flakyAnnotation = description.getAnnotation(FlakyTests::class.java)
                val count = flakyAnnotation?.count ?: 1
                val failures = mutableListOf<Throwable>()

                for (i in 0 until count) {
                    try {
                        base.evaluate()
                    } catch (t: Throwable) {
                        failures.add(t)
                        println("Test ${description.methodName} failed on attempt ${i + 1}/$count: ${t.message}")
                    }
                }

                MultipleFailureException.assertEmpty(failures)
            }
        }
    }
}
