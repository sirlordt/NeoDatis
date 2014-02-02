package org.neodatis.odb.test.update.nullobject;

import java.util.Date;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;

public class TestNullObject extends ODBTest {
	public void test1() throws Exception {
		deleteBase("sict");
		ODB odb = open("sict");
		User user = popula(odb);
		AT at = createAT(user);
		odb.store(at);
		odb.store(createSensor(user, at, 1));
		odb.store(createSensor(user, at, 2));
		odb.store(createSensor(user, at, 3));
		odb.store(createSensor(user, at, 4));

		odb.close();

		odb = open("sict");
		Objects ats = odb.getObjects(AT.class);
		int nbAts = ats.size();
		at = (AT) ats.getFirst();
		AT newAT = null;
		SensorAT newSensor = null;
		IQuery query = new CriteriaQuery(SensorAT.class, Where.equal("at.name", at.getName()));
		query.orderByAsc("lane");
		Objects sensors = odb.getObjects(query);
		println("Duplicando AT " + at.getName());

		for (int i = 0; i < 10; i++) {
			newAT = duplicateAT(at, nbAts + i + 1);
			odb.store(newAT);
			sensors.reset();
			while (sensors.hasNext()) {
				newSensor = duplicateSensor((SensorAT) sensors.next(), newAT);
				odb.store(newSensor);
			}
			// println("AT " + newAT.getName()+" created");
		}
		odb.close();

	}

	public static AT createAT(User user) {

		Constructor constructor = new Constructor();
		constructor.setCreationDate(new Date());
		constructor.setName("neodatis");
		constructor.setDescription("Neodatis");

		AT newAt = new AT();
		newAt.setName("AT1");

		newAt.setConstructor(constructor);
		newAt.setCreationDate(new Date());
		newAt.setDeleted(false);
		newAt.setIpAddress("1.1.1.1");
		newAt.setPhysicalAddress("A01");
		newAt.setPort(4000);
		newAt.setStatus(true);
		newAt.setType("Type1");
		newAt.setUpdateDate(new Date());
		newAt.setUser(user);
		return newAt;
	}

	public static SensorAT createSensor(User user, AT at, int index) {
		SensorAT newSensorAT = new SensorAT();
		newSensorAT.setName(at.getName() + "-" + index);
		newSensorAT.setCreationDate(new Date());
		newSensorAT.setDeleted(false);
		newSensorAT.setKm(new Float(105.7));
		newSensorAT.setLane(index);
		newSensorAT.setState(1);
		newSensorAT.setStatus(true);
		newSensorAT.setUpdateDate(new Date());
		newSensorAT.setUser(user);
		newSensorAT.setWay(1);
		newSensorAT.setAt(at);
		return newSensorAT;
	}

	public static AT duplicateAT(AT at, int index) {
		AT newAt = new AT();
		newAt.setName(at.getName() + "-" + index);
		newAt.setConstructor(at.getConstructor());
		newAt.setCreationDate(new Date());
		newAt.setDeleted(false);
		newAt.setIpAddress(at.getIpAddress());
		newAt.setPhysicalAddress(at.getPhysicalAddress());
		newAt.setPort(at.getPort());
		newAt.setStatus(true);
		newAt.setType(at.getType());
		newAt.setUpdateDate(new Date());
		newAt.setUser(at.getUser());
		return newAt;
	}

	public static SensorAT duplicateSensor(SensorAT sensorAT, AT at) {
		SensorAT newSensorAT = new SensorAT();
		newSensorAT.setName(at.getName() + "-" + sensorAT.getName());
		newSensorAT.setCreationDate(new Date());
		newSensorAT.setDeleted(false);
		newSensorAT.setKm(sensorAT.getKm());
		newSensorAT.setLane(sensorAT.getLane());
		newSensorAT.setState(sensorAT.getState());
		newSensorAT.setStatus(true);
		newSensorAT.setUpdateDate(new Date());
		newSensorAT.setUser(sensorAT.getUser());
		newSensorAT.setWay(sensorAT.getWay());
		newSensorAT.setAt(at);
		return newSensorAT;
	}

	public static User popula(ODB odb) throws Exception {
		// cria perfil
		Profile profileAdmin = new Profile("administrador");
		odb.store(profileAdmin);
		Profile profileOper = new Profile("operador");
		odb.store(profileOper);
		// cria funcao
		createFunctionProfile(odb, profileAdmin, profileOper);
		// cria usuario
		User user = new User();
		user.setCreationDate(new Date());
		user.setDeleted(false);
		user.setLastLogin(new Date());
		user.setLogin("admin");
		user.setName("Administrador");
		user.setPassword("trocar");
		user.setProfileId(profileAdmin);
		user.setRejectedLogin(new Integer(0));
		user.setUpdateDate(new Date());
		user.setStatus(true);
		user.setSessionKey("123456");
		odb.store(user);
		return user;
	}

