package org.neodatis.odb.test.ee.fromusers.icosystem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;

class ExampleDAONeoDatis {
	private AtomicLong dtoId = new AtomicLong(1);
	private final ODB odb;

	public ExampleDAONeoDatis(){
		odb = ODBFactory.open("exampleodb");
		CriteriaQuery query = new CriteriaQuery(ExampleDTO.class);
		query.orderByDesc("id");
		Objects<ExampleDTO> objects = odb.getObjects(query);
		if(!objects.isEmpty()){
			dtoId.set(objects.getFirst().getId() + 1);
		}
	}

	public ExampleDTO create(){
		ExampleDTO dto = new ExampleDTO(dtoId.getAndIncrement());
		dto.generateData();
		odb.store(dto);
		odb.commit();
		return dto;
	}

	public ExampleDTO copy(long id){
		ExampleDTO dto = create();
		dto.initializeWith(get(id));
		odb.store(dto);
		odb.commit();
		return dto;
	}

	public void delete(long id){
		CriteriaQuery query = new CriteriaQuery(ExampleDTO.class, Where.equal("id", id));
		Objects<ExampleDTO> objects = odb.getObjects(query);
		odb.delete(objects.getFirst());
		odb.commit();
	}

	public ExampleDTO get(long id){
		CriteriaQuery query = new CriteriaQuery(ExampleDTO.class, Where.equal("id", id));
		Objects<ExampleDTO> objects = odb.getObjects(query);
		return objects.getFirst();
	}

	public List<ExampleDTO> get(){
		CriteriaQuery query = new CriteriaQuery(ExampleDTO.class);
		Objects<ExampleDTO> objects = odb.getObjects(query);
		return new ArrayList<ExampleDTO>(objects);
	}

	public long save(ExampleDTO dto){
		odb.store(dto);
		odb.commit();
		return dto.getId();
	}
	
	public void shutdown() {
		odb.close();
	}
}
