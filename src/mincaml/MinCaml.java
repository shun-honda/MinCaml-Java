package mincaml;

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

	public static final void main(String[] args) {
		displayVersion();
	}

	public final static void displayVersion() {
		System.out.println(
				ProgName + "-" + Version + " (" + CodeName + ") on Java JVM-" + System.getProperty("java.version"));
		ConsoleUtils.println(Copyright);
	}
}
