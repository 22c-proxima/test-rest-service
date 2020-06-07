package ru.proxima.alpha.test;

import lombok.Data;
import lombok.val;
import org.springframework.util.LinkedMultiValueMap;
/**
 * Параметры поиска предметов
 * @author 22c-proxima
 */
@Data
public class SearchParams {

	public final int box;
	public final String color;

	public LinkedMultiValueMap<String, String> toMap() {
		val res = new LinkedMultiValueMap<String, String>();

		res.add("box", String.valueOf(box));
		res.add("color", color);

		return res;
	}

}
