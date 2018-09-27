package com.example.paul.photogalleryapp;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String IMAGES_DIRECTORY = Environment.DIRECTORY_PICTURES;
    public static final String CAPTIONS_DIRECTORY = Environment.DIRECTORY_DOCUMENTS;
    public static final String DATE_FORMAT_PATTERN = "yyyyMMdd";
    public static final int SEARCH_ACTIVITY_REQUEST_CODE = 0;
    static final int CAMERA_REQUEST_CODE = 1;
    private String currentPhotoPath = null;
    private String currentPhotoCaptionPath = null;
    private int currentPhotoIndex = 0;
    private ArrayList<String> photoGallery;
    private ArrayList<String> photoCaptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up buttons
        Button btnLeft = findViewById(R.id.btnLeft);
        Button btnRight = findViewById(R.id.btnRight);
        Button btnFilter = findViewById(R.id.btnFilter);
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        btnFilter.setOnClickListener(filterListener);

        // set up caption
        EditText photoCaptionEntry = findViewById(R.id.etPhotoCaption);
        photoCaptionEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newCaption = s.toString();
                updateCaption(newCaption, currentPhotoIndex);
            }
        });

        Date minDate = new Date(Long.MIN_VALUE);
        Date maxDate = new Date(Long.MAX_VALUE);
        photoCaptions = populateCaptions(minDate, maxDate);
        photoGallery = populateGallery(minDate, maxDate);
        Log.d("onCreate, size", Integer.toString(photoGallery.size()));
        if (photoGallery.size() > 0) {
            currentPhotoPath = photoGallery.get(currentPhotoIndex);
            currentPhotoCaptionPath = photoCaptions.get(currentPhotoIndex);
        }
        displayPhoto(currentPhotoPath);
        displayPhotoCaption(currentPhotoCaptionPath);
    }

    private void updateCaption(String newCaption, int currentPhotoIndex) {
        File captionFile = new File(photoCaptions.get(currentPhotoIndex));
        try {
            FileWriter writer = new FileWriter(captionFile);
            writer.write(newCaption);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener filterListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(MainActivity.this, SearchActivity.class);
            startActivityForResult(i, SEARCH_ACTIVITY_REQUEST_CODE);
        }
    };

    private ArrayList<String> populateGallery(Date minDate, Date maxDate) {
        // get list of images
        File imagesDir = getExternalFilesDir(IMAGES_DIRECTORY);

        photoGallery = new ArrayList<String>();

        assert imagesDir != null;

        // filter names
        SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT_PATTERN);

        // create list of file names
        File[] fList = imagesDir.listFiles();
        if (fList != null) {
            for (File f : imagesDir.listFiles()) {
                String fileName = f.getName();
                String fileNameDate = fileName.split("_")[1];
                Date fileDate;
                try {
                    fileDate = fmt.parse(fileNameDate);

                    if (fileDate.after(minDate) && fileDate.before(maxDate)) {
                        photoGallery.add(f.getPath());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return photoGallery;
    }

    private ArrayList<String> populateCaptions(Date minDate, Date maxDate) {
        // get list of captions
        File captionsDir = getExternalFilesDir(CAPTIONS_DIRECTORY);

        photoCaptions = new ArrayList<String>();

        assert captionsDir != null;

        // filter names
        SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT_PATTERN);

        // create list of captions
        File[] fList = captionsDir.listFiles();

        if (fList != null) {
            for (File f : captionsDir.listFiles()) {
                String fileName = f.getName();
                String fileNameDate = fileName.split("_")[1];
                Date fileDate;
                try {
                    fileDate = fmt.parse(fileNameDate);

                    if (fileDate.after(minDate) && fileDate.before(maxDate)) {
                        photoCaptions.add(f.getPath());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return photoCaptions;
    }

    private void displayPhoto(String path) {
        ImageView iv = findViewById(R.id.ivMain);
        iv.setImageBitmap(BitmapFactory.decodeFile(path));

        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd_HHmmss");
        TextView date = findViewById(R.id.photoTimestamp);
        try {
            String[] photoPathSplit = path.split("_");
            String photoDateUnformatted = photoPathSplit[1] + "_" + photoPathSplit[2];
            date.setText(fmt.parse(photoDateUnformatted).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayPhotoCaption(String path) {
        EditText photoCaptionEntry = findViewById(R.id.etPhotoCaption);
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String caption = br.readLine();
            photoCaptionEntry.setText(caption);
        } catch (FileNotFoundException e) {
            photoCaptionEntry.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onClick(View v) {
        // find new photo index based on button pressed
        switch (v.getId()) {
            case R.id.btnLeft:
                --currentPhotoIndex;
                break;
            case R.id.btnRight:
                ++currentPhotoIndex;
                break;
            default:
                break;
        }
        if (currentPhotoIndex < 0) {
            currentPhotoIndex = 0;
        }
        if (currentPhotoIndex >= photoGallery.size()) {
            currentPhotoIndex = photoGallery.size() - 1;
        }

        if (photoGallery.size() == 0) {
            return;
        }
        // get new photo and caption from index
        currentPhotoPath = photoGallery.get(currentPhotoIndex);
        currentPhotoCaptionPath = photoCaptions.get(currentPhotoIndex);

        Log.d("phpotoleft, size", Integer.toString(photoGallery.size()));
        Log.d("photoleft, index", Integer.toString(currentPhotoIndex));
        displayPhoto(currentPhotoPath);
        displayPhotoCaption(currentPhotoCaptionPath);
    }


    public void goToSettings(View v) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    public void goToDisplay(String x) {
        Intent i = new Intent(this, DisplayActivity.class);
        i.putExtra("DISPLAY_TEXT", x);
        startActivity(i);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("createImageFile", data.getStringExtra("STARTDATE"));
                Log.d("createImageFile", data.getStringExtra("ENDDATE"));

                String fromDateString = data.getStringExtra("STARTDATE");
                String toDateString = data.getStringExtra("ENDDATE");

                SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
                Date fromDate;
                Date toDate;
                try {
                    fromDate = fmt.parse(fromDateString);
                    toDate = fmt.parse(toDateString);


                } catch (ParseException e) {
                    fromDate = new Date(Long.MIN_VALUE);
                    toDate = new Date(Long.MAX_VALUE);
                }

                photoGallery = populateGallery(fromDate, toDate);
                photoCaptions = populateCaptions(fromDate, toDate);
                Log.d("onCreate, size", Integer.toString(photoGallery.size()));
                if (photoCaptions.size() > 0) {
                    currentPhotoIndex = 0;
                    currentPhotoPath = photoGallery.get(currentPhotoIndex);
                    currentPhotoCaptionPath = photoCaptions.get(currentPhotoIndex);
                    displayPhoto(currentPhotoPath);
                    displayPhotoCaption(currentPhotoCaptionPath);
                }
            }
        }
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("createImageFile", "Picture Taken");
                photoGallery = populateGallery(new Date(), new Date());
                photoCaptions = populateCaptions(new Date(), new Date());
                currentPhotoIndex = 0;
                currentPhotoPath = photoGallery.get(currentPhotoIndex);
                currentPhotoCaptionPath = photoCaptions.get(currentPhotoIndex);
                displayPhoto(currentPhotoPath);
                displayPhotoCaption(currentPhotoCaptionPath);
            }
        }
    }

    public void takePicture(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d("FileCreation", "Failed");
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.paul.photogalleryapp.pictures.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File picturesDir = getExternalFilesDir(IMAGES_DIRECTORY);
        File captionsDir = getExternalFilesDir(CAPTIONS_DIRECTORY);

        // create image file
        File image = File.createTempFile(imageFileName, ".jpg", picturesDir);
        currentPhotoPath = image.getAbsolutePath();
        Log.d("createImageFile", "image path: " + currentPhotoPath);

        // create empty caption file for image
        File caption = File.createTempFile(imageFileName, ".txt", captionsDir);
        return image;
    }


}
