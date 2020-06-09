package com.allandroidprojects.ecomsample.ui.composer.merchant.products;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
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
import com.allandroidprojects.ecomsample.data.models.Product;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.viewmodel.product.AddProductViewModel;
import com.allandroidprojects.ecomsample.ui.composer.merchant.startup.MerchantActivity;
import com.allandroidprojects.ecomsample.ui.startup.ViewPagerActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static android.app.Activity.RESULT_OK;

public class AddProductFragment extends Fragment {

    private AddProductActivity activity;

    private Button save, update, delete;
    private View root;
    private TextInputEditText productName, productDescription, productPrice, productStock, productTags;
    private TransactionType transactionType;
    private RecyclerView recyclerView;
    private AutoCompleteTextView actcategory, actcondition, actWholeSale;
    private ChipGroup tagsGroup;

    private RecyclerView.LayoutManager recylerViewLayoutManager;
    private RecyclerView.Adapter adapter;
    private AddProductViewModel viewModel;

    private String[] categories = new String[]{"Sweets", "Goods", "Clothing", "Decoration", "Souvenir"};
    private String[] conditions = new String[]{"New", "Used"};
    private String[] wholeSale = new String[]{"Available", "Planned", "Not Available"};

    private Product productToUpdate;
    private Product newProduct = new Product();


    private ArrayList<Uri> images = new ArrayList<>();

    private ArrayList<String> imagesLinksToSave = new ArrayList<>();
    private int REQUEST_CODE_READ_STORAGE = 1;
    private LottieAlertDialog alertDialog;


    private boolean isValidInputsForSave() {
        //Now we reomve the images first index because its null to avoid any error
        //
        images.remove(0);

        if (!isTextInputsValid()) {
            images.add(0, null);
            showErrorAlert("Invalid Inputs", "Please fill out all inputs.");
            return false;
        }

        if (!isImageInputsValid()) {
            images.add(0, null);
            showErrorAlert("Invalid Images", "Please upload atleast 1 image for your product.");
            return false;
        }

        if (!isProducTagsValid()) {
            images.add(0, null);
            showErrorAlert("Invalid Tags", "Please input some tags for your products. " +
                    "Tags serves as the search text of your product.");
            return false;
        }

        showSavingDialog();
        Product product = dataMapping();
        uploadImages(product);

        return true;
    }

    private boolean isValidInputsForUpdate() {
        //Now we reomve the images first index because its null to avoid any error
        //
        images.remove(0);
        if (!isTextInputsValid()) {
            images.add(0, null);
            showErrorAlert("Invalid Inputs", "Please fill out all inputs.");
            return false;
        }

        if (!isProducTagsValid()) {
            images.add(0, null);
            showErrorAlert("Invalid Tags", "Please input some tags for your products. " +
                    "Tags serves as the search text of your product.");
            return false;
        }

        if (!isImageInputsValid()) {
            images.add(0, null);
            showErrorAlert("Invalid Images", "Please upload atleast 1 image for your product.");
            return false;
        }


        for (int i = 0; i < images.size(); ) {
            Uri image = images.get(i);
            if (productToUpdate.getImageUrls().contains(image.toString())) {
                images.remove(i);
                imagesLinksToSave.add(image.toString());
            } else {
                i++;
            }
        }

        showSavingDialog();
        Product product = dataMapping();
        if (images.size() > 0)
            uploadImages(product);
        else
            updateProduct(product);
        return true;
    }

    private boolean isTextInputsValid() {
        if (isEmpty(
                productName.getText().toString(),
                productDescription.getText().toString(),
                productPrice.getText().toString(),
                productPrice.getText().toString(),
                productStock.getText().toString()
        )) {
            return false;
        }
        return true;
    }

    private boolean isImageInputsValid() {
        if (isClear(images.size())) {
            return false;
        }
        return true;
    }

    private boolean isProducTagsValid() {
        if (isClear(tagsGroup.getChildCount())) {
            return false;
        }
        return true;
    }

    private void showErrorAlert(String title, String body) {
        LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(getActivity(), DialogTypes.TYPE_ERROR)
                .setTitle(title)
                .setDescription(body)
                .setPositiveText("Close")
                .setPositiveListener(lottieAlertDialog -> {
                    lottieAlertDialog.dismiss();
                })
                .build();
        alertDialog.show();
    }

    private void showSuccessAlert(String title, String body) {
        LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(getActivity(), DialogTypes.TYPE_SUCCESS)
                .setTitle(title)
                .setDescription(body)
                .setPositiveText("Close")
                .setPositiveListener(lottieAlertDialog -> {
                    lottieAlertDialog.dismiss();
                }).build();
        alertDialog.dismiss();
    }

    private boolean isClear(int... sizes) {
        for (int size : sizes)
            if (size < 1)
                return true;
        return false;
    }

    private boolean isEmpty(Object... objs) {
        for (Object obj : objs)
            if (objs.equals(""))
                return true;
        return false;
    }

    private LottieAlertDialog showSavingDialog() {
        alertDialog = new LottieAlertDialog.Builder(getContext(), DialogTypes.TYPE_LOADING)
                .setTitle("Saving")
                .setDescription("Please Wait")
                .build();

        alertDialog.setCancelable(false);
        alertDialog.show();

        return alertDialog;
    }

    private void dismisSavingDialog() {
        alertDialog.dismiss();
    }

