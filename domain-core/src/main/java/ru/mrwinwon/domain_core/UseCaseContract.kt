package ru.mrwinwon.domain_core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber

interface FlowUseCase<in Input, Output> {
    /**
     * Executes the flow on Dispatchers.IO by default and wraps exceptions inside it into Result
     */
    operator fun invoke(
        param: Input,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<Result<Output>> =
        execute(param)
            .catch { e -> emit(Result.failure(e)) }
            .flowOn(dispatcher)

    fun execute(param: Input): Flow<Result<Output>>
}

interface FlowOrDefaultValueUseCase<in Input, Output> {
    val useCaseDefaultValue: Output

    /**
     * Executes the flow on Dispatchers.IO by default and return default value in case of errors.
     */
    operator fun invoke(
        param: Input,
        defaultValue: Output = useCaseDefaultValue,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<Output> = execute(param)
        .catch { e ->
            Timber.e("emitting default value:$defaultValue, occurred exception: ${e.message}")
            emit(defaultValue)
        }
        .flowOn(dispatcher)

    fun execute(param: Input): Flow<Output>
}

interface SuspendedUseCase<in Input, Output> {
    suspend operator fun invoke(
        param: Input,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Output =
        withContext(dispatcher) { execute(param) }

    suspend fun execute(param: Input): Output
}