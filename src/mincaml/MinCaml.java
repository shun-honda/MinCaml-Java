package mincaml;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import nez.NezOption;
import nez.SourceContext;
import nez.lang.Grammar;
import nez.lang.GrammarFile;
import nez.util.ConsoleUtils;

public class MinCaml {
	public final static String ProgName = "MinCaml-Java";
	public final static String CodeName = "yokohama";
	public final static int MajorVersion = 0;
	public final static int MinerVersion = 9;
	public final static int PatchLevel = 0;
	public final static String Version = "" + MajorVersion + "." + MinerVersion + "." + PatchLevel;
	public final static String Copyright = "Copyright (c) 2015, Shun Honda";
	public final static String License = "BSD-License Open Source";

	private static Grammar grammar;
	private static MinCamlTreeTransducer treeTransducer;

	public static final void main(String[] args) {
		displayVersion();
		grammar = newMinCamlGrammar();
		treeTransducer = new MinCamlTreeTransducer();
		if(args.length == 0) {
			shell();
		} else {
			MinCamlTransducer mincaml = new MinCamlTransducer();
			MinCamlTree node = parse(mincaml, args[0]);
			execute(mincaml, node);
		}
	}

	public final static void displayVersion() {
		System.out.println(
				ProgName + "-" + Version + " (" + CodeName + ") on Java JVM-" + System.getProperty("java.version"));
		ConsoleUtils.println(Copyright);
	}

	private static GrammarFile mincamlGrammar = null;

	public final static Grammar newMinCamlGrammar() {
		if(mincamlGrammar == null) {
			try {
				mincamlGrammar = GrammarFile.loadGrammarFile("grammar/mincaml.nez", NezOption.newDefaultOption());
			} catch (IOException e) {
				ConsoleUtils.exit(1, "can't load mincaml.nez");
			}
		}
		return mincamlGrammar.newGrammar("File");
	}

	public final static MinCamlTree parse(MinCamlTransducer mincaml, String path) {
		SourceContext source = loadFile(path);
		MinCamlTree node = (MinCamlTree) grammar.parse(source, treeTransducer);
		if(node == null) {
			ConsoleUtils.println(source.getSyntaxErrorMessage());
		}
		System.out.println("parsed:\n" + node + "\n");
		mincaml.eval(node);
		return node;
	}

	private static SourceContext loadFile(String path) {
		try {
			SourceContext source = SourceContext.newFileContext(path);
			return source;
		} catch (IOException e) {
			ConsoleUtils.exit(1, "cannot open: " + path);
		}
		return null;
	}

	public final static MinCamlTree parse(MinCamlTransducer mincaml, String urn, int linenum, String text) {
		SourceContext source = SourceContext.newStringSourceContext(urn, linenum, text);
		MinCamlTree node = (MinCamlTree) grammar.parse(source, treeTransducer);
		if(node == null) {
			ConsoleUtils.println(source.getSyntaxErrorMessage());
		}
		System.out.println("parsed:\n" + node + "\n");
		mincaml.eval(node);
		return node;
	}

	private static void shell() {
		int linenum = 1;
		String command = null;
		MinCamlTransducer mincaml = new MinCamlTransducer();
		while((command = readLine()) != null) {
			MinCamlTree node = parse(mincaml, "<stdio>", linenum, command);
			execute(mincaml, node);
			linenum += (command.split("\n").length);
		}
	}

	private static String readLine() {
		ConsoleUtils.println("\n>>>");
		Object console = ConsoleUtils.getConsoleReader();
		StringBuilder sb = new StringBuilder();
		while(true) {
			String line = ConsoleUtils.readSingleLine(console, "   ");
			if(line == null) {
				return null;
			}
			if(line.equals("")) {
				return sb.toString();
			}
			ConsoleUtils.addHistory(console, line);
			sb.append(line);
			sb.append("\n");
		}
	}

	public static void execute(MinCamlTransducer mincaml, MinCamlTree node) {
		JVMByteCodeGenerator generator = new JVMByteCodeGenerator(mincaml, "GeneratedClass");
		node.generate(generator);
		Class<?> mainClass = generator.generateClass();
		try {
			System.out.println("\n@@@@ Execute Byte Code @@@@");
			Method method = mainClass.getMethod("main");
			method.invoke(null);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			System.out.println("Invocation problem");
			e.printStackTrace();
		}
	}

}
