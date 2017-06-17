package gumtree.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;

import com.github.gumtreediff.gen.TreeGenerator;
import com.github.gumtreediff.gen.jdt.AbstractJdtVisitor;
import com.github.gumtreediff.tree.TreeContext;

public abstract class AbstractBlockJdtTreeGenerator extends TreeGenerator {

    private static char[] readerToCharArray(Reader r) throws IOException {
        StringBuilder fileData = new StringBuilder(1000);
        BufferedReader br = new BufferedReader(r);

        char[] buf = new char[10];
        int numRead = 0;
        while ((numRead = br.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        br.close();

        return  fileData.toString().toCharArray();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public TreeContext generate(Reader r) throws IOException {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setKind(ASTParser.K_STATEMENTS);
        Map pOptions = JavaCore.getOptions();
        pOptions.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
        pOptions.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
        pOptions.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
        parser.setCompilerOptions(pOptions);
        parser.setSource(readerToCharArray(r));
//        CompilationUnit unit = (CompilationUnit) parser.createAST(null);
        AbstractJdtVisitor v = createVisitor();
        parser.createAST(null).accept(v);
        return v.getTreeContext();
    }

    protected abstract AbstractJdtVisitor createVisitor();
}