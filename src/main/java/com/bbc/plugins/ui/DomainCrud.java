package com.bbc.plugins.ui;

import com.bbc.plugins.build.JavaFileBuild;
import com.bbc.plugins.build.ToJavaFileBuild;
import com.bbc.plugins.utils.CommonUtil;
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
public class DomainCrud extends JDialog {
    private JPanel pane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField ToPath;
    private JTextField QueryRequestPath;
    private JTextField UpdateRequestPath;
    private JTextField CreateRequestPath;
    private JLabel TO;
    private JLabel QueryRequest;
    private JLabel UpdateRequest;
    private JPanel contentPanel;
    private JLabel CreateRequest;
    private JTextField RepositoryPath;
    private JLabel Repository;
    private JTextField ConvertPath;
    private JLabel Convert;
    private JTextField ReadServicePath;
    private JLabel ReadService;
    private JTextField WriteServicePath;
    private JTextField ReadFacadePath;
    private JTextField ReadFacadeImplPath;
    private JTextField WriteFacadePath;
    private JTextField WriteFacadeImplPath;
    private JLabel WriteService;
    private JLabel ReadFacade;
    private JLabel ReadFacadeImpl;
    private JLabel WriteFacade;
    private JLabel WriteFacadeImpl;
    private Project project;
    private PsiClass psiClass;
    private PsiDirectory psiDirectory;

    private JavaFileBuild javaFileBuild;
    private ToJavaFileBuild toJavaFileBuild;

    public DomainCrud(String basePath) {
        setContentPane(contentPanel);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        javaFileBuild = new JavaFileBuild();
        toJavaFileBuild = new ToJavaFileBuild();

        ToPath.setText(basePath + "model");
        QueryRequestPath.setText(basePath + "request");
        UpdateRequestPath.setText(basePath + "request");
        CreateRequestPath.setText(basePath + "request");
        RepositoryPath.setText(basePath + "repository");
        ConvertPath.setText(basePath + "converter");
        ReadServicePath.setText(basePath + "service");
        WriteServicePath.setText(basePath + "service");
        ReadFacadePath.setText(basePath + "facade");
        ReadFacadeImplPath.setText(basePath + "facade");
        WriteFacadePath.setText(basePath + "facade");
        WriteFacadeImplPath.setText(basePath + "facade");

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

        toJavaFileBuild.execute(project, psiClass, ToPath.getText(), pathMap, "api", "TO", msg);
        javaFileBuild.execute(project, psiClass, CreateRequestPath.getText(), pathMap, "api", "CreateRequest", msg);
        javaFileBuild.execute(project, psiClass, QueryRequestPath.getText(), pathMap, "api", "QueryRequest", msg);
        javaFileBuild.execute(project, psiClass, UpdateRequestPath.getText(), pathMap, "api", "UpdateRequest", msg);
        javaFileBuild.execute(project, psiClass, ReadFacadePath.getText(), pathMap, "api", "ReadFacade", msg);
        javaFileBuild.execute(project, psiClass, WriteFacadePath.getText(), pathMap, "api", "WriteFacade", msg);
        javaFileBuild.execute(project, psiClass, RepositoryPath.getText(), pathMap, "implement", "Repository", msg);
        javaFileBuild.execute(project, psiClass, ConvertPath.getText(), pathMap, "implement", "Converter", msg);
        javaFileBuild.execute(project, psiClass, ReadServicePath.getText(), pathMap, "implement", "ReadService", msg);
        javaFileBuild.execute(project, psiClass, WriteServicePath.getText(), pathMap, "implement", "WriteService", msg);
        javaFileBuild.execute(project, psiClass, ReadFacadeImplPath.getText(), pathMap, "implement", "ReadFacadeImpl", msg);
        javaFileBuild.execute(project, psiClass, WriteFacadeImplPath.getText(), pathMap, "implement", "WriteFacadeImpl", msg);

        if (!StringUtils.isEmpty(msg.toString())) {
            Messages.showMessageDialog(project, msg.toString(), "CRUD构建", Messages.getInformationIcon());
        }

        dispose();
    }

    private Map<String, String> initMap() {

        PsiJavaFile psiJavaFile = (PsiJavaFile) psiClass.getContainingFile();

        Map<String, String> map = new HashMap<>(16);
        map.put("poPath", psiJavaFile.getPackageName());
        map.put("toPath", ToPath.getText());
        map.put("repositoryPath", RepositoryPath.getText());
        map.put("converterPath", ConvertPath.getText());
        map.put("createRequestPath", CreateRequestPath.getText());
        map.put("queryRequestPath", QueryRequestPath.getText());
        map.put("readFacadePath", ReadFacadePath.getText());
        map.put("readFacadeImpl", ReadFacadeImplPath.getText());
        map.put("readServicePath", ReadServicePath.getText());
        map.put("repository", RepositoryPath.getText());
        map.put("updateRequestPath", UpdateRequestPath.getText());
        map.put("writeFacadePath", WriteFacadePath.getText());
        map.put("writeFacadeImpl", WriteFacadeImplPath.getText());
        map.put("writeServicePath", WriteServicePath.getText());

        PsiAnnotation annotation = psiClass.getAnnotation("io.terminus.trantorframework.api.annotation.Model");
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
