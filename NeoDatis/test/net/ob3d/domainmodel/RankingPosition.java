package net.ob3d.domainmodel;

public class RankingPosition {
	int rank;
	PlayerInfo boxer;

	public int getRank() {
		return rank;
	}

	public RankingPosition(PlayerInfo playerInfo, int rank) {
		super();
		this.boxer = playerInfo;
		this.rank = rank;
		// TODO Auto-generated constructor stub
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public PlayerInfo getBoxer() {
		return boxer;
	}

	public void setBoxer(PlayerInfo boxer) {
		this.boxer = boxer;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getRank() + ":" + getBoxer();
	}
}
