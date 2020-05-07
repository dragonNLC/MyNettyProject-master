package com.aptdev.common.ui.activity.base;

import android.support.v4.app.FragmentManager;

import com.aptdev.common.R;
import com.aptdev.common.contracts.presenter.base.BasePresenter;
import com.aptdev.common.contracts.view.base.BaseView;
import com.aptdev.common.ui.fragment.base.BaseFragment;

/**
 * Created by lb on 2019/1/4.
 */

public abstract class BaseFragmentActivity<V extends BaseView, T extends BasePresenter> extends BaseActivity<V, T> {

    public void replaceFragment(BaseFragment baseFragment, int fragmentLayoutId) {
        if (baseFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.anim_scale_inside_ceneter, R.anim.anim_translate_outside_left, R.anim.anim_translate_inside_right, R.anim.anim_scale_outside_center)
                    .replace(fragmentLayoutId, baseFragment,baseFragment.getClass().getSimpleName())
                    .addToBackStack(baseFragment.getClass().getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    public void addFragment(BaseFragment baseFragment, int fragmentLayoutId) {
        if (baseFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.anim_scale_inside_ceneter, R.anim.anim_translate_outside_left, R.anim.anim_translate_inside_right, R.anim.anim_scale_outside_center)
                    .add(fragmentLayoutId, baseFragment,baseFragment.getClass().getSimpleName())
                    .addToBackStack(baseFragment.getClass().getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    public boolean removeOneFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
            return true;
        }
        return false;
    }

    public void removeAllFragment() {
        removeTagPopSelfFragment(null);
    }


    public boolean removeTagPopSelfFragment(String tagName) {
        return removeFragment(tagName, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public boolean removeTagPopFragment(String tagName) {
        return removeFragment(tagName, 0);
    }

    private boolean removeFragment(String tagName, int actType) {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate(tagName, actType);
            return true;
        }
        return false;
    }

}
