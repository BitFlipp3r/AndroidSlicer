package org.unibremen.mcyl.androidslicer.wala.parser;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.SynchronizedStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;


/**
 * This is an implementation of the of the MethodVisitor-pattern for the JavaParser described in their ebook.
 * The code based on the work by Markus Gulman (Masterthesis 2014) and Philip Phu Dang Hoan Nguyen (Masterthesis 2018) but has been 
 * heavily altered by Michael Cyl with bug fixed, improvements and refactorings. Most notable changes are the update from Version 1.5
 * to 3.14.9 along with the fix of all breaking changes and the addition of missing statement types like SynchronizedStmt, ExpressionStmt,
 * FinallyBlocks and changes to handling of special statements like return statements.
 */
public class MethodVisitor extends VoidVisitorAdapter<Object> {

    // in
    private Set<Integer> slicedLineNumbers;

    // out
    private Set<Integer> sourceLineNumbers;

    // use this to always take the class declaration into the slice
    private String mainClassName;

    public MethodVisitor(final Set<Integer> slicedLineNumbers, String mainClassName) {
        super();
        this.slicedLineNumbers = slicedLineNumbers;
        this.sourceLineNumbers = new HashSet<Integer>(slicedLineNumbers);
        this.mainClassName = mainClassName;
    }

