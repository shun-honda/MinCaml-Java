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

	public Class<?> getJavaClass() {
		return this.getClass();
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

	@Override
	public Class<?> getJavaClass() {
		return c;
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
	boolean standard;
	MinCamlTypeVariable retType = new MinCamlTypeVariable(this.name);
	List<MinCamlType> argTypeList;

	public MinCamlFuncType(String name) {
		super(name);
		this.standard = false;
	}

	public MinCamlFuncType(String name, boolean standard) {
		super(name);
		this.standard = standard;
	}

	public void setReturnType(MinCamlType type) {
		this.retType.setType(type);
	}

	public MinCamlTypeVariable getReturnType() {
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

class MinCamlArrayType extends MinCamlType {
	MinCamlType type;

	public MinCamlArrayType(String name, MinCamlType type) {
		super("array:" + name);
		this.type = type;
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

}

class MinCamlTypeVariable extends MinCamlType {
	MinCamlType type;

	public MinCamlTypeVariable(String name) {
		super("var:" + name);
	}

	public void setType(MinCamlType type) {
		this.type = type;
	}

	@Override
	boolean equalsType(MinCamlType exprType) {
		return this.type == exprType;
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
	public Class<?> getJavaClass() {
		if(this.type == null) {
			return this.getClass();
		}
		return this.type.getJavaClass();
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
