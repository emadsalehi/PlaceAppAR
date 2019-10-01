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

    public TranslationController getTranslationController() {
        return translationController;
    }

    public RotationController getRotationController() {
        return rotationController;
    }
}
