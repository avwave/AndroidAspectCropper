package avwave.androidaspectcropper.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import avwave.androidaspectcropper.R;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PhotoPickerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class PhotoPickerFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public PhotoPickerFragment() {
        // Required empty public constructor
    }

    private ImageView croppedView;
    public static String EXTRA_IMAGE_PATH = "image_path";
    public static String EXTRA_CROPPED_IMAGE_BYTE_ARRAY = "byte_array";

    private float targetHeight, targetWidth;
    private boolean isCropRect = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo_picker, container, false);

        croppedView = (ImageView)rootView.findViewById(R.id.croppedImage);

        rootView.findViewById(R.id.circleCropButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCropRect = false;
                targetHeight = targetWidth = 1.0f;
                photoPicker();
            }
        });

        rootView.findViewById(R.id.aspect1Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCropRect = true;
                targetHeight = 15.0f;
                targetWidth = 20.0f;
                photoPicker();
            }
        });

        rootView.findViewById(R.id.aspect2Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCropRect = true;
                targetHeight = 70.0f;
                targetWidth = 40.0f;
                photoPicker();
            }
        });

        rootView.findViewById(R.id.aspect3Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCropRect = true;
                targetHeight = targetWidth = 1.0f;
                photoPicker();
            }
        });

        return rootView;
    }

    private void photoPicker() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            Uri photoUri = intent.getData();
            if (photoUri != null) {
                mListener.onPhotoPicked(photoUri, targetWidth, targetHeight, isCropRect);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onPhotoPicked(Uri uri, float targetWidth, float targetHeight, boolean isCropRect);
    }

}
