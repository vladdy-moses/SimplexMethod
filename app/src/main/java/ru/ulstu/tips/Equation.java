package ru.ulstu.tips;

/**
 * Класс, определяющий уравнение.
 * 
 * @author Моисеев Владислав
 * @version 1.0
 */
public final class Equation extends LinearExpression {	
	
	/**
	 * Преобразование коэффициентов уравнения.
	 * 
	 * @param variable	Номер переменной в уравнении
	 * @param value		Значение, к которому стоит привести коэффициент перед переменной
	 */
	public void reform(int variable, double value) {
		double coefficient = this.get(variable) / value;
		
		if(variable < this.getVariablesCount()) {
			if(Double.compare(value, 0.0) != 0) {
				if(Double.compare(this.get(variable), 0.0) != 0) {
					for(int i = 0; i < this.getVariablesCount(); i++)
						if(Double.compare(this.get(i), 0.0) != 0)
							this.set(i, this.get(i) / coefficient);
				}
				
				if(Double.compare(this.getFreeTerm(), 0.0) != 0) {
					this.setFreeTerm(this.getFreeTerm() / coefficient);
				}
			}
		}
	}
	
	/**
	 * Суммирование двух уравнений.
	 * 
	 * @param eq	Слагаемое
	 */
	public void sum(Equation eq) {
		if(eq.getVariablesCount() > this.getVariablesCount())
			this.setVariablesCount(eq.getVariablesCount());
		for(int i = 0; i < eq.getVariablesCount(); i++)
			this.set(i, this.get(i) + eq.get(i));
		
		this.setFreeTerm(this.getFreeTerm() + eq.getFreeTerm());
	}
	
	/**
	 * Умножение коэффициентов уравнения на определённый множитель.
	 * 
	 * @param coefficient	Множитель
	 */
	public void multiplication(double coefficient) {
		for(int i = 0; i < this.getVariablesCount(); i++)
			if(Double.compare(this.get(i), 0.0) != 0)
				this.set(i, this.get(i) * coefficient);
		
		if(Double.compare(this.getFreeTerm(), 0.0) != 0)
			this.setFreeTerm(this.getFreeTerm() * coefficient);
	}
	
	/**
	 * Вывод коэффициентов и свободного члена в виде строки.
	 * 
	 * @return	Строка, представляющая уравнение
	 */
	public String print() {
		String result;
		
		result = String.valueOf(this.getFreeTerm()) + "\t\t| ";
		for(int i = 0; i < this.getVariablesCount(); i++) {
			result += this.get(i) + "\t";
		}
		
		return result;
	}
	
	/**
	 * Клонирование уравнения.
	 * 
	 * @return	Копия исходного уравнения
	 */
	public Equation clone() {
		Equation cloneEquation = new Equation();
		
		cloneEquation.setVariablesCount(this.getVariablesCount());
		for(int i = 0; i < this.getVariablesCount(); i++)
			cloneEquation.set(i, this.get(i));
		
		cloneEquation.setFreeTerm(this.getFreeTerm());
		
		return cloneEquation;
	}
}
