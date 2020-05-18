package com.allandroidprojects.ecomsample.util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.models.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 9/18/2017.
 */

public class ChatMessageListAdapter extends ArrayAdapter<ChatMessage> {

    private static final String TAG = "ChatMessageListAdapter";

    private int mLayoutResource;
    private Context mContext;
    private FirebaseUser firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();

    public ChatMessageListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<ChatMessage> objects) {
        super(context, resource, objects);
        mContext = context;
        mLayoutResource = resource;
    }

    public static class ViewHolder{
        TextView message;
        CircleImageView mProfileImage;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mLayoutResource, parent, false);
            holder = new ViewHolder();

            holder.message = convertView.findViewById(R.id.message);
            holder.mProfileImage = convertView.findViewById(R.id.profile_image);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
            holder.message.setText("");
        }

        try{
            //set the message
            holder.message.setText(getItem(position).getMessage());


            //set the image (make sure to prevent the image 'flash')
            if (holder.mProfileImage.getTag() == null ||
                    !holder.mProfileImage.getTag().equals(getItem(position).getProfile_image())) {

                //we only load image if prev. URL and current URL do not match, or tag is null
                ImageLoader.getInstance().displayImage(getItem(position).getProfile_image(), holder.mProfileImage,
                        new SimpleImageLoadingListener());
                holder.mProfileImage.setTag(getItem(position).getProfile_image());
            }

        }catch (NullPointerException e){
            Log.e(TAG, "getView: NullPointerException: ", e.getCause() );
        }

        return convertView;
    }

}

















