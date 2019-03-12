package cm.aiyouxi.live.baselibrary.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import cm.aiyouxi.live.baselibrary.common.Constant
import cm.aiyouxi.live.baselibrary.common.RxCompose
import cm.aiyouxi.live.baselibrary.common.toast

import com.github.nukc.stateview.StateView
import com.jakewharton.rxbinding2.view.clicks

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

abstract class BaseActivity<P : IBaseConstract.IBasePresenter> : AppCompatActivity(), IBaseConstract.IBaseView {
    private var toast: Toast? = null
    lateinit var stateView: StateView
    var mPresenter: P? = null
    private var disposable: CompositeDisposable? = null

    @get:LayoutRes
    protected abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        stateView = StateView.inject(this)
        toast = Toast(this)
        toast!!.duration = Toast.LENGTH_SHORT
        mPresenter = providePresenter()
        disposable = CompositeDisposable()
        initView()
        initData()

    }

    protected abstract fun initData()

    protected abstract fun initView()

    protected abstract fun providePresenter(): P?
    lateinit var tx: TextView
    override fun showLoading() {

    }

    override fun showEmpty() {

    }

    override fun showContent() {

    }

    override fun showRetryView() {

    }

    override fun showToast(msg: String) {
        toast(msg)
    }

    override fun showToast(strId: Int) {
        toast(strId)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mPresenter != null) {
            mPresenter!!.onDetach()
            mPresenter = null
        }
    }

    fun addSubscription(dis: Disposable) {
        disposable?.add(dis)
    }

    fun clicks(view: Disposable) {
        disposable?.add(view)
    }

//         clicks(tx.clicks().compose(RxCompose.clickDurationST()).subscribe())

    //    abstract @LayoutRes
    //    int getLayoutId();

}
