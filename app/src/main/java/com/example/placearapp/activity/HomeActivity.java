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

import com.example.placearapp.R;
import com.example.placearapp.Transformable;
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
    private FragmentManager fragmentManager;
    private ArFragment arFragment;
    private SessionHandler session;
    private ModelRenderable modelRenderable;
    private String currentModelId;
    private List<Node> nodes = new ArrayList<>();
    private Button shopButton;
    private EditText modelNameText;
    private LinearLayout previewLinearLayout;
    private Fragment shopFragment;

    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
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
        fragmentManager = getSupportFragmentManager();
        shopFragment = fragmentManager.findFragmentById(R.id.shop_fragment);

        arFragment = (ArFragment) fragmentManager.findFragmentById(R.id.ux_fragment);
        fragmentManager.beginTransaction().show(fragmentManager.findFragmentById(R.id.ux_fragment))
                .hide(fragmentManager.findFragmentById(R.id.shop_fragment)).commit();

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

                    ImageView preview = addPreviewImage();
                    for (int i = 0; i < previewLinearLayout.getChildCount(); i++) {
                        ImageView otherPreview = (ImageView) previewLinearLayout.getChildAt(i);
                        otherPreview.setBackgroundResource(android.R.color.transparent);
                    }
                    preview.setBackgroundResource(R.drawable.preview_border);

                    modelTransformable.setOnTapListener((hitTestResult, motionEvent1) -> {
                        modelTransformable.select();
                        for (int i = 0; i < previewLinearLayout.getChildCount(); i++) {
                            ImageView otherPreview = (ImageView) previewLinearLayout.getChildAt(i);
                            otherPreview.setBackgroundResource(android.R.color.transparent);
                        }
                        preview.setBackgroundResource(R.drawable.preview_border);
                    });
                });

        shopButton = findViewById(R.id.shop_button);
        shopButton.setOnClickListener(view -> {
            showShopFragment();
        });
    }

    private ImageView addPreviewImage() {
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
            for (int i = 0; i < previewLinearLayout.getChildCount(); i++) {
                ImageView otherPreview = (ImageView) previewLinearLayout.getChildAt(i);
                otherPreview.setBackgroundResource(android.R.color.transparent);
            }
            preview.setBackgroundResource(R.drawable.preview_border);
        });
        return preview;
    }

    public void setModelRenderable(String id) {
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

    public void showShopFragment() {
        // First get FragmentManager object.
        Button addModelButton = findViewById(R.id.add_model);
        addModelButton.setEnabled(false);
        addModelButton.setVisibility(View.GONE);

        Button deleteButton = findViewById(R.id.delete_button);
        deleteButton.setEnabled(false);
        deleteButton.setVisibility(View.GONE);

        EditText editText = findViewById(R.id.model_name);
        editText.setEnabled(false);
        editText.setVisibility(View.GONE);

        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.in_from_left, R.anim.out_to_right)
                .show(shopFragment).commit();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.in_from_left, R.anim.out_to_right)
                .hide(arFragment).commit();
    }

    public void productSelected(String product_id) {
        Button addModelButton = findViewById(R.id.add_model);
        addModelButton.setEnabled(true);
        addModelButton.post(() -> addModelButton.setVisibility(View.VISIBLE));

        Button deleteButton = findViewById(R.id.delete_button);
        deleteButton.setEnabled(true);
        deleteButton.post(() -> deleteButton.setVisibility(View.VISIBLE));

        EditText editText = findViewById(R.id.model_name);
        editText.setEnabled(true);
        editText.post(() -> editText.setVisibility(View.VISIBLE));

        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left)
                .show(arFragment).commit();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left)
                .hide(shopFragment).commit();
        setModelRenderable("chair");

    }

    @Override
    public void onBackPressed() {
        if (!shopFragment.isHidden()) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left)
                    .hide(shopFragment).commit();
            fragmentManager.beginTransaction().
                    setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left)
                    .show(arFragment).commit();
        }
        else {
            super.onBackPressed();
        }
    }
}
