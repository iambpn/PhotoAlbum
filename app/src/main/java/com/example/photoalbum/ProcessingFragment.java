package com.example.photoalbum;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class ProcessingFragment extends Fragment {

    // the static fragment initialization parameters
    private static final String PHOTO_LOCATION = "photoLocation";
    private static final String PHOTO_NAME = "photoName";
    private static final String Encoding_Error = "Encoding Error";

    // Normal variable deceleration
    private String photoLocation;
    private String photoName;
    private String resultText;
    private RequestQueue requestQueue;
    private LinearLayout llProcessingLayout;
    private LinearLayout llResultLayout;
    private TextView result;
    private ImageButton ibPlayText;
    private SharedPreferences preferences;
    private RelativeLayout rlResultLayout;
    private ImageView imagePreview;
    private TextToSpeech tts;

    public ProcessingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static ProcessingFragment newInstance(PhotoData photoData) {
        ProcessingFragment fragment = new ProcessingFragment();
        Bundle args = new Bundle();
        args.putString(PHOTO_LOCATION, photoData.getPhotoLocation());
        args.putString(PHOTO_NAME, photoData.getPhotoName());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) { // check argument that is saved through args.putString()
            photoLocation = getArguments().getString(PHOTO_LOCATION);
            photoName = getArguments().getString(PHOTO_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_processing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imagePreview = view.findViewById(R.id.iv_image_preview);
        rlResultLayout = view.findViewById(R.id.rl_result_layout);
        preferences = ((MainActivity) getContext()).getSettingsSharedPreference();
        llProcessingLayout = view.findViewById(R.id.ll_processing_layout);
        llResultLayout = view.findViewById(R.id.ll_result_layout);
        ibPlayText = view.findViewById(R.id.ib_play_text);
        result = view.findViewById(R.id.tv_result);
        tts = ((MainActivity) getContext()).getTextToSpeech();

        // if generate description is false in settings then do not display description
        if (!preferences.getBoolean(SettingsFragment.GENERATE_DESCRIPTION_KEY, SettingsFragment.DEFAULT_GENERATE_DESCRIPTION)) {
            rlResultLayout.setVisibility(View.GONE);
        }
		else{

			Picasso.get().load(new File(this.photoLocation)).into(imagePreview); // using Picasso dependency to view image

			// request queue to server
			requestQueue = Volley.newRequestQueue((MainActivity) getContext()); // using volley dependency to send request to server

	// ------------- to do --------------------------
	//          // uncomment this after server is setup
	//        String server_Url = preferences.getString(SettingsFragment.SERVER_URL_KEY,SettingsFragment.DEFAULT_SERVER_URL);
	//        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_Url,
	//                this::showResponse, // method reference to showResponse
	//                error -> {
	//                    // if error while sending request
	//                    showResponse("Request to Server Failed. \nPlease check your connections");
	//                    Log.e("Volley error", "onErrorResponse: Please check your connections", error.getCause());
	//                }
	//        ) {
	//            @Override
	//            protected Map<String, String> getParams() throws AuthFailureError {
	//                // send params to server
	//                Map<String, String> params = new HashMap<>();
	//
	//                //generate unique file name
	//                // .jpg is added because image is converted to jpeg in getByteEncodedString function;
	//                String fileName = System.currentTimeMillis() + ".jpg";
	//                String data = getByteEncodedString(photoLocation);
	//                if (data == Encoding_Error) {
	//                    // error in encoding so do not send new params
	//                    return super.getParams();
	//                }
	//                params.put("encodedImage", data);
	//                return params;
	//            }
	//        };

	// ----------------for testing only --------------------
			String server_Url = "https://jsonplaceholder.typicode.com/todos/1";
			StringRequest stringRequest = new StringRequest(Request.Method.GET, server_Url,
					this::showResponse, // method reference to showResponse
					error -> {
						showResponse("Request to Server Failed. \nPlease check your connections");
						Log.e("Volley error", "onErrorResponse: Please check your connections", error.getCause());
					}
			);

			stringRequest.setTag("description"); // add tag to stringRequest
			stringRequest.setShouldCache(false); // set cache to false
			requestQueue.add(stringRequest); // add stringRequest to false

			// speak text when Image button is pressed
			ibPlayText.setOnClickListener(v -> {
				tts.speak(resultText, TextToSpeech.QUEUE_FLUSH, null, null);
			});
		}
    }

    private void showResponse(String responseText) {
        resultText = responseText; // save in resultText for onclick listener
        result.setText(responseText); // change text in text view

        // change UI
        llProcessingLayout.setVisibility(View.GONE);
        llResultLayout.setVisibility(View.VISIBLE);

        // check preference for reading description after server request
        if (preferences.getBoolean(SettingsFragment.READ_DESCRIPTION_KEY, SettingsFragment.DEFAULT_READ_DESCRIPTION)) {
            tts.speak(resultText, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    public String getByteEncodedString(String path) {
        try {
            // converting to bit map
            InputStream stream = ((MainActivity) getContext()).getContentResolver().openInputStream(Uri.fromFile(new File(path)));
            Bitmap bitmap = BitmapFactory.decodeStream(stream);

            // encoding bitmap image to string
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            return android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception ex) {
            Log.e("ImageEncoding", "getByteEncodedString: " + ex.getMessage(), ex);
            return Encoding_Error;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(this.photoName);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll("description");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tts.stop();
    }
}