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

package com.google.firebase.samples.apps.mlkit.facedetection;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.google.android.gms.vision.CameraSource;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.samples.apps.mlkit.ChooserActivity;
import com.google.firebase.samples.apps.mlkit.GraphicOverlay;
import com.google.firebase.samples.apps.mlkit.GraphicOverlay.Graphic;
import com.google.firebase.samples.apps.mlkit.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.File;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
public class FaceGraphic extends Graphic {
  private static final float FACE_POSITION_RADIUS = 10.0f;
  private static final float ID_TEXT_SIZE = 40.0f;
  private static final float ID_Y_OFFSET = 50.0f;
  private static final float ID_X_OFFSET = -50.0f;
  private static final float BOX_STROKE_WIDTH = 5.0f;

  private static final int[] COLOR_CHOICES = {
    Color.BLUE //, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.RED, Color.WHITE, Color.YELLOW
  };
  private static int currentColorIndex = 0;

  private int facing;

  private final Paint facePositionPaint;
  private final Paint idPaint;
  private final Paint boxPaint;

  private volatile FirebaseVisionFace firebaseVisionFace;

  public FaceGraphic(GraphicOverlay overlay) {
    super(overlay);

    currentColorIndex = (currentColorIndex + 1) % COLOR_CHOICES.length;
    final int selectedColor = COLOR_CHOICES[currentColorIndex];

    facePositionPaint = new Paint();
    facePositionPaint.setColor(selectedColor);

    idPaint = new Paint();
    idPaint.setColor(selectedColor);
    idPaint.setTextSize(ID_TEXT_SIZE);

    boxPaint = new Paint();
    boxPaint.setColor(selectedColor);
    boxPaint.setStyle(Paint.Style.STROKE);
    boxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
  }

  /**
   * Updates the face instance from the detection of the most recent frame. Invalidates the relevant
   * portions of the overlay to trigger a redraw.
   */
  public void updateFace(FirebaseVisionFace face, int facing) {
    firebaseVisionFace = face;
    this.facing = facing;
    postInvalidate();
  }

  /** Draws the face annotations for position on the supplied canvas. */

  public void setImageToFace(Canvas canvas, FirebaseVisionFace face)
  {
    //toc

    //Kinh

    // Draws a circle at the position of the detected face, with the face's track id below.
    float x = translateX(face.getBoundingBox().centerX());
    float y = translateY(face.getBoundingBox().centerY());
    //canvas.drawCircle(x, y, FACE_POSITION_RADIUS, facePositionPaint);


    //canvas.drawText("id: " + face.getTrackingId(), x + ID_X_OFFSET, y + ID_Y_OFFSET, idPaint);
  /*  canvas.drawText(
        "happiness: " + String.format("%.2f", face.getSmilingProbability()),
        x + ID_X_OFFSET * 3,
        y - ID_Y_OFFSET,
        idPaint);*/
    if (facing == CameraSource.CAMERA_FACING_FRONT) {
/*      canvas.drawText(
          "right eye: " + String.format("%.2f", face.getRightEyeOpenProbability()),
          x - ID_X_OFFSET,
          y,
          idPaint);
      canvas.drawText(
          "left eye: " + String.format("%.2f", face.getLeftEyeOpenProbability()),
          x + ID_X_OFFSET * 6,
          y,
          idPaint);*/
    } else {
     /* canvas.drawText(
          "left eye: " + String.format("%.2f", face.getLeftEyeOpenProbability()),
          x - ID_X_OFFSET,
          y,
          idPaint);
      canvas.drawText(
          "right eye: " + String.format("%.2f", face.getRightEyeOpenProbability()),
          x + ID_X_OFFSET * 6,
          y,
          idPaint);*/
    }

    // Draws a bounding box around the face.
    float xOffset = scaleX(face.getBoundingBox().width() / 2.0f);
    float yOffset = scaleY(face.getBoundingBox().height() / 2.0f);
    float left = x - xOffset;
    float top = y - yOffset;
    float right = x + xOffset;
    float bottom = y + yOffset;

    if(true)
    {
      //Kinh

      int x_c = Math.abs((int) (right - left));
      int y_c = Math.abs((int)  (bottom - top));

      if(ChooserActivity._ac.getIdApple() == 0)
      {
        Bitmap resized = getBitmapGlasses(right, left, bottom, top, face);
        canvas.drawBitmap(resized, left + x_c / 13 - 12 , top + y_c / 5 , null);
      }
      if(ChooserActivity._ac.getIdCherry() == 1)
      {
        Bitmap resizedHair = getBitmapHair(right, left, bottom, top, face);
        canvas.drawBitmap(resizedHair, left + 10 , top - (int)(y_c *0.7) , null);
      }
      if(ChooserActivity._ac.getIdBanana() == 2)
      {
        Bitmap Necklace = getBitmapNecklace(right, left, bottom, top, face);
        canvas.drawBitmap(Necklace, left + (int)(x_c * 0.12) , top +(int) (y_c * 0.75)  , null);
      }
//Toc
    }


    //canvas.drawRect(left, top, right, bottom, boxPaint);
  }

