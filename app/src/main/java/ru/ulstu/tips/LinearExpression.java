package ru.ulstu.tips;

import java.util.Vector;

/**
 * Класс, определяющий линейное выражение (будть то равенство или неравенство).
 * 
 * @author Моисеев Владислав
 * @version 1.0
 */
public class LinearExpression {
	protected Vector<Double> iCoefficients;
	protected double iFreeTerm;
	
	public LinearExpression() {
		this.InitVariables();
	}
	
	/**
	 * Инициализация переменных.
	 */
	protected void InitVariables() {
		iCoefficients = new Vector<Double>();
		iFreeTerm = 0;
	}
	
	/**
	 * Задаёт число переменных в выражении.
	 * 
	 * @param variablesCount Число переменных
	 */
	public void setVariablesCount(int variablesCount) {
		int realVariables = this.getVariablesCount();
		if(variablesCount > realVariables) {
			for(int i = realVariables; i < variablesCount; i++)
				iCoefficients.add(i, Double.valueOf(0));
		}
		if(variablesCount < realVariables) {
			for(int i = variablesCount; i < realVariables; i++)
				iCoefficients.remove(i);
		}
	}
	
	/**
	 * Возвращает число переменных в выражении.
	 * 
	 * @return Число переменных
	 */
	public int getVariablesCount() {
		return iCoefficients.size();
	}
	
	/**
	 * Задаёт свободный член выражения.
	 * 
	 * @param d	Свободный член
	 */
	public void setFreeTerm(double d) {
		iFreeTerm = d;
	}
	
	/**
	 * Возвращает свободный член выражения.
	 * 
	 * @return Свободный член
	 */
	public double getFreeTerm() {
		return iFreeTerm;
	}
	
	/**
	 * Задаёт коэффициент перед переменной.
	 * 
	 * @param place	Номер переменной
	 * @param val	Значение коэффициента
	 */
	public void set(int place, double val) {
		if(place < this.getVariablesCount()) {
			iCoefficients.set(place, Double.valueOf(val));
		}
	}
	
	/**
	 * Возвращает коэффициент перед переменной.
	 * 
	 * @param place	Номер переменной
	 * @return	Значение коэффициента
	 */
	public double get(int place) {
		if(place < this.getVariablesCount()) {
			return iCoefficients.get(place).doubleValue();
		} else {
			return 0;
		}
	}
}
