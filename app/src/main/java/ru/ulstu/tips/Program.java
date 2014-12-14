package ru.ulstu.tips;

import ru.ulstu.tips.Inequality.eSign;

public class Program {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Inequality i;
		
		Simplex s = new Simplex();
		s.setFactVariablesCount(3);
		
		Equation r = new Equation();
		r.setVariablesCount(3);
		r.set(0, 31); r.set(1, 20); r.set(2, 15);
		s.setResultFunction(r);
		
		i = new Inequality();
		i.setVariablesCount(3);
		i.set(0, 10); i.set(1, 12); i.set(2, 17);
		i.setFreeTerm(100);
		i.setSign(eSign.LESS);
		s.addInequality(i);
		
		i = new Inequality();
		i.setVariablesCount(3);
		i.set(0, 1); i.set(1, 1); i.set(2, 1);
		i.setFreeTerm(6);
		s.addInequality(i);
		
		i = new Inequality();
		i.setVariablesCount(3);
		i.set(2, 1);
		i.setFreeTerm(1.5);
		i.setSign(eSign.MORE);
		s.addInequality(i);
		
		i = new Inequality();
		i.setVariablesCount(3);
		i.set(2, 1);
		i.setFreeTerm(2.5);
		i.setSign(eSign.LESS);
		s.addInequality(i);
		
		s.run();
		
		System.out.print(s.getLog().get());
	}
}
