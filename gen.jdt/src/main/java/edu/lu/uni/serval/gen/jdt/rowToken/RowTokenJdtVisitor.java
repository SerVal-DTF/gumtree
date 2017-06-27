package edu.lu.uni.serval.gen.jdt.rowToken;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.*;

import edu.lu.uni.serval.gen.jdt.exp.ExpressionRebuilder;

/**
 * RowTokenJdtVisitor is used to visit the row tokens of source code and label trees' nodes with ASTNode types.
 * 
 * @author kui.liu
 *
 */
public class RowTokenJdtVisitor  extends AbstractRowTokenJdtVisitor {
    public RowTokenJdtVisitor() {
        super();
    }

    @Override
    public void preVisit(ASTNode n) {
    	if (! (n instanceof Comment || n instanceof TagElement || n instanceof TextElement)) {
    		pushNode(n, getLabel(n));
    	}
    }

    protected String getLabel(ASTNode n) {
    	if (n instanceof AnonymousClassDeclaration) {
    		
		} else if (n instanceof BodyDeclaration) {
			if (n instanceof MethodDeclaration) { // methods and constructors
				MethodDeclaration method = (MethodDeclaration) n;
				List<?> modifiers = method.modifiers();
				Type returnType = method.isConstructor() ? null : method.getReturnType2();
				List<?> typeParameters = method.typeParameters();
				SimpleName methodName = method.getName();
				List<?> parameters = method.parameters();
				List<?> exceptionTypes = method.thrownExceptionTypes();
				String methodLabel = "";
				for (Object obj : modifiers) {
					methodLabel += obj.toString() + ", ";
				}
				for (Object obj : typeParameters) {
					methodLabel += obj.toString() + ", ";
				}
				methodLabel += (returnType == null) ? "" : (returnType.toString() + ", ");
				methodLabel += methodName + ", ";
				for (Object obj : parameters) {
					methodLabel += obj.toString() + ", ";
				}
				for (Object obj : exceptionTypes) {
					methodLabel += obj.toString() + ", ";
				}
				return "MethodDeclaration:" + methodLabel;
			} else if (n instanceof AbstractTypeDeclaration) {
				if (n instanceof TypeDeclaration) { // classes and interfaces
					TypeDeclaration node = (TypeDeclaration) n;
					return node.getName().toString();
				} else if (n instanceof EnumDeclaration) {
					
				} else {
					// AnnotationTypeDeclaration
				}
			} else if (n instanceof AnnotationTypeMemberDeclaration) {
			} else if (n instanceof EnumConstantDeclaration) {
			} else if (n instanceof FieldDeclaration) {
				FieldDeclaration node = (FieldDeclaration) n;
				String nodeStr = node.toString();
		    	nodeStr = nodeStr.substring(0, nodeStr.length() - 1);
		    	return "FieldDeclaration:" + nodeStr;
			} else { // Initializer
			} 
		} else if (n instanceof CatchClause) {
			CatchClause catchClause = (CatchClause) n;
			return "CatchClause:" + catchClause.getException().toString();
		} else if (n instanceof Comment) {
			// if (n instanceof BlockComment) {
			// } else if (n instanceof Javadoc) {
			// } else { //LineComment
			// }
		// } else if (n instanceof Dimension) {
		} else if (n instanceof Expression) {
			if (n instanceof Annotation) { // MarkerAnnotation,NormalAnnotation,SingleMemberAnnotation
			} else if (n instanceof ArrayAccess) {
				return "ArrayAccess:" + ((ArrayAccess)n).toString();
			} else if (n instanceof ArrayCreation) {
				return "ArrayCreation:" + ((ArrayCreation)n).toString();
			} else if (n instanceof ArrayInitializer) {
				return "ArrayInitializer:" + ((ArrayInitializer)n).toString();
			} else if (n instanceof Assignment) { // = += -= etc.
				return "Assignment:" + ((Assignment) n).toString();
			} else if (n instanceof BooleanLiteral) {// true, false
				return "BooleanLiteral:" + ((BooleanLiteral) n).toString();
			} else if (n instanceof CastExpression) {
				return "CastExpression:" + ((CastExpression)n).toString();
			} else if (n instanceof CharacterLiteral) {
				return "CharacterLiteral:" + ((CharacterLiteral) n).getEscapedValue();
			} else if (n instanceof ClassInstanceCreation) {
				return "ClassInstanceCreation:" + ((ClassInstanceCreation)n).toString();
			} else if (n instanceof ConditionalExpression) {
				return "ConditionalExpression" + ((ConditionalExpression)n).toString();
			} else if (n instanceof FieldAccess) {
				return "FieldAccess:" + ((FieldAccess)n).toString();
			} else if (n instanceof InfixExpression) {
				return "InfixExpression:" + ((InfixExpression) n).toString();
			} else if (n instanceof InstanceofExpression) {
				return "InstanceofExpression:" + ((InstanceofExpression)n).toString();
			} else if (n instanceof LambdaExpression) {
				return "LambdaExpression:"+((LambdaExpression)n).toString();
			} else if (n instanceof MethodInvocation) {
				return "MethodInvocation:"+ ((MethodInvocation) n).toString();
			} else if (n instanceof MethodReference) { 
				// CreationReference, ExpressionMethodReference, SuperMethodReference, TypeMethodReference.
			} else if (n instanceof Name) { // (SimpleName, QualifiedName)
				if (n instanceof QualifiedName) {
					return "QualifiedName:"+ ((QualifiedName) n).getFullyQualifiedName();
				} else {
					return "SimpleName:"+ ((SimpleName) n).getFullyQualifiedName();
				}
			} else if (n instanceof NullLiteral) {
				return "NullLiteral:null";
			} else if (n instanceof NumberLiteral) {
				return "NumberLiteral:" +((NumberLiteral) n).getToken();
			} else if (n instanceof ParenthesizedExpression) {
				return "ParenthesizedExpression:"+((ParenthesizedExpression)n).toString();
			} else if (n instanceof PostfixExpression) {
				return "PostfixExpression:"+((PostfixExpression) n).toString();
			} else if (n instanceof PrefixExpression) {
				return "PrefixExpression:"+((PrefixExpression) n).toString();
			} else if (n instanceof StringLiteral) {
				return "StringLiteral:"+((StringLiteral) n).getEscapedValue();
			} else if (n instanceof ThisExpression) {
				return "this";
			} else if (n instanceof SuperFieldAccess) {
				return "SuperField:" + ((SuperFieldAccess)n).toString();
			} else if (n instanceof SuperMethodInvocation) {
				return "SuperMethod:" + ((SuperMethodInvocation)n).toString();
			} else if (n instanceof TypeLiteral) {
				return "TypeLiteral:" +((TypeLiteral)n).toString();
			} else if (n instanceof VariableDeclarationExpression){
				return "VariableDeclarationExpression:" +((VariableDeclarationExpression)n).toString();
			}
		} else if (n instanceof ImportDeclaration) {
			ImportDeclaration node = (ImportDeclaration) n;
			String nodeStr = node.toString();
	    	nodeStr = nodeStr.substring(0, nodeStr.length() - 1);
	    	return "ImportDeclaration:" + nodeStr;
		// } else if (n instanceof MemberRef) {
		// } else if (n instanceof MemberValuePair) {
		// } else if (n instanceof MethodRef) {
		// } else if (n instanceof MethodRefParameter) {
		} else if (n instanceof Modifier) { // public, protected, private, static, abstract, final, native, synchronized, transient, volatile, strictfp
			Modifier node = (Modifier) n;
			return "Modifier:" + node.getKeyword().toString();
		// } else if (n instanceof PackageDeclaration) {
		} else if (n instanceof Statement) {
			if (n instanceof Block) {
				return "Block:";
			} else if (n instanceof AssertStatement) {
				AssertStatement node = (AssertStatement) n;
				Expression exp = node.getExpression();
		    	Expression msg = node.getMessage();
		        String value = exp.getClass().getSimpleName() + ":" + exp.toString();
		        if (msg != null) {
		            value += ", Msg-" + msg.getClass().getSimpleName() + ":" + msg.toString();
		        }
				return "AssertStatement:"+value;
			} else if (n instanceof BreakStatement) {
				BreakStatement node = (BreakStatement) n;
				return "BreakStatement:"+ (node.getLabel() != null ? node.getLabel().toString() : "");
			} else if (n instanceof ConstructorInvocation) {
				ConstructorInvocation node = (ConstructorInvocation) n;
				String nodeStr = node.toString();
		        nodeStr = nodeStr.substring(0, nodeStr.length() - 1);
		        return "ConstructorInvocation:"+nodeStr;
			} else if (n instanceof ContinueStatement) {
				ContinueStatement node = (ContinueStatement) n;
				return "ContinueStatement:"+ (node.getLabel() != null ? node.getLabel().toString() : "");
			} else if (n instanceof DoStatement) {
				DoStatement node = (DoStatement) n;
				Expression exp = node.getExpression();
				return "DoStatement:"+exp.getClass().getSimpleName() + ":" + exp.toString();
			} else if (n instanceof EmptyStatement) {
			} else if (n instanceof EnhancedForStatement) {
				EnhancedForStatement node = (EnhancedForStatement)n;
				SingleVariableDeclaration parameter = node.getParameter();
		    	Expression exp = node.getExpression();
				return "EnhancedForStatement:"+parameter.toString() + ", " + exp.getClass().getSimpleName() + ":" + exp.toString();
			} else if (n instanceof ExpressionStatement) {
				ExpressionStatement node = (ExpressionStatement)n;
				Expression exp = node.getExpression();
				return "ExpressionStatement:"+exp.getClass().getSimpleName() + ":" + exp.toString();
			} else if (n instanceof ForStatement) {
				ForStatement node = (ForStatement)n;
				String value = "";
		        List<?> init = node.initializers();
				Expression exp = node.getExpression();
				List<?> update = node.updaters();
		        value += init.toString() + ";";
				if (exp != null) {
					value += exp.toString() + "; ";
				}
				value += update.toString();
				return "ForStatement:"+value;
			} else if (n instanceof IfStatement) {
				IfStatement node = (IfStatement) n;
				Expression exp = node.getExpression();
				return "IfStatement:"+exp.getClass().getSimpleName() + ":" + exp.toString();
			} else if (n instanceof LabeledStatement) {
				return "LabeledStatement:"+((LabeledStatement)n).getLabel().getFullyQualifiedName();
			} else if (n instanceof ReturnStatement) {
				ReturnStatement node = (ReturnStatement) n;
				Expression exp = node.getExpression();
				if (exp != null) {
					return "ReturnStatement:" + exp.getClass().getSimpleName() + ":" + exp.toString();
				} else {
					return "ReturnStatement: ";
				}
			} else if (n instanceof SuperConstructorInvocation) {
				SuperConstructorInvocation node = (SuperConstructorInvocation) n;
				String nodeStr = node.toString();
		    	nodeStr = nodeStr.substring(0, nodeStr.length() - 1);
				return "SuperConstructorInvocation:" + nodeStr;
			} else if (n instanceof SwitchCase) {
				SwitchCase node = (SwitchCase) n;
				Expression exp = node.getExpression();
		    	if (exp != null) {
		    		return "SwitchCase:" + exp.getClass().getSimpleName() + ":" + exp.toString();
		    	} else {
		    		return "SwitchCase:default";
		    	}
			} else if (n instanceof SwitchStatement) {
				SwitchStatement node = (SwitchStatement)n;
				Expression exp = node.getExpression();
				return "SwitchStatement:"+exp.getClass().getSimpleName() + ":" + exp.toString();
			} else if (n instanceof SynchronizedStatement) { // synchronized  (Expression) Block
				SynchronizedStatement node = (SynchronizedStatement)n;
				Expression exp = node.getExpression();
				return "SynchronizedStatement:"+exp.getClass().getSimpleName() + ":" + exp.toString();
			} else if (n instanceof ThrowStatement) {
				ThrowStatement node = (ThrowStatement)n;
				Expression exp = node.getExpression();
				return "ThrowStatement:"+exp.getClass().getSimpleName() + ":" + exp.toString();
			} else if (n instanceof TryStatement) {
				TryStatement node = (TryStatement) n;
				List<?> resources = node.resources();
		    	if (resources != null) {
		    		return "TryStatement:"+resources.toString();
		    	} else {
		    		return "TryStatement: ";
		    	}
			} else if (n instanceof TypeDeclarationStatement) {
				// TypeDeclaration, EnumDeclaration
			} else if (n instanceof VariableDeclarationStatement) {
				VariableDeclarationStatement node = (VariableDeclarationStatement) n;
				String nodeStr = node.toString();
		    	nodeStr = nodeStr.substring(0, nodeStr.length() - 1);
		    	return "VariableDeclarationStatement:"+nodeStr;
			} else if (n instanceof WhileStatement){
				WhileStatement node = (WhileStatement)n;
				Expression exp = node.getExpression();
				return "WhileStatement:"+exp.getClass().getSimpleName() + ":" + exp.toString();
			}
//		 } else if (n instanceof TagElement) {
//			 return ((TagElement) n).getTagName();
//		 } else if (n instanceof TextElement) {
//	        return n.toString();
		} else if (n instanceof Type) {
			if (n instanceof WildcardType) {
				WildcardType node = (WildcardType) n;
				return "WildcardType:"+ (node.isUpperBound() ? "extends" : "super");
			} else {
				return n.getClass().getSimpleName().toString() + ":" + n.toString();
			}
//			if (n instanceof NameQualifiedType) {
//			} else if (n instanceof PrimitiveType) {
//			} else if (n instanceof QualifiedType) {
//			} else if (n instanceof SimpleType) {
//			} else if (n instanceof ArrayType) {
//			} else if (n instanceof IntersectionType) {
//			} else if (n instanceof ParameterizedType) {
//			} else if (n instanceof UnionType) {
//			}
		} else if (n instanceof TypeParameter) {
			TypeParameter node = (TypeParameter) n;
			return "TypeParameter:" +node.getName().getFullyQualifiedName();
		} else if (n instanceof SingleVariableDeclaration) {
			SingleVariableDeclaration node = (SingleVariableDeclaration)n;
			return "SingleVariableDeclaration:"+node.toString();
		} else if (n instanceof VariableDeclarationFragment) {
			VariableDeclarationFragment node = (VariableDeclarationFragment) n;
			return "VariableDeclarationFragment:"+node.toString();
		}
    	
        return "";
    }
    
