package com.allandroidprojects.ecomsample.ui.composer.merchant.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.viewmodel.account.DashboardViewModel;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.MessagingFragment;
import com.allandroidprojects.ecomsample.ui.composer.merchant.account.ShopInfoFragment;
import com.allandroidprojects.ecomsample.ui.composer.merchant.notifications.NotificationsFragment;
import com.allandroidprojects.ecomsample.ui.composer.merchant.ordermanagement.OrderManagement;
import com.allandroidprojects.ecomsample.ui.composer.merchant.products.ProductFragment;
import com.allandroidprojects.ecomsample.ui.composer.merchant.startup.MerchantActivity;
import com.facebook.drawee.view.SimpleDraweeView;

public class DashboardFragment extends Fragment {


//    Variables
    Business business;

//    Components
    private SimpleDraweeView coverImage;
    private CardView accountSettings, productMgmt, orderMgmt, notif, help;
    private ImageButton message, camera;
    private DashboardViewModel dashboardViewModel;
    private View root;

//    Fragments
    DashboardFragment dashfragment;
    ProductFragment productfragment;
    OrderManagement orderfragment;
    NotificationsFragment notiffragment;
    MessagingFragment messagingfragment;
    ShopInfoFragment infofragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        business = ((MerchantActivity) getActivity()).myBusiness;

        initializeFragments();
        initializeComponents();
        initializeBusinessInfo();



        return root;
    }

    private void initializeBusinessInfo(){
        coverImage.setImageURI(business.getCoverUri());
    }

    private void initializeFragments(){
        dashfragment = new DashboardFragment();
        productfragment = new ProductFragment();
        orderfragment = new OrderManagement();
        notiffragment = new NotificationsFragment();
        messagingfragment = new MessagingFragment();
        infofragment = new ShopInfoFragment();
    }


    private void initializeComponents(){
        this.coverImage = root.findViewById(R.id.coverImage);
        this.accountSettings = root.findViewById(R.id.accountSettingsCard);
        this.productMgmt = root.findViewById(R.id.productManagementCard);
        this.orderMgmt = root.findViewById(R.id.orderManagementCard);
        this.notif = root.findViewById(R.id.notificationCard);
        this.help = root.findViewById(R.id.helpCenterCard);
        this.message = root.findViewById(R.id.messageIcon);


        accountSettings.setOnClickListener(view -> {
            ((MerchantActivity)getActivity()).getViewPager().setCurrentItem(5);
        });

        productMgmt.setOnClickListener(view -> {
            ((MerchantActivity)getActivity()).getViewPager().setCurrentItem(1);
        });

        orderMgmt.setOnClickListener(view -> {
            ((MerchantActivity)getActivity()).getViewPager().setCurrentItem(2);
        });

        notif.setOnClickListener(view -> {
            ((MerchantActivity)getActivity()).getViewPager().setCurrentItem(3);
        });

        help.setOnClickListener(view -> {
            ((MerchantActivity)getActivity()).getViewPager().setCurrentItem(0);
        });

        message.setOnClickListener(view -> {
            ((MerchantActivity)getActivity()).getViewPager().setCurrentItem(4);
        });


    }

    @Override
    public void onStart() {
        super.onStart();
//        AnyChartView anyChartView = root.findViewById(R.id.any_chart_view);
//        anyChartView.setProgressBar(root.findViewById(R.id.progressBar));
//
//        Pie pie = AnyChart.pie();
//
//        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
//            @Override
//            public void onClick(Event event) {
//                Toast.makeText(getContext(), event.getData().get("x") + ":" + event.getData().get("value"), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        List<DataEntry> data = new ArrayList<>();
//        data.add(new ValueDataEntry("Sweets", 6371664));
//        data.add(new ValueDataEntry("Goods", 789622));
//        data.add(new ValueDataEntry("Clothing", 7216301));
//        data.add(new ValueDataEntry("Books & More", 1486621));
//        data.add(new ValueDataEntry("More", 1200000));
//
//        pie.data(data);
//
//        pie.title("Total Sales of Your Store)");
//        pie.labels().position("outside");
//
//        pie.legend().title().enabled(true);
//        pie.legend().title()
//                .text("Retail channels")
//                .padding(0d, 0d, 10d, 0d);
//
//        pie.legend()
//                .position("center-bottom")
//                .itemsLayout(LegendLayout.HORIZONTAL)
//                .align(Align.CENTER);
//
//        anyChartView.setChart(pie);
    }
}
