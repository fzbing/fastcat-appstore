package com.fastspider.fastcat.cache;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;

/**
 * ͼƬ�������߰�
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ImageUtils {

	public final static String SDCARD_MNT = "/mnt/sdcard";
	public final static String SDCARD = "/sdcard";

	/** ������� */
	public static final int REQUEST_CODE_GETIMAGE_BYSDCARD = 0;
	/** ������� */
	public static final int REQUEST_CODE_GETIMAGE_BYCAMERA = 1;
	/** ����ü� */
	public static final int REQUEST_CODE_GETIMAGE_BYCROP = 2;

	/**
	 * дͼƬ�ļ� ��Androidϵͳ�У��ļ������� /data/data/PACKAGE_NAME/files Ŀ¼��
	 * 
	 * @throws IOException
	 */
	public static void saveImage(Context context, String fileName, Bitmap bitmap)
			throws IOException {
		saveImage(context, fileName, bitmap, 100);
	}

	public static void saveImage(Context context, String fileName,
			Bitmap bitmap, int quality) throws IOException {
		if (bitmap == null || fileName == null || context == null)
			return;

		FileOutputStream fos = context.openFileOutput(fileName,
				Context.MODE_PRIVATE);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, quality, stream);
		byte[] bytes = stream.toByteArray();
		fos.write(bytes);
		fos.close();
	}

	/**
	 * дͼƬ�ļ���SD��
	 * 
	 * @throws IOException
	 */
	public static void saveImageToSD(Context ctx, String filePath,
			Bitmap bitmap, int quality) throws IOException {
		if (bitmap != null) {
			File file = new File(filePath.substring(0,
					filePath.lastIndexOf(File.separator)));
			if (!file.exists()) {
				file.mkdirs();
			}
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(filePath));
			bitmap.compress(CompressFormat.JPEG, quality, bos);
			bos.flush();
			bos.close();
			if(ctx!=null){
				scanPhoto(ctx, filePath);
			}
		}
	}
	
	public static void saveBackgroundImage(Context ctx, String filePath, Bitmap bitmap, int quality) throws IOException{
		if (bitmap != null) {
			File file = new File(filePath.substring(0,
					filePath.lastIndexOf(File.separator)));
			if (!file.exists()) {
				file.mkdirs();
			}
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(filePath));
			bitmap.compress(CompressFormat.PNG, quality, bos);
			bos.flush();
			bos.close();
			if(ctx!=null){
				scanPhoto(ctx, filePath);
			}
		}
	}

	/**
	 * ��Gallery�������Ͽ�����ͼƬ
	 */
	private static void scanPhoto(Context ctx, String imgFileName) {
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File file = new File(imgFileName);
		Uri contentUri = Uri.fromFile(file);
		mediaScanIntent.setData(contentUri);
		ctx.sendBroadcast(mediaScanIntent);
	}

	/**
	 * ��ȡbitmap
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static Bitmap getBitmap(Context context, String fileName) {
		FileInputStream fis = null;
		Bitmap bitmap = null;
		try {
			fis = context.openFileInput(fileName);
			bitmap = BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return bitmap;
	}

	/**
	 * ��ȡbitmap
	 * 
	 * @param filePath
	 * @return
	 */
	public static Bitmap getBitmapByPath(String filePath) {
		return getBitmapByPath(filePath, null);
	}

	public static Bitmap getBitmapByPath(String filePath,
			BitmapFactory.Options opts) {
		FileInputStream fis = null;
		Bitmap bitmap = null;
		try {
			File file = new File(filePath);
			fis = new FileInputStream(file);
			bitmap = BitmapFactory.decodeStream(fis, null, opts);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return bitmap;
	}

	/**
	 * ��ȡbitmap
	 * 
	 * @param file
	 * @return
	 */
	public static Bitmap getBitmapByFile(File file) {
		FileInputStream fis = null;
		Bitmap bitmap = null;
		try {
			fis = new FileInputStream(file);
			bitmap = BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return bitmap;
	}

	/**
	 * ʹ�õ�ǰʱ���ƴ��һ��Ψһ���ļ���
	 * 
	 * @param format
	 * @return
	 */
	public static String getTempFileName() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SS");
		String fileName = format.format(new Timestamp(System
				.currentTimeMillis()));
		return fileName;
	}

	/**
	 * ��ȡ�����ʹ�õ�Ŀ¼
	 * 
	 * @return
	 */
	public static String getCamerPath() {
		return Environment.getExternalStorageDirectory() + File.separator
				+ "FounderNews" + File.separator;
	}

	/**
	 * �жϵ�ǰUrl�Ƿ��׼��content://��ʽ������ǣ��򷵻ؾ��·��
	 * 
	 * @param uri
	 * @return
	 */
	public static String getAbsolutePathFromNoStandardUri(Uri mUri) {
		String filePath = null;

		String mUriString = mUri.toString();
		mUriString = Uri.decode(mUriString);

		String pre1 = "file://" + SDCARD + File.separator;
		String pre2 = "file://" + SDCARD_MNT + File.separator;

		if (mUriString.startsWith(pre1)) {
			filePath = Environment.getExternalStorageDirectory().getPath()
					+ File.separator + mUriString.substring(pre1.length());
		} else if (mUriString.startsWith(pre2)) {
			filePath = Environment.getExternalStorageDirectory().getPath()
					+ File.separator + mUriString.substring(pre2.length());
		}
		return filePath;
	}

	/**
	 * ͨ��uri��ȡ�ļ��ľ��·��
	 * 
	 * @param uri
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getAbsoluteImagePath(Activity context, Uri uri) {
		String imagePath = "";
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.managedQuery(uri, proj, // Which columns to
														// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)

		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			if (cursor.getCount() > 0 && cursor.moveToFirst()) {
				imagePath = cursor.getString(column_index);
			}
		}

		return imagePath;
	}

	/**
	 * ��ȡͼƬ����ͼ ֻ��Android2.1���ϰ汾֧��
	 * 
	 * @param imgName
	 * @param kind
	 *            MediaStore.Images.Thumbnails.MICRO_KIND
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Bitmap loadImgThumbnail(Activity context, String imgName,
			int kind) {
		Bitmap bitmap = null;

		String[] proj = { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DISPLAY_NAME };

		Cursor cursor = context.managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj,
				MediaStore.Images.Media.DISPLAY_NAME + "='" + imgName + "'",
				null, null);

		if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
			ContentResolver crThumb = context.getContentResolver();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			bitmap = MethodsCompat.getThumbnail(crThumb, cursor.getInt(0),
					kind, options);
		}
		return bitmap;
	}

	public static Bitmap loadImgThumbnail(String filePath, int w, int h) {
		Bitmap bitmap = getBitmapByPath(filePath);
		return zoomBitmap(bitmap, w, h);
	}

	/**
	 * ��ȡSD��������ͼƬ·��
	 * 
	 * @return
	 */
	@SuppressWarnings({ "deprecation", "unused" })
	public static String getLatestImage(Activity context) {
		String latestImage = null;
		String[] items = { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DATA };
		Cursor cursor = context.managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, items, null,
				null, MediaStore.Images.Media._ID + " desc");

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				latestImage = cursor.getString(1);
				break;
			}
		}

		return latestImage;
	}

	/**
	 * ��������ͼƬ�Ŀ��
	 * 
	 * @param img_size
	 * @param square_size
	 * @return
	 */
	public static int[] scaleImageSize(int[] img_size, int square_size) {
		if (img_size[0] <= square_size && img_size[1] <= square_size)
			return img_size;
		double ratio = square_size
				/ (double) Math.max(img_size[0], img_size[1]);
		return new int[] { (int) (img_size[0] * ratio),
				(int) (img_size[1] * ratio) };
	}

	/**
	 * ��������ͼ
	 * 
	 * @param context
	 * @param largeImagePath
	 *            ԭʼ��ͼ·��
	 * @param thumbfilePath
	 *            �������ͼ·��
	 * @param square_size
	 *            ���ͼƬ���
	 * @param quality
	 *            ���ͼƬ����
	 * @throws IOException
	 */
	public static void createImageThumbnail(Context context,
			String largeImagePath, String thumbfilePath, int square_size,
			int quality) throws IOException {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 1;
		// ԭʼͼƬbitmap
		Bitmap cur_bitmap = getBitmapByPath(largeImagePath, opts);

		if (cur_bitmap == null)
			return;

		// ԭʼͼƬ�ĸ߿�
		int[] cur_img_size = new int[] { cur_bitmap.getWidth(),
				cur_bitmap.getHeight() };
		// ����ԭʼͼƬ���ź�Ŀ��
		int[] new_img_size = scaleImageSize(cur_img_size, square_size);
		// ������ź��bitmap
		Bitmap thb_bitmap = zoomBitmap(cur_bitmap, new_img_size[0],
				new_img_size[1]);
		// ������ź��ͼƬ�ļ�
		saveImageToSD(null,thumbfilePath, thb_bitmap, quality);
	}

	/**
	 * �Ŵ���СͼƬ
	 * 
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bgimage, double newWidth, double newHeight) {
		
		// ��ȡ���ͼƬ�Ŀ�͸� 
        float width = bgimage.getWidth(); 
        float height = bgimage.getHeight(); 
        // ��������ͼƬ�õ�matrix���� 
        Matrix matrix = new Matrix(); 
        // ������������ 
        float scaleWidth = ((float) newWidth) / width; 
        float scaleHeight = ((float) newHeight) / height; 
        // ����ͼƬ���� 
        matrix.postScale(scaleWidth, scaleHeight); 
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, 
                        (int) height, matrix, true); 
        return bitmap; 
	}

	public static Bitmap scaleBitmap(Bitmap bitmap) {
		// ��ȡ���ͼƬ�Ŀ�͸�
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// ����Ԥת���ɵ�ͼƬ�Ŀ�Ⱥ͸߶�
		int newWidth = 200;
		int newHeight = 200;
		// ���������ʣ��³ߴ��ԭʼ�ߴ�
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// ��������ͼƬ�õ�matrix����
		Matrix matrix = new Matrix();
		// ����ͼƬ����
		matrix.postScale(scaleWidth, scaleHeight);
		// ��תͼƬ ����
		// matrix.postRotate(45);
		// �����µ�ͼƬ
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return resizedBitmap;
	}

	/**
	 * (����)�ػ�ͼƬ
	 * 
	 * @param context
	 *            Activity
	 * @param bitmap
	 * @return
	 */
	public static Bitmap reDrawBitMap(Activity context, Bitmap bitmap) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int rHeight = dm.heightPixels;
		int rWidth = dm.widthPixels;
		int height = bitmap.getHeight();
		int width = bitmap.getWidth();
		float zoomScale;
		/** ��ʽ1 **/
		// if(rWidth/rHeight>width/height){//�Ը�Ϊ׼
		// zoomScale=((float) rHeight) / height;
		// }else{
		// //if(rWidth/rHeight<width/height)//�Կ�Ϊ׼
		// zoomScale=((float) rWidth) / width;
		// }
		/** ��ʽ2 **/
		// if(width*1.5 >= height) {//�Կ�Ϊ׼
		// if(width >= rWidth)
		// zoomScale = ((float) rWidth) / width;
		// else
		// zoomScale = 1.0f;
		// }else {//�Ը�Ϊ׼
		// if(height >= rHeight)
		// zoomScale = ((float) rHeight) / height;
		// else
		// zoomScale = 1.0f;
		// }
		/** ��ʽ3 **/
		if (width >= rWidth)
			zoomScale = ((float) rWidth) / width;
		else
			zoomScale = 1.0f;
		// ��������ͼƬ�õ�matrix����
		Matrix matrix = new Matrix();
		// ����ͼƬ����
		matrix.postScale(zoomScale, zoomScale);
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * ��Drawableת��ΪBitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;

	}

	/**
	 * ���Բ��ͼƬ�ķ���
	 * 
	 * @param bitmap
	 * @param roundPx
	 *            һ�����14
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * ��ô�Ӱ��ͼƬ����
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}

	/**
	 * ��bitmapת��Ϊdrawable
	 * 
	 * @param bitmap
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Drawable bitmapToDrawable(Bitmap bitmap) {
		Drawable drawable = new BitmapDrawable(bitmap);
		return drawable;
	}

	/**
	 * ��ȡͼƬ����
	 * 
	 * @param file
	 * @return
	 */
	public static String getImageType(File file) {
		if (file == null || !file.exists()) {
			return null;
		}
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			String type = getImageType(in);
			return type;
		} catch (IOException e) {
			return null;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * ��ȡͼƬ��������Ϣ
	 * 
	 * @param in
	 * @return
	 * @see #getImageType(byte[])
	 */
	public static String getImageType(InputStream in) {
		if (in == null) {
			return null;
		}
		try {
			byte[] bytes = new byte[8];
			in.read(bytes);
			return getImageType(bytes);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * ��ȡͼƬ��������Ϣ
	 * 
	 * @param bytes
	 *            2~8 byte at beginning of the image file
	 * @return image mimetype or null if the file is not image
	 */
	public static String getImageType(byte[] bytes) {
		if (isJPEG(bytes)) {
			return "image/jpeg";
		}
		if (isGIF(bytes)) {
			return "image/gif";
		}
		if (isPNG(bytes)) {
			return "image/png";
		}
		if (isBMP(bytes)) {
			return "application/x-bmp";
		}
		return null;
	}

	private static boolean isJPEG(byte[] b) {
		if (b.length < 2) {
			return false;
		}
		return (b[0] == (byte) 0xFF) && (b[1] == (byte) 0xD8);
	}

	private static boolean isGIF(byte[] b) {
		if (b.length < 6) {
			return false;
		}
		return b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8'
				&& (b[4] == '7' || b[4] == '9') && b[5] == 'a';
	}

	private static boolean isPNG(byte[] b) {
		if (b.length < 8) {
			return false;
		}
		return (b[0] == (byte) 137 && b[1] == (byte) 80 && b[2] == (byte) 78
				&& b[3] == (byte) 71 && b[4] == (byte) 13 && b[5] == (byte) 10
				&& b[6] == (byte) 26 && b[7] == (byte) 10);
	}

	private static boolean isBMP(byte[] b) {
		if (b.length < 2) {
			return false;
		}
		return (b[0] == 0x42) && (b[1] == 0x4d);
	}
}