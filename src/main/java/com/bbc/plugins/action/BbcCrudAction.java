package com.bbc.plugins.action;

import com.bbc.plugins.ui.BbcCrud;
import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.*;

import java.awt.*;
import java.util.Arrays;

public class BbcCrudAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        Project project = anActionEvent.getProject();
        IdeView ideView = anActionEvent.getData(LangDataKeys.IDE_VIEW);
        PsiDirectory psiDirectory = ideView.getOrChooseDirectory();

        BbcCrud dialog = null;
        try {
            PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
            PsiJavaFile javaFile = (PsiJavaFile) psiFile;
            String basePath = javaFile.getPackageName().split("vo")[0];
            PsiClass psiClass = javaFile.getClasses()[0];
            PsiAnnotation[] annotations =  psiClass.getAnnotations();
            Boolean existsModelAnnotation = Arrays.stream(annotations).anyMatch(annotation -> annotation.getQualifiedName().contains("TransientModel")
                    || annotation.getQualifiedName().contains("io.terminus.trantorframework.api.annotation.TransientModel")
            );
            if(!existsModelAnnotation){
                Messages.showMessageDialog(project,"请选择VO类","生成CRUD提示", Messages.getWarningIcon());
                return;
            }
            dialog = new BbcCrud(basePath);
            dialog.setPsiClass(psiClass);
        }catch (Exception e){
            e.printStackTrace();
            Messages.showMessageDialog(project,"请选择VO类","生成CRUD异常", Messages.getErrorIcon());
        }
        dialog.setProject(project);
        dialog.setPsiDirectory(psiDirectory);
        dialog.setListener(e -> {
            String str = "public " + e.getActionCommand() + " methodName() {return null;}";
            WriteCommandAction.runWriteCommandAction(project, () -> {
                Editor editor = anActionEvent.getRequiredData(CommonDataKeys.EDITOR);
                CaretModel caretModel = editor.getCaretModel();
                int offset = caretModel.getOffset();
                editor.getDocument().insertString(offset, str);
            });
        });
        dialog.setTitle("确认包路径");
        dialog.setMinimumSize(new Dimension(660, 260));
        //dialog.setLocationRelativeTo(null);//居中
        dialog.setLocationRelativeTo(WindowManager.getInstance().getFrame(anActionEvent.getProject()));
        dialog.pack();
        dialog.setVisible(true);
    }
}
