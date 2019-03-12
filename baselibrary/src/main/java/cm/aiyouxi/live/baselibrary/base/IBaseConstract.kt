package cm.aiyouxi.live.baselibrary.base

import android.support.annotation.StringRes

interface IBaseConstract {
    interface IBasePresenter {
        fun onDetach()
    }

    interface IBaseView {

        fun showLoading()

        fun showEmpty()

        fun showContent()

        fun showRetryView()

        fun showToast(msg: String)

        fun showToast(@StringRes strId: Int)
    }
}
