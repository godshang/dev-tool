//package com.github.godshang.devtool.page;
//
//import eu.mihosoft.monacofx.MonacoFX;
//import javafx.beans.value.ChangeListener;
//import javafx.scene.Node;
//
//public abstract class CodeEditorPage extends AbstractPage {
//
//    private MonacoFX monacoFX;
//
//    public CodeEditorPage() {
//        super();
//
//        monacoFX = new MonacoFX();
//    }
//
//    protected Node getEditor() {
//        return monacoFX;
//    }
//
//    protected String getEditorText() {
//        return monacoFX.getEditor().getDocument().getText();
//    }
//
//    protected void setEditorText(String text) {
//        monacoFX.getEditor().getDocument().setText(text);
//    }
//
//    protected void setEditorLanguage(String language) {
//        monacoFX.getEditor().setCurrentLanguage(language);
//    }
//
//    public void addEditorTextListener(ChangeListener<String> listener) {
//        monacoFX.getEditor().getDocument().textProperty().addListener(listener);
//    }
//}
