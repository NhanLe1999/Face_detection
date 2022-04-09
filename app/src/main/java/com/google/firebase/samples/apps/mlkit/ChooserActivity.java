// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.google.firebase.samples.apps.mlkit;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Demo app chooser which takes care of runtime permission requesting and allows you to pick from
 * all available testing Activities.
 */
public final class ChooserActivity extends AppCompatActivity
        implements OnRequestPermissionsResultCallback, AdapterView.OnItemClickListener {
    private static final String TAG = "ChooserActivity";
    private static final int PERMISSION_REQUESTS = 1;
    public Button startButton;

    private static final String textTrueCorrect = "how are you";

    public static ChooserActivity _ac;

    private static int IdApple = -1;
    private static int IdCherry = -1;
    private static int IdBanana = -1;

    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    private static final Class<?>[] CLASSES =
            new Class<?>[] {
                    LivePreviewActivity.class//, StillImageActivity.class,
            };

    private static final int[] DESCRIPTION_IDS =
            new int[] {
                    R.string.desc_camera_source_activity, R.string.desc_still_image_activity,
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        /*setContentView(R.layout.speak_to_text);

        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

        //getActionBar().hide();

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });*/
        setContenViewFace();

        //setContentView(R.layout.activity_chooser);
    }

    public static void PlayAudio(int id)
    {
        final MediaPlayer mp = MediaPlayer.create(ChooserActivity._ac, id);
        if(mp != null)
        {
            mp.start();
        }
    }

    public static void setIdForLearn(int id)
    {
        if(id == 0)
        {
            IdApple = 0;
        }else if(id == 1)
        {
            IdCherry = 1;
        }else if(id == 2)
        {
            IdBanana = 2;
        }
    }

    public static int getIdApple()
    {
        return IdApple;
    }

    public static int getIdCherry()
    {
        return IdCherry;
    }

    public static int getIdBanana()
    {
        return IdBanana;
    }

    private void setContenViewFace()
    {
        Class<?> clicked = CLASSES[0];
        //startActivity(new Intent( getApplicationContext(), clicked));
        setContentView(R.layout.activity_chooser);

        startButton = (Button) findViewById(R.id.start);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class<?> clicked = CLASSES[0];

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 2000ms
                        PlayAudio(R.raw.oudio_intro);
                    }
                }, 500);


                startActivity(new Intent( getApplicationContext(), clicked));
            }
        });

        // Set up ListView and Adapter
/*        ListView listView = (ListView) findViewById(R.id.test_activity_list_view);

        MyArrayAdapter adapter = new MyArrayAdapter(this, android.R.layout.simple_list_item_2, CLASSES);
        adapter.setDescriptionIds(DESCRIPTION_IDS);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);*/

        _ac = this;

        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        }
    }

    private String convertStringToLowerCase(String message)
    {
        char[] charArray = message.toCharArray();
        //sử dụng vòng lặp for để duyệt các phần tử trong charArray
        for(int i = 0; i < charArray.length; i++) {
            if(charArray[i] >= 65 && charArray[i] <= 90){
                charArray[i] += 32;
            }
        }
        // chuyển đổi mảng char thành string
        message = String.valueOf(charArray);

        return message;
    }

    private boolean compareText(String text1, String text2)
    {
        return text1.equals(text2);
    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    boolean isCorrect = false;

                    for(int i = 0; i < result.size(); i++)
                    {
                        String textSpeech = result.get(0);

                        textSpeech = convertStringToLowerCase(textSpeech);
                        isCorrect = compareText(textSpeech, textTrueCorrect);
                    }
                    if(isCorrect)
                    {
                        setContenViewFace();
                    }
                    txtSpeechInput.setText(result.get(0));
                }
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.camera_button_menu, menu);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Class<?> clicked = CLASSES[position];
        startActivity(new Intent(this, clicked));
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission granted: " + permission);
            return true;
        }
        Log.i(TAG, "Permission NOT granted: " + permission);
        return false;
    }

    private static class MyArrayAdapter extends ArrayAdapter<Class<?>> {

        private final Context context;
        private final Class<?>[] classes;
        private int[] descriptionIds;

        public MyArrayAdapter(Context context, int resource, Class<?>[] objects) {
            super(context, resource, objects);

            this.context = context;
            classes = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (convertView == null) {
                LayoutInflater inflater =
                        (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(android.R.layout.simple_list_item_2, null);
            }

//      ((TextView) view.findViewById(android.R.id.text1)).setText(classes[position].getSimpleName());
            ((TextView) view.findViewById(android.R.id.text1)).setText("START");
            ((TextView) view.findViewById(android.R.id.text2)).setText(descriptionIds[position]);

            return view;
        }

        public void setDescriptionIds(int[] descriptionIds) {
            this.descriptionIds = descriptionIds;
        }
    }
}