	public static void createFunctionProfile(ODB odb, Profile admin, Profile oper) throws Exception {
		Functions function = new Functions();
		function.setDescription("Inclusão de usuãrio");
		function.setName("incluiUsuario");
		function.setNameUrl("usuario.do/criar");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Ediãão de Usuãrio");
		function.setName("editaUsuario");
		function.setNameUrl("usuario.do/edit");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Exclusão de Usuãrio");
		function.setName("excluiUsuario");
		function.setNameUrl("usuario.do/excluir");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Consulta de Usuãrios");
		function.setName("listaUsuario");
		function.setNameUrl("usuario.do/visualizar");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller do usuãrio");
		function.setName("usuario");
		function.setNameUrl("consultaUsuario.do/editar");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller do usuãrio");
		function.setName("usuario");
		function.setNameUrl("consultaUsuario.do/excluir");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller do usuãrio");
		function.setName("usuario");
		function.setNameUrl("consultaUsuario.do/visualizar");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller da senha");
		function.setName("alteraSenha");
		function.setNameUrl("alteraSenha.do/editar");
		function.addProfile(admin);
		function.addProfile(oper);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller da senha");
		function.setName("alteraSenha");
		function.setNameUrl("alteraSenha.do");
		function.addProfile(admin);
		function.addProfile(oper);
		odb.store(function);

		function = new Functions();
		function.setDescription("Alteraãão de Senha de outros");
		function.setName("alteraSenhaOutros");
		function.setNameUrl("alteraSenhaOutros.do/editar");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Alteraãão de Senha de outros");
		function.setName("alteraSenhaOutros");
		function.setNameUrl("alteraSenhaOutros.do");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Pãgina Principal");
		function.setName("main");
		function.setNameUrl("main.jsp");
		function.addProfile(admin);
		function.addProfile(oper);
		odb.store(function);

		function = new Functions();
		function.setDescription("Pãgina Sobre");
		function.setName("main_sobre");
		function.setNameUrl("main_sobre.jsp");
		function.addProfile(admin);
		function.addProfile(oper);
		odb.store(function);

		function = new Functions();
		function.setDescription("Inclusão de PMV");
		function.setName("incluiPmv");
		function.setNameUrl("pmv.do/create");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Ediãão de PMV");
		function.setName("editaPmv");
		function.setNameUrl("pmv.do/edit");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Exclusão de Pmv");
		function.setName("excluiPmv");
		function.setNameUrl("pmv.do/delete");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Consulta de PMV");
		function.setName("listaPmv");
		function.setNameUrl("pmv.do/view");
		function.addProfile(admin);
		function.addProfile(oper);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller do PMV");
		function.setName("PMV");
		function.setNameUrl("searchPmv.do/edit");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller do PMV");
		function.setName("PMV");
		function.setNameUrl("searchPmv.do/delete");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller do PMV");
		function.setName("PMV");
		function.setNameUrl("searchPmv.do/view");
		function.addProfile(admin);
		function.addProfile(oper);
		odb.store(function);

		function = new Functions();
		function.setDescription("Inclusão de Fornecedor");
		function.setName("incluiFornecedor");
		function.setNameUrl("constructor.do/create");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Ediãão de Fornecedor");
		function.setName("editaFornecedor");
		function.setNameUrl("constructor.do/edit");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Exclusão de Fornecedor");
		function.setName("excluiFornecedor");
		function.setNameUrl("constructor.do/delete");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Consulta de Fornecedor");
		function.setName("listaFornecedor");
		function.setNameUrl("constructor.do/view");
		function.addProfile(admin);
		function.addProfile(oper);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller do Fornecedor");
		function.setName("Fornecedor");
		function.setNameUrl("searchConstructor.do/edit");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller do Fornecedor");
		function.setName("Fornecedor");
		function.setNameUrl("searchConstructor.do/delete");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller do Fornecedor");
		function.setName("Fornecedor");
		function.setNameUrl("searchConstructor.do/view");
		function.addProfile(admin);
		function.addProfile(oper);
		odb.store(function);

		function = new Functions();
		function.setDescription("Inclusão de AT");
		function.setName("incluiAT");
		function.setNameUrl("at.do/create");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Ediãão de AT");
		function.setName("editaAT");
		function.setNameUrl("at.do/edit");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Exclusão de AT");
		function.setName("excluiAT");
		function.setNameUrl("at.do/delete");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Consulta de AT");
		function.setName("listaAT");
		function.setNameUrl("at.do/view");
		function.addProfile(admin);
		function.addProfile(oper);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller do AT");
		function.setName("AT");
		function.setNameUrl("searchAt.do/edit");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller do AT");
		function.setName("AT");
		function.setNameUrl("searchAt.do/delete");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller do AT");
		function.setName("AT");
		function.setNameUrl("searchAt.do/view");
		function.addProfile(admin);
		function.addProfile(oper);
		odb.store(function);

		function = new Functions();
		function.setDescription("Inclusão de Sensor AT");
		function.setName("incluiSensorAT");
		function.setNameUrl("sensorAt.do/create");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Ediãão de Sensor AT");
		function.setName("editaSensorAT");
		function.setNameUrl("sensorAt.do/edit");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Exclusão de Sensor AT");
		function.setName("excluiSensorAT");
		function.setNameUrl("sensorAt.do/delete");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Consulta de Sensor AT");
		function.setName("listaSensorAT");
		function.setNameUrl("sensorAt.do/view");
		function.addProfile(admin);
		function.addProfile(oper);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller do Sensor AT");
		function.setName("SensorAT");
		function.setNameUrl("searchSensorAt.do/edit");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller do Sensor AT");
		function.setName("SensorAT");
		function.setNameUrl("searchSensorAt.do/delete");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller do Sensor AT");
		function.setName("SensorAT");
		function.setNameUrl("searchSensorAt.do/view");
		function.addProfile(admin);
		function.addProfile(oper);
		odb.store(function);

		function = new Functions();
		function.setDescription("Inclusão de Meteo");
		function.setName("incluiMeteo");
		function.setNameUrl("meteo.do/create");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Ediãão de Meteo");
		function.setName("editaMeteo");
		function.setNameUrl("meteo.do/edit");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Exclusão de Meteo");
		function.setName("excluiMeteo");
		function.setNameUrl("meteo.do/delete");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Consulta de Meteo");
		function.setName("listaMeteo");
		function.setNameUrl("meteo.do/view");
		function.addProfile(admin);
		function.addProfile(oper);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller do Meteo");
		function.setName("Meteo");
		function.setNameUrl("searchMeteo.do/edit");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller do Meteo");
		function.setName("Meteo");
		function.setNameUrl("searchMeteo.do/delete");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller do Meteo");
		function.setName("Meteo");
		function.setNameUrl("searchMeteo.do/view");
		function.addProfile(admin);
		function.addProfile(oper);
		odb.store(function);

		function = new Functions();
		function.setDescription("Inclusão de Sensor Meteo");
		function.setName("incluiSensorMeteo");
		function.setNameUrl("sensorMeteo.do/create");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Ediãão de Sensor Meteo");
		function.setName("editaSensorMeteo");
		function.setNameUrl("sensorMeteo.do/edit");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Exclusão de Sensor Meteo");
		function.setName("excluiSensorMeteo");
		function.setNameUrl("sensorMeteo.do/delete");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Consulta de Sensor Meteo");
		function.setName("listaSensorMeteo");
		function.setNameUrl("sensorMeteo.do/view");
		function.addProfile(admin);
		function.addProfile(oper);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller do Sensor Meteo");
		function.setName("SensorMeteo");
		function.setNameUrl("searchSensorMeteo.do/edit");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller do Sensor Meteo");
		function.setName("SensorMeteo");
		function.setNameUrl("searchSensorMeteo.do/delete");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller do Sensor Meteo");
		function.setName("SensorMeteo");
		function.setNameUrl("searchSensorMeteo.do/view");
		function.addProfile(admin);
		function.addProfile(oper);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller do PmvMessage");
		function.setName("sendPmvMessage");
		function.setNameUrl("sendMessagePmv.do/edit");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller do ActiveConf");
		function.setName("activeConf");
		function.setNameUrl("activeConf.do/edit");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller do Monitor");
		function.setName("monitor");
		function.setNameUrl("monitor.do/view");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller do Monitor");
		function.setName("monitor");
		function.setNameUrl("monitor.do/view");
		function.addProfile(oper);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller da Consulta Mensage");
		function.setName("searchMessagePMV");
		function.setNameUrl("searchMessagePmv.do/view");
		function.addProfile(admin);
		odb.store(function);

		function = new Functions();
		function.setDescription("Controller da Consulta Mensage");
		function.setName("searchMessagePMV");
		function.setNameUrl("searchMessagePmv.do/view");
		function.addProfile(oper);
		odb.store(function);
	}
}
