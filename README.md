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



特别感谢：该架构主要来自于[https://github.com/yuanhoujun/] 大佬提供的参考项目，本人进行抽取修改后并根据个人喜好进行了适当的修改。
