package jvm;

import mincaml.MinCamlTree;

public abstract class CodeGenerator {

	public abstract void generateTopLevel(MinCamlTree node);

	public abstract void generateVarDecl(MinCamlTree node);

	public abstract void generateVariable(MinCamlTree node);

	public abstract void generateFunctionDecl(MinCamlTree node);

	public abstract void generateFunctionCall(MinCamlTree node);

	public abstract void generateArguments(MinCamlTree node);

	public abstract void generateLiteral(MinCamlTree node);

	public abstract void generateUnaryOperator(MinCamlTree node);

	public abstract void generateBinaryOperator(MinCamlTree node);

	public abstract void generateCompOperator(MinCamlTree node);

	public abstract void generateIfExpression(MinCamlTree node);

	public abstract void generateArrayCreate(MinCamlTree node);

}
