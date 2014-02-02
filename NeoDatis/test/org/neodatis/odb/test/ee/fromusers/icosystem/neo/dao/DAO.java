package org.neodatis.odb.test.ee.fromusers.icosystem.neo.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dto.ComplexDTO;
import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dto.Entity;
import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dto.ParentDTO;
import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dto.SimpleDTO;
import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dto.SubClassWithList;


public class DAO{
	private AtomicLong dtoId = new AtomicLong(1);
	private final ODB odb;

	public DAO(ODB odb){
		this.odb = odb;
		CriteriaQuery query = new CriteriaQuery(SimpleDTO.class);
		query.setPolymorphic(true);
		query.orderByDesc("id");
		Objects<Entity> objects = odb.getObjects(query);
		
		if(!objects.isEmpty()){
			dtoId.set(objects.getFirst().getId() + 1);
		}
	}
	
	public ComplexDTO createComplex(){
		ComplexDTO dto = new ComplexDTO(dtoId.getAndIncrement());
		odb.store(dto);
		odb.commit();
		System.err.printf("Created :: %s\n",dto);
		return dto;
	}
	
	public ComplexDTO getComplex(long id){
		CriteriaQuery query = new CriteriaQuery(ComplexDTO.class, Where.equal("id", id));
		Objects<ComplexDTO> objects = odb.getObjects(query);
		return objects.getFirst();
	}
	
	public List<ComplexDTO> getComplexes(){
		CriteriaQuery query = new CriteriaQuery(ComplexDTO.class);
		Objects<ComplexDTO> objects = odb.getObjects(query);
		return new ArrayList<ComplexDTO>(objects);
	}

	public SimpleDTO createSimple(){
		SimpleDTO dto = new SimpleDTO(dtoId.getAndIncrement());
		dto.setCls(String.class);
		odb.store(dto);
		odb.commit();
		System.err.printf("Created :: %s\n",dto);
		return dto;
	}

	public SubClassWithList createSubClassWithList(Class<?>... classes){
		SubClassWithList dto = new SubClassWithList(dtoId.getAndIncrement());
		dto.setCls(int.class);
		if(classes != null){
			for(Class<?> clx : classes){
				dto.add(clx);
			}
		}
		odb.store(dto);
		odb.commit();
		System.err.printf("Created :: %s\n",dto);
		return dto;
	}

	public ParentDTO createParent(SimpleDTO simple,SubClassWithList subClassWithList){
		ParentDTO dto = new ParentDTO(dtoId.getAndIncrement());
		dto.setSimpleDTO(simple);
		dto.setWithSubClass(subClassWithList);
		odb.store(dto);
		odb.commit();
		System.err.printf("Created :: %s\n",dto);
		return dto;
	}

	public void delete(Class<?> clx,long id){
		CriteriaQuery query = new CriteriaQuery(clx, Where.equal("id", id));
		query.setPolymorphic(true);
		Objects<?> objects = odb.getObjects(query);
		odb.delete(objects.getFirst());
		odb.commit();
	}
	
	public void deleteCascade(Class<?> clx,long id){
		CriteriaQuery query = new CriteriaQuery(clx, Where.equal("id", id));
		query.setPolymorphic(true);
		Objects<?> objects = odb.getObjects(query);
		odb.deleteCascade(objects.getFirst());
		odb.commit();
	}
	
	public List<SimpleDTO> getSimples(){
		CriteriaQuery query = new CriteriaQuery(SimpleDTO.class);
		Objects<SimpleDTO> objects = odb.getObjects(query);
		return new ArrayList<SimpleDTO>(objects);
	}

	public SimpleDTO getSimple(long id){
		CriteriaQuery query = new CriteriaQuery(SimpleDTO.class, Where.equal("id", id));
		Objects<SimpleDTO> objects = odb.getObjects(query);
		return objects.getFirst();
	}

	public List<SimpleDTO> getPoly(){
		CriteriaQuery query = new CriteriaQuery(SimpleDTO.class);
		query.setPolymorphic(true);
		Objects<SimpleDTO> objects = odb.getObjects(query);
		return new ArrayList<SimpleDTO>(objects);
	}
	
	public List<SubClassWithList> getSubClassWithList(){
		CriteriaQuery query = new CriteriaQuery(SubClassWithList.class);
		Objects<SubClassWithList> objects = odb.getObjects(query);
		return new ArrayList<SubClassWithList>(objects);
	}
	
	public List<ParentDTO> getParents(){
		CriteriaQuery query = new CriteriaQuery(ParentDTO.class);
		Objects<ParentDTO> objects = odb.getObjects(query);
		return new ArrayList<ParentDTO>(objects);
	}
	
	public ParentDTO getParent(long id){
		CriteriaQuery query = new CriteriaQuery(ParentDTO.class,Where.equal("id", id));
		Objects<ParentDTO> objects = odb.getObjects(query);
		return objects.getFirst();
	}

	public long save(Entity dto){
		odb.store(dto);
		odb.commit();
		return dto.getId();
	}
	
	public long getNextId() {
		return dtoId.getAndIncrement();
	}

	public void shutdown(){
		odb.close();
	}
}
