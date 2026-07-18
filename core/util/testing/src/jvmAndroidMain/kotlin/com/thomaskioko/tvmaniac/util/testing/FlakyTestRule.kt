package com.thomaskioko.tvmaniac.util.testing

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

public class FlakyTestRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                val attempts = description.getAnnotation(FlakyTests::class.java)?.count ?: 1
                var lastFailure: Throwable? = null

                repeat(attempts) { attempt ->
                    try {
                        base.evaluate()
                        return
                    } catch (t: Throwable) {
                        lastFailure = t
                        println("Test ${description.methodName} failed on attempt ${attempt + 1}/$attempts: ${t.message}")
                    }
                }

                throw lastFailure!!
            }
        }
    }
}
