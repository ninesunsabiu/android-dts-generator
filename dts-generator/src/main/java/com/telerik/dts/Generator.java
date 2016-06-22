package com.telerik.dts;

import org.apache.bcel.classfile.JavaClass;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by plamen5kov on 6/17/16.
 */
public class Generator {

    private static File outFolder;
    private FileWriter fw;
    private DtsApi dtsApi;

    public void start(File outDir, File[] jars, boolean multipleFiles) throws IOException {
        outFolder = outDir;
        this.fw = new FileWriter(outDir, multipleFiles);
        this.dtsApi = new DtsApi();

        loadJavaClasses(jars);
        ClassRepo.sortCachedProviders();

        generate();
    }

    private void generate() {
        String[] classNames = ClassRepo.getClassNames();
        for (String className : classNames) {
            try {
                JavaClass clazz = ClassRepo.findClass(className);
                generateDts(clazz);
            } catch (Throwable e) {
                e.printStackTrace(); //todo: fix exception
            }
        }
    }

    private void generateDts(JavaClass clazz) throws FileNotFoundException {
        String generatedContent = this.dtsApi.generateDtsContent(clazz);
        this.fw.write(generatedContent, clazz.getFileName());
    }

    //HELPER FUNTIONS
    private void loadJavaClasses(File[] jars) throws IOException {
        for (File file : jars) {
            if (file.exists()) {
                if (file.isFile() && file.getName().endsWith(".jar")) {
                    JarFile jar = JarFile.readJar(file.getAbsolutePath());
                    ClassRepo.cacheJarFile(jar);
                } else if (file.isDirectory()) {
                    ClassDirectrory dir = ClassDirectrory.readDirectory(file.getAbsolutePath());
                    ClassRepo.cacheJarFile(dir);
                }
            }
        }
    }
}