package com.allandroidprojects.ecomsample.merchant.account;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.ShopMenuOption;
import com.allandroidprojects.ecomsample.merchant.products.AddProductActivity;
import com.allandroidprojects.ecomsample.merchant.startup.MerchantActivity;
import com.allandroidprojects.ecomsample.merchant.startup.ShopActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import info.androidhive.fontawesome.FontDrawable;

import static android.app.Activity.RESULT_OK;

public class ShopInfoFragment extends Fragment {

    private MerchantActivity activity;
    private ShopInfoViewModel mViewModel;
    private View root;
    private Business myBusiness;

    private TextInputEditText businessName, businessAddress, businessEmail;
    private Button upload;

    private RecyclerView.LayoutManager recylerViewLayoutManager;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager menuRecylerViewLayoutManager;
    private RecyclerView menuRecyclerView;
    private RecyclerView.Adapter menuAdapter;

    private ArrayList<String> images = new ArrayList<>();
    private ArrayList<ShopMenuOption> menus = new ArrayList<>();

    private int REQUEST_CODE_READ_STORAGE = 1;

    public static ShopInfoFragment newInstance() {
        return new ShopInfoFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MerchantActivity) getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_shop_info, container, false);
        this.myBusiness = ShopActivity.myBusiness;
        initComponents();
        initPreferences();
        return root;
    }

    private void initComponents() {
        businessName = root.findViewById(R.id.shop_name);
        businessAddress = root.findViewById(R.id.shop_address);
        businessEmail = root.findViewById(R.id.shop_email);

        upload = root.findViewById(R.id.btnChoose);
        upload.setOnClickListener(v -> {
            // Display the file chooser dialog
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askForPermission();
            } else {
                showChooser();
            }
        });

        recyclerView = root.findViewById(R.id.uploadedImage);
        recylerViewLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(recylerViewLayoutManager);
        adapter = new ShopImagesRecyclerViewAdapter(recyclerView, images);
        recyclerView.setAdapter(adapter);


        menus.add(new ShopMenuOption("Add New Product", R.string.fa_box_solid));
        menus.add(new ShopMenuOption("View Shop Rating", R.string.fa_star_solid));
        menus.add(new ShopMenuOption("My Income", R.string.fa_money_bill_solid));
        menus.add(new ShopMenuOption("My Performance", R.string.fa_chart_area_solid));
        menus.add(new ShopMenuOption("Help Center", R.string.fa_question_circle_solid));

        menuRecyclerView = root.findViewById(R.id.rv_shop_menu);
        menuRecylerViewLayoutManager = new LinearLayoutManager(activity);

        menuRecyclerView.setLayoutManager(menuRecylerViewLayoutManager);
        menuAdapter = new ShopMenuRecyclerViewAdapter(menuRecyclerView, getContext(), menus);
        menuRecyclerView.setAdapter(menuAdapter);
    }

    private void initPreferences() {
        if (myBusiness != null) {
            businessName.setText(myBusiness.getBusinessName());
            businessAddress.setText(myBusiness.getBusinessAddress());
            businessEmail.setText(myBusiness.getBusinessEmail());
            images.addAll(myBusiness.getBusinessPhotos());
            adapter.notifyDataSetChanged();
        }
    }

    private void showChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE_READ_STORAGE);
    }

    private void removeImage(int position) {
        images.remove(position);
        adapter.notifyItemRemoved(position);
    }

    private void askForPermission() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_READ_STORAGE);
        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, REQUEST_CODE_READ_STORAGE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // Handle item selection
        switch (menuItem.getItemId()) {
            case R.id.shop_update:
                Intent intent = new Intent(getActivity(), AddProductActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.shop_option, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, REQUEST_CODE_READ_STORAGE);
                } else {
                    Toast.makeText(activity, "Please allow permission to upload images.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:

        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ShopInfoViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_READ_STORAGE) {
                if (resultData != null) {
                    if (resultData.getClipData() != null) {
                        int count = resultData.getClipData().getItemCount();
                        int currentItem = 0;
                        while (currentItem < count) {
                            Uri imageUri = resultData.getClipData().getItemAt(currentItem).getUri();
                            currentItem = currentItem + 1;

                            Log.d("Uri Selected", imageUri.toString());

                            try {
                                images.add(String.valueOf(imageUri));
                                adapter.notifyItemInserted(images.size() - 1);
//                                MyAdapter mAdapter = new MyAdapter(MainActivity.this, arrayList);
//                                listView.setAdapter(mAdapter);

                            } catch (Exception e) {
                                Log.e("File Chooser", "File select error", e);
                            }
                        }
                    } else if (resultData.getData() != null) {

                        final Uri uri = resultData.getData();
                        Log.i("URI Closing", "Uri = " + uri.toString());

                        try {
                            images.add(String.valueOf(uri));
                            adapter.notifyItemInserted(images.size() - 1);
//                            MyAdapter mAdapter = new MyAdapter(activity, arrayList);
//                            listView.setAdapter(mAdapter);

                        } catch (Exception e) {
                            Log.e("File Chooser", "File select error", e);
                        }
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
    }


    public class ShopImagesRecyclerViewAdapter
            extends RecyclerView.Adapter<ShopImagesRecyclerViewAdapter.ViewHolder> {

        private ArrayList<String> imageUri;
        private RecyclerView mRecyclerView;

        public ShopImagesRecyclerViewAdapter(RecyclerView recyclerView, ArrayList<String> imagesUri) {
            imageUri = imagesUri;
            mRecyclerView = recyclerView;
        }

        @Override
        public ShopImagesRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
            return new ShopImagesRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onViewRecycled(ShopImagesRecyclerViewAdapter.ViewHolder holder) {
            if (holder.mImageView.getController() != null) {
                holder.mImageView.getController().onDetach();
            }
            if (holder.mImageView.getTopLevelDrawable() != null) {
                holder.mImageView.getTopLevelDrawable().setCallback(null);
//                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
            }
        }

        @Override
        public void onBindViewHolder(final ShopImagesRecyclerViewAdapter.ViewHolder holder, final int position) {
            holder.mImageView.setImageURI(Uri.parse(imageUri.get(position)));
            holder.mImageView.setOnClickListener(v -> {
            });
            holder.deleteImage.setOnClickListener(v -> {
                removeImage(position);
            });
        }

        @Override
        public int getItemCount() {
            return imageUri.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final SimpleDraweeView mImageView;
            public final ImageView deleteImage;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = view.findViewById(R.id.image1);
                deleteImage = view.findViewById(R.id.delete_image_button);
                deleteImage.bringToFront();
            }
        }
    }

    public class ShopMenuRecyclerViewAdapter
            extends RecyclerView.Adapter<ShopMenuRecyclerViewAdapter.ViewHolder> {

        private ArrayList<ShopMenuOption> menus;
        private RecyclerView mRecyclerView;
        private Context context;

        public ShopMenuRecyclerViewAdapter(RecyclerView recyclerView, Context context, ArrayList<ShopMenuOption> menus) {
            this.menus = menus;
            this.context = context;
            mRecyclerView = recyclerView;
        }

        @Override
        public ShopMenuRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_menu_action, parent, false);
            return new ShopMenuRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onViewRecycled(ShopMenuRecyclerViewAdapter.ViewHolder holder) {

        }

        @Override
        public void onBindViewHolder(final ShopMenuRecyclerViewAdapter.ViewHolder holder, final int position) {
            ShopMenuOption item = menus.get(position);


            FontDrawable drawable = new FontDrawable(context, item.getMenuIcon(), true, false);
            drawable.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            drawable.setTextSize(20);
            holder.icon.setImageDrawable(drawable);
            holder.menu.setText(item.getMenuName());
        }

        @Override
        public int getItemCount() {
            return menus.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView menu;
            public final ImageView icon;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                menu = view.findViewById(R.id.tvMenuname);
                icon = view.findViewById(R.id.ivMenuIcon);
            }
        }
    }

}
