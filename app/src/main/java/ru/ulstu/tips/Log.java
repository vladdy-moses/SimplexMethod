package ru.ulstu.tips;

/**
 * Класс, служащий для логирования информации.
 * 
 * @author Моисеев Владислав
 * @version 1.0
 */
public final class Log {
	private String sLog = "";
	
	/**
	 * Добавляет информацию в лог.
	 * 
	 * @param str	Строка с информацией
	 */
	public void add(String str) {
		sLog += str + "\n";
	}
	
	/**
	 * Возвращает сформированную строку.
	 * 
	 * @return	Сформированная строка
	 */
	public String get() {
		return sLog;
	}
}
