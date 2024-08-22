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

#### addAll

- `addAll`：往一个list中添加一个list，并返回新的list

```kotlin
userList = userList.addAll(listOf(UserBean("yangchoi", 18), UserBean("yangchoi2", 18)))
```

#### add

- `add`：往一个list中添加一个item，并返回新的list

#### removeIndex

- `removeIndex`：删除对应index的item，并返回新的list

#### update

- `update`：找出符合条件的item，修改其中的某个值，并返回新的list

```kotlin
// it.name == "张三" 找出名称是张三的对象
// it.copy(name = "修改的张三") 将这个对象的名字改成:修改的张三
userList = userList.update({ it.name == "张三" }, { it.copy(name = "修改的张三") })
```



### ResultExt

返回结果扩展

#### andThen

1对1依赖，方法B依赖方法A的返回值。

示例场景：调用方法A上传用户头像，上传成功之后修改至本地数据。

定义2个方法，并使用Result返回：

```kotlin
fun uploadFile() : Result<String> {
    val random = Random.nextInt(1,7)
    Log.e("ResultTAG","uploadFile:$random")
    return if (random >= 3) {
        Result.success("url")
    } else {
        Result.failure(Exception("upload file failed"))
    }
}

fun updateUserInfo(url:String) : Result<Boolean> {
    val random = Random.nextInt(1,7)
    Log.e("ResultTAG","updateUserInfo:$random $url")
    return if (random >= 3) {
        Result.success(true)
    } else {
        Result.failure(Exception("update user info failed"))
    }
}
```

使用`andThen`关联2个方法

```kotlin
uploadFile()
    .andThen { updateUserInfo(it) }
    .onSuccess { Log.e("ResultTAG","个人信息修改成功") }
    .onFailure { Log.e("ResultTAG","catch:${it.message}") }
```

当`uploadFile`执行结果为true，并返回url时，`andThen`会触发然后执行`updateUserInfo(it)`方法

当2个方法的执行结果都是true时，触发onSuccess

只要有某个方法执行结果为false就会触发onFailure

成功示例日志：

```
uploadFile:4
updateUserInfo:6 url
个人信息修改成功
```

失败示例日志：

```
uploadFile:2 // 上传头像执行失败，直接就触发onFailure
catch:upload file failed
```



#### dispatch

一对多依赖简单说就是，方法B 和 方法C 依赖方法A的返回值。



示例：

获取用户的总积分。

比如平台X的积分分为可用积分和不可用积分，并且是通过不同接口去查询的，但是都要用到手机号，所以要先获取到手机号，然后再去获取积分，最后对2种积分进行求和。



定义三个方法：

```kotlin
// 获取用户手机号
fun getUserPhone(): Result<String> {
    val random = Random.nextInt(2,7)
    Log.e("ResultTAG","getUserPhone:$random")
    return if (random >= 3) {
        Result.success("phone")
    } else {
        Result.failure(Exception("获取用户手机号失败"))
    }
}

// 通过手机号查询用户可用积分
fun getAvailablePoints(phone:String): Result<Int> {
    val random = Random.nextInt(2,7)
    Log.e("ResultTAG","getAvailablePoints:$random $phone")
    return if (random >= 3) {
        Result.success(random)
    } else {
        Result.failure(Exception("获取用户可用积分失败"))
    }
}

// 通过手机号查询用户不可用积分
fun getUnAvailablePoints(phone:String): Result<Int> {
    val random = Random.nextInt(2,7)
    Log.e("ResultTAG","getUnAvailablePoints:$random $phone")
    return if (random >= 3) {
        Result.success(random)
    } else {
        Result.failure(Exception("获取用户不可用积分失败"))
    }
}
```

通过dispatch关联三个方法

```kotlin
getUserPhone()
    .dispatch {
        val availablePoints = getAvailablePoints(it).getOrThrow()
        val unAvailablePoints = getUnAvailablePoints(it).getOrThrow()
        availablePoints + unAvailablePoints
    }
    .onSuccess {
        Log.e("ResultTAG","积分总额:$it")
    }
    .onFailure {
        Log.e("ResultTAG","查询失败:${it.message}")
    }
```

<!--`availablePoints + unAvailablePoints` ：这个返回值可以自定义，demo中是返回的int，所以在onSuccess中it就是int类型，如果是`"${availablePoints + unAvailablePoints}"`那么在onSuccess中就是String类型。-->



成功示例日志：

