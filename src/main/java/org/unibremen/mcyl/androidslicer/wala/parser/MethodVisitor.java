package org.unibremen.mcyl.androidslicer.wala.parser;


import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
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

//TODO source: Philip, fix breaking changes since 1.5
public class MethodVisitor extends VoidVisitorAdapter<Object> {

	private Set<Integer> slice = new HashSet<>();

	private Set<Integer> inSlice = new HashSet<>();

	public MethodVisitor(final Set<Integer> inSlice) {
		super();
		this.inSlice = inSlice;
	}

	public Set<Integer> getStatementBody(Node node, int line, Set<Integer> exeSlice) {
		if (node instanceof ForStmt || node instanceof SwitchStmt || node instanceof WhileStmt || node instanceof IfStmt
				|| node instanceof TryStmt || node instanceof BlockStmt) {
			if (node.getBegin().get().line <= line && node.getEnd().get().line >= line) {
				exeSlice.add(node.getBegin().get().line);
				exeSlice.add(node.getEnd().get().line);
				if (node instanceof ForStmt) {
					ForStmt forStatement = (ForStmt) node;
					exeSlice.add(forStatement.getBody().getBegin().get().line);
					exeSlice.add(forStatement.getBody().getEnd().get().line);
					if (forStatement.getBody() instanceof BlockStmt) {
						exeSlice.addAll(getStatementBody(forStatement.getBody(), line, exeSlice));
					}
				}
				if (node instanceof WhileStmt) {
					WhileStmt whileStatement = (WhileStmt) node;
					exeSlice.add(whileStatement.getBody().getBegin().get().line);
					exeSlice.add(whileStatement.getBody().getEnd().get().line);
					if (whileStatement.getBody() instanceof BlockStmt) {
						exeSlice.addAll(getStatementBody(whileStatement.getBody(), line, exeSlice));
					}
				}
				if (node instanceof IfStmt) {
					IfStmt ifStatement = (IfStmt) node;
					int then_last_line = 0;
					if (ifStatement.getThenStmt() != null) {
						// if no bracets, no inner blocks and probably statement without bracets
						if (ifStatement.getThenStmt().toString().contains("{")) {
							exeSlice.add(ifStatement.getThenStmt().getBegin().get().line);
							then_last_line = ifStatement.getThenStmt().getEnd().get().line;
							exeSlice.add(then_last_line);
						}
						if (ifStatement.getThenStmt().getBegin().get().line <= line
								&& ifStatement.getThenStmt().getEnd().get().line >= line) {
							exeSlice.addAll(getStatementBody(ifStatement.getThenStmt(), line, exeSlice));
						}
					}
					if (ifStatement.getElseStmt().get() != null) {
						// if no bracets, no inner blocks and probably statement without bracets
						if (ifStatement.getThenStmt().toString().contains("{")) {
							exeSlice.add(ifStatement.getElseStmt().get().getBegin().get().line);
							exeSlice.add(ifStatement.getElseStmt().get().getEnd().get().line);
							List<Integer> lines = IntStream
									.rangeClosed(then_last_line, ifStatement.getElseStmt().get().getBegin().get().line).boxed()
									.collect(Collectors.toList());
							exeSlice.addAll(lines);
						}
						if (ifStatement.getElseStmt().get().getBegin().get().line <= line
								&& ifStatement.getElseStmt().get().getEnd().get().line >= line) {
							if (!ifStatement.getElseStmt().get().toString().contains("else")) {
								exeSlice.add(ifStatement.getElseStmt().get().getBegin().get().line - 1);
								// exeSlice.add(ifStatement.getElseStmt().get().getEnd().get().line);
							}
							exeSlice.addAll(getStatementBody(ifStatement.getElseStmt().get(), line, exeSlice));
						}
					}
				}

				if (node instanceof TryStmt) {
					TryStmt trystmt = (TryStmt) node;
					// exeSlice.addAll(getStatementBody(trystmt.getTryBlock(),line,exeSlice));
					exeSlice.remove(node.getEnd().get().line);
					for (Node child : trystmt.getChildNodes()) {
						exeSlice.addAll(getStatementBody(child, line, exeSlice));
					}

				}
				if (node instanceof BlockStmt) {
					BlockStmt blockstmt = (BlockStmt) node;

					for (Statement stmt : blockstmt.getStatements()) {
						Node blocknode = (Node) stmt;
						exeSlice.addAll(getStatementBody(blocknode, line, exeSlice));
					}
				}
				if (node instanceof CatchClause) {
					CatchClause catchstmt = (CatchClause) node;
					// exeSlice.add(catchstmt.getCatchBlock().getBegin().get().line);
					// exeSlice.add(catchstmt.getCatchBlock().getEnd().get().line);
				}

			}
		}
		return exeSlice;
	}

