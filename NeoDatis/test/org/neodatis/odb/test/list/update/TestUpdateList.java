package org.neodatis.odb.test.list.update;

import java.util.ArrayList;
import java.util.List;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;

public class TestUpdateList extends ODBTest {

	public void test1() throws Exception {
		String file = "testeul.neodatis";
		deleteBase(file);
		DadosUsuario dadosUsuario = new DadosUsuario();
		dadosUsuario.setNome("Olivier");
		dadosUsuario.setLogin("olivier");
		dadosUsuario.setEmail("olivier@neodatis.org");
		dadosUsuario.setOid("oid");
		List l = new ArrayList();
		l.add(new Publicacao("p1", "Texto 1"));
		dadosUsuario.setPublicados(l);
		ODB odb = null;

		try {
			odb = open(file);
			odb.store(dadosUsuario);
		} finally {
			if (odb != null) {
				odb.close();
			}
		}

		try {
			odb = open(file);
			Objects l2 = odb.getObjects(DadosUsuario.class);
			println(l2);
			DadosUsuario du = (DadosUsuario) l2.getFirst();
			du.getPublicados().add(new Publicacao("p2", "Texto2"));
			odb.store(du);
		} finally {
			if (odb != null) {
				odb.close();
			}
		}

		try {
			odb = open(file);
			Objects l2 = odb.getObjects(DadosUsuario.class);
			println(l2);
			DadosUsuario du = (DadosUsuario) l2.getFirst();
			println(du.getPublicados());
			assertEquals(2, du.getPublicados().size());

		} finally {
			if (odb != null) {
				odb.close();
			}
		}
	}

	public void test2() throws Exception {
		String file = "testeul.neodatis";
		deleteBase(file);
		DadosUsuario dadosUsuario = new DadosUsuario();
		dadosUsuario.setNome("Olivier");
		dadosUsuario.setLogin("olivier");
		dadosUsuario.setEmail("olivier@neodatis.org");
		dadosUsuario.setOid("oid");
		List l = new ArrayList();
		l.add(new Publicacao("p0", "Texto0"));
		dadosUsuario.setPublicados(l);
		ODB odb = null;

		try {
			odb = open(file);
			odb.store(dadosUsuario);
		} finally {
			if (odb != null) {
				odb.close();
			}
		}

		int size = 100;
		for (int i = 0; i < size; i++) {
			try {
				odb = open(file);
				Objects l2 = odb.getObjects(DadosUsuario.class);
				// println(l2);
				DadosUsuario du = (DadosUsuario) l2.getFirst();
				du.getPublicados().add(new Publicacao("p" + (i + 1), "Texto" + (i + 1)));
				odb.store(du);
			} finally {
				if (odb != null) {
					odb.close();
				}
			}
		}

		try {
			odb = open(file);
			Objects l2 = odb.getObjects(DadosUsuario.class);
			println(l2);
			DadosUsuario du = (DadosUsuario) l2.getFirst();
			println(du.getPublicados());
			assertEquals(size + 1, du.getPublicados().size());
			for (int i = 0; i < size + 1; i++) {
				Publicacao p = (Publicacao) du.getPublicados().get(i);
				assertEquals("Texto" + (i), p.getTexto());
				assertEquals("p" + (i), p.getName());
			}

		} finally {
			if (odb != null) {
				odb.close();
			}
		}
	}
}
