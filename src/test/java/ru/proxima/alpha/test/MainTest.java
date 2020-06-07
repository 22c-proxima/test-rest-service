package ru.proxima.alpha.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * Тестим работу приложения на примере загрузки файла test.xml
 * @author 22c-proxima
 */
@SpringBootTest(
	args = "test.xml",
	webEnvironment = WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
public class MainTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void contextLoads() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();

		val testCases = new HashMap<String, SearchParams>();
		testCases.put("[2,3]", new SearchParams(1, "red"));
		testCases.put("[3]", new SearchParams(3, "red"));
		testCases.put("[4]", new SearchParams(1, "black"));
		testCases.put("[]", new SearchParams(1, "white"));

		for (String res : testCases.keySet()) {
			mockMvc.perform(get("/test")
				.params(testCases.get(res).toMap())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(res));
			mockMvc.perform(post("/test")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(testCases.get(res)))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(res));
		}
	}

}
