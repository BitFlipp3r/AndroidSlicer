package org.unibremen.mcyl.androidslicer.wala.parser;

import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.Statement;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import java.util.HashSet;
import java.util.List;

//TODO source: Philip,
// mcyl refractor for higher code quality
// mcy fix breaking changes since 1.5
// mcyl fix for multi line ExpressionStmt
public class MethodVisitor extends VoidVisitorAdapter<Object> {

    // out
    private Set<Integer> sourceLineNumbers = new HashSet<>();

    // in
    private Set<Integer> slicedLineNumbers;

    public MethodVisitor(final Set<Integer> slicedLineNumbers) {
        super();
        this.slicedLineNumbers = slicedLineNumbers;
    }

    public void addStatementBody(Node node, int line) {
        if (node instanceof ForStmt ||
            node instanceof SwitchStmt ||
            node instanceof WhileStmt ||
            node instanceof IfStmt ||
            node instanceof TryStmt ||
            node instanceof BlockStmt) {

            if (isLineInNode(node, line)) {

                sourceLineNumbers.add(node.getBegin().get().line);
                sourceLineNumbers.add(node.getEnd().get().line);

                if (node instanceof ForStmt) {
                    ForStmt forStatement = (ForStmt) node;
                    sourceLineNumbers.add(forStatement.getBody().getBegin().get().line);
                    sourceLineNumbers.add(forStatement.getBody().getEnd().get().line);
                    if (forStatement.getBody() instanceof BlockStmt) {
                        addStatementBody(forStatement.getBody(), line);
                    }
                }

                if (node instanceof WhileStmt) {
                    WhileStmt whileStatement = (WhileStmt) node;
                    sourceLineNumbers.add(whileStatement.getBody().getBegin().get().line);
                    sourceLineNumbers.add(whileStatement.getBody().getEnd().get().line);
                    if (whileStatement.getBody() instanceof BlockStmt) {
                        addStatementBody(whileStatement.getBody(), line);
                    }
                }

                if (node instanceof IfStmt) {
                    IfStmt ifStatement = (IfStmt) node;
                    int thenLastLine = 0;
                    if (ifStatement.getThenStmt() != null) {
                        // if no bracets, no inner blocks and probably statement without bracets
                        if (ifStatement.getThenStmt().toString().contains("{")) {
                            sourceLineNumbers.add(ifStatement.getThenStmt().getBegin().get().line);
                            thenLastLine = ifStatement.getThenStmt().getEnd().get().line;
                            sourceLineNumbers.add(thenLastLine);
                        }
                        if (ifStatement.getThenStmt().getBegin().get().line <= line
                                && ifStatement.getThenStmt().getEnd().get().line >= line) {
                                    addStatementBody(ifStatement.getThenStmt(), line);
                        }
                    }

                    if (ifStatement.getElseStmt().get() != null) {
                        // if no bracets, no inner blocks and probably statement without bracets
                        if (ifStatement.getThenStmt().toString().contains("{")) {
                            sourceLineNumbers.add(ifStatement.getElseStmt().get().getBegin().get().line);
                            sourceLineNumbers.add(ifStatement.getElseStmt().get().getEnd().get().line);

                            addAllLinesFromBeginToEnd(
                                thenLastLine,
                                ifStatement.getElseStmt().get().getBegin().get().line,
                                sourceLineNumbers);
                        }
                        if (ifStatement.getElseStmt().get().getBegin().get().line <= line
                                && ifStatement.getElseStmt().get().getEnd().get().line >= line) {
                            if (!ifStatement.getElseStmt().get().toString().contains("else")) {
                                sourceLineNumbers.add(ifStatement.getElseStmt().get().getBegin().get().line - 1);
                            }
                            addStatementBody(ifStatement.getElseStmt().get(), line);
                        }
                    }
                }

                if (node instanceof TryStmt) {
                    TryStmt trystmt = (TryStmt) node;
                    sourceLineNumbers.remove(node.getEnd().get().line);
                    for (Node child : trystmt.getChildNodes()) {
                        addStatementBody(child, line);
                    }

                }

                if (node instanceof BlockStmt) {
                    BlockStmt blockstmt = (BlockStmt) node;

                    for (Statement stmt : blockstmt.getStatements()) {
                        Node blocknode = (Node) stmt;
                        addStatementBody(blocknode, line);
                    }
                }

                if (node instanceof CatchClause) {
                    CatchClause catchstmt = (CatchClause) node;
                    addAllLinesFromBeginToEnd(
                        catchstmt.getBody().getBegin().get().line,
                        catchstmt.getBody().getEnd().get().line,
                        sourceLineNumbers);
                }

            }
        }

        // mcyl: fix for multiline expression statements
        if (node instanceof ExpressionStmt) {
            ExpressionStmt expressionStmt = (ExpressionStmt) node;

            addAllLinesFromBeginToEnd(
                expressionStmt.getBegin().get().line,
                expressionStmt.getEnd().get().line,
                sourceLineNumbers);
        }
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration, Object arg) {
        if (!areSlicedLineNumbersinNode(methodDeclaration)) {
            return;
        }

        // Fix Philip
        // Setting Class Body
        Node parentNode = methodDeclaration.getParentNode().get();
        int firstLine = parentNode.getBegin().get().line;
        if (parentNode.toString().startsWith("@")) {
            firstLine++;
        }
        sourceLineNumbers.add(firstLine);
        if (!parentNode.toString().contains("{")) {
            sourceLineNumbers.add(firstLine + 1);
        }
        // Add all lines between class and first node (Fix for backets in nextline)
        List<Node> children = parentNode.getChildNodes();
        if (!children.isEmpty()) {
            int firstBodyIndex = 0;
            for (Node child : children) {
                if (child instanceof ClassOrInterfaceType) {
                    firstBodyIndex++;
                } else
                    break;
            }

            addAllLinesFromBeginToEnd(
                firstLine,
                children.get(firstBodyIndex).getBegin().get().line - 1,
                sourceLineNumbers);
        }

        // End Fix Philip
        sourceLineNumbers.add(parentNode.getEnd().get().line);
        List<Statement> nodes = methodDeclaration.getBody().get().getStatements();

        if (nodes == null) {
            return;
        }

        for (Node node : nodes) {
            for (Integer line : slicedLineNumbers) {
                if (node.getBegin().get().line <= line && node.getEnd().get().line >= line) {
                    // Fix Philip
                    sourceLineNumbers.add(methodDeclaration.getBegin().get().line);
                    sourceLineNumbers.add(methodDeclaration.getEnd().get().line);
                    // END Fix Philip
                    // PND 20180213
                    sourceLineNumbers.add(methodDeclaration.getBody().get().getBegin().get().line);
                    sourceLineNumbers.add(methodDeclaration.getBody().get().getEnd().get().line);
                    // END PND

                    // Add all lines between method and first brackets
                    // (Fix for multiple line method heads)
                    addAllLinesFromBeginToEnd(
                        methodDeclaration.getBegin().get().line,
                        methodDeclaration.getBody().get().getBegin().get().line,
                        sourceLineNumbers);

                    addStatementBody(node, line);

                }
            }
        }
    }

