package com.example.placearapp.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.placearapp.R;
import com.example.placearapp.Transformable;
import com.example.placearapp.fragment.ShopFragment;
import com.example.placearapp.handler.SessionHandler;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Sun;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseTransformableNode;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;
    private ArFragment arFragment;
    private SessionHandler session;
    private ModelRenderable modelRenderable;
    private String currentModelId;
    private List<Node> nodes = new ArrayList<>();
    private Button shopButton;
    private EditText modelNameText;
    private LinearLayout previewLinearLayout;

    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "SceneFrm requires Android N or later");
            Toast.makeText(activity, "SceneForm requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "SceneForm requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "SceneForm requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this))
            return;

        setContentView(R.layout.activity_ux);

        session = new SessionHandler(getApplicationContext());
        modelNameText = findViewById(R.id.model_name);
        previewLinearLayout = findViewById(R.id.preview_linear_layout);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (modelRenderable == null) {
                        return;
                    }

                    // Create the Anchor.
                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    // Create the transformable modelTransformable and add it to the anchor.
                    Transformable modelTransformable = new Transformable(arFragment.getTransformationSystem());
                    modelTransformable.setParent(anchorNode);
                    modelTransformable.setRenderable(modelRenderable);
                    modelTransformable.select();
                    nodes.add(arFragment.getTransformationSystem().getSelectedNode());
                    addPreviewImage();
                });

        shopButton = findViewById(R.id.shop_button);
        shopButton.setOnClickListener(view -> {
            Fragment shopFragment = new ShopFragment();
            replaceFragment(shopFragment);
        });
    }

    private void addPreviewImage() {
        ImageView preview = new ImageView(this);
        previewLinearLayout.addView(preview);
        preview.setAdjustViewBounds(true);
        preview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                , LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, 0, 10, 0);
        preview.setLayoutParams(layoutParams);
        Drawable drawable = getDrawable(getResources().getIdentifier(currentModelId, "drawable", getPackageName()));
        preview.setImageDrawable(drawable);
        preview.setOnClickListener(view -> {
            int index = previewLinearLayout.indexOfChild(view);
            Node node = nodes.get(index);
            ((BaseTransformableNode) node).select();
        });
    }

    private void setModelRenderable(String id) {
        ModelRenderable.builder()
                .setSource(this, getResources().getIdentifier(id, "raw", getPackageName()))
                .build()
                .thenAccept(renderable -> modelRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });
        currentModelId = id;
    }

    public void changeModel(View v) {
        setModelRenderable(modelNameText.getText().toString());
    }


    public void onAccountButtonPressed(View v) {
        session.logoutUser();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }

    public void onDeleteButtonPressed(View v) {
        Node node = arFragment.getTransformationSystem().getSelectedNode();
        if (node.getParent() != null) {
            if (node instanceof AnchorNode) {
                if (((AnchorNode) node).getAnchor() != null) {
                    ((AnchorNode) node).getAnchor().detach();
                }
            }
            if (!(node instanceof Camera) && !(node instanceof Sun)) {
                node.setParent(null);
                int index = nodes.indexOf(node);
                nodes.remove(index);
                previewLinearLayout.removeViewAt(index);
            }
        }
    }

    public void replaceFragment(Fragment destFragment) {
        // First get FragmentManager object.
        FragmentManager fragmentManager = this.getSupportFragmentManager();

        // Begin Fragment transaction.
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace the layout holder with the required Fragment object.
        fragmentTransaction.replace(R.id.hf, destFragment);

        // Commit the Fragment replace action.
        fragmentTransaction.commit();
    }


}
