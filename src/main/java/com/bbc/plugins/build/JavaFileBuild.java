package com.bbc.plugins.build;

import com.bbc.plugins.utils.CommonUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * @author 杨森君
 * @date 2023/6/11 11:35
 */

public class JavaFileBuild{

    public PsiClass execute(Project project, PsiClass target, String path, Map<String, String> additionalProperties, String module, String template,StringBuilder msg) {
        try {
            PsiDirectory pathDirectory = CommonUtil.analysisPath(project, module, path);
            if (pathDirectory.findFile(additionalProperties.get("ModelName") + template+".java") != null) {
                msg.append(template + "生成失败:文件已存在\n");
                return null;
            }
            if(pathDirectory.findFile(template.replace("Delete","Delete"+additionalProperties.get("ModelName"))
                    .replace("Create","Create"+additionalProperties.get("ModelName"))
                    .replace("Update","Update"+additionalProperties.get("ModelName"))+".java") !=null
            ){
                msg.append(template + "生成失败:文件已存在\n");
                return null;
            }

            JavaDirectoryService myDirectoryService = JavaDirectoryService.getInstance();
            LocalDateTime time = LocalDateTime.now();
            additionalProperties.put("date", time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            additionalProperties.put("packagePath", path);
            PsiClass buildClass = myDirectoryService.createClass(pathDirectory, additionalProperties.get("ModelName")+template, template, true, additionalProperties);

            afterProcess(project, target, buildClass);

            return buildClass;

        } catch (Exception e) {
            e.printStackTrace();
            Messages.showMessageDialog(project,template+"生成失败:"+e.getMessage(),"生成CRUD异常", Messages.getErrorIcon());
        }

        return null;
    }

    public void afterProcess(Project project, PsiClass po, PsiClass psiClass) {

    }
}
