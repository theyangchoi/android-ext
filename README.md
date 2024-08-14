## Android扩展方法

仅提供一些扩展方法



### 导入依赖

项目级`gradle`添加`https://jitpack.io`

```css
dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		mavenCentral()
		maven { url 'https://jitpack.io' }
	}
}
```

工程`gradle`添加

```css
dependencies {
	implementation 'com.github.theyangchoi:android-ext:1.0.1'
}
```



### ListExt

用一个用户对象作为示例

```kotlin
data class UserBean (
    var name:String,
    var age:Int
)

var userList = listOf<UserBean>()
```

- `addAll`：往一个list中添加一个list，并返回新的list

```kotlin
userList = userList.addAll(listOf(UserBean("yangchoi", 18), UserBean("yangchoi2", 18)))
```

- `add`：往一个list中添加一个item，并返回新的list
- `removeIndex`：删除对应index的item，并返回新的list
- `update`：找出符合条件的item，修改其中的某个值，并返回新的list

```kotlin
// it.name == "张三" 找出名称是张三的对象
// it.copy(name = "修改的张三") 将这个对象的名字改成:修改的张三
userList = userList.update({ it.name == "张三" }, { it.copy(name = "修改的张三") })
```

