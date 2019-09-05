package com.example.placearapp;

import com.google.ar.sceneform.ux.BaseTransformableNode;
import com.google.ar.sceneform.ux.RotationController;
import com.google.ar.sceneform.ux.TransformationSystem;
import com.google.ar.sceneform.ux.TranslationController;

public class Transformable extends BaseTransformableNode {
    private final TranslationController translationController;
    private final RotationController rotationController;

    @SuppressWarnings("initialization") // Suppress @UnderInitialization warning.
    public Transformable(TransformationSystem transformationSystem) {
        super(transformationSystem);

        translationController =
                new TranslationController(this, transformationSystem.getDragRecognizer());
        addTransformationController(translationController);

        rotationController = new RotationController(this, transformationSystem.getTwistRecognizer());
        addTransformationController(rotationController);

    }

    /** Returns the controller that translates this node using a drag gesture. */
    public TranslationController getTranslationController() {
        return translationController;
    }

    /** Returns the controller that rotates this node using a twist gesture. */
    public RotationController getRotationController() {
        return rotationController;
    }
}
