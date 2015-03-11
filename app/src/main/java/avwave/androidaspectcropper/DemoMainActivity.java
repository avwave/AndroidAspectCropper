package avwave.androidaspectcropper;

import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import avwave.androidaspectcropper.fragment.CropFragment;
import avwave.androidaspectcropper.fragment.PhotoPickerFragment;


public class DemoMainActivity extends ActionBarActivity
        implements PhotoPickerFragment.OnFragmentInteractionListener, CropFragment.OnFragmentInteractionListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PhotoPickerFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_demo_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCropImage(String pathToCroppedImage, Rect croppedRectArea) {

    }

    @Override
    public void onPhotoPicked(Uri uri, float targetWidth, float targetHeight, boolean isCropRect) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, CropFragment.newInstance(uri.toString(), targetWidth, targetHeight, isCropRect))
                .addToBackStack(null)
                .commit();
    }
}
