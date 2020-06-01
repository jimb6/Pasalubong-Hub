package com.allandroidprojects.ecomsample.ui.composer.merchant.ordermanagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.util.ProductCategory;
import com.allandroidprojects.ecomsample.util.ProductOrderStatus;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class OrderManagement extends Fragment {

    private OrderManagementViewModel mViewModel;
    private View root;
    static ViewPager viewPager;
    static TabLayout tabLayout;

    public static OrderManagement newInstance() {
        return new OrderManagement();
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getFragmentManager());
        Bundle bundle = new Bundle();

        OrderListFragment fragment = new OrderListFragment();
        bundle = new Bundle();
        bundle.putString("type", ProductCategory.SWEETS.getValue());
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, ProductCategory.SWEETS.getValue());

        fragment = new OrderListFragment();
        bundle = new Bundle();
        bundle.putString("type", ProductOrderStatus.PENDING.get());
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, ProductOrderStatus.PENDING.get());

        fragment = new OrderListFragment();
        bundle = new Bundle();
        bundle.putString("type", ProductOrderStatus.ACCEPTED.get());
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, ProductOrderStatus.ACCEPTED.get());

        fragment = new OrderListFragment();
        bundle = new Bundle();
        bundle.putString("type", ProductOrderStatus.CANCELLED.get());
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, ProductOrderStatus.CANCELLED.get());

        fragment = new OrderListFragment();
        bundle = new Bundle();
        bundle.putString("type", ProductOrderStatus.HISTORY.get());
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, ProductOrderStatus.HISTORY.get());

        viewPager.setAdapter(adapter);
    }

    private void refresh(){
        
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root =  inflater.inflate(R.layout.order_management_fragment, container, false);

        viewPager = root.findViewById(R.id.viewpager);
        tabLayout = root.findViewById(R.id.tabs);

        if (viewPager != null) {
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
        }

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(OrderManagementViewModel.class);
        // TODO: Use the ViewModel
    }



    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
