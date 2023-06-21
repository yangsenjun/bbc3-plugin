package com.bbc.plugins.ui;

import com.bbc.plugins.build.JavaFileBuild;
import com.bbc.plugins.build.ToJavaFileBuild;
import com.bbc.plugins.utils.CommonUtil;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiJavaFile;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author 杨森君
 * @date 2023/6/7 10:06
 */
public class BbcCrud extends JDialog {
    private JPanel pane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField VOConverterPath;
    private JTextField QueryRequestPath;
    private JTextField UpdateRequestPath;
    private JTextField CreateRequestPath;
    private JLabel QueryRequest;
    private JLabel UpdateRequest;
    private JPanel contentPanel;
    private JLabel CreateRequest;
    private JTextField ReadFacadePath;
    private JTextField WriteFacadePath;
    private JLabel ReadFacade;
    private JLabel WriteFacade;
    private JLabel CreateAction;
    private JTextField CreateActionPath;
    private JTextField UpdateActionPath;
    private JLabel UpdateAction;
    private JLabel DeleteAction;
    private JTextField DeleteActionPath;
    private JLabel DetailAction;
    private JTextField DetailActionPath;
    private JTextField PagingActionPath;
    private JLabel PagingAction;
    private JTextField ToPath;
    private JLabel TO;
    private JLabel VOConverter;
    private Project project;
    private PsiClass psiClass;
    private PsiDirectory psiDirectory;

    private JavaFileBuild javaFileBuild;
    private ToJavaFileBuild toJavaFileBuild;

    public BbcCrud(String basePath) {
        setContentPane(contentPanel);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        javaFileBuild = new JavaFileBuild();
        toJavaFileBuild = new ToJavaFileBuild();

        String domainPath = basePath.replace("app.bbc","domain");

        ToPath.setText(domainPath + "model");
        VOConverterPath.setText(basePath + "converter");
        QueryRequestPath.setText(domainPath + "request");
        UpdateRequestPath.setText(domainPath + "request");
        CreateRequestPath.setText(domainPath + "request");
        ReadFacadePath.setText(domainPath + "facade");
        WriteFacadePath.setText(domainPath + "facade");

        CreateActionPath.setText(basePath + "action");
        UpdateActionPath.setText(basePath + "action");
        DeleteActionPath.setText(basePath + "action");
        DetailActionPath.setText(basePath + "action");
        PagingActionPath.setText(basePath + "action");

        buttonOK.addActionListener(e -> {
            e.getSource();
            onOK();
        });

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        pane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        ActionListener l = e -> {
            dispose();
            if (listener != null) {
                listener.actionPerformed(e);
            }
        };
    }

    private ActionListener listener;

    public void setListener(ActionListener listener) {
        this.listener = listener;
    }

    private void onOK() {
        // add your code here

        Map<String, String> pathMap = initMap();
        //反馈信息
        StringBuilder msg = new StringBuilder();

        String moduleName = ModuleUtil.findModuleForFile(psiClass.getContext().getContainingFile().getVirtualFile(),project).getName();

        javaFileBuild.execute(project, psiClass, VOConverterPath.getText(), pathMap, moduleName, "VOConverter", msg);
        javaFileBuild.execute(project, psiClass, CreateActionPath.getText(), pathMap, moduleName, "CreateAction", msg);
        javaFileBuild.execute(project, psiClass, UpdateActionPath.getText(), pathMap, moduleName, "UpdateAction", msg);
        javaFileBuild.execute(project, psiClass, DeleteActionPath.getText(), pathMap, moduleName, "DeleteAction", msg);
        javaFileBuild.execute(project, psiClass, DetailActionPath.getText(), pathMap, moduleName, "DetailAction", msg);
        javaFileBuild.execute(project, psiClass, PagingActionPath.getText(), pathMap, moduleName, "PagingAction", msg);
        javaFileBuild.execute(project, psiClass, CreateActionPath.getText()+".impl", pathMap, moduleName, "CreateActionImpl", msg);
        javaFileBuild.execute(project, psiClass, UpdateActionPath.getText()+".impl", pathMap, moduleName, "UpdateActionImpl", msg);
        javaFileBuild.execute(project, psiClass, DeleteActionPath.getText()+".impl", pathMap, moduleName, "DeleteActionImpl", msg);
        javaFileBuild.execute(project, psiClass, DetailActionPath.getText()+".impl", pathMap, moduleName, "DetailActionImpl", msg);
        javaFileBuild.execute(project, psiClass, PagingActionPath.getText()+".impl", pathMap, moduleName, "PagingActionImpl", msg);

        if (!StringUtils.isEmpty(msg.toString())) {
            Messages.showMessageDialog(project, msg.toString(), "CRUD构建", Messages.getInformationIcon());
        }

        dispose();
    }

    private Map<String, String> initMap() {

        PsiJavaFile psiJavaFile = (PsiJavaFile) psiClass.getContainingFile();

        Map<String, String> map = new HashMap<>(16);
        map.put("toPath", ToPath.getText());
        map.put("voPath", psiJavaFile.getPackageName());
        map.put("converterVOPath", VOConverterPath.getText());
        map.put("createRequestPath", CreateRequestPath.getText());
        map.put("queryRequestPath", QueryRequestPath.getText());
        map.put("readFacadePath", ReadFacadePath.getText());
        map.put("updateRequestPath", UpdateRequestPath.getText());
        map.put("writeFacadePath", WriteFacadePath.getText());
        map.put("createActionPath", CreateActionPath.getText());
        map.put("updateActionPath", UpdateActionPath.getText());
        map.put("deleteActionPath", DeleteActionPath.getText());
        map.put("detailActionPath", DetailActionPath.getText());
        map.put("pagingActionPath", PagingActionPath.getText());

        PsiAnnotation annotation = psiClass.getAnnotation("io.terminus.trantorframework.api.annotation.TransientModel");
        map.put("modelDesc",annotation.findAttributeValue("name").getText().replaceAll("\"",""));

        String modelName = CommonUtil.getModelName(psiClass);
        map.put("ModelName", modelName);
        map.put("modelName", modelName.toLowerCase(Locale.ROOT));

        String[] projectNames = project.getName().split("-");
        if (projectNames.length > 2) {
            map.put("moduleName", projectNames[1] + "-" + projectNames[2]);
        } else {
            map.put("moduleName", project.getName());
        }

        String text = psiClass.getText();
        if(text.contains("@Author")){
            String textStartAuthor = psiClass.getText().split("@Author")[1];
            map.put("userName",textStartAuthor.split("\n")[0]);
        }else if(text.contains("@author")) {
            String textStartAuthor = psiClass.getText().split("@author")[1];
            map.put("userName", textStartAuthor.split("\n")[0]);
        }else{
            map.put("userName", "");
        }
        return map;
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public void setPsiClass(PsiClass psiClass) {
        this.psiClass = psiClass;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setPsiDirectory(PsiDirectory psiDirectory) {
        this.psiDirectory = psiDirectory;
    }
}
