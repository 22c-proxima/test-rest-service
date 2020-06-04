package ru.proxima.alpha.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * Загрузка XML с помощью StAX. Можно и с помощью JAXB это делать
 * @author 22с-proxima
 */
@Component @Slf4j
public class XmlLoad {

	private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();

	@Autowired
	Storage storage;
/**
 * Загрузка исходных данных для сервиса из XML-файла
 * @param xmlPath Файл с исходными данными
 */
	public void from(String xmlPath) {
		try (XmlLoader loader = new XmlLoader(xmlPath)) {
			loader.exploreStorage();
		} catch (XMLStreamException | NumberFormatException ex) {
			log.error("Не удалось считать XML-файл", ex);
			throw new Error();
		}
	}

	private class XmlLoader implements AutoCloseable {

		private final XMLStreamReader reader;
		private XmlLoader(String xmlPath) throws XMLStreamException {
			try {
				reader = FACTORY.createXMLStreamReader(Files.newInputStream(Paths.get(xmlPath)));
			} catch (IOException ex) {
				log.error("Не удалось открыть XML-файл из параметров командной строки, работа невозможна", ex);
				throw new Error();
			}
		}

		private void exploreStorage() throws XMLStreamException {
			if (lookFor("Storage")) {
				exploreBox(0);
				storage.dump();
			} else {
				throw new XMLStreamException("Неверный формат входного XML, отсутствует элемент Storage");
			}
		}

		private void exploreBox(int parent) throws XMLStreamException {
			Integer event;
			int id;
			String color;

			while (true) {
				event = lookFor(XMLEvent.START_ELEMENT, XMLEvent.END_ELEMENT);
				if (event == null) {
					storage.dump();
					throw new XMLStreamException("Неверный формат входного XML");
				} else if (event == XMLEvent.END_ELEMENT) {
					return;
				}

				switch (reader.getLocalName()) {
					case "Box":
						id = getIdAttr();
						storage.putBox(id, parent);
						exploreBox(id);
						break;
					case "Item":
						id = getIdAttr();
						color = getColorAttr();
						storage.putItem(id, parent, color);
						lookFor(XMLEvent.END_ELEMENT);
						break;
					default:
						throw new XMLStreamException("Неверный формат входного XML, найден недопустимый элемент");
				}
			}
		}

		private Integer lookFor(int... events) throws XMLStreamException {
			while (reader.hasNext()) {
				int e = reader.next();
				for (int event : events) {
					if (e == event) {
						return e;
					}
				}
			}
			return null;
		}

		private boolean lookFor(String value) throws XMLStreamException {
			while (reader.hasNext()) {
				int e = reader.next();
				if (e == XMLEvent.START_ELEMENT && value.equals(reader.getLocalName())) {
					return true;
				}
			}
			return false;
		}

		private int getIdAttr() throws NumberFormatException {
			int id = Integer.parseInt(reader.getAttributeValue(null, "id"));
			if (id > 0) {
				return id;
			} else {
				throw new NumberFormatException("Идентификатор должен быть положительным");
			}
		}

		private String getColorAttr() {
			String color = reader.getAttributeValue(null, "color");
			return (color == null ? "" : color);
		}

		@Override
		public void close() {
		 if (reader != null) {
			  try {
				 reader.close();
			  } catch (XMLStreamException e) {
			  }
		   }
		}

	} // private class XmlLoader implements AutoCloseable {

}
