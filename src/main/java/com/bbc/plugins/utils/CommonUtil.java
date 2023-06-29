package com.bbc.plugins.utils;

import com.intellij.ide.projectView.impl.ProjectRootsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;

import java.io.IOException;
import java.util.Objects;

/**
 * @author 杨森君
 * @date 2023/6/10 17:58
 */
public class CommonUtil {

    public static PsiDirectory analysisPath(Project project, String module, String path) throws IOException {
        VirtualFile virtualFile = null;
        for(VirtualFile file:project.getProjectFile().getParent().getParent().getChildren()){
            if(file.getName().contains(module)){
                virtualFile = file;
            }
        }
        virtualFile = virtualFile.findChild("src");
        virtualFile = virtualFile.findChild("main");
        virtualFile = virtualFile.findChild("java");

        for(String pack:path.split("\\.")){
            VirtualFile child = virtualFile.findChild(pack);
            if(Objects.isNull(child)){
                virtualFile = virtualFile.createChildDirectory(virtualFile,pack);
            }else{
                virtualFile = child;
            }
        }

        PsiDirectory pathDirectory = PsiManager.getInstance(project).findDirectory(virtualFile);

        return pathDirectory;
    }

    public static PsiDirectory analysisPathDefineModule(Project project, PsiClass target, String path) throws IOException {
        PsiDirectory targetDirectory = PsiManager.getInstance(project).findDirectory(target.getContainingFile().getVirtualFile());
        while (!ProjectRootsUtil.isModuleContentRoot(targetDirectory)){
            targetDirectory = targetDirectory.getParentDirectory();
        }

        VirtualFile virtualFile = targetDirectory.getVirtualFile();

        for(String pack:path.split("\\.")){
            VirtualFile child = virtualFile.findChild(pack);
            if(Objects.isNull(child)){
                virtualFile = virtualFile.createChildDirectory(virtualFile,pack);
            }else{
                virtualFile = child;
            }
        }

        PsiDirectory pathDirectory = PsiManager.getInstance(project).findDirectory(virtualFile);

        return pathDirectory;
    }

    public static String getModelName(PsiClass po) {
        return po.getName().substring(0, po.getName().length() - 2);
    }



}
