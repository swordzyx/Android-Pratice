package com.sword.plugin

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager


class SwordTransform extends Transform {
    @Override
    String getName() {
        return "Sowrd"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        println "Sword Transform call transform 1"
        transformInvocation.inputs.forEach { input ->
            input.jarInputs.forEach { jarInput ->
                def targetFile = transformInvocation.outputProvider.getContentLocation(jarInput.name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                println "Jar: ${jarInput.file.absolutePath}, Target: ${targetFile.absolutePath}"
                FileUtils.copyFile(jarInput.file, targetFile)
            }
            input.directoryInputs.forEach { dirInput ->
                def targetFile = transformInvocation.outputProvider.getContentLocation(dirInput.name, dirInput.contentTypes, dirInput.scopes,
                        Format.DIRECTORY)
                println "Dir: ${dirInput.file.absolutePath}, Target: ${targetFile.absolutePath}"
                FileUtils.copyDirectory(dirInput.file, targetFile)
            }
        }

    }
}
