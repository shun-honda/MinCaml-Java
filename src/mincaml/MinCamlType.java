package mincaml;

import java.util.ArrayList;
import java.util.List;

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

	public final static MinCamlType DefualtType = new MinCamlPrimitiveType("int", int.class);

	public static MinCamlType newErrorType(MinCamlTree node, String msg) {
		return new MinCamlErrorType(node, msg);
	}
}

class MinCamlPrimitiveType extends MinCamlType {
	Class<?> c;

	public MinCamlPrimitiveType(String name, Class<?> c) {
		super(name);
		this.c = c;
	}

	@Override
	boolean isGreekType() {
		return false;
	}

	boolean equalsType(MinCamlType exprType) {
		return this == exprType;
	}

	boolean matchType(MinCamlType exprType) {
		return this == exprType;
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

class MinCamlFuncType extends MinCamlType {
	MinCamlReturnType retType = new MinCamlReturnType(this.name);
	List<MinCamlType> argTypeList;

	public MinCamlFuncType(String name) {
		super(name);
	}

	public void setReturnType(MinCamlType type) {
		this.retType.setType(type);
	}

	public MinCamlReturnType getReturnType() {
		return this.retType;
	}

	public void setArgsType(List<MinCamlType> types) {
		this.argTypeList = new ArrayList<MinCamlType>();
		this.argTypeList = types;
	}

	public void setArgType(int index, MinCamlType type) {
		this.argTypeList.remove(index);
		this.argTypeList.add(index, type);
	}

	@Override
	boolean equalsType(MinCamlType exprType) {
		return false;
	}

	@Override
	boolean matchType(MinCamlType exprType) {
		return false;
	}

	@Override
	boolean isGreekType() {
		return false;
	}
}

class MinCamlReturnType extends MinCamlType {
	MinCamlType type;

	public MinCamlReturnType(String name) {
		super("ret:" + name);
	}

	public void setType(MinCamlType type) {
		this.type = type;
	}

	@Override
	boolean equalsType(MinCamlType exprType) {
		return false;
	}

	@Override
	boolean matchType(MinCamlType exprType) {
		return false;
	}

	@Override
	boolean isGreekType() {
		return false;
	}

	@Override
	public String toString() {
		if(this.type != null) {
			return this.type.toString();
		}
		return this.getName();
	}

	public boolean isNull() {
		return this.type == null;
	}

}
