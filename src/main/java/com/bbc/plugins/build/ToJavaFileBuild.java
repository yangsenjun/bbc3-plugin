package com.bbc.plugins.build;

import com.bbc.plugins.build.JavaFileBuild;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;

/**
 * @author 杨森君
 * @date 2023/6/7 13:27
 */
public class ToJavaFileBuild extends JavaFileBuild {

    @Override
    public void afterProcess(Project project, PsiClass po, PsiClass psiClass) {
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);

        WriteCommandAction.runWriteCommandAction(project, () -> {
                try {
                    for (PsiField psiField : po.getFields()) {
                        if (psiField.hasAnnotation("io.terminus.trantorframework.api.annotation.Field")) {
                            PsiField field = elementFactory.createField(psiField.getName(), psiField.getType());
                            field.getModifierList().setModifierProperty(PsiModifier.PRIVATE, true);

                            PsiElement firstChild = field.getModifierList().getFirstChild();

                            //增加注解
                            PsiAnnotation fieldAnnotation = psiField.getAnnotation("io.terminus.trantorframework.api.annotation.Field");
                            String nameValue = fieldAnnotation.findAttributeValue("name") == null ? field.getName() : fieldAnnotation.findAttributeValue("name").getText();
                            PsiAnnotation psiAnnotation = elementFactory.createAnnotationFromText("@ApiModelProperty(" + nameValue + ")", field);
                            field.getModifierList().addBefore(psiAnnotation, firstChild);

                            psiClass.add(field);
                        }
                    }
                }catch (Exception e){
                    Messages.showMessageDialog(project,e.getMessage(),"TO生成异常", Messages.getErrorIcon());
                }
        });
    }



}
