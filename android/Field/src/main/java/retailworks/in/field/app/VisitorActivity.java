package retailworks.in.field.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import retailworks.in.field.R;
import retailworks.in.field.utils.Constants;

import retailworks.in.field.utils.SuperActivity;

public class VisitorActivity extends
		SuperActivity implements
        View.OnClickListener {

	private static final String LOGC = Constants.APP_TAG
			+ VisitorActivity.class.getSimpleName();

	private static String[] shopNames =
			{
					"Maha Bazaar", "Super Bazaar", "Nilgiris", "Total SuperBazaar", "HyperCity","Maha Bazaar",
					"Super Bazaar", "Nilgiris", "Total SuperBazaar", "HyperCity", "Maha Bazaar", "Super Bazaar",
					"Nilgiris", "Total SuperBazaar", "HyperCity",
			};


    private static String[] coNames =
            {
                    "RetailWorks", "ABC Corp", "AlphaBet"
            };

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_visitor);

		ArrayAdapter<String> storeAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,shopNames);
		storeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner stores = (Spinner) findViewById(R.id.storeSpinner);
		stores.setAdapter(storeAdapter);


        ArrayAdapter<String> coAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, coNames);
        coAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner co = (Spinner) findViewById(R.id.coSpinner);
        co.setAdapter(coAdapter);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
        //Toast.makeText(this, getString(R.string.back_pressed), Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_menu, menu);
		return true;
	}


    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.visitorTitle)
			;
        else if(view.getId() == R.id.visitor_cancel)
            finish();
        else if(view.getId() == R.id.visitorImageIn)
            dispatchTakePictureIntent(REQUEST_IN_IMAGE_CAPTURE);
        else if(view.getId() == R.id.visitorImageOut)
            dispatchTakePictureIntent(REQUEST_OUT_IMAGE_CAPTURE);
    }

    static final int REQUEST_IN_IMAGE_CAPTURE   = 1;
    static final int REQUEST_OUT_IMAGE_CAPTURE  = 2;

    private void dispatchTakePictureIntent(int reqCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, reqCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ImageButton imageView;
            if(requestCode == REQUEST_IN_IMAGE_CAPTURE)
                imageView = (ImageButton) this.findViewById(R.id.visitorImageIn);
            else //if(requestCode == REQUEST_OUT_IMAGE_CAPTURE)
                imageView = (ImageButton) this.findViewById(R.id.visitorImageOut);
            imageView.setImageBitmap(imageBitmap);
            imageView.setPadding(0,0,0,0);
        }
    }
}