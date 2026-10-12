package com.borrowbox.app.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;

import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public final class ImageUtils {

    private static final int MAX_DIMENSION = 1280;
    private static final int JPEG_QUALITY = 80;

    private ImageUtils() {}

    public static File createTempCameraImageFile(Context context) {
        File storageDir = new File(context.getCacheDir(), "camera_cache");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        return new File(storageDir, "camera_" + System.currentTimeMillis() + ".jpg");
    }

    public static Uri getUriForFile(Context context, File file) {
        return FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".fileprovider",
                file
        );
    }

    public static String processAndSaveImage(Context context, Uri sourceUri, String subDirectory, String filePrefix) throws Exception {
        File directory = new File(context.getFilesDir(), subDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File destFile = new File(directory, filePrefix + "_" + System.currentTimeMillis() + ".jpg");

        File tempInput = null;
        File fileToRead;
        if ("file".equalsIgnoreCase(sourceUri.getScheme()) && sourceUri.getPath() != null) {
            fileToRead = new File(sourceUri.getPath());
        } else {
            tempInput = new File(context.getCacheDir(), "import_temp_" + System.currentTimeMillis() + ".tmp");
            try (InputStream in = context.getContentResolver().openInputStream(sourceUri);
                 FileOutputStream out = new FileOutputStream(tempInput)) {
                if (in == null) throw new Exception("Cannot open image input stream");
                byte[] buffer = new byte[8192];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }
            fileToRead = tempInput;
        }

        try {
            // 1. Decode bounds
            BitmapFactory.Options boundsOptions = new BitmapFactory.Options();
            boundsOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(fileToRead.getAbsolutePath(), boundsOptions);

            int origWidth = boundsOptions.outWidth;
            int origHeight = boundsOptions.outHeight;
            if (origWidth <= 0 || origHeight <= 0) {
                throw new Exception("Invalid image dimensions");
            }

            // Calculate sample size
            int inSampleSize = 1;
            while ((origWidth / inSampleSize) > MAX_DIMENSION * 1.5 || (origHeight / inSampleSize) > MAX_DIMENSION * 1.5) {
                inSampleSize *= 2;
            }

            // 2. Decode bitmap with downsampling
            BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
            decodeOptions.inSampleSize = inSampleSize;
            Bitmap bitmap = BitmapFactory.decodeFile(fileToRead.getAbsolutePath(), decodeOptions);
            if (bitmap == null) {
                throw new Exception("Failed to decode image");
            }

            // 3. Fix EXIF orientation
            int rotation = 0;
            try {
                ExifInterface exif = new ExifInterface(fileToRead.getAbsolutePath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotation = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotation = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotation = 270;
                        break;
                }
            } catch (Exception ignored) {}

            if (rotation != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);
                Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                if (rotated != bitmap) {
                    bitmap.recycle();
                    bitmap = rotated;
                }
            }

            // 4. Scale down if still larger than max dimension
            int currentWidth = bitmap.getWidth();
            int currentHeight = bitmap.getHeight();
            if (currentWidth > MAX_DIMENSION || currentHeight > MAX_DIMENSION) {
                float ratio = Math.min((float) MAX_DIMENSION / currentWidth, (float) MAX_DIMENSION / currentHeight);
                int targetWidth = Math.round(currentWidth * ratio);
                int targetHeight = Math.round(currentHeight * ratio);
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);
                if (scaled != bitmap) {
                    bitmap.recycle();
                    bitmap = scaled;
                }
            }

            // 5. Compress and write
            try (FileOutputStream out = new FileOutputStream(destFile)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out);
                out.flush();
            } finally {
                bitmap.recycle();
            }

            return destFile.getAbsolutePath();
        } finally {
            if (tempInput != null && tempInput.exists()) {
                tempInput.delete();
            }
        }
    }

    public static void deleteFileQuietly(String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            try {
                File file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    file.delete();
                }
            } catch (Exception ignored) {}
        }
    }

    public static File saveBitmapFromUri(Context context, Uri uri, String filename) {
        try {
            String path = processAndSaveImage(context, uri, "images", filename.replace(".jpg", ""));
            return new File(path);
        } catch (Exception e) {
            return null;
        }
    }
}