    private boolean isSimplestMethodInvocation(Expression exp) {
    	if (exp instanceof MethodInvocation) {
    		MethodInvocation node = (MethodInvocation) exp;
    		Expression e = node.getExpression();
    		if (e != null) return false;
    		List<?> typeArguments = node.typeArguments();
    		if (typeArguments.size() > 0) return false;
    		List<?> arguments = node.arguments();
    		if (arguments.size() > 0) {
    			return false;
    		} else {
    			return true;
    		}
    	}
    	return false;
    }

    private void visitSubExpression(Expression exp) {
    	if (!(exp instanceof BooleanLiteral || exp instanceof CharacterLiteral ||
				exp instanceof Name || exp instanceof NullLiteral ||
				exp instanceof NumberLiteral || exp instanceof StringLiteral ||
				exp instanceof ThisExpression || isSimplestMethodInvocation(exp))) {
			exp.accept(this);
		}
    }
    
    private void visitList(List<?> list) {
        for (Object obj : list) {
        	ASTNode node = (ASTNode) obj;
            (node).accept(this);
        }
    }
    
    ////---------------Expressions---------------
	//  ----------------Annotation---------------
	@Override
	public boolean visit(MarkerAnnotation node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(NormalAnnotation node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(SingleMemberAnnotation node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	// ---------------Annotation---------------
	
    @Override
	public boolean visit(ArrayAccess node) {
    	Expression arrayExp = node.getArray();
    	pushNode(arrayExp, "ArrayName:" + arrayExp.getClass().getSimpleName() + ":" + arrayExp.toString()); //"ArrayName-" + 
    	if (!(arrayExp instanceof Name || !isSimplestMethodInvocation(arrayExp))) {
    		arrayExp.accept(this);
    	}
    	popNode();
		Expression indexExpression = node.getIndex();
		pushNode(indexExpression, "ArrayIndex:" + indexExpression.getClass().getSimpleName() + ":" + indexExpression.toString()); //"ArrayIndex-" + 
		if (!(indexExpression instanceof NumberLiteral || indexExpression instanceof SimpleName || !isSimplestMethodInvocation(arrayExp))) {
			indexExpression.accept(this);
		}
    	popNode();
		return false;
	}

	@Override
	public boolean visit(ArrayCreation node) {
		return true;
	}

	@Override
	public boolean visit(ArrayInitializer node) {
		return true;
	}

	@Override
	public boolean visit(Assignment node) {
		Expression leftHandExp = node.getLeftHandSide();
		pushNode(leftHandExp, "LeftHandExp:" + leftHandExp.getClass().getSimpleName() + ":" + leftHandExp.toString());
		if (!(leftHandExp instanceof SimpleName)) {
			leftHandExp.accept(this);
		}
		popNode();
		String op = node.getOperator().toString();
		push(0, "", "Operator:" + op, leftHandExp.getStartPosition() + leftHandExp.getLength() + 1, op.length());
		popNode();
		
		Expression rightHandExp = node.getRightHandSide();
		pushNode(rightHandExp, "RighttHandExp:" + rightHandExp.getClass().getSimpleName() + ":" + rightHandExp.toString());
		visitSubExpression(rightHandExp);
		popNode();
		return false;
	}

	@Override
	public boolean visit(BooleanLiteral node) {
		return false;
	}

	@Override
	public boolean visit(CastExpression node) {
		return true;
	}

	@Override
	public boolean visit(CharacterLiteral node) {
		return false;
	}

	@Override
	public boolean visit(ClassInstanceCreation node) {
		Expression exp = node.getExpression();
		if (exp != null) {
			pushNode(exp, "" + exp.getClass().getSimpleName() + ":" + exp.toString());
			popNode();
		}
		List<?> typeArguments = node.typeArguments();
		for (Object obj : typeArguments) {
			Type typeArgu = (Type) obj;
			pushNode(typeArgu, "TypeArgument:" + typeArgu.getClass().getSimpleName() + ":" + typeArgu.toString());
			popNode();
		}
		
		Type type = node.getType();
		pushNode(type, "ClassName:" + type.getClass().getSimpleName() + ":" + type.toString());
		
		List<?> arguments = node.arguments();
		for (Object obj : arguments) {
			Expression argu = (Expression) obj;
			pushNode(argu, "Argument:" + argu.getClass().getSimpleName() + ":" + argu.toString());
			visitSubExpression(argu);
			popNode();
		}
    	popNode();
		return false;
	}

	@Override
	public boolean visit(ConditionalExpression node) {
		return true;
	}

	@Override
	public boolean visit(FieldAccess node) {
		return true;
	}

	@Override
	public boolean visit(InfixExpression node) {
		InfixExpression.Operator infixOperator = node.getOperator();
		Expression leftExp = node.getLeftOperand();
		List<?> extendedOperands = node.extendedOperands();
		Expression rightExp;
		if (extendedOperands != null && extendedOperands.size() > 0) {
			String nodeStr = node.toString();
			nodeStr = nodeStr.substring(leftExp.toString().length()).trim();
			nodeStr = nodeStr.substring(infixOperator.toString().length()).trim();
			ExpressionRebuilder expRebuilder = new ExpressionRebuilder();
			rightExp = expRebuilder.createExpression(nodeStr);
		} else {
			rightExp = node.getRightOperand();
		}
		pushNode(leftExp, "LeftExp:" + leftExp.getClass().getSimpleName() + ":" + leftExp.toString());
		visitSubExpression(leftExp);
		popNode();
		
		String op = infixOperator.toString();
		push(0, "", "InfixOperator:" + op, leftExp.getStartPosition() + leftExp.getLength() + 1, op.length());
		popNode();
		
		pushNode(rightExp, "RightExp:" + rightExp.getClass().getSimpleName() + ":" + rightExp.toString());
		visitSubExpression(leftExp);
		popNode();
		return false;
	}

	@Override
	public boolean visit(InstanceofExpression node) {
		return true;
	}

	@Override
	public boolean visit(LambdaExpression node) {
		return true;
	}

	@Override
	public boolean visit(MethodInvocation node) {
		Expression exp = node.getExpression();
		List<?> typeArguments = node.typeArguments();
		SimpleName methodName = node.getName();
		List<?> arguments = node.arguments();
		String arguStr = arguments.toString();
		arguStr = arguStr.substring(arguStr.length() - 1);
		List<MethodInvocation> methods = new ArrayList<>();
		while (exp != null) {
			if (exp instanceof MethodInvocation) {
				MethodInvocation method = (MethodInvocation) exp;
				methods.add(0, method);
				exp = method.getExpression();
			} else {
				pushNode(exp, "ClassName:" + exp.toString());
				popNode();
				exp = null;
			}
		}
		for (MethodInvocation method : methods) {
			pushNode(method, "MethodName:" + method.getName().getFullyQualifiedName());
			List<?> argumentsList = method.arguments();
			for (Object obj : argumentsList) {
				Expression argu = (Expression) obj;
				pushNode(argu, "Argument:" + argu.getClass().getSimpleName() + ":" + argu.toString());
				visitSubExpression(argu);
				popNode();
			}
			popNode();
		}
		for (Object obj : typeArguments) {
			Expression typeArgu = (Expression) obj;
			pushNode(typeArgu, "TypeArgument:" + typeArgu.getClass().getSimpleName() + ":" + typeArgu.toString());
			popNode();
		}
		pushNode(methodName, "MethodName:" + methodName.getFullyQualifiedName());
		
		for (Object obj : arguments) {
			Expression argu = (Expression) obj;
			pushNode(argu, "Argument:" + argu.getClass().getSimpleName() + ":" + argu.toString());
			visitSubExpression(argu);
			popNode();
		}
    	popNode();
		return false;
	}

	// ----------------MethodReference----------------
	@Override
	public boolean visit(CreationReference node) {
		return false;
	}

	@Override
	public boolean visit(ExpressionMethodReference node) {
		return false;
	}

	@Override
	public boolean visit(SuperMethodReference node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public void endVisit(SuperMethodReference node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}
	
	@Override
	public boolean visit(TypeMethodReference node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public void endVisit(TypeMethodReference node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}
	// ----------------MethodReference----------------
	
	// ----------------Name----------------
	@Override
	public boolean visit(QualifiedName node) {
		return false;
	}

	@Override
	public boolean visit(SimpleName node) {
		return false;
	}
	// ----------------Name----------------

	@Override
	public boolean visit(NullLiteral node) {
		return false;
	}

	@Override
	public boolean visit(NumberLiteral node) {
		return false;
	}

	@Override
	public boolean visit(ParenthesizedExpression node) {
		return true;
	}

	@Override
	public boolean visit(PostfixExpression node) {
		Expression exp = node.getOperand();
		pushNode(exp, exp.getClass().getSimpleName() + ":" + exp.toString());
		visitSubExpression(exp);
		popNode();
		String op = node.getOperator().toString();
		push(0, "", "Operator:" + op, exp.getStartPosition() + exp.getLength() + 1, op.length());
		popNode();
		return false;
	}

	@Override
	public boolean visit(PrefixExpression node) {
		String op = node.getOperator().toString();
		push(0, "", "Operator:" + op, node.getStartPosition(), op.length());
		popNode();
		Expression exp = node.getOperand();
		pushNode(exp, exp.getClass().getSimpleName() + ":" + exp.toString());
		visitSubExpression(exp);
		popNode();
		return false;
	}

	@Override
	public boolean visit(StringLiteral node) {
		return false;
	}

	@Override
	public boolean visit(SuperFieldAccess node) {
		Name className = node.getQualifier();
		if (className != null) {
			pushNode(className, className.getFullyQualifiedName());
			popNode();
		}
		SimpleName identifier = node.getName();
		pushNode(identifier, identifier.getFullyQualifiedName());
		popNode();
		return false;
	}

	@Override
	public boolean visit(SuperMethodInvocation node) {
		Name className = node.getQualifier();
		if (className != null) {
			pushNode(className, className.getFullyQualifiedName());
			popNode();
		}
		SimpleName methodName = node.getName();
		pushNode(methodName, methodName.getFullyQualifiedName());
		
		List<?> arguments = node.arguments();
		for (Object obj : arguments) {
			Expression argu = (Expression) obj;
			pushNode(argu, "Argument:" + argu.getClass().getSimpleName() + ":" + argu.toString());
			visitSubExpression(argu);
			popNode();
		}
    	popNode();
		return false;
	}

	@Override
	public boolean visit(ThisExpression node) {
		return false;
	}

    @Override
    public boolean visit(TypeLiteral node) {
        return false;
    }

    @Override
    public boolean visit(VariableDeclarationExpression node) {
    	List<?> modifiers = node.modifiers();
    	for (Object obj : modifiers) {
    		IExtendedModifier modifier = (IExtendedModifier) obj;
    		if (modifier.isModifier()) {
    			pushNode((Modifier) modifier, "Modifier:" + modifier.toString());
    			popNode();
    		}
    	}
    	Type type = node.getType();
    	pushNode(type, type.getClass().getSimpleName() + ":" + type.toString());
    	popNode();
    	List<?> fragments = node.fragments();
    	visitList(fragments);
        return false;
    }

    ////---------------End of Expressions---------------
    
    ////////
    @Override
   	public boolean visit(ArrayType node) {
   		return false;
   	}

	@Override
	public boolean visit(Dimension node) {
		return false;
	}


	///////////////////
    @Override
	public boolean visit(ImportDeclaration node) {
    	return false;
    }
    
    @Override
    public boolean visit(Javadoc node) {
        return false;
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        return true;
    }

    @Override
    public boolean visit(FieldDeclaration node) {
    	List<?> modifiers = node.modifiers();
    	for (Object obj : modifiers) {
    		IExtendedModifier modifier = (IExtendedModifier) obj;
    		if (modifier.isModifier()) {
    			pushNode((Modifier) modifier, "Modifier:" + modifier.toString());
    			popNode();
    		}
    	}
    	
    	Type type = node.getType();
    	pushNode(type, type.getClass().getSimpleName() + ":" + type.toString());
    	popNode();
    	List<?> fragments = node.fragments();
    	visitList(fragments);
        return false;
    }

    @Override
	public boolean visit(Initializer node) {
		return true;
	}
    
	@Override
	public boolean visit(MethodDeclaration node) {
		List<?> modifiers = node.modifiers();
    	for (Object obj : modifiers) {
    		IExtendedModifier modifier = (IExtendedModifier) obj;
    		if (modifier.isModifier()) {
    			pushNode((Modifier) modifier, "Modifier:" + modifier.toString());
    			popNode();
    		}
    	}
    	
    	Type returnType = node.isConstructor() ? null : node.getReturnType2();
    	if (returnType != null) {
    		pushNode(returnType, returnType.getClass().getSimpleName() + ":" + returnType.toString());
        	popNode();
    	}
//		List<?> typeParameters = node.typeParameters();
//		SimpleName methodName = node.getName();
//		List<?> parameters = node.parameters();
//		List<?> exceptionTypes = node.thrownExceptionTypes();
//		
		// The body can be null when the method declaration is from a interface
		if (node.getBody() != null) {
			node.getBody().accept(this);
		}
		return false;
	}

    @Override
    public boolean visit(Modifier node) {
        return false;
    }

    //-----------------Types-----------------
    @Override
    public boolean visit(NameQualifiedType node) {
    	// Name <b>.</b> { Annotation } SimpleName
    	return false;
    }
    
    @Override
    public boolean visit(ParameterizedType node) {
        return false;
    }

    @Override
    public boolean visit(PrimitiveType node) {
        return false;
    }

    @Override
    public boolean visit(QualifiedType node) {
        return false;
    }

    @Override
    public boolean visit(SimpleType node) {
        return false;
    }


    @Override
    public boolean visit(WildcardType node) {
        return true;
    }
    //-----------------Types-----------------

    @Override
    public boolean visit(SingleVariableDeclaration node) {
        return true;
    }

    @Override
    public boolean visit(TypeParameter node) {
        return false;
    }

    @Override
    public boolean visit(VariableDeclarationFragment node) {
        return true;
    }

    ////***************Statements*************************
    @Override
    public boolean visit(CatchClause node) {
        return true;
    }

    ////-------------------Statements-------------------
    @Override
    public boolean visit(Block node) {
        return true;
    }

    @Override
    public boolean visit(AssertStatement node) {
        return true;
    }

    @Override
    public boolean visit(BreakStatement node) {
        return false;
    }

    @Override
    public boolean visit(ConstructorInvocation node) {
        return true;
    }

    @Override
    public boolean visit(ContinueStatement node) {
        return false;
    }

    @Override
    public boolean visit(DoStatement node) {
        return true;
    }

    @Override
    public boolean visit(EmptyStatement node) {
        return false;
    }

    @Override
    public boolean visit(EnhancedForStatement node) {
        return true;
    }

    @Override
    public boolean visit(ExpressionStatement node) {
        return true;
    }

    @Override
    public boolean visit(ForStatement node) {
        return true;
    }

    @Override
    public boolean visit(IfStatement node) {
        return true;
    }

    @Override
    public boolean visit(LabeledStatement node) {
        return true;
    }

    @Override
    public boolean visit(ReturnStatement node) {
        return true;
    }

    @Override
    public boolean visit(SuperConstructorInvocation node) {
        return true;
    }

    @Override
    public boolean visit(SwitchCase node) {
        return true;
    }

    @Override
    public boolean visit(SwitchStatement node) {
        return true;
    }

    @Override
    public boolean visit(SynchronizedStatement node) {
        return true;
    }

    @Override
    public boolean visit(ThrowStatement node) {
        return true;
    }

    @Override
    public boolean visit(TryStatement node) {
    	List<?> resources = node.resources();
    	visitList(resources);
    	node.getBody().accept(this);
    	List<?> catchClauses = node.catchClauses(); // CatchClause
    	visitList(catchClauses);
    	Block finallyBlock = node.getFinally();
    	if (finallyBlock != null) {
    		pushNode(finallyBlock, "Finally");
        	finallyBlock.accept(this);
        	popNode();
    	}
        return false;
    }

    @Override
    public boolean visit(TypeDeclarationStatement node) {
        // skip, only type declaration is interesting
        return true;
    }

    @Override
    public boolean visit(VariableDeclarationStatement node) {
        return true;
    }

    @Override
    public boolean visit(WhileStatement node) {
        return true;
    }

    @Override
    public void postVisit(ASTNode n) {
    	if (!(n instanceof Comment || n instanceof TagElement || n instanceof TextElement)) {
    		popNode();
    	}
    }
}
