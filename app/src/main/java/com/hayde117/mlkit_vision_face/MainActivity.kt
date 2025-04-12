package com.hayde117.mlkit_vision_face

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@ExperimentalGetImage
class MainActivity : AppCompatActivity() {


    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private lateinit var previewView: PreviewView
    private lateinit var textView: TextView
    private lateinit var faceBoundingBoxView: FaceBoundingBoxView
    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        // Do your own thing whether it success or not
    }
    val faceDetectorOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .enableTracking()
        .build()
    val detector = FaceDetection.getClient(faceDetectorOptions)
    val faceDetectionAnalyzer = ImageAnalysis.Analyzer { imageProxy ->
        val mediaImage = imageProxy.image
        if (mediaImage != null){
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            detector.process(image)
                .addOnSuccessListener { faces ->
                    textView.text = ""
                    faces.forEachIndexed { index, face ->
                        var faceInfoString = "Face $index"
                        val bounds = face.boundingBox
                        faceBoundingBoxView.setBoundingBox(bounds, imageProxy.width, imageProxy.height)
                        // Face is facing upward
                        val rotX = face.headEulerAngleX
                        faceInfoString += "\\nRotation X: $rotX (${if (rotX >= 0) "Facing Upward" else "Facing Down"})"
                        // Face is facing to the right of the camera
                        val rotY = face.headEulerAngleY
                        faceInfoString += "\\nRotation Y: $rotY (${if (rotY >= 0) "Facing Right" else "Facing Left"})"
                        // Face is rotated counter-clockwise relative to the camera
                        val rotZ = face.headEulerAngleZ
                        faceInfoString += "\\nRotation Z: $rotZ (${if (rotZ >= 0) "Rotation Counter-Clockwise" else "Facing Rotation Clockwise"})"
                        // Landmark
                        face.getLandmark(FaceLandmark.LEFT_EAR)?.let {}
                        // Contour
                        face.getContour(FaceContour.FACE)?.points?.let {}
                        // Classification
                        if (face.smilingProbability != null){
                            val smileProb = face.smilingProbability
                            faceInfoString += "\\nSmiling Probability: ${smileProb}"
                        }
                        if (face.leftEyeOpenProbability != null){
                            val leftEyeOpenProb = face.leftEyeOpenProbability
                            faceInfoString += "\\nLeft Eye Open Probability: ${leftEyeOpenProb}"
                        }
                        if (face.rightEyeOpenProbability != null){
                            val rightEyeOpenProb = face.rightEyeOpenProbability
                            faceInfoString += "\\nRight Eye Open Probability: ${rightEyeOpenProb}"
                        }
                        if (face.trackingId != null){
                            val id = face.trackingId
                        }
                        textView.text = faceInfoString
                    }
                    imageProxy.close()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        previewView = findViewById(R.id.previewView)
        textView = findViewById(R.id.textView)
        faceBoundingBoxView = findViewById(R.id.face_bounding_box_view)
        if (
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
            return
        }
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }
    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview : Preview = Preview.Builder().build()
        val cameraSelector : CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()
        val imageAnalysis = ImageAnalysis.Builder()
            .setResolutionSelector(
                ResolutionSelector.Builder()
                    .setResolutionStrategy(ResolutionStrategy.HIGHEST_AVAILABLE_STRATEGY)
                    .build()
            )
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        imageAnalysis.setAnalyzer(executor, faceDetectionAnalyzer)
        preview.setSurfaceProvider(previewView.surfaceProvider)
        var camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, imageAnalysis, preview)
    }


}