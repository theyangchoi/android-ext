package com.yangchoi.ext

// arraylist的扩展函数

/**
 * 找出符合条件的项，并修改其中的某个值，返回新的ArrayList
 * @param predicate 条件
 * @param update 修改后的值
 * @return 新的ArrayList
 *
 * 示例:
 * list = list.update({ it == 3 }, { it + 1 })
 * 这个示例是找出list中的值为3的项，并将其值加1
 */
fun <T> ArrayList<T>.update(predicate: (T) -> Boolean, update: (T) -> T): ArrayList<T> {
    return this.apply {
        val index = indexOfFirst(predicate)
        if (index != -1) {
            set(index, update(get(index)))
        }
    }
}