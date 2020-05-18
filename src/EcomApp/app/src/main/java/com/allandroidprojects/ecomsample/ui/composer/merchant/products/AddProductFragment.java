package com.allandroidprojects.ecomsample.merchant.products;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.factory.product.AddProductViewModelFactory;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.models.product.Product;
import com.allandroidprojects.ecomsample.data.view_model.product.AddProductViewModel;
import com.allandroidprojects.ecomsample.merchant.startup.MerchantActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.roger.catloadinglibrary.CatLoadingView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static android.app.Activity.RESULT_OK;

public class AddProductFragment extends Fragment {

    private AddProductActivity activity;
    private Button upload, save, update, delete;
    private View root;
    private ArrayList<Uri> images = new ArrayList<>();
    //    private Map<Integer, Uri> images = new HashMap<>();
    private int REQUEST_CODE_READ_STORAGE = 1;
    private RecyclerView.LayoutManager recylerViewLayoutManager;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private AddProductViewModel viewModel;
    private AutoCompleteTextView actcategory, actcondition, actWholeSale;
    private CatLoadingView mView;
    private int imagesUniqueId = 0;
    private ChipGroup tagsGroup;
    private String[] categories = new String[]{"Sweets", "Goods", "Clothing", "Decoration", "Souvenir"};
    private String[] conditions = new String[] {"New", "Used"};
    private String[] wholeSale = new String[] {"Available", "Planned", "Not Available"};
    private Product productToUpdate;
    private int SpannedLength = 0, chipLength = 4;
    private Product newProduct = new Product();
    private ArrayList<String> tempImages = new ArrayList<>();
    private TextInputEditText productName, productDescription, productPrice, productStock, productTags;
    private View loadingLayout;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AddProductActivity) getActivity();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_first, container, false);
        initializeViewModel();
        initializeComponents();
        setupRecyclerView();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
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
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(resultData);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                images.add(resultUri);
                adapter.notifyItemInserted(images.size() - 1);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if (requestCode == REQUEST_CODE_READ_STORAGE) {
            if (resultCode == RESULT_OK) {
                if (resultData != null) {
//                    if (resultData.getClipData() != null) {
//                        int count = resultData.getClipData().getItemCount();
//                        int currentItem = 0;
//                        while (currentItem < count) {
//                            Uri imageUri = resultData.getClipData().getItemAt(currentItem).getUri();
//                            currentItem = currentItem + 1;
//
//                            Log.d("Uri Selected", imageUri.toString());
//
//                            try {
//                                images.add(imageUri);
//                                adapter.notifyItemInserted(images.size() - 1);
////                                MyAdapter mAdapter = new MyAdapter(MainActivity.this, arrayList);
////                                listView.setAdapter(mAdapter);
//
//                            } catch (Exception e) {
//                                Log.e("File Chooser", "File select error", e);
//                            }
//                        }
//                    }
                    if (resultData.getData() != null) {
                        final Uri uri = resultData.getData();
                        try {
                            Intent intent = CropImage.activity(uri)
                                    .getIntent(getContext());
                            startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);

                        } catch (Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void initializeViewModel(){
        this.viewModel = ViewModelProviders.of(activity, new AddProductViewModelFactory()).get(AddProductViewModel.class);
    }

    private void initializeComponents() {
//        mView = new CatLoadingView();
//        mView.setCanceledOnTouchOutside(false);
        loadingLayout = root.findViewById(R.id.custom_loading);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                        getContext(),
                        R.layout.product_category_menu_dropdown,
                        categories);

        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.product_category_menu_dropdown,
                conditions);

        ArrayAdapter<String> wholeSaleAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.product_category_menu_dropdown,
                wholeSale);

        actcategory = root.findViewById(R.id.product_category);
        actcategory.setAdapter(categoryAdapter);

        actcondition = root.findViewById(R.id.product_condition);
        actcondition.setAdapter(conditionAdapter);

        actWholeSale = root.findViewById(R.id.product_whole_sale);
        actWholeSale.setAdapter(wholeSaleAdapter);

        productName = root.findViewById(R.id.product_name);
        productDescription = root.findViewById(R.id.product_description);
        productPrice = root.findViewById(R.id.product_price);
        productStock = root.findViewById(R.id.product_stock);

        tagsGroup = root.findViewById(R.id.tags_group);
        productTags = root.findViewById(R.id.tags);
        productTags.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String txtVal = v.getText().toString();
                    if (!txtVal.equals("")) {
                        addChipToGroup(txtVal, tagsGroup);
                        productTags.setText("");
                    }
                    return true;
                }
                return false;
            }
        });

        upload = root.findViewById(R.id.btnChoose);
        upload.setOnClickListener(v -> {
            // Display the file chooser dialog
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askForPermission();
            } else {
                showChooser();
            }
        });

        save = root.findViewById(R.id.button_first);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInput("Save");
            }
        });

        update = root.findViewById(R.id.button_update);
        update.setVisibility(View.GONE);
        update.setOnClickListener(c -> {
            validateInput("Update");
        });

        delete = root.findViewById(R.id.button_delete);
        delete.setVisibility(View.GONE);
        delete.setOnClickListener(c -> {
            new MaterialAlertDialogBuilder(getActivity())
                    .setTitle("Delete Prdocut")
                    .setMessage("Are you sure you want to permamently delete this product?")
                    .setNeutralButton(R.string.cancel, (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .setNegativeButton(R.string.decline, (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .setPositiveButton(R.string.accept, (dialog, which) -> {
                        deleteProduct();
                    })
                    .show();
        });


        productToUpdate = activity.getProduct();
        if (productToUpdate != null) {
            update.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            save.setVisibility(View.GONE);

            productName.setText(productToUpdate.getProductname());
            productDescription.setText(productToUpdate.getProductDescription());
            productPrice.setText(String.valueOf(productToUpdate.getPrice()));
            productStock.setText(String.valueOf(productToUpdate.getStock()));

            actcategory.setText(productToUpdate.getProductCategory());
            actcondition.setText(productToUpdate.getCondition());
            actWholeSale.setText(productToUpdate.getWholeSeller());

            for (String tag : productToUpdate.getTags()) {
                addChipToGroup(tag, tagsGroup);
            }

            for (String url : productToUpdate.getImageUrls()) {
                images.add(Uri.parse(url));
            }
        }
    }

    private void setupRecyclerView(){
        recyclerView = root.findViewById(R.id.uploadedImage);
        recylerViewLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(recylerViewLayoutManager);
        adapter = new SimpleStringRecyclerViewAdapter(recyclerView, images);
        recyclerView.setAdapter(adapter);
    }

    private void deleteProduct() {
        loadingLayout.setVisibility(View.VISIBLE);
        viewModel.deleteProduct(productToUpdate);
        viewModel.isProductDeleted().observe(getViewLifecycleOwner(), p -> {
            if (p) {
                Toast.makeText(getContext(), "Product Deleted Successfully.", Toast.LENGTH_SHORT).show();
                activity.finish();
            } else {
                Toast.makeText(getContext(), "Unable to Delete Product.", Toast.LENGTH_SHORT).show();
            }
            loadingLayout.setVisibility(View.GONE);
//            mView.dismiss();
        });
    }

    private void saveProductImages(String action)
    {
        loadingLayout.setVisibility(View.VISIBLE);

//        mView.show(getParentFragmentManager(), "");
        AtomicInteger numberOfResult = new AtomicInteger();
        viewModel.saveNewProductImages(MerchantActivity.myBusiness.getOwnerId(), images);
        viewModel.getNewProductImagesResult().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success){
                tempImages.add(((Result.Success) result).getData().toString());
                numberOfResult.getAndIncrement();
                Toast.makeText(activity, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                if (numberOfResult.get() == images.size()) {
                    if (action.equals("Save")) {
                        saveProductInfo(tempImages);
                    } else if (action.equals("Update")) {
                        updateProductInfo(tempImages, true);
                    }
                }
            }else{
                numberOfResult.getAndIncrement();
                Toast.makeText(activity, "Failed to upload Image", Toast.LENGTH_SHORT).show();
                loadingLayout.setVisibility(View.GONE);
            }
        });


    }

    private void saveProductInfo(ArrayList<String> links) {
        Product product = new Product();
        product.setProductname(productName.getText().toString());
        product.setProductDescription(productDescription.getText().toString());
        product.setProductCategory(actcategory.getText().toString());
        product.setPrice(Double.parseDouble(productPrice.getText().toString()));
        product.setStock(Integer.parseInt(productStock.getText().toString()));
        product.setCondition(actcondition.getText().toString());
        product.setWholeSeller(actWholeSale.getText().toString());
        ArrayList<String> tags = new ArrayList<>();
        for (int i = 0; i < tagsGroup.getChildCount(); i++) {
            Chip chipObj = (Chip) tagsGroup.getChildAt(i);
            tags.add(chipObj.getText().toString());
        }
        product.setTags(tags);

//        product.setImageUrls(arrayList);
        product.setBusinessOwnerId(MerchantActivity.myBusiness.getOwnerId());
//        String[] imageLinks = new String[links.size()];
//        for (int i=0; i<links.size();i++)
//            imageLinks[i] = links.get(i).toString();

        product.setImageUrls(links);
        viewModel.saveNewProduct(product);
        viewModel.getNewProductResult().observe(getViewLifecycleOwner(), p -> {
            if (p instanceof Result.Success){
                Toast.makeText(getContext(), "Product Added Successfully.", Toast.LENGTH_SHORT).show();
                Product reference = (Product) ((Result.Success) p).getData();
//                saveImages();
                activity.finish();
            }else if (p instanceof Result.Error){
                Toast.makeText(getContext(), ((Result.Error) p).getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
            loadingLayout.setVisibility(View.GONE);
//            mView.dismiss();
        });
    }

    private void validateInput(String action) {

        if (productName.getText().toString().equals(""))
            return;
        if (productDescription.getText().toString().equals(""))
            return;
        if (actcategory.getText().toString().equals(""))
            return;
        if (productPrice.getText().toString().equals(""))
            return;
        if (productStock.getText().toString().equals(""))
            return;
        if (actcondition.getText().toString().equals(""))
            return;
        if (actWholeSale.getText().toString().equals(""))
            return;

        newProduct.setProductname(productName.getText().toString());
        newProduct.setProductDescription(productDescription.getText().toString());
        newProduct.setProductCategory(actcategory.getText().toString());
        newProduct.setPrice(Double.parseDouble(productPrice.getText().toString()));
        newProduct.setStock(Integer.parseInt(productStock.getText().toString()));
        newProduct.setCondition(actcondition.getText().toString());
        newProduct.setWholeSeller(actWholeSale.getText().toString());

        if (productToUpdate != null) {
            newProduct.setBusinessOwnerId(productToUpdate.getBusinessOwnerId());
            newProduct.setProductReference(productToUpdate.getProductReference());
        }

        ArrayList<String> tags = new ArrayList<>();
        for (int i = 0; i < tagsGroup.getChildCount(); i++) {
            Chip chipObj = (Chip) tagsGroup.getChildAt(i);
            tags.add(chipObj.getText().toString());
        }

        newProduct.setTags(tags);

        if (tags.size() < 1)
            return;

        if (images.size() < 1)
            return;

//        Checks if new images added
        if (productToUpdate != null) {
            for (int i = 0; i < images.size(); ) {
                Uri image = images.get(i);
                if (productToUpdate.getImageUrls().contains(image.toString())) {
                    images.remove(i);
                    tempImages.add(image.toString());
                } else {
                    i++;
                }
            }
        }

        if (images.size() > 0)
            saveProductImages(action);
        else
            updateProductInfo(tempImages, false);
    }

    private void updateProductInfo(ArrayList<String> links, boolean hasNewImages) {
        if (!hasNewImages)
            loadingLayout.setVisibility(View.VISIBLE);

        newProduct.setImageUrls(links);
        viewModel.updateProduct(newProduct);
        viewModel.getUpdatedProduct().observe(getViewLifecycleOwner(), p -> {
            if (p instanceof Result.Success) {
                Toast.makeText(getContext(), "Product Updated.", Toast.LENGTH_SHORT).show();
                Product reference = (Product) ((Result.Success) p).getData();
                activity.finish();
            } else if (p instanceof Result.Error) {
                Toast.makeText(getContext(), ((Result.Error) p).getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
            loadingLayout.setVisibility(View.GONE);
        });
    }

    private void askForPermission() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_READ_STORAGE);
        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, REQUEST_CODE_READ_STORAGE);
        }
    }

    private void showChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE_READ_STORAGE);
    }

    private void removeImage(int position) {
        images.remove(position);
        setupRecyclerView();
    }

    private void addChipToGroup(String txt, ChipGroup chipGroup) {
        Chip chip = new Chip(getContext());
        chip.setText(txt);
//        chip.chipIcon = ContextCompat.getDrawable(requireContext(), baseline_person_black_18)
        chip.setCloseIconEnabled(true);
        chip.setChipIconTintResource(R.color.jet);

        // necessary to get single selection working
        chip.setClickable(false);
        chip.setCheckable(false);
        chipGroup.addView(chip);
        chip.setOnCloseIconClickListener(c -> {
            chipGroup.removeView(chip);
        });
        printChipsValue(chipGroup);
    }

    private void printChipsValue(ChipGroup chipGroup) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chipObj = (Chip) chipGroup.getChildAt(i);
            Log.d("Chips text :: ", chipObj.getText().toString());
        }
    }










    public class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private ArrayList<Uri> imageUri;
        private RecyclerView mRecyclerView;

        @Override
        public void onBindViewHolder(final SimpleStringRecyclerViewAdapter.ViewHolder holder, final int position) {
            final Uri uri = imageUri.get(position);
            holder.mImageView.setImageURI(uri);
            holder.mImageView.setOnClickListener(v -> {
            });
            holder.deleteImage.setOnClickListener(v -> {
                removeImage(position);
            });

        }

        public SimpleStringRecyclerViewAdapter(RecyclerView recyclerView, ArrayList<Uri> imagesUri) {
            imageUri = imagesUri;
            mRecyclerView = recyclerView;
        }

        @Override
        public SimpleStringRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
            return new SimpleStringRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onViewRecycled(SimpleStringRecyclerViewAdapter.ViewHolder holder) {
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

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = view.findViewById(R.id.image1);
                deleteImage = view.findViewById(R.id.delete_image_button);
                deleteImage.bringToFront();
            }
        }

        @Override
        public int getItemCount() {
            return imageUri.size();
        }
    }
}
