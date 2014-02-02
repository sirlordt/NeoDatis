package net.ob3d.domainmodel;

import java.util.ArrayList;
import java.util.List;

public class Ranking {
	String name;
	List<RankingPosition> members;

	public Ranking() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Ranking(String name) {
		super();
		this.name = name;
		members = new ArrayList<RankingPosition>();
		// TODO Auto-generated constructor stub
	}

	public List<RankingPosition> getMembers() {
		return members;
	}

	public void setMembers(List<RankingPosition> members) {
		this.members = members;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String str = "";
		for (RankingPosition member : members) {
			str += member + "\n";
		}
		return str;
	}
}
