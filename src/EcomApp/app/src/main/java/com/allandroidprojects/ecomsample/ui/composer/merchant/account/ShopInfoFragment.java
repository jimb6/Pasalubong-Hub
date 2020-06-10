package com.allandroidprojects.ecomsample.ui.composer.merchant.account;

import android.Manifest;
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
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.models.ShopMenuOption;
import com.allandroidprojects.ecomsample.ui.composer.merchant.products.AddProductActivity;
import com.allandroidprojects.ecomsample.ui.composer.merchant.startup.MerchantActivity;
import com.allandroidprojects.ecomsample.ui.composer.merchant.startup.ShopActivity;
import com.allandroidprojects.ecomsample.ui.startup.ViewPagerActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;
import com.shivtechs.maplocationpicker.LocationPickerActivity;
import com.shivtechs.maplocationpicker.MapUtility;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class ShopInfoFragment extends Fragment {

    private MerchantActivity activity;
    private ShopInfoViewModel mViewModel;
    private View root;
    private ImageButton coverImageSelection;
    private ImageView locationButton;
    private SimpleDraweeView coverImage;
    private Business myBusiness;
    private SwitchMaterial switcher;
    private Button update_info_button;
    private TextView coverImageSaveButton;
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
    private BottomSheetLayout bottomSheet;

    private int REQUEST_CODE_READ_STORAGE = 1;
    private int REQUEST_CODE_COVER_IMAGE = 3;
    private int ADDRESS_PICKER_REQUEST = 2;

    public static ShopInfoFragment newInstance() {
        return new ShopInfoFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MerchantActivity) getActivity();
        setHasOptionsMenu(true);
    }

    private void setupBusinessInfo() {
        myBusiness = activity.myBusiness;


        businessName.setText(myBusiness.getBusinessName());
        businessAddress.setText(myBusiness.getBusinessAddress());
        businessEmail.setText(myBusiness.getBusinessEmail());
        coverImage.setImageURI(myBusiness.getCoverUri());
        for(String url:myBusiness.getBusinessPhotos()){
            images.add(url);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_shop_info, container, false);
        MapUtility.apiKey = getResources().getString(R.string.google_api_key);

        this.myBusiness = ShopActivity.myBusiness;
        initComponents();
        initPreferences();
        disabledComponent();
        setupBusinessInfo();
        return root;
    }

    private void initComponents() {

        switcher = root.findViewById(R.id.edit_swithcer);
        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    enabledComponent();
                else
                    disabledComponent();
            }
        });
        update_info_button = root.findViewById(R.id.update_info_button);
        update_info_button.setOnClickListener(v -> {
            updateBusinessInfo();
        });

        coverImageSaveButton = root.findViewById(R.id.coverImageSaveButton);


        coverImageSelection = root.findViewById(R.id.cover_image_selection);
        bottomSheet = (BottomSheetLayout) root.findViewById(R.id.bottomsheet);
        businessName = root.findViewById(R.id.shop_name);
        businessAddress = root.findViewById(R.id.shop_address);
        businessEmail = root.findViewById(R.id.shop_email);
        coverImage = root.findViewById(R.id.coverImage);

        images.add(null);
        recyclerView = root.findViewById(R.id.uploadedImage);
        recylerViewLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(recylerViewLayoutManager);
        adapter = new ShopImagesRecyclerViewAdapter(recyclerView, images);
        recyclerView.setAdapter(adapter);

        locationButton = root.findViewById(R.id.location);
        locationButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LocationPickerActivity.class);
            if (myBusiness.getLat() != null && myBusiness.getLng() != null) {
                intent.putExtra(MapUtility.LATITUDE, myBusiness.getLat());
                intent.putExtra(MapUtility.LONGITUDE, myBusiness.getLng());
            }
            startActivityForResult(intent, ADDRESS_PICKER_REQUEST);
        });


        coverImageSelection.setOnClickListener(v -> {
            showChooser();
        });

        coverImageSaveButton.setOnClickListener(v -> {
            if (!myBusiness.getCoverUri().equals(activity.myBusiness.getCoverUri())){
                saveCoverImage();
            }
        });
    }

    private void disabledComponent() {
        update_info_button.setVisibility(View.GONE);
        coverImageSelection.setEnabled(false);
        businessName.setEnabled(false);
        businessAddress.setEnabled(false);
        businessEmail.setEnabled(false);
        coverImage.setEnabled(false);
        locationButton.setEnabled(false);
    }

    private void enabledComponent() {
        update_info_button.setVisibility(View.VISIBLE);
        coverImageSelection.setEnabled(true);
        businessName.setEnabled(true);
        businessAddress.setEnabled(true);
        businessEmail.setEnabled(true);
        coverImage.setEnabled(true);
        locationButton.setEnabled(true);
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
        startActivityForResult(intent, REQUEST_CODE_COVER_IMAGE);
    }

    private void saveCoverImage(){

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

    public void showAlertSaveState(String title, String body){
        LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(getActivity(), DialogTypes.TYPE_QUESTION)
                .setTitle(title)
                .setDescription(body)
                .setPositiveText("Update")
                .setPositiveListener(lottieAlertDialog -> {
                    saveState();
                    lottieAlertDialog.dismiss();
                }).setNegativeText("Cancel")
                .setNegativeListener(lottieAlertDialog -> {
                    lottieAlertDialog.dismiss();
                })
                .build();
        alertDialog.show();
    }

    private void updateBusinessInfo() {
        if (myBusiness.getBusinessEmail().equals(String.valueOf(businessEmail.getText())) &&
                myBusiness.getBusinessAddress().equals(String.valueOf(businessAddress.getText())) &&
                myBusiness.getBusinessName().equals(String.valueOf(businessName.getText())) &&
                myBusiness.getLat().equals(activity.myBusiness.getLat()) &&
                myBusiness.getLng().equals(activity.myBusiness.getLng()) &&
                myBusiness.getBusinessPhotos().equals(activity.myBusiness.getBusinessPhotos())) {
            Toast.makeText(activity, "No changes happen.", Toast.LENGTH_SHORT).show();
            return;
        }
        String title = "Business Info Changes.";
        String message = "Do you want to continue update your business info?";
        showAlertSaveState(title, message);
    }

    private void saveState() {
        mViewModel.saveState(myBusiness);
        mViewModel.getSavingStateResult().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                Toast.makeText(activity, "Updated Successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Unable to Update.", Toast.LENGTH_SHORT).show();
            }
        });
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
                                coverImage.setImageURI(imageUri);
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
                            coverImage.setImageURI(uri);

                        } catch (Exception e) {
                            Log.e("File Chooser", "File select error", e);
                        }
                    }
                }
            }
            if (requestCode == REQUEST_CODE_COVER_IMAGE) {
                if (resultData != null) {
                    if (resultData.getClipData() != null) {
                        int count = resultData.getClipData().getItemCount();
                        int currentItem = 0;
                        while (currentItem < count) {
                            Uri imageUri = resultData.getClipData().getItemAt(currentItem).getUri();
                            currentItem = currentItem + 1;
                            Log.d("Uri Selected", imageUri.toString());
                            try {

                                coverImage.setImageURI(imageUri);
                                myBusiness.setCoverUri(imageUri.toString());
                                coverImageSaveButton.setVisibility(View.VISIBLE);
                            } catch (Exception e) {
                                Log.e("File Chooser", "File select error", e);
                            }
                        }
                    } else if (resultData.getData() != null) {
                        final Uri uri = resultData.getData();
                        Log.i("URI Closing", "Uri = " + uri.toString());
                        try {
                            myBusiness.setCoverUri(uri.toString());
                            coverImage.setImageURI(uri);
                            coverImageSaveButton.setVisibility(View.VISIBLE);

                        } catch (Exception e) {
                            Log.e("File Chooser", "File select error", e);
                        }
                    }
                }
            }

            if (requestCode == ADDRESS_PICKER_REQUEST) {
                try {
                    if (resultData != null && resultData.getStringExtra(MapUtility.ADDRESS) != null) {
                        String address = resultData.getStringExtra(MapUtility.ADDRESS);
                        double selectedLatitude = resultData.getDoubleExtra(MapUtility.LATITUDE, 0.0);
                        double selectedLongitude = resultData.getDoubleExtra(MapUtility.LONGITUDE, 0.0);
//                        txtAddress.setText("Address: "+address);
//                        txtLatLong.setText("Lat:"+selectedLatitude+"  Long:"+selectedLongitude);
                        businessAddress.setText(address);
                        myBusiness.setLat(String.valueOf(selectedLatitude));
                        myBusiness.setLng(String.valueOf(selectedLongitude));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    public class ShopImagesRecyclerViewAdapter
            extends RecyclerView.Adapter<ShopImagesRecyclerViewAdapter.ViewHolder> {

        private ArrayList<String> imageUri;
        private RecyclerView mRecyclerView;

        @Override
        public void onBindViewHolder(final ShopImagesRecyclerViewAdapter.ViewHolder holder, final int position) {
            final String uri = imageUri.get(position);

            if (uri == null) {
                holder.add_new_button.setVisibility(View.VISIBLE);
                holder.add_new_button.setOnClickListener(v -> {
                    // Display the file chooser dialog
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        askForPermission();
                    } else {
                        showChooser();
                    }
                });
                holder.deleteImage.setVisibility(View.GONE);
            } else {

                holder.mImageView.setImageURI(Uri.parse(uri));
                holder.mImageView.setOnClickListener(v -> {

                    //Image Viewr will show when Image are greater than 1
//                    because we set image 1 as null in order to show add button

                    if (imageUri.size() > 1) {
                        ArrayList<String> imageUriString = new ArrayList<>();
                        for (String uriStr : imageUri) {
                            if (uriStr == null)
                                continue;
                            imageUriString.add(uriStr.toString());
                        }

                        Intent intent = new Intent(getActivity(), ViewPagerActivity.class);
                        intent.putExtra("position", position);
                        intent.putStringArrayListExtra("images", imageUriString);
                        startActivity(intent);
                    }
                });
                holder.deleteImage.setVisibility(View.GONE);
                holder.deleteImage.setOnClickListener(v -> {
                    removeImage(position);
                });
            }


        }

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


        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final SimpleDraweeView mImageView;
            public final ImageView deleteImage;
            public final View add_new_button;


            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = view.findViewById(R.id.image1);
                deleteImage = view.findViewById(R.id.delete_image_button);
                deleteImage.bringToFront();
                add_new_button = view.findViewById(R.id.add_image_item);

            }
        }

        @Override
        public int getItemCount() {
            return imageUri.size();
        }
    }

}