    // Fix Philip: ConstructorDeclaration was ignored and led to wrong reconstructed
    // code
    @Override
    public void visit(ConstructorDeclaration constructorDeclaration, Object arg) {
        if (!areSlicedLineNumbersinNode(constructorDeclaration)) {
            return;
        }

        // Setting Class Body
        Node parentNode = constructorDeclaration.getParentNode().get();
        int firstLine = parentNode.getBegin().get().line;
        if (parentNode.toString().startsWith("@")) {
            firstLine++;
        }

        sourceLineNumbers.add(firstLine);
        if (!parentNode.toString().contains("{")) {
            sourceLineNumbers.add(firstLine + 1);
        }
        // Add all lines between class and constructor, because there is nothing in
        // between (Fix for backets in nextline)
        List<Node> children = parentNode.getChildNodes();
        if (children.size() > 1) {
            addAllLinesFromBeginToEnd(
                firstLine,
                children.get(0).getBegin().get().line - 1,
                sourceLineNumbers);
        }
        // End Fix Philip
        sourceLineNumbers.add(parentNode.getEnd().get().line);
        List<Statement> nodes = constructorDeclaration.getBody().getStatements();
        sourceLineNumbers.add(constructorDeclaration.getBody().getBegin().get().line);
        sourceLineNumbers.add(constructorDeclaration.getBody().getEnd().get().line);

        if (nodes == null) {
            return;
        }

        for (Node node : nodes) {
            for (Integer line : slicedLineNumbers) {
                if (node.getBegin().get().line <= line && node.getEnd().get().line >= line) {
                    // Fix Philip
                    sourceLineNumbers.add(constructorDeclaration.getBegin().get().line);
                    sourceLineNumbers.add(constructorDeclaration.getEnd().get().line);
                    // END Fix Philip

                    // Add all lines between method and first brackets
                    // (Fix for multiple line method heads)
                    addAllLinesFromBeginToEnd(
                        constructorDeclaration.getBegin().get().line,
                        constructorDeclaration.getBody().getBegin().get().line,
                        sourceLineNumbers);

                    addStatementBody(node, line);
                }
            }
        }
    }

    public Set<Integer> getSlice() {
        return sourceLineNumbers;
    }

    private boolean isLineInNode(Node node, int line) {
        return (node.getBegin().get().line <= line && node.getEnd().get().line >= line);
    }

    private boolean areSlicedLineNumbersinNode(Node node) {
        for (Integer line : slicedLineNumbers) {
            if (isLineInNode(node, line)) {
                return true;
            }
        }
        return false;
    }

    // --------------------- Note -------------------------------------
    // rangeClosed(int startInclusive, int endInclusive):
    // Returns a sequential ordered IntStream from startInclusive (inclusive)
    // to endInclusive (inclusive) by an incremental step of 1.
    // (see: https://docs.oracle.com/javase/8/docs/api/java/util/stream/IntStream.html)
    private void addAllLinesFromBeginToEnd(int start, int end, Set<Integer> collection){
        collection.addAll(IntStream.rangeClosed(start,end).boxed().collect(Collectors.toList()));
    }
}
