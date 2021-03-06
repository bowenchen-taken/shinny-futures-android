package com.shinnytech.futures.controller.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shinnytech.futures.R;
import com.shinnytech.futures.application.BaseApplication;
import com.shinnytech.futures.controller.activity.MainActivity;
import com.shinnytech.futures.databinding.FragmentQuotePagerBinding;
import com.shinnytech.futures.model.adapter.ViewPagerFragmentAdapter;
import com.shinnytech.futures.model.engine.DataManager;
import com.shinnytech.futures.model.engine.LatestFileManager;
import com.shinnytech.futures.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

import static com.shinnytech.futures.constants.CommonConstants.CONFIG_RECOMMEND_OPTIONAL;
import static com.shinnytech.futures.constants.CommonConstants.DALIAN;
import static com.shinnytech.futures.constants.CommonConstants.DALIANZUHE;
import static com.shinnytech.futures.constants.CommonConstants.DOMINANT;
import static com.shinnytech.futures.constants.CommonConstants.NENGYUAN;
import static com.shinnytech.futures.constants.CommonConstants.OPTIONAL;
import static com.shinnytech.futures.constants.CommonConstants.SHANGHAI;
import static com.shinnytech.futures.constants.CommonConstants.ZHENGZHOU;
import static com.shinnytech.futures.constants.CommonConstants.ZHENGZHOUZUHE;
import static com.shinnytech.futures.constants.CommonConstants.ZHONGJIN;


public class QuotePagerFragment extends LazyLoadFragment {

    private FragmentQuotePagerBinding mBinding;
    private ViewPagerFragmentAdapter mViewPagerFragmentAdapter;
    private List<String> mTitleList;
    private MainActivity mMainActivity;
    private String mTitle;
    private boolean mIsInit = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void show() {
        try {
            if (!mIsInit) {
                ((QuoteFragment) mViewPagerFragmentAdapter.getItem(mBinding.quotePager.getCurrentItem())).show();
            }
            mIsInit = false;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void leave() {
        try {
            if (!mIsInit) {
                ((QuoteFragment) mViewPagerFragmentAdapter.getItem(mBinding.quotePager.getCurrentItem())).leave();
            }
            mIsInit = false;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_quote_pager, container, false);
        initData();
        initEvent();
        return mBinding.getRoot();
    }

    private void initData() {
        mMainActivity = (MainActivity) getActivity();
        mTitleList = new ArrayList<>();
        mTitleList.add(OPTIONAL);
        mTitleList.add(DOMINANT);
        mTitleList.add(SHANGHAI);
        mTitleList.add(NENGYUAN);
        mTitleList.add(DALIAN);
        mTitleList.add(ZHENGZHOU);
        mTitleList.add(ZHONGJIN);
        mTitleList.add(DALIANZUHE);
        mTitleList.add(ZHENGZHOUZUHE);

        //添加合约页，设置首页是自选合约列表页
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(QuoteFragment.newInstance(OPTIONAL));
        fragmentList.add(QuoteFragment.newInstance(DOMINANT));
        fragmentList.add(QuoteFragment.newInstance(SHANGHAI));
        fragmentList.add(QuoteFragment.newInstance(NENGYUAN));
        fragmentList.add(QuoteFragment.newInstance(DALIAN));
        fragmentList.add(QuoteFragment.newInstance(ZHENGZHOU));
        fragmentList.add(QuoteFragment.newInstance(ZHONGJIN));
        fragmentList.add(QuoteFragment.newInstance(DALIANZUHE));
        fragmentList.add(QuoteFragment.newInstance(ZHENGZHOUZUHE));
        //初始化适配器类
        mViewPagerFragmentAdapter = new ViewPagerFragmentAdapter(
                getActivity().getSupportFragmentManager(), fragmentList);
        mBinding.quotePager.setAdapter(mViewPagerFragmentAdapter);
        if (!SPUtils.contains(BaseApplication.getContext(), CONFIG_RECOMMEND_OPTIONAL)){
            mTitle = OPTIONAL;
            mBinding.quotePager.setCurrentItem(0);
        } else if (LatestFileManager.getOptionalInsList().isEmpty()){
            mTitle = DOMINANT;
            mBinding.quotePager.setCurrentItem(1);
        }else {
            mTitle = OPTIONAL;
            mBinding.quotePager.setCurrentItem(0);
        }

        mBinding.quotePager.setOffscreenPageLimit(8);
    }

    private void initEvent() {
        mBinding.quotePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                DataManager.getInstance().IS_POSITIVE = true;
                mTitle = mTitleList.get(position);
                mMainActivity.getmMainActivityPresenter().getmToolbarTitle().setText(mTitle);
                mMainActivity.getmMainActivityPresenter().switchQuotesNavigation(mTitle);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public String getmTitle() {
        return mTitle;
    }

    public QuoteFragment getCurrentItem() {
        try {
            if (mBinding.quotePager != null) {
                int index = mBinding.quotePager.getCurrentItem();
                return (QuoteFragment) mViewPagerFragmentAdapter.getItem(index);
            } else return null;
        } catch (Exception e) {
            return null;
        }
    }

    public void setCurrentItem(int index) {
        try {
            if (mBinding.quotePager != null) mBinding.quotePager.setCurrentItem(index, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
