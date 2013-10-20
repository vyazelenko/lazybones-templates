import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.SimpleFileVisitor
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.BasicFileAttributes

def props = [:]
props.conf_group = ask("Define value for 'group' [org.example]: ", "org.example", "conf_group")
props.conf_version = ask("Define value for 'version' [0.1]: ", "0.1", "conf_version")
props.packagename = props.conf_group
props.projectName = ask("Define value for 'project name' [My project]: ", "My project", "projectName")
props.conf_dolphinVersion = ask("Define value for 'dolphinVersion' [0.8]: ", "0.8", "conf_dolphinVersion")
props.conf_groovyAllVersion = ask("Define value for 'groovyAllVersion' [2.1.2]: ", "2.1.2", "conf_groovyAllVersion")

// Figure out target installation directory by reading location from which this script runs
// FIXME: There must be a way to get this information from lazybones
String installationDirPath = new File(getClass().protectionDomain.codeSource.location.path).parent

def renamePackageDir(File dir, String packagePath) {
    File[] files = dir.listFiles();
    for (File f : files) {
        if (f.isDirectory()) {
            if (f.getName().equals("@packagename@")) {
                File dest = new File(f.getParentFile(), packagePath)
                dest.mkdirs();
                boolean moved = f.renameTo(dest);
                //println "moved=" + moved + ":" + f.getAbsolutePath() + " => " + dest.getAbsolutePath()
            } else {
                renamePackageDir(f, packagePath);
            }
        }
    }
}

String packageNamePath = props.packagename.replace('.', '/');
File installationDir = new File(installationDirPath);
renamePackageDir(installationDir, packageNamePath)

// Finally let template variables be resolved
processTemplates("gradle.properties", props)
processTemplates("settings.gradle", props)
processTemplates("**/*.java", props)
processTemplates("**/*.groovy", props)