package ru.ulstu.tips;

import java.util.Vector;

/**
 * Класс, включающий логику выполнения симплекс-метода для минимизации функции.
 * 
 * @author Моисеев Владислав
 * @version 1.1
 */
public final class Simplex {
	private int iFactVariablesCount;
	private int iDummyVariablesCount;
	private int iArtificialVariablesCount;
	private Vector<Inequality> vInequalities;
	private Vector<Equation> vEquations;
	private Equation zFunction;
	private Equation auxiliaryFunction;
	private boolean bNeedAuxiliaryFunction;
	private Log l;
	
	public Simplex() {
		iFactVariablesCount = 0;
		iDummyVariablesCount = 0;
		iArtificialVariablesCount = 0;
		vInequalities = new Vector<Inequality>();
		vEquations = new Vector<Equation>();
		zFunction = new Equation();
		bNeedAuxiliaryFunction = false;
		l = new Log();
	}
	
	/**
	 * Задаёт число фактических переменных в целевой функции.
	 * 
	 * @param count Число фактических переменных
	 */
	public void setFactVariablesCount(int count) {
		iFactVariablesCount = count;
	}
	
	/**
	 * Возвращает число фактических переменных в целевой функции.
	 * 
	 * @return Число фактических переменных
	 */
	public int getFactVariablesCount() {
		return iFactVariablesCount;
	}
	
	/**
	 * Возвращает число использованных переменных.
	 * То есть, фактических и фиктивных переменных.
	 * 
	 * @return Число фактических и фиктивных переменных
	 */
	private int getUsedVariablesCount() {
		return this.getFactVariablesCount() + this.iDummyVariablesCount;
	}
	
	/**
	 * Добавляет условие в виде неравенства.
	 * 
	 * @param iq Неравенство, содержащее условие
	 */
	public void addInequality(Inequality iq) {
		vInequalities.add(iq);
	}
	
	/**
	 * Возвращает число условий в задаче.
	 * 
	 * @return	Число условий
	 */
	public int getInequalitiesCount() {
		return vInequalities.size();
	}
	
	/**
	 * Задаёт целевую функцию.
	 * 
	 * @param func	Целевая функция
	 */
	public void setResultFunction(Equation func) {
		zFunction = func;
	}
	
	/**
	 * Получает целевую функцию.
	 * 
	 * @return	Целевая функция
	 */
	public Equation getResultFunction() {
		return zFunction;
	}
	
	/**
	 * Возвращает лог
	 * 
	 * @return	Лог
	 */
	public Log getLog() {
		return l;
	}
	
	/**
	 * Запускает вычисления.
	 * 
	 * @return	Результирующее уравнение, содержащее значение целевой функции
	 * как свобобный член и коэффициенты переменных как их значения.
	 */
	public Equation run() {
		this.getEquations();
		this.processEquations();
		this.prepareResultFunctions();
		this.calculate();
		
		return this.getSolution();
	}

	private void getEquations() {
		Equation eq;
		Inequality iq;
		
		l.add("Начинается преобразование неравенств в равенства.");
		for(int i = 0; i < this.getInequalitiesCount(); i++) {
			iq = vInequalities.get(i);
			iq.setVariablesCount(this.getUsedVariablesCount());
			eq = iq.getEquation();
			vEquations.add(eq);
			l.add(eq.print());
			
			iDummyVariablesCount = eq.getVariablesCount() - this.getFactVariablesCount();
		}
	}
	
	private void processEquations() {
		Equation eq;
		boolean flagHasPositive;
		
		if(vEquations.size() > 0) {
			l.add("Начинается введение искусственных переменных.");
			auxiliaryFunction = new Equation();
			for(int i = 0; i < this.getInequalitiesCount(); i++) {
				eq = vEquations.get(i);
				eq.setVariablesCount(this.getUsedVariablesCount() + iArtificialVariablesCount);
				
				flagHasPositive = false;
				for(int j = this.getFactVariablesCount(); j < eq.getVariablesCount(); j++)
					if(Double.compare(eq.get(j), 0.0) > 0)
						flagHasPositive = true;
				
				if((!flagHasPositive) && (Double.compare(eq.getFreeTerm(), 0.0) != 0)) {
					l.add("  Введена новая искусственная переменная.");
					++iArtificialVariablesCount;
					auxiliaryFunction.sum(eq);
					eq.setVariablesCount(this.getUsedVariablesCount() + iArtificialVariablesCount);
					eq.set(eq.getVariablesCount() - 1, 1.0);
				}
			}
		}
	}
	
