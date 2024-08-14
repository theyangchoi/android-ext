package com.yangchoi.ext

// list的扩展函数

/**
 * 往当前list中添加一个list，并返回一个新的list
 * @param list 要添加的list
 * @return 新的list
 *
 * 示例：
 * var list = listOf(1, 2, 3)
 * list = list.addAll(listOf(4, 5, 6))
 * println(list)
 */
fun <T> List<T>.addAll(list: List<T>):List<T> {
    return this.toMutableList().apply {
        addAll(list)
    }
}

/**
 * 往当前list中添加一个项，并返回新的list
 * @param item 要添加的项
 * @return 新的list
 *
 * 示例：
 * var list = listOf(1, 2, 3)
 * list = list.add(4)
 * println(list)
 */
fun <T> List<T>.add(item: T): List<T> {
    return this.toMutableList().apply {
        add(item)
    }
}

/**
 * 删除当前list中指定索引的项，并返回新的list
 * @param index 要删除的索引
 * @return 新的list
 *
 * 示例：
 * list = list.removeIndex(2)
 * println(list)
 */
fun <T> List<T>.removeIndex(index: Int): List<T> {
    return this.toMutableList().apply {
        removeAt(index)
    }
}

/**
 * 找出符合条件的项，并修改其中的某个值，返回新的list
 * @param predicate 条件
 * @param update 修改后的值
 * @return 新的list
 *
 * 示例:
 * list = list.update({ it == 3 }, { it + 1 })
 * 这个示例是找出list中的值为3的项，并将其值加1
 */
fun <T> List<T>.update(predicate: (T) -> Boolean, update: (T) -> T): List<T> {
    return this.toMutableList().apply {
        val index = indexOfFirst(predicate)
        if (index != -1) {
            set(index, update(get(index)))
        }
    }
}

// 将list转成ArrayList
fun <T> List<T>.toArrayList(): ArrayList<T> {
    return ArrayList(this)
}