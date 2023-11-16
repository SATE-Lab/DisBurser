import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class MethodDetection {
    public static void main(String[] args) throws FileNotFoundException {
        // 计算调用的外部API数和系统调用数量
        Set<String> externalAPIs = new HashSet<>();
        int systemCalls = 0;
        int commandCalls = 0;

        // 处理SimpleTest.java文件
        File file1 = new File("path/to/SimpleTest.java");
        CompilationUnit cu1 = parseFile(file1);
        new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodDeclaration md, Void arg) {
                if (md.isAnnotationPresent("Test")) {
                    super.visit(md, arg);
                }
            }

            @Override
            public void visit(MethodCallExpr n, Void arg) {
                super.visit(n, arg);
                processMethodCall(n, externalAPIs, systemCalls);
            }
        }.visit(cu1, null);

        // 处理ReditHelper.java文件
        File file2 = new File("path/to/ReditHelper.java");
        CompilationUnit cu2 = parseFile(file2);
        new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodDeclaration md, Void arg) {
                if (md.getNameAsString().equals("getDeployment")) {
                    super.visit(md, arg);
                }
            }

            @Override
            public void visit(MethodCallExpr n, Void arg) {
                super.visit(n, arg);
                if (n.getNameAsString().endsWith("Command")) {
                    commandCalls++;
                }
                processMethodCall(n, externalAPIs, systemCalls);
            }
        }.visit(cu2, null);

        System.out.println("External APIs: " + externalAPIs.size());
        System.out.println("System calls: " + systemCalls);
        System.out.println("*Command calls: " + commandCalls);
    }

    private static CompilationUnit parseFile(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine()).append("\n");
        }
        scanner.close();
        String code = sb.toString();
        return StaticJavaParser.parse(code);
    }

    private static void processMethodCall(MethodCallExpr n, Set<String> externalAPIs, int systemCalls) {
        String methodName = n.getNameAsString();
        String scope = n.getScope().map(Object::toString).orElse("");

        // 检测HDFS API调用
        if (scope.contains("hdfs") || scope.contains("fs")) {
            externalAPIs.add(methodName);
        }

        // 检测系统命令调用
        if (methodName.equals("exec") && (scope.contains("Runtime") || scope.contains("ProcessBuilder"))) {
            systemCalls++;
        }
    }
}