    // add all kinds of ast statements
    // see:
    // https://static.javadoc.io/com.github.javaparser/javaparser-core/3.5.0/com/github/javaparser/ast/stmt/Statement.html
    public void addStatementBody(Node node, int line) {

        if (node instanceof BlockStmt) {
            BlockStmt blockStmt = (BlockStmt) node;
            for (Statement stmt : blockStmt.getStatements()) {
                addStatementBody(stmt, line);
            }
            return;
        }

        if (isLineInNode(node, line)) {

            sourceLineNumbers.add(node.getBegin().get().line);
            sourceLineNumbers.add(node.getEnd().get().line);

            if (node instanceof ForStmt) {
                ForStmt forStmt = (ForStmt) node;
                // mcyl: fix for multiline heads
                addAllLinesFromBeginToEnd(forStmt.getBegin().get().line, 
                        forStmt.getBody().getBegin().get().line,
                        sourceLineNumbers);
                sourceLineNumbers.add(forStmt.getBody().getEnd().get().line);

                if (forStmt.getBody() instanceof BlockStmt){
                    addStatementBody(forStmt.getBody(), line);
                }
            }

            // mcyl: added ForEachStmt
            if (node instanceof ForEachStmt) {
                ForEachStmt forEachStmt = (ForEachStmt) node;
                // mcyl: fix for multiline heads
                addAllLinesFromBeginToEnd(
                    forEachStmt.getBegin().get().line,
                    forEachStmt.getBody().getBegin().get().line,
                    sourceLineNumbers);
                sourceLineNumbers.add(forEachStmt.getBody().getEnd().get().line);

                if (forEachStmt.getBody() instanceof BlockStmt) {
                    addStatementBody(forEachStmt.getBody(), line);
                }
            }

            // mcyl: added SynchronizedStmt
            if (node instanceof SynchronizedStmt) {
                SynchronizedStmt synchronizedStmt = (SynchronizedStmt) node;
                // mcyl: fix for multiline heads
                addAllLinesFromBeginToEnd(
                    synchronizedStmt.getBegin().get().line,
                    synchronizedStmt.getBody().getBegin().get().line,
                    sourceLineNumbers);
                sourceLineNumbers.add(synchronizedStmt.getBody().getEnd().get().line);

                if (synchronizedStmt.getBody() instanceof BlockStmt) {
                    addStatementBody(synchronizedStmt.getBody(), line);
                }
            }

            if (node instanceof WhileStmt) {
                WhileStmt whileStmt = (WhileStmt) node;
                // mcyl: fix for multiline heads
                addAllLinesFromBeginToEnd(
                    whileStmt.getBegin().get().line,
                    whileStmt.getBody().getBegin().get().line,
                    sourceLineNumbers);
                sourceLineNumbers.add(whileStmt.getBody().getEnd().get().line);

                if (whileStmt.getBody() instanceof BlockStmt) {
                    addStatementBody(whileStmt.getBody(), line);
                }
            }

            if (node instanceof IfStmt) {
                IfStmt ifStmt = (IfStmt) node;
                int thenLastLine = 0;
                Statement thenStmt = ifStmt.getThenStmt();
                if (thenStmt != null){
                    // mcyl: fix for multiline heads
                    addAllLinesFromBeginToEnd(
                        ifStmt.getBegin().get().line,
                        thenStmt.getBegin().get().line,
                        sourceLineNumbers);

                    // if no brackets, no inner blocks and probably statement without brackets
                    if (thenStmt.toString().contains("{")) {
                        thenLastLine = thenStmt.getEnd().get().line;
                    }
                    addStatementBody(thenStmt, line);
                }

                if(ifStmt.getElseStmt() != null && ifStmt.getElseStmt().isPresent()){
                    Statement elseStmt = ifStmt.getElseStmt().get();
                    if (elseStmt != null) {
                        // if no brackets, no inner blocks and probably statement without brackets
                        if (thenLastLine > 0) {
                            sourceLineNumbers.add(elseStmt.getBegin().get().line);
                            sourceLineNumbers.add(elseStmt.getEnd().get().line);

                            addAllLinesFromBeginToEnd(
                                    thenLastLine, 
                                    elseStmt.getBegin().get().line,
                                    sourceLineNumbers);
                        }
                        if (isLineInNode(elseStmt, line)) {
                            if (!elseStmt.toString().contains("else")) {
                                sourceLineNumbers.add(elseStmt.getBegin().get().line - 1);
                            }
                            addStatementBody(elseStmt, line);
                        }
                    }
                }
            }

            if (node instanceof TryStmt) {
                TryStmt tryStmt = (TryStmt) node;
                for (Node child : tryStmt.getChildNodes()) {
                    addStatementBody(child, line);
                }

                 // mcyl: added catch clauses
                for (CatchClause catchClause : tryStmt.getCatchClauses()) {
                    if (isLineInNode(catchClause, line)){
                        // add catch all lines of catch predicate
                        addAllLinesFromBeginToEnd(
                            catchClause.getBegin().get().line,
                            catchClause.getBody().getBegin().get().line,
                            sourceLineNumbers);
                        // add statements
                        for (Node child : catchClause.getChildNodes()) {
                            addStatementBody(child, line);
                        }
                    }

                }
                
                //mcyl: added finally blocks
                Optional<BlockStmt> finallyBlock = tryStmt.getFinallyBlock();
                if (finallyBlock.isPresent() && isLineInNode(finallyBlock.get(), line)) {
                    // add statements
                    sourceLineNumbers.add(finallyBlock.get().getBegin().get().line);
                    sourceLineNumbers.add(finallyBlock.get().getEnd().get().line);
                    for (Node child : finallyBlock.get().getChildNodes()) {
                        addStatementBody(child, line);
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

            // mcyl: fix for multiline throw statements
            if (node instanceof ThrowStmt) {
                ThrowStmt throwStmt = (ThrowStmt) node;

                addAllLinesFromBeginToEnd(
                    throwStmt.getBegin().get().line,
                    throwStmt.getEnd().get().line,
                    sourceLineNumbers);
            }

            // mcyl: added switch statements
            if (node instanceof SwitchStmt) {
                SwitchStmt switchStmt = (SwitchStmt) node;
                // mcyl: fix for multiline heads
                int entrySize = switchStmt.getEntries().size();
                if(entrySize > 0){
                    addAllLinesFromBeginToEnd(
                        switchStmt.getBegin().get().line,
                        switchStmt.getEntry(0).getBegin().get().line,
                        sourceLineNumbers);
                    sourceLineNumbers.add(switchStmt.getEntry(entrySize -1).getEnd().get().line);

                    for(SwitchEntry switchEntry : switchStmt.getEntries()) {
                        addStatementBody(switchEntry, line);
                    }
                }
            }
            // mcyl: added switch entry statements
            if (node instanceof SwitchEntry) {
                SwitchEntry switchEntry = (SwitchEntry) node;
                
                addAllLinesFromBeginToEnd(
                    switchEntry.getBegin().get().line,
                    switchEntry.getEnd().get().line,
                    sourceLineNumbers);

                for(Statement switchEntryStmt : switchEntry.getStatements()) {
                    addStatementBody(switchEntryStmt, line);
                }
            }
        }

        // mcyl: add all return statements from control dependencies, regardless if sliced line is inside this return statement node
        if (node instanceof ReturnStmt) {
            ReturnStmt returnStmt = (ReturnStmt) node;

            addAllLinesFromBeginToEnd(
                returnStmt.getBegin().get().line,
                returnStmt.getEnd().get().line,
                sourceLineNumbers);
        }
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration, Object arg) {
        if (!areSlicedLineNumbersInNode(methodDeclaration)) {
            return;
        }

        Node parentNode = methodDeclaration.getParentNode().get();

        // mcyl: fix for pngyens of set the class body  
        if(parentNode instanceof ClassOrInterfaceDeclaration){          
           setClassBody((ClassOrInterfaceDeclaration)parentNode);
        }        

        List<Node> methodNodes = methodDeclaration.getBody().get().getChildNodes();

        // Add all lines between method and first brackets
        // (Fix for multiple line method heads)
        addAllLinesFromBeginToEnd(
            methodDeclaration.getBegin().get().line,
            methodDeclaration.getBody().get().getBegin().get().line,
            sourceLineNumbers);
        sourceLineNumbers.add(methodDeclaration.getBody().get().getEnd().get().line);
        sourceLineNumbers.add(methodDeclaration.getEnd().get().line);

        if (methodNodes == null) {
            return;
        }

        for (Node node : methodNodes) {
            for (int line : slicedLineNumbers) {
                if (isLineInNode(node, line)) {
                    addStatementBody(node, line);
                }
            }
        }
    }

    // pnguyen: ConstructorDeclaration was ignored and led to wrong reconstructed code
    @Override
    public void visit(ConstructorDeclaration constructorDeclaration, Object arg) {
        // mcyl: set class body if slice line is inside note or if its the main class (i.e. entry class)
        if(areSlicedLineNumbersInNode(constructorDeclaration) | 
            constructorDeclaration.getNameAsString().equals(this.mainClassName)) {
              
                Node parentNode = constructorDeclaration.getParentNode().get();         
                if(parentNode instanceof ClassOrInterfaceDeclaration){          
                   setClassBody((ClassOrInterfaceDeclaration)parentNode);
                }                                                 
        }

        /**
         * search inside of constructor if slice node line is inside or else return
         */ 
        if (!areSlicedLineNumbersInNode(constructorDeclaration)) {
            return;
        }

        List<Node> constructorNodes = constructorDeclaration.getBody().getChildNodes();
    
        // Add all lines between method and first brackets
        // (Fix for multiple line method heads)
        addAllLinesFromBeginToEnd(
            constructorDeclaration.getBegin().get().line,
            constructorDeclaration.getBody().getBegin().get().line,
            sourceLineNumbers);
        sourceLineNumbers.add(constructorDeclaration.getBody().getEnd().get().line);
        sourceLineNumbers.add(constructorDeclaration.getEnd().get().line);

        if (constructorNodes == null) {
            return;
        }

        for (Node node : constructorNodes) {
            for (int line : slicedLineNumbers) {
                if (isLineInNode(node, line)) {
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

    private boolean areSlicedLineNumbersInNode(Node node) {
        for (Integer line : slicedLineNumbers) {
            if (isLineInNode(node, line)) {
                return true;
            }
        }
        return false;
    }

    private void setClassBody(ClassOrInterfaceDeclaration classNode){
        sourceLineNumbers.add(classNode.getBegin().get().line);

        // mcyl: check if any implements or extends reach to another line
        Set<Node> extendsAndImplementsNodes = new HashSet<Node>();
        extendsAndImplementsNodes.addAll(classNode.getExtendedTypes());
        extendsAndImplementsNodes.addAll(classNode.getImplementedTypes());
        for(Node extendsAndImplementsNode : extendsAndImplementsNodes){
            addAllLinesFromBeginToEnd(
                classNode.getBegin().get().line,
                extendsAndImplementsNode.getEnd().get().line ,
                sourceLineNumbers);
        }

        sourceLineNumbers.add(classNode.getEnd().get().line);
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