    private Product dataMapping() {
        //Data Mapping for new product to be save
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
        product.setBusinessOwnerId(MerchantActivity.myBusiness.getOwnerId());
        product.setImageUrls(imagesLinksToSave);
        product.setTotalSales(0);


        if (transactionType == TransactionType.EDIT) {
            product.setProductReference(productToUpdate.getProductReference());
            product.setTotalSales(productToUpdate.getTotalSales());
        }

        return product;
    }

    private void saveNewproduct(Product product) {
        alertDialog.setMessage("Saving new prouct. Please wait...");

        viewModel.saveNewProduct(product);
        viewModel.getNewProductResult().observe(getViewLifecycleOwner(), p -> {
            dismisSavingDialog(); // Dismiss the loading dialog
            if (p instanceof Result.Success) {
                showSuccessAlert("Saved!", "New product saved to your store.");
                Product reference = (Product) ((Result.Success) p).getData();
                finishFragmentTransaction();
            } else if (p instanceof Result.Error) {
                showErrorAlert("Error", ((Result.Error) p).getError().getMessage());
            }
        });
    }

    private void updateProduct(Product product) {
        alertDialog.setMessage("Updating product please wait...");

        newProduct.setImageUrls(imagesLinksToSave);
        viewModel.updateProduct(product);
        viewModel.getUpdatedProduct().observe(getViewLifecycleOwner(), p -> {
            dismisSavingDialog();
            if (p instanceof Result.Success) {
                showSuccessAlert("Success", "Product has been upated!");
                Product reference = (Product) ((Result.Success) p).getData();
                finishFragmentTransaction();
            } else if (p instanceof Result.Error) {
                showErrorAlert("Error", ((Result.Error) p).getError().getMessage());
            }

        });
    }

    private void checkFragmentTransactionInstance() {
        productToUpdate = activity.getProduct();
        if (productToUpdate != null)
            transactionType = TransactionType.EDIT;
        else
            transactionType = TransactionType.CREATE;
    }

    private void finishFragmentTransaction() {
        getActivity().finish();
    }

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

        images.add(null); // This is for ad image button put in recycler view

        initializeViewModel();
        checkFragmentTransactionInstance();
        initializeComponents();
        setupRecyclerView();
        return root;
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
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        //This request code is for image croping
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

        //This request code is for save the image uri and display in recyclerview
        if (requestCode == REQUEST_CODE_READ_STORAGE) {
            if (resultCode == RESULT_OK) {
                if (resultData != null) {
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

    private void initializeViewModel() {
        this.viewModel = ViewModelProviders.of(activity, new AddProductViewModelFactory()).get(AddProductViewModel.class);
    }

    private void initializeComponents() {
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
        productTags.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String txtVal = v.getText().toString();
                if (!txtVal.equals("")) {
                    addChipToGroup(txtVal, tagsGroup);
                    productTags.setText("");
                }
                return true;
            }
            return false;
        });

        save = root.findViewById(R.id.button_first);
        save.setOnClickListener(view -> {
            isValidInputsForSave();
        });

        update = root.findViewById(R.id.button_update);
        update.setVisibility(View.GONE);
        update.setOnClickListener(c -> {
            isValidInputsForUpdate();
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

        if (transactionType == TransactionType.EDIT) {
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

    private void setupRecyclerView() {
        recyclerView = root.findViewById(R.id.uploadedImage);
        recylerViewLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(recylerViewLayoutManager);
        adapter = new SimpleStringRecyclerViewAdapter(recyclerView, images);
        recyclerView.setAdapter(adapter);
    }

    private void deleteProduct() {
        viewModel.deleteProduct(productToUpdate);
        viewModel.isProductDeleted().observe(getViewLifecycleOwner(), p -> {
            if (p) {
                Toast.makeText(getContext(), "Product Deleted Successfully.", Toast.LENGTH_SHORT).show();
                activity.finish();
            } else {
                Toast.makeText(getContext(), "Unable to Delete Product.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImages(Product product) {
//        This atomic integer is the indicator if all images are uploaded
//        alert dialog set title.
        alertDialog.setMessage("Uploading images...");

        AtomicInteger numberOfResult = new AtomicInteger();
        viewModel.saveNewProductImages(MerchantActivity.myBusiness.getOwnerId(), images);
        viewModel.getNewProductImagesResult().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                imagesLinksToSave.add(((Result.Success) result).getData().toString());
                numberOfResult.getAndIncrement();
                Toast.makeText(activity, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                if (numberOfResult.get() == images.size()) {
                    if (transactionType == TransactionType.EDIT) {
                        updateProduct(product);
                    } else {
                        saveNewproduct(product);
                    }
                }
            } else {
                showErrorAlert("Upload Image Failed", "Sorry, some image are failed to upload in our server. Try again later.");
                dismisSavingDialog();
            }
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

                holder.mImageView.setImageURI(uri);
                holder.mImageView.setOnClickListener(v -> {

                    //Image Viewr will show when Image are greater than 1
//                    because we set image 1 as null in order to show add button

                    if (imageUri.size() > 1) {
                        ArrayList<String> imageUriString = new ArrayList<>();
                        for (Uri uriStr : imageUri) {
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
                holder.deleteImage.setOnClickListener(v -> {
                    removeImage(position);
                });
            }


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


    enum TransactionType {
        CREATE, EDIT
    }
}
