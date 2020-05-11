package ru.proxima.alpha.test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
/**
 * Связь с БД, используемой для хранения и доступа к загруженным данным
 * @author 22с-proxima
 */
@Component
public class Storage {

	private static final Logger LOG = LoggerFactory.getLogger(Storage.class);

	@Autowired
	JdbcTemplate db;

	@PostConstruct
	public void init() {
		db.execute("CREATE TABLE BOX (ID INTEGER PRIMARY KEY, CONTAINED_IN INTEGER)");
		db.execute("CREATE TABLE ITEM (ID INTEGER PRIMARY KEY, CONTAINED_IN INTEGER REFERENCES BOX(ID), COLOR VARCHAR(100))");
	}
/**
 * Добавляем ящик в БД
 * @param id Идентификатор ящика
 * @param parent Ящик, где лежит данный; либо нуль
 */
	public void putBox(int id, int parent) {
		db.update(
			"INSERT INTO BOX (ID, CONTAINED_IN) VALUES (?, ?)",
			ps -> {
				ps.setInt(1, id);
				setNullableParent(ps, parent);
			}
		);
	}
/**
 * Добавляем предмет в БД
 * @param id Идентификатор предмета
 * @param parent Ящик, где лежит предмет; либо нуль
 * @param color Цвет ящика, если отсутствует - ""
 */
	public void putItem(int id, int parent, String color) {
		db.update(
			"INSERT INTO ITEM (ID, CONTAINED_IN, COLOR) VALUES (?, ?, ?)",
			ps -> {
				ps.setInt(1, id);
				setNullableParent(ps, parent);
				ps.setString(3, color);
			}
		);
	}
/**
 * Список предметов данного цвета в данном ящике, жаль что H2 не поддерживает рекурсивные запросы
 * @param parent ID ящика, где следует искать
 * @param color Цвет предметов
 * @return Идентификаторы искомых предметов
 */
	public List<Integer> getItems(int parent, String color) {
		return db.query(
			"SELECT ID FROM ITEM WHERE CONTAINED_IN = ? AND COLOR = ?",
			ps -> {
				ps.setInt(1, parent);
				ps.setString(2, color);
			},
			(rs, _1) -> rs.getInt(1)
		);
	}
/**
 * Список ящиков внутри данного ящика
 * @param parent ID ящика, где следует искать
 * @return Идентификаторы искомых ящиков
 */
	public List<Integer> getBoxes(int parent) {
		return db.query(
			"SELECT ID FROM BOX WHERE CONTAINED_IN = ?",
			ps -> ps.setInt(1, parent),
			(rs, _1) -> rs.getInt(1)
		);
	}
/**
 * Логгирует содержимое БД
 * throws RuntimeException 
 */
	public void dump() throws RuntimeException {
		LOG.info("Выгрузка содержимого БД начинается");
		LOG.info("Выгрузка таблицы предметов начинается");
		db.query(
			"SELECT * FROM ITEM",
			rs -> {
				LOG.info(String.format(
					"Предмет ID: %d, ID хранителя: %s, цвет: %s",
					rs.getInt("ID"),
					getNullableParent(rs),
					rs.getString("COLOR")
				));
			}
		);
		LOG.info("Выгрузка таблицы предметов успешно завершена");
		LOG.info("Выгрузка таблицы ящиков начинается");
		db.query(
			"SELECT * FROM BOX",
			rs -> {
				LOG.info(String.format(
					"Ящик ID: %d, ID хранителя: %s",
					rs.getInt("ID"),
					getNullableParent(rs)
				));
			}
		);
		LOG.info("Выгрузка таблицы ящиков успешно завершена");
		LOG.info("Выгрузка содержимого БД успешно завершена");
	}

	private void setNullableParent(PreparedStatement ps, int parent) throws SQLException {
		if (parent == 0) {
			ps.setNull(2, java.sql.Types.INTEGER);
		} else {
			ps.setInt(2, parent);
		}
	}

	private String getNullableParent(ResultSet rs) throws SQLException {
		int parent = rs.getInt("CONTAINED_IN");
		if (rs.wasNull()) {
			return "NULL";
		} else {
			return String.valueOf(parent);
		}
	}

}
