package avwave.androidaspectcropper.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import avwave.androidaspectcrop.utils.DecodeUtils;
import avwave.androidaspectcrop.views.CropImageLayout;
import avwave.androidaspectcropper.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CropFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CropFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CropFragment extends Fragment {
    private static final String ARG_PHOTO_URI_STRING = "photoURIString";
    private static final String ARG_PHOTO_SIZE_HEIGHT = "photoSizeHeightFloat";
    private static final String ARG_PHOTO_SIZE_WIDTH = "photoSizeWidthFloat";
    private static final String ARG_IS_CROP_SHAPE_RECT = "isCropShapeRectBoolean";

    private Uri photoURI;

    private Bitmap bitmap;

    private float targetHeight, targetWidth;
    private boolean isCropRect = true;

    private CropImageLayout cropView;

    private OnFragmentInteractionListener mListener;

    /**
     *
     * @param uriString
     * @param targetWidth
     * @param targetHeight
     * @param isCropRect
     * @return
     */

    public static CropFragment newInstance(String uriString, float targetWidth, float targetHeight, boolean isCropRect) {
        CropFragment fragment = new CropFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PHOTO_URI_STRING, uriString);
        args.putFloat(ARG_PHOTO_SIZE_WIDTH, targetWidth);
        args.putFloat(ARG_PHOTO_SIZE_HEIGHT, targetHeight);
        args.putBoolean(ARG_IS_CROP_SHAPE_RECT, isCropRect);
        fragment.setArguments(args);
        return fragment;
    }

    public CropFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            photoURI = Uri.parse(getArguments().getString(ARG_PHOTO_URI_STRING));
            targetHeight = getArguments().getFloat(ARG_PHOTO_SIZE_HEIGHT);
            targetWidth = getArguments().getFloat(ARG_PHOTO_SIZE_WIDTH);
            isCropRect = getArguments().getBoolean(ARG_IS_CROP_SHAPE_RECT);

            final DisplayMetrics metrics = getResources().getDisplayMetrics();
            bitmap = DecodeUtils.decode(getActivity(), photoURI, metrics.widthPixels, metrics.heightPixels);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_crop, container, false);
        cropView = (CropImageLayout) rootView.findViewById(R.id.clip);
        if (null != bitmap) {
            cropView.setImageBitmap(bitmap);
            cropView.setCropDimensions(targetWidth, targetHeight, isCropRect);
            cropView.setSrcBitmap(photoURI, 72, (int)targetWidth, (int)targetHeight);

        }
        else {
            getFragmentManager().popBackStackImmediate();
        }

        rootView.findViewById(R.id.cropButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCrop();
            }
        });

        return rootView;
    }

    private void doCrop() {
        Bitmap b = cropView.clipOriginalImageAtURI(photoURI);
        AlertDialog.Builder alertadd = new AlertDialog.Builder(
                getActivity());
        alertadd.setTitle("Android");

        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View view = factory.inflate(R.layout.dialog_main, null);

        ImageView image= (ImageView) view.findViewById(R.id.imageView);
        image.setImageBitmap(b);

        alertadd.setView(view);
        alertadd.setNeutralButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int sumthin) {

            }
        });

        alertadd.show();
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
        // TODO: Update argument type and name
        public void onCropImage(String pathToCroppedImage, Rect croppedRectArea);
    }

}
