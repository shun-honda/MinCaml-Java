package mincaml;

public abstract class MinCamlType {
	String name;

	public MinCamlType(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	abstract boolean equalsType(MinCamlType exprType);

	abstract boolean matchType(MinCamlType exprType);

	abstract boolean isGreekType();

	boolean isPolymorphic() {
		return false;
	}

	public String toString() {
		return this.getName();
	}

	public final static MinCamlType DefualtType = new MinCamlPrimitiveType("int");

	public static MinCamlType newErrorType(MinCamlTree node, String msg) {
		return new MinCamlErrorType(node, msg);
	}
}

class MinCamlPrimitiveType extends MinCamlType {
	public MinCamlPrimitiveType(String name) {
		super(name);
	}

	@Override
	boolean isGreekType() {
		return false;
	}

	boolean equalsType(MinCamlType exprType) {
		return false;
	}

	boolean matchType(MinCamlType exprType) {
		return false;
	}
}

class MinCamlErrorType extends MinCamlType {
	MinCamlTree node;
	String msg;

	MinCamlErrorType(MinCamlTree node, String msg) {
		super("'" + msg + "'");
		this.node = node;
		this.msg = msg;
	}

	@Override
	boolean isGreekType() {
		return false;
	}

	boolean equalsType(MinCamlType exprType) {
		return true;
	}

	boolean matchType(MinCamlType exprType) {
		return true;
	}
}
