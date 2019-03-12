package cm.aiyouxi.live.baselibrary.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BasePresenter<V : IBaseConstract.IBaseView>(protected var mView: V?) : IBaseConstract.IBasePresenter {
    var disposable: CompositeDisposable? = null

    init {
        disposable = CompositeDisposable()
    }

    override fun onDetach() {
        if (disposable != null) {
            disposable!!.dispose()
            disposable = null
        }
        mView = null
    }

    fun addSubscription(dis: Disposable) {
        disposable?.add(dis)
    }
}