	private void prepareResultFunctions() {
		if(Double.compare(zFunction.getFreeTerm(), 0.0) != 0)
			zFunction.setFreeTerm(zFunction.getFreeTerm() * -1.0);
		l.add("Целевая функция определена: " + zFunction.print());
		
		auxiliaryFunction.multiplication(-1.0);
		l.add("Вспомогателья целевая функция определена: \n" + auxiliaryFunction.print());
		
		bNeedAuxiliaryFunction = true;
	}
	
	private void calculate() {
		int currentColumn;
		int currentRow;
		
		l.add("Начинается основной этап вычислений.");
		while((bNeedAuxiliaryFunction) || (!isRightResultFunction())) {
			currentColumn = getCurrentColumn();
			currentRow = getCurrentRow(currentColumn);
			
			l.add("  Ведущий столбец - " + String.valueOf(currentColumn) + " | " +
				"Ведущая строка - " + String.valueOf(currentColumn));
			
			vEquations.get(currentRow).reform(currentColumn, 1.0);
			for(int i = 0; i < vInequalities.size(); i++)
				if(i != currentRow)
					gaussReform(vEquations.get(currentRow), vEquations.get(i), currentColumn);
			
			gaussReform(vEquations.get(currentRow), this.getResultFunction(), currentColumn);
			
			if(bNeedAuxiliaryFunction)
				gaussReform(vEquations.get(currentRow), auxiliaryFunction, currentColumn);
			
			for(int i = 0; i < vInequalities.size(); i++) {
				l.add(vEquations.get(i).print());
			}
			l.add(this.getResultFunction().print());
			if(bNeedAuxiliaryFunction)
				l.add(auxiliaryFunction.print());
			l.add("");
			
			checkAuxiliaryFunction();
		}
	}
	
	private Equation getSolution() {
		Equation eq = new Equation();
		eq.setVariablesCount(this.getFactVariablesCount());
		
		eq.setFreeTerm(this.getResultFunction().getFreeTerm() * -1.0);
		for(int i = 0; i < eq.getVariablesCount(); i++) {
			if(Double.compare(this.getResultFunction().get(i), 0.0) == 0) {
				for(int j = 0; j < this.vInequalities.size(); j++) {
					if(Double.compare(vEquations.get(j).get(i), 1.0) == 0)
						eq.set(i, vEquations.get(j).getFreeTerm());
				}
			}
		}
		
		l.add("Решение сформировано: " + eq.print());
		return eq;
	}

	private int getCurrentColumn() {
		Equation eq = (bNeedAuxiliaryFunction) ? auxiliaryFunction : this.getResultFunction();
		int result = -1;
		int currentRow;
		double minCoefficient = 0.0;
		double currentCoefficient;
		
		for(int i = 0; i < eq.getVariablesCount(); i++)
			if(Double.compare(eq.get(i), 0.0) < 0) {
				currentRow = this.getCurrentRow(i);
				currentCoefficient = eq.get(i) * vEquations.get(currentRow).getFreeTerm() / vEquations.get(currentRow).get(i);
				if((result == -1) || 
						(Double.compare(currentCoefficient, minCoefficient) > 0)) {
					minCoefficient = currentCoefficient;
					result = i;
				}
			}
		
		return result;
	}
	
	private int getCurrentRow(int currentColumn) {
		int result = -1;
		Equation eq;
		Equation resultEq = null;
		
		if(currentColumn >= 0) {
			for(int i = 0; i < this.getInequalitiesCount(); i++) {
				eq = vEquations.get(i);
				if((Double.compare(eq.get(currentColumn), 0.0) > 0) &&
						(Double.compare(eq.getFreeTerm(), 0.0) != 0)) {
					if((result == -1) ||
							(Double.compare(eq.getFreeTerm() / eq.get(currentColumn),
									resultEq.getFreeTerm() / resultEq.get(currentColumn)) < 0)) {
						result = i;
						resultEq = vEquations.get(result);
					}
				}
			}
		}
		
		return result;
	}
	
	private void gaussReform(Equation base, Equation eq, int index) {
		Equation eqBase = base.clone();
		
		if(Double.compare(eq.get(index), 0.0) != 0) {
			eqBase.reform(index, eq.get(index) * -1.0);
			eq.sum(eqBase);
		}
	}

	private boolean isRightResultFunction() {
		boolean result = true;
		
		for(int i = 0; i < this.getUsedVariablesCount(); i++)
			if(Double.compare(this.getResultFunction().get(i), 0.0) < 0)
				result = false;
			
		return result;
	}
	
	private void checkAuxiliaryFunction() {
		boolean result = false;
		for(int i = 0; i < this.getUsedVariablesCount(); i++)
			if(Double.compare(auxiliaryFunction.get(i), 0.0) != 0)
				result = true;
		
		if(Double.compare(auxiliaryFunction.getFreeTerm(), 0.0) != 0)
			result = true;
		
		bNeedAuxiliaryFunction = result;
	}
}
