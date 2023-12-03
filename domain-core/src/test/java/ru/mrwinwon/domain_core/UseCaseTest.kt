package ru.mrwinwon.domain_core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class UseCaseTest {
    private lateinit var exampleUseCase: ExampleUseCase
    @Before
    fun before() {
        exampleUseCase = TestUseCaseImpl()
    }
    @Test
    fun `test CheckAppHardUpdateUseCase with less then zero`() = runBlocking {
        val flow = exampleUseCase(SomeData(-1))

        flow.collect {
            it.onFailure { throwable ->
                Assert.assertEquals("Number should be above zero", throwable.message)
            }
        }
    }

    @Test
    fun `test CheckAppHardUpdateUseCase with 5`() = runBlocking {
        val flow = exampleUseCase(SomeData(5))

        flow.collect {
            it.onSuccess { result ->
                Assert.assertEquals(25, result)
            }
        }
    }

    @Test
    fun `test CheckAppHardUpdateUseCase`() = runBlocking {
        val flow = exampleUseCase(SomeData(10))

        flow.collect { result ->
            result.onSuccess { successResult ->
                Assert.assertEquals(100, successResult)
            }
            result.onFailure {
                Assert.assertEquals("Number should be above zero", it.message)
            }
        }
    }
}

interface ExampleUseCase : FlowUseCase<SomeData, Int>

class TestUseCaseImpl : ExampleUseCase {
    override fun execute(param: SomeData): Flow<Result<Int>> = flow {
        val number = param.someValue
        if (number > 0) {
            emit(Result.success(param.someValue * param.someValue))
        } else {
            emit(Result.failure(NumberFormatException("Number should be above zero")))
        }
    }
}

data class SomeData(val someValue: Int)