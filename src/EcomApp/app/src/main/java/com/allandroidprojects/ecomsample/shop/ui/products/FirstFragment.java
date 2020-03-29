package com.allandroidprojects.ecomsample.shop.ui.products;

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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.model.product.Product;
import com.allandroidprojects.ecomsample.mvvm.factory.AddProductViewModelFactory;
import com.allandroidprojects.ecomsample.mvvm.view_model.AddProductViewModel;
import com.allandroidprojects.ecomsample.shop.ShopActivity;
import com.allandroidprojects.ecomsample.startup.data.Result;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.textfield.TextInputEditText;
import com.roger.catloadinglibrary.CatLoadingView;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static android.app.Activity.RESULT_OK;

public class FirstFragment extends Fragment {

    private AddProductActivity activity;
    private Button upload, save;
    private View root;
    private ArrayList<Uri> arrayList = new ArrayList<>();
    private int REQUEST_CODE_READ_STORAGE = 1;
    private RecyclerView.LayoutManager recylerViewLayoutManager;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private AddProductViewModel viewModel;
    private AutoCompleteTextView actcategory, actcondition, actWholeSale;
    private CatLoadingView mView;

    private String[] categories = new String[] {"Sweets", "Goods", "Clothing", "Books"};
    private String[] conditions = new String[] {"New", "Used"};
    private String[] wholeSale = new String[] {"Available", "Planned", "Not Available"};


    private TextInputEditText productName, productDescription, productPrice, productStock;



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
        return root;
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
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
                                arrayList.add(imageUri);
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
                            arrayList.add(uri);
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

    private void initializeViewModel(){
        this.viewModel = ViewModelProviders.of(activity, new AddProductViewModelFactory()).get(AddProductViewModel.class);
    }

    private void initializeComponents() {
        mView = new CatLoadingView();
        mView.setCanceledOnTouchOutside(false);

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
                saveNewProduct();
//                NavHostFragment.findNavController(FirstFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });

        recyclerView = (RecyclerView)root.findViewById(R.id.uploadedImage);
        recylerViewLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(recylerViewLayoutManager);
        adapter = new SimpleStringRecyclerViewAdapter(recyclerView, arrayList);
        recyclerView.setAdapter(adapter);
    }

    private void saveNewProduct() {
        mView.show(getParentFragmentManager(), "");
        ArrayList<String> links = new ArrayList<>();
        AtomicInteger numberOfResult = new AtomicInteger();
        viewModel.saveNewProductImages(ShopActivity.myBusiness.getUserId(), arrayList);
        viewModel.getNewProductImagesResult().observe(this, result -> {
            if (result instanceof Result.Success){
                links.add(((Result.Success) result).getData().toString());
                numberOfResult.getAndIncrement();
                Toast.makeText(activity, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                if(numberOfResult.get() == arrayList.size()){
                    saveProductInfo(links);
                }
            }else{
                numberOfResult.getAndIncrement();
                Toast.makeText(activity, "Failed to upload Image", Toast.LENGTH_SHORT).show();
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
//        product.setImageUrls(arrayList);
        product.setBusinessOwnerId(ShopActivity.myBusiness.getUserId());
//        String[] imageLinks = new String[links.size()];
//        for (int i=0; i<links.size();i++)
//            imageLinks[i] = links.get(i).toString();

        product.setImageUrls(links);
        viewModel.saveNewProduct(product);
        viewModel.getNewProductResult().observe(this, p -> {
            if (p instanceof Result.Success){
                Toast.makeText(getContext(), "Product Added Successfully.", Toast.LENGTH_SHORT).show();
                Product reference = (Product) ((Result.Success) p).getData();
//                saveImages();
            }else if (p instanceof Result.Error){
                Toast.makeText(getContext(), ((Result.Error) p).getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
            mView.dismiss();
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
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE_READ_STORAGE);
    }

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private ArrayList<Uri> imageUri;
        private RecyclerView mRecyclerView;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final SimpleDraweeView mImageView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (SimpleDraweeView) view.findViewById(R.id.image1);
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

        @Override
        public void onBindViewHolder(final SimpleStringRecyclerViewAdapter.ViewHolder holder, final int position) {
            final Uri uri = imageUri.get(position);
            holder.mImageView.setImageURI(uri);
        }

        @Override
        public int getItemCount() {
            return imageUri.size();
        }
    }
}
