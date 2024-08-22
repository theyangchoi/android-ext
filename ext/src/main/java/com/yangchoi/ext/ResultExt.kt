package com.yangchoi.ext

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class InnerException(message: String = "inner value error"): Exception(message)

// 一对一依赖简单得说就是，方法B依赖方法A的返回值。
@OptIn(ExperimentalContracts::class)
public inline fun <V, E> Result<V>.andThen(transform: (V) -> Result<E>): Result<E> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    if (isSuccess) {
        val value = getOrNull() ?: return Result.failure(InnerException())
        return transform(value)
    } else {
        val exception = exceptionOrNull() ?: return Result.failure(InnerException())
        return Result.failure(exception)
    }
}

// 一对多依赖简单说就是，方法B 和 方法C 依赖方法A的返回值。
@OptIn(ExperimentalContracts::class)
public inline fun <V, E> Result<V>.dispatch(transform: (V) -> E): Result<E> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    if (isSuccess) {
        val value = getOrNull() ?: return Result.failure(InnerException())
        return kotlin.runCatching {
            transform(value)
        }
    } else {
        val exception = exceptionOrNull() ?: return Result.failure(InnerException())
        return Result.failure(exception)
    }
}

// 多对一依赖与一对多依赖正好相反，它是指方法C依赖于方法A和方法B的返回值。
@OptIn(ExperimentalContracts::class)
public inline fun <V> zip(block: () -> V): Result<V> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return runCatching {
        block()
    }
}

// 多选一
public fun <V> Result<V>.or(result: Result<V>): Result<V> {
    return when {
        isSuccess -> this
        else -> result
    }
}

// 集合
public fun <V, R : Result<V>> valuesOf(results: List<R>): List<V> {
    return results.asIterable().filterValues()
}

public fun <V> Iterable<Result<V>>.filterValues(): List<V> {
    return filterValuesTo(ArrayList())
}

public fun <V, C : MutableCollection<in V>> Iterable<Result<V>>.filterValuesTo(destination: C): C {
    for (element in this) {
        if (element.isSuccess) {
            val value = element.getOrNull() ?: continue
            destination.add(value)
        }
    }
    return destination
}

public fun <V> Iterable<Result<V>>.allSuccess(): Boolean {
    return all(Result<V>::isSuccess)
}