```
demo1:
getUserPhone:6
getAvailablePoints:3 phone
getUnAvailablePoints:3 phone
积分总额:6

demo2:
getUserPhone:3
getAvailablePoints:4 phone
getUnAvailablePoints:3 phone
积分总额:7
```



失败示例日志：

```
demo1:
getUserPhone:4
getAvailablePoints:2 phone
查询失败:获取用户可用积分失败

demo2:
getUserPhone:2
查询失败:获取用户手机号失败
```



#### zip&andThen

多对一依赖与一对多依赖正好相反，它是指方法C依赖于方法A和方法B的返回值。

示例：

获取用户X在设备X上的登录日志。

比如平台X在数据库存有设备的登录日志，现在需要查看这个日志就需要2个参数，用户手机和设备id。



定义三个方法：

```kotlin
// 获取用户手机号
fun getUserPhone(): Result<String> {
    val random = Random.nextInt(2,7)
    Log.e("ResultTAG","getUserPhone:$random")
    return if (random >= 3) {
        Result.success("phone")
    } else {
        Result.failure(Exception("获取用户手机号失败"))
    }
}

// 获取设备id
fun getDeviceId(): Result<String> {
    val random = Random.nextInt(2,7)
    Log.e("ResultTAG","getDeviceId:$random")
    return if (random >= 3) {
        Result.success("id-1111")
    } else {
        Result.failure(Exception("获取设备id失败"))
    }
}

// 获取设备登录日志
fun getLoginLog(phone:String,deviceId:String): Result<Int> {
    val random = Random.nextInt(2,7)
    Log.e("ResultTAG","getLoginLog:$random $phone $deviceId")
    return if (random >= 3) {
        Result.success(random)
    } else {
        Result.failure(Exception("获取设备登录日志失败"))
    }
}
```



通过`zip`和`andThen`关联三个方法：

```
zip {
    val phone = getUserPhone().getOrThrow()
    val deviceId = getDeviceId().getOrThrow()
    mapOf("phone" to phone, "deviceId" to deviceId)
}
.andThen {
    getLoginLog(it["phone"]!!, it["deviceId"]!!)
}
.onSuccess {
    Log.e("ResultTAG","执行成功:$it")
}
.onFailure {
    Log.e("ResultTAG","执行失败:${it.message}")
}
```

<!--`mapOf("phone" to phone, "deviceId" to deviceId)`是返回给`andThen`的数据类型，可以自己定义，这里是返回的map，支持所有数据类型。-->



成功示例日志：

```
getUserPhone:4
getDeviceId:4
getLoginLog:5 phone id-1111
执行成功:5
```



失败示例日志:

```
getUserPhone:5
getDeviceId:6
getLoginLog:2 phone id-1111
执行失败:获取设备登录日志失败
```



#### or

在多个方法中选择一个成功的。

示例：

用户在X平台绑定了三张银行卡，分别是工商、农行、建行，现在发起支付的时候会依次执行工商扣款如果失败就执行工行扣款，如果农行还失败就建行扣款，如果三个银行都扣款失败就是支付失败，只要有某个成功就是支付成功。

定义三个方法：

```kotlin
// 工商银行支付
fun payICBC(): Result<String> {
    val random = Random.nextInt(2,7)
    Log.e("ResultTAG","payICBC:$random")
    return if (random >= 5) {
        Result.success("success")
    } else {
        Result.failure(Exception("工商银行支付失败"))
    }
}

// 农业银行支付
fun payABC(): Result<String> {
    val random = Random.nextInt(2,7)
    Log.e("ResultTAG","paypayABCICBC:$random")
    return if (random >= 5) {
        Result.success("success")
    } else {
        Result.failure(Exception("农业银行支付失败"))
    }
}

// 建设银行支付
fun payCCB(): Result<String> {
    val random = Random.nextInt(1,7)
    Log.e("ResultTAG","payCCB:$random")
    return if (random >= 5) {
        Result.success("success")
    } else {
        Result.failure(Exception("建设银行支付失败"))
    }
}
```

使用or关联起来：

```kotlin
payICBC().or(payABC()).or(payICBC())
                    .onSuccess { Log.e("ResultTAG","支付成功") }
                    .onFailure { Log.e("ResultTAG","支付失败:${it.message}") }
```



成功示例日志：

```
payICBC:3
paypayABCICBC:5
payCCB:2
支付成功
```



失败示例日志:

```
payICBC:3
paypayABCICBC:4
payCCB:2
支付失败:建设银行支付失败
```

