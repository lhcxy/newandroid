package cm.aiyouxi.live.baselibrary.base

import android.util.Log
import android.widget.TextView
import cm.aiyouxi.live.baselibrary.common.Constant
import cm.aiyouxi.live.baselibrary.common.RxCompose
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

class TestActivity : BaseActivity<TestPresenter>(), TestCon.TestV {
    override fun initData() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        mPresenter?.getViewData()
    }

    private lateinit var textView: TextView
    override val layoutId: Int
        get() = 0

    override fun providePresenter(): TestPresenter? {
        return TestPresenter(this)
    }


    override fun initView() {

        addSubscription(RxView.clicks(textView).compose(RxCompose.clickDurationST()).subscribe { o -> Log.e("sjdkl", "click$o") })
    }
}
