package com.uds.urifia.smgenerator;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.uds.urifia.smgenerator.smanet.managers.EventManager;
import com.uds.urifia.smgenerator.smanet.managers.datastorage.EventDataSource;
import com.uds.urifia.smgenerator.smanet.managers.datastorage.SubscriptionDataSource;
import com.uds.urifia.smgenerator.smanet.model.Event;
import com.uds.urifia.smgenerator.smanet.model.Subject;
import com.uds.urifia.smgenerator.utils.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Publish extends AppCompatActivity implements View.OnClickListener {
    final String TAG = "PUBLISH ACTIVITY";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PICK_FILE_RESULT_CODE = 2;

    ImageButton btnUpload, btnCamera;
    MultiAutoCompleteTextView contentView;
    EditText descriptionEditText;
    CalendarView validityCalendar;

    Subject subject;
    long validity;
    File publishedFile = null;
    String description;
    String eventId;

    Date selectedDate = new Date();
    boolean rawContent = true; //The user type the message to publish by default

    SubscriptionDataSource subscriptionDataSource;
    EventDataSource eventDataSource;
    EventManager em;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        subject = (Subject) getIntent().getSerializableExtra("SUBJECT");

        subscriptionDataSource = new SubscriptionDataSource(getApplicationContext());
        subscriptionDataSource.open();
        eventDataSource = new EventDataSource(getApplicationContext());
        eventDataSource.open();
        em = EventManager.getInstance(getApplicationContext(), eventDataSource, subscriptionDataSource);

        final TextView textContent = findViewById(R.id.publish_content_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
        validityCalendar = findViewById(R.id.publish_date_calendar);
        contentView = findViewById(R.id.text_publish_content);
        btnUpload = findViewById(R.id.btn_upload_file);
        btnUpload.setOnClickListener(this);
        btnCamera = findViewById(R.id.btn_camera_capture);
        btnCamera.setOnClickListener(this);

        Switch switchView = findViewById(R.id.publish_hint_switch);
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    rawContent = false;
                    contentView.setVisibility(View.INVISIBLE);
                    textContent.setVisibility(View.INVISIBLE);
                    btnUpload.setVisibility(View.VISIBLE);
                    btnCamera.setVisibility(View.VISIBLE);
                } else {
                    rawContent = true;
                    contentView.setVisibility(View.VISIBLE);
                    textContent.setVisibility(View.VISIBLE);
                    btnUpload.setVisibility(View.INVISIBLE);
                    btnCamera.setVisibility(View.INVISIBLE);
                }
            }
        });

        validityCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate.setYear(year - 1900);
                selectedDate.setMonth(month);
                selectedDate.setDate(dayOfMonth);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.publish_options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_submit_publish) {
            selectedDate.setHours(23);
            selectedDate.setMinutes(59);
            selectedDate.setSeconds(59);
            validity = selectedDate.getTime();
            Date today = new Date();

            SimpleDateFormat formater = new SimpleDateFormat();
            Log.e("PUBLISH", formater.format(today) + " " + formater.format(selectedDate));

            if (selectedDate.before(today)){
                Toast.makeText(getApplicationContext(), "Date de validité invalide", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            }

            description = descriptionEditText.getText().toString().trim();
            if (description.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Aucune description", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            }

            if (rawContent) {
                if (publishedFile != null) {
                    publishedFile.delete();
                }

                String fileContent, content = contentView.getText().toString().trim();
                if (content.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Contenu vide", Toast.LENGTH_SHORT).show();
                }
                fileContent = Constants.fileContentHead + content + Constants.fileContentFoot;
                File root = getFilesDir();
                Log.e("FilesDir", root.toString());

                if (root.getFreeSpace() >  fileContent.length()) {
                    String dirPath = root.getAbsolutePath() + "/" + subject.getId().replaceAll("\\.", "/");
                    File dir = new File(dirPath);
                    eventId = generateId();

                    boolean stored = false;
                    if (dir.isDirectory() || dir.mkdirs()) {
                        String filename = eventId + ".html";
                        publishedFile = new File(dirPath + "/" + filename);
                        try {
                            if (publishedFile.createNewFile()) {
                                FileOutputStream out = new FileOutputStream(publishedFile);
                                out.write(fileContent.getBytes());
                                out.close();
                                stored = true;
                            }
                        } catch (IOException e) {
                            //ignore
                        }
                    }
                    if (stored) {
                        if (publish()){
                            Toast.makeText(getApplicationContext(), "Succès!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            publishedFile.delete();
                            Toast.makeText(getApplicationContext(), "Echec", Toast.LENGTH_SHORT).show();
                        }

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Espace insufisant", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (publishedFile == null) {
                    Toast.makeText(getApplicationContext(), "Aucun fichier à publier", Toast.LENGTH_SHORT).show();
                } else {
                    if (publish()){
                        Toast.makeText(getApplicationContext(), "Succès!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else {
                        publishedFile.delete();
                        Toast.makeText(getApplicationContext(), "Echec", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v instanceof ImageButton) {
            ImageButton btn = (ImageButton)v;
            if (btn == btnCamera) {
                dispatchTakePictureIntent();
            }
            else if (btn == btnUpload) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");

                String[] mimeTypes = {"image/*", "text/html", "text/plain", "application/pdf"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, PICK_FILE_RESULT_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case PICK_FILE_RESULT_CODE:
                if(resultCode==RESULT_OK){
                    Uri returnUri = data.getData();
                    handlePickFileResult(returnUri);
                }
                break;
        }
    }

    private void handlePickFileResult(Uri uri) {
        Cursor returnCursor =
                getContentResolver().query(uri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String fileName = returnCursor.getString(nameIndex);
        Long fileSize = returnCursor.getLong(sizeIndex);

        File root = getFilesDir();
        if (root.getFreeSpace() > fileSize) {
            if (isExternalStorageReadable()) {
                Log.e(TAG, "The name of the selected file is: "+fileName);
                if (publishedFile != null) {
                    publishedFile.delete();
                }
                String dirPath = root.getAbsolutePath() + "/" + subject.getId().replaceAll("\\.", "/");
                File dir = new File(dirPath);
                eventId = generateId();

                if (dir.isDirectory() || dir.mkdirs()) {
                    final String extention = fileName.substring(fileName.lastIndexOf("."));
                    String filename_ = eventId + extention;
                    publishedFile = new File(dirPath + "/" + filename_);
                    InputStream in = null;
                    OutputStream out = null;
                    try {
                        if (publishedFile.createNewFile()) {
                            Log.e(TAG, "File created: " + publishedFile.getAbsolutePath());
                            in = getContentResolver().openInputStream(uri);
                            out = new FileOutputStream(publishedFile);
                            byte[] offset = new byte[64];
                            while ((in.read(offset) > -1)) {
                                out.write(offset);
                            }
                        }
                    } catch (IOException e) {
                        //ignore
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

        } else {
            Toast.makeText(getApplicationContext(), "Espace insufisant", Toast.LENGTH_SHORT).show();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.e(TAG, "Loading the Camera...");
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            if (publishedFile != null) {
                publishedFile.delete();
            }
            File root = getFilesDir();
            String dirPath = root.getAbsolutePath() + "/" + subject.getId().replaceAll("\\.", "/");
            File dir = new File(dirPath);
            eventId = generateId();

            if (dir.isDirectory() || dir.mkdirs()) {
                Log.e(TAG, dirPath+ " exists");
                String filename = eventId + ".jpg";
                publishedFile = new File(dirPath + "/" + filename);
                try {
                    if (publishedFile.createNewFile()) {
                        Log.e(TAG, "File created: " + publishedFile.getAbsolutePath());
                    }
                } catch (IOException e) {
                    //ignore
                }
            }

            Log.e(TAG, "Camera is ready");
            if (publishedFile != null) {
                Log.e(TAG, "Starting the Camera..");
                Uri photoURI = FileProvider.getUriForFile(
                       this,
                       "com.uds.urifia.smgenerator.fileprovider",
                        publishedFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private String generateId() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        StringBuilder builder = new StringBuilder();
        double r;
        int INF = 10, SUP = 99, c;
        for(int i = 0; i<4; i++) {
            r = Math.random();
            c = new Double((SUP - INF) * r).intValue();
            builder.append(c);
        }
        String id = "SMG_" + timeStamp + "_" + builder.toString();

        return id;
    }

    private boolean publish() {
        Date today = new Date();
        Event e = new Event(eventId, subject.getId(), today, validity, description, publishedFile.getAbsolutePath());
        if (em.saveEvent(e) != null){
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

}
