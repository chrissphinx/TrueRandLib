package edu.wmich.csphinx.truerand;

public class Main
{
	public static void main(String[] args) throws Exception {
		Random rand = new Random();
		for (int j = 0; j < 10; j++) {
			for (int i = 0; i < 10; i++) {
				System.out.print(rand.nextInt(0, 9) + " ");
			} System.out.println();
		}
		rand.newBytes();
		rand.close();
	}
}