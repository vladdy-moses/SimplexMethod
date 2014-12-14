package ru.ulstu.tips;

/**
 * Класс, определяющий неравенство.
 * 
 * @author Моисеев Владислав
 * @version 1.0
 */
public final class Inequality extends LinearExpression {

	private eSign sign;
	/**
	 * Знак неравенства.
	 * 
	 * @author Моисеев Владислав
	 */
	public enum eSign {LESS, EQUAL, MORE};
	
	@Override
	protected void InitVariables() {
		super.InitVariables();
		sign = eSign.EQUAL;
	}
	
	/**
	 * @return Знак неравенства
	 */
	public eSign getSign() {
		return sign;
	}
	
	/**
	 * @param newSign	Знак неравенства
	 */
	public void setSign(eSign newSign) {
		sign = newSign;
	}
	
	/**
	 * Получение равенства из неравенства путём введения новой переменной.
	 * 
	 * @return	Равенство, преобразованное из неравенства
	 */
	public Equation getEquation() {
		Equation eq = new Equation();
		
		if(sign == eSign.LESS) {
			eq.setVariablesCount(this.getVariablesCount() + 1);
			eq.set(this.getVariablesCount(), 1.0);
		} else if(sign == eSign.MORE) {
			eq.setVariablesCount(this.getVariablesCount() + 1);
			eq.set(this.getVariablesCount(), -1.0);
		} else {
			eq.setVariablesCount(this.getVariablesCount());
		}
		
		eq.setFreeTerm(this.getFreeTerm());
		for(int i = 0; i < this.getVariablesCount(); i++)
			eq.set(i, this.get(i));
		
		return eq;
	}
}
