# 介绍
一个 Android 应用的基础 MVP 架构，主要用于快速搭建好一个应用的大体框架。相比于传统的 MVP 架构，摒弃了个人不太喜欢的 Dagger2，而是采用反射去去创建
相应的 Presenter 以及 View 对象。
该架构对网络/UI等相关模块进行了极度的简化，加上Kotlin语言的加持，让开发者可以专注于业务逻辑，而无需分心于一些繁琐线程管理工作，如ToolBar的管理，
Fragment的管理等。

框架中主要使用了以下第三方库：
Retrofit: [https://github.com/square/retrofit](https://github.com/square/retrofit)

BaseRecyclerViewAdapterHelper： [https://github.com/CymChad/BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)

Glide: [https://github.com/bumptech/glide](https://github.com/bumptech/glide)

RxJava: [https://github.com/ReactiveX/RxJava](https://github.com/ReactiveX/RxJava)

RxAndroid: [https://github.com/ReactiveX/RxAndroid](https://github.com/ReactiveX/RxAndroid)

AnkoExt: [https://github.com/yuanhoujun/AnkoExt](https://github.com/yuanhoujun/AnkoExt)

# 如何使用这个框架？
### UI
#### 页面跳转
```
// 最简单的方式
push(LoginFragment::class)

// 跳转到指定页面并传值
push(OrderFragment::class, "id" to "20170820xxxx", "amount" to 3000)

// 从页面快速获取页面传值
data {
    val id = getString("id")
    val amount = getInt("amount")
}

```

### QA
a）如何实现类似Activity的startActivityForResult？

```
// 使用方法与Activity完全一致
private fun initListener() {
    btn_confirm.setOnClickListener {
        val value = edit_value.text.toString()
        if(!TextUtils.isEmpty(value)) {
            startFragmentForResult(fragment = B1Fragment::class,
                                   requestCode = REQUEST_CODE,
                                   data = KEY_PASS_VALUE to value)
        }
    }
}

override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?) {
    super.onFragmentResult(requestCode, resultCode, data)

    if(REQUEST_CODE == requestCode) {
        if(RESULT_OK == resultCode) {
            val value = data?.getString(B1Fragment.KEY_RETURN_VALUE)
            if(!TextUtils.isEmpty(value)) {
                text_return_value.text = "收到下一个返回值：$value"
            }
        }
    }
}

// 目标页面处理方式
val returnValue = edit_value.text.toString()
if(!TextUtils.isEmpty(returnValue)) {
    val data = Bundle()
    data.putString(KEY_RETURN_VALUE, returnValue)
    setResult(RESULT_OK, data)
    finish()
}

```

b）如何处理页面回退？

```
// 回退到上级页面
pop()

// 回退到指定页面
popToFragment(TargetFragment::class)

// 回退后，目标页面以下方法将会被调用
open fun onBackCompleted(from: BaseFragment? = null)

```

#### 页面基础UI样式设置
这里推荐使用注解方式设置，简化不必要的样式设置代码!

a）自定义Toolbar

```
// 不需要Toolbar
@Toolbar(mode = ToolbarMode.NONE)

// 自定义Toolbar
@Toolbar(mode = ToolbarMode.CUSTOM, entityClass = DefaultWebToolbar::class)

// 设置Toolbar基本样式
@ToolbarSetting(titleColor = "#ffffffff",
                backgroundColor = "#ff52b8f9",
                navigationDrawable = R.drawable.background_button_navi_blue)
```

**Note: 框架支持使用注解直接指定Fragment布局（目前只支持Fragment）**

```
@BindView(R.layout.fragment_network)
open class LoginFragment : QuickFragment<Contract.LoginView, LoginPresenterImpl>(), Contract.LoginView {

```

### QA
a）在某些特殊情况下，Fragment需要自己处理回退事件，怎么办？
不用担心，框架已经为你考虑了这种情况，依然是使用一个简单的注解就可以解决这个问题！具体使用方法如下：

```
// 拦截回退事件(是否拦截回退事件，由注解参数指定,默认不拦截)
// 参数设置为true后，页面回退时将自动调用页面的onBackPressed()方法
@InterceptBackPressed(true)
class LoanSubmitFragment : BaseFragment() 

```

### Network
a）如何使用网络框架？

网络部分使用了当前主流的网络框架Retrofit，并进行了简单的封装，实现了如下目标：

1）页面销毁时请求自动取消

2）统一错误处理（也可以自己处理）

使用方法如下：

```
// 1）先定义Service接口
@FormUrlEncoded
@POST("/v1.0/app/customer/login")
fun login(@Field("mobile") phone: String,
          @Field("pwd") pwd: String?,
          @Field("lon") lon: String? = null,
          @Field("lat") lat: String? = null): QuickTask<HttpResponse<User>>

// 2）使用lambda表达式监听请求状态（这里具体回调的意思，请参照QuickTask源码）
val retrofit = RetrofitHelper.getDefault()
val userService = retrofit.create(UserService::class.java)
userService.login(username, EnCryptUtil.md5(pwd)).bindView(view).onStart {
    view?.onRequestStarted()
}.onSuccess { _ , response ->
    view?.loginSuccess(response?.data)
}.onComplete {
    view?.onRequestCompleted()
}.onError { _ , _ , bizCode, error ->

}.doIt()
```

注意：这里第二步获取Executor对象有两个接口<code>bindView</code>和<code>executor</code>, 这两个接口是有本质区别的！

<code>bindView</code>: 使用这个接口将自动完成Http请求与视图层的绑定，它将默认使用页面基类处理网络错误。当然也可以选择自己手动处理网络错误。同时，这个接口还会在视图销毁的时候自动取消当前Http请求。

<code>executor</code>：这个接口与视图无关，仅仅用于发起Http请求，必要的时候需要手动取消Http请求！

### 如何取舍
因此，如果你是构建MVP页面，推荐使用<code>bindView</code>, 而如果你仅仅只是发起Http请求，请使用<code>executor</code>接口

b）如果要自己封装网络处理，要怎么做呢？
QuickTask就是一个简单的例子，继承AbsTask做自己的实现就可以了！

### MVP
在MVP的构建方面，框架层也提供了一些简单的方法。目前，可以做到：

1）自动创建Presenter

2）自动关联视图（需要实现一个方法）

具体步骤如下：

i）设置接口约定类Contract
```
/**
 * Contract
 *
 * @author Scott Smith 2017-08-22 16:05
 */
class Contract {
    interface LoginView: BaseView {
        /**
         * 登录成功
         */
        fun loginSuccess(user: User?)

        /**
         * 登录失败
         *
         * @param code    错误码
         * @param message 错误描述
         */
        fun loginErr(code: String? , message: String?)
    }

    abstract class LoginPresenter: BasePresenter<LoginView>() {
        /**
         * 登录接口
         *
         * @param username 用户名
         * @param pwd   用户密码
         */
        abstract fun login(username: String, pwd: String)
    }
}
```

ii）实现Presenter逻辑

iii）继承QuickFragment，类体增加泛型标记
```
open class LoginFragment : QuickFragment<Contract.LoginView, LoginPresenterImpl>(), Contract.LoginView 
```

通过上述3个步骤，Presenter可以实现自动创建，具体视图关联重写createPresenterView()返回View接口即可。


# Best Practice
a）推荐继承QuickFragment类，实现onInitView、onInitListener接口，完成视图设置以及事件绑定。

b）使用Contract定义Presenter和视图接口

c）Http接口分模块存放（定义不同的Service接口）

# 如何提交代码？
Fork这个版本库 -> 修改代码提交到自己的版本库，推送Pull Request -> 合并/拒绝

# 代码提交须知
1）必须遵守【[编程规范](http://git.oschina.net/ouyangfeng/YaoJiFinancialMgr/blob/NG/programming_specification.md)】

2）必须使用Kotlin语言开发

3）必须严格遵守MVP编程规范

4）每次推送的PR应该尽量简化，确保：一个idea，一次提交

# PR可能被拒绝的情况
1）没有遵守**代码提交须知**规范

2）代码中出现明显的逻辑错误

3）代码不能正常运行

4）代码中包含错误的英文单词或者注释不规范

# 其它
为了简化开发，框架层提供了一下简化日常操作的API。目前，主要包括：

1）WebView快速注册自定义前端协议

```
smart_web_view.registerScheme("yjlc", "buy") { scheme, action, params ->
        val data = params["id"]
        KLog.d("data=$data")
        if(!TextUtils.isEmpty(data)) {
            try {
                mTargetId = URLDecoder.decode(data, "UTF-8")
                guideToNext(mTargetId)
            } catch(e: Exception) {
            }
        }
    }
```

2）从相册选择图片
在Activity或者Fragment子类中调用selectPicture()方法，选择图片后返回，会自动调用OnPictureSelectListener接口
方法。为了处理回调，页面需要实现该接口。

3）如何直接跳转至Fragment？
由于页面全部使用Fragment构建，有时候需要直接跳转至某个Fragment。这个时候可以借助**RouterManager**接口处理跳转


