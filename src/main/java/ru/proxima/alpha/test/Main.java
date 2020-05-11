package ru.proxima.alpha.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * Тестовое приложение
 * @author 22c-proxima
 */
@SpringBootApplication
public class Main implements CommandLineRunner {

	@Autowired
	XmlLoad xmlLoad;
/**
 * Точка входа в приложение
 * @param args Аргументы командной строки
 */
	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}
/**
 * Загрузка XML и логгирование содержимого БД
 * @param args Параметры командной строки, первый должен содержать путь к XML
 */
	@Override
	public void run(String... args) {
		xmlLoad.from(args[0]);
	}

}