  private Bitmap getBitmapGlasses(float right, float left, float bottom, float top,FirebaseVisionFace face)
  {
    BitmapFactory.Options optionsGlasses = new BitmapFactory.Options();
    optionsGlasses.inJustDecodeBounds = true;
    BitmapFactory.decodeResource(ChooserActivity._ac.getResources(),R.drawable.whitek, optionsGlasses);
    Bitmap bitmap1 = BitmapFactory.decodeResource(ChooserActivity._ac.getResources(),R.drawable.whitek);

    optionsGlasses.inSampleSize = (calculateInSampleSize(optionsGlasses, Math.abs((int) (right - left)), Math.abs((int) (bottom - top))));
    optionsGlasses.inJustDecodeBounds = false;
    int x_c = Math.abs((int) (right - left));
    int y_c = Math.abs((int)  (bottom - top));
    Bitmap bitmap5 =  BitmapFactory.decodeResource(ChooserActivity._ac.getResources(),R.drawable.whitek, optionsGlasses);


    float scaleWidth = ((float) (right - left) / optionsGlasses.outWidth);
    float scaleHeight = ((float)  (bottom - top) / optionsGlasses.outHeight);

    Matrix matrixGlasses = new Matrix();
    matrixGlasses.postRotate(face.getHeadEulerAngleZ());
    matrixGlasses.postScale(scaleWidth / 2, scaleHeight / 2);

    Bitmap bitmap6 = Bitmap.createBitmap(bitmap5, 0, 0, bitmap5.getWidth(), bitmap5.getHeight(), matrixGlasses, true);

    Bitmap resized = Bitmap.createScaledBitmap(bitmap6, x_c - 30 , y_c / 3 , true);
    return resized;


  }


  private Bitmap getBitmapNecklace(float right, float left, float bottom, float top,FirebaseVisionFace face)
  {
    BitmapFactory.Options optionsGlasses = new BitmapFactory.Options();
    optionsGlasses.inJustDecodeBounds = true;
    BitmapFactory.decodeResource(ChooserActivity._ac.getResources(),R.drawable.day_chuyen, optionsGlasses);
    Bitmap bitmap1 = BitmapFactory.decodeResource(ChooserActivity._ac.getResources(),R.drawable.day_chuyen);

    optionsGlasses.inSampleSize = (calculateInSampleSize(optionsGlasses, Math.abs((int) (right - left)), Math.abs((int) (bottom - top))));
    optionsGlasses.inJustDecodeBounds = false;
    int x_c = Math.abs((int) (right - left));
    int y_c = Math.abs((int)  (bottom - top));
    Bitmap bitmap5 =  BitmapFactory.decodeResource(ChooserActivity._ac.getResources(),R.drawable.day_chuyen, optionsGlasses);


    float scaleWidth = ((float) (right - left) / optionsGlasses.outWidth);
    float scaleHeight = ((float)  (bottom - top) / optionsGlasses.outHeight);

    Matrix matrixGlasses = new Matrix();
    matrixGlasses.postRotate(face.getHeadEulerAngleZ());
    matrixGlasses.postScale(scaleWidth / 2, scaleHeight / 2);

    Bitmap bitmap6 = Bitmap.createBitmap(bitmap5, 0, 0, bitmap5.getWidth(), bitmap5.getHeight(), matrixGlasses, true);

    Bitmap resized = Bitmap.createScaledBitmap(bitmap6, (int)(x_c * 0.75) , (int) (y_c / 2) , true);
    return resized;


  }

  private Bitmap getBitmapHair(float right, float left, float bottom, float top,FirebaseVisionFace face)
  {
    BitmapFactory.Options optionsHair = new BitmapFactory.Options();
    optionsHair.inJustDecodeBounds = true;
    BitmapFactory.decodeResource(ChooserActivity._ac.getResources(),R.drawable.toc, optionsHair);
    Bitmap bitmap1Hair = BitmapFactory.decodeResource(ChooserActivity._ac.getResources(),R.drawable.toc);


    optionsHair.inSampleSize = (calculateInSampleSize(optionsHair, Math.abs((int) (right - left)), Math.abs((int) (bottom - top))));
    optionsHair.inJustDecodeBounds = false;
    Bitmap bitmap5Hair =  BitmapFactory.decodeResource(ChooserActivity._ac.getResources(),R.drawable.toc, optionsHair);


    int x_c = Math.abs((int) (right - left));
    int y_c = Math.abs((int)  (bottom - top));

   float scaleWidth = ((float) (right - left) / optionsHair.outWidth);
    float scaleHeight = ((float)  (bottom - top) / optionsHair.outHeight);

    Matrix matrixHair = new Matrix();
    matrixHair.postRotate(face.getHeadEulerAngleZ());
    matrixHair.postScale(scaleWidth / 2, scaleHeight / 2);

    Bitmap bitmap6Hair = Bitmap.createBitmap(bitmap5Hair, 0, 0, bitmap5Hair.getWidth(), bitmap5Hair.getHeight(), matrixHair, true);

    Bitmap resizedHair = Bitmap.createScaledBitmap(bitmap6Hair, (int)(x_c*0.8)    , (int)(y_c *0.8 ) , true);

    return resizedHair;

  }

  @Override
  public void draw(Canvas canvas) {
    FirebaseVisionFace face = firebaseVisionFace;
    if (face == null) {
      return;
    }


    /*if(face.getTrackingId() == 0)
    {
      setImageToFace(canvas, face);
    }*/

    setImageToFace(canvas, face);

  }

  public static int calculateInSampleSize(BitmapFactory.Options options,
                                          int reqWidth, int reqHeight) {

    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {
      if (width > height) {
        inSampleSize = Math.round((float)height / (float)reqHeight);
      } else {
        inSampleSize = Math.round((float)width / (float)reqWidth);
      }
    }
    return inSampleSize;
  }

}
