// Copyright 2018 Google LLC
//
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

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.annotation.KeepName;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.samples.apps.mlkit.barcodescanning.BarcodeScanningProcessor;
import com.google.firebase.samples.apps.mlkit.custommodel.CustomImageClassifierProcessor;
import com.google.firebase.samples.apps.mlkit.facedetection.FaceDetectionProcessor;
import com.google.firebase.samples.apps.mlkit.imagelabeling.ImageLabelingProcessor;
import com.google.firebase.samples.apps.mlkit.textrecognition.TextRecognitionProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Demo app showing the various features of ML Kit for Firebase. This class is used to
 * set up continuous frame processing on frames from a camera source. */
@KeepName
public final class LivePreviewActivity extends AppCompatActivity
    implements OnRequestPermissionsResultCallback,
        OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener {
  private static final String FACE_DETECTION = "Face Detection";
  private static final String TEXT_DETECTION = "Text Detection";
  private static final String BARCODE_DETECTION = "Barcode Detection";
  private static final String IMAGE_LABEL_DETECTION = "Label Detection";
  private static final String CLASSIFICATION = "Classification";
  private static final String TAG = "LivePreviewActivity";
  private static final int PERMISSION_REQUESTS = 1;

  private CameraSource cameraSource = null;
  private CameraSourcePreview preview;
  private GraphicOverlay graphicOverlay;
  private String selectedModel = FACE_DETECTION;
  private ImageButton btnSpeak;
  private ImageView buttonAs;
  private ImageView imgVm;
  private ImageView imgK;
  private ImageView imgdc;
  private final int REQ_CODE_SPEECH_INPUT = 100;

  private static final String textTrueCorrect = "how are you";


  private static final String textTrueApple = "apple";
  private static final String textTrueCherry = "orange";
  private static final String textTrueBanana = "banana";
  private static final int IdApple = 0;
  private static final int IdCherry = 1;
  private static final int IdBanana = 2;
  private static TextView textViewAn;
  private static ImageView imgAs;
  private static int index = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    setContentView(R.layout.activity_live_preview);
  try {

    ImageView imageView = new ImageView(this);
    imageView.setImageResource(R.drawable.kinh);

    @SuppressLint("ResourceType") RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.layout.activity_live_preview);
    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
    );

    int kk = 0;
    layoutParams.addRule(RelativeLayout.BELOW, R.drawable.button);
    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

    relativeLayout.addView(imageView, layoutParams);

  } catch (Exception e) {
    e.printStackTrace();
  }


    btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

    buttonAs = (ImageView) findViewById(R.id.button_ans);

    imgVm = (ImageView) findViewById(R.id.idblackVM);
    imgK = (ImageView) findViewById(R.id.idblackK);
    imgdc = (ImageView) findViewById(R.id.idblackDc);


    if(buttonAs != null)
    {
      buttonAs.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View v) {
          //  imgbu.setImageResource(R.drawable.kinh);

          if(index == 0)
          {
            ChooserActivity._ac.PlayAudio(R.raw.tao);
          }

          if(index == 1)
          {
            ChooserActivity._ac.PlayAudio(R.raw.quacam);
          }

          if(index == 2)
          {
            ChooserActivity._ac.PlayAudio(R.raw.chuoi);
          }

        }
      });

    }
    textViewAn = (TextView) findViewById(R.id.text_ans);

    if(textViewAn != null)
    {
      textViewAn.setText("Apple");
    }

     imgAs = (ImageView) findViewById(R.id.img_ans);

    if(btnSpeak != null)
    {
      btnSpeak.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
        //  imgbu.setImageResource(R.drawable.kinh);

          promptSpeechInput();
        }
      });
    }



    preview = (CameraSourcePreview) findViewById(R.id.firePreview);
    if (preview == null) {
      Log.d(TAG, "Preview is null");
    }
    graphicOverlay = (GraphicOverlay) findViewById(R.id.fireFaceOverlay);
    if (graphicOverlay == null) {
      Log.d(TAG, "graphicOverlay is null");
    }

    Spinner spinner = (Spinner) findViewById(R.id.spinner);
    List<String> options = new ArrayList<>();
    options.add(FACE_DETECTION);
    options.add(TEXT_DETECTION);
    options.add(BARCODE_DETECTION);
    options.add(IMAGE_LABEL_DETECTION);
    options.add(CLASSIFICATION);
    // Creating adapter for spinner
    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_style, options);
    // Drop down layout style - list view with radio button
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // attaching data adapter to spinner

    if(dataAdapter != null && spinner != null)
    {
      spinner.setAdapter(dataAdapter);
      spinner.setOnItemSelectedListener(this);
    }

    ToggleButton facingSwitch = (ToggleButton) findViewById(R.id.facingswitch);
    facingSwitch.setOnCheckedChangeListener(this);

    if (allPermissionsGranted()) {
      createCameraSource(selectedModel);
    } else {
      getRuntimePermissions();
    }
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

          int isCorrect = -1;

          for(int i = 0; i < result.size(); i++)
          {
            String textSpeech = result.get(0);

            textSpeech = convertStringToLowerCase(textSpeech);
            isCorrect = compareText(textSpeech);
          }


          if(isCorrect == 0 && index == 0)
          {
            //PlayAudioTao

            final MediaPlayer mp = MediaPlayer.create(ChooserActivity._ac, R.raw.build_sentence);
            mp.start();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
              @Override
              public void run() {
                //Do something after 2000ms
                setImage(imgK, R.drawable.whitek);
                ChooserActivity._ac.setIdForLearn(0);
              }
            }, 500);

            handler.postDelayed(new Runnable() {
              @Override
              public void run() {
                //Do something after 2000ms
                index++;
                imgAs.setImageResource(R.drawable.cam);
                textViewAn.setText("Orange");
              }
            }, 2000);

          } else if(isCorrect == 1 && index == 1)
          {
            //Play audio cherry
            final MediaPlayer mp = MediaPlayer.create(ChooserActivity._ac, R.raw.build_sentence);
            mp.start();


            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
              @Override
              public void run() {
                //Do something after 2000ms
                setImage(imgVm, R.drawable.whitevm);
                ChooserActivity._ac.setIdForLearn(1);
              }
            }, 500);

            handler.postDelayed(new Runnable() {
              @Override
              public void run() {
                //Do something after 2000ms
                index++;
                imgAs.setImageResource(R.drawable.chuoi);
                textViewAn.setText("Banana");
              }
            }, 2000);



          } else if(isCorrect == 2 && index == 2)
          {
            index++;
            //Play audio banana
            setImage(imgdc, R.drawable.whitedc);
            ChooserActivity._ac.setIdForLearn(2);
            final MediaPlayer mp = MediaPlayer.create(ChooserActivity._ac, R.raw.build_sentence);
            mp.start();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
              @Override
              public void run() {

                ChooserActivity._ac.PlayAudio(R.raw.end_game);

                //Do something after 2000ms
              }
            }, 1000);
          }
          else{
              //playaudio sai
            ChooserActivity._ac.PlayAudio(R.raw.audio_sai);
          }
          //txtSpeechInput.setText(result.get(0));
        }
        break;
      }
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

  private int compareText(String text1)
  {

    if(text1.equals(textTrueApple))
    {
      return 0;
    }

    if(text1.equals(textTrueCherry))
    {
      return 1;
    }

    if(text1.equals(textTrueBanana))
    {
      return 2;
    }
    return -1;
  }


  @Override
  public synchronized void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    // An item was selected. You can retrieve the selected item using
    // parent.getItemAtPosition(pos)
    selectedModel = parent.getItemAtPosition(pos).toString();
    Log.d(TAG, "Selected model: " + selectedModel);
    preview.stop();
    if (allPermissionsGranted()) {
      createCameraSource(selectedModel);
      startCameraSource();
    } else {
      getRuntimePermissions();
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    // Do nothing.
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    Log.d(TAG, "Set facing");
    final MediaPlayer mp = MediaPlayer.create(ChooserActivity._ac, R.raw.build_sentence);
    mp.start();
    if (cameraSource != null) {
      if (isChecked) {
        cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
      } else {
        cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
      }
    }
    preview.stop();
    startCameraSource();
  }

  private void createCameraSource(String model) {
    // If there's no existing cameraSource, create one.
    if (cameraSource == null) {
      cameraSource = new CameraSource(this, graphicOverlay);
    }

    cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);

    try {
      switch (model) {
        case CLASSIFICATION:
          Log.i(TAG, "Using Custom Image Classifier Processor");
          cameraSource.setMachineLearningFrameProcessor(new CustomImageClassifierProcessor(this));
          break;
        case TEXT_DETECTION:
          Log.i(TAG, "Using Text Detector Processor");
          cameraSource.setMachineLearningFrameProcessor(new TextRecognitionProcessor());
          break;
        case FACE_DETECTION:
          Log.i(TAG, "Using Face Detector Processor");
          cameraSource.setMachineLearningFrameProcessor(new FaceDetectionProcessor());
          break;
        case BARCODE_DETECTION:
          Log.i(TAG, "Using Barcode Detector Processor");
          cameraSource.setMachineLearningFrameProcessor(new BarcodeScanningProcessor());
          break;
        case IMAGE_LABEL_DETECTION:
          Log.i(TAG, "Using Image Label Detector Processor");
          cameraSource.setMachineLearningFrameProcessor(new ImageLabelingProcessor());
          break;
        default:
          Log.e(TAG, "Unknown model: " + model);
      }
    } catch (FirebaseMLException e) {
      Log.e(TAG, "can not create camera source: " + model);
    }
  }

  /**
   * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
   * (e.g., because onResume was called before the camera source was created), this will be called
   * again when the camera source is created.
   */
  private void startCameraSource() {
    if (cameraSource != null) {
      try {
        if (preview == null) {
          Log.d(TAG, "resume: Preview is null");
        }
        if (graphicOverlay == null) {
          Log.d(TAG, "resume: graphOverlay is null");
        }
        preview.start(cameraSource, graphicOverlay);
      } catch (IOException e) {
        Log.e(TAG, "Unable to start camera source.", e);
        cameraSource.release();
        cameraSource = null;
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "onResume");
    startCameraSource();
  }

  /** Stops the camera. */
  @Override
  protected void onPause() {
    super.onPause();
    preview.stop();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (cameraSource != null) {
      cameraSource.release();
    }
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

  @Override
  public void onRequestPermissionsResult(
          int requestCode, String[] permissions, int[] grantResults) {
    Log.i(TAG, "Permission granted!");
    if (allPermissionsGranted()) {
      createCameraSource(selectedModel);
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  public static void setImage(ImageView img, int id)
  {
    img.setImageResource(id);
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
}
