package ru.proxima.alpha.test;

import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * Обработчик запросов на рекурсивный поиск предметов
 * @author 22c-proxima
 */
@RestController
public class RestServer {
/**
 * Параметры поиска предметов
 */
	@AllArgsConstructor
	public static class SearchParams {
		private int box;
		private String color;
	}

	@Autowired
	Storage storage;
/**
 * GET метод нашего REST-сервиса
 * @param box ID ящика, где будем рекурсивно искать
 * @param color Цвет предметов, которые нам нужны
 * @return Набор предметов, удовлетворяющих заданному условию
 */
	@GetMapping("/test")
	public Stream<Integer> get(@RequestParam int box, @RequestParam String color) {
		return find(box, color);
	}
/**
 * POST метод нашего REST-сервиса
 * @param params Условия поиска
 * @return Набор предметов, удовлетворяющих заданному условию
 */
	@PostMapping("/test")
	public Stream<Integer> post(@RequestBody SearchParams params) {
		return find(params.box, params.color);
	}

	private Stream<Integer> find(int where, String what) {
		return Stream.concat(
			storage.getItems(where, what).stream(),
			storage.getBoxes(where).stream().flatMap(subBox -> find(subBox, what))
		);
	}

}
