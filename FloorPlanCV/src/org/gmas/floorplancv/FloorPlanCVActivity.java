package org.gmas.floorplancv;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Point;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

public class FloorPlanCVActivity extends Activity implements CvCameraViewListener2 {
    // Final variables (constants)
	private static final String TAG = "FloorPlanCV::Activity";
    private static final int VIEW_MODE_RGBA = 0;
    private static final int VIEW_MODE_THRESH = 1;
    private static final int VIEW_MODE_CANNY = 2;
    private static final int VIEW_MODE_LINES = 3;
    private static final int VIEW_MODE_CORNERS = 4;
    
    // Variables
    private int mViewMode; // For mode selection (RGB, Gray, Canny, Lines, Corners)
    private Mat mRgba; // RGB Matrix for storing frames
    private Mat mAux; // Auxiliar matrix
    private Mat mGray; // Grayscale matrix
    private Mat mThresh; // Threshold matrix
    private Mat mLines; // Lines matrix
    private Mat mCorners; // Corners matrix

    // Menu Item List
    private MenuItem mItemPreviewRGBA;
    private MenuItem mItemPreviewThresh;
    private MenuItem mItemPreviewCanny;
    private MenuItem mItemPreviewLines;
    private MenuItem mItemPreviewCorners;
    
    // Camera and OpenCV Interaction
    private CameraBridgeViewBase mOpenCvCameraView;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        // Initialization of OpenCV
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("mixed_sample");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    
    // Constructor
    public FloorPlanCVActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    // Create step of the activity's lifetime cycle. 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.floorplancv_surface_view);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.floorplancv_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    // Options Menu Create step. 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        mItemPreviewRGBA = menu.add("RGBA");
        mItemPreviewThresh = menu.add("Threshold");
        mItemPreviewCanny = menu.add("Canny");
        mItemPreviewLines = menu.add("Lines");
        mItemPreviewCorners = menu.add("Corners");
        return true;
    }
    
    // Pause step of the activity's lifetime cycle. 
    @Override
    public void onPause() {
        super.onPause();
        // Disables the camera
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
    
    // Resume step of the activity's lifetime cycle. 
    @Override
    public void onResume() {
    	super.onResume();
    	// Restarts the camera
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }
    
    // Destroy step of the activity's lifetime cycle.
    public void onDestroy() {
        super.onDestroy();
        // Disables the camera
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    // Camera starts. Create the matrices necessary.
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
        mThresh = new Mat(height, width, CvType.CV_8UC1);
        mAux = new Mat(height, width, CvType.CV_8UC4);
        //mLines = new Mat(height, width, CvType.CV_8UC1);
        mCorners = new Mat(height, width, CvType.CV_8UC1);
    }

    // Camera stops. Clean the matrices.
    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
        mThresh.release();
        mAux.release();
        //mLines.release();
        mCorners.release();
    }
    
    // Returns a frame from the Camera
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        final int viewMode = mViewMode;
        // Note: Matrix must return in RGBA.
        switch (viewMode) {
        case VIEW_MODE_RGBA:
            // Normal View. Frame returns as normal in RGBA.
            mRgba = inputFrame.rgba();
            break;
        case VIEW_MODE_THRESH:
            // Threshold.
            mGray = inputFrame.gray();
            // Threshold value at 110 looked the best so far.
            Imgproc.threshold(mGray, mThresh, 110, 255, Imgproc.THRESH_BINARY);
            Imgproc.cvtColor(mThresh, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
            break;
        case VIEW_MODE_CANNY:
            // Canny Filter.
            mRgba = inputFrame.rgba();
            mGray = inputFrame.gray();
            Imgproc.Canny(mGray, mAux, 80, 100);
            Imgproc.cvtColor(mAux, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
            break;
        case VIEW_MODE_LINES:
            // HoughLines applied to the Threshold Filtered Image
        	mRgba = inputFrame.rgba();
            mGray = inputFrame.gray();
            //Imgproc.threshold(mGray, mThresh, 110, 255, Imgproc.THRESH_BINARY);
            Imgproc.Canny(mGray, mThresh, 80, 100);
            Imgproc.HoughLines(mThresh, mLines = new Mat(), 1, Math.PI/180, 10);
            Log.i(TAG, "Rows = "+mLines.rows()+" Cols = "+mLines.cols());
            mLines.release();
        	
            Imgproc.cvtColor(mThresh, mRgba, Imgproc.COLOR_GRAY2BGRA);
            
            /*
            for(int i=0; i<mLines.cols(); i++) {
            	double[] vec = mLines.get(0, i);
            	Point[] points = new Point[2];
            	points[0] = new Point(vec[0], vec[1]);
            	points[1] = new Point(vec[2], vec[3]);
            	Core.line(mRgba, points[0], points[1], new Scalar(0,255, 0, 255),2);
            }
            */
            break;
        case VIEW_MODE_CORNERS:
            // input frame has RBGA format
            mRgba = inputFrame.rgba();
            break;
        }

        return mRgba;
    }

    // Select an item from the Menu
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);

        if (item == mItemPreviewRGBA) {
            mViewMode = VIEW_MODE_RGBA;
        } else if (item == mItemPreviewThresh) {
            mViewMode = VIEW_MODE_THRESH;
        } else if (item == mItemPreviewCanny) {
            mViewMode = VIEW_MODE_CANNY;
        } else if (item == mItemPreviewLines) {
        	mViewMode = VIEW_MODE_LINES;
        } else if (item == mItemPreviewCorners) {
        	mViewMode = VIEW_MODE_CORNERS;
        }

        return true;
    }

    //public native void FindFeatures(long matAddrGr, long matAddrRgba);
}
