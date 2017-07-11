package com.RMC.BDCloud.Android;

import android.content.Context;
import android.util.Log;

import com.RMC.BDCloud.RealmDB.Model.RMCCheckin;
import com.RMC.BDCloud.RealmDB.Model.RMCImageNames;
import com.RMC.BDCloud.RealmDB.Model.RMCImageUris;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by mayanksaini on 08/12/16.
 */

public class UploadImagesTask implements RequestCallback {

    private Context context;
    private String imageName;
    private AmazonS3 s3;
    private TransferUtility transferUtility;
    private Realm realm;
    private RequestCallback requestCallback;

    public UploadImagesTask(Context context, RequestCallback requestCallback) {
        this.requestCallback = requestCallback;
        this.context = context;
    }

    public void uploadToS3() {

        if (BDCloudUtils.DEBUG_LOG) {
            Log.d("UploadImagesTask", "Photo upload started");
        }
        realm = BDCloudUtils.getRealmBDCloudInstance();
        RealmQuery<RMCCheckin> photoQuery = realm.where(RMCCheckin.class).beginGroup()
                .equalTo("checkinType", "Photo").isNull("imageUrl").endGroup();


        RealmResults<RMCCheckin> photoResult = photoQuery.findAll();
        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = BDCloudUtils.getCredentialsProvider(context);

        // Create an S3 client
        s3 = BDCloudUtils.getS3(credentialsProvider);
        transferUtility = BDCloudUtils.getTransferService(context, s3);

        for (RMCCheckin checkin : photoResult) {
            RealmList<RMCImageUris> imageUri = checkin.getImageUri();
            RealmList<RMCImageNames> imageNames = checkin.getImageName();
            if (imageUri != null) {
                for (int i = 0; i < imageUri.size(); i++) {
                    File file = new File(imageUri.get(i).getImageUri());
                    imageName = imageNames.get(i).getImageName();
                    final TransferObserver observer = transferUtility.upload(
                            Constants.BUCKET_NAME,     /* The bucket to upload to */
                            imageName,    /* The key for the uploaded object */
                            file        /* The file where the data to upload exists */
                    );

                    observer.setTransferListener(new TransferListener() {
                        @Override
                        public void onStateChanged(int id, TransferState state) {
                            if (state.equals(TransferState.COMPLETED)) {

                                if (BDCloudUtils.INFO_LOG) {
                                    Log.i("UploadImagesTask", "uploadToS3 - Transfer complete " + observer.getKey());
                                }

                                JSONObject object = new JSONObject();
                                try {
                                    object.put("imageName", observer.getKey());
                                    onResponseReceived(object, true, "Successfully Uploaded", Constants.uploadPhotoCallback);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }


                        }

                        @Override
                        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                        }

                        @Override
                        public void onError(int id, Exception ex) {
                            ex.printStackTrace();
                        }
                    });

                }
            }

        }
        realm.close();
    }

    @Override
    public void onResponseReceived(JSONObject object, boolean status, String message, int type) {
        if (status == true) {
            if (BDCloudUtils.DEBUG_LOG) {
                Log.d("UploadImagesTask", "onResponseReceived");
            }
            String imageURI = null;
            realm = BDCloudUtils.getRealmBDCloudInstance();
            try {
                String imageName = object.getString("imageName");
                final String url = Constants.CLOUDFRONT_URL + imageName;
                final RealmResults<RMCCheckin> result = realm.where(RMCCheckin.class).equalTo("imageName.imageNames", imageName)
                        .findAll();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        result.get(0).setImageUrl(url);
                        //result.get(0).setCheckinType("Data");
                        realm.copyToRealmOrUpdate(result);
                    }
                });

                if (result != null) {
                    RealmList<RMCImageNames> imageNames = result.get(0).getImageName();

                    for (int j = 0; j < imageNames.size(); j++) {
                        if (imageNames.get(j).getImageName().equalsIgnoreCase(imageName)) {
                            imageURI = result.get(0).getImageUri().get(j).getImageUri();
                        }
                    }
                }

                if (requestCallback != null) {
                    requestCallback.onResponseReceived(null, true, imageURI, Constants.deleteImageCallback);
                }

                realm.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