	@Override
	public void visit(MethodDeclaration n, Object arg) {
		Set<Integer> exeSlice = new HashSet<>();
		List<Statement> nodes;
		boolean go = false;
		for (Integer line : inSlice) {
			if (n.getBegin().get().line <= line && n.getEnd().get().line >= line) {
				go = true;
			}
		}
		if (go == false) {
			return;
		}

		// Fix Philip
		// Setting Class Body
		Integer first_line;
		if (n.getParentNode().get().toString().startsWith("@")) {
			first_line = n.getParentNode().get().getBegin().get().line + 1;
		} else {
			first_line = n.getParentNode().get().getBegin().get().line;
		}

		exeSlice.add(first_line);
		if (!n.getParentNode().get().toString().contains("{")) {
			exeSlice.add(first_line + 1);
		}
		// Add all lines between class and first node (Fix for backets in nextline)
		List<Node> children = n.getParentNode().get().getChildNodes();
		if (!children.isEmpty()) {
			int first_body_index = 0;
			for (Node child : children) {
				if (child instanceof ClassOrInterfaceType) {
					++first_body_index;
				} else
					break;
			}
			List<Integer> lines = IntStream.rangeClosed(first_line, children.get(first_body_index).getBegin().get().line - 1)
					.boxed().collect(Collectors.toList());
			exeSlice.addAll(lines);
		}

		// End Fix Philip
		exeSlice.add(n.getParentNode().get().getEnd().get().line);
		nodes = n.getBody().get().getStatements();

		if (nodes == null) {
			exeSlice.addAll(inSlice);
			slice.addAll(exeSlice);
			return;
		}

		for (Node node : nodes) {
			for (Integer line : inSlice) {
				if (node.getBegin().get().line <= line && node.getEnd().get().line >= line) {
					// Fix Philip
					exeSlice.add(n.getBegin().get().line);
					exeSlice.add(n.getEnd().get().line);
					// END Fix Philip
					// PND 20180213
					exeSlice.add(n.getBody().get().getBegin().get().line);
					exeSlice.add(n.getBody().get().getEnd().get().line);
					// END PND

					// Add all lines between method and first brackets (Fix for multiple line method
					// heads)
					List<Integer> lines = IntStream.rangeClosed(n.getBegin().get().line, n.getBody().get().getBegin().get().line).boxed()
							.collect(Collectors.toList());
					exeSlice.addAll(lines);

					exeSlice.addAll(getStatementBody(node, line, exeSlice));

				}
			}
		}
		exeSlice.addAll(inSlice);
		slice.addAll(exeSlice);
	}

	// Fix Philip: ConstructorDeclaration was ignored and led to wrong reconstructed
	// code
	@Override
	public void visit(ConstructorDeclaration n, Object arg) {
		Set<Integer> exeSlice = new HashSet<>();
		List<Statement> nodes;
		boolean go = false;

		for (Integer line : inSlice) {
			if (n.getBegin().get().line <= line && n.getEnd().get().line >= line) {
				go = true;
			}
		}
		if (go == false) {
			return;
		}

		// Setting Class Body
		Integer first_line;
		if (n.getParentNode().get().toString().startsWith("@")) {
			first_line = n.getParentNode().get().getBegin().get().line + 1;
		} else {
			first_line = n.getParentNode().get().getBegin().get().line;
		}

		exeSlice.add(first_line);
		if (!n.getParentNode().get().toString().contains("{")) {
			exeSlice.add(first_line + 1);
		}
		// Add all lines between class and constructor, because there is nothing in
		// between (Fix for backets in nextline)
		List<Node> children = n.getParentNode().get().getChildNodes();
		if (children.size() > 1) {
			List<Integer> lines = IntStream.rangeClosed(first_line, children.get(0).getBegin().get().line - 1).boxed()
					.collect(Collectors.toList());
			exeSlice.addAll(lines);
		}
		// End Fix Philip
		exeSlice.add(n.getParentNode().get().getEnd().get().line);
		nodes = n.getBody().getStatements();
		exeSlice.add(n.getBody().getBegin().get().line);
		exeSlice.add(n.getBody().getEnd().get().line);

		if (nodes == null) {
			exeSlice.addAll(inSlice);
			slice.addAll(exeSlice);
			return;
		}

		for (Node node : nodes) {
			for (Integer line : inSlice) {
				if (node.getBegin().get().line <= line && node.getEnd().get().line >= line) {
					// Fix Philip
					exeSlice.add(n.getBegin().get().line);
					exeSlice.add(n.getEnd().get().line);
					// END Fix Philip

					// Add all lines between method and first brackets (Fix for multiple line method
					// heads)
					List<Integer> lines = IntStream.rangeClosed(n.getBegin().get().line, n.getBody().getBegin().get().line).boxed()
							.collect(Collectors.toList());
					exeSlice.addAll(lines);

					exeSlice.addAll(getStatementBody(node, line, exeSlice));
				}
			}
		}

		exeSlice.addAll(inSlice);
		slice.addAll(exeSlice);
	}

	public Set<Integer> getSlice() {
		return slice;
	}
}